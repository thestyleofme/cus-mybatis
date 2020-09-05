package com.github.codingdebugallday.cus.mybatis.config;

import java.io.InputStream;
import java.util.List;

import com.github.codingdebugallday.cus.mybatis.constants.SqlCommandType;
import com.github.codingdebugallday.cus.mybatis.pojo.Configuration;
import com.github.codingdebugallday.cus.mybatis.pojo.MappedStatement;
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
        // 查找具体执行的sql
        List<Element> selectElements = mapperRootElement.elements("select");
        List<Element> updateElements = mapperRootElement.elements("update");
        List<Element> deleteElements = mapperRootElement.elements("delete");
        List<Element> insertElements = mapperRootElement.elements("insert");
        MappedStatement mappedStatement;
        for (Element element : selectElements) {
            mappedStatement = genMappedStatementByElement(namespace,element);
            mappedStatement.setSqlCommandType(SqlCommandType.SELECT);
        }
        for (Element element : updateElements) {
            mappedStatement = genMappedStatementByElement(namespace,element);
            mappedStatement.setSqlCommandType(SqlCommandType.UPDATE);
        }
        for (Element element : deleteElements) {
            mappedStatement = genMappedStatementByElement(namespace,element);
            mappedStatement.setSqlCommandType(SqlCommandType.DELETE);
        }
        for (Element element : insertElements) {
            mappedStatement = genMappedStatementByElement(namespace,element);
            mappedStatement.setSqlCommandType(SqlCommandType.INSERT);
        }
    }

    private MappedStatement genMappedStatementByElement(String namespace, Element element) {
        String id = element.attributeValue("id");
        String parameterType = element.attributeValue("parameterType");
        String resultType = element.attributeValue("resultType");
        String sql = element.getTextTrim();
        MappedStatement mappedStatement = new MappedStatement(id, parameterType, resultType, sql);
        configuration.getMappedStatementMap().put(namespace + "." + id, mappedStatement);
        return mappedStatement;
    }
}
