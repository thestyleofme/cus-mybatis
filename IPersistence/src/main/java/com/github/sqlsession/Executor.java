package com.github.sqlsession;

import java.util.List;

import com.github.pojo.Configuration;
import com.github.pojo.MappedStatement;

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
}
