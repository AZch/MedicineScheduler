package com.wcreators.db.entities.userMedicine;

import com.wcreators.db.entities.Medicine;
import com.wcreators.db.entities.User;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class UserMedicineId implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(referencedColumnName = "userId")
    @NotFound(action = NotFoundAction.IGNORE)
    @Setter
    @Getter
    @Singular
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "medicineId")
    @NotFound(action = NotFoundAction.IGNORE)
    @Setter
    @Getter
    @Singular
    private Medicine medicine;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserMedicineId that = (UserMedicineId) o;

        return Objects.equals(user, that.user) && Objects.equals(medicine, that.medicine);
    }

    public int hashCode() {
        return Objects.hash(user, medicine);
    }
}
