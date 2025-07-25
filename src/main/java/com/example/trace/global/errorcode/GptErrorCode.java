package com.example.trace.global.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GptErrorCode implements ErrorCode {
    GPT_LOGIC_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GPT의 논리적인 오류입니다. 텍스트가 선행이 아니라면, 이미지는 인증될 수 없습니다."),
    WRONG_CONTENT(HttpStatus.BAD_REQUEST, "게시글 내용이 선행과 관련이 없습니다."),
    DAILY_VERIFICATION_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "오늘 인증 횟수를 초과했습니다. 내일 다시 시도해주세요."),
    ;
    private final HttpStatus httpStatus;
    private final String message;
}
