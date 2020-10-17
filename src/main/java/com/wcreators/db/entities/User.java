package com.wcreators.db.entities;

import com.wcreators.db.entities.agent.Agent;
import com.wcreators.db.entities.userMedicine.UserMedicine;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.*;

@Entity
@Table(name = "User")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    @Column(name = "userId", nullable = false)
    @Basic
    @Setter
    @Getter
    private UUID userId;

    @NaturalId
    @Column(name = "email", unique = true, nullable = false, length = 256)
    @Basic
    @Setter
    @Getter
    private String email;

    @Column(name = "firstName", nullable = false, length = 100)
    @Basic
    @Setter
    @Getter
    private String firstName;

    @Column(name = "lastName", nullable = false, length = 100)
    @Basic
    @Setter
    @Getter
    private String lastName;

    @OneToMany(mappedBy = "pk.user", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotFound(action = NotFoundAction.IGNORE)
    @Setter
    @Getter
    private Set<UserMedicine> userMedicines = new HashSet<>();

    public void addUserMedicine(UserMedicine userMedicine) {
        userMedicines.add(userMedicine);
    }

    @OneToMany(mappedBy = "user", cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY)
    @Setter
    @Getter
    @Singular
    private Set<Agent> agents;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User that = (User) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, userId);
    }
}
