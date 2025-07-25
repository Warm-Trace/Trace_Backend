package com.example.trace.notification.service;

import static com.example.trace.global.errorcode.UserErrorCode.USER_NOT_FOUND;

import com.example.trace.auth.repository.UserRepository;
import com.example.trace.global.exception.UserException;
import com.example.trace.global.response.CursorResponse;
import com.example.trace.global.response.CursorResponse.CursorMeta;
import com.example.trace.notification.domain.NotificationEvent;
import com.example.trace.notification.dto.NotificationCursorRequest;
import com.example.trace.notification.dto.NotificationResponse;
import com.example.trace.notification.repository.NotificationEventRepository;
import com.example.trace.user.User;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationEventRepository notificationEventRepository;

    @Transactional(readOnly = true)
    public CursorResponse<NotificationResponse> getNotifications(NotificationCursorRequest request, User user) {
        int pageSize = request.getSize();
        List<NotificationEvent> notifications;

        if (request.getCursorDateTime() == null || request.getCursorId() == null) {
            // 첫 번째 요청인 경우
            notifications = notificationEventRepository.findFirstPage(user,
                    PageRequest.of(0, pageSize, Sort.by("createdAt").descending().and(Sort.by("id").descending())));
        } else {
            // 두 번째 이후 요청
            notifications = notificationEventRepository.findNextPage(user, request.getCursorDateTime(),
                    request.getCursorId(),
                    PageRequest.of(0, pageSize, Sort.by("createdAt").descending().and(Sort.by("id").descending())));
        }

        List<NotificationResponse> results = notifications.stream().map(NotificationResponse::fromEntity).toList();

        boolean hasNext = results.size() == pageSize;
        NotificationResponse last = results.get(results.size() - 1);
        CursorResponse.CursorMeta nextCursor = getNextCursorFrom(last, hasNext);

        return new CursorResponse<>(results, hasNext, nextCursor);
    }

    @Transactional
    public NotificationEvent read(Long id, String userProviderId) {
        NotificationEvent notificationEvent = notificationEventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));
        User user = userRepository.findByProviderId(userProviderId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        Long refId = notificationEvent.getRefId();
        List<NotificationEvent> referred = notificationEventRepository.findAllByRefIdAndUser(refId, user);

        for (NotificationEvent n : referred) {
            n.read();
        }

        return notificationEvent.read();
    }

    private CursorResponse.CursorMeta getNextCursorFrom(NotificationResponse last, boolean hasNext) {
        if (!hasNext) {
            return null;
        }

        LocalDateTime createdAt = Instant.ofEpochMilli(last.getCreatedAt())
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();

        return CursorMeta.builder()
                .dateTime(createdAt)
                .id(last.getId())
                .build();
    }
}
