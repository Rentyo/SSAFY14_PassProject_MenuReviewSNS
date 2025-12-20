package com.ssafy.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 프로필 페이지 응답 DTO
 * 프로필 화면에 표시될 정보만 포함
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 프로필 응답 DTO (팔로우 정보 포함)")
public class UserProfile {

    // === 기본 프로필 정보 ===
    @Schema(description = "사용자 번호", example = "1")
    private Long userNo;

    @Schema(description = "닉네임", example = "홍길동")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String userImg;

    @Schema(description = "성별", example = "MALE")
    private String gender;

    @Schema(description = "가입일시", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    // === 팔로우 통계 ===
    @Schema(description = "팔로워 수 (나를 팔로우하는 사람)", example = "150")
    private Integer followerCount;

    @Schema(description = "팔로잉 수 (내가 팔로우하는 사람)", example = "200")
    private Integer followingCount;

    // === 팔로우 상태 (다른 사람 프로필 볼 때만) ===
    @Schema(description = "내가 이 사람을 팔로우하는지 여부", example = "true")
    private Boolean isFollowing;

    @Schema(description = "이 사람이 나를 팔로우하는지 여부 (맞팔 확인)", example = "false")
    private Boolean isFollower;

    @Schema(description = "팔로우 ID (언팔로우 시 사용, 팔로우 중이 아니면 null)", example = "123")
    private Long followId;

    // === 추가 통계 ===
    @Schema(description = "게시글 수", example = "42")
    private Integer postCount;

    // === 본인 프로필 여부 ===
    @Schema(description = "내 프로필인지 여부 (편집 버튼 표시용)", example = "false")
    private Boolean isMyProfile;
    
    @Schema(description = "식당 사장님이면 식당 번호(프로필에서 이동용)", example = "1")
    private Long restaurantId;
}