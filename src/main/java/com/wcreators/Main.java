package com.wcreators;

import com.wcreators.common.Application;
import com.wcreators.common.ApplicationContext;
import com.wcreators.domain.scheduler.EventScheduler;
import com.wcreators.domain.scheduler.MedicineScheduler;
import com.wcreators.domain.scheduler.Scheduler;
import org.telegram.telegrambots.ApiContextInitializer;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
//        new GenerateSchema().generate();
        ApiContextInitializer.init();
        ApplicationContext context = Application.run("com.wcreators", new HashMap<>());

        Scheduler eScheduler = context.getObject(EventScheduler.class);
        Scheduler mScheduler = context.getObject(MedicineScheduler.class);
        mScheduler.run();
        eScheduler.run();
    }
}
