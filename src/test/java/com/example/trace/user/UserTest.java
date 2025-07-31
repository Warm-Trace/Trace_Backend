package com.example.trace.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.trace.gpt.dto.VerificationDto;
import com.example.trace.post.domain.PostType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @DisplayName("미션 인증 시 확득 포인트")
    @Test
    void textMissionValidation() throws Exception {
        //given
        User user = User.builder().build();
        VerificationDto missionVerification = VerificationDto.builder().textResult(true).imageResult(false).build();

        //when
        user.updateVerification(missionVerification, PostType.MISSION);
        System.out.println(user);
        //then
        assertEquals(1L, user.getVerificationCount());
        assertEquals(150L, user.getVerificationScore());
    }

    @DisplayName("미션 인증 시 확득 포인트")
    @Test
    void imageMissionValidation() throws Exception {
        //given
        User user = User.builder().build();
        VerificationDto missionVerification = VerificationDto.builder().textResult(true).imageResult(true).build();

        //when
        user.updateVerification(missionVerification, PostType.MISSION);
        System.out.println(user);
        //then
        assertEquals(1L, user.getVerificationCount());
        assertEquals(150L, user.getVerificationScore());
    }

    @DisplayName("이미지를 포함한 선행 인증 시 획득 포인트")
    @Test
    void textPostValidation() throws Exception {
        //given
        User user = User.builder().build();
        VerificationDto imageVerification = VerificationDto.builder().textResult(true).imageResult(false).build();

        //when
        user.updateVerification(imageVerification, PostType.GOOD_DEED);
        System.out.println(user);
        //then
        assertEquals(1L, user.getVerificationCount());
        assertEquals(50L, user.getVerificationScore());
    }


    @DisplayName("이미지를 포함한 선행 인증 시 획득 포인트")
    @Test
    void imagePostValidation() throws Exception {
        //given
        User user = User.builder().build();
        VerificationDto imageVerification = VerificationDto.builder().textResult(true).imageResult(true).build();

        //when
        user.updateVerification(imageVerification, PostType.GOOD_DEED);
        System.out.println(user);
        //then
        assertEquals(1L, user.getVerificationCount());
        assertEquals(50L, user.getVerificationScore());
    }


}