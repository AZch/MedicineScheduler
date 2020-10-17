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

@EnableScheduling
@Singleton
@UsingEventSheduler
public class MedicineScheduler implements Scheduler {

    @InjectByType(TelegramExternalAgent.class)
    private ExternalAgent externalAgent;

    private final List<ExternalAgent> externalAgents = new ArrayList<>();

    // TODO concurency
    private final List<Task> tasks = new ArrayList<>();

    @PostConstruct
    public void init() {
        externalAgents.add(externalAgent);
    }

    @Scheduled
    @Override
    public void run() {
        try {
            tasks.stream()
                    .filter(Task::isEventHappened)
                    .forEach(task -> {
                        System.out.println("eventeckie " + task.toString());
                        externalAgents.forEach(externalAgent -> {
                            System.out.println("send to agent " + externalAgent.toString());
                            externalAgent.sendEvent(task);
                        });
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTask(Task task) {
        System.out.println("add task " + task.toString());
        if (tasks.stream().noneMatch(existTask -> existTask.equals(task))) {
            System.out.println("added " + task.toString());
            tasks.add(task);
            // TODO send event here
        } else {
            System.out.println("already added");
        }
    }
}
