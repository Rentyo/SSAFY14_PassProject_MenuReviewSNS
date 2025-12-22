package com.ssafy.sns.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "SNS 게시물 응답 DTO")
public class SnsBoardResp {
    // 게시물 정보
    @Schema(description = "게시물 ID", example = "1")
    private Long boardId;
    
    @Schema(description = "식당 ID", example = "1")
    private Long restaurantId;
    
    @Schema(description = "식당명", example = "식당명")
    private String restaurantName;
    
    @Schema(description = "카테고리", example = "KOREAN")
    private String category;

    @Schema(description = "게시물 제목", example = "서울 맛집 후기") // ⭐ 추가
    private String title;

    @Schema(description = "게시물 내용", example = "오늘 점심 맛있게 먹었어요! #맛집")
    private String content;

    @Schema(description = "게시물 대표 이미지 URL", example = "https://example.com/image.jpg") // ⭐ 추가
    private String boardImg;

    @Schema(description = "게시물 태그", example = "#서울맛집 #강남 #추천") // ⭐ 추가
    private String boardTag;

    @Schema(description = "작성일시", example = "2024-01-15T12:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2024-01-15T14:20:00")
    private LocalDateTime updatedAt;

    // 작성자 정보
    @Schema(description = "작성자 사용자 번호", example = "1")
    private Long userNo;
    
    @Schema(description = "작성자 닉네임", example = "홍길동")
    private String nickName;

    @Schema(description = "작성자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String userImg;

    // 이미지 목록
    @Schema(description = "게시물 이미지 목록")
    private List<SnsBoardImage> images;

    // 좋아요 정보
    @Schema(description = "좋아요 수", example = "42")
    private Integer likeCount;

    @Schema(description = "내가 좋아요를 눌렀는지 여부", example = "true")
    private Boolean isLiked;

    // 댓글 정보
    @Schema(description = "댓글 수", example = "15")
    private Integer commentCount;

    @Schema(description = "댓글 목록 (대댓글 포함)")
    private List<SnsComment> comments;
    @Schema(description = "One-line AI review based on search query", example = "This place feels just like the query described!")
    private String oneLineReview;
}