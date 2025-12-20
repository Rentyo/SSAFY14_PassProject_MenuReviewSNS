package com.ssafy.search.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.menu.dto.Tag;
import com.ssafy.search.dto.Gungu;
import com.ssafy.search.dto.SearchKeywords;
import com.ssafy.search.dto.SearchRestaurant;
import com.ssafy.search.dto.SearchRestaurantPageResp;
import com.ssafy.search.dto.Sido;
import com.ssafy.search.service.SearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/search")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Search", description = "식당 검색 API")
public class SearchController {

	private final SearchService service;

	public SearchController(@Qualifier("searchServiceImpl") SearchService service) {
		this.service = service;
	}

	/**
	 * 시도 리스트 전체 조회(첫 번째 checkbox)
	 * GET /sido
	 */
	@GetMapping("/sido")
	@Operation(summary = "지역(시도) SelectBox Items", description = "검색 페이지가 나올 때 지역(시도) 데이터를 받습니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "시도 리스트 조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Sido.class)))),
			@ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
	})
	public ResponseEntity<List<Sido>> getSidoList() throws Exception {
		List<Sido> list = service.getSidoList();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	/**
	 * 특정 시도의 군구 리스트 조회(첫 번째 checkbox를 선택할 시)
	 * GET /gungu/1
	 */
	@Operation(summary = "지역(군구) SelectBox Items", description = "지역(시도) SelectBox Item 선택 시 지역(군구) 데이터를 받습니다. ", parameters = {
			@Parameter(name = "sidoNo", description = "시도 코드(예: 1=서울)", required = true, example = "1")
	})
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "군구 리스트 조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Gungu.class)))),
			@ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 sidoNo)", content = @Content),
			@ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
	})
	@GetMapping("/gungu/{sidoNo}")
	public ResponseEntity<List<Gungu>> getGunguList(@PathVariable("sidoNo") String sidoNo) throws Exception {
		Sido sido = new Sido(sidoNo, "");

		List<Gungu> list = service.getGunguList(sido);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	/**
	 * 특정 카테고리의 태그 리스트 조회(세 번째 checkbox를 선택할 시)
	 * GET /tag?category=KOREAN
	 */
	@GetMapping("/tag/{category}")
	@Operation(summary = "메뉴 태그 SelectBox Items", description = "카테고리 SelectBox Item 선택 시 해당 카테고리에 해당하는 메뉴 태그 데이터를 받습니다. ", parameters = {
			@Parameter(name = "category", description = "식당 카테고리 (예: KOREAN, JAPANESE, CAFE 등)", required = true, example = "KOREAN"),
	})
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "카테고리에 해당하는 메뉴 태그 리스트 조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Tag.class)))),
			@ApiResponse(responseCode = "400", description = "잘못된 카테고리 형식 또는 유효하지 않은 요청 파라미터"),
			@ApiResponse(responseCode = "404", description = "해당 카테고리의 메뉴 태그를 찾을 수 없음"),
			@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	public ResponseEntity<List<Tag>> getMenuTagListFromCategory(@PathVariable("category") String category)
			throws Exception {
		List<Tag> list = service.getMenuTagList(category);
		return new ResponseEntity<>(list, HttpStatus.OK);

	}

	/**
	 * 전체 메뉴 태그 리스트 조회
	 * GET /tag
	 */
	@Operation(summary = "전체 메뉴 태그 조회", description = "등록된 모든 메뉴 태그(Tag) 데이터를 조회합니다. "
			+ "각 태그는 tagId, tagName의 정보를 포함합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "메뉴 태그 리스트 조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Tag.class)))),
			@ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(mediaType = "application/json"))
	})
	@GetMapping("/tag")
	public ResponseEntity<List<Tag>> getMenuTagList() throws Exception {
		List<Tag> list = service.getMenuTagListAll();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	/**
	 * 4개의 SelectBox 데이터를 통해 검색
	 * GET /restaurant/{sidoId}/{gunguCode}/{category}/{tagId}
	 */
	@Operation(summary = "식당 검색", description = "시도, 군구, 카테고리, 태그ID를 이용하여 식당을 검색합니다.", parameters = {
			@Parameter(name = "sidoNo", description = "시도 코드(예: 1=서울)", required = true, example = "1"),
			@Parameter(name = "gunguCode", description = "군구 코드(예: 1=강남구)", required = true, example = "1"),
			@Parameter(name = "category", description = "식당 카테고리 (예: KOREAN, JAPANESE, CAFE 등)", required = true, example = "KOREAN"),
			@Parameter(name = "tagId", description = "메뉴 태그 ID (예: 1=찌개, 2=제육 등)", required = true, example = "1"),
			@Parameter(name = "gunguName", description = "군구 이름 (AI 검색용, 예: 동구, 강남구)", required = false, example = "동구")
	})
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "성공적으로 식당을 조회했습니다.", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SearchRestaurant.class)))),
			@ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터입니다."),
			@ApiResponse(responseCode = "404", description = "조건에 맞는 식당을 찾을 수 없습니다."),
			@ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.")
	})
	@GetMapping("/restaurant/{sidoNo}/{gunguCode}/{category}/{tagId}")
	public ResponseEntity<SearchRestaurantPageResp> searchRestaurantList(
			@PathVariable("sidoNo") String sidoNo,
			@PathVariable("gunguCode") String gunguCode,
			@PathVariable("category") String category,
			@PathVariable("tagId") long tagId,
			@RequestParam(value = "gunguName", required = false) String gunguName,
			@RequestParam(value = "tagName", required = false) String tagName,
			@RequestParam(value = "sortBy", required = false) String sortBy,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "LATEST") String sort) throws Exception {

		// gunguName이 있으면 gunguCode로 변환
		if (gunguName != null && !gunguName.isEmpty() && sidoNo != null && !sidoNo.equals("not")) {
			try {
				Gungu gungu = service.getGunguToName(sidoNo, gunguName);
				if (gungu != null) {
					gunguCode = gungu.getGunguCode();
				}
			} catch (Exception e) {
				// 변환 실패 시 gunguCode 그대로 사용
			}
		}

		// tagName이 있으면 tagId로 변환
		if (tagName != null && !tagName.isEmpty()) {
			try {
				Tag tag = service.getMenuTagToName(tagName);
				if (tag != null) {
					tagId = tag.getTagId();
				}
			} catch (Exception e) {
				// 변환 실패 시 tagId 그대로 사용
			}
		}

		// sortBy가 있으면 sort로 변환
		if (sortBy != null && !sortBy.isEmpty()) {
			sort = sortBy;
		}

		SearchRestaurantPageResp result = service
				.searchRestaurantList(new SearchKeywords(sidoNo, gunguCode, category, tagId, tagName), page, size,
						sort);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
