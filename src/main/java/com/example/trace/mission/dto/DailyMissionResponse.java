package com.example.trace.mission.dto;

import com.example.trace.mission.mission.DailyMission;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Schema(description = "미션 응답")
public class DailyMissionResponse {
    @Schema(name = "미션 내용", example = "대중교통에서 자리 양보하기")
    private String content;
    @Schema(name = "할당 받은 미션 변경 횟수", example = "4")
    private int changeCount;

    @Schema(name = "미션 인증 여부", example = "false")
    @JsonProperty("isVerified")
    private boolean isVerified;

    public static DailyMissionResponse fromEntity(DailyMission dailyMission) {
        return DailyMissionResponse.builder()
                .content(dailyMission.getMission().getDescription())
                .changeCount(dailyMission.getChangeCount())
                .isVerified(dailyMission.isVerified())
                .build();
    }

}
