package com.example.trace.notification.dto;

import com.example.trace.notification.domain.NotificationEvent;
import com.example.trace.notification.domain.NotificationEvent.NotificationData;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "알림 메시지 정보")
public class NotificationResponse {
    //TODO: swagger 정보 추가?

    private UUID id;

    private LocalDateTime createdAt;

    private Boolean isRead;

    private String title;

    private String body;

    private NotificationData data;

    public static NotificationResponse fromEntity(NotificationEvent event) {
        return switch (event.getType()) {
            case DATA -> NotificationResponse.builder()
                    .id(event.getId())
                    .isRead(event.getIsRead())
                    .createdAt(event.getCreatedAt())
                    .data(event.getData())
                    .build();

            case NOTIFICATION -> NotificationResponse.builder()
                    .id(event.getId())
                    .isRead(event.getIsRead())
                    .createdAt(event.getCreatedAt())
                    .title(event.getTitle())
                    .body(event.getBody())
                    .build();
        };
    }
}
