package com.wcreators.db.dao;

import com.wcreators.common.annotations.InjectByType;
import com.wcreators.db.entities.Medicine;
import com.wcreators.db.entities.agent.AgentType;
import com.wcreators.db.entities.userMedicine.UserMedicine;
import com.wcreators.db.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MedicineDao {
    @InjectByType
    HibernateUtil utils;

    public List<Medicine> get() {
        Session session = utils.getSessionFactory().openSession();
        session.beginTransaction();
        List<Medicine> medicines = session.createQuery("from Medicine", Medicine.class).list();
        session.getTransaction().commit();
        session.close();
        return medicines;
    }

    public Medicine getByTitle(String title) {
        try {
            Session session = utils.getSessionFactory().openSession();
            session.beginTransaction();
            Medicine medicine = session.createQuery("FROM Medicine M WHERE M.title = :title", Medicine.class)
                    .setParameter("title", title)
                    .getSingleResult();
            session.getTransaction().commit();
            session.close();
            return medicine;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // TODO move to usermedicine dao
    public boolean delete(String title, Long agentId, AgentType type) {
        try {
            Session session = utils.getSessionFactory().openSession();
            session.beginTransaction();

            List<UserMedicine> userMedicines = session.createQuery("" +
                    "SELECT DISTINCT UM " +
                    "from UserMedicine AS UM " +
                    "JOIN FETCH UM.pk.user U " +
                    "JOIN FETCH U.agents A " +
                    "JOIN FETCH UM.pk.medicine M " +
                    "WHERE A.agentId = :agentId AND A.agentType = :type " +
                    "AND M.title = :title", UserMedicine.class)
                    .setParameter("agentId", agentId)
                    .setParameter("type", type)
                    .setParameter("title", title)
                    .getResultList();
            userMedicines.parallelStream().forEach(session::delete);

            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void create(Medicine medicine) {
        Transaction transaction = null;
        try (Session session = utils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(medicine);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    public List<Medicine> getMedicineListByUserAgent(AgentType type, Long agentId) {
        try {
            Session session = utils.getSessionFactory().openSession();
            session.beginTransaction();

            List<Medicine> medicines = session.createQuery("" +
                    "SELECT DISTINCT M FROM Medicine AS M " +
                    "LEFT JOIN FETCH M.userMedicines UM " +
                    "LEFT JOIN FETCH UM.pk.user U " +
                    "LEFT JOIN U.agents A " +
                    "WHERE A.agentId = :agentId AND A.agentType = :type", Medicine.class)
                    .setParameter("agentId", agentId)
                    .setParameter("type", type)
                    .getResultList();


            session.getTransaction().commit();
            session.close();
            return medicines;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
