package com.ssafy.restaurant.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 이 DTO는 공지 입력 DTO를 명세합니다.
 * 
 *  공지 입력 정보
 *  1. restaurantId -> 식당 번호
 *  2. title     	-> 공지 제목
 *  3. importance  	-> 중요도
 *  4. content	 	-> 공지 내용
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "식당 공지 입력 DTO ")
public class insertNoticeReq {
	@Schema(description = "식당 ID", example = "1")
	private long restaurantId;
	@Schema(description = "공지 제목", example = "신메뉴 추가 공지")
	private String title;
	@Schema(description = "중요도", example = "1 : 일반글, 2 : 중요, 3 : 긴급")
	private int importance;
	@Schema(description = "공지 내용", example = "신메뉴 불짜장 출시했습니다. 12/10까지 한정 판매")
	private String content;
}
