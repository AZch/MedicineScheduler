package com.wcreators.db.dao;

import com.wcreators.common.annotations.InjectByType;
import com.wcreators.db.entities.AgentEntity;
import com.wcreators.db.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class AgentDao {
    @InjectByType
    HibernateUtil utils;

    public List<AgentEntity> get() {
        Session session = utils.getSessionFactory().openSession();
        session.beginTransaction();
        List<AgentEntity> agents = session.createQuery("from AgentEntity", AgentEntity.class).list();
        session.getTransaction().commit();
        session.close();
        return agents;
    }

    public void create(AgentEntity agent) {
        Transaction transaction = null;
        try (Session session = utils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(agent);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
