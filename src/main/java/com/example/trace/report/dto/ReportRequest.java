package com.example.trace.report.dto;

import com.example.trace.report.ReportReason;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "신고 요청 DTO")
public class ReportRequest {
    @Schema(description = "신고 대상 게시글 ID", example = "12345")
    private Long postId;
    @Schema(description = "신고 대상 댓글 ID", example = "67890")
    private Long commentId;
    @Schema(description = "신고 사유", example = "INSULT")
    private ReportReason reason;
}