package com.example.trace.post.domain;

import com.example.trace.global.errorcode.PostErrorCode;
import com.example.trace.global.exception.PostException;
import com.example.trace.gpt.domain.Verification;
import com.example.trace.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
// TODO(seobeeeee): 연관관계 정리
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private PostType postType;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", length = 800)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> commentList;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Verification verification;

    @Column(name = "mission_content")
    private String missionContent;

    @Transient
    private boolean contentModified = false;

    @Builder
    public Post(PostType postType,
                Long viewCount,
                String title,
                String content,
                User user,
                Verification verification,
                String missionContent) {

        if (content == null || content.length() > 800) {
            throw new PostException(PostErrorCode.CONTENT_TOO_LONG);
        }

        this.postType = postType;
        this.viewCount = viewCount;
        this.title = title;
        this.content = content;
        this.user = user;
        this.verification = verification;
        this.missionContent = missionContent;
        this.images = new ArrayList<>();
        this.commentList = new ArrayList<>();
    }

    public void addImage(PostImage image) {
        this.images.add(image);
        image.setPost(this);
    }

    public void addComment(Comment comment) {
        this.commentList.add(comment);
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void editPost(String title, String content, List<PostImage> images) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }

        if (content != null && !content.isBlank()) {
            if (content.length() > 800) {
                throw new PostException(PostErrorCode.CONTENT_TOO_LONG);
            }
            this.content = content;
        }

        if (images != null && !images.isEmpty()) {
            images.forEach(this::addImage); // 추가할 이미지를 기존 이미지 리스트에 추가하면서 연관관계 매핑
            for (int i = 0; i < this.images.size(); i++) {
                this.images.get(i).setOrder(i); // 모든 order 재정렬
            }
        }
        this.contentModified = true;
    }

    public boolean isOwner(String providerId) {
        return this.user.getProviderId().equals(providerId);
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        updatedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    @PreUpdate
    protected void onUpdate() {
        if (contentModified) {
            updatedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
            contentModified = false; // 플래그 리셋
        }
    }
}