package com.example.trace.notification.repository;

import com.example.trace.notification.domain.NotificationSetting;
import com.example.trace.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    Optional<NotificationSetting> findByUser(User user);
}
