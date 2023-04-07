package cn.fj.mybatis.scripting;

import cn.fj.mybatis.bulider.BaseBuilder;
import cn.fj.mybatis.mapping.SqlSource;
import cn.fj.mybatis.scripting.defaults.RawSqlSource;
import cn.fj.mybatis.session.Configuration;
import org.w3c.dom.Node;

public class XMLScriptBuilder extends BaseBuilder {

    private final Node context;

    private final Class<?> parameterType;

    public XMLScriptBuilder(Configuration configuration,Node context,Class<?> parameterType) {
        super(configuration);
        this.context = context;
        this.parameterType = parameterType;
    }

    public SqlSource parseScriptNode(){
        //TODO 解析mapper的子标签
        String sql = context.getNodeValue();
        return new RawSqlSource(configuration,sql,parameterType);
    }
}
