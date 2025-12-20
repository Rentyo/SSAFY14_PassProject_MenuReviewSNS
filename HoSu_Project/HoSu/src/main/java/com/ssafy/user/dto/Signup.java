package com.ssafy.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 이 DTO는 회원가입 요청용 DTO를 명세합니다.
 *
 * 회원가입에 필요한 정보
 * ※ userImg(프로필 사진)은 추후 업데이트 기능으로 처리
 * ※ createdAt, updatedAt은 서버에서 자동 생성
 * ※ userNo는 DB에서 자동 생성(AUTO_INCREMENT)
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class Signup {
    @Schema(description = "이메일 (로그인 ID로 사용)", example = "user@example.com", required = true)
    private String userEmail;

    @Schema(description = "비밀번호", example = "password123!", required = true)
    private String userPw;

    @Schema(description = "닉네임 (중복 불가)", example = "홍길동", required = true)
    private String nickname;

    @Schema(description = "성별", example = "MALE", allowableValues = { "MALE", "FEMALE", "OTHER" }, required = true)
    private String gender;

    @Schema(description = "사용자 역할 (기본값: USER)", example = "USER", allowableValues = { "USER",
            "ADMIN", "CEO" }, defaultValue = "USER")
    private String role;

    @Schema(description = "전화번호", example = "010-1234-5678", required = true)
    private String phoneNumber;

    @Schema(description = "사용자 번호 (자동 생성, 응답용)", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userNo; // insert 후 자동생성 값 받기용
}