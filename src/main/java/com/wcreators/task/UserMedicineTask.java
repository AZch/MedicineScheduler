package com.wcreators.task;

import com.wcreators.db.entities.userMedicine.UserMedicine;
import lombok.AllArgsConstructor;

import java.time.LocalTime;
import java.util.Objects;

@AllArgsConstructor
public class UserMedicineTask implements Task {
    private final UserMedicine task;

    @Override
    public boolean isEventHappened() {
        System.out.println("check event " + toString());
        Integer nowSeconds = LocalTime.now().toSecondOfDay();
        return task.getExecutionTimes().stream().anyMatch(executionTime -> executionTime > nowSeconds);
    }

    @Override
    public Long sendTo() {
        return task.getUser().getAgents().iterator().next().getAgentId();
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
