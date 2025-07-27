package com.example.trace.global.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 알림입니다."),
    NOTIFICATION_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "알림을 삭제할 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
