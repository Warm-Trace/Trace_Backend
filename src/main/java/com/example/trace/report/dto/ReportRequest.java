package com.example.trace.report.dto;

import com.example.trace.report.ReportReason;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequest {
    private Long postId;
    private Long commentId;
    private ReportReason reason;
}