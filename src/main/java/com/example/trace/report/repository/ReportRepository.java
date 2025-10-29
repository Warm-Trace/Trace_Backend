package com.example.trace.report.repository;

import com.example.trace.post.domain.Comment;
import com.example.trace.post.domain.Post;
import com.example.trace.report.domain.Report;
import com.example.trace.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByReporterAndPost(User reporter, Post post);

    Optional<Report> findByReporterAndComment(User reporter, Comment comment);
}
