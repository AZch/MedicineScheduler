package com.wcreators.db.dao;

import com.wcreators.common.annotations.InjectByType;
import com.wcreators.db.entities.userMedicine.UserMedicine;
import com.wcreators.db.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalTime;
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
                "JOIN FETCH UM.pk.user U " +
                "JOIN FETCH U.agents " +
                "JOIN FETCH UM.pk.medicine M " +
                "WHERE U.userId != NULL AND M.medicineId != NULL " +
                        "AND ET > :etMore AND ET < :etLess"
                , UserMedicine.class)
                .setParameter("etMore", LocalTime.now().toSecondOfDay() - seconds)
                .setParameter("etLess", LocalTime.now().toSecondOfDay() + seconds)
                .getResultList(); // TODO what different between this and list()?
        System.out.println(LocalTime.now().toSecondOfDay() - seconds);
        System.out.println(LocalTime.now().toSecondOfDay() + seconds);
        System.out.println(userMedicines.size());
        session.getTransaction().commit();
        session.close();
        return userMedicines.stream().peek(userMedicine -> {
            userMedicine.setUser(utils.initializeAndUnproxy(userMedicine.getUser()));
            userMedicine.setMedicine(utils.initializeAndUnproxy(userMedicine.getMedicine()));
        }).collect(Collectors.toList());
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
