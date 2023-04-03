package cn.fj.mybatis.mapping;

public class BoundSql {

    private final String sql;


    public BoundSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}
