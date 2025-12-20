package com.ssafy.restaurant.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.ssafy.restaurant.dto.CheckRestaurantResp;
import com.ssafy.restaurant.dto.Notice;
import com.ssafy.restaurant.dto.Restaurant;
import com.ssafy.restaurant.dto.RestaurantDetailById;
import com.ssafy.restaurant.dto.RestaurantOpenRequest;
import com.ssafy.restaurant.dto.UpdateNoticeReq;
import com.ssafy.restaurant.dto.insertNoticeReq;
import com.ssafy.restaurant.service.RestaurantService;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/restaurants")
@Tag(name = "Restaurants", description = "식당 API(관리자 기능 같은 경우 권한이 필요하기 때문에 swagger에서 실행이 되질 않습니다.)")
public class RestaurantController {
	
	private final RestaurantService restaurantService;
	
	public RestaurantController(@Qualifier("RestaurantServiceImpl") RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }
	@GetMapping("/search")
	@Operation(
	    summary = "카카오 API 검색",
	    description = "키워드를 기반으로 주소를 검색합니다.",
	    parameters = {
	        @Parameter(
	            name = "keyword",
	            description = "주소 + 식당명",
	            required = true,
	            example = "대전 중구 중촌동 410-12 써니 파스타몽"
	        )
	    }
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "검색 성공"),
	    @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
	    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	public ResponseEntity<List<CheckRestaurantResp>> searchRestaurants(@RequestParam(required = true) String keyword) throws Exception{
		List<CheckRestaurantResp> results = restaurantService.search(keyword);
        return ResponseEntity.ok(results);
	}
	
	/** 
     * 관리자 기능 : 새로운 식당 입력
     * Post
     * RequestBody - Restaurant
     *  1. id(PK)	2. name		3. address		4. category		5. totalRating
     *  6. lat		7. lng		8. phone		9. mainImage	10.totalContents
     *  11.viewCount12 sidoNo	13 gunguCode
     */
	@PostMapping
	@Operation(
	    summary = "관리자 기능: 새로운 식당 생성",
	    description = "관리자 권한으로 새로운 식당 정보를 등록합니다.",
	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
	        description = "식당 정보 (Restaurant 객체)",
	        required = true
	    )
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "식당 등록 성공"),
	    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	    @ApiResponse(responseCode = "401", description = "관리자 인증 실패"),
	    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	public ResponseEntity<String> insertRestaurant(@RequestBody Restaurant restaurant) throws Exception {
		restaurantService.insertRestaurant(restaurant);
        return ResponseEntity.ok("성공");
    }
	
	/** 
     * 관리자 기능 : 식당 정보 수정
     * PUT
     * RequestBody - Restaurant
     *  1. id(PK)	2. name		3. address		4. category		5. totalRating
     *  6. lat		7. lng		8. phone		9. mainImage	10.totalContents
     *  11.viewCount12 sidoNo	13 gunguCode
     */
	@PutMapping("/{id}")
	@Operation(
	    summary = "관리자 기능: 식당 정보 수정",
	    description = "관리자 권한으로 특정 식당의 정보를 수정합니다.",
	    parameters = {
	        @Parameter(
	            name = "id",
	            description = "수정할 식당의 ID",
	            required = true,
	            example = "10"
	        )
	    },
	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
	        description = "수정할 식당 정보 (Restaurant 객체)",
	        required = true
	    )
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "식당 정보 수정 성공"),
	    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	    @ApiResponse(responseCode = "401", description = "관리자 인증 실패"),
	    @ApiResponse(responseCode = "404", description = "해당 식당을 찾을 수 없음"),
	    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
    public ResponseEntity<String> updateRestaurant(
            @PathVariable("id") Long id,
            @RequestBody Restaurant restaurant) throws Exception {
        restaurant.setId(id);  
        restaurantService.updateRestaurant(restaurant);

        return ResponseEntity.ok("성공");
    }
	
	/** 
     * 관리자 기능 : 식당 정보 삭제
     * DELETE /{id} 
     */
    @DeleteMapping("/{id}")
	@Operation(
	    summary = "관리자 기능: 식당 삭제",
	    description = "관리자 권한으로 특정 식당 데이터를 삭제합니다.",
	    parameters = {
	        @Parameter(
	            name = "id",
	            description = "삭제할 식당의 ID",
	            required = true,
	            example = "7"
	        )
	    }
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "식당 삭제 성공"),
	    @ApiResponse(responseCode = "401", description = "관리자 인증 실패"),
	    @ApiResponse(responseCode = "404", description = "삭제할 식당을 찾을 수 없음"),
	    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
    public ResponseEntity<String> deleteRestaurant(@PathVariable("id") Long id) throws Exception {
        restaurantService.deleteRestaurant(new Restaurant(id));
        return ResponseEntity.ok("삭제 성공: id=" + id);
    }
    
    
    /** 
     * 식당 주인 기능 : 식당 개시 요청
     * Post
     * RequestBody - Restaurant
     * 1. address - 주소
     * 2. category - 카테고리
     * 3. description - 가게 설명
     * 4. useNo - 식당 사장님 번호
     */
	@PostMapping("/open/request")
	@Operation(
	    summary = "식당 주인 기능: 식당 개시 요청",
	    description = "새로운 식당 정보를 등록 요청합니다.",
	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
	        description = "식당 정보 (Restaurant 객체)",
	        required = true
	    )
	)
	public ResponseEntity<String> insertOpenRequest(@RequestBody RestaurantOpenRequest req) throws Exception {
		restaurantService.insertOpenRequest(req);
        return ResponseEntity.ok("성공");
    }
	
	/** 
     * 사용자 기능 : 식당 정보 조회
     * Get
     * PathVariable
     * 1. restaurantId 식당 번호
     */
	@GetMapping("/{restaurantId}")
	@Operation(
	    summary = "식당 리스트 item을 클릭했을 시 조회",
	    description = "식당 정보를 조회합니다..",
		parameters = {
		        @Parameter(
		            name = "restaurantId",
		            description = "조회할 식당의 ID",
		            required = true,
		            example = "7"
		        )
		    }
	)
	public ResponseEntity<RestaurantDetailById> selectRestaurantById(@PathVariable Long restaurantId) throws Exception {
        return ResponseEntity.ok(restaurantService.selectRestaurantById(restaurantId));
    }
	
	/** 
     * 사장님 기능 : 유저 정보로 식당 번호 조회
     * Get
     * PathVariable
     * 1. userNo
     */
	@GetMapping("/restaurantId/{userNo}")
	@Operation(
	    summary = "사장님 마이 페이지에서 조회하기 전 식당 번호 필요",
	    description = "식당 번호를 조회합니다..",
		parameters = {
		        @Parameter(
		            name = "userNo",
		            description = "사용자 번호",
		            required = true,
		            example = "7"
		        )
		    }
	)
	public ResponseEntity<Long> selectRestaurantIdByUserNo(@PathVariable("userNo") Long userNo) throws Exception {
        return ResponseEntity.ok(restaurantService.selectRestaurantIdByUserNo(userNo));
    }
	
	
	/** 
     * 사장님 기능 : 식당 번호로 공지 조회
     * Get
     * PathVariable
     * 1. restaurantId
     */
	@GetMapping("/{restaurantId}/notice")
	@Operation(
	    summary = "사장님 마이 페이지 내 공지 조회",
	    description = "공지글들을 조회합니다.",
		parameters = {
		        @Parameter(
		            name = "restaurantId",
		            description = "식당 번호",
		            required = true,
		            example = "7"
		        )
		    }
	)
	public ResponseEntity<List<Notice>> selectNoticesIdByRestaurantId(@PathVariable("restaurantId") Long restaurantId) throws Exception {
        return ResponseEntity.ok(restaurantService.selectNoticesIdByRestaurantId(restaurantId));
    }
	
	
	
	
	/** 
     * 사장님 기능 : 공지 입력
     * Post
     * @RequestBody
     * 1. restaurantId
     * 2. title     	
     * 3. importance  	
     * 4. content
     * 
     */
	@PostMapping("/notice")
	@Operation(
	    summary = "사장님 공지글 게시",
	    description = "공지글들을 입력합니다."
	)
	public ResponseEntity<String> createNotice(@RequestBody insertNoticeReq req) throws Exception {
		restaurantService.insertNotice(req);
	    return ResponseEntity.ok("공지사항 등록 성공");
	}
	
	/** 
     * 사장님 기능 : 공지 수정
     * Put
     * @RequestBody
     * 1. restaurantNoticeId
     * 2. title     	
     * 3. importance  	
     * 4. content
     */
	@PutMapping("/notice")
	@Operation(
	    summary = "사장님 공지글 수정",
	    description = "공지글들을 수정합니다."
	)
	public ResponseEntity<String> updateNotice(@RequestBody UpdateNoticeReq req) throws Exception {
		restaurantService.updateNotice(req);
	    return ResponseEntity.ok("공지사항 수정 성공");
	}
	
	/** 
     * 사장님 기능 : 공지 삭제
     * Delete
     * PathVariable
     * 1. restaurantNoticeId
     */
	@DeleteMapping("/notice/{restaurantNoticeId}")
	@Operation(
	    summary = "사장님 공지글 삭제",
	    description = "공지글들을 삭제합니다.",
		parameters = {
		        @Parameter(
		            name = "restaurantNoticeId",
		            description = "공지 번호",
		            required = true,
		            example = "7"
		        )
		    }
	)
	public ResponseEntity<String> updateNotice(@PathVariable("restaurantNoticeId") long restaurantNoticeId) throws Exception {
		restaurantService.deleteNotice(restaurantNoticeId);
	    return ResponseEntity.ok("공지사항 수정 성공");
	}
	
	/** 
     * 공통 기능 : 식당 리스트 클릭 시 조회 수 증가
     * Put
     * @PathVariable
     * 1. restraurantId
     */
	@PutMapping("/viewCount/{restaurantId}")
	@Operation(
	    summary = "사장님 공지글 수정",
	    description = "공지글들을 수정합니다."
	)
	public ResponseEntity<String> viewCountUp(@PathVariable("restaurantId") Long restaurantId) throws Exception {
		restaurantService.viewCountUp(restaurantId);
	    return ResponseEntity.ok("조회수 증가");
	}
	
	/** 
     * 사장님 기능 : 마이 페이지에서 식당이 있는 지, 등록 중인 지, 아무것도 아닌 지 3가지 단계로 나눔
     * Get
     * PathVariable
     * 1. userNo
     */
	@GetMapping("/ceolevel/{userNo}")
	@Operation(
	    summary = "사장님 마이 페이지 내 CEO 단계를 반환합니다.",
	    description = "식당이 있는 상태라면 1, 식당이 없는 상태라면 0, 등록 요청 중이라면 -1을 반환합니다.",
		parameters = {
		        @Parameter(
		            name = "userNo",
		            description = "사용자 번호",
		            required = true,
		            example = "7"
		        )
		    }
	)
	public ResponseEntity<Integer> getCeoLevel(@PathVariable("userNo") Long userNo) throws Exception {
        return ResponseEntity.ok(restaurantService.getCeoLevel(userNo));
    }
	
	
	
	
	
	
}
