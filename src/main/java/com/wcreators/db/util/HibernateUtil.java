package com.wcreators.db.util;

import com.wcreators.common.annotations.PostConstruct;
import com.wcreators.common.annotations.Singleton;
import lombok.Getter;
import lombok.SneakyThrows;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.proxy.HibernateProxy;

@Singleton
public class HibernateUtil {

    public <T> T initializeAndUnproxy(T entity) {
        if (entity == null) {
            throw new
                    NullPointerException("Entity passed for initialization is null");
        }

        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer()
                    .getImplementation();
        }
        return entity;
    }

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
