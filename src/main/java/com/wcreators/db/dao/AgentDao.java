package com.wcreators.db.dao;

import com.wcreators.common.annotations.InjectByType;
import com.wcreators.db.entities.agent.Agent;
import com.wcreators.db.entities.User;
import com.wcreators.db.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class AgentDao {
    @InjectByType
    HibernateUtil utils;

    public List<Agent> get() {
        Session session = utils.getSessionFactory().openSession();
        session.beginTransaction();
        List<Agent> agents = session.createQuery("from Agent", Agent.class).list();
        session.getTransaction().commit();
        session.close();
        return agents;
    }

    public User getUserByAgent(Long agentId) {
        Session session = utils.getSessionFactory().openSession();
        session.beginTransaction();

        Agent agent = session.createQuery("select A from Agent  as A join fetch A.user where A.agentId = :agentId", Agent.class)
                .setParameter("agentId", agentId)
                .getSingleResult();

        session.getTransaction().commit();
        session.close();
        return agent.getUser();
    }

    public void create(Agent agent) {
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
