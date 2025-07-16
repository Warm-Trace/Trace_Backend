package com.example.trace.global.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReportErrorCode implements ErrorCode {
    INVALID_TARGET(HttpStatus.BAD_REQUEST, "잘못된 신고 대상입니다. postId 또는 commentId를 제공해주세요."),
    CANNOT_REPORT_YOUR_OWN_CONTENT(HttpStatus.FORBIDDEN, "자신의 콘텐츠는 신고할 수 없습니다."),
    ALREADY_REPORTED(HttpStatus.CONFLICT, "이미 신고한 콘텐츠입니다."),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "신고를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "이미 처리된 신고입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}