package com.example.trace.global.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TokenErrorCode implements ErrorCode {
    // JWT
    WRONG_SIGNATURE(HttpStatus.UNAUTHORIZED, false, false, "잘못된 서명입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, true, false, "만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.BAD_REQUEST, false, false, "지원되지 않는 JWT 토큰입니다."),
    ILLEGAL_ARGUMENT(HttpStatus.BAD_REQUEST, false, false, "JWT 토큰이 아닙니다."),
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.NOT_FOUND, false, true, "리프레시 토큰이 존재하지 않거나, 일치하지 않습니다. "),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, false, true, "사용자가 존재하지 않습니다"),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, false, true, "알 수 없는 오류입니다."),
    LOGOUT_TOKEN(HttpStatus.UNAUTHORIZED, true, false, "로그아웃된 토큰입니다."),
    DELETED_USER(HttpStatus.UNAUTHORIZED, true, false, "회원탈퇴한 사용자입니다."),

    //FCM
    NOT_FOUND_FCM_TOKEN(HttpStatus.NOT_FOUND, false, true, "FCM 토큰을 찾을 수 없습니다"),
    ;

    private final HttpStatus httpStatus;
    private final boolean isExpired;
    private final boolean isValid;
    private final String message;
}
