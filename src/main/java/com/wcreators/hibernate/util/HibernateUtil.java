package com.wcreators.hibernate.util;

import com.wcreators.common.annotations.PostConstruct;
import com.wcreators.common.annotations.Singleton;
import lombok.Getter;
import lombok.SneakyThrows;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Singleton
public class HibernateUtil {

    @Getter
    private SessionFactory sessionFactory;

    @PostConstruct
    @SneakyThrows
    public void init() {
        Configuration configuration = new Configuration();
        configuration.configure();

        sessionFactory = configuration.buildSessionFactory();
    }
}
