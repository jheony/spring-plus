package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "log")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long todoId;
    private Long managerId;
    private boolean isSuccess;

    @CreatedDate
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    public Log(Long todoId, Long managerId, boolean isSuccess) {
        this.todoId = todoId;
        this.managerId = managerId;
        this.isSuccess = isSuccess;
        this.createdAt = LocalDateTime.now();
    }

    public void updateLog(Long managerId, boolean isSuccess){
        this.managerId = managerId;
        this.isSuccess = isSuccess;
    }
}
