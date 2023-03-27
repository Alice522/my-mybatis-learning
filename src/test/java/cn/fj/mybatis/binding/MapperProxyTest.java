package cn.fj.mybatis.binding;


import cn.fj.mybatis.mapper.SchoolMapper;
import cn.fj.mybatis.session.SqlSession;
import cn.fj.mybatis.session.SqlSessionFactory;
import cn.fj.mybatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.sql.Connection;


public class MapperProxyTest {

    @Test
    public void test_xmlBuilder() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("src/test/java/cn/fj/mybatis/resources/mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(new InputStreamReader(fileInputStream));

        SqlSession sqlSession = sqlSessionFactory.openSession();
        for(int i=0;i < 50;i++){
            Connection connect = sqlSession.getConnect();
            System.out.println(connect);
        }
    }
}
