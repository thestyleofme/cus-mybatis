package com.github.sqlsession;

import java.io.InputStream;

import com.github.config.XMLConfigBuilder;
import com.github.pojo.Configuration;
import org.dom4j.DocumentException;

/**
 * <p>
 * description
 * </p>
 * 
 * @author isaac 2020/8/20 23:42
 * @since 1.0.0
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(InputStream in) throws DocumentException {
        // 使用dom4j解析配置文件，将解析出来的内容封装到Configuration中
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parseConfig(in);
        // 创建SqlSessionFactory 工厂类：生产SqlSession会话对象
        return new DefaultSqlSessionFactory(configuration);
    }
}
