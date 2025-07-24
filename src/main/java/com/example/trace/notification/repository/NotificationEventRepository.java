package com.example.trace.notification.repository;

import com.example.trace.notification.domain.NotificationEvent;
import com.example.trace.user.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationEventRepository extends JpaRepository<NotificationEvent, Long> {
    List<NotificationEvent> findAllByRefIdAndUser(Long refId, User user);

    List<NotificationEvent> findFirstPage(User user, Pageable pageable);

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
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
