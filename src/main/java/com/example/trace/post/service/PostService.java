package com.example.trace.post.service;

import com.example.trace.global.response.CursorResponse;
import com.example.trace.gpt.dto.VerificationDto;
import com.example.trace.post.dto.cursor.MyPagePostRequest;
import com.example.trace.post.dto.cursor.PostCursorRequest;
import com.example.trace.post.dto.cursor.PostFeedRequest;
import com.example.trace.post.dto.cursor.PostSearchRequest;
import com.example.trace.post.dto.post.PostCreateDto;
import com.example.trace.post.dto.post.PostDto;
import com.example.trace.post.dto.post.PostFeedDto;
import com.example.trace.post.dto.post.PostUpdateDto;
import com.example.trace.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    PostDto createPost(PostCreateDto postCreateDto, Long userId);

    PostDto createPost(PostCreateDto postCreateDto, String ProviderId, VerificationDto verificationDto);

    PostDto getPostById(Long id, User user);

    PostDto updatePost(Long id, PostUpdateDto postUpdateDto, List<MultipartFile> imageFiles, String providerId);

    CursorResponse<PostFeedDto> getAllPostsWithCursor(PostFeedRequest request, String requesterId);

    void deletePost(Long id, String providerId);

    CursorResponse<PostFeedDto> searchPostsWithCursor(PostSearchRequest request, String providerId);

    CursorResponse<PostFeedDto> getMyPostsWithCursor(PostCursorRequest request, String providerId);

    CursorResponse<PostFeedDto> getUserCommentedPostsWithCursor(PostCursorRequest request, String providerId);

    CursorResponse<PostFeedDto> getUserEmotedPostsWithCursor(PostCursorRequest request, String providerId);

    CursorResponse<PostFeedDto> getMyPagePostsWithCursor(MyPagePostRequest request, String providerId);

} 