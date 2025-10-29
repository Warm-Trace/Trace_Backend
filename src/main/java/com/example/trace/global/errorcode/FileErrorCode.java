package com.example.trace.global.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {
    UNSUPPORTED_MEDIA_FORMAT(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "허용되지 않는 파일 형식"),
    FILE_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "파일 크기가 너무 큽니다."),
    FILE_DELETE_FAILED(HttpStatus.FORBIDDEN, "삭제 권한이 없어 파일을 삭제할 수 없습니다.");
    private final HttpStatus httpStatus;
    private final String message;
}
