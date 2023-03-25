package cn.fj.mybatis.session.defaults;

import cn.fj.mybatis.mapping.Environment;
import cn.fj.mybatis.session.Configuration;
import cn.fj.mybatis.session.SqlSession;
import cn.fj.mybatis.session.SqlSessionFactory;
import cn.fj.mybatis.session.TransactionIsolationLevel;
import cn.fj.mybatis.transaction.Transaction;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return openSessionFromDataSource(null,false);
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        return openSessionFromDataSource(null,autoCommit);
    }

    @Override
    public SqlSession openSession(TransactionIsolationLevel level) {
        return openSessionFromDataSource(level,false);
    }

    private SqlSession openSessionFromDataSource(TransactionIsolationLevel level,boolean autocommit){
        Environment environment = configuration.getEnvironment();
        Transaction transaction = environment.getTransactionFactory().newTransaction(environment.getDataSource(), autocommit, level);
        return new DefaultSqlSession(configuration,transaction);
    }
}
