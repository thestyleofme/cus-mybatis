package com.github.pojo;

/**
 * <p>
 * mapper.xml映射类
 * </p>
 *
 * @author isaac 2020/8/20 23:29
 * @since 1.0.0
 */
public class MappedStatement {

    /**
     * id标识
     */
    private String id;
    /**
     * 返回值类型
     */
    private String resultType;
    /**
     * 参数类型
     */
    private  String parameterType;
    /**
     * sql语句
     */
    private String sql;

    public MappedStatement(String id, String parameterType,String resultType , String sql) {
        this.id = id;
        this.parameterType = parameterType;
        this.resultType = resultType;
        this.sql = sql;
    }

    public MappedStatement() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
