package com.example.trace.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorNotificationResponse<T> {
    private List<T> content;
    private boolean hasNext;
    private CursorMeta cursor;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "UUID 기반 커서 메타 정보")
    public static class CursorMeta {
        @Schema(description = "커서 날짜 및 시간", example = "2025-05-22T08:26:36.025Z")
        private LocalDateTime dateTime;

        @Schema(description = "커서 UUID", example = "a1b2c3d4-e5f6-7a89-b0c1-d2e3f4a5b6c7")
        private UUID id;
    }
}