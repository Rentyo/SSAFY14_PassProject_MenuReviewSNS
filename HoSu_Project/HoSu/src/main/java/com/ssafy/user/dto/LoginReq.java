package com.ssafy.user.dto;
/*
 * 이 DTO는 로그인 요청용 DTO 정보를 명세합니다.
 * 
 *  로그인 요청에 필요한 정보
 *  1. userEmail-> ID
 *  2. userPw	-> 비밀번호
 * 
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "로그인 요청 DTO")
public class LoginReq {
    @Schema(description = "이메일 (로그인 ID)", example = "user@example.com", required = true)
    private String userEmail;

    @Schema(description = "비밀번호", example = "password123!", required = true)
    private String userPw;
}