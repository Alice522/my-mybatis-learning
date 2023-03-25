package cn.fj.mybatis.session.defaults;

import cn.fj.mybatis.session.Configuration;
import cn.fj.mybatis.session.SqlSession;
import cn.fj.mybatis.session.SqlSessionFactory;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
