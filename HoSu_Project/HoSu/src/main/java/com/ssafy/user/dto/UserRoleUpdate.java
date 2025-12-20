package com.ssafy.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "사용자 권한 수정 DTO ")
public class UserRoleUpdate {
	@Schema(description = "권한", example = "U")
	@Pattern(regexp = "U|A|C", message = "role 값은 U(사용자), A(관리자), C(식당 사장님) 중 하나여야 합니다.")
    private String role;  // U, A, C
	
}
