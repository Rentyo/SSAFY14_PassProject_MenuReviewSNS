package com.ssafy.restaurant.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "식당 개시 요청 DTO ")
public class RestaurantOpenRequest {
		
		@Schema(description = "주소", example = "대전 중구 중촌동 410-12 1층 써니 파스타 몽")
		private String address;

	    @Schema(description = "카테고리", example = "KOREAN")
	    private String category;
	    
	    @Schema(description = "사장님 번호", example = "1")
	    private Long userNo;
	    
	    @Schema(description = "가게 설명", example = "어머니의 손맛을 그대로 재현하려 노력하는 양식 전문점입니다.")
	    private String description;
}
