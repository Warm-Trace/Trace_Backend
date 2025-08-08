package com.example.trace.point;

import com.example.trace.user.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointRepository extends JpaRepository<Point, Long> {
    @Query("SELECT p FROM Point p WHERE p.user = :user ORDER BY p.createdAt DESC, p.id DESC")
    List<Point> findFirstPage(@Param("user") User user, Pageable pageable);

    @Query("""
                            SELECT p FROM Point p
                            WHERE p.user = :user AND (
                            p.createdAt < :dateTime OR
                            (p.createdAt = :dateTime AND p.id < :id)
                            )
                            ORDER BY p.createdAt DESC, p.id DESC
            """)
    List<Point> findNextPage(
            @Param("user") User user,
            @Param("dateTime") LocalDateTime dateTime,
            @Param("id") Long id,
            Pageable pageable
    );
}
