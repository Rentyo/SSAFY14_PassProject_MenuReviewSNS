package com.ssafy.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
 * 이 DTO는 사용자 정보 응답용 DTO를 명세합니다.
 *
 * 클라이언트에게 반환할 사용자 정보
 * ※ pw(비밀번호)는 보안상 제외합니다.
 * 추후 로그인, 회원가입, 사용자 정보 수정시 응답하는
 * 기능을 구현할때 이용하는 DTO 입니다.
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "사용자 정보 응답 DTO (로그인, 회원가입, 정보 조회 시 사용)")
public class LoginResp {
    @Schema(description = "사용자 번호", example = "1")
    private Long userNo;

    @Schema(description = "이메일", example = "user@example.com")
    private String userEmail;

    // private String userPw; 보안상 제외함.

    @Schema(description = "닉네임", example = "홍길동")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String userImg;

    @Schema(description = "가입일시", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "최종 수정일시", example = "2024-01-20T14:20:00")
    private LocalDateTime updatedAt;

    @Schema(description = "성별", example = "MALE")
    private String gender;

    @Schema(description = "사용자 역할", example = "USER")
    private String role;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;

    @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    @Schema(description = "권한이 CEO일 때 식당이 존재하는 지", example = "true")
    private boolean owner;
}