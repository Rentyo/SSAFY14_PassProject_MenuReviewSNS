package com.ssafy.restaurant.dto;

import java.util.List;

import com.ssafy.sns.dto.SnsBoardInRestaurant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 이 DTO는 식당 정보에 대한 응답용 DTO를 명세합니다.
 * 식당 리스트에서 식당을 클릭했을 시 식당 정보에 대한 페이지로 넘어가고 해당 페이지에 필요한 DTO를 명시합니다.
 * 1. id : 식당 고유 번호
 * 2. name : 식당 이름
 * 3. address : 식당 주소
 * 4. category : 식당 카테고리
 * 5. avgRating : 식당 평균 평점
 * 6. lat : 식당 위치(위도) -> 카카오 map API에 활용
 * 7. lng : 식당 위치(경도) -> 카카오 map API에 활용
 * 8. phone : 식당 전화 번호
 * 9. mainImage : 식당 상세 페이지에 명시할 이미지
 * 10. totalContent : 리뷰 수
 * 11. viewCount: 조회 수
 * 12. boards : 리뷰
 * 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDetailById {
	@Schema(description = "식당 고유 번호", example = "1")
	private Long id;
	@Schema(description = "식당 이름", example = "파스타 몽")
	private String name;
	@Schema(description = "식당 주소", example = "대전 중구 중촌동 410-12")
	private String address;
	@Schema(description = "카테고리", example = "WESTERN")
	private String category;
	@Schema(description = "전메뉴 총 평점", example = "3.5")
	private double totalRating;
	@Schema(description = "위도")
	private double lat;
	@Schema(description = "경도")
	private double lng;
	@Schema(description = "식당 전화번호", example = "042-xxx-xxxx")
	private String phone;
	@Schema(description = "식당 메인 이미지")
	private String mainImage;
	@Schema(description = "식당 리뷰 총 개수")
	private int totalContents;
	@Schema(description = "조회수")
	private int viewCount;
	@Schema(description = "게시물에 게시된 메뉴 수")
	private Long reviewCounts;
	@Schema(description = "게시물(리뷰)")
	private List<SnsBoardInRestaurant> boards;
	@Schema(description = "식당 주인 no")
	private Long userNo;
}
