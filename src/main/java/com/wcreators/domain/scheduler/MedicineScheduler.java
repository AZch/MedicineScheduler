package com.wcreators.domain.scheduler;

import com.wcreators.common.annotations.eventSchedulers.UsingEventSheduler;
import com.wcreators.common.annotations.externalAgents.ExternalAgnts;
import com.wcreators.common.annotations.scheduling.EnableScheduling;
import com.wcreators.common.annotations.scheduling.Scheduled;
import com.wcreators.externalAgent.ExternalAgent;
import com.wcreators.common.annotations.*;
import com.wcreators.task.Task;

import java.util.ArrayList;
import java.util.List;

@EnableScheduling
@Singleton
@UsingEventSheduler
public class MedicineScheduler implements Scheduler {

    private final List<Task> tasks = new ArrayList<>();
    private final List<Agent> agents = new ArrayList<>();

    @Override
    public void addTask(Task task) {
        if (!tasks.contains(task)) {
            tasks.add(task);
        }
    }

    @SneakyThrows
    @Override
    @Scheduled
    public void run() {
        tasks.stream()
            .filter(Task::isEventHappened)
            .forEach(task -> agents.forEach(agent -> agent.sendEvent(task)));
    }
}
