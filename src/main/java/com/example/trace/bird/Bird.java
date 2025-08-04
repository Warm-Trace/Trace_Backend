package com.example.trace.bird;

import com.example.trace.user.User;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Bird {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private BirdLevel birdLevel;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean isActive;

}
