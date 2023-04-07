package cn.fj.mybatis.bulider;

import cn.fj.mybatis.mapping.BoundSql;
import cn.fj.mybatis.mapping.SqlSource;
import cn.fj.mybatis.session.Configuration;

public class StaticSqlSource implements SqlSource {

    private final String sql;

    private final Configuration configuration;

    public StaticSqlSource(String sql, Configuration configuration) {
        this.sql = sql;
        this.configuration = configuration;
    }

    @Override
    public BoundSql getBoundSql() {
        return new BoundSql(sql);
    }
}
