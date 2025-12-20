package com.ssafy.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 이 DTO는 검색된 맛집 DTO를 명세합니다.
 * 
 *  검색 결과
 *  1. id       	-> 맛집 번호 (PK)
 *  2. name     	-> 상호명
 *  3. category		-> 카테고리
 *  4. address		-> 주소
 *  5. lat   		-> 식당 위치(위도)
 *  6. long			-> 식당 위치(경도)
 *  7. phone 		-> 가게 전화번호
 *  8. mainImage	-> 가게 대표이미지
 *  9. totalRating	-> 평점 
 *  10.totalContents-> 리뷰 수
 *  11.viewCounts	-> 열럼 수
 *  12 reviewCounts -> 메뉴마다의 리뷰 수
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRestaurant {
	private Long id;
	private String name;
	private String category;
	private String address;
	private double lat;
	private double lng;
	private String phone;
	private String mainImage;
	private double totalRating;
	private int totalContents;
	private int viewCount;
	private long reviewCounts;
}
