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
import com.fasterxml.jackson.core.JsonProcessingException;
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
                .type(SourceType.MISSION)
                .build();

        NotificationData sentData = fcmTokenNotificationService.sendDataOnlyMessage(
                providerId,
                "오늘의 선행 미션 도착!",
                mission.getDescription(),
                data
        );

        try {
            saveDataMessage(user, sentData);
        } catch (JsonProcessingException e) {
            log.error("전송한 FCM Data Message를 Json으로 직렬화하는데 실패했습니다. - data: {}", sentData);
            throw new RuntimeException(e);
        }
        return sentData;
    }

    public void sendCommentNotification(User user, Long postId, PostType postType, String commentContent) {
        NotificationEvent.NotificationData data = NotificationData.builder()
                .type(SourceType.COMMENT)
                .postId(postId)
                .build();

        NotificationData sentData = fcmTokenNotificationService.sendDataOnlyMessage(
                user.getProviderId(),
                postType.getType() + "게시판",
                "새로운 댓글이 달렸어요 : " + commentContent,
                data
        );

        try {
            saveDataMessage(user, sentData);
        } catch (JsonProcessingException e) {
            log.error("전송한 FCM Data Message를 Json으로 직렬화하는데 실패했습니다. - data: {}", sentData);
            throw new RuntimeException(e);
        }
    }

    public void sendEmotionNotification(
            User user,
            Long postId,
            PostType postType,
            EmotionType emotionType,
            String nickName) {
        NotificationEvent.NotificationData data = NotificationData.builder()
                .type(SourceType.EMOTION)
                .postId(postId)
                .emotion(emotionType)
                .build();

        NotificationData sentData = fcmTokenNotificationService.sendDataOnlyMessage(
                user.getProviderId(),
                postType.getType() + " 게시판",
                nickName + "님이 당신의 흔적에 " + emotionType.getDescription() + "를 남겼어요",
                data
        );

        try {
            saveDataMessage(user, sentData);
        } catch (JsonProcessingException e) {
            log.error("전송한 FCM Data Message를 Json으로 직렬화하는데 실패했습니다. - data: {}", sentData);
            throw new RuntimeException(e);
        }
    }

    /**
     * FCM 메시지 중 데이터 메시지를 저장하는 메서드
     */
    private void saveDataMessage(User user, NotificationData data) throws JsonProcessingException {
        NotificationEvent event = NotificationEvent.builder()
                .refId(data.getPostId())
                .data(data)
                .createdAt(Long.valueOf(data.getTimestamp()))
                .sourceType(data.getType())
                .type(NotificationEventType.DATA)
                .build();

        event.mapToUser(user);
        notificationEventRepository.save(event);
    }

    //TODO(gyunho): 30일 지나면 삭제하는 기능 추가
}
