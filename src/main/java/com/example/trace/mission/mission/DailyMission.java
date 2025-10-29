package com.example.trace.mission.mission;

import com.example.trace.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class DailyMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Mission mission;

    private LocalDate createdAt;

    @Column(nullable = false)
    private int changeCount;

    @Column(name = "is_verified")
    private boolean isVerified;

    @Column(name = "post_id")
    private Long postId;

    public void changeMission(Mission newMission) {
        this.mission = newMission;
        this.changeCount++;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now(ZoneId.of("Asia/Seoul"));
    }

    public void updateVerification(boolean isVerified, Long postId) {
        this.isVerified = isVerified;
        if (isVerified) {
            this.postId = postId;
        }
    }


}
