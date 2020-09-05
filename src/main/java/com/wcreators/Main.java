package com.wcreators;

import com.wcreators.common.Application;
import com.wcreators.common.ApplicationContext;
import com.wcreators.domain.scheduler.MedicineScheduler;
import com.wcreators.domain.scheduler.Scheduler;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = Application.run("com.wcreators", new HashMap<>());

        Scheduler mScheduler = context.getObject(MedicineScheduler.class);
        mScheduler.run();
    }
}
