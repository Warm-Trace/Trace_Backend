package com.example.trace.emotion;

import com.example.trace.emotion.dto.EmotionCountDto;
import com.example.trace.emotion.dto.EmotionResponse;
import com.example.trace.notification.service.NotificationEventService;
import com.example.trace.post.domain.Post;
import com.example.trace.post.domain.PostType;
import com.example.trace.post.repository.PostRepository;
import com.example.trace.report.service.UserBlockService;
import com.example.trace.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmotionService {
    private final EmotionRepository emotionRepository;
    private final PostRepository postRepository;
    private final NotificationEventService notificationEventService;
    private final UserBlockService userBlockService;


    @Transactional
    public EmotionResponse toggleEmotion(Long postId, User user, EmotionType emotionType) {
        Emotion existingEmotion = emotionRepository.findByPostIdAndUser(postId, user);

        if (existingEmotion == null) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
            Emotion emotion = Emotion.builder()
                    .post(post)
                    .user(user)
                    .emotionType(emotionType)
                    .build();
            emotionRepository.save(emotion);

            if (!user.getProviderId().equals(post.getUser().getProviderId()) &&
                    !userBlockService.isBlocked(post.getUser().getProviderId(), user.getProviderId())) {
                User postAuthor = post.getUser();
                PostType postType = post.getPostType();
                String nickName = user.getNickname();
                notificationEventService.sendEmotionNotification(postAuthor, postId, postType, emotionType, nickName);
            }
            return new EmotionResponse(true, emotionType.name());

        } else {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
            EmotionType currentType = existingEmotion.getEmotionType();
            if (currentType != emotionType) {
                existingEmotion.updateEmotion(emotionType);
                emotionRepository.save(existingEmotion);

                if (!user.getProviderId().equals(post.getUser().getProviderId()) &&
                        !userBlockService.isBlocked(post.getUser().getProviderId(), user.getProviderId())) {
                    User postAuthor = post.getUser();
                    PostType postType = post.getPostType();
                    String nickName = user.getNickname();
                    notificationEventService.sendEmotionNotification(postAuthor, postId, postType, emotionType,
                            nickName);
                    return new EmotionResponse(true, emotionType.name());
                }
                return new EmotionResponse(true, emotionType.name());
            } else {
                emotionRepository.delete(existingEmotion);
                return new EmotionResponse(false, emotionType.name());
            }
        }
    }

    public EmotionCountDto getEmotionCountsByType(Long postId) {
        Long heartwarmingCount = emotionRepository.countByPostIdAndEmotionType(postId, EmotionType.HEARTWARMING);
        Long gratefulCount = emotionRepository.countByPostIdAndEmotionType(postId, EmotionType.GRATEFUL);
        Long impressiveCount = emotionRepository.countByPostIdAndEmotionType(postId, EmotionType.IMPRESSIVE);
        Long touchingCount = emotionRepository.countByPostIdAndEmotionType(postId, EmotionType.TOUCHING);
        Long likeableCount = emotionRepository.countByPostIdAndEmotionType(postId, EmotionType.LIKEABLE);

        return EmotionCountDto.builder()
                .heartwarmingCount(heartwarmingCount)
                .gratefulCount(gratefulCount)
                .impressiveCount(impressiveCount)
                .touchingCount(touchingCount)
                .likeableCount(likeableCount)
                .build();
    }

    public EmotionType getYourEmotion(Long postId, User user) {
        Emotion yourEmotion = emotionRepository.findByPostIdAndUser(postId, user);
        EmotionType yourEmotionType;
        if (yourEmotion == null) {
            yourEmotionType = null;
        } else {
            yourEmotionType = yourEmotion.getEmotionType();
        }
        return yourEmotionType;
    }
}
