package com.example.trace.bird;

import lombok.Getter;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Getter
public enum BirdLevel {
    EGG(0, "알", 0, 0),
    BABY_BIRD(1, "새끼", 1, 1),
    YOUNG_BIRD(2, "어린 새", 3, 3),
    ADOLESCENT_BIRD(3, "청년 새", 7, 5),
    ADULT_BIRD(4, "성조", 15, 10),
    LEADER_BIRD(5, "리더새", 30, 15),
    LEGENDARY_SWALLOW(6, "전설의 제비", 60, 25);

    private final int level;
    private final String name;
    private final int requiredGoodDeedCount;
    private final int requiredMissionCount;

    BirdLevel(int level, String name, int requiredGoodDeedCount, int requiredMissionCount) {
        this.level = level;
        this.name = name;
        this.requiredGoodDeedCount = requiredGoodDeedCount;
        this.requiredMissionCount = requiredMissionCount;
    }

    public static BirdLevel fromLevel(int level) {
        if (level < 0 || level >= values().length) {
            throw new IllegalArgumentException("유효하지 않은 레벨: " + level);
        }
        return Arrays.stream(values())
                .filter(birdLevel -> birdLevel.level == level)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("레벨 " + level + "인 제비가 존재하지 않습니다."));
    }
}
