package com.wcreators.hibernate.dao;

import com.wcreators.common.annotations.InjectByType;
import com.wcreators.hibernate.entities.UserEntity;
import com.wcreators.hibernate.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserDao {
    @InjectByType
    HibernateUtil utils;

    public List<UserEntity> get() {
        Session session = utils.getSessionFactory().openSession();
        session.beginTransaction();
        List<UserEntity> users = session.createQuery("from UserEntity ", UserEntity.class).list();
        session.getTransaction().commit();
        session.close();
        return users;
    }

    public void save(UserEntity user) {
        Transaction transaction = null;
        try (Session session = utils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
