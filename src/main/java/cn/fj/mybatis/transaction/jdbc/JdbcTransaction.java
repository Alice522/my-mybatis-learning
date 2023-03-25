package cn.fj.mybatis.transaction.jdbc;

import cn.fj.mybatis.session.TransactionIsolationLevel;
import cn.fj.mybatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class JdbcTransaction implements Transaction {

    private Connection connection;

    private DataSource dataSource;

    private boolean autoCommit;

    private TransactionIsolationLevel level;

    public JdbcTransaction(Connection connection) {
        this.connection = connection;
    }

    public JdbcTransaction(DataSource dataSource, boolean autoCommit, TransactionIsolationLevel level) {
        this.dataSource = dataSource;
        this.autoCommit = autoCommit;
        this.level = level;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if(connection == null){
            openConnection();
        }
        return connection;
    }

    private void openConnection() throws SQLException {
        connection = dataSource.getConnection();
        if(level != null){
            connection.setTransactionIsolation(level.getLevel());
        }
        setDesiredAutoCommit(autoCommit);
    }

    private void setDesiredAutoCommit(boolean autoCommit) {
        try {
            if(connection.getAutoCommit() != autoCommit){
                connection.setAutoCommit(autoCommit);
            }
        } catch (SQLException e) {
            throw new TransactionException(
                    "Error configuring AutoCommit.  " + "Your driver may not support getAutoCommit() or setAutoCommit(). "
                            + "Requested setting: " + autoCommit + ".  Cause: " + e,
                    e);
        }
    }

    @Override
    public void commit() throws SQLException {
        if(connection != null && !connection.getAutoCommit()){
            connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if(connection != null && !connection.getAutoCommit()){
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if(connection != null){
            connection.close();
        }
    }

    @Override
    public Integer getTimeout() throws SQLException {
        return null;
    }
}
