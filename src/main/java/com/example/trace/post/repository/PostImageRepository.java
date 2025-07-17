package com.example.trace.post.repository;

import com.example.trace.post.domain.PostImage;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPostId(Long postId);

    void deleteByPostId(Long postId);

    void deleteAllByImageUrlIn(Collection<String> imageUrls);
}