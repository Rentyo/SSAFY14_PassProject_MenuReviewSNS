package com.ssafy.follow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 팔로우 통계 정보 DTO
 * 사용자의 팔로워 수와 팔로잉 수를 제공
 * 프로필 페이지 헤더, 메인 페이지 등에서 사용
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Schema(description = "팔로우 통계 정보 DTO")
public class FollowStats {

    @Schema(description = "사용자 번호", example = "1")
    private Long userNo;

    @Schema(description = "팔로워 수 (나를 팔로우하는 사람)", example = "150")
    private Integer followerCount;

    @Schema(description = "팔로잉 수 (내가 팔로우하는 사람)", example = "200")
    private Integer followingCount;
}
