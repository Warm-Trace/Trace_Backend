package com.example.trace.global.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReportErrorCode implements ErrorCode {
    INVALID_TARGET(HttpStatus.BAD_REQUEST, "Invalid report target. Provide postId or commentId."),
    CANNOT_REPORT_YOUR_OWN_CONTENT(HttpStatus.FORBIDDEN, "You cannot report your own content."),
    ALREADY_REPORTED(HttpStatus.CONFLICT, "You have already reported this content."),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "Report not found."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Post not found."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Comment not found."),
    ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "This report has already been processed.");

    private final HttpStatus httpStatus;
    private final String message;
}