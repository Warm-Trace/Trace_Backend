package com.example.trace.notification.domain;

import com.example.trace.global.errorcode.NotificationErrorCode;
import com.example.trace.global.exception.NotificationException;

public enum SourceType {
    POST, COMMENT, MISSION, EMOTION;

    public static SourceType fromString(String value) {
        if (value == null) {
            return null;
        }

        try {
            return SourceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotificationException(NotificationErrorCode.UNKNOWN_NOTIFICATION_SOURCE_TYPE);
        }
    }
}