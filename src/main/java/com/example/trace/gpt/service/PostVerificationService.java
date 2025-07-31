package com.example.trace.gpt.service;

import com.example.trace.gpt.dto.VerificationDto;
import com.example.trace.mission.dto.SubmitDailyMissionDto;
import com.example.trace.mission.mission.DailyMission;
import com.example.trace.post.dto.post.PostCreateDto;

public interface PostVerificationService {
    VerificationDto verifyPost(PostCreateDto postCreateDto, String providerId);

    VerificationDto verifyDailyMission(SubmitDailyMissionDto submitDto, DailyMission assignedDailyMission,
                                       String providerId);
} 