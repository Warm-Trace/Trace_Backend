package com.example.trace.report.service;

import com.example.trace.auth.repository.UserRepository;
import com.example.trace.report.domain.UserBlock;
import com.example.trace.report.repository.UserBlockRepository;
import com.example.trace.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBlockService {

    private final UserRepository userRepository;
    private final UserBlockRepository userBlockRepository;

    public void blockUser(Long blockerId, Long blockedId) {
        if (blockerId.equals(blockedId)) {
            throw new IllegalArgumentException("자기 자신을 차단할 수 없습니다");
        }
        User blocker = userRepository.findById(blockerId)
                .orElseThrow(() -> new IllegalArgumentException("차단하는 사용자를 찾을 수 없습니다."));
        User blocked = userRepository.findById(blockedId)
                .orElseThrow(() -> new IllegalArgumentException("차단될 사용자들 찾을 수 없습니다."));

        userBlockRepository.findByBlockerAndBlocked(blocker, blocked)
                .ifPresent(existingBlock -> {
                    throw new IllegalArgumentException("이미 차단한 사용자입니다.");
                });

        UserBlock userBlock = UserBlock.builder()
                .blocker(blocker)
                .blocked(blocked)
                .build();

        userBlockRepository.save(userBlock);
    }

    public void unblockUser(Long blockerId, Long blockedId) {
        User blocker = userRepository.findById(blockerId)
                .orElseThrow(() -> new IllegalArgumentException("차단 해제하는 사용자를 찾을 수 없습니다."));
        User blocked = userRepository.findById(blockedId)
                .orElseThrow(() -> new IllegalArgumentException("차단 해제될 사용자를 찾을 수 없습니다."));

        UserBlock userBlock = userBlockRepository.findByBlockerAndBlocked(blocker, blocked)
                .orElseThrow(() -> new IllegalArgumentException("차단된 사용자가 아닙니다."));

        userBlockRepository.delete(userBlock);
    }
}
