package com.example.trace.bird;

import com.example.trace.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BirdRepository extends JpaRepository<Bird, Long> {
    Optional<Bird> findByUserAndIsSelectedTrue(User user);

    List<Bird> findAllByUser(User user);
}
