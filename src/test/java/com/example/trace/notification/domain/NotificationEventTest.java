package com.example.trace.notification.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.example.trace.notification.domain.NotificationEvent.NotificationData;
import com.example.trace.notification.domain.NotificationEvent.NotificationDataConverter;
import com.example.trace.post.domain.PostType;
import com.github.f4b6a3.uuid.UuidCreator;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationEventTest {

    @DisplayName("전송된 데이터를 직렬화할 수 있다")
    @Test
    void serializeNotificationData() throws Exception {
        //given
        NotificationEvent.NotificationData data = NotificationData.builder()
                .id(UuidCreator.getTimeOrderedEpoch())
                .title(PostType.FREE + "게시판")
                .body("새로운 댓글이 달렸어요 : " + "commentContent")
                .timestamp(LocalDateTime.now())
                .type(SourceType.COMMENT)
                .postId(1L)
                .build();

        //when
        NotificationDataConverter converter = new NotificationDataConverter();

        //then
        assertDoesNotThrow(() -> converter.convertToDatabaseColumn(data));
    }
}