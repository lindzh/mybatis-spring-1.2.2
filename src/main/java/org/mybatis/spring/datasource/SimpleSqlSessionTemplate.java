package org.mybatis.spring.datasource;

import static java.lang.reflect.Proxy.newProxyInstance;
import static org.apache.ibatis.reflection.ExceptionUtil.unwrapThrowable;
import static org.mybatis.spring.SqlSessionUtils.closeSqlSession;
import static org.mybatis.spring.SqlSessionUtils.getSqlSession;
import static org.mybatis.spring.SqlSessionUtils.isSqlSessionTransactional;
import static org.springframework.util.Assert.notNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.support.PersistenceExceptionTranslator;

/**
 * 多数据源sqlsessiontemplate配置
 * 
 * @author lindezhi 2016年7月19日 下午5:59:26
 */
public class SimpleSqlSessionTemplate extends SqlSessionTemplate {

	/**
	 * 默认SqlSessionFactory
	 */
	private final SqlSessionFactory defaultSqlSessionFactory;

	/**
	 * 执行器类型，方便创建sqlsession
	 */
	private final ExecutorType executorType;

	/**
	 * sqlSession代理对象
	 */
	private final SqlSession sqlSessionProxy;

	/**
	 * Mysql异常转换器
	 */
	private final PersistenceExceptionTranslator exceptionTranslator;

	/**
	 * 可以选择的SqlSessionfactory列表
	 */
	private Map<String, SqlSessionFactory> targetSqlSessionFactory = new HashMap<String, SqlSessionFactory>();

	/**
	 * Constructs a Spring managed SqlSession with the {@code SqlSessionFactory} provided as an argument.
	 *
	 * @param sqlSessionFactory
	 */
	public SimpleSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		this(sqlSessionFactory, sqlSessionFactory.getConfiguration().getDefaultExecutorType());
	}
	
	public SimpleSqlSessionTemplate(SqlSessionFactory sqlSessionFactory,Map<String, SqlSessionFactory> targetSqlSessionFactory) {
		this(sqlSessionFactory, sqlSessionFactory.getConfiguration().getDefaultExecutorType());
		this.targetSqlSessionFactory = targetSqlSessionFactory;
	}

	/**
	 * Constructs a Spring managed SqlSession with the {@code SqlSessionFactory} provided as an argument and the given
	 * {@code ExecutorType} {@code ExecutorType} cannot be changed once the {@code SimpleSqlSessionTemplate} is
	 * constructed.
	 *
	 * @param sqlSessionFactory
	 * @param executorType
	 */
	public SimpleSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
		this(sqlSessionFactory, executorType, new MyBatisExceptionTranslator(sqlSessionFactory.getConfiguration().getEnvironment()
				.getDataSource(), true));
	}

	/**
	 * Constructs a Spring managed {@code SqlSession} with the given {@code SqlSessionFactory} and {@code ExecutorType}.
	 * A custom {@code SQLExceptionTranslator} can be provided as an argument so any {@code PersistenceException} thrown
	 * by MyBatis can be custom translated to a {@code RuntimeException} The {@code SQLExceptionTranslator} can also be
	 * null and thus no exception translation will be done and MyBatis exceptions will be thrown
	 *
	 * @param sqlSessionFactory
	 * @param executorType
	 * @param exceptionTranslator
	 */
	public SimpleSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType,
			PersistenceExceptionTranslator exceptionTranslator) {
		super(sqlSessionFactory);
		notNull(sqlSessionFactory, "Property 'sqlSessionFactory' is required");
		notNull(executorType, "Property 'executorType' is required");

		this.defaultSqlSessionFactory = sqlSessionFactory;
		this.executorType = executorType;
		this.exceptionTranslator = exceptionTranslator;
		this.sqlSessionProxy = (SqlSession) newProxyInstance(SqlSessionFactory.class.getClassLoader(), new Class[] { SqlSession.class },
				new SqlSessionInterceptor());
	}

	/**
	 * sqlSessionFactory获取
	 */
	public SqlSessionFactory getSqlSessionFactory() {
		String contextType = DataSourceContextHolder.getContextType();
		if (contextType == null) {
			return defaultSqlSessionFactory;
		}
		SqlSessionFactory sessionFactory = this.targetSqlSessionFactory.get(contextType);
		if (sessionFactory != null) {
			return sessionFactory;
		} else {
			return defaultSqlSessionFactory;
		}
	}

	public ExecutorType getExecutorType() {
		return this.executorType;
	}

	public PersistenceExceptionTranslator getPersistenceExceptionTranslator() {
		return this.exceptionTranslator;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T selectOne(String statement) {
		return this.sqlSessionProxy.<T> selectOne(statement);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T selectOne(String statement, Object parameter) {
		return this.sqlSessionProxy.<T> selectOne(statement, parameter);
	}

	/**
	 * {@inheritDoc}
	 */
	public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
		return this.sqlSessionProxy.<K, V> selectMap(statement, mapKey);
	}

	/**
	 * {@inheritDoc}
	 */
	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
		return this.sqlSessionProxy.<K, V> selectMap(statement, parameter, mapKey);
	}

	/**
	 * {@inheritDoc}
	 */
	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
		return this.sqlSessionProxy.<K, V> selectMap(statement, parameter, mapKey, rowBounds);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> List<E> selectList(String statement) {
		return this.sqlSessionProxy.<E> selectList(statement);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> List<E> selectList(String statement, Object parameter) {
		return this.sqlSessionProxy.<E> selectList(statement, parameter);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
		return this.sqlSessionProxy.<E> selectList(statement, parameter, rowBounds);
	}

	/**
	 * {@inheritDoc}
	 */
	public void select(String statement, ResultHandler handler) {
		this.sqlSessionProxy.select(statement, handler);
	}

	/**
	 * {@inheritDoc}
	 */
	public void select(String statement, Object parameter, ResultHandler handler) {
		this.sqlSessionProxy.select(statement, parameter, handler);
	}

	/**
	 * {@inheritDoc}
	 */
	public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
		this.sqlSessionProxy.select(statement, parameter, rowBounds, handler);
	}

	/**
	 * {@inheritDoc}
	 */
	public int insert(String statement) {
		return this.sqlSessionProxy.insert(statement);
	}

	/**
	 * {@inheritDoc}
	 */
	public int insert(String statement, Object parameter) {
		return this.sqlSessionProxy.insert(statement, parameter);
	}

	/**
	 * {@inheritDoc}
	 */
	public int update(String statement) {
		return this.sqlSessionProxy.update(statement);
	}

	/**
	 * {@inheritDoc}
	 */
	public int update(String statement, Object parameter) {
		return this.sqlSessionProxy.update(statement, parameter);
	}

	/**
	 * {@inheritDoc}
	 */
	public int delete(String statement) {
		return this.sqlSessionProxy.delete(statement);
	}

	/**
	 * {@inheritDoc}
	 */
	public int delete(String statement, Object parameter) {
		return this.sqlSessionProxy.delete(statement, parameter);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T getMapper(Class<T> type) {
		return getConfiguration().getMapper(type, this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() {
		throw new UnsupportedOperationException("Manual commit is not allowed over a Spring managed SqlSession");
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit(boolean force) {
		throw new UnsupportedOperationException("Manual commit is not allowed over a Spring managed SqlSession");
	}

	/**
	 * {@inheritDoc}
	 */
	public void rollback() {
		throw new UnsupportedOperationException("Manual rollback is not allowed over a Spring managed SqlSession");
	}

	/**
	 * {@inheritDoc}
	 */
	public void rollback(boolean force) {
		throw new UnsupportedOperationException("Manual rollback is not allowed over a Spring managed SqlSession");
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		throw new UnsupportedOperationException("Manual close is not allowed over a Spring managed SqlSession");
	}

	/**
	 * {@inheritDoc}
	 */
	public void clearCache() {
		this.sqlSessionProxy.clearCache();
	}

	/**
	 * {@inheritDoc}
	 *
	 */
	public Configuration getConfiguration() {
		return this.defaultSqlSessionFactory.getConfiguration();
	}

	/**
	 * {@inheritDoc}
	 */
	public Connection getConnection() {
		return this.sqlSessionProxy.getConnection();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.0.2
	 *
	 */
	public List<BatchResult> flushStatements() {
		return this.sqlSessionProxy.flushStatements();
	}

	/**
	 * Proxy needed to route MyBatis method calls to the proper SqlSession got from Spring's Transaction Manager It also
	 * unwraps exceptions thrown by {@code Method#invoke(Object, Object...)} to pass a {@code PersistenceException} to
	 * the {@code PersistenceExceptionTranslator}.
	 */
	private class SqlSessionInterceptor implements InvocationHandler {
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			SqlSession sqlSession = getSqlSession(SimpleSqlSessionTemplate.this.getSqlSessionFactory(),
					SimpleSqlSessionTemplate.this.executorType, SimpleSqlSessionTemplate.this.exceptionTranslator);
			try {
				Object result = method.invoke(sqlSession, args);
				if (!isSqlSessionTransactional(sqlSession, SimpleSqlSessionTemplate.this.getSqlSessionFactory())) {
					// force commit even on non-dirty sessions because some databases require
					// a commit/rollback before calling close()
					sqlSession.commit(true);
				}
				return result;
			} catch (Throwable t) {
				Throwable unwrapped = unwrapThrowable(t);
				if (SimpleSqlSessionTemplate.this.exceptionTranslator != null && unwrapped instanceof PersistenceException) {
					// release the connection to avoid a deadlock if the translator is no loaded. See issue #22
					closeSqlSession(sqlSession, SimpleSqlSessionTemplate.this.getSqlSessionFactory());
					sqlSession = null;
					Throwable translated = SimpleSqlSessionTemplate.this.exceptionTranslator
							.translateExceptionIfPossible((PersistenceException) unwrapped);
					if (translated != null) {
						unwrapped = translated;
					}
				}
				throw unwrapped;
			} finally {
				if (sqlSession != null) {
					closeSqlSession(sqlSession, SimpleSqlSessionTemplate.this.getSqlSessionFactory());
				}
			}
		}
	}

	public void setTargetSqlSessionFactory(Map<String, SqlSessionFactory> targetSqlSessionFactory) {
		this.targetSqlSessionFactory = targetSqlSessionFactory;
	}
}
