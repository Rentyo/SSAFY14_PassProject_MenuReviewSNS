package com.ssafy.restaurant.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 이 DTO는 공지 DTO를 명세합니다.
 * 
 *  공지 정보
 *  1. restaurantNoticeId 	-> 공지 번호
 *  2. restaurantId     	-> 식당 번호
 *  3. title  				-> 공지 제목
 *  4. importance	 		-> 중요도
 *  5. content   			-> 공지 내용
 *  6. createdAt			-> 작성일
 *  7. viewCount 			-> 조회수
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "식당 공지 입력 DTO ")
public class Notice {
	@Schema(description = "공지 ID", example = "1")
	private long restaurantNoticeId;
	@Schema(description = "식당 ID", example = "1")
	private long restaurantId;
	@Schema(description = "공지 제목", example = "신메뉴 추가 공지")
	private String title;
	@Schema(description = "중요도", example = "1 : 일반글, 2 : 중요, 3 : 긴급")
	private int importance;
	@Schema(description = "공지 내용", example = "신메뉴 불짜장 출시했습니다. 12/10까지 한정 판매")
	private String content;
	@Schema(description = "작성일", example = "2025-12-10")
	private LocalDateTime createdAt;
	@Schema(description = "조회수", example = "1")
	private Long viewCount;
}
