package cn.fj.mybatis.bulider;

import cn.fj.mybatis.mapping.SqlSource;
import cn.fj.mybatis.session.Configuration;

public class SqlSourceBuilder extends BaseBuilder{

    public SqlSourceBuilder(Configuration configuration) {
        super(configuration);
    }

    public SqlSource parse(String originalSql,Class<?> parameterType){
        //TODO 解析#｛｝
        return new StaticSqlSource(originalSql,configuration);
    }
}
