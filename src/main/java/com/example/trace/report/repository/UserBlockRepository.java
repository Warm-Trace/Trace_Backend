package com.example.trace.report.repository;

import com.example.trace.report.domain.UserBlock;
import com.example.trace.user.domain.User;
import feign.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {
    Optional<UserBlock> findByBlockerAndBlocked(User blocker, User blocked);

    @Query("""
            SELECT ub FROM UserBlock ub
            WHERE ub.blocker = :blocker
            ORDER BY ub.createdAt DESC, ub.id DESC
            """)
    List<UserBlock> findAllByBlocker(@Param("blocker") User blocker);
}