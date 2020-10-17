package com.wcreators.externalAgent;

import com.wcreators.task.Task;

public interface ExternalAgent {
    void sendEvent(Task task);
}
