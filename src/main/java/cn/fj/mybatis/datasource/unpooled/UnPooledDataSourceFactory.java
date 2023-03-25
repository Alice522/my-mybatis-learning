package cn.fj.mybatis.datasource.unpooled;

import cn.fj.mybatis.datasource.DataSourceFactory;
import org.apache.ibatis.datasource.DataSourceException;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import javax.sql.DataSource;
import java.util.Properties;

public class UnPooledDataSourceFactory implements DataSourceFactory {

    private static final String DRIVER_PROPERTY_PREFIX = "driver.";
    private static final int DRIVER_PROPERTY_PREFIX_LENGTH = DRIVER_PROPERTY_PREFIX.length();
    private DataSource dataSource;

    public UnPooledDataSourceFactory() {
        this.dataSource = new UnPooledDataSource();
    }
    @Override
    public void setProperties(Properties properties) {
        Properties driverProperties = new Properties();
        MetaObject metaObject = SystemMetaObject.forObject(dataSource);
        for(Object key : properties.keySet()){
            String propertyName = (String) key;
            if(propertyName.startsWith(DRIVER_PROPERTY_PREFIX)){
                String value = properties.getProperty(propertyName);
                driverProperties.setProperty(propertyName.substring(DRIVER_PROPERTY_PREFIX_LENGTH),value);
            }else if (metaObject.hasSetter(propertyName)) {
                metaObject.setValue(propertyName,properties.get(propertyName));
            }else {
                throw new DataSourceException("Unknown DataSource property: " + propertyName);
            }
        }
        if(driverProperties.size() > 0){
            metaObject.setValue("driverProperties", driverProperties);
        }
    }
    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }
}
