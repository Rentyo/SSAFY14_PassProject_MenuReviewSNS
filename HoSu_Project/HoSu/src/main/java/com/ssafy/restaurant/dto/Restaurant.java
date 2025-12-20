package com.ssafy.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 이 DTO는 맛집 정보 DTO를 명세합니다.
 * 
 *  맛집 정보
 *  1. id       	-> 맛집 번호 (PK)
 *  2. name     	-> 상호명
 *  3. address  	-> 주소
 *  4. category	 	-> 카테고리
 *  5. avgRating   	-> 평점
 *  6. lat			-> 식당 위치(위도)
 *  7. lng 			-> 식당 위치(경도)
 *  8. phone		-> 가게 전화번호
 *  9. mainImage	-> 가게 대표이미지
 *  10.totalContents-> 리뷰 수
 *  11.viewCounts	-> 열럼 수
 *  12 sidoNo 		-> 시도 코드
 *  13 gunguCode	-> 군구 코드
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Restaurant {
	private Long id;
	private String name;
	private String address;
	private String category;
	private double totalRating;
	private double lat;
	private double lng;
	private String phone;
	private String mainImage;
	private int totalContents;
	private int viewCount;
	private String sidoNo;
	private String gunguCode;
	private Long userNo;
	private Long reviewCounts;
	
	public Restaurant(Long id) {
		this.id = id;
	}

}
