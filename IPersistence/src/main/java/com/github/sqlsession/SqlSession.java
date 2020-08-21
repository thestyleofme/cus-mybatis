package com.github.sqlsession;

import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author isaac 2020/8/21 1:16
 * @since 1.0.0
 */
public interface SqlSession {

    /**
     * 查询所有
     *
     * @param statementId statementId
     * @param params      可变参数
     * @param <E>         E
     * @return List<E>
     */
    <E> List<E> selectList(String statementId, Object... params) throws Exception;

    /**
     * 查询某一个
     *
     * @param statementId statementId
     * @param params      可变参数
     * @param <T>         T
     * @return T
     */
    <T> T selectOne(String statementId, Object... params) throws Exception;
}
