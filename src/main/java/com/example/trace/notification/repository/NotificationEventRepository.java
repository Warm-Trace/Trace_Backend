package com.example.trace.notification.repository;

import com.example.trace.notification.domain.NotificationEvent;
import com.example.trace.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationEventRepository extends JpaRepository<NotificationEvent, Long> {
    List<NotificationEvent> findAllByRefIdAndUser(Long refId, User user);
}
