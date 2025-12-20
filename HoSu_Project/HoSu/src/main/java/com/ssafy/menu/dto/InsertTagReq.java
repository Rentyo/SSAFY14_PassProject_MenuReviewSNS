package com.ssafy.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 이 DTO는 태그 입력에 대한 DTO를 명세합니다.
 * 
 *  태그 입력
 *  1. tagName 	-> 태그명
 *  2. category -> 카테고리
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsertTagReq {
	long tagId;
	String tagName;
	String category;
}
