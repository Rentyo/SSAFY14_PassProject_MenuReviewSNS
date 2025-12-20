package com.ssafy.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchKeywords {
	private String sidoNo;
	private String gunguCode;
	private String category;
	private long tagId;
	private String tagName; // 메뉴 이름으로 검색
}
