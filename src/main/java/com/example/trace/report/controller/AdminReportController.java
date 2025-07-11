package com.example.trace.report.controller;

import com.example.trace.global.response.ApiResponse;
import com.example.trace.report.dto.AdminReportResponse;
import com.example.trace.report.service.AdminReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN')") // ADMIN 권한이 있는 사용자만 접근 가능
@Tag(name = "Admin Report Management", description = "관리자 신고 관리 API")
public class AdminReportController {

    private final AdminReportService adminReportService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminReportResponse>>> getReports() {
        List<AdminReportResponse> reports = adminReportService.getReports();
        return ResponseEntity.ok(ApiResponse.success("신고 목록 조회가 완료되었습니다.", reports));
    }

    @PostMapping("/{reportId}/approve")
    public ResponseEntity<ApiResponse> approveReport(@PathVariable Long reportId) {
        adminReportService.approveReport(reportId);
        return ResponseEntity.ok(ApiResponse.success("신고를 승인하고 해당 콘텐츠를 삭제했습니다."));
    }

    @PostMapping("/{reportId}/reject")
    public ResponseEntity<ApiResponse> rejectReport(@PathVariable Long reportId) {
        adminReportService.rejectReport(reportId);
        return ResponseEntity.ok(ApiResponse.success("신고를 기각 처리했습니다."));
    }
}