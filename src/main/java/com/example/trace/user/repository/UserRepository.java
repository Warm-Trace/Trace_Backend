package com.example.trace.user.repository;

import com.example.trace.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderId(String providerId);

    Optional<User> findByProviderIdAndProvider(String providerId, String provider);

    boolean existsByNickname(String nickname);

    @Query(" SELECT u.pointBalance FROM User u WHERE u.id = :userId")
    Long getBalance(Long userId);
}