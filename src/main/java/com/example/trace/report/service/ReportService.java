package com.example.trace.report.service;

import com.example.trace.auth.repository.UserRepository;
import com.example.trace.global.errorcode.ReportErrorCode;
import com.example.trace.global.errorcode.UserErrorCode;
import com.example.trace.global.exception.ReportException;
import com.example.trace.global.exception.UserException;
import com.example.trace.post.domain.Comment;
import com.example.trace.post.domain.Post;
import com.example.trace.post.repository.CommentRepository;
import com.example.trace.post.repository.PostRepository;
import com.example.trace.report.domain.Report;
import com.example.trace.report.dto.ReportRequest;
import com.example.trace.report.repository.ReportRepository;
import com.example.trace.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void createReport(String providerId, ReportRequest request) {
        User reporter = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (request.getPostId() != null && request.getCommentId() == null) {
            reportPost(reporter, request);
        } else if (request.getPostId() == null && request.getCommentId() != null) {
            reportComment(reporter, request);
        } else {
            throw new ReportException(ReportErrorCode.INVALID_TARGET);
        }
    }

    private void reportPost(User reporter, ReportRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ReportException(ReportErrorCode.POST_NOT_FOUND));

        if (post.getUser().getProviderId().equals(reporter.getProviderId())) {
            throw new ReportException(ReportErrorCode.CANNOT_REPORT_YOUR_OWN_CONTENT);
        }

        reportRepository.findByReporterAndPost(reporter, post).ifPresent(r -> {
            throw new ReportException(ReportErrorCode.ALREADY_REPORTED);
        });

        Report report = Report.builder()
                .reporter(reporter)
                .post(post)
                .reason(request.getReason())
                .build();
        reportRepository.save(report);
    }

    private void reportComment(User reporter, ReportRequest request) {
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new ReportException(ReportErrorCode.COMMENT_NOT_FOUND));

        if (comment.getUser().getProviderId().equals(reporter.getProviderId())) {
            throw new ReportException(ReportErrorCode.CANNOT_REPORT_YOUR_OWN_CONTENT);
        }

        reportRepository.findByReporterAndComment(reporter, comment).ifPresent(r -> {
            throw new ReportException(ReportErrorCode.ALREADY_REPORTED);
        });

        Report report = Report.builder()
                .reporter(reporter)
                .comment(comment)
                .reason(request.getReason())
                .build();
        reportRepository.save(report);
    }
}
