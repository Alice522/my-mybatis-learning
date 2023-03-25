package cn.fj.mybatis.session;

public interface SqlSessionFactory {

    SqlSession openSession();

    SqlSession openSession(boolean autoCommit);

    SqlSession openSession(TransactionIsolationLevel level);
}
