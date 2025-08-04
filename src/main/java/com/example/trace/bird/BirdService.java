package com.example.trace.bird;

import com.example.trace.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    public List<BirdDto> getAllBirds(User user) {
        List<Bird> birds = birdRepository.findAllByUser(user);
        return birds.stream()
                .map(bird -> BirdDto.builder()
                        .birdLevel(bird.getBirdLevel())
                        .isSelected(bird.isSelected())
                        .build())
                .collect(Collectors.toList());
    }

    public Optional<BirdLevel> checkAndUnlockBirdLevel(User user) {
        long verificationCount = user.getVerificationCount();
        long completedMissionCount = user.getCompletedMissionCount();

        Set<BirdLevel> ownedBirds = getOwnedBirdLevel(user);


        for (BirdLevel birdLevel : BirdLevel.values()) {
            if (!ownedBirds.contains(birdLevel) &&
                    verificationCount >= birdLevel.getRequiredGoodDeedCount() &&
                    completedMissionCount >= birdLevel.getRequiredMissionCount()) {

                // 새로운 제비 해금
                Bird newBird = Bird.builder()
                        .user(user)
                        .birdLevel(birdLevel)
                        .isSelected(true) // 자동 선택
                        .build();

                // 기존 선택된 제비가 있다면 선택된 제비를 해제
                if (!ownedBirds.isEmpty()) {
                    Bird selectedBird = birdRepository.findByUserAndIsSelectedTrue(user)
                            .orElseThrow(() -> new IllegalArgumentException("선택된 새가 없습니다."));
                    selectedBird.deselect();
                    birdRepository.save(selectedBird);
                }

                birdRepository.save(newBird);
                return Optional.of(birdLevel);
            }
        }
        return Optional.empty();
    }

    public Set<BirdLevel> getOwnedBirdLevel(User user) {
        return getAllBirds(user).stream()
                .map(BirdDto::getBirdLevel)
                .collect(Collectors.toSet());
    }
}
