package com.example.trace.bird;

import com.example.trace.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
