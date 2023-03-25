package cn.fj.mybatis.transaction.jdbc;

import cn.fj.mybatis.session.TransactionIsolationLevel;
import cn.fj.mybatis.transaction.Transaction;
import cn.fj.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;

public class JdbcTransactionFactory implements TransactionFactory {
    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, boolean autoCommit, TransactionIsolationLevel level) {
        return new JdbcTransaction(dataSource, autoCommit, level);
    }
}
