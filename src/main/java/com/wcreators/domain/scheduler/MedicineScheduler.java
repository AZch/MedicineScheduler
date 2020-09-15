package com.wcreators.domain.scheduler;

import com.wcreators.agents.Agent;
import com.wcreators.common.annotations.*;
import com.wcreators.task.Task;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

@EnableScheduling
@Singleton
public class MedicineScheduler implements Scheduler {

    private final List<Task> tasks = new ArrayList<>();
    @Listner
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
