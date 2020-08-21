package com.github.dao;

import java.util.List;

import com.github.pojo.User;

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
     */
    List<User> findAll() throws Exception;

    /**
     * 条件查询
     *
     * @param user User
     * @return User
     */
    User findByCondition(User user) throws Exception;
}
