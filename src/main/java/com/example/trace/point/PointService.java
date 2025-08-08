package com.example.trace.point;

import com.example.trace.global.response.CursorResponse;
import com.example.trace.global.response.CursorResponse.CursorMeta;
import com.example.trace.gpt.dto.VerificationDto;
import com.example.trace.post.domain.PostType;
import com.example.trace.user.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    @Transactional
    public void grantPointForPost(PostType postType, User user, VerificationDto verification) {
        PointSource source = postType == PostType.MISSION ? PointSource.MISSION_POST : PointSource.GOOD_DEED_POST;
        int finalPoints = source.calculatePointFor(verification);

        Point point = Point.of(source, finalPoints, user);
        pointRepository.save(point);
    }

    @Transactional(readOnly = true)
    public CursorResponse<PointResponse> getPage(Integer size, Long id, LocalDateTime dateTime, User user) {
        List<Point> points;

        if (isFirstPage(id, dateTime)) {
            points = pointRepository.findFirstPage(
                    user,
                    PageRequest.of(0, size, Sort.by("createdAt").descending().and(Sort.by("id").descending()))
            );
        } else {
            points = pointRepository.findNextPage(
                    user,
                    dateTime,
                    id,
                    PageRequest.of(0, size, Sort.by("createdAt").descending().and(Sort.by("id").descending()))
            );
        }

        List<PointResponse> response = points.stream()
                .map(PointResponse::fromEntity)
                .toList();

        boolean hasNext = response.size() == size;
        PointResponse last = response.get(response.size() - 1);
        CursorMeta nextCursor = getNextCursorFrom(last, hasNext);

        return new CursorResponse<>(response, hasNext, nextCursor);
    }

    private CursorResponse.CursorMeta getNextCursorFrom(PointResponse last, boolean hasNext) {
        if (!hasNext) {
            return null;
        }

        return CursorMeta.builder()
                .dateTime(last.getCreatedAt())
                .id(last.getId())
                .build();
    }

    private boolean isFirstPage(Long id, LocalDateTime time) {
        return id == null || time == null;
    }
}
