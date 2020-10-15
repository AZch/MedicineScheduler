package com.wcreators.db.entities.userMedicine;

import com.wcreators.db.entities.Medicine;
import com.wcreators.db.entities.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "UserMedicine")
@AssociationOverrides({
    @AssociationOverride(name = "pk.user", joinColumns = @JoinColumn(name = "userId")),
    @AssociationOverride(name = "pk.medicine", joinColumns = @JoinColumn(name = "medicineId"))
})
public class UserMedicine {
    @EmbeddedId
    @Setter
    @Getter
    private UserMedicineId pk = new UserMedicineId();

    @ElementCollection
    @CollectionTable(name = "ExecutionTimes")
    @Column(name = "executionTime")
    @Setter
    @Getter
    private List<Integer> executionTimes = new ArrayList<>();

    @Column(name = "executionType")
    @Setter
    @Getter
    private ExecutionType executionType;

    @Column(name = "notifyEveryMinutes")
    @Setter
    @Getter
    private int notifyEveryMinutes;

    @Transient
    public User getUser() {
        return getPk().getUser();
    }

    public void setUser(User user) {
        getPk().setUser(user);
    }

    @Transient
    public Medicine getMedicine() {
        return getPk().getMedicine();
    }

    public void setMedicine(Medicine medicine) {
        getPk().setMedicine(medicine);
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserMedicine that = (UserMedicine) o;

        if (pk != null ? !pk.equals(that.getPk()) : that.pk != null) {
            return false;
        }
//        System.out.println(executionTimes);
//        System.out.println(that.executionTimes);
//        System.out.println(executionTimes.equals(that.executionTimes));
//        System.out.println(Arrays.equals(executionTimes, that.executionTimes));

        return executionType == that.executionType &&
                notifyEveryMinutes == that.notifyEveryMinutes;// &&
//                executionTimes.equals(that.executionTimes);
    }

    public int hashCode() {
        return Objects.hash(pk.hashCode(), executionType, notifyEveryMinutes, executionTimes);
    }
}
