package com.ssafy.sns.controller;

import com.ssafy.sns.dto.SnsLikeResp;
import com.ssafy.sns.service.SnsLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sns/likes")
@RequiredArgsConstructor
@Tag(name = "SNS Like", description = "SNS 좋아요 API")
public class SnsLikeController {

    private final SnsLikeService likeService;

    @PostMapping("/{boardId}/{userNo}")
    @Operation(summary = "좋아요 토글", description = "게시물의 좋아요를 토글합니다. 좋아요가 없으면 추가하고, 이미 있으면 취소합니다. 현재 좋아요 상태와 총 좋아요 수를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 토글 성공", content = @Content(schema = @Schema(implementation = SnsLikeResp.class)))
    })
    public ResponseEntity<SnsLikeResp> toggleLike(
            @Parameter(description = "게시물 ID", example = "1", required = true) @PathVariable Long boardId,
            @Parameter(description = "사용자 번호", example = "1", required = true) @PathVariable Long userNo) {
        SnsLikeResp response = likeService.toggleLike(boardId, userNo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{boardId}/count")
    @Operation(summary = "좋아요 수 조회", description = "게시물의 총 좋아요 수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 수 조회 성공", content = @Content(schema = @Schema(implementation = Integer.class)))
    })
    public ResponseEntity<Integer> getLikeCount(
            @Parameter(description = "게시물 ID", example = "1", required = true) @PathVariable Long boardId) {
        int count = likeService.getLikeCount(boardId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{boardId}/{userNo}")
    @Operation(summary = "좋아요 여부 확인", description = "특정 사용자가 게시물에 좋아요를 눌렀는지 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 여부 확인 성공", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ResponseEntity<Boolean> isLiked(
            @Parameter(description = "게시물 ID", example = "1", required = true) @PathVariable Long boardId,
            @Parameter(description = "사용자 번호", example = "1", required = true) @PathVariable Long userNo) {
        boolean isLiked = likeService.isLiked(boardId, userNo);
        return ResponseEntity.ok(isLiked);
    }
}