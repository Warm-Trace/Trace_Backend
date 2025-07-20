package com.example.trace.global.fcm;

import com.example.trace.global.fcm.domain.NotificationEvent;
import com.example.trace.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationEventRepository extends JpaRepository<NotificationEvent, Long> {
    List<NotificationEvent> findAllByRefIdAndUser(Long refId, User user);
}
