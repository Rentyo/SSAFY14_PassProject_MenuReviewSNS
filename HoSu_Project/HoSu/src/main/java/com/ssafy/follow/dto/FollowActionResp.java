package com.ssafy.follow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 팔로우/언팔로우 액션 응답 DTO
 * 팔로우 또는 언팔로우 성공 후 클라이언트에 반환
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "팔로우/언팔로우 액션 응답 DTO")
public class FollowActionResp {

    @Schema(description = "성공 여부", example = "true")
    private Boolean success;

    @Schema(description = "응답 메시지", example = "팔로우 성공")
    private String message;

    @Schema(description = "팔로우 ID (팔로우 성공 시 생성된 ID, 언팔로우 시 null)", example = "123")
    private Long followId;

    @Schema(description = "업데이트된 팔로우 통계 (팔로워 수, 팔로잉 수)")
    private FollowStats stats;
}
