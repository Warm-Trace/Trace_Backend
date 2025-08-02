package com.example.trace.notification.repository;

import com.example.trace.notification.domain.NotificationEvent;
import com.example.trace.user.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationEventRepository extends JpaRepository<NotificationEvent, Long> {
    List<NotificationEvent> findAllByRefIdAndUser(Long refId, User user);

    @Query("SELECT n FROM NotificationEvent n WHERE n.user = :user ORDER BY n.createdAt DESC, n.id DESC")
    List<NotificationEvent> findFirstPage(@Param("user") User user, Pageable pageable);

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
}
