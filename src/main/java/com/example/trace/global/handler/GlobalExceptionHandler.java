package com.example.trace.global.handler;

import com.example.trace.global.errorcode.AuthErrorCode;
import com.example.trace.global.errorcode.ErrorCode;
import com.example.trace.global.errorcode.FileErrorCode;
import com.example.trace.global.errorcode.GptErrorCode;
import com.example.trace.global.errorcode.MissionErrorCode;
import com.example.trace.global.errorcode.NotificationErrorCode;
import com.example.trace.global.errorcode.PostErrorCode;
import com.example.trace.global.errorcode.ReportErrorCode;
import com.example.trace.global.errorcode.SignUpErrorCode;
import com.example.trace.global.errorcode.TokenErrorCode;
import com.example.trace.global.exception.AuthException;
import com.example.trace.global.exception.FileException;
import com.example.trace.global.exception.GptException;
import com.example.trace.global.exception.MissionException;
import com.example.trace.global.exception.NotificationException;
import com.example.trace.global.exception.PostException;
import com.example.trace.global.exception.ReportException;
import com.example.trace.global.exception.SignUpException;
import com.example.trace.global.exception.TokenException;
import com.example.trace.global.response.ErrorResponse;
import com.example.trace.global.response.GptErrorResponse;
import com.example.trace.global.response.TokenErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ErrorResponse> handleNotificationException(NotificationException e) {
        NotificationErrorCode notificationErrorCode = e.getNotificationErrorCode();
        return handleExceptionInternal(notificationErrorCode);
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<TokenErrorResponse> handleTokenException(TokenException e) {
        TokenErrorCode tokenErrorCode = e.getTokenErrorCode();
        return handleExceptionInternal(tokenErrorCode);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException e) {
        AuthErrorCode authErrorCode = e.getAuthErrorCode();
        return handleExceptionInternal(authErrorCode);
    }

    @ExceptionHandler(MissionException.class)
    public ResponseEntity<ErrorResponse> handleMissionException(MissionException e) {
        MissionErrorCode missionErrorCode = e.getMissionErrorCode();
        return handleExceptionInternal(missionErrorCode);
    }


    @ExceptionHandler(SignUpException.class)
    public ResponseEntity<ErrorResponse> handleSignUpException(SignUpException e) {
        SignUpErrorCode signUpErrorCode = e.getSignUpErrorCode();
        return handleExceptionInternal(signUpErrorCode);
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(FileException e) {
        FileErrorCode fileErrorCode = e.getFileErrorCode();
        return handleExceptionInternal(fileErrorCode);
    }

    @ExceptionHandler(PostException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(PostException e) {
        PostErrorCode postErrorCode = e.getPostErrorCode();
        return handleExceptionInternal(postErrorCode);
    }

    @ExceptionHandler(ReportException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(ReportException e) {
        ReportErrorCode reportErrorCode = e.getReportErrorCode();
        return handleExceptionInternal(reportErrorCode);
    }

    @ExceptionHandler(GptException.class)
    public ResponseEntity<GptErrorResponse> handleGptException(GptException e) {
        GptErrorCode gptErrorCode = e.getGptErrorCode();
        String failureReason = e.getFailureReason();
        return handleExceptionInternal(gptErrorCode, failureReason);
    }

    private ResponseEntity<ErrorResponse> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeErrorResponse(errorCode));
    }

    private ResponseEntity<TokenErrorResponse> handleExceptionInternal(TokenErrorCode tokenErrorCode) {
        return ResponseEntity.status(tokenErrorCode.getHttpStatus())
                .body(makeTokenErrorResponse(tokenErrorCode));
    }

    private ResponseEntity<GptErrorResponse> handleExceptionInternal(GptErrorCode gptErrorCode, String failureReason) {
        return ResponseEntity.status(gptErrorCode.getHttpStatus())
                .body(makeGptErrorResponse(gptErrorCode, failureReason));
    }

    private TokenErrorResponse makeTokenErrorResponse(TokenErrorCode tokenErrorCode) {
        return TokenErrorResponse.builder()
                .code(tokenErrorCode.name())
                .message(tokenErrorCode.getMessage())
                .isExpired(tokenErrorCode.isExpired())
                .isValid(tokenErrorCode.isValid())
                .build();
    }

    private GptErrorResponse makeGptErrorResponse(GptErrorCode gptErrorCode, String failureReason) {
        return GptErrorResponse.builder()
                .code(gptErrorCode.name())
                .message(gptErrorCode.getMessage())
                .failureReason(failureReason)
                .build();
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
    }
}
