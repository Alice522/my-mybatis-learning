package cn.fj.mybatis.bulider;

import cn.fj.mybatis.session.Configuration;
import cn.fj.mybatis.type.TypeAliasRegistry;
import org.apache.ibatis.builder.BuilderException;

public abstract class BaseBuilder {

    protected final Configuration configuration;

    protected final TypeAliasRegistry typeAliasRegistry;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    protected <T> Class<? extends T> resolveClass(String alias){
        if(alias == null){
            return null;
        }
        try {
            return resolveAlias(alias);
        } catch (Exception e) {
            throw new BuilderException("Error resolving class. Cause: " + e, e);
        }
    }

    protected <T> Class<? extends T> resolveAlias(String alias){
        return typeAliasRegistry.resolveAlias(alias);
    }
}
