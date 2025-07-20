package com.example.trace.global.fcm;

import static com.example.trace.global.errorcode.UserErrorCode.USER_NOT_FOUND;

import com.example.trace.auth.repository.UserRepository;
import com.example.trace.global.exception.UserException;
import com.example.trace.global.fcm.domain.NotificationEvent;
import com.example.trace.user.User;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationEventRepository notificationEventRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // 내림차순(최신순)으로 반환
        return user.getNotificationEvents().stream()
                .sorted(Comparator.reverseOrder())
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    @Transactional
    public NotificationEvent read(Long id) {
        NotificationEvent notificationEvent = notificationEventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));

        //TODO: ref id로 다른 알림 가져온 후 read
        return notificationEvent.read();
    }
}
