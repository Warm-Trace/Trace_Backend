package com.example.trace.user.repository;

import com.example.trace.user.domain.AttendanceDay;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceDay, Long> {

    @Modifying
    @Query(value = """
                    INSERT IGNORE INTO attendance_day(user_id, att_date, checked_at)
                    VALUES (:userId, :attDate, NOW(6))
            """, nativeQuery = true)
    int insertIfAbsent(@Param("userId") long userId, @Param("attDate") LocalDate attDate);

    Optional<AttendanceDay> findByUserIdAndAttDate(Long userId, LocalDate attDate);
}
