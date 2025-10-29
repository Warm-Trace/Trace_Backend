package com.example.trace.bird;

import com.example.trace.auth.dto.PrincipalDetails;
import com.example.trace.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/birds")
public class BirdController {
    public final BirdService birdService;


    @GetMapping("/selection")
    public ResponseEntity<BirdDto> getSelectedBird(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        BirdDto selectedBird = birdService.getSelectedBird(user);
        return ResponseEntity.ok(selectedBird);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BirdDto>> getAllBirds(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        List<BirdDto> allBirds = birdService.getAllBirds(user);
        return ResponseEntity.ok(allBirds);
    }
}
