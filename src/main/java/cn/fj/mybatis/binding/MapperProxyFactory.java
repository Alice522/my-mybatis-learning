package cn.fj.mybatis.binding;

import cn.fj.mybatis.session.SqlSession;

import java.lang.reflect.Proxy;

public class MapperProxyFactory<T> {

    private final Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

    @SuppressWarnings("unchecked")
    protected T newInstance(MapperProxy<T> mapperProxy){
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(),new Class[]{mapperInterface},mapperProxy);
    }

    public T newInstance(SqlSession sqlSession){
        return newInstance(new MapperProxy<>(mapperInterface,sqlSession));
    }
}
