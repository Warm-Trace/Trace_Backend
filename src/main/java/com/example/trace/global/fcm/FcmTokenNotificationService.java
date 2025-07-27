package com.example.trace.global.fcm;

import static com.example.trace.global.errorcode.TokenErrorCode.NOT_FOUND_FCM_TOKEN;

import com.example.trace.global.exception.TokenException;
import com.example.trace.notification.domain.NotificationEvent.NotificationData;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmTokenNotificationService {

    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenService fcmTokenService;

    /**
     * Data-only 메시지 전송 (notification 필드 사용 안함)
     */
    public NotificationData sendDataOnlyMessage(String providerId, String title, String body,
                                                NotificationData data) {
        Optional<String> tokenOpt = fcmTokenService.getTokenByProviderId(providerId);

        if (tokenOpt.isEmpty()) {
            log.warn("FCM 토큰을 찾을 수 없습니다 - 사용자 ID: {}", providerId);
            throw new TokenException(NOT_FOUND_FCM_TOKEN);
        }

        String token = tokenOpt.get();

        // Data-only 메시지 구성 (notification 필드 없음)
        data.setTitle(title);
        data.setBody(body);
        data.setTimestamp(String.valueOf(System.currentTimeMillis()));

        Message message = Message.builder()
                .setToken(token)
                .putAllData(data.toMap())  // notification 필드 대신 data 필드만 사용
                .build();

        log.info("fcm 알림 보내는 중..");

        try {
            String response = firebaseMessaging.send(message);
            log.info("FCM message send sucess - user ID: {}, reponse: {}", providerId, response);
        } catch (FirebaseMessagingException e) {
            handleFirebaseException(e, providerId, token);
        }
        return data;
    }

    /**
     * 여러 사용자에게 동시 전송
     */
    public void sendDataOnlyMessageToMultipleUsers(List<String> providerIds, String title, String body,
                                                   Map<String, String> additionalData) {
        List<String> tokens = providerIds.stream()
                .map(fcmTokenService::getTokenByProviderId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (tokens.isEmpty()) {
            log.warn("not found valid fcm token");
            return;
        }

        Map<String, String> data = new HashMap<>();
        data.put("title", title);
        data.put("body", body);
        data.put("timestamp", String.valueOf(System.currentTimeMillis()));

        if (additionalData != null) {
            data.putAll(additionalData);
        }

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .putAllData(data)
                .build();

        try {
            BatchResponse response = firebaseMessaging.sendMulticast(message);
            log.info("FCM 멀티캐스트 메시지 전송 완료 - 성공: {}, 실패: {}",
                    response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("FCM 멀티캐스트 메시지 전송 실패: {}", e.getMessage());
        }
    }

    private void handleFirebaseException(FirebaseMessagingException e, String providerId, String token) {
        MessagingErrorCode errorCode = e.getMessagingErrorCode();

        switch (errorCode) {
            case UNREGISTERED:
                log.warn("not valid token - provider ID: {}", providerId);
                // 토큰 삭제 로직 추가 가능
                break;
            case INVALID_ARGUMENT:
                log.error("wrong arg - provider ID: {}, error: {}", providerId, e.getMessage());
                break;
            default:
                log.error("fcm messsage send failed - provider ID: {}, error : {}, message: {}",
                        providerId, errorCode, e.getMessage());
        }
    }
}
