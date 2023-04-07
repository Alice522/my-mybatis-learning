package cn.fj.mybatis.binding;

import cn.fj.mybatis.bulider.annotation.MapperAnnotationBuilder;
import cn.fj.mybatis.session.Configuration;
import cn.fj.mybatis.session.SqlSession;
import cn.hutool.core.lang.ClassScanner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapperRegistry {

    private final Configuration configuration;
    private final Map<Class<?>,MapperProxyFactory<?>> knownMappers = new HashMap<>();

    public MapperRegistry(Configuration configuration) {
        this.configuration = configuration;
    }

    /*
     * @description: 获取指定接口的代理对象
     * @author: liuzhuochuan
     * @date: 2023-03-20 14:12
     * @return: T
     **/
    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> classType, SqlSession sqlSession){
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(classType);

        if(null == mapperProxyFactory){
            throw new BindingException("Type " + classType + " is not known to the MapperRegistry.");
        }

        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new BindingException("Error getting mapper instance. Cause: " + e, e);
        }
    }

    public <T> boolean hasMapper(Class<T> type){
        return knownMappers.containsKey(type);
    }

    public <T> void addMapper(Class<T> type){
        if(type.isInterface()){
            if (hasMapper(type)) {
                throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
            }
            knownMappers.put(type,new MapperProxyFactory<>(type));
            MapperAnnotationBuilder mapperAnnotationBuilder = new MapperAnnotationBuilder(configuration, type);
            mapperAnnotationBuilder.parse();
        }
    }

    public void addMappers(String packName){
        Set<Class<?>> classSet = ClassScanner.scanPackage(packName);
        for(Class<?> clazz : classSet){
            addMapper(clazz);
        }
    }

}
