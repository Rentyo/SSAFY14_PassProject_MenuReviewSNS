package com.ssafy.aiSns.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.aiSns.service.AISnsService;
import com.ssafy.follow.service.FollowService;
import com.ssafy.restaurant.dto.UpdateNoticeReq;
import com.ssafy.sns.dto.SnsBoardResp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/aisns")
@Tag(name = "AISns", description = "SNS 검색 AI")
public class AISnsController {
	
	private AISnsService service;
	
	public AISnsController(@Qualifier("AISnsSesrviceImpl") AISnsService service) {
		this.service = service;
	}
	
	@GetMapping
    @Operation(summary = "AI로 SNS 게시물 검색", description = "AI ")
    @ApiResponses({
                    @ApiResponse(responseCode = "200", description = "게시물 목록 조회 성공", content = @Content(schema = @Schema(implementation = SnsBoardResp.class)))
    })
    public ResponseEntity<List<SnsBoardResp>> getBoardListByAI( 
    		@RequestParam("query") String query) throws Exception {
            return ResponseEntity.ok(service.getTop5BoardIds(query));
    }
	 
}
