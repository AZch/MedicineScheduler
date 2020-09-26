package com.wcreators.hibernate.entities;

import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Medicine", schema = "test_db")
public class MedicineEntity {
    private String title;
    private UserEntity userByUserId;

    @Setter
    private int id;

    public MedicineEntity() {}

    @Basic
    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicineEntity that = (MedicineEntity) o;
        return Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    public UserEntity getUserByUserId() {
        return userByUserId;
    }

    public void setUserByUserId(UserEntity userByUserId) {
        this.userByUserId = userByUserId;
    }

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }
}
