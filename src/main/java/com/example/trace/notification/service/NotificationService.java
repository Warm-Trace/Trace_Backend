package com.example.trace.notification.service;

import static com.example.trace.global.errorcode.NotificationErrorCode.NOTIFICATION_DELETE_FORBIDDEN;
import static com.example.trace.global.errorcode.NotificationErrorCode.NOTIFICATION_NOT_FOUND;
import static com.example.trace.global.errorcode.UserErrorCode.USER_NOT_FOUND;

import com.example.trace.global.exception.NotificationException;
import com.example.trace.global.exception.UserException;
import com.example.trace.notification.domain.NotificationEvent;
import com.example.trace.notification.dto.CursorNotificationResponse;
import com.example.trace.notification.dto.CursorNotificationResponse.CursorMeta;
import com.example.trace.notification.dto.NotificationResponse;
import com.example.trace.notification.repository.NotificationEventRepository;
import com.example.trace.user.domain.User;
import com.example.trace.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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
    public CursorNotificationResponse<NotificationResponse> getNotifications(
            Integer pageSize,
            UUID cursorId,
            LocalDateTime cursorDateTime,
            User user) {
        List<NotificationEvent> notifications;

        if (cursorDateTime == null || cursorId == null) {
            // 첫 번째 요청인 경우
            notifications = notificationEventRepository.findFirstPage(user,
                    PageRequest.of(0, pageSize, Sort.by("createdAt").descending().and(Sort.by("id").descending())));
        } else {
            // 두 번째 이후 요청
            notifications = notificationEventRepository.findNextPage(user, cursorDateTime,
                    cursorId,
                    PageRequest.of(0, pageSize, Sort.by("createdAt").descending().and(Sort.by("id").descending())));
        }

        List<NotificationResponse> results = notifications.stream().map(NotificationResponse::fromEntity).toList();

        boolean hasNext = results.size() == pageSize;
        NotificationResponse last = results.get(results.size() - 1);
        CursorNotificationResponse.CursorMeta nextCursor = getNextCursorFrom(last, hasNext);

        return new CursorNotificationResponse<>(results, hasNext, nextCursor);
    }

    @Transactional
    public NotificationEvent read(UUID id, String userProviderId) {
        NotificationEvent notificationEvent = notificationEventRepository.findById(id)
                .orElseThrow(() -> new NotificationException(NOTIFICATION_NOT_FOUND));
        User user = userRepository.findByProviderId(userProviderId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        Long refId = notificationEvent.getRefId();
        List<NotificationEvent> referred = notificationEventRepository.findAllByRefIdAndUser(refId, user);

        for (NotificationEvent n : referred) {
            n.read();
        }

        return notificationEvent.read();
    }

    @Transactional
    public void delete(UUID notificationId, String userProviderId) {
        NotificationEvent notification = notificationEventRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException(NOTIFICATION_NOT_FOUND));

        if (!notification.isOwner(userProviderId)) {
            throw new NotificationException(NOTIFICATION_DELETE_FORBIDDEN);
        }

        User user = notification.getUser();
        user.getNotificationEvents().remove(notification);
        notificationEventRepository.delete(notification);
    }

    private CursorNotificationResponse.CursorMeta getNextCursorFrom(NotificationResponse last, boolean hasNext) {
        if (!hasNext) {
            return null;
        }

        return CursorMeta.builder()
                .dateTime(last.getCreatedAt())
                .id(last.getId())
                .build();
    }
}
