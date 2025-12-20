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
@Schema(description = "SNS 댓글 응답 DTO")
public class SnsComment {
    @Schema(description = "댓글 ID", example = "1")
    private Long commentId;

    @Schema(description = "게시물 ID", example = "1")
    private Long boardId;

    @Schema(description = "작성자 사용자 번호", example = "2")
    private Long userNo;

    @Schema(description = "부모 댓글 ID (대댓글인 경우, 일반 댓글이면 null)", example = "5")
    private Long parentId;

    @Schema(description = "댓글 내용", example = "정말 맛있어 보이네요!")
    private String content;

    @Schema(description = "작성일시", example = "2024-01-15T13:45:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2024-01-15T14:00:00")
    private LocalDateTime updatedAt;

    // 조회용 추가 필드
    @Schema(description = "작성자 닉네임", example = "김철수")
    private String nickName;

    @Schema(description = "작성자 프로필 이미지 URL", example = "https://example.com/profile2.jpg")
    private String userImg;

    @Schema(description = "대댓글 목록")
    private List<SnsComment> replies;
}