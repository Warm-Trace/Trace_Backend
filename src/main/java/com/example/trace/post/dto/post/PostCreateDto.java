package com.example.trace.post.dto.post;

import com.example.trace.mission.dto.SubmitDailyMissionDto;
import com.example.trace.post.domain.PostType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 작성 요청 DTO")
public class PostCreateDto {

    @NotNull(message = "게시글 유형을 선택해주세요")
    @Schema(description = "게시글 유형", example = "FREE,GOOD_DEED,MISSION")
    private PostType postType;

    @NotBlank(message = "제목을 입력해주세요")
    @Schema(description = "게시글 제목", example = "게시글 제목")
    private String title;
    @NotBlank(message = "내용을 입력해주세요")
    @Schema(description = "게시글 내용", example = "게시글 내용")
    private String content;

    @Schema(description = "할당된 미션 내용", example = "종업원에게 인사하기", hidden = true)
    private String missionContent;

    @JsonIgnore
    private MultipartFile imageFile;

    @JsonIgnore
    private List<MultipartFile> imageFiles;

    public static PostCreateDto createForMission(SubmitDailyMissionDto dailyMissionDto, String content) {
        return PostCreateDto.builder()
                .postType(PostType.MISSION)
                .title(dailyMissionDto.getTitle())
                .content(dailyMissionDto.getContent())
                .imageFiles(dailyMissionDto.getImageFiles())
                .missionContent(content)
                .build();
    }
} 