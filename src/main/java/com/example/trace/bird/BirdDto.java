package com.example.trace.bird;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BirdDto {
    private BirdLevel birdLevel;
    private boolean isSelected;
}
