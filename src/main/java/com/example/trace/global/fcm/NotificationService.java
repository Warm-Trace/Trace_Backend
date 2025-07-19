package com.example.trace.global.fcm;

import static com.example.trace.global.errorcode.UserErrorCode.USER_NOT_FOUND;

import com.example.trace.auth.repository.UserRepository;
import com.example.trace.global.exception.UserException;
import com.example.trace.user.User;
import com.example.trace.user.dto.NotificationResponse;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;

    public List<NotificationResponse> getAllNotifications(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // 내림차순(최신순)으로 반환
        return user.getNotificationEvents().stream()
                .sorted(Comparator.reverseOrder())
                .map(NotificationResponse::fromEntity)
                .toList();
    }
}
