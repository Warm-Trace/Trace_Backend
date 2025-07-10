package com.example.trace.report.domain;

import com.example.trace.post.domain.Comment;
import com.example.trace.post.domain.Post;
import com.example.trace.report.ReportReason;
import com.example.trace.report.ReportStatus;
import com.example.trace.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신고자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    // 신고된 게시글 (댓글 신고의 경우 null)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    // 신고된 댓글 (게시글 신고의 경우 null)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus reportStatus;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Report(User reporter, Post post, Comment comment, ReportReason reason) {
        this.reporter = reporter;
        this.post = post;
        this.comment = comment;
        this.reason = reason;
        this.reportStatus = ReportStatus.PENDING;
    }

    public void approve() {
        this.reportStatus = ReportStatus.APPROVED;
    }

    public void reject() {
        this.reportStatus = ReportStatus.REJECTED;
    }

}
