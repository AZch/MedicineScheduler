package com.wcreators.hibernate.entities;

import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "User", schema = "test_db")
public class UserEntity {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @Setter
    private int id;
    private Collection<MedicineEntity> medicinesById;

    @Basic
    @Column(name = "firstName", length = 100)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Basic
    @Column(name = "lastName", length = 100)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Basic
    @Column(name = "email", nullable = false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "password", length = 100)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return id == that.id &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(email, that.email) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, password, id);
    }

    @OneToMany(mappedBy = "userByUserId")
    public Collection<MedicineEntity> getMedicinesById() {
        return medicinesById;
    }

    public void setMedicinesById(Collection<MedicineEntity> medicinesById) {
        this.medicinesById = medicinesById;
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName();
    }
}
