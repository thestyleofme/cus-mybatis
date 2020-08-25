package com.github.cus.mybatis.pojo;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

/**
 * <p>
 * sqlMapConfig.xml映射类
 * </p>
 *
 * @author isaac 2020/8/20 23:33
 * @since 1.0.0
 */
public class Configuration {

    private DataSource dataSource;
    /**
     * key: statementId
     * value: MappedStatement
     */
    private Map<String,MappedStatement> mappedStatementMap = new HashMap<>();

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, MappedStatement> getMappedStatementMap() {
        return mappedStatementMap;
    }

    public void setMappedStatementMap(Map<String, MappedStatement> mappedStatementMap) {
        this.mappedStatementMap = mappedStatementMap;
    }
}
