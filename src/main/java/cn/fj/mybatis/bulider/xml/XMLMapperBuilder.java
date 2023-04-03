package cn.fj.mybatis.bulider.xml;

import cn.fj.mybatis.bulider.BaseBuilder;
import cn.fj.mybatis.bulider.MapperBuilderAssistant;
import cn.fj.mybatis.parsing.XPathParser;
import cn.fj.mybatis.session.Configuration;
import org.apache.ibatis.builder.BuilderException;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class XMLMapperBuilder extends BaseBuilder {

    private final XPathParser parser;

    private final MapperBuilderAssistant builderAssistant;

    private String resource;

    public XMLMapperBuilder(Configuration configuration, InputStream inputStream,String resource,String namespace) {
        super(configuration);
        this.parser = new XPathParser(new InputStreamReader(inputStream));
        this.resource = resource;
        this.builderAssistant = new MapperBuilderAssistant(configuration,resource);
        this.builderAssistant.setCurrentNameSpace(namespace);
    }

    public void parse(){
        if(!configuration.isResourceLoaded(resource)){
            configurationElement(parser.evalNode("/mapper"));
            configuration.addLoadResource(resource);
        }
    }

    private void configurationElement(Node context) {
        String namespace = context.getAttributes().getNamedItem("namespace").getNodeValue();
        if(namespace == null || namespace.isEmpty()){
            throw new BuilderException("Mapper's namespace cannot be empty");
        }
        builderAssistant.setCurrentNameSpace(namespace);
        buildStatementFromContext(parser.evalNodes("update|select|insert|delete",context));
    }

    private void buildStatementFromContext(List<Node> evalNodes) {

    }
}
