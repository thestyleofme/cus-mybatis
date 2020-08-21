package com.github.sqlsession;

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

import com.github.config.BoundSql;
import com.github.pojo.Configuration;
import com.github.pojo.MappedStatement;
import com.github.utils.CloseUtil;
import com.github.utils.GenericTokenParser;
import com.github.utils.ParameterMapping;
import com.github.utils.ParameterMappingTokenHandler;

/**
 * <p>
 * description
 * </p>
 *
 * @author isaac 2020/8/21 1:41
 * @since 1.0.0
 */
public class SimpleExecutor implements Executor {

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
            String sql = mappedStatement.getSql();
            // 转换sql语句 select * from user where id = ? and username = ? 还需对#{}里的值进行储存
            BoundSql boundSql = getBoundSql(sql);
            // 获取预处理对象
            preparedStatement = connection.prepareStatement(boundSql.getSqlText());
            // 设置参数
            String parameterType = mappedStatement.getParameterType();
            Class<?> parameterTypeClazz = genClass(parameterType);
            List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
            for (int i = 0, size = parameterMappingList.size(); i < size; i++) {
                ParameterMapping parameterMapping = parameterMappingList.get(i);
                String content = parameterMapping.getContent();
                // 反射
                Field field = parameterTypeClazz.getDeclaredField(content);
                // 设置暴力访问 防止是私有属性
                field.setAccessible(true);
                Object obj = field.get(params[0]);
                preparedStatement.setObject(i + 1, obj);
            }
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
