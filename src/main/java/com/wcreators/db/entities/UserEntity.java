package com.wcreators.db.entities;

import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "User")
@ToString
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    @Basic
    @Setter
    @Getter
    private UUID id;

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

    @Column(name = "password", length = 100)
    @Basic
    @Setter
    @Getter
    private String password;

    @ManyToMany(cascade = { CascadeType.DETACH })
    @JoinTable(
            name = "User_Medicine",
            joinColumns = { @JoinColumn( name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "medicine_id") }
    )
    @Setter
    @Getter
    @Singular
    private Set<MedicineEntity> medicines;

    @ManyToMany(cascade = { CascadeType.REMOVE })
    @JoinTable (
            name = "User_Agent",
            joinColumns = { @JoinColumn( name = "user_id" ) },
            inverseJoinColumns = { @JoinColumn(name = "agent_id") }
    )
    @Setter
    @Getter
    @Singular
    private Set<AgentEntity> agents;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(email, that.email) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, password, id);
    }
}
