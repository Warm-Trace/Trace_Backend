package com.example.trace.user;


import static com.example.trace.global.errorcode.UserErrorCode.POINT_NOT_GRANTED;
import static com.example.trace.global.errorcode.UserErrorCode.USER_NOT_FOUND;

import com.example.trace.auth.Util.JwtUtil;
import com.example.trace.auth.Util.RedisUtil;
import com.example.trace.global.errorcode.TokenErrorCode;
import com.example.trace.global.errorcode.UserErrorCode;
import com.example.trace.global.exception.TokenException;
import com.example.trace.global.exception.UserException;
import com.example.trace.point.PointRepository;
import com.example.trace.point.PointService;
import com.example.trace.point.PointSource;
import com.example.trace.report.domain.UserBlock;
import com.example.trace.report.repository.UserBlockRepository;
import com.example.trace.user.domain.AttendanceDay;
import com.example.trace.user.domain.User;
import com.example.trace.user.dto.AttendanceResponse;
import com.example.trace.user.dto.BlockedUserProfileDto;
import com.example.trace.user.dto.UpdateNickNameRequest;
import com.example.trace.user.dto.UserDto;
import com.example.trace.user.dto.UserVerificationInfo;
import com.example.trace.user.repository.AttendanceRepository;
import com.example.trace.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserBlockRepository userBlockRepository;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final AttendanceRepository attendanceRepository;
    private final PointRepository pointRepository;
    private final PointService pointService;

    /**
     * providerId로 사용자 정보를 조회합니다.
     */
    public UserDto getUserInfo(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserDto.fromEntity(user);
    }

    public BlockedUserProfileDto getBlockedUserProfile(String currentUserProviderId, String targetProviderId) {
        // 차단 정보 조회
        User currentUser = getUser(currentUserProviderId);
        User targetUser = getUser(targetProviderId);

        UserBlock userBlock = userBlockRepository.findByBlockerAndBlocked(currentUser, targetUser)
                .orElseThrow(() -> new IllegalArgumentException("차단 정보를 찾을 수 없습니다."));

        return BlockedUserProfileDto.fromEntity(targetUser, userBlock.getCreatedAt());
    }

    public User getUser(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        return user;
    }


    @Transactional
    public UserDto updateUserNickName(User user, UpdateNickNameRequest request) {
        String newNickname = request.getNickname();
        if (newNickname != null) {
            // 닉네임이 현재 닉네임과 다를 때만 중복 체크
            if (!newNickname.equals(user.getNickname()) && userRepository.existsByNickname(newNickname)) {
                throw new UserException(UserErrorCode.ALREADY_IN_USE_NICKNAME);
            }
            user.updateNickname(newNickname);
        }
        userRepository.save(user);
        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto updateUserProfileImage(String providerId, String imageUrl) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.updateProfileImageUrl(imageUrl);
        userRepository.save(user);
        return UserDto.fromEntity(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void logout(String accessToken) {
        String providerId = jwtUtil.getProviderId(accessToken);
        long expiration = jwtUtil.getExpTime(accessToken);
        redisUtil.save(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
        String redisKey = "RT:" + providerId;
        redisUtil.delete(redisKey);
    }

    public void deleteUser(String accessToken) {
        String providerId = jwtUtil.getProviderId(accessToken);
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new TokenException(TokenErrorCode.NOT_FOUND_USER));
        userRepository.delete(user);
        Long expiration = jwtUtil.getExpTime(accessToken);
        redisUtil.save(accessToken, "delete", expiration, TimeUnit.MILLISECONDS);
        String redisKey = "RT:" + providerId;
        redisUtil.delete(redisKey);
    }

    public UserVerificationInfo getUserVerificationInfo(User user) {
        return UserVerificationInfo.from(user);
    }

    @Transactional
    public AttendanceResponse attend(User user) {
        LocalDate today = LocalDate.now();
        Long userId = user.getId();

        int inserted = attendanceRepository.insertIfAbsent(userId, today);
        long pointsAdded = 0;

        boolean isNewCheckin = (inserted == 1);
        if (isNewCheckin) {
            // 중복 포인트 제공 방지
            AttendanceDay attendanceDay = attendanceRepository.findByUserIdAndAttDate(userId, today)
                    .orElseThrow(() -> new UserException(POINT_NOT_GRANTED));
            boolean given = pointRepository.findByAttendanceDay(attendanceDay).isPresent();

            if (!given) {
                // 포인트 지급
                user.getPoint(PointSource.ATTENDANCE.getBasePoints());
                pointsAdded = pointService.grantAttendancePoint(user, attendanceDay);
            }
        }

        userRepository.save(user);
        Long balance = userRepository.getBalance(user.getId());

        return new AttendanceResponse(today, true, pointsAdded, balance);
    }

    @Transactional(readOnly = true)
    public boolean getTodayAttendance(Long userId) {
        LocalDate today = LocalDate.now();
        boolean attended = attendanceRepository.findByUserIdAndAttDate(userId, today).isPresent();

        return attended;
    }
}
