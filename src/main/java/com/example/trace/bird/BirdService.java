package com.example.trace.bird;

import com.example.trace.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BirdService {
    private final BirdRepository birdRepository;

    public BirdDto getSelectedBird(User user) {
        Bird bird = birdRepository.findByUserAndIsSelectedTrue(user)
                .orElseThrow(() -> new IllegalArgumentException("선택된 새가 없습니다."));

        return BirdDto.builder()
                .birdLevel(bird.getBirdLevel())
                .isSelected(bird.isSelected())
                .build();
    }

    public BirdLevel getHighestOwnedBirdLevel(User user) {
        return birdRepository.findAllByUser(user).stream()
                .map(Bird::getBirdLevel)
                .max(Comparator.comparingInt(BirdLevel::getLevel))
                .orElseThrow(() -> new NoSuchElementException("사용자가 소유한 제비가 없습니다."));
    }

    public List<BirdDto> getAllBirds(User user) {
        List<Bird> birds = birdRepository.findAllByUser(user);
        return birds.stream()
                .map(bird -> BirdDto.builder()
                        .birdLevel(bird.getBirdLevel())
                        .isSelected(bird.isSelected())
                        .build())
                .collect(Collectors.toList());
    }

    public Optional<BirdLevel> findUnlockableBird(User user) {
        long verifiedPostCount = user.getVerifiedPostCount();
        long completedMissionCount = user.getCompletedMissionCount();

        BirdLevel highestOwnedBirdLevel = getHighestOwnedBirdLevel(user);

        if (highestOwnedBirdLevel == BirdLevel.LEGENDARY_SWALLOW) {
            return Optional.empty(); // 이미 전설의 제비를 소유하고 있는 경우
        }

        BirdLevel nextBirdLevel = BirdLevel.fromLevel(highestOwnedBirdLevel.getLevel() + 1);

        if (verifiedPostCount >= nextBirdLevel.getRequiredVerifiedPostCount() &&
                completedMissionCount >= nextBirdLevel.getRequiredMissionCount()) {
            return Optional.of(nextBirdLevel);
        }
        return Optional.empty();
    }

    public void unlockBird(User user, BirdLevel birdLevel) {
        Bird selectedBird = birdRepository.findByUserAndIsSelectedTrue(user)
                .orElseThrow(() -> new IllegalArgumentException("선택된 새가 없습니다."));

        selectedBird.deselect();
        birdRepository.save(selectedBird);

        Bird newBird = Bird.builder()
                .user(user)
                .birdLevel(birdLevel)
                .isSelected(true) // 자동 선택
                .build();
        birdRepository.save(newBird);
    }

    public Optional<BirdLevel> checkAndUnlockBirdLevel(User user) {
        Optional<BirdLevel> unlockableBirdLevel = findUnlockableBird(user);
        if (unlockableBirdLevel.isPresent()) {
            unlockBird(user, unlockableBirdLevel.get());
        }
        return unlockableBirdLevel;
    }

}
