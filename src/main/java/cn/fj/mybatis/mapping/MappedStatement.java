package cn.fj.mybatis.mapping;

import cn.fj.mybatis.session.Configuration;

public final class MappedStatement {

    private String resource;

    private Configuration configuration;

    private String id;

    private StatementType statementType;

    private SqlSource sqlSource;

    private SqlCommandType sqlCommandType;

    public static class Builder{
        private final MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration,String id,SqlSource sqlSource,SqlCommandType sqlCommandType){
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlSource = sqlSource;
            mappedStatement.statementType = StatementType.PREPARED;
            mappedStatement.sqlCommandType = sqlCommandType;
        }

        public Builder resource(String resource){
            mappedStatement.resource = resource;
            return this;
        }

        public MappedStatement build(){
            return mappedStatement;
        }
    }

    public String getResource() {
        return resource;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getId() {
        return id;
    }

    public StatementType getStatementType() {
        return statementType;
    }

    public SqlSource getSqlSource() {
        return sqlSource;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }
}
