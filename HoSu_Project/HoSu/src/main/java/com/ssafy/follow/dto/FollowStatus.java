package com.ssafy.follow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 팔로우 상태 확인용 DTO
 * 특정 사용자와의 팔로우 관계를 확인할 때 사용
 * 프로필 방문 시 팔로우 버튼 상태 결정에 사용
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "팔로우 상태 확인용 DTO")
public class FollowStatus {

    @Schema(description = "대상 사용자 번호", example = "2")
    private Long targetUserNo;

    @Schema(description = "내가 이 사람을 팔로우하는지 여부", example = "true")
    private Boolean isFollowing;

    @Schema(description = "이 사람이 나를 팔로우하는지 여부 (맞팔 확인)", example = "false")
    private Boolean isFollower;

    @Schema(description = "팔로우 ID (언팔로우 시 필요, 팔로우 중이 아니면 null)", example = "123")
    private Long followId;
}