package com.ssafy.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedQuery {
    private String sidoNo; // 시도 번호
    private String gunguCode; // 군구 코드
    private String gunguName; // 군구 이름 (AI가 추출)
    private String category; // 카테고리 (KOREAN, JAPANESE 등)
    private Integer tagId; // 메뉴 태그 ID
    private String tagName; // 메뉴 태그 이름 (AI가 추출, 예: 피자, 파스타)
    private String sortBy; // 정렬 기준 (LATEST, RATING, REVIEW, VIEW)
    private String keyword; // 키워드 (폴백용)
}
