package com.wcreators.db.dao;

import com.wcreators.common.annotations.InjectByType;
import com.wcreators.common.annotations.Singleton;
import com.wcreators.db.entities.userMedicine.UserMedicine;
import com.wcreators.db.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.stream.Collectors;

public class UserMedicineDao {
    @InjectByType
    HibernateUtil utils;

    public List<UserMedicine> getNear(int seconds) {
        Session session = utils.getSessionFactory().openSession();
        session.beginTransaction();
        List<UserMedicine> userMedicines = session.createQuery("" +
                "SELECT DISTINCT UM " +
                "FROM UserMedicine AS UM " +
                "JOIN FETCH UM.executionTimes ET " +
                "JOIN FETCH UM.pk.user u " +
                "JOIN FETCH u.agents " +
                "JOIN FETCH UM.pk.medicine m " +
                "WHERE u.userId != NULL AND m.medicineId != NULL"/* AND ET > :etMore AND ET < :etLess"*/, UserMedicine.class)
//                .setParameter("etMore", LocalTime.now().toSecondOfDay() + 1)
//                .setParameter("etLess", LocalTime.now().toSecondOfDay() + 60)
                .list()
                .stream()
                .filter(userMedicine -> userMedicine.getUser() != null && userMedicine.getMedicine() != null)
                .collect(Collectors.toList());

        session.getTransaction().commit();
        session.close();
        return userMedicines;
    }

    public void create(UserMedicine userMedicine) {
        Transaction transaction = null;
        try (Session session = utils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(userMedicine);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

}
