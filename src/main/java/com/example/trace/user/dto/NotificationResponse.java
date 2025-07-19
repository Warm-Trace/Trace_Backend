package com.example.trace.user.dto;

import com.example.trace.global.fcm.domain.NotificationEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "알림 메시지 정보")
public class NotificationResponse {

    private String title;

    private String body;

    private Object data;

    public static NotificationResponse fromEntity(NotificationEvent event) {
        return switch (event.getType()) {
            case NOTIFICATION -> NotificationResponse.builder()
                    .data(event.getData())
                    .build();

            case DATA -> NotificationResponse.builder()
                    .title(event.getTitle())
                    .body(event.getBody())
                    .build();
        };
    }
}
