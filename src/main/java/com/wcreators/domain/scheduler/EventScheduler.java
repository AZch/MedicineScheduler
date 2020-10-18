package com.wcreators.domain.scheduler;

import com.wcreators.common.annotations.*;
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
    private UserMedicineDao userMedicineDao;

    @InjectByType(MedicineScheduler.class)
    private Scheduler medicineScheduler;

    @InjectProperty("SCHEDULE_INTERVAL")
    private Integer interval;

    private final List<Scheduler> schedulers = new ArrayList<>();

    @PostConstruct
    public void init() {
        schedulers.add(medicineScheduler);
    }

    @SneakyThrows
    @Scheduled
    @Override
    public void run() {
        userMedicineDao
                .getNear(interval)
                .forEach(userMedicine -> schedulers
                        .forEach(scheduler -> scheduler.addTask(UserMedicineTask.builder().task(userMedicine).build()))
                );
    }

    @Override
    public void addTask(Task task) {}
}
