package com.github.cus.mybatis.sqlsession;

import java.sql.SQLException;
import java.util.List;

import com.github.cus.mybatis.pojo.Configuration;
import com.github.cus.mybatis.pojo.MappedStatement;

/**
 * <p>
 * description
 * </p>
 *
 * @author isaac 2020/8/21 1:38
 * @since 1.0.0
 */
public interface Executor {

    /**
     * jdbc查询
     *
     * @param configuration   Configuration
     * @param mappedStatement MappedStatement
     * @param params          Object...
     * @param <E>             E
     * @return List<E>
     */
    <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception;

    /**
     * jdbc 删除/更新/插入
     *
     * @param ms     MappedStatement
     * @param params Object...
     * @return int
     * @throws SQLException SQLException
     */
    int update(MappedStatement ms, Object... params) throws SQLException;
}
