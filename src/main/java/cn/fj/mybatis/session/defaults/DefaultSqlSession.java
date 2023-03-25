package cn.fj.mybatis.session.defaults;

import cn.fj.mybatis.exceptions.TooManyResultsException;
import cn.fj.mybatis.session.Configuration;
import cn.fj.mybatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statement) {
        return this.selectOne(statement,null);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        List<T> list = this.selectList(statement, parameter);
        if(list.size() == 1){
            return list.get(0);
        }
        if (list.size() > 1) {
            throw new TooManyResultsException(
                    "Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        } else {
            return null;
        }
    }

    @Override
    public <E> List<E> selectList(String statement) {
        return this.selectList(statement,null);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        ArrayList<E> list = new ArrayList<>();
        list.add((E) ("你被代理了！" + "方法：" + statement + " 入参：" + parameter));
        return list;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type,this);
    }
}
