package com.ssafy.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
 * 이 DTO는 검색 카테고리 중 군구에 대한 DTO를 명세합니다.
 * 
 *  군구 
 *  1. sidoNo   	-> 시도 코드 (PK)
 *  2. gunguCode	-> 군구 코드 (PK)
 *  3. gunguName  	-> 군구명
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "군구 정보 DTO")
public class Gungu {
	
	@Schema(description = "시도 번호", example = "1")
	private String sidoNo;
	
	@Schema(description = "군구 코드", example = "1")
	private String gunguCode;
	
	@Schema(description = "군구명", example = "강남구")
	private String gunguName;
	
}
