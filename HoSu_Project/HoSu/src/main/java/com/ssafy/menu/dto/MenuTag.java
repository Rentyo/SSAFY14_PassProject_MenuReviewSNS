package com.ssafy.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 이 DTO는 메뉴와 태그를 매핑하는 DTO를 명세합니다.
 * 
 *  메뉴 태그 매핑
 *  1. mappingId-> 메뉴와 태그를 매핑하는 키 (PK)
 *  2. menuId 	-> 메뉴아이디
 *  3. tagId  	-> 태그아이디
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuTag {
	long mappingId;
	long menuId;
	long tagId;
}
