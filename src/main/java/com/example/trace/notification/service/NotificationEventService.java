package com.example.trace.notification.service;

import com.example.trace.emotion.EmotionType;
import com.example.trace.global.fcm.FcmTokenNotificationService;
import com.example.trace.mission.mission.Mission;
import com.example.trace.notification.domain.NotificationEvent;
import com.example.trace.notification.domain.NotificationEvent.NotificationData;
import com.example.trace.notification.domain.NotificationEventType;
import com.example.trace.notification.domain.SourceType;
import com.example.trace.notification.repository.NotificationEventRepository;
import com.example.trace.post.domain.PostType;
import com.example.trace.user.User;
import com.github.f4b6a3.uuid.UuidCreator;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventService {

    private final FcmTokenNotificationService fcmTokenNotificationService;
    private final NotificationEventRepository notificationEventRepository;

    public NotificationData sendDailyMissionAssignedNotification(User user, Mission mission) {
        String providerId = user.getProviderId();
        NotificationEvent.NotificationData data = NotificationData.builder()
                .id(UuidCreator.getTimeOrderedEpoch())
                .title("오늘의 선행 미션 도착!")
                .body(mission.getDescription())
                .timestamp(LocalDateTime.now())
                .type(SourceType.MISSION)
                .build();

        NotificationData sentData = fcmTokenNotificationService.sendDataOnlyMessage(providerId, data);
        saveDataMessage(user, sentData);

        return sentData;
    }

    public void sendCommentNotification(User user, Long postId, PostType postType, String commentContent) {
        NotificationEvent.NotificationData data = NotificationData.builder()
                .id(UuidCreator.getTimeOrderedEpoch())
                .title(postType.getType() + "게시판")
                .body("새로운 댓글이 달렸어요 : " + commentContent)
                .timestamp(LocalDateTime.now())
                .type(SourceType.COMMENT)
                .postId(postId)
                .build();

        //NotificationData sentData = fcmTokenNotificationService.sendDataOnlyMessage(user.getProviderId(), data);
        saveDataMessage(user, data);
    }

    public void sendEmotionNotification(
            User user,
            Long postId,
            PostType postType,
            EmotionType emotionType,
            String nickName) {
        NotificationEvent.NotificationData data = NotificationData.builder()
                .id(UuidCreator.getTimeOrderedEpoch())
                .title(postType.getType() + " 게시판")
                .body(nickName + "님이 당신의 흔적에 " + emotionType.getDescription() + "를 남겼어요")
                .timestamp(LocalDateTime.now())
                .type(SourceType.EMOTION)
                .postId(postId)
                .emotion(emotionType)
                .build();

        NotificationData sentData = fcmTokenNotificationService.sendDataOnlyMessage(user.getProviderId(), data);
        saveDataMessage(user, sentData);
    }

    /**
     * FCM 메시지 중 데이터 메시지를 저장하는 메서드
     */
    private void saveDataMessage(User user, NotificationData data) {
        NotificationEvent event = NotificationEvent.builder()
                .id(data.getId())
                .refId(data.getPostId())
                .data(data)
                .createdAt(data.getTimestamp())
                .sourceType(data.getType())
                .type(NotificationEventType.DATA)
                .build();

        event.mapToUser(user);
        notificationEventRepository.save(event);
    }

    //TODO(gyunho): 30일 지나면 삭제하는 기능 추가
}
