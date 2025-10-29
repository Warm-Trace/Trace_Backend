package com.example.trace.emotion;

import com.example.trace.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmotionRepository extends JpaRepository<Emotion, Long> {
    Optional<Emotion> findByPostIdAndUserAndEmotionType(Long postId, User user, EmotionType emotionType);

    Long countByPostIdAndEmotionType(Long postId, EmotionType emotionType);

    Emotion findByPostIdAndUser(Long postId, User user);
}
