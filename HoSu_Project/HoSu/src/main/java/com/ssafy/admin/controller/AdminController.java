package com.ssafy.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.admin.dto.OpenRequest;
import com.ssafy.admin.dto.OpenRequestPage;
import com.ssafy.admin.service.AdminService;
import com.ssafy.restaurant.dto.Restaurant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "관리자 페이지 기능(관리자 기능 같은 경우 권한이 필요하기 때문에 swagger에서 실행이 되질 않습니다.)")
public class AdminController {
	
	private AdminService service;
	public AdminController(@Qualifier("AdminServiceImpl") AdminService service) {
		this.service = service;
	}
	
	/** 
     * 관리자 페이지 : 요청 전체 조회
     * Get
     */
	@GetMapping("/restaurants/requests")
	@Operation(
	    summary = "관리자 기능: 요청 전체 조회",
	    description = "관리자 권한으로 모든 식당 등록 요청을 조회합니다."
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "식당 등록 요청 조회 성공"),
	    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	    @ApiResponse(responseCode = "401", description = "관리자 인증 실패"),
	    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	public ResponseEntity<OpenRequestPage> selectOpenRequests(
			@RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) String type) throws Exception {
		OpenRequestPage result = service.getOpenRequests(page, size, type);
        return ResponseEntity.ok(result);
    }
	
	/** 
     * 관리자 페이지 : 요청 승인
     * Get
     */
	@PutMapping("/restaurants/approve/{reqId}")
	@Operation(
	    summary = "관리자 기능: 요청 승인",
	    description = "관리자 권한으로 해당 식당 등록 요청 승인합니다."
	    		+ "Response Body\r\n"
	    		+ "1 : 성공\r\n"
	    		+ "2 : 승인 실패\r\n"
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "식당 요청 승인 성공"),
	    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	    @ApiResponse(responseCode = "401", description = "관리자 인증 실패"),
	    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	public ResponseEntity<Integer> approveRequest (
			@PathVariable("reqId") long reqId)
			 throws Exception {
		int result = service.approveOpenRequests(reqId);
        return result != 0 ? ResponseEntity.ok(1) : ResponseEntity.ok(2) ;
    }
	
	/** 
     * 관리자 페이지 : 요청 거절
     * Get
     */
	@PutMapping("/restaurants/reject/{reqId}")
	@Operation(
	    summary = "관리자 기능: 요청 거절",
	    description = "관리자 권한으로 해당 식당 등록 요청 거절합니다."
	    		+ "Response Body\r\n"
	    		+ "-1 : 성공\r\n"
	    		+ "3 : 승인 실패\r\n"
	)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "식당 등록 요청 거절"),
	    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	    @ApiResponse(responseCode = "401", description = "관리자 인증 실패"),
	    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	public ResponseEntity<Integer> rejectRequest (
			@PathVariable("reqId") long reqId)
			 throws Exception {
		int result = service.rejectRequests(reqId);
		return result != 0 ? ResponseEntity.ok(-1) : ResponseEntity.ok(3);
    }
	
}
