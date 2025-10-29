package com.example.trace.notification.domain;

import com.example.trace.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean mission = true;

    @Column(nullable = false)
    private boolean comment = true;

    @Column(nullable = false)
    private boolean emotion = true;

    protected NotificationSetting() {
    }

    private NotificationSetting(User user) {
        this.user = user;
    }

    public static NotificationSetting of(User user) {
        return new NotificationSetting(user);
    }

    public void setNotificationEnabled(boolean enabled, SourceType type) {
        switch (type) {
            case MISSION -> this.mission = enabled;
            case COMMENT -> this.comment = enabled;
            case EMOTION -> this.emotion = enabled;
            default -> log.error("Unsupported notification type requested: {}", type);
        }
    }

    public boolean statusOf(SourceType type) {
        return switch (type) {
            case MISSION -> this.mission;
            case COMMENT -> this.comment;
            case EMOTION -> this.emotion;
            default -> false;
        };
    }
}
