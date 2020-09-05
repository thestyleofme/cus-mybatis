package com.github.codingdebugallday.cus.mybatis.dao;

import java.util.List;

import com.github.codingdebugallday.cus.mybatis.pojo.User;

/**
 * <p>
 * description
 * </p>
 *
 * @author isaac 2020/8/21 14:25
 * @since 1.0.0
 */
public interface IUserDao {

    /**
     * 查询所有
     *
     * @return List<User>
     * @throws Exception Exception
     */
    List<User> findAll() throws Exception;

    /**
     * 条件查询
     *
     * @param user User
     * @return User
     * @throws Exception Exception
     */
    User findByCondition(User user) throws Exception;

    /**
     * 新增
     *
     * @param user User
     * @return int
     * @throws Exception Exception
     */
    int insert(User user) throws Exception;

    /**
     * 更新
     *
     * @param user User
     * @return int
     * @throws Exception Exception
     */
    int update(User user) throws Exception;

    /**
     * 更新
     *
     * @param id Long
     * @return int
     * @throws Exception Exception
     */
    int delete(Long id) throws Exception;


}
