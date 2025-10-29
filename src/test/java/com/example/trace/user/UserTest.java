package com.example.trace.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.trace.gpt.dto.VerificationDto;
import com.example.trace.post.domain.PostType;
import com.example.trace.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @DisplayName("미션 인증 시 확득 포인트")
    @Test
    void textMissionValidation() throws Exception {
        //given
        User user = User.builder().build();
        VerificationDto verificationDto = VerificationDto.builder()
                .imageResult(false)
                .textResult(true)
                .build();

        //when
        user.updateVerification(verificationDto, PostType.MISSION);

        //then
        assertEquals(150L, user.getPointBalance());
    }

    @DisplayName("미션 인증 시 확득 포인트")
    @Test
    void imageMissionValidation() throws Exception {
        //given
        User user = User.builder().build();
        VerificationDto verificationDto = VerificationDto.builder()
                .imageResult(true)
                .textResult(true)
                .build();

        //when
        user.updateVerification(verificationDto, PostType.MISSION);

        //then
        assertEquals(300L, user.getPointBalance());
    }

    @DisplayName("이미지를 포함한 선행 인증 시 획득 포인트")
    @Test
    void textPostValidation() throws Exception {
        //given
        User user = User.builder().build();
        VerificationDto verificationDto = VerificationDto.builder()
                .imageResult(false)
                .textResult(true)
                .build();

        //when
        user.updateVerification(verificationDto, PostType.GOOD_DEED);

        //then
        assertEquals(50L, user.getPointBalance());
    }

    @DisplayName("이미지를 포함한 선행 인증 시 획득 포인트")
    @Test
    void imagePostValidation() throws Exception {
        //given
        User user = User.builder().build();
        VerificationDto verificationDto = VerificationDto.builder()
                .imageResult(true)
                .textResult(true)
                .build();

        //when
        user.updateVerification(verificationDto, PostType.GOOD_DEED);

        //then
        assertEquals(100L, user.getPointBalance());
    }
}