package com.example.trace.user.dto;

import com.example.trace.user.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserVerificationInfo {
    private Long verifiedPostCount;
    private Long completedMissionCount;

    public UserVerificationInfo(Long verifiedPostCount, Long completedMissionCount) {
        this.verifiedPostCount = verifiedPostCount;
        this.completedMissionCount = completedMissionCount;
    }

    public static UserVerificationInfo from(User user) {
        return new UserVerificationInfo(
                user.getVerifiedPostCount(),
                user.getCompletedMissionCount()
        );
    }
}
