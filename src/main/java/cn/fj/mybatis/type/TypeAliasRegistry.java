package cn.fj.mybatis.type;

import cn.fj.mybatis.io.Resources;
import org.apache.ibatis.type.TypeException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TypeAliasRegistry {

    private final Map<String,Class<?>> typeAlias = new HashMap<>();

    public TypeAliasRegistry() {
        registerAlias("string", String.class);

        registerAlias("byte", Byte.class);
        registerAlias("char", Character.class);
        registerAlias("character", Character.class);
        registerAlias("long", Long.class);
        registerAlias("short", Short.class);
        registerAlias("int", Integer.class);
        registerAlias("integer", Integer.class);
        registerAlias("double", Double.class);
        registerAlias("float", Float.class);
        registerAlias("boolean", Boolean.class);
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> resolveAlias(String string){
        try {
            if(string == null){
                return null;
            }
            Class<T> value;
            String key = string.toLowerCase(Locale.ENGLISH);
            if(typeAlias.containsKey(key)){
                value = (Class<T>) typeAlias.get(key);
            }else {
                value = (Class<T>) Resources.classForName(string);
            }
            return value;
        } catch (Exception e) {
            throw new TypeException("Could not resolve type alias '" + string + "'.  Cause: " + e, e);
        }
    }

    public void registerAlias(Class<?> type){
        String alias = type.getSimpleName();
        registerAlias(alias,type);
    }

    public void registerAlias(String alias,Class<?> value){
        if(alias == null){
            throw new TypeException("The parameter alias cannot be null");
        }
        String key = alias.toLowerCase(Locale.ENGLISH);
        if(typeAlias.containsKey(key) && typeAlias.get(key) != null &&!typeAlias.get(key).equals(value)){
            throw new TypeException(
                    "The alias '" + alias + "' is already mapped to the value '" + typeAlias.get(key).getName() + "'.");
        }
        typeAlias.put(key,value);
    }
}
