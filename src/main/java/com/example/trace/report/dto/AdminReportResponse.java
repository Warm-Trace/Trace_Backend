package com.example.trace.report.dto;

import com.example.trace.report.ReportReason;
import com.example.trace.report.ReportStatus;
import com.example.trace.report.domain.Report;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminReportResponse {
    private final Long reportId;
    private final String reporterName;
    private final Long reporterId;
    private final Long contentId;
    private final String contentType;
    private final ReportReason reason;
    private final ReportStatus status;
    private final LocalDateTime createdAt;

    public static AdminReportResponse from(Report report) {
        Long contentId = report.getPost() != null ? report.getPost().getId() : report.getComment().getId();
        String contentType = report.getPost() != null ? "POST" : "COMMENT";

        return AdminReportResponse.builder()
                .reportId(report.getId())
                .reporterName(report.getReporter().getNickname())
                .reportId(report.getReporter().getId())
                .contentId(contentId)
                .contentType(contentType)
                .reason(report.getReason())
                .status(report.getReportStatus())
                .createdAt(report.getCreatedAt())
                .build();
    }
}