package cn.fj.mybatis.bulider.xml;

import cn.fj.mybatis.bulider.BaseBuilder;
import cn.fj.mybatis.datasource.DataSourceFactory;
import cn.fj.mybatis.mapping.Environment;
import cn.fj.mybatis.parsing.XPathParser;
import cn.fj.mybatis.session.Configuration;
import cn.fj.mybatis.transaction.TransactionFactory;
import org.apache.ibatis.builder.BuilderException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import java.io.Reader;
import java.util.Properties;

public class XMLConfigBuilder extends BaseBuilder {

    private final XPathParser xPathParser;

    private String environment;

    public XMLConfigBuilder(Reader reader) {
        super(new Configuration());
        this.xPathParser = new XPathParser(reader);
    }

    public Configuration parse(){
        Node node = xPathParser.evalNode("/configuration");
        parserConfiguration(node);
        return configuration;
    }

    private void parserConfiguration(Node root){
        try {
            mapperElement(xPathParser.evalNode("mappers",root));
            environmentsElement(xPathParser.evalNode("environments",root));
        } catch (Exception e) {
            throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
    }

    private void environmentsElement(Node context) throws Exception{
        if(context != null){
            String nodeName1 = context.getNodeName();
            if(environment == null){
                environment = context.getAttributes().getNamedItem("default").getNodeValue();
            }
            NodeList childNodes = context.getChildNodes();
            for(int i=0;i < childNodes.getLength();i++){
                Node item = childNodes.item(i);
                if(item.getNodeType() == Node.ELEMENT_NODE) {
                    String id = item.getAttributes().getNamedItem("id").getNodeValue();
                    if (isSpecifiedEnvironment(id)) {
                        TransactionFactory transactionFactory = transactionManagerElement(xPathParser.evalNode("transactionManager", item));
                        DataSourceFactory dataSourceFactory = dataSourceElement(xPathParser.evalNode("dataSource", item));
                        DataSource dataSource = dataSourceFactory.getDataSource();
                        Environment.Builder environmentBuilder = new Environment.Builder(id).transactionFactory(transactionFactory).dataSource(dataSource);
                        configuration.setEnvironment(environmentBuilder.build());
                        break;
                    }
                }
            }
        }
    }

    private TransactionFactory transactionManagerElement(Node parent) throws Exception{
        if(parent != null){
            String type = parent.getAttributes().getNamedItem("type").getNodeValue();
            return (TransactionFactory) resolveClass(type).getDeclaredConstructor().newInstance();
        }
        throw new BuilderException("Environment declaration requires a TransactionFactory.");
    }

    private DataSourceFactory dataSourceElement(Node parent) throws Exception{
        if(parent != null){
            String type = parent.getAttributes().getNamedItem("type").getNodeValue();
            //获取Properties
            Properties properties = getPropertiesForChild(parent);
            //创建DataSourceFactory对象
            DataSourceFactory dataSourceFactory = (DataSourceFactory) resolveClass(type).getDeclaredConstructor().newInstance();
            //把Properties装配进DataSourceFactory
            dataSourceFactory.setProperties(properties);
            return dataSourceFactory;
        }
        throw new BuilderException("Environment declaration requires a DataSourceFactory.");
    }

    private Properties getPropertiesForChild(Node parent) {
        NodeList childNodes = parent.getChildNodes();
        Properties properties = new Properties();
        for(int i=0;i < childNodes.getLength();i++){
            Node item = childNodes.item(i);
            if(item.getNodeType() == Node.ELEMENT_NODE) {
                String name = item.getAttributes().getNamedItem("name").getNodeValue();
                String value = item.getAttributes().getNamedItem("value").getNodeValue();
                if (name != null && value != null) {
                    properties.setProperty(name, value);
                }
            }
        }
        return properties;
    }

    private void mapperElement(Node parent) {
        if(parent != null){
            NodeList childNodes = parent.getChildNodes();
            for(int i=0;i < childNodes.getLength();i++){
                Node node = childNodes.item(i);
                if("package".equals(node.getNodeName())){
                    String mapperPackage = node.getAttributes().getNamedItem("name").getNodeValue();
                    configuration.addMappers(mapperPackage);
                }
            }
        }
    }

    private boolean isSpecifiedEnvironment(String id){
        if(environment == null){
            throw new BuilderException("No environment specified.");
        }
        if(id == null){
            throw new BuilderException("Environment requires an id attribute.");
        }
        return id.equals(environment);
    }
}
