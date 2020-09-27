package com.wcreators.db.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "Agent")
@ToString
@NoArgsConstructor
public class AgentEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    @Basic
    @Setter
    @Getter
    private UUID id;

    @Enumerated
    @Column(name = "agentType", nullable = false)
    @Basic
    @Setter
    @Getter
    private AgentType agentType;

    @Column(name = "agentId", nullable = false, length = 100)
    @Basic
    @Setter
    @Getter
    private String agentId;

    @ManyToMany(mappedBy = "agents")
    @Setter
    @Getter
    @Singular
    private Set<UserEntity> users;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AgentEntity that = (AgentEntity) obj;
        return agentType == that.agentType &&
                Objects.equals(id, that.id) &&
                Objects.equals(agentId, that.agentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentType, agentId, id);
    }
}
