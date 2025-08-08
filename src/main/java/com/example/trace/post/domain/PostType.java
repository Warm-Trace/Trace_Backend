package com.example.trace.post.domain;

import com.example.trace.gpt.dto.VerificationDto;
import lombok.Getter;

@Getter
public enum PostType {
    FREE("자유", 0, 0),
    GOOD_DEED("선행", 50, 50),
    MISSION("미션", 150, 150);

    private final String type;
    private final Integer textScore;
    private final Integer imageScore;

    PostType(String type, Integer textScore, Integer imageScore) {
        this.type = type;
        this.textScore = textScore;
        this.imageScore = imageScore;
    }

    public Integer getTotalScore(VerificationDto verificationDto) {
        int score = 0;

        if (verificationDto.isTextResult()) {
            score += getTextScore();
        }
        if (verificationDto.isImageResult()) {
            score += getImageScore();
        }

        return score;
    }
}
