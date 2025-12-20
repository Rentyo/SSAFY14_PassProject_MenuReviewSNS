package com.ssafy.user.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "사용자 권한 응답 DTO ")
public class UserRole {
	@Schema(description = "사용자 번호", example = "1")
    private Long userNo;
	
	@Schema(description = "사용자 메일", example = "user@user.com")
	private String userEmail;

    @Schema(description = "닉네임", example = "홍길동")
    private String nickname;
    
    @Schema(description = "가입일시", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "프로필 사진")
    private String userImg;
    
    @Schema(description = "권한", example = "USER")
    private String role;
    
    @Schema(description = "작성한 리뷰 수" , example = "1")
    private Long reviewCount;
    
    @Schema(description = "팔로워 수" , example = "1")
    private Long followerCount;
    

}
