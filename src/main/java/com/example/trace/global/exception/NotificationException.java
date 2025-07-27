package com.example.trace.global.exception;

import com.example.trace.global.errorcode.NotificationErrorCode;
import lombok.Getter;

@Getter
public class NotificationException extends RuntimeException {
    private final NotificationErrorCode notificationErrorCode;

    public NotificationException(NotificationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.notificationErrorCode = errorCode;
    }
}
