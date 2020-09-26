package com.wcreators.domain.scheduler;

import com.wcreators.task.Task;

public interface Scheduler extends Runnable {
    void run();
    void addTask(Task task);
}
