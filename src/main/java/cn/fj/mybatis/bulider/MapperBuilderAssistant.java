package cn.fj.mybatis.bulider;

import cn.fj.mybatis.mapping.MappedStatement;
import cn.fj.mybatis.mapping.SqlCommandType;
import cn.fj.mybatis.mapping.SqlSource;
import cn.fj.mybatis.mapping.StatementType;
import cn.fj.mybatis.session.Configuration;
import org.apache.ibatis.builder.BuilderException;

public class MapperBuilderAssistant extends BaseBuilder{

    private String currentNameSpace;

    private final String resource;

    public MapperBuilderAssistant(Configuration configuration,String resource) {
        super(configuration);
        this.resource = resource;
    }

    public String getCurrentNameSpace() {
        return currentNameSpace;
    }

    public void setCurrentNameSpace(String currentNameSpace) {
        this.currentNameSpace = currentNameSpace;
    }

    public MappedStatement addMappedStatement(String id, SqlSource sqlSource, StatementType statementType, SqlCommandType sqlCommandType){
        id = applyCurrentNamespace(id,false);
        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, id, sqlSource, sqlCommandType).statementType(statementType);
        MappedStatement mappedStatement = builder.build();
        configuration.addMappedStatement(mappedStatement);
        return mappedStatement;
    }

    public String applyCurrentNamespace(String base, boolean isReference) {
        if (base == null) {
            return null;
        }
        if (isReference) {
            // is it qualified with any namespace yet?
            if (base.contains(".")) {
                return base;
            }
        } else {
            // is it qualified with this namespace yet?
            if (base.startsWith(currentNameSpace + ".")) {
                return base;
            }
            if (base.contains(".")) {
                throw new BuilderException("Dots are not allowed in element names, please remove it from " + base);
            }
        }
        return currentNameSpace + "." + base;
    }
}
