package com.github.cus.mybatis.utils;

/**
 * <p>
 * description
 * </p>
 *
 * @author isaac 2020/8/21 2:12
 * @since 1.0.0
 */
public class ParameterMapping {

    /**
     * 解析出来的参数名称
     */
    private String content;

    public ParameterMapping(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ParameterMapping{" +
                "content='" + content + '\'' +
                '}';
    }
}
