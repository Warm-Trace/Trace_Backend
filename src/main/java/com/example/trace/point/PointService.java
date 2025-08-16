package com.example.trace.point;

import com.example.trace.global.response.CursorResponse;
import com.example.trace.global.response.CursorResponse.CursorMeta;
import com.example.trace.gpt.dto.VerificationDto;
import com.example.trace.post.domain.Post;
import com.example.trace.post.domain.PostType;
import com.example.trace.user.User;
import com.example.trace.util.StringUtil;
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
    public void grantPointForPost(Post post, User user, VerificationDto verification) {
        PointSource source =
                post.getPostType() == PostType.MISSION ? PointSource.MISSION_POST : PointSource.GOOD_DEED_POST;
        int finalPoints = source.calculatePointFor(verification);
        String previewContent = StringUtil.truncateLess(post.getContent());

        Point point = Point.builder()
                .source(source)
                .amount(finalPoints)
                .post(post)
                .content(previewContent)
                .user(user)
                .build();

        pointRepository.save(point);
    }

    @Transactional(readOnly = true)
    public CursorResponse<PointResponse> getPage(Integer size, Long id, LocalDateTime dateTime, User user) {
        int fetchSize = size + 1;
        Sort sortCriteria = Sort.by("createdAt").descending().and(Sort.by("id").descending());
        PageRequest pageable = PageRequest.of(0, fetchSize, sortCriteria);

        List<Point> points = isFirstPage(id, dateTime)
                ? pointRepository.findFirstPage(user, pageable)
                : pointRepository.findNextPage(user, dateTime, id, pageable);

        boolean hasNext = points.size() > size;
        if (hasNext) {
            points = points.subList(0, size);
        }

        List<PointResponse> response = points.stream().map(PointResponse::fromEntity).toList();
        CursorMeta nextCursor = getNextCursorFrom(response, hasNext);

        return new CursorResponse<>(response, hasNext, nextCursor);
    }

    private CursorResponse.CursorMeta getNextCursorFrom(List<PointResponse> response, boolean hasNext) {
        if (!hasNext || response.isEmpty()) {
            return null;
        }

        PointResponse last = response.get(response.size() - 1);
        return CursorMeta.builder()
                .dateTime(last.getCreatedAt())
                .id(last.getId())
                .build();
    }

    private boolean isFirstPage(Long id, LocalDateTime time) {
        return id == null || time == null;
    }
}
