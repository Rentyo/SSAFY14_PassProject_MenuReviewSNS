package com.ssafy.sns.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "SNS 댓글 작성 요청 DTO")
public class SnsCommentReq {
    @Schema(description = "게시물 ID", example = "1", required = true)
    private Long boardId;

    @Schema(description = "작성자 사용자 번호", example = "1", required = true)
    private Long userNo;

    @Schema(description = "부모 댓글 ID (대댓글인 경우, 일반 댓글이면 null)", example = "5")
    private Long parentId;

    @Schema(description = "댓글 내용", example = "정말 맛있어 보이네요!", required = true)
    private String content;
}