package com.ssafy.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 이 DTO는 태그 수정에 대한 DTO를 명세합니다.
 * 
 *  태그 수정
 *  1. tagId 	-> 태그Id
 *  2. tagName 	-> 태그명
 *  3. category -> 카테고리
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTagReq {
	long tagId;
	String tagName;
	String category;
}
