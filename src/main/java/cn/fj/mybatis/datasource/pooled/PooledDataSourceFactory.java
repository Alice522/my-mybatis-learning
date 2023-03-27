package cn.fj.mybatis.datasource.pooled;

import cn.fj.mybatis.datasource.unpooled.UnPooledDataSourceFactory;

public class PooledDataSourceFactory extends UnPooledDataSourceFactory {

    public PooledDataSourceFactory() {
        this.dataSource = new PooledDataSource();
    }
}
