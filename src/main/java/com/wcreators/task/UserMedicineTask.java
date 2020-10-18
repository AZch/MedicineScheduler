package com.wcreators.task;

import com.wcreators.db.entities.agent.AgentType;
import com.wcreators.db.entities.userMedicine.UserMedicine;
import lombok.Builder;

import java.time.LocalTime;
import java.util.Objects;

@Builder
public class UserMedicineTask implements Task {
    private final UserMedicine task;
    @Builder.Default
    private boolean isDone = false;

    @Override
    public boolean isEventHappened() {
        Integer nowSeconds = LocalTime.now().toSecondOfDay();
        boolean isHappened = task.getExecutionTimes().stream().anyMatch(executionTime -> executionTime < nowSeconds);
        if (isHappened) {
            task.updateExecutionTimes(nowSeconds);
        }
        return isHappened;
    }

    @Override
    public void done() {
        isDone = true;
    }

    @Override
    public boolean taskIsDone() {
        return isDone;
    }

    @Override
    public Long[] agentIdsByType(AgentType type) {
        return task.getUser().agentIdsByType(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserMedicineTask that = (UserMedicineTask) o;
        return task.equals(that.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task);
    }

    @Override
    public String toString() {
        return task.getMedicine().getTitle();
    }
}
