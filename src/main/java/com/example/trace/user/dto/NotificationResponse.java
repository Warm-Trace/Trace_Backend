package com.example.trace.user.dto;

import com.example.trace.global.fcm.domain.NotificationEvent;
import io.swagger.v3.oas.annotations.media.Schema;
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

    private String title;

    private String body;

    private Object data;

    public static NotificationResponse fromEntity(NotificationEvent event) {
        return switch (event.getType()) {
            case DATA -> NotificationResponse.builder()
                    .data(event.getData())
                    .build();

            case NOTIFICATION -> NotificationResponse.builder()
                    .title(event.getTitle())
                    .body(event.getBody())
                    .build();
        };
    }
}
