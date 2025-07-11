package com.example.trace.report.controller;

import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.global.response.ApiResponse;
import com.example.trace.report.dto.ReportRequest;
import com.example.trace.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "신고 API", description = "신고 관련 API")
public class ReportController {
    private final ReportService reportService;

    @PostMapping(consumes = "application/json")
    @Operation(summary = "신고 생성", description = "사용자가 신고를 생성합니다.")
    public ResponseEntity<ApiResponse> createReport(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody ReportRequest request) {
        reportService.createReport(principalDetails.getUser().getId(), request);
        return ResponseEntity.ok(ApiResponse.success("신고가 성공적으로 접수되었습니다."));
    }
}
