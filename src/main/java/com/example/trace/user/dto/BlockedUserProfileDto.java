package com.example.trace.user.dto;

import com.example.trace.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "차단된 사용자 프로필 정보")
public class BlockedUserProfileDto {

    @Schema(description = "사용자 providerId")
    private String providerId;

    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "프로필 이미지 URL")
    private String profileImageUrl;

    @Schema(description = "차단한 날짜")
    private LocalDateTime blockedAt;

    public static BlockedUserProfileDto fromEntity(User user, LocalDateTime blockedAt) {
        return BlockedUserProfileDto.builder()
                .providerId(user.getProviderId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .blockedAt(blockedAt)
                .build();
    }
}