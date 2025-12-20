package com.ssafy.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
 * 이 DTO는 공지 수정 DTO를 명세합니다.
 * 
 *  공지 수정 정보
 *  1. restaurantNoticeId 	-> 수정할 공지 번호
 *  2. title     			-> 공지 제목
 *  3. importance  			-> 중요도
 *  4. content	 			-> 공지 내용
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "식당 공지 수정 DTO ")
public class UpdateNoticeReq {
	@Schema(description = "공지 ID", example = "1")
	private long restaurantNoticeId;
	@Schema(description = "공지 제목", example = "신메뉴 추가 공지")
	private String title;
	@Schema(description = "중요도", example = "1 : 일반글, 2 : 중요, 3 : 긴급")
	private int importance;
	@Schema(description = "공지 내용", example = "신메뉴 불짜장 출시했습니다. 12/10까지 한정 판매")
	private String content;
}
