package com.wcreators.db.dao;

import com.wcreators.common.annotations.InjectByType;
import com.wcreators.db.entities.Medicine;
import com.wcreators.db.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
}
