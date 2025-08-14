package com.example.trace.point;

import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.global.response.CursorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/points")
@Tag(name = "Point", description = "획득 포인트 관련 API")
public class PointController {
    private final PointService pointService;

    @GetMapping("/list")
    @Operation(summary = "획득한 포인트 목록 조회", description = "첫 페이지를 보려면 size만 보내고, 두 번째 이후 페이지를 보려면 id와 dateTime을 포함하여 보내주세요.")
    public ResponseEntity<?> get(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestParam("size") Integer size,
            @RequestParam(value = "cursorId", required = false) Long cursorId,
            @RequestParam(value = "cursorDateTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorDateTime
    ) {
        CursorResponse<PointResponse> pointPage = pointService.getPage(size, cursorId, cursorDateTime,
                userDetails.getUser());

        return ResponseEntity.ok(pointPage);
    }
}
