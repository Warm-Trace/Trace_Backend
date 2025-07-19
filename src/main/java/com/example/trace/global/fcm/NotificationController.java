package com.example.trace.global.fcm;

import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notifications", description = "알림 탭 API")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    @Operation(summary = "모든 알림 가져오기", description = "알림 탭 새로고침 시 호출됩니다. 사용자의 알림을 최신순으로 정렬하여 전송합니다.")
    public ResponseEntity<?> getAllNotifications(@AuthenticationPrincipal PrincipalDetails current) {
        User currentUser = current.getUser();
        return ResponseEntity.ok(notificationService.getAllNotifications(currentUser.getProviderId()));
    }
}
