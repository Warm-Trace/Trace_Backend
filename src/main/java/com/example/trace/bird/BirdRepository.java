package com.example.trace.bird;

import com.example.trace.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BirdRepository extends JpaRepository<Bird, Long> {
    Optional<Bird> findByUserAndIsSelectedTrue(User user);

    List<Bird> findAllByUser(User user);
}
