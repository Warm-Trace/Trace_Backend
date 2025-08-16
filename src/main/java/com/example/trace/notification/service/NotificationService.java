package com.example.trace.notification.service;

import static com.example.trace.global.errorcode.NotificationErrorCode.NOTIFICATION_DELETE_FORBIDDEN;
import static com.example.trace.global.errorcode.NotificationErrorCode.NOTIFICATION_NOT_FOUND;
import static com.example.trace.global.errorcode.UserErrorCode.USER_NOT_FOUND;

import com.example.trace.auth.repository.UserRepository;
import com.example.trace.global.exception.NotificationException;
import com.example.trace.global.exception.UserException;
import com.example.trace.notification.domain.NotificationEvent;
import com.example.trace.notification.dto.CursorNotificationResponse;
import com.example.trace.notification.dto.CursorNotificationResponse.CursorMeta;
import com.example.trace.notification.dto.NotificationResponse;
import com.example.trace.notification.repository.NotificationEventRepository;
import com.example.trace.user.User;
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
        int fetchSize = pageSize + 1;
        Sort sortCriteria = Sort.by("createdAt").descending().and(Sort.by("id").descending());
        PageRequest pageable = PageRequest.of(0, fetchSize, sortCriteria);

        List<NotificationEvent> notifications = (cursorDateTime == null || cursorId == null)
                ? notificationEventRepository.findFirstPage(user, pageable)
                : notificationEventRepository.findNextPage(user, cursorDateTime, cursorId, pageable);

        boolean hasNext = notifications.size() > pageSize;
        if (hasNext) {
            notifications = notifications.subList(0, pageSize);
        }

        List<NotificationResponse> results = notifications.stream().map(NotificationResponse::fromEntity).toList();
        CursorNotificationResponse.CursorMeta nextCursor = getNextCursorFrom(results, hasNext);

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

    private CursorNotificationResponse.CursorMeta getNextCursorFrom(List<NotificationResponse> response,
                                                                    boolean hasNext) {
        if (!hasNext || response.isEmpty()) {
            return null;
        }

        NotificationResponse last = response.get(response.size() - 1);
        return CursorMeta.builder()
                .dateTime(last.getCreatedAt())
                .id(last.getId())
                .build();
    }
}
