package com.example.trace.point;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointSource {

    // 선행 인증 게시글
    GOOD_DEED_POST_TEXT(50, "선행 인증 (텍스트)"),
    GOOD_DEED_POST_IMAGE(50, "선행 인증 (이미지)"),

    // 미션 인증 게시글
    MISSION_POST_TEXT(150, "미션 인증 (텍스트)"),
    MISSION_POST_IMAGE(150, "미션 인증 (이미지)"),

    // 출석
    ATTENDANCE(25, "출석 체크"),

    // 보너스
    // 연속 선행 보너스는 일수에 따라 포인트가 달라지므로, 기본 포인트는 0으로 설정합니다.
    // 실제 포인트 지급은 서비스 로직에서 계산하여 부여합니다.
    CONSECUTIVE_DAYS_BONUS(0, "연속 선행 일수 보너스");

    private final int points;
    private final String description;
}
