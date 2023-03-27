package cn.fj.mybatis.datasource.pooled;

import cn.fj.mybatis.datasource.unpooled.UnPooledDataSource;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class PooledDataSource implements DataSource {

    private static final Log log = LogFactory.getLog(PooledDataSource.class);

    private final UnPooledDataSource dataSource;

    private final PoolState state = new PoolState(this);

    protected int poolMaximumActiveConnections = 10;

    protected int poolMaximumIdleConnections = 5;

    protected int poolMaximumCheckoutTime = 20000;

    protected int poolTimeToWait = 20000;

    private int expectedConnectionTypeCode;

    private final Lock lock = new ReentrantLock();

    private final Condition condition = lock.newCondition();

    public PooledDataSource() {
        this.dataSource = new UnPooledDataSource();
    }

    public PooledDataSource(UnPooledDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(dataSource.getUsername(), dataSource.getPassword());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return popConnection(username,password).getProxyConnection();
    }

    public void setDriver(String driver) {
        dataSource.setDriver(driver);
        forceCloseAll();
    }

    public void setUrl(String url) {
        dataSource.setUrl(url);
        forceCloseAll();
    }

    public void setUsername(String username) {
        dataSource.setUsername(username);
        forceCloseAll();
    }

    public void setPassword(String password) {
        dataSource.setPassword(password);
        forceCloseAll();
    }

    public String getDriver() {
        return dataSource.getDriver();
    }

    public String getUrl() {
        return dataSource.getUrl();
    }

    public String getUsername() {
        return dataSource.getUsername();
    }

    public String getPassword() {
        return dataSource.getPassword();
    }


    public void forceCloseAll(){
        lock.lock();

        try {
            for(int i=state.activeConnections.size();i > 0;i--){
                try {
                    PooledConnection conn = state.activeConnections.remove(i - 1);
                    Connection realConnection = conn.getRealConnection();

                    if(!realConnection.getAutoCommit()){
                        realConnection.rollback();
                    }
                    realConnection.close();
                } catch (Exception e) {

                }
            }
            for(int i=state.idleConnections.size();i > 0;i--){
                try {
                    PooledConnection conn = state.idleConnections.remove(i - 1);
                    Connection realConnection = conn.getRealConnection();

                    if(!realConnection.getAutoCommit()){
                        realConnection.rollback();
                    }
                    realConnection.close();
                } catch (Exception e) {

                }
            }
        } finally {
            lock.unlock();
        }
    }

    protected void pushConnection(PooledConnection connection) throws SQLException{
        lock.lock();
        try {
            state.activeConnections.remove(connection);
            if(state.idleConnections.size() < poolMaximumIdleConnections){
                state.accumulatedCheckoutTime += connection.getCheckoutTime();
                if(!connection.getRealConnection().getAutoCommit()){
                    connection.getRealConnection().rollback();
                }
                PooledConnection newConn = new PooledConnection(connection.getRealConnection(), this);
                newConn.setCreatedTimestamp(connection.getCreatedTimestamp());
                newConn.setLastUsedTimestamp(connection.getLastUsedTimestamp());
                state.idleConnections.add(newConn);
                condition.signal();
            }else {
                state.accumulatedCheckoutTime += connection.getCheckoutTime();
                if(!connection.getRealConnection().getAutoCommit()){
                    connection.getRealConnection().rollback();
                }
                connection.getRealConnection().close();
            }
        } finally {
            lock.unlock();
        }
    }

    private PooledConnection popConnection(String username,String password) throws SQLException{
        PooledConnection conn = null;
        boolean countedWait = false;
        long t = System.currentTimeMillis();

        while (conn == null) {
            lock.lock();
            try {
                if (!state.idleConnections.isEmpty()) {
                    conn = state.idleConnections.remove(0);
                    System.out.println("从连接池中获取连接，当前连接池中有空闲连接");
                } else if (state.activeConnections.size() < poolMaximumActiveConnections) {
                    conn = new PooledConnection(dataSource.getConnection(), this);
                    System.out.println("从连接池中获取连接，当前连接池中没有空闲连接，创建新的连接");
                } else {
                    PooledConnection oldestActiveConnection = state.activeConnections.get(0);
                    long longestCheckoutTime = oldestActiveConnection.getCheckoutTime();
                    if (longestCheckoutTime > poolMaximumCheckoutTime) {
                        state.claimedOverdueConnectionCount++;
                        state.accumulatedCheckoutTimeOfOverdueConnections += longestCheckoutTime;
                        state.accumulatedCheckoutTime += longestCheckoutTime;
                        state.activeConnections.remove(oldestActiveConnection);

                        if (!oldestActiveConnection.getRealConnection().getAutoCommit()) {
                            oldestActiveConnection.getRealConnection().rollback();
                        }

                        conn = new PooledConnection(oldestActiveConnection.getRealConnection(), this);
                        conn.setCreatedTimestamp(oldestActiveConnection.getCreatedTimestamp());
                        conn.setLastUsedTimestamp(oldestActiveConnection.getLastUsedTimestamp());
                        System.out.println("从连接池中获取连接，当前连接池中没有空闲连接，但有活跃连接超时，回收该连接");
                    } else {
                        try {
                            if (!countedWait) {
                                state.hadToWaitCount++;
                                countedWait = true;
                            }
                            long wt = System.currentTimeMillis();
                            condition.await(poolTimeToWait, TimeUnit.MILLISECONDS);
                            state.accumulatedWaitTime += System.currentTimeMillis() - wt;
                            System.out.println("从连接池中获取连接，当前连接池中没有空闲连接，等待空闲连接释放");
                        } catch (InterruptedException e) {
                            // set interrupt flag
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }

                if (conn != null) {
                    if (!conn.getRealConnection().getAutoCommit()) {
                        conn.getRealConnection().rollback();
                    }
                    conn.setCheckoutTimestamp(System.currentTimeMillis());
                    conn.setLastUsedTimestamp(System.currentTimeMillis());
                    state.requestCount++;
                    state.accumulatedRequestTime += System.currentTimeMillis() - t;
                    state.activeConnections.add(conn);
                    System.out.println("从连接池中获取连接，成功");
                }
            } finally {
                lock.unlock();
            }
        }

        if (conn == null) {
            throw new SQLException(
                    "PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
        }
        return conn;
    }


    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
