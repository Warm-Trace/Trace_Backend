package com.example.trace.notification.dto;

import com.example.trace.notification.domain.NotificationSetting;
import lombok.Getter;

@Getter
public class NotificationSettingResponse {
    private Long id;

    private Long userId;

    private boolean mission;

    private boolean comment;

    private boolean emotion;

    public NotificationSettingResponse() {
    }

    private NotificationSettingResponse(NotificationSetting setting) {
        this.id = setting.getId();
        this.userId = setting.getUser().getId();
        this.mission = setting.isMission();
        this.comment = setting.isComment();
        this.emotion = setting.isEmotion();
    }

    public static NotificationSettingResponse from(NotificationSetting setting) {
        return new NotificationSettingResponse(setting);
    }
}
