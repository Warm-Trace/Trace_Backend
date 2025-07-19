package com.example.trace.global.fcm;

import com.example.trace.emotion.EmotionType;
import com.example.trace.global.fcm.domain.NotificationEvent;
import com.example.trace.global.fcm.domain.NotificationEventType;
import com.example.trace.global.fcm.domain.SourceType;
import com.example.trace.post.domain.PostType;
import com.example.trace.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventService {

    private final FcmTokenNotificationService fcmTokenNotificationService;
    private final NotificationEventRepository notificationEventRepository;

    public void sendDailyMissionAssignedNotification(User user) {
        String providerId = user.getProviderId();
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("type", "mission");

        Map<String, String> sentData = fcmTokenNotificationService.sendDataOnlyMessage(
                providerId,
                "오늘의 선행 미션 도착!",
                "작은 선행으로 따뜻한 흔적을 남겨보세요!",
                additionalData
        );

        try {
            saveDataMessage(user, sentData);
        } catch (JsonProcessingException e) {
            log.error("전송한 FCM Data Message를 Json으로 직렬화하는데 실패했습니다. - data: {}", sentData);
            throw new RuntimeException(e);
        }
    }

    public void sendCommentNotification(User user, Long postId, PostType postType, String commentContent) {
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("type", "comment");
        additionalData.put("postId", String.valueOf(postId));

        Map<String, String> sentData = fcmTokenNotificationService.sendDataOnlyMessage(
                user.getProviderId(),
                postType.getType() + "게시판",
                "새로운 댓글이 달렸어요 : " + commentContent,
                additionalData
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
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("type", "emotion");
        additionalData.put("postId", String.valueOf(postId));
        additionalData.put("emotion", emotionType.name());

        Map<String, String> sentData = fcmTokenNotificationService.sendDataOnlyMessage(
                user.getProviderId(),
                postType.getType() + " 게시판",
                nickName + "님이 당신의 흔적에 " + emotionType.getDescription() + "를 남겼어요",
                additionalData
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
    private void saveDataMessage(User user, Map<String, String> data) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String dataJson = objectMapper.writeValueAsString(data);
        String timestamp = data.get("timestamp");
        String sourceType = data.get("type");

        NotificationEvent event = NotificationEvent.builder()
                .data(dataJson)
                .timestamp(timestamp)
                .user(user)
                .sourceType(SourceType.fromString(sourceType))
                .type(NotificationEventType.DATA)
                .build();

        notificationEventRepository.save(event);
    }
}
