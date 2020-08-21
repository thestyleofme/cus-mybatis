package com.github.sqlsession;

import java.util.List;

import com.github.pojo.Configuration;
import com.github.pojo.MappedStatement;

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

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
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
}
