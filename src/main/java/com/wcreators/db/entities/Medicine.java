package com.wcreators.db.entities;

import com.wcreators.db.entities.userMedicine.UserMedicine;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Medicine")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Medicine {
    @Id
    @GeneratedValue
    @Column(name = "medicineId", nullable = false)
    @Basic
    @Setter
    @Getter
    private Integer medicineId;

    @NaturalId
    @Column(name = "title", nullable = false, unique = true, length = 256)
    @Basic
    @Setter
    @Getter
    private String title;

    @OneToMany(mappedBy = "pk.medicine", orphanRemoval = true, cascade = CascadeType.ALL)
    @Setter
    @Getter
    @Singular
    private Set<UserMedicine> userMedicines = new HashSet<UserMedicine>();

    public void addUserMedicine(UserMedicine userMedicine) {
        userMedicines.add(userMedicine);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Medicine that = (Medicine) o;
        return Objects.equals(medicineId, that.medicineId) &&
            Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, medicineId);
    }
}
