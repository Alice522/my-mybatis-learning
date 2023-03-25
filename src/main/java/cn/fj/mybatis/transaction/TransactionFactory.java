package cn.fj.mybatis.transaction;

import cn.fj.mybatis.session.TransactionIsolationLevel;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

public interface TransactionFactory {

    default void setProperties(Properties prop){};

    Transaction newTransaction(Connection conn);

    Transaction newTransaction(DataSource dataSource, boolean autoCommit, TransactionIsolationLevel level);
}
