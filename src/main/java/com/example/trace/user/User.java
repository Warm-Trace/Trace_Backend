package com.example.trace.user;

import com.example.trace.gpt.dto.VerificationDto;
import com.example.trace.notification.domain.NotificationEvent;
import com.example.trace.post.domain.PostType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String providerId; //provider에서 받아온 userId

    @Column(nullable = false)
    private String provider;

    private String email;

    private String nickname;

    private String profileImageUrl;

    @Builder.Default
    private Long verificationScore = 0L;

    @Builder.Default
    private Long verifiedPostCount = 0L;

    @Builder.Default
    private Long completedMissionCount = 0L;

    //spring security용으로 일단 두기.
    private String password;

    private String username;

    @Enumerated(EnumType.STRING) // Enum 이름을 DB에 문자열로 저장
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<NotificationEvent> notificationEvents = new ArrayList<>();

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void updateProfileImageUrl(String newProfileImageUrl) {
        this.profileImageUrl = newProfileImageUrl;
    }

    public void updateVerification(VerificationDto verificationDto, PostType type) {
        this.verificationScore += type.getTotalScore(verificationDto);
        if (verificationDto.isTextResult() || verificationDto.isImageResult()) {
            if (type == PostType.GOOD_DEED) {
                this.verifiedPostCount++;
            } else if (type == PostType.MISSION) {
                this.completedMissionCount++;
            }
        }
    }

    public boolean addNotification(NotificationEvent notificationEvent) {
        return this.notificationEvents.add(notificationEvent);
    }
}