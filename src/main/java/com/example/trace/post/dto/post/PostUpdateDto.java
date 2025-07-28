package com.example.trace.post.dto.post;

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

    private String title;

    private String content;

    @Builder.Default
    private List<String> removal = new ArrayList<>();
} 