package com.example.trace.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AttendanceDay {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private AttendanceId id;

    @Column(nullable = false, updatable = false)
    private Instant checkedAt;

    @PrePersist
    void prePersist() {
        if (checkedAt == null) {
            checkedAt = Instant.now();
        }
    }

    @Embeddable
    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class AttendanceId implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L; // 직렬화 호환성을 위해
        private Long userId;
        private LocalDate attDate;
    }
}
