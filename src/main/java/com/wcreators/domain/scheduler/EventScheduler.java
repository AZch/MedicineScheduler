package com.wcreators.domain.scheduler;

import com.wcreators.common.annotations.*;
import com.wcreators.common.annotations.eventSchedulers.EventSchedulers;
import com.wcreators.common.annotations.scheduling.EnableScheduling;
import com.wcreators.common.annotations.scheduling.Scheduled;
import com.wcreators.db.dao.UserMedicineDao;
import com.wcreators.task.Task;
import com.wcreators.task.UserMedicineTask;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

@EnableScheduling
@Singleton
public class EventScheduler implements Scheduler {
    @InjectByType
    UserMedicineDao userMedicineDao;

    @InjectProperty("SCHEDULE_INTERVAL")
    private Integer interval;

    @EventSchedulers
    private final List<Scheduler> schedulers = new ArrayList<>();

    @SneakyThrows
    @Scheduled
    @Override
    public void run() {
        userMedicineDao
                .getNear(interval)
                .forEach(userMedicine -> schedulers
                        .forEach(scheduler -> scheduler.addTask(new UserMedicineTask(userMedicine)))
                );
    }

    @Override
    public void addTask(Task task) {}
}
