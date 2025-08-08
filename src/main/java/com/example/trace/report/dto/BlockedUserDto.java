package com.example.trace.report.dto;

import com.example.trace.report.domain.UserBlock;
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
@Schema(description = "차단한 사용자 정보")
public class BlockedUserDto {

    @Schema(description = "사용자 providerId")
    private String providerId;

    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "프로필 이미지 URL")
    private String profileImageUrl;

    @Schema(description = "차단한 날짜")
    private LocalDateTime blockedAt;

    public static BlockedUserDto fromEntity(UserBlock userBlock) {
        return BlockedUserDto.builder()
                .providerId(userBlock.getBlocked().getProviderId())
                .nickname(userBlock.getBlocked().getNickname())
                .profileImageUrl(userBlock.getBlocked().getProfileImageUrl())
                .blockedAt(userBlock.getCreatedAt())
                .build();
    }
}