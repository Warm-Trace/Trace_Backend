package com.example.trace.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.example.trace.auth.repository.UserRepository;
import com.example.trace.notification.domain.NotificationEvent;
import com.example.trace.notification.domain.NotificationEvent.NotificationData;
import com.example.trace.notification.domain.NotificationEventType;
import com.example.trace.notification.domain.SourceType;
import com.example.trace.notification.dto.CursorNotificationResponse;
import com.example.trace.notification.dto.NotificationResponse;
import com.example.trace.notification.repository.NotificationEventRepository;
import com.example.trace.notification.service.NotificationService;
import com.example.trace.user.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationEventRepository notificationEventRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void getNotifications() throws Exception {
        //given
        String providerId = "12345";
        User user = User.builder()
                .id(1L)
                .providerId(providerId)
                .build();
        Integer size = 10;
        UUID uuid = UUID.randomUUID();

        NotificationData data = getDataOf("mission", "");
        NotificationEvent event = getNotificationFromData(uuid, 1L, data);

        event.mapToUser(user);

        //when
        when(notificationEventRepository.findFirstPage(user, PageRequest.of(0, size + 1,
                Sort.by("createdAt").descending().and(Sort.by("id").descending()))))
                .thenReturn(List.of(event));

        CursorNotificationResponse<NotificationResponse> notifications = notificationService.getNotifications(
                size, null, null, user);

        //then
        assertEquals(1, notifications.getContent().size());
        assertNotNull(notifications.getContent().get(0).getData());
    }

    @Test
    void read() throws Exception {
        //given
        User user = new User();
        UUID uuid = UUID.randomUUID();
        NotificationData data = getDataOf("mission", "");
        NotificationEvent event = getNotificationFromData(uuid, 1L, data);

        //when
        when(userRepository.findByProviderId("none")).thenReturn(Optional.of(user));
        when(notificationEventRepository.findById(uuid)).thenReturn(Optional.of(event));

        NotificationEvent updated = notificationService.read(uuid, "none");

        //then
        assertTrue(updated.getIsRead());
    }

    @Test
    void readReferredNotifications() throws Exception {
        //given
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();
        User user = User.builder().providerId("123").build();
        NotificationData commentData1 = getDataOf("comment", "1");
        NotificationData commentData2 = getDataOf("comment", "2");
        NotificationData emotionData = getDataOf("emotion", "2");
        NotificationEvent notification1 = getNotificationFromData(uuid1, 10L, commentData1);
        NotificationEvent notification2 = getNotificationFromData(uuid2, 10L, commentData2);
        NotificationEvent notification3 = getNotificationFromData(uuid3, 10L, emotionData);

        //when
        when(userRepository.findByProviderId("123")).thenReturn(Optional.of(user));
        when(notificationEventRepository.findById(uuid1)).thenReturn(Optional.of(notification1));
        when(notificationEventRepository.findAllByRefIdAndUser(10L, user)).thenReturn(
                List.of(notification1, notification2, notification3));

        notificationService.read(uuid1, "123");

        //then
        assertTrue(notification2.getIsRead());
        assertTrue(notification3.getIsRead());
    }

    private NotificationData getDataOf(String type, String num) {
        return NotificationData.builder()
                .type(SourceType.fromString(type))
                .title("title" + num)
                .body("body" + num)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private NotificationEvent getNotificationFromData(UUID id, Long refId, NotificationData data) {
        return NotificationEvent.builder()
                .id(id)
                .refId(refId)
                .data(data)
                .createdAt(LocalDateTime.now())
                .sourceType(data.getType())
                .type(NotificationEventType.DATA)
                .build();
    }
}