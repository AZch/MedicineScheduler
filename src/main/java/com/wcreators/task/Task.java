package com.wcreators.task;

import com.wcreators.db.entities.agent.AgentType;

public interface Task {
    boolean isEventHappened();
    void done();
    boolean taskIsDone();
    Long[] agentIdsByType(AgentType type);
}
