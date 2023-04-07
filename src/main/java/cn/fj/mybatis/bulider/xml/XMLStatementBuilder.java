package cn.fj.mybatis.bulider.xml;

import cn.fj.mybatis.bulider.BaseBuilder;
import cn.fj.mybatis.bulider.MapperBuilderAssistant;
import cn.fj.mybatis.mapping.SqlCommandType;
import cn.fj.mybatis.mapping.SqlSource;
import cn.fj.mybatis.mapping.StatementType;
import cn.fj.mybatis.scripting.XMLScriptBuilder;
import cn.fj.mybatis.session.Configuration;
import org.w3c.dom.Node;

import java.util.Locale;

public class XMLStatementBuilder extends BaseBuilder {

    private final MapperBuilderAssistant assistant;

    private final Node context;

    public XMLStatementBuilder(Configuration configuration,MapperBuilderAssistant assistant,Node context) {
        super(configuration);
        this.assistant = assistant;
        this.context = context;
    }

    public void parseStatementNode(){
        String id = context.getAttributes().getNamedItem("id").getNodeValue();

        String nodeName = context.getNodeName();
        SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
        XMLScriptBuilder xmlScriptBuilder = new XMLScriptBuilder(configuration, context, null);
        SqlSource sqlSource = xmlScriptBuilder.parseScriptNode();
        StatementType statementType = StatementType
                .valueOf(StatementType.PREPARED.toString());
        assistant.addMappedStatement(id,sqlSource,statementType,sqlCommandType);
    }
}
