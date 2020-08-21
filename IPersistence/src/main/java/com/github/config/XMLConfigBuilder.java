package com.github.config;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;

import com.github.io.Resources;
import com.github.pojo.Configuration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <p>
 * description
 * </p>
 *
 * @author isaac 2020/8/20 23:45
 * @since 1.0.0
 */
public class XMLConfigBuilder {

    private final Configuration configuration;

    public XMLConfigBuilder() {
        this.configuration = new Configuration();
    }

    /**
     * 使用dom4j将配置文件解析为Configuration
     *
     * @param in InputStream
     * @return Configuration
     */
    public Configuration parseConfig(InputStream in) throws DocumentException {
        Document document = new SAXReader().read(in);
        // <configuration>
        Element rootElement = document.getRootElement();
        // 创建连接池
        configuration.setDataSource(genHikariDatasource(rootElement));
        // mapper.xml解析
        genMappedStatement(rootElement);
        return configuration;
    }

    private void genMappedStatement(Element rootElement) throws DocumentException {
        List<Element> mapperList = rootElement.elements("mapper");
        for (Element element : mapperList) {
            String mapperPath = element.attributeValue("resource");
            InputStream in = Resources.getResourceAsStream(mapperPath);
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration);
            xmlMapperBuilder.parse(in);
        }
    }

    private DataSource genHikariDatasource(Element rootElement) {
        Element dataSourceElement = rootElement.elements("dataSource").get(0);
        Iterator<Element> dataSourceElementIterator = dataSourceElement.elementIterator();
        Properties properties = new Properties();
        while (dataSourceElementIterator.hasNext()) {
            Element element = dataSourceElementIterator.next();
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.setProperty(name, value);
        }
        HikariConfig hikariConfig = new HikariConfig(properties);
        return new HikariDataSource(hikariConfig);
    }
}
