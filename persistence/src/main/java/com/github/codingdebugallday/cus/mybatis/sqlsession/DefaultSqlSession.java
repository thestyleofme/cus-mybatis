package com.github.codingdebugallday.cus.mybatis.sqlsession;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;

import com.github.codingdebugallday.cus.mybatis.pojo.Configuration;
import com.github.codingdebugallday.cus.mybatis.pojo.MappedStatement;

/**
 * <p>
 * description
 * </p>
 *
 * @author isaac 2020/8/21 1:17
 * @since 1.0.0
 */
public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;
    private final SimpleExecutor simpleExecutor;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
        simpleExecutor = new SimpleExecutor();
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws Exception {
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        return simpleExecutor.query(configuration, mappedStatement, params);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {
        List<Object> objects = selectList(statementId, params);
        if (objects != null && objects.size() == 1) {
            return (T) objects.get(0);
        }
        throw new IllegalStateException("查询结果为空或返回结果过多");
    }

    @Override
    public int update(String statementId, Object... params) throws Exception {
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        return simpleExecutor.update(configuration, mappedStatement, params);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        // 使用jdk动态代理为dao接口生成代理对象并返回
        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(),
                new Class[]{mapperClass},
                (proxy, method, args) -> {
                    // 底层还是去执行jdbc代码 根据不用情况 来调用selectList或selectOne
                    // 准备参数1. statementId sql语句唯一标识 namespace.id=接口全路径名.方法名
                    String className = method.getDeclaringClass().getName();
                    String methodName = method.getName();
                    String statementId = className + "." + methodName;
                    // 获取被调用方法的返回值类型
                    Type type = method.getGenericReturnType();
                    MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
                    // 根据不同的sqlCommandType，调用不同的方法
                    switch (mappedStatement.getSqlCommandType()) {
                        case SELECT:
                            return doSelect(statementId, type, args);
                        case INSERT:
                        case DELETE:
                        case UPDATE:
                            return doUpdate(statementId, args);
                        default:
                            // 不会到达这里
                            throw new IllegalStateException("invalid sqlCommandType");
                    }
                });
        return (T) proxyInstance;
    }

    private Object doUpdate(String statementId, Object[] args) throws Exception {
        return update(statementId, args);
    }

    private Object doSelect(String statementId, Type type, Object[] args) throws Exception {
        // 简单判断下 若返回值 泛型类型参数化 即返回List<E> 否则就是实体对象
        if (type instanceof ParameterizedType) {
            return selectList(statementId, args);
        }
        return selectOne(statementId, args);
    }
}
