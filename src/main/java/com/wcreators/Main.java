package com.wcreators;

import com.wcreators.common.Application;
import com.wcreators.common.ApplicationContext;
import com.wcreators.domain.scheduler.MedicineScheduler;
import com.wcreators.domain.scheduler.Scheduler;
import org.telegram.telegrambots.ApiContextInitializer;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        ApplicationContext context = Application.run("com.wcreators", new HashMap<>());

//        Agent tgAgent = context.getObject(TelegramAgent.class);
//        UserDao userDao = context.getObject(UserDao.class);
//        userDao.get().forEach(user -> System.out.println(user.toString()));
        Scheduler mScheduler = context.getObject(MedicineScheduler.class);
        mScheduler.run();
    }
}
