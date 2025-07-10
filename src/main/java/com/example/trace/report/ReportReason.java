package com.example.trace.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportReason {

    // 신고 사유
    INSULT("욕설/비방/인신공격"),
    ILLEGAL("불법/범죄/불건전한 내용"),
    LEWD("음란/선정/불쾌한 내용"),
    SCAM("사기/허위정보/유출"),
    COMMERCIAL("상업적 광고 및 판매"),
    IRRELEVANT("게시판 성격에 부적절함"),
    UNPLEASANT("불쾌감을 주는 사용자");

    private final String description;
}
