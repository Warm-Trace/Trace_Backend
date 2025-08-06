package com.example.trace.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.trace.post.domain.PostType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @DisplayName("미션 인증 시 확득 포인트")
    @Test
    void textMissionValidation() throws Exception {
        //given
        User user = User.builder().build();

        //when
        user.updateVerification(PostType.MISSION);

        //then
        assertEquals(150L, user.getVerificationScore());
    }

    @DisplayName("이미지를 포함한 선행 인증 시 획득 포인트")
    @Test
    void textPostValidation() throws Exception {
        //given
        User user = User.builder().build();

        //when
        user.updateVerification(PostType.GOOD_DEED);

        //then
        assertEquals(50L, user.getVerificationScore());
    }
}