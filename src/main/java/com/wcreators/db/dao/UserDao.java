package com.wcreators.db.dao;

import com.wcreators.common.annotations.InjectByType;
import com.wcreators.db.entities.User;
import com.wcreators.db.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class UserDao {
    @InjectByType
    HibernateUtil utils;

    public List<User> get() {
        Session session = utils.getSessionFactory().openSession();
        session.beginTransaction();
        List<User> users = session.createQuery("from User", User.class).list();
        session.getTransaction().commit();
        session.close();
        return users;
    }

    public User getById(String id) {
        Session session = utils.getSessionFactory().openSession();
        session.beginTransaction();

        User user = session.createQuery("from User where id = :id", User.class)
                .setParameter("id", id)
                .getSingleResult();

        session.getTransaction().commit();
        session.close();
        return user;
    }

    public User getByEmail(String email) {
        Session session = utils.getSessionFactory().openSession();
        session.beginTransaction();
        User user = session.createQuery("from User where email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();

        session.getTransaction().commit();
        session.close();
        return user;
    }

    public boolean create(User user) {
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
            return false;
        }
        return true;
    }
}
