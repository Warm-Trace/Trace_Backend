package com.example.trace.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "페이징 알림 요청 DTO")
public class NotificationCursorRequest {

    @Schema(description = "커서 ID(첫 요청일 시, null)", example = "null")
    private Long cursorId;

    @Builder.Default
    @Schema(description = "페이지 크기", example = "10")
    private Integer size = 10;

    @Schema(description = "커서 날짜 및 시간(첫 요청일 시, null)", example = "null")
    private LocalDateTime cursorDateTime;
}
