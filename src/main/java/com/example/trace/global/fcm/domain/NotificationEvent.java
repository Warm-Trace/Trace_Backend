package com.example.trace.global.fcm.domain;

import com.example.trace.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 관련 리소스 id
    private Long refId;

    // optional(for notification message)
    private String title;

    // optional(for notification message)
    private String body;

    // data 메시지인 경우 json 직렬화해서 저장
    @Column(columnDefinition = "TEXT")
    private String data;

    private String timestamp;

    @Builder.Default
    private Boolean isRead = false;

    @Builder.Default
    private Boolean isHidden = false;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    @Enumerated(EnumType.STRING)
    private NotificationEventType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public boolean mapToUser(User user) {
        if (this.user != null && this.user.equals(user)) {
            return false; // 이미 같은 user에 설정되어 있음
        }
        this.user = user;
        user.addNotification(this);
        return true;
    }

    //TODO: sourceType에 따라 refId 할당
}
