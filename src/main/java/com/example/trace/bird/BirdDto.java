package com.example.trace.bird;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BirdDto {
    private BirdLevel birdLevel;
    private boolean isSelected;

}
