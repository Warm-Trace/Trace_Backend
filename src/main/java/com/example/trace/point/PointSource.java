package com.example.trace.point;

import com.example.trace.gpt.dto.VerificationDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointSource {

    GOOD_DEED_POST(50, "선행 인증"),       // 기본 50점
    MISSION_POST(150, "미션 인증"),       // 기본 150점
    ATTENDANCE(25, "출석 체크"),
    CONSECUTIVE_DAYS_BONUS(0, "연속 선행 일수 보너스");

    private static final int FULL_VERIFICATION_MULTIPLIER = 2;
    private final int basePoints;
    private final String description;

    public int calculatePointFor(VerificationDto verification) {
        int finalPoints = getBasePoints();

        if (verification.isTextResult() && verification.isImageResult()) {
            return finalPoints * FULL_VERIFICATION_MULTIPLIER;
        }
        return finalPoints;
    }
}
