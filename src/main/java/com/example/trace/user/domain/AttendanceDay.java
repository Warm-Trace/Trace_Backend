package com.example.trace.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Entity
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "attendance_day",
        uniqueConstraints = @UniqueConstraint(name = "uq_att_unique_day", columnNames = {"user_id", "att_date"}))
public class AttendanceDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime checkedAt;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "att_date", nullable = false)
    private LocalDate attDate;

    public AttendanceDay() {
    }

    public AttendanceDay(Long userId, LocalDate attDate) {
        this.userId = userId;
        this.attDate = attDate;
    }

    @PrePersist
    void onCreate() {
        if (checkedAt == null) {
            checkedAt = LocalDateTime.now();
        }
    }
}
