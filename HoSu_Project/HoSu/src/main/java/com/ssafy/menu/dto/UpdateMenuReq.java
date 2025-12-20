package com.ssafy.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 이 DTO는 메뉴 수정 DTO를 명세합니다.
 * 
 *  메뉴 수정 
 *  1. menuName		-> 메뉴명
 *  2. price 		-> 메뉴 가격
 *  3. description 	-> 메뉴 설명
 *  4. tagIds		-> 메뉴 태그s
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMenuReq {
	long menuId;
	String menuName;
	int price;
	String description;
	long[] tagIds;
}
