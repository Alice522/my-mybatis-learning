package cn.fj.mybatis.bulider;

import cn.fj.mybatis.session.Configuration;

public class MapperBuilderAssistant extends BaseBuilder{

    private String currentNameSpace;

    private final String resource;

    public MapperBuilderAssistant(Configuration configuration,String resource) {
        super(configuration);
        this.resource = resource;
    }

    public String getCurrentNameSpace() {
        return currentNameSpace;
    }

    public void setCurrentNameSpace(String currentNameSpace) {
        this.currentNameSpace = currentNameSpace;
    }
}
