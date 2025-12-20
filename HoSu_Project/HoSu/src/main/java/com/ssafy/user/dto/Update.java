package com.ssafy.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "사용자 정보 수정 요청 DTO")
public class Update {
    @Schema(description = "변경할 비밀번호 (선택)", example = "newPassword123!")
    private String userPw;

    @Schema(description = "변경할 닉네임 (선택, 중복 불가)", example = "새닉네임")
    private String nickname;

    @Schema(description = "변경할 전화번호 (선택)", example = "010-9876-5432")
    private String phoneNumber;

    @Schema(description = "변경할 프로필 이미지 URL (선택)", example = "https://example.com/profile.jpg")
    private String userImg;
}
