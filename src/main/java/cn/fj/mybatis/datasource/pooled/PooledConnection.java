package cn.fj.mybatis.datasource.pooled;

import org.apache.ibatis.reflection.ExceptionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

public class PooledConnection implements InvocationHandler {

    private static final String CLOSE = "close";

    private static final Class<?>[] IFACES = new Class[]{Connection.class};

    private final PooledDataSource dataSource;
    private final Connection realConnection;
    private final Connection proxyConnection;

    private long checkoutTimestamp;

    private long createdTimestamp;

    private long lastUsedTimestamp;

    private int connectionTypeCode;


    public PooledConnection(Connection connection,PooledDataSource dataSource) {
        this.dataSource = dataSource;
        this.realConnection = connection;
        this.createdTimestamp = System.currentTimeMillis();
        this.lastUsedTimestamp = System.currentTimeMillis();
        this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),IFACES,this);
    }

    public long getCheckoutTime() {
        return System.currentTimeMillis() - checkoutTimestamp;
    }

    public void setCheckoutTimestamp(long checkoutTimestamp) {
        this.checkoutTimestamp = checkoutTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public void setLastUsedTimestamp(long lastUsedTimestamp) {
        this.lastUsedTimestamp = lastUsedTimestamp;
    }

    public void setConnectionTypeCode(int connectionTypeCode) {
        this.connectionTypeCode = connectionTypeCode;
    }

    public long getCheckoutTimestamp() {
        return checkoutTimestamp;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public long getLastUsedTimestamp() {
        return lastUsedTimestamp;
    }

    public int getConnectionTypeCode() {
        return connectionTypeCode;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if(CLOSE.equals(methodName)){
            dataSource.pushConnection(this);
            return null;
        }
        try {
            return method.invoke(realConnection,args);
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
    }

    public Connection getRealConnection() {
        return realConnection;
    }

    public Connection getProxyConnection() {
        return proxyConnection;
    }
}
