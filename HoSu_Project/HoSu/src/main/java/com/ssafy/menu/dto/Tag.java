package com.ssafy.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/*
 * 이 DTO는 메뉴에 대한 태그 DTO를 명세합니다.
 * 
 *  메뉴 태그
 *  1. tagId-> 태그 아이디
 *  2. tagName 	-> 태그명
 *  3. category -> 카테고리
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Tag {
	long tagId;
	String tagName;
	String category;
}
