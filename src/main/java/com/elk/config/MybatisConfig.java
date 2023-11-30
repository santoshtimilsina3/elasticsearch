package com.elk.config;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

@ApplicationScoped
public class MybatisConfig {

    @ApplicationScoped
    @Produces
    public SqlSession getSqlSession() throws Exception {
        return sqlSessionFactory().openSession();
    }
    @ApplicationScoped
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        try (InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml")) {
            return new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException exception) {
            throw new IOException("Unable to Read Configuration file");
        }
    }

}
