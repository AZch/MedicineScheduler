package com.wcreators.db.entities;

import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Medicine")
@ToString
@NoArgsConstructor
public class MedicineEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    @Basic
    @Setter
    @Getter
    private UUID id;

    @NaturalId
    @Column(name = "title", nullable = false, unique = true, length = 256)
    @Basic
    @Setter
    @Getter
    private String title;

    @ManyToMany(mappedBy = "medicines")
    @Setter
    @Getter
    @Singular
    private Set<UserEntity> users;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MedicineEntity that = (MedicineEntity) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, id);
    }
}
