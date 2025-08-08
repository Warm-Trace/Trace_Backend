package com.example.trace.point;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointResponse {
    private Long id;
    private Integer amount;
    private LocalDateTime createdAt;
    private PointSource source;

    public static PointResponse fromEntity(Point point) {
        return PointResponse.builder()
                .id(point.getId())
                .amount(point.getAmount())
                .createdAt(point.getCreatedAt())
                .source(point.getSource())
                .build();
    }
}
