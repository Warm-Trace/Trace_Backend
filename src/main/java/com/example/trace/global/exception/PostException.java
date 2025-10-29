package com.example.trace.global.exception;

import com.example.trace.global.errorcode.PostErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PostException extends RuntimeException {
    private final PostErrorCode postErrorCode;

    public PostException(PostErrorCode postErrorCode) {
        super(postErrorCode.getMessage());
        this.postErrorCode = postErrorCode;
    }
}

