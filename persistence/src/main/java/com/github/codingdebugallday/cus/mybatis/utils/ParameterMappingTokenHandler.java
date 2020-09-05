package com.github.codingdebugallday.cus.mybatis.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * mybatis源码里扣的
 * 标记处理类，配合标记解析器来完成对占位符的解析处理操作
 * </p>
 *
 * @author isaac 2020/8/21 2:12
 * @since 1.0.0
 */
public class ParameterMappingTokenHandler implements TokenHandler {
    private List<ParameterMapping> parameterMappings = new ArrayList<>();

    /**
     * content是参数名称 #{id} #{username}里面的id username
     */
    @Override
    public String handleToken(String content) {
        parameterMappings.add(buildParameterMapping(content));
        return "?";
    }

    private ParameterMapping buildParameterMapping(String content) {
        return new ParameterMapping(content);
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public void setParameterMappings(List<ParameterMapping> parameterMappings) {
        this.parameterMappings = parameterMappings;
    }

}
