package cn.fj.mybatis.session;

import cn.fj.mybatis.binding.MapperRegistry;
import cn.fj.mybatis.datasource.pooled.PooledDataSourceFactory;
import cn.fj.mybatis.datasource.unpooled.UnPooledDataSourceFactory;
import cn.fj.mybatis.mapping.Environment;
import cn.fj.mybatis.transaction.jdbc.JdbcTransactionFactory;
import cn.fj.mybatis.type.TypeAliasRegistry;

import java.util.HashSet;
import java.util.Set;

public class Configuration {
    protected Environment environment;
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

    protected TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    protected final Set<String> loadResources = new HashSet<>();

    public Configuration() {
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("UNPOOLED", UnPooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
    }

    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    public void setTypeAliasRegistry(TypeAliasRegistry typeAliasRegistry) {
        this.typeAliasRegistry = typeAliasRegistry;
    }

    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    public void setMapperRegistry(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void addLoadResource(String resource){
        loadResources.add(resource);
    }

    public boolean isResourceLoaded(String resource){
        return loadResources.contains(resource);
    }
}
