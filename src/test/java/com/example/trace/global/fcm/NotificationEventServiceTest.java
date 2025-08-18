package com.example.trace.global.fcm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.trace.mission.mission.Mission;
import com.example.trace.notification.domain.NotificationEvent.NotificationData;
import com.example.trace.notification.domain.NotificationSetting;
import com.example.trace.notification.repository.NotificationEventRepository;
import com.example.trace.notification.service.NotificationEventService;
import com.example.trace.notification.service.NotificationService;
import com.example.trace.user.domain.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationEventServiceTest {

    private NotificationEventService notificationEventService;
    private FcmTokenNotificationService fcmTokenNotificationService;

    @Mock
    private FirebaseMessaging firebaseMessaging;
    @Mock
    private FcmTokenService fcmTokenService;
    @Mock
    private NotificationEventRepository notificationEventRepository;
    @Mock
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 수동으로 의존성 주입
        fcmTokenNotificationService = new FcmTokenNotificationService(firebaseMessaging, fcmTokenService);
        notificationEventService = new NotificationEventService(
                fcmTokenNotificationService,
                notificationEventRepository,
                notificationService
        );
    }

    @Test
    void sendMissionDescriptionInMessage() throws Exception {
        //given
        String providerId = "123";
        User user = User.builder()
                .id(1L)
                .providerId(providerId)
                .build();
        Mission mission = new Mission(1L, "낯선 사람에게 친절하게 인사하기");
        NotificationSetting setting = NotificationSetting.of(user);

        //when
        when(fcmTokenService.getTokenByProviderId(providerId)).thenReturn(Optional.of("token"));
        when(firebaseMessaging.send(any(Message.class))).thenReturn("response");
        when(notificationService.getSettingsOf(user.getId())).thenReturn(setting);

        NotificationData sentData = notificationEventService.sendDailyMissionAssignedNotification(user, mission);

        //then
        assertEquals(mission.getDescription(), sentData.getBody());
    }
}