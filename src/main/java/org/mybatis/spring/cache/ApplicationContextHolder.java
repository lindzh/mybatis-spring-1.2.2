package org.mybatis.spring.cache;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextHolder implements ApplicationContextAware{
	
	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext cxt)
			throws BeansException {
		context = cxt;
	}

	public static boolean containsBean(String arg0) {
		return context.containsBean(arg0);
	}

	public static boolean containsBeanDefinition(String arg0) {
		return context.containsBeanDefinition(arg0);
	}

	public static <A extends Annotation> A findAnnotationOnBean(String arg0,
			Class<A> arg1) {
		return context.findAnnotationOnBean(arg0, arg1);
	}

	public static String[] getAliases(String arg0) {
		return context.getAliases(arg0);
	}

	public static <T> T getBean(Class<T> arg0) throws BeansException {
		return context.getBean(arg0);
	}

	public static <T> T getBean(String arg0, Class<T> arg1) throws BeansException {
		return context.getBean(arg0, arg1);
	}

	public static Object getBean(String arg0, Object... arg1) throws BeansException {
		return context.getBean(arg0, arg1);
	}

	public static Object getBean(String arg0) throws BeansException {
		return context.getBean(arg0);
	}

	public static int getBeanDefinitionCount() {
		return context.getBeanDefinitionCount();
	}

	public static String[] getBeanDefinitionNames() {
		return context.getBeanDefinitionNames();
	}

	public static String[] getBeanNamesForType(Class<?> arg0, boolean arg1,
			boolean arg2) {
		return context.getBeanNamesForType(arg0, arg1, arg2);
	}

	public static String[] getBeanNamesForType(Class<?> arg0) {
		return context.getBeanNamesForType(arg0);
	}

	public static <T> Map<String, T> getBeansOfType(Class<T> arg0, boolean arg1,
			boolean arg2) throws BeansException {
		return context.getBeansOfType(arg0, arg1, arg2);
	}

	public static <T> Map<String, T> getBeansOfType(Class<T> arg0)
			throws BeansException {
		return context.getBeansOfType(arg0);
	}

	public static Map<String, Object> getBeansWithAnnotation(
			Class<? extends Annotation> arg0) throws BeansException {
		return context.getBeansWithAnnotation(arg0);
	}

	public static String getId() {
		return context.getId();
	}

	public static String getMessage(MessageSourceResolvable arg0, Locale arg1)
			throws NoSuchMessageException {
		return context.getMessage(arg0, arg1);
	}

	public static String getMessage(String arg0, Object[] arg1, Locale arg2)
			throws NoSuchMessageException {
		return context.getMessage(arg0, arg1, arg2);
	}

	public static String getMessage(String arg0, Object[] arg1, String arg2,
			Locale arg3) {
		return context.getMessage(arg0, arg1, arg2, arg3);
	}

	public static Resource getResource(String arg0) {
		return context.getResource(arg0);
	}

	public static Resource[] getResources(String arg0) throws IOException {
		return context.getResources(arg0);
	}

	public static long getStartupDate() {
		return context.getStartupDate();
	}

	public static boolean isPrototype(String arg0)
			throws NoSuchBeanDefinitionException {
		return context.isPrototype(arg0);
	}

	public static boolean isSingleton(String arg0)
			throws NoSuchBeanDefinitionException {
		return context.isSingleton(arg0);
	}

	public static boolean isTypeMatch(String arg0, Class<?> arg1)
			throws NoSuchBeanDefinitionException {
		return context.isTypeMatch(arg0, arg1);
	}
	
	
	

}
