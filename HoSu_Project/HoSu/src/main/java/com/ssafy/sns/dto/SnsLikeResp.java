package com.ssafy.sns.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "SNS 좋아요 토글 응답 DTO")
public class SnsLikeResp {
    @Schema(description = "성공 여부", example = "true")
    private Boolean success;

    @Schema(description = "좋아요 상태 (true: 좋아요 추가됨, false: 좋아요 취소됨)", example = "true")
    private Boolean isLiked;

    @Schema(description = "총 좋아요 수", example = "43")
    private Integer likeCount;
}
