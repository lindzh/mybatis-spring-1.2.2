/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.annotation;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

/**
 * A component provider that scans the classpath from a base package. It then
 * applies exclude and include filters to the resulting classes to find candidates.
 *
 * <p>This implementation is based on Spring's
 * {@link org.springframework.core.type.classreading.MetadataReader MetadataReader}
 * facility, backed by an ASM {@link org.springframework.asm.ClassReader ClassReader}.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Ramnivas Laddad
 * @author Chris Beams
 * @since 2.5
 * @see org.springframework.core.type.classreading.MetadataReaderFactory
 * @see org.springframework.core.type.AnnotationMetadata
 * @see ScannedGenericBeanDefinition
 */
public class ClassPathScanningCandidateComponentProvider implements EnvironmentCapable, ResourceLoaderAware {

	static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

	protected final Log logger = LogFactory.getLog(getClass());

	private Environment environment;

	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);

	private String resourcePattern = DEFAULT_RESOURCE_PATTERN;

	private final List<TypeFilter> includeFilters = new LinkedList<TypeFilter>();

	private final List<TypeFilter> excludeFilters = new LinkedList<TypeFilter>();


	/**
	 * Create a ClassPathScanningCandidateComponentProvider.
	 * @param useDefaultFilters whether to register the default filters for the
	 * {@link Component @Component}, {@link Repository @Repository},
	 * {@link Service @Service}, and {@link Controller @Controller}
	 * stereotype annotations
	 * @see #registerDefaultFilters()
	 */
	public ClassPathScanningCandidateComponentProvider(boolean useDefaultFilters) {
		this(useDefaultFilters, new StandardEnvironment());
	}

	public ClassPathScanningCandidateComponentProvider(boolean useDefaultFilters, Environment environment) {
		if (useDefaultFilters) {
			registerDefaultFilters();
		}
		this.environment = environment;
	}


	/**
	 * Set the ResourceLoader to use for resource locations.
	 * This will typically be a ResourcePatternResolver implementation.
	 * <p>Default is PathMatchingResourcePatternResolver, also capable of
	 * resource pattern resolving through the ResourcePatternResolver interface.
	 * @see org.springframework.core.io.support.ResourcePatternResolver
	 * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver
	 */
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
		this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
	}

	/**
	 * Set the Environment to use when resolving placeholders and evaluating
	 * {@link Profile @Profile}-annotated component classes.
	 * <p>The default is a {@link StandardEnvironment}
	 * @param environment the Environment to use
	 */
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public Environment getEnvironment() {
		return this.environment;
	}

	/**
	 * Return the ResourceLoader that this component provider uses.
	 */
	public final ResourceLoader getResourceLoader() {
		return this.resourcePatternResolver;
	}

	/**
	 * Set the resource pattern to use when scanning the classpath.
	 * This value will be appended to each base package name.
	 * @see #findCandidateComponents(String)
	 * @see #DEFAULT_RESOURCE_PATTERN
	 */
	public void setResourcePattern(String resourcePattern) {
		Assert.notNull(resourcePattern, "'resourcePattern' must not be null");
		this.resourcePattern = resourcePattern;
	}

	/**
	 * Add an include type filter to the <i>end</i> of the inclusion list.
	 */
	public void addIncludeFilter(TypeFilter includeFilter) {
		this.includeFilters.add(includeFilter);
	}

	/**
	 * Add an exclude type filter to the <i>front</i> of the exclusion list.
	 */
	public void addExcludeFilter(TypeFilter excludeFilter) {
		this.excludeFilters.add(0, excludeFilter);
	}

	/**
	 * Reset the configured type filters.
	 * @param useDefaultFilters whether to re-register the default filters for
	 * the {@link Component @Component}, {@link Repository @Repository},
	 * {@link Service @Service}, and {@link Controller @Controller}
	 * stereotype annotations
	 * @see #registerDefaultFilters()
	 */
	public void resetFilters(boolean useDefaultFilters) {
		this.includeFilters.clear();
		this.excludeFilters.clear();
		if (useDefaultFilters) {
			registerDefaultFilters();
		}
	}

	/**
	 * Register the default filter for {@link Component @Component}.
	 * <p>This will implicitly register all annotations that have the
	 * {@link Component @Component} meta-annotation including the
	 * {@link Repository @Repository}, {@link Service @Service}, and
	 * {@link Controller @Controller} stereotype annotations.
	 * <p>Also supports Java EE 6's {@link javax.annotation.ManagedBean} and
	 * JSR-330's {@link javax.inject.Named} annotations, if available.
	 *
	 */
	@SuppressWarnings("unchecked")
	protected void registerDefaultFilters() {
		this.includeFilters.add(new AnnotationTypeFilter(Component.class));
		ClassLoader cl = ClassPathScanningCandidateComponentProvider.class.getClassLoader();
		try {
			this.includeFilters.add(new AnnotationTypeFilter(
					((Class<? extends Annotation>) cl.loadClass("javax.annotation.ManagedBean")), false));
			logger.info("JSR-250 'javax.annotation.ManagedBean' found and supported for component scanning");
		}
		catch (ClassNotFoundException ex) {
			// JSR-250 1.1 API (as included in Java EE 6) not available - simply skip.
		}
		try {
			this.includeFilters.add(new AnnotationTypeFilter(
					((Class<? extends Annotation>) cl.loadClass("javax.inject.Named")), false));
			logger.info("JSR-330 'javax.inject.Named' annotation found and supported for component scanning");
		}
		catch (ClassNotFoundException ex) {
			// JSR-330 API not available - simply skip.
		}
	}
	
	/**
	 * com.linda.koala.biz.dao
	 * @return
	 */
	private List<String> getDaoClasses(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("com.linda.koala.biz.dao.AdminAccountDao");
		list.add("com.linda.koala.biz.dao.ApiAuthDao");
		list.add("com.linda.koala.biz.dao.ApiEditLogDao");
		list.add("com.linda.koala.biz.dao.ApiExpireLimitDao");
		list.add("com.linda.koala.biz.dao.ApiInfoDao");
		list.add("com.linda.koala.biz.dao.ApiInfoDraftDao");
		list.add("com.linda.koala.biz.dao.ApiIPExpireLimitDao");
		list.add("com.linda.koala.biz.dao.ApiLogVersionDao");
		list.add("com.linda.koala.biz.dao.ApiObjectDao");
		list.add("com.linda.koala.biz.dao.ApiObjectFieldDao");
		list.add("com.linda.koala.biz.dao.ApiObjectFieldDraftDao");
		list.add("com.linda.koala.biz.dao.ApiObjectParamDao");
		list.add("com.linda.koala.biz.dao.ApiParamDao");
		list.add("com.linda.koala.biz.dao.ApiParamDraftDao");
		list.add("com.linda.koala.biz.dao.ApiProdDao");
		list.add("com.linda.koala.biz.dao.AppDao");
		list.add("com.linda.koala.biz.dao.AppMd5Dao");
		list.add("com.linda.koala.biz.dao.ClientSourceDao");
		list.add("com.linda.koala.biz.dao.ExpParamDao");
		list.add("com.linda.koala.biz.dao.ExpressionDao");
		list.add("com.linda.koala.biz.dao.KopIPDao");
		list.add("com.linda.koala.biz.dao.KVStoreDao");
		list.add("com.linda.koala.biz.dao.ModuleDao");
		list.add("com.linda.koala.biz.dao.OperationDao");
		list.add("com.linda.koala.biz.dao.OwnersDao");
		list.add("com.linda.koala.biz.dao.ProdDao");
		list.add("com.linda.koala.biz.dao.RoleDao");
		list.add("com.linda.koala.biz.dao.RoleOperationDao");
		list.add("com.linda.koala.biz.dao.SimplePropertyDao");
		return list;
	}
	
	/**
	 * com.linda.koala.biz
	 * @return
	 */
	private List<String> getBizClasses(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("com.linda.koala.biz.remote.EventBuilder$1");
		list.add("com.linda.koala.biz.remote.EventBuilder");
		list.add("com.linda.koala.biz.remote.GatewayExportService");
		list.add("com.linda.koala.biz.remote.GatewayRemoteServiceImpl");
		list.add("com.linda.koala.biz.remote.MessageService");
		list.add("com.linda.koala.biz.spring.CacheTemplate");
		list.add("com.linda.koala.biz.spring.aop.HiFrequencyAdvice");
		list.add("com.linda.koala.biz.spring.aop.HiFrequency");
		list.add("com.linda.koala.biz.spring.mq.RocketMQListener");
		list.add("com.linda.koala.biz.spring.mq.RocketMQListenerProcessor");
		list.add("com.linda.koala.biz.pojo.AdminAccount");
		list.add("com.linda.koala.biz.pojo.AdminOperation");
		list.add("com.linda.koala.biz.pojo.AdminRole");
		list.add("com.linda.koala.biz.pojo.ApiAuth");
		list.add("com.linda.koala.biz.pojo.ApiEditLog");
		list.add("com.linda.koala.biz.pojo.ApiExpireLimit");
		list.add("com.linda.koala.biz.pojo.ApiInfo");
		list.add("com.linda.koala.biz.pojo.ApiInfoDraft");
		list.add("com.linda.koala.biz.pojo.ApiIPExpireLimit");
		list.add("com.linda.koala.biz.pojo.ApiLogVersion");
		list.add("com.linda.koala.biz.pojo.ApiObject");
		list.add("com.linda.koala.biz.pojo.ApiObjectField");
		list.add("com.linda.koala.biz.pojo.ApiObjectFieldDraft");
		list.add("com.linda.koala.biz.pojo.ApiObjectParam");
		list.add("com.linda.koala.biz.pojo.ApiParam");
		list.add("com.linda.koala.biz.pojo.ApiParamDraft");
		list.add("com.linda.koala.biz.pojo.ApiProd");
		list.add("com.linda.koala.biz.pojo.App");
		list.add("com.linda.koala.biz.pojo.AppMd5");
		list.add("com.linda.koala.biz.pojo.ClientSource");
		list.add("com.linda.koala.biz.pojo.ExpParam");
		list.add("com.linda.koala.biz.pojo.Expression");
		list.add("com.linda.koala.biz.pojo.KopIP");
		list.add("com.linda.koala.biz.pojo.KVStore$1");
		list.add("com.linda.koala.biz.pojo.KVStore");
		list.add("com.linda.koala.biz.pojo.Module");
		list.add("com.linda.koala.biz.pojo.Owners");
		list.add("com.linda.koala.biz.pojo.Prod");
		list.add("com.linda.koala.biz.pojo.SimpleProperty");
		list.add("com.linda.koala.biz.pojo.SmsInfo");
		list.add("com.linda.koala.biz.pojo.VarificationCode");
		list.add("com.linda.koala.biz.service.AdminAccountService");
		list.add("com.linda.koala.biz.service.ApiAuthService");
		list.add("com.linda.koala.biz.service.ApiCompareService");
		list.add("com.linda.koala.biz.service.ApiEditLogService");
		list.add("com.linda.koala.biz.service.ApiInfoDraftService");
		list.add("com.linda.koala.biz.service.ApiInfoService");
		list.add("com.linda.koala.biz.service.ApiLimitService");
		list.add("com.linda.koala.biz.service.ApiLogVersionService");
		list.add("com.linda.koala.biz.service.ApiObjectService");
		list.add("com.linda.koala.biz.service.ApiParamDraftService");
		list.add("com.linda.koala.biz.service.ApiParamService");
		list.add("com.linda.koala.biz.service.ApiPublishService");
		list.add("com.linda.koala.biz.service.ApiSyncService");
		list.add("com.linda.koala.biz.service.AppMd5Service");
		list.add("com.linda.koala.biz.service.AppService");
		list.add("com.linda.koala.biz.service.ClientSourceService");
		list.add("com.linda.koala.biz.service.ConfigService");
		list.add("com.linda.koala.biz.service.ExpParamService");
		list.add("com.linda.koala.biz.service.ExpressionService");
		list.add("com.linda.koala.biz.service.FindPwdService");
		list.add("com.linda.koala.biz.service.IPService");
		list.add("com.linda.koala.biz.service.KopLogService");
		list.add("com.linda.koala.biz.service.KopLogService$KopOperation");
		list.add("com.linda.koala.biz.service.ModuleService");
		list.add("com.linda.koala.biz.service.MysqlKVService");
		list.add("com.linda.koala.biz.service.OperationService");
		list.add("com.linda.koala.biz.service.OwnersService");
		list.add("com.linda.koala.biz.service.ProdService");
		list.add("com.linda.koala.biz.service.PropertyService");
		list.add("com.linda.koala.biz.service.RoleService");
		list.add("com.linda.koala.biz.service.SecurityService");
		return list;
	}
	
	private Set<BeanDefinition> findKopBeanComponents(String basePackage){
		boolean debugEnabled = logger.isDebugEnabled();
		Set<BeanDefinition> candidates = new LinkedHashSet<BeanDefinition>();
		List<String> classes = null;
		if(basePackage.contains("com.linda.koala.biz.dao")){
			classes = this.getDaoClasses();
		}else if(basePackage.contains("com.linda.koala.biz")){
			classes = this.getBizClasses();
		}
		if(classes!=null){
			for(String clazz:classes){
				ClassLoader loader = this.getClass().getClassLoader();
				try {
					Class<?> class1 = loader.loadClass(clazz);
					if(class1!=null){
						
						AnnotatedGenericBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(class1);
						if (isCandidateComponent(beanDefinition)) {
							if (debugEnabled) {
								logger.debug("Identified candidate component class: " + clazz);
							}
							candidates.add(beanDefinition);
						}
						else {
							if (debugEnabled) {
								logger.debug("Ignored because not a concrete top-level class: " +clazz);
							}
						}
					}
				} catch (ClassNotFoundException e) {
					throw new BeanDefinitionStoreException("Failed to read candidate component class: " + clazz, e);
				}
			}
		}
		return candidates;
	}


	/**
	 * Scan the class path for candidate components.
	 * @param basePackage the package to check for annotated classes
	 * @return a corresponding Set of autodetected bean definitions
	 */
	public Set<BeanDefinition> findCandidateComponents(String basePackage) {
		Set<BeanDefinition> components = findKopBeanComponents(basePackage);
		if(!CollectionUtils.isEmpty(components)){
			return components;
		}
		
		Set<BeanDefinition> candidates = new LinkedHashSet<BeanDefinition>();
		try {
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
					resolveBasePackage(basePackage) + "/" + this.resourcePattern;
			Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
			boolean traceEnabled = logger.isTraceEnabled();
			boolean debugEnabled = logger.isDebugEnabled();
			for (Resource resource : resources) {
				if (traceEnabled) {
					logger.trace("Scanning " + resource);
				}
				if (resource.isReadable()) {
					try {
						MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
						if (isCandidateComponent(metadataReader)) {
							ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
							sbd.setResource(resource);
							sbd.setSource(resource);
							if (isCandidateComponent(sbd)) {
								if (debugEnabled) {
									logger.debug("Identified candidate component class: " + resource);
								}
								candidates.add(sbd);
							}
							else {
								if (debugEnabled) {
									logger.debug("Ignored because not a concrete top-level class: " + resource);
								}
							}
						}
						else {
							if (traceEnabled) {
								logger.trace("Ignored because not matching any filter: " + resource);
							}
						}
					}
					catch (Throwable ex) {
						throw new BeanDefinitionStoreException(
								"Failed to read candidate component class: " + resource, ex);
					}
				}
				else {
					if (traceEnabled) {
						logger.trace("Ignored because not readable: " + resource);
					}
				}
			}
		}
		catch (IOException ex) {
			throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
		}
		return candidates;
	}


	/**
	 * Resolve the specified base package into a pattern specification for
	 * the package search path.
	 * <p>The default implementation resolves placeholders against system properties,
	 * and converts a "."-based package path to a "/"-based resource path.
	 * @param basePackage the base package as specified by the user
	 * @return the pattern specification to be used for package searching
	 */
	protected String resolveBasePackage(String basePackage) {
		return ClassUtils.convertClassNameToResourcePath(environment.resolveRequiredPlaceholders(basePackage));
	}

	/**
	 * Determine whether the given class does not match any exclude filter
	 * and does match at least one include filter.
	 * @param metadataReader the ASM ClassReader for the class
	 * @return whether the class qualifies as a candidate component
	 
	protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
		for (TypeFilter tf : this.excludeFilters) {
			if (tf.match(metadataReader, this.metadataReaderFactory)) {
				return false;
			}
		}
		for (TypeFilter tf : this.includeFilters) {
			if (tf.match(metadataReader, this.metadataReaderFactory)) {
				AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
				if (!ProfileHelper.isProfileAnnotationPresent(metadata)) {
					return true;
				}
				return this.environment.acceptsProfiles(ProfileHelper.getCandidateProfiles(metadata));
			}
		}
		return false;
	}
	*/
	
	protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
		for (TypeFilter tf : this.excludeFilters) {
			if (tf.match(metadataReader, this.metadataReaderFactory)) {
				return false;
			}
		}
		for (TypeFilter tf : this.includeFilters) {
			if (tf.match(metadataReader, this.metadataReaderFactory)) {
				AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
				if (!metadata.isAnnotated(Profile.class.getName())) {
					return true;
				}
				AnnotationAttributes profile = MetadataUtils.attributesFor(metadata, Profile.class);
				return this.environment.acceptsProfiles(profile.getStringArray("value"));
			}
		}
		return false;
	}

	/**
	 * Determine whether the given bean definition qualifies as candidate.
	 * <p>The default implementation checks whether the class is concrete
	 * (i.e. not abstract and not an interface). Can be overridden in subclasses.
	 * @param beanDefinition the bean definition to check
	 * @return whether the bean definition qualifies as a candidate component
	 */
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return (beanDefinition.getMetadata().isConcrete() && beanDefinition.getMetadata().isIndependent());
	}
	
	public static void main(String[] args) {
		System.out.println("hello world");
	}
}
