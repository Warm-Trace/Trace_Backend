package com.example.trace.report.service;

import com.example.trace.global.errorcode.ReportErrorCode;
import com.example.trace.global.exception.ReportException;
import com.example.trace.post.repository.CommentRepository;
import com.example.trace.post.repository.PostRepository;
import com.example.trace.report.ReportStatus;
import com.example.trace.report.domain.Report;
import com.example.trace.report.dto.AdminReportResponse;
import com.example.trace.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public List<AdminReportResponse> getReports() {
        return reportRepository.findAll()
                .stream()
                .map(AdminReportResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveReport(Long reportId) {
        Report report = findReportById(reportId);

        if (report.getPost() != null) {
            postRepository.delete(report.getPost());
        } else if (report.getComment() != null) {
            commentRepository.delete(report.getComment());
        }

        report.approve();
    }

    @Transactional
    public void rejectReport(Long reportId) {
        Report report = findReportById(reportId);
        report.reject();
    }

    private Report findReportById(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportException(ReportErrorCode.REPORT_NOT_FOUND));

        if (report.getReportStatus() != ReportStatus.PENDING) {
            throw new ReportException(ReportErrorCode.ALREADY_PROCESSED);
        }
        return report;
    }
}
