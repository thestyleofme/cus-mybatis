package com.github.config;

import java.io.InputStream;
import java.util.List;

import com.github.pojo.Configuration;
import com.github.pojo.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <p>
 * description
 * </p>
 *
 * @author isaac 2020/8/21 0:50
 * @since 1.0.0
 */
public class XMLMapperBuilder {

    private final Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parse(InputStream in) throws DocumentException {
        Document document = new SAXReader().read(in);
        // <<mapper>
        Element mapperRootElement = document.getRootElement();
        String namespace = mapperRootElement.attributeValue("namespace");
        List<Element> selectElements = mapperRootElement.elements("select");
        MappedStatement mappedStatement;
        for (Element selectElement : selectElements) {
            String id = selectElement.attributeValue("id");
            String parameterType = selectElement.attributeValue("parameterType");
            String resultType = selectElement.attributeValue("resultType");
            String sql = selectElement.getTextTrim();
            mappedStatement = new MappedStatement(id, parameterType, resultType, sql);
            configuration.getMappedStatementMap().put(namespace + "." + id, mappedStatement);
        }
    }
}
