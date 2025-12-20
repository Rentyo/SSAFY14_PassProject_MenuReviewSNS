package com.ssafy.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 이 DTO는 메뉴 입력 요청 DTO를 명세합니다.
 * 
 *  식당 메뉴 요청
 *  1. restaurantId -> 메뉴를 보유한 식당
 *  2. menuName  	-> 메뉴명
 *  3. price	 	-> 가격
 *  4. description  -> 메뉴 설명
 *  5. tagIds 		-> 한 메뉴에 대한 여러 가지 태그
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class InsertMenuReq {
	long restaurantId;
	String menuName;
	int price;
	String description;
	long[] tagIds;
}
