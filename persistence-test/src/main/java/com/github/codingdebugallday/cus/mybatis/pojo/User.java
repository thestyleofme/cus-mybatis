package com.github.codingdebugallday.cus.mybatis.pojo;

/**
 * <p>
 * description
 * </p>
 * 
 * @author isaac 2020/8/20 23:44
 * @since 1.0.0
 */
public class User {

    private Long id;
    private String username;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
