package com.example.trace.notification.domain;

import static com.example.trace.global.errorcode.NotificationErrorCode.DATA_DESERIALIZATION_FAILED;
import static com.example.trace.global.errorcode.NotificationErrorCode.DATA_SERIALIZATION_FAILED;
import static com.example.trace.global.errorcode.NotificationErrorCode.IDENTIFIER_NOT_FOUND;
import static com.example.trace.global.errorcode.NotificationErrorCode.TIMESTAMP_NOT_FOUND;

import com.example.trace.emotion.EmotionType;
import com.example.trace.global.exception.NotificationException;
import com.example.trace.user.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.NotNull;

@Slf4j
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent implements Comparable<NotificationEvent> {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    // 관련 리소스 id
    private Long refId;

    // optional(for notification message)
    private String title;

    // optional(for notification message)
    private String body;

    // data 메시지인 경우 json 직렬화해서 저장
    @Column(columnDefinition = "TEXT")
    @Convert(converter = NotificationDataConverter.class)
    private NotificationData data;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime createdAt;

    @Builder.Default
    private Boolean isRead = false;

    @Builder.Default
    private Boolean isHidden = false;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    @Enumerated(EnumType.STRING)
    private NotificationEventType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public boolean mapToUser(User user) {
        if (this.user != null && this.user.equals(user)) {
            return false; // 이미 같은 user에 설정되어 있음
        }
        this.user = user;
        user.addNotification(this);
        return true;
    }

    //TODO: sourceType에 따라 refId 할당
    public NotificationEvent read() {
        isRead = true;
        return this;
    }

    public boolean isOwner(String providerId) {
        return this.user.getProviderId().equals(providerId);
    }

    @Override
    public int compareTo(@NotNull NotificationEvent o) {
        return this.createdAt.compareTo(o.createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationEvent)) {
            return false;
        }
        NotificationEvent that = (NotificationEvent) o;
        return this.id != null && this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Notification의 data 필으데 직렬화되어 저장될 구조체
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class NotificationData {
        private UUID id;
        private SourceType type;
        private Long postId;
        private EmotionType emotion;
        private String title;
        private String body;
        private LocalDateTime timestamp;

        public Map<String, String> toMap() {
            if (id == null) {
                throw new NotificationException(IDENTIFIER_NOT_FOUND);
            }
            if (timestamp == null) {
                throw new NotificationException(TIMESTAMP_NOT_FOUND);
            }
            Map<String, String> map = new HashMap<>();
            map.put("id", id.toString());
            map.put("timestamp", timestamp.toString());

            if (title != null) {
                map.put("title", title);
            }
            if (body != null) {
                map.put("body", body);
            }
            if (type != null) {
                map.put("type", type.name());
            }
            if (postId != null) {
                map.put("postId", postId.toString());
            }
            if (emotion != null) {
                map.put("emotion", emotion.name());
            }
            return map;
        }
    }

    @Converter
    public static class NotificationDataConverter implements AttributeConverter<NotificationData, String> {

        private static final ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        @Override
        public String convertToDatabaseColumn(NotificationData attribute) {
            if (attribute == null) {
                return null;
            }
            try {
                return objectMapper.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                log.error("직렬화 실패 {}", attribute, e);
                throw new NotificationException(DATA_SERIALIZATION_FAILED);
            }
        }

        @Override
        public NotificationData convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isEmpty()) {
                return null;
            }
            try {
                return objectMapper.readValue(dbData, NotificationData.class);
            } catch (Exception e) {
                throw new NotificationException(DATA_DESERIALIZATION_FAILED);
            }
        }
    }
}
