package com.ssafy.menu.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 이 DTO는 식당 메뉴 DTO를 명세합니다.
 * 
 *  식당 메뉴
 *  1. menuId       -> 메뉴 번호 (PK)
 *  2. restaurantId -> 메뉴를 보유한 식당
 *  3. menuName  	-> 메뉴명
 *  4. price	 	-> 가격
 *  5. description  -> 메뉴 설명
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema
public class Menu {
	@Schema(description = "메뉴Id(메뉴명이 같더라도 식당마다 다름)", example = "1")
	long menuId;
	@Schema(description = "식당Id", example = "1")
	long restaurantId;
	@Schema(description = "메뉴명", example = "유산슬")
	String menuName;
	@Schema(description = "메뉴 가격", example = "15000")
	int price;
	@Schema(description = "메뉴 설명", example = "원산지 표시, 메뉴만의 특별한 점, 몇 인분 등등에 대한 설명")
	String description;
	@Schema(description = "총 평점" , example = "5.3")
	double totalRating;
	@Schema(description = "리뷰 메뉴 개수" , example = "60")
	long reviewCount;
	@Schema(description = "메뉴 태그")
	List<Tag> tags; 
	@Schema(description = "메뉴활성화여부")
	boolean isActive; 
}
