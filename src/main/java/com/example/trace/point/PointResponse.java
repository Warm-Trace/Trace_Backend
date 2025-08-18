package com.example.trace.point;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "획득 포인트 목록 응답")
public class PointResponse {
    @Schema(description = "포인트 id", example = "1")
    private Long id;
    @Schema(description = "획득한 포인트 양", example = "50")
    private Integer amount;
    @Schema(description = "포인트를 획득한 시간", example = "2025-08-08T11:12:58.016Z")
    private LocalDateTime createdAt;
    @Schema(description = "포인트 획득 경로", example = "GOOD_DEED_POST")
    private PointSource source;
    @Schema(description = "관련 리소스 Id", example = "1")
    private Long postId;
    @Schema(description = "관련 리소스 내용", example = "할머니를 도와드렸다.")
    private String content;

    public static PointResponse fromEntity(Point point) {
        return PointResponse.builder()
                .id(point.getId())
                .amount(point.getAmount())
                .createdAt(point.getCreatedAt())
                .postId(point.getPost() != null ? point.getPost().getId() : null)
                .content(point.getContent())
                .source(point.getSource())
                .build();
    }
}
