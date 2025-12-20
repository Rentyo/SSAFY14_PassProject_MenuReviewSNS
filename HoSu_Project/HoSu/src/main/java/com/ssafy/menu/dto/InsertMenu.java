package com.ssafy.menu.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 이 DTO는 메뉴 입력 DTO를 명세합니다.
 * 
 *  식당 메뉴
 *  1. restaurantId -> 메뉴를 보유한 식당
 *  2. menuName  	-> 메뉴명
 *  3. price	 	-> 가격
 *  4. description  -> 메뉴 설명
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InsertMenu {
	long menuId;
	long restaurantId;
	String menuName;
	int price;
	String description;
}
