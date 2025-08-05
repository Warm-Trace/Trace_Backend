package com.example.trace.mission.service;

import com.example.trace.global.errorcode.MissionErrorCode;
import com.example.trace.global.exception.MissionException;
import com.example.trace.global.response.CursorResponse;
import com.example.trace.gpt.dto.VerificationDto;
import com.example.trace.gpt.service.PostVerificationService;
import com.example.trace.mission.dto.DailyMissionResponse;
import com.example.trace.mission.dto.MissionCursorRequest;
import com.example.trace.mission.dto.SubmitDailyMissionDto;
import com.example.trace.mission.mission.DailyMission;
import com.example.trace.mission.mission.Mission;
import com.example.trace.mission.repository.DailyMissionRepository;
import com.example.trace.mission.repository.MissionRepository;
import com.example.trace.mission.util.MissionDateUtil;
import com.example.trace.notification.service.NotificationEventService;
import com.example.trace.post.domain.PostType;
import com.example.trace.post.dto.post.PostCreateDto;
import com.example.trace.post.dto.post.PostDto;
import com.example.trace.post.service.PostService;
import com.example.trace.user.User;
import com.example.trace.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DailyMissionService {

    private final MissionRepository missionRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final UserService userService;
    private final PostVerificationService postVerificationService;
    private final PostService postService;
    private final NotificationEventService notificationEventService;

    private static final int MAX_CHANGES_PER_DAY = 10;
    private static final int DEFAULT_PAGE_SIZE = 20;


    @Scheduled(cron = "0 0 7 * * *")
    public void assignDailyMissionsToAllUsers() {
        LocalDate today = LocalDate.now();
        List<User> users = userService.getAllUsers();

        for (User user : users) {

            try {
                Optional<DailyMission> existingMission = dailyMissionRepository.findByUserAndCreatedAt(user, today);
                if (existingMission.isPresent()) {
                    continue;
                }
                assignDailyMissionsToUser(user, today);
                log.info("유저 : {}에게 데일리 미션 할당 성공", user.getProviderId());
            } catch (Exception e) {
                log.error("유저 ID {}에게 데일리 미션 할당 실패: {}", user.getId(), e.getMessage(), e);
            }

        }

    }


    @Transactional
    public DailyMissionResponse assignDailyMissionsToUser(User user, LocalDate date) {
        Mission randomMission = missionRepository.findRandomMission();
        DailyMission dailyMission = DailyMission.builder()
                .user(user)
                .mission(randomMission)
                .createdAt(date)
                .changeCount(0)
                .isVerified(false)
                .build();

        dailyMission = dailyMissionRepository.save(dailyMission);

        notificationEventService.sendDailyMissionAssignedNotification(user, randomMission);
        return DailyMissionResponse.fromEntity(dailyMission);
    }


    @Transactional
    public DailyMissionResponse changeDailyMission(String providerId) {
        User user = userService.getUser(providerId);
        LocalDate missionDate = MissionDateUtil.getMissionDate();

        DailyMission currentMission = dailyMissionRepository.findByUserAndCreatedAt(user, missionDate)
                .orElseThrow(() -> new MissionException(MissionErrorCode.DAILYMISSION_NOT_FOUND));

        if (currentMission.isVerified()) {
            throw new MissionException(MissionErrorCode.ALREADY_VERIFIED);
        }

        if (currentMission.getChangeCount() >= MAX_CHANGES_PER_DAY) {
            throw new MissionException(MissionErrorCode.MISSION_CREATION_LIMIT_EXCEEDED);
        }

        Long currentMissionId = currentMission.getMission().getId();

        Mission newMission = null;
        for (int i = 0; i < 5; i++) {
            Mission randomMission = missionRepository.findRandomMission();
            if (!randomMission.getId().equals(currentMissionId)) {
                newMission = randomMission;
                break;
            }
        }

        if (newMission == null) {
            throw new MissionException(MissionErrorCode.RANDOM_MISSION_NOT_FOUND);
        }

        currentMission.changeMission(newMission);
        dailyMissionRepository.save(currentMission);
        return DailyMissionResponse.fromEntity(currentMission);
    }

    /**
     * providerId로 오늘의 미션을 조회합니다.
     */
    public DailyMissionResponse getTodaysMissionByProviderId(String providerId) {
        User user = userService.getUser(providerId);
        LocalDate missionDate = MissionDateUtil.getMissionDate();
        DailyMission dailyMission = dailyMissionRepository.findByUserAndCreatedAt(user, missionDate)
                .orElseThrow(() -> new MissionException(MissionErrorCode.DAILYMISSION_NOT_FOUND));

        return DailyMissionResponse.fromEntity(dailyMission);
    }

    public PostDto verifySubmissionAndCreatePost(String providerId, SubmitDailyMissionDto submitDto) {
        User user = userService.getUser(providerId);

        LocalDate missionDate = MissionDateUtil.getMissionDate();
        DailyMission assignedDailyMission = dailyMissionRepository.findByUserAndCreatedAt(user, missionDate)
                .orElseThrow(() -> new MissionException(MissionErrorCode.DAILYMISSION_NOT_FOUND));

        VerificationDto verificationDto = postVerificationService.verifyDailyMission(submitDto, assignedDailyMission, providerId);
        if (!verificationDto.isImageResult() && !verificationDto.isTextResult()) {
            throw new MissionException(MissionErrorCode.VERIFICATION_FAIL);
        }

        PostCreateDto postCreateDto = PostCreateDto.builder()
                .postType(PostType.MISSION)
                .title(submitDto.getTitle())
                .content(submitDto.getContent())
                .imageFiles(submitDto.getImageFiles())
                .missionContent(assignedDailyMission.getMission().getDescription())
                .build();

        PostDto postDto = postService.createPost(postCreateDto, providerId, verificationDto);

        // 미션 완료 처리
        assignedDailyMission.updateVerification(true, postDto.getId());
        dailyMissionRepository.save(assignedDailyMission);

        return postDto;
    }

    public CursorResponse<DailyMissionResponse> getCompletedMissions(User user, MissionCursorRequest request) {
        Integer size = request.getSize() != null ? request.getSize() : DEFAULT_PAGE_SIZE;

        List<DailyMission> completedMissions = dailyMissionRepository
                .findVerifiedMissionsWithCursor(user,
                        request.getCursorDateTime() != null ? request.getCursorDateTime().toLocalDate() : null,
                        size + 1);

        boolean hasNext = false;
        if (completedMissions.size() > size) {
            hasNext = true;
            completedMissions = completedMissions.subList(0, size);
        }

        List<DailyMissionResponse> missionResponses = completedMissions.stream()
                .map(DailyMissionResponse::fromEntity)
                .toList();

        CursorResponse.CursorMeta nextCursor = null;
        if (!missionResponses.isEmpty() && hasNext) {
            DailyMissionResponse lastMission = missionResponses.get(missionResponses.size() - 1);
            nextCursor = CursorResponse.CursorMeta.builder()
                    .dateTime(lastMission.getCreatedAt().atStartOfDay()) // LocalDate를 LocalDateTime으로 변환
                    .build();
        }

        // 응답 생성
        return CursorResponse.<DailyMissionResponse>builder()
                .content(missionResponses)
                .hasNext(hasNext)
                .cursor(nextCursor)
                .build();
    }
}

