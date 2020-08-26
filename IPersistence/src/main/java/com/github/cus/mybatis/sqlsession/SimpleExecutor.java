package com.github.cus.mybatis.sqlsession;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.cus.mybatis.config.BoundSql;
import com.github.cus.mybatis.pojo.Configuration;
import com.github.cus.mybatis.pojo.MappedStatement;
import com.github.cus.mybatis.utils.CloseUtil;
import com.github.cus.mybatis.utils.GenericTokenParser;
import com.github.cus.mybatis.utils.ParameterMapping;
import com.github.cus.mybatis.utils.ParameterMappingTokenHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * description
 * </p>
 *
 * @author isaac 2020/8/21 1:41
 * @since 1.0.0
 */
public class SimpleExecutor implements Executor {

    private final Logger logger = LoggerFactory.getLogger(SimpleExecutor.class);

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        ArrayList<Object> list;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            // 获取Connection
            connection = configuration.getDataSource().getConnection();
            preparedStatement = getPreparedStatement(connection, mappedStatement, params);
            // 执行
            resultSet = preparedStatement.executeQuery();
            // 封装返回结果集
            String resultType = mappedStatement.getResultType();
            Class<?> resultTypeClazz = genClass(resultType);
            list = new ArrayList<>();
            while (resultSet.next()) {
                Object result = Thread.currentThread()
                        .getContextClassLoader()
                        .loadClass(resultType)
                        .getDeclaredConstructor()
                        .newInstance();
                ResultSetMetaData metaData = resultSet.getMetaData();
                for (int i = 1, size = metaData.getColumnCount(); i <= size; i++) {
                    // 字段名
                    String columnName = metaData.getColumnName(i);
                    // 字段值
                    Object value = resultSet.getObject(columnName);
                    // 使用反射或内省，根据数据库表和实体的对应关系 完成封装
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClazz);
                    Method writeMethod = propertyDescriptor.getWriteMethod();
                    writeMethod.invoke(result, value);
                }
                list.add(result);
            }
        } finally {
            CloseUtil.close(resultSet, preparedStatement, connection);
        }
        return (List<E>) list;
    }

    @Override
    public int update(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            // 获取Connection
            connection = configuration.getDataSource().getConnection();
            preparedStatement = getPreparedStatement(connection, mappedStatement, params);
            return preparedStatement.executeUpdate();
        } finally {
            CloseUtil.close(preparedStatement, connection);
        }
    }

    private PreparedStatement getPreparedStatement(Connection connection, MappedStatement mappedStatement, Object... params) throws Exception {
        // 获取预处理对象
        String sql = mappedStatement.getSql();
        // 转换sql语句 select * from user where id = ? and username = ? 还需对#{}里的值进行储存
        BoundSql boundSql = getBoundSql(sql);
        String sqlText = boundSql.getSqlText();
        logger.debug("sql: {}", sqlText);
        logger.debug("params: {}", boundSql.getParameterMappingList().stream()
                .map(ParameterMapping::getContent)
                .collect(Collectors.toList()));
        logger.debug("value: {}", params[0]);
        PreparedStatement preparedStatement = connection.prepareStatement(sqlText);
        // 设置参数
        String parameterType = mappedStatement.getParameterType();
        Class<?> parameterTypeClazz = genClass(parameterType);
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        for (int i = 0, size = parameterMappingList.size(); i < size; i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();
            // 需判断参数是否为基础类型 不是使用反射 是的话直接传值即可
            if (isPrimitive(params[0])) {
                preparedStatement.setObject(i + 1, params[0]);
            } else {
                Field field = parameterTypeClazz.getDeclaredField(content);
                // 设置暴力访问 防止是私有属性
                field.setAccessible(true);
                Object obj = field.get(params[0]);
                preparedStatement.setObject(i + 1, obj);
            }
        }
        return preparedStatement;
    }

    private boolean isPrimitive(Object obj) {
        try {
            return ((Class<?>) obj.getClass().getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    private Class<?> genClass(String className) throws ClassNotFoundException {
        if (Objects.nonNull(className)) {
            return Class.forName(className);
        }
        return null;
    }

    /**
     * 1. 将#{}使用?代替
     * 2. 将#{}里的值进行储存
     *
     * @param sql sql
     * @return BoundSql
     */
    private BoundSql getBoundSql(String sql) {
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser =
                new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        // 解析过后的sql，即占位符为?
        String parseSql = genericTokenParser.parse(sql);
        // #{}里的值
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();
        return new BoundSql(parseSql, parameterMappings);
    }
}
