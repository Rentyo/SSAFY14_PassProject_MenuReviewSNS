package com.ssafy.follow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Schema(description = "팔로워/팔로잉 목록 조회용 DTO")
public class FollowUser {

    /*
     * 팔로워/팔로잉 목록 조회용 DTO
     * 사용자 정보와 팔로우 관계 정보를 함께 제공
     * 프로필 페이지의 팔로워/팔로잉 목록에서 사용
     */

    // 사용자 기본정보 (user에서 join으로 가져올 예정)
    @Schema(description = "사용자 번호", example = "2")
    private Long userNo;

    @Schema(description = "닉네임", example = "김철수")
    private String nickName;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile2.jpg")
    private String userImg;

    // 팔로우 관계 정보
    @Schema(description = "팔로우 ID", example = "123")
    private Long followId;

    @Schema(description = "팔로우 시작일시", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    // 맞 팔로우 여부 확인용
    @Schema(description = "내가 이 사람을 팔로우하는지 여부", example = "true")
    private Boolean isFollowing;

    @Schema(description = "이 사람이 나를 팔로우하는지 여부", example = "true")
    private Boolean isFollower;
}
