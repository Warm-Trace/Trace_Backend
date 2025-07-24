package com.example.trace.global.fcm;

import com.example.trace.global.fcm.domain.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationEventRepository extends JpaRepository<NotificationEvent, Long> {
}
