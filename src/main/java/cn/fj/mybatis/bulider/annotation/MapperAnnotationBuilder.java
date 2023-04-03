package cn.fj.mybatis.bulider.annotation;

import cn.fj.mybatis.bulider.MapperBuilderAssistant;
import cn.fj.mybatis.io.Resources;
import cn.fj.mybatis.session.Configuration;

import java.io.IOException;
import java.io.InputStream;

public class MapperAnnotationBuilder {

    private final Configuration configuration;
    private final MapperBuilderAssistant assistant;
    private final Class<?> type;

    public MapperAnnotationBuilder(Configuration configuration, Class<?> type) {
        String resource = type.getName().replace('.', '/') + ".java (best guess)";
        this.configuration = configuration;
        this.assistant = new MapperBuilderAssistant(configuration,resource);
        this.type = type;
    }

    public void parse(){
        String resource = type.toString();
        if(!configuration.isResourceLoaded(resource)){
            loadXmlResource();
            configuration.addLoadResource(resource);
        }
    }

    private void loadXmlResource() {
        if(!configuration.isResourceLoaded("namespace:" + type.getName())){
            String xmlResource = type.getName().replace(".","/") + ".xml";
            InputStream inputStream = type.getResourceAsStream("/" + xmlResource);
            if(null == inputStream){
                try {
                    inputStream = Resources.getResourceAsStream(type.getClassLoader(),xmlResource);
                } catch (IOException e) {
                }
            }
            if(inputStream != null){

            }
        }
    }
}
