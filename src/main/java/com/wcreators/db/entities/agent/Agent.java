package com.wcreators.db.entities.agent;

import com.wcreators.db.entities.User;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name = "Agent")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Agent {
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

    @Column(name = "agentId", unique = true, nullable = false)
    @Basic
    @Setter
    @Getter
    private Long agentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    @Getter
    @Singular
    private User user;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Agent that = (Agent) obj;
        return agentType == that.agentType &&
                Objects.equals(id, that.id) &&
                Objects.equals(agentId, that.agentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentType, agentId, id);
    }
}
