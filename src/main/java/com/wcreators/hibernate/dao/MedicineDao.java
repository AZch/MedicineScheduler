package com.wcreators.hibernate.dao;

import com.wcreators.common.annotations.InjectByType;
import com.wcreators.hibernate.entities.MedicineEntity;
import com.wcreators.hibernate.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class MedicineDao {
    @InjectByType
    HibernateUtil utils;

    public List<MedicineEntity> get() {
        Session session = utils.getSessionFactory().openSession();
        session.beginTransaction();
        List<MedicineEntity> medicines = session.createQuery("from MedicineEntity", MedicineEntity.class).list();
        session.getTransaction().commit();
        session.close();
        return medicines;
    }

    public void save(MedicineEntity medicine) {
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
