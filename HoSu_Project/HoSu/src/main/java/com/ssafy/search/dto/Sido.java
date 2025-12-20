package com.ssafy.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 이 DTO는 검색 카테고리 중 시도에 대한 DTO를 명세합니다.
 * 
 *  시도 
 *  1. sidoNo   	-> 시도 코드 (PK)
 *  2. sidoName  	-> 시도명
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "시도 정보 DTO")
public class Sido {
	@Schema(description = "시도 번호", example = "1")
	private String sidoNo;
	@Schema(description = "시도 이름", example = "서울")
	private String sidoName;
}
