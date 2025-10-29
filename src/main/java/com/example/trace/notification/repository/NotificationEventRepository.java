package com.example.trace.notification.repository;

import com.example.trace.notification.domain.NotificationEvent;
import com.example.trace.notification.domain.SourceType;
import com.example.trace.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationEventRepository extends JpaRepository<NotificationEvent, UUID> {
    List<NotificationEvent> findAllByRefIdAndUser(Long refId, User user);

    @org.springframework.data.jpa.repository.QueryHints(
            value = @jakarta.persistence.QueryHint(
                    name = "org.hibernate.comment",
                    value = "모든 알림 가져오기(first)"
            )
    )
    @Query("SELECT n FROM NotificationEvent n WHERE n.user = :user ORDER BY n.createdAt DESC, n.id DESC")
    List<NotificationEvent> findFirstPage(@Param("user") User user, Pageable pageable);

    @org.springframework.data.jpa.repository.QueryHints(
            value = @jakarta.persistence.QueryHint(
                    name = "org.hibernate.comment",
                    value = "모든 알림 가져오기(next)"
            )
    )
    @Query("""
                SELECT n FROM NotificationEvent n
                WHERE n.user = :user AND (
                    n.createdAt < :cursorDateTime OR
                    (n.createdAt = :cursorDateTime AND n.id < :cursorId)
                )
                ORDER BY n.createdAt DESC, n.id DESC
            """)
    List<NotificationEvent> findNextPage(
            @Param("user") User user,
            @Param("cursorDateTime") LocalDateTime cursorDateTime,
            @Param("cursorId") UUID cursorId,
            Pageable pageable
    );

    @org.springframework.data.jpa.repository.QueryHints(
            value = @jakarta.persistence.QueryHint(
                    name = "org.hibernate.comment",
                    value = "송신 알림 개수 체크: userId+refId+sourceType"
            )
    )
    @Query(value = """
                    SELECT COUNT(*) FROM NotificationEvent n
                    WHERE n.user.id = :user_id AND n.refId = :ref_id AND n.sourceType = :source_type
            """)
    long countByUserIdAndRefIdAndSourceType(@Param("user_id") Long userId, @Param("ref_id") Long postId,
                                            @Param("source_type") SourceType sourceType);
}
