package com.example.trace.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.example.trace.auth.repository.UserRepository;
import com.example.trace.global.fcm.NotificationEventRepository;
import com.example.trace.global.fcm.NotificationResponse;
import com.example.trace.global.fcm.NotificationService;
import com.example.trace.global.fcm.domain.NotificationEvent;
import com.example.trace.global.fcm.domain.NotificationEventType;
import com.example.trace.global.fcm.domain.SourceType;
import com.example.trace.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationEventRepository notificationEventRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void getAllNotifications() throws Exception {
        //given
        String providerId = "12345";
        User user = User.builder()
                .id(1L)
                .providerId(providerId)
                .build();
        userRepository.save(user);

        Map<String, String> data = getMissionData("");
        ObjectMapper objectMapper = new ObjectMapper();
        NotificationEvent event = NotificationEvent.builder()
                .data(objectMapper.writeValueAsString(data))
                .createdAt(Long.valueOf(data.get("timestamp")))
                .sourceType(SourceType.fromString(data.get("type")))
                .type(NotificationEventType.DATA)
                .build();

        event.mapToUser(user);

        //when
        when(userRepository.findByProviderId(providerId)).thenReturn(java.util.Optional.of(user));
        List<NotificationResponse> allNotifications = notificationService.getAllNotifications(providerId);

        //then
        assertEquals(1, allNotifications.size());
        assertNotNull(allNotifications.get(0).getData());
    }

    @Test
    void read() throws Exception {
        //given
        Map<String, String> data = getMissionData("");
        ObjectMapper objectMapper = new ObjectMapper();
        NotificationEvent event = NotificationEvent.builder()
                .id(1L)
                .data(objectMapper.writeValueAsString(data))
                .createdAt(Long.valueOf(data.get("timestamp")))
                .sourceType(SourceType.fromString(data.get("type")))
                .type(NotificationEventType.DATA)
                .build();

        //when
        when(notificationEventRepository.findById(1L)).thenReturn(Optional.of(event));
        NotificationEvent updated = notificationService.read(1L);

        //then
        assertTrue(updated.getIsRead());
    }

    private Map<String, String> getMissionData(String num) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "mission");
        data.put("title", "title" + num);
        data.put("body", "body" + num);
        data.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return data;
    }
}