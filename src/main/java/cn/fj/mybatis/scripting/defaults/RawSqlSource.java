package cn.fj.mybatis.scripting.defaults;

import cn.fj.mybatis.bulider.SqlSourceBuilder;
import cn.fj.mybatis.mapping.BoundSql;
import cn.fj.mybatis.mapping.SqlSource;
import cn.fj.mybatis.session.Configuration;

public class RawSqlSource implements SqlSource {

    private final SqlSource sqlSource;

    public RawSqlSource(Configuration configuration,String sql,Class<?> parameterType) {
        SqlSourceBuilder parser = new SqlSourceBuilder(configuration);
        parameterType = parameterType == null ? Object.class : parameterType;
        this.sqlSource =  parser.parse(sql,parameterType);
    }

    @Override
    public BoundSql getBoundSql() {
        return sqlSource.getBoundSql();
    }
}
