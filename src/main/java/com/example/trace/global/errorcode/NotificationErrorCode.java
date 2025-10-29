package com.example.trace.global.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 알림입니다."),
    NOTIFICATION_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "알림을 삭제할 권한이 없습니다."),
    TIMESTAMP_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "알림 데이터에 타임스탬프가 존재하지 않습니다."),
    IDENTIFIER_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "알림 데이터에 고유 식별자가 존재하지 않습니다."),
    DATA_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "NotificationData 직렬화에 실패하였습니다."),
    DATA_DESERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "NotificationData 역직렬화에 실패하였습니다."),
    UNKNOWN_NOTIFICATION_SOURCE_TYPE(HttpStatus.BAD_REQUEST, "존재하지 않는 알림 타입입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
