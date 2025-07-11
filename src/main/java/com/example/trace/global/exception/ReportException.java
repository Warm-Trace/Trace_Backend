package com.example.trace.global.exception;

import com.example.trace.global.errorcode.ReportErrorCode;
import lombok.Getter;


@Getter
public class ReportException extends RuntimeException {
    private final ReportErrorCode reportErrorCode;
    public ReportException(ReportErrorCode reportErrorCode) {
        super(reportErrorCode.getMessage());
        this.reportErrorCode = reportErrorCode;
    }
}

