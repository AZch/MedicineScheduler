package com.wcreators.domain.scheduler;

import com.wcreators.common.annotations.eventSchedulers.UsingEventSheduler;
import com.wcreators.common.annotations.scheduling.EnableScheduling;
import com.wcreators.common.annotations.scheduling.Scheduled;
import com.wcreators.externalAgent.ExternalAgent;
import com.wcreators.common.annotations.*;
import com.wcreators.externalAgent.TelegramExternalAgent;
import com.wcreators.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EnableScheduling
@Singleton
@UsingEventSheduler
public class MedicineScheduler implements Scheduler {

    @InjectByType(TelegramExternalAgent.class)
    private ExternalAgent externalAgent;

    private final List<ExternalAgent> externalAgents = new ArrayList<>();

    private Map<Integer, Task> tasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        externalAgents.add(externalAgent);
    }

    @Scheduled
    @Override
    public void run() {
        try {
            tasks.forEach((integer, task) -> {
                if (task.taskIsDone()) {
                    tasks.remove(integer);
                } else if (task.isEventHappened()) {
                    externalAgents.forEach(agent -> agent.sendEvent(task));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTask(Task task) {
        System.out.println("add task " + task.toString());
        if (tasks.get(task.hashCode()) == null) {
            System.out.println("added " + task.toString());
            tasks.put(task.hashCode(), task);
            // TODO send event here
        } else {
            System.out.println("already added");
        }
    }
}
