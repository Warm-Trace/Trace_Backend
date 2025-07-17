package com.example.trace.post.dto.post;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateDto {

    @NotBlank(message = "제목을 입력해주세요")
    private String title;

    private String content;

    @Builder.Default
    private List<String> removal = new ArrayList<>();
} 