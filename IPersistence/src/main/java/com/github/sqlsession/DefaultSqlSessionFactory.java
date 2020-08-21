package com.github.sqlsession;

import com.github.pojo.Configuration;

/**
 * <p>
 * description
 * </p>
 *
 * @author isaac 2020/8/21 1:11
 * @since 1.0.0
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
