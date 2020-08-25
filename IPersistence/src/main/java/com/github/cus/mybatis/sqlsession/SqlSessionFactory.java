package com.github.cus.mybatis.sqlsession;

/**
 * <p>
 * description
 * </p>
 *
 * @author isaac 2020/8/20 23:44
 * @since 1.0.0
 */
public interface SqlSessionFactory {

    /**
     * 创建会话对象
     *
     * @return SqlSession
     */
    SqlSession openSession();
}
