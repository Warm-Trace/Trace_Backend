package com.example.trace.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {

    PENDING("처리 대기"),
    APPROVED("승인"),
    REJECTED("기각");

    private final String description;
}
