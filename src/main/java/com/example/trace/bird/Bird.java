package com.example.trace.bird;

import com.example.trace.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bird {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private BirdLevel birdLevel;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean isSelected;

    public void deselect() {
        isSelected = false;
    }
}
