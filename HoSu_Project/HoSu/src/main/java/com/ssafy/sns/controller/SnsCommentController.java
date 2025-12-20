package com.ssafy.sns.controller;

import com.ssafy.sns.dto.SnsComment;
import com.ssafy.sns.dto.SnsCommentReq;
import com.ssafy.sns.service.SnsCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sns/comments")
@RequiredArgsConstructor
@Tag(name = "SNS Comment", description = "SNS 댓글 API")
public class SnsCommentController {

        private final SnsCommentService commentService;

        @PostMapping
        @Operation(summary = "댓글 작성", description = "게시물에 댓글을 작성합니다. parentId를 설정하면 대댓글로 작성됩니다. 성공 시 생성된 댓글 ID를 반환합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "댓글 작성 성공", content = @Content(schema = @Schema(implementation = Long.class))),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content)
        })
        public ResponseEntity<Long> createComment(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "댓글 작성 정보 (게시물 ID, 사용자 번호, 내용, 대댓글이면 parentId)", required = true) @RequestBody SnsCommentReq req) {
                Long commentId = commentService.createComment(req);
                return ResponseEntity.status(HttpStatus.CREATED).body(commentId);
        }

        @PutMapping("/{commentId}/{userNo}")
        @Operation(summary = "댓글 수정", description = "댓글의 내용을 수정합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
                        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음", content = @Content)
        })
        public ResponseEntity<Void> updateComment(
                        @Parameter(description = "수정할 댓글 ID", example = "1", required = true) @PathVariable Long commentId,
                        @Parameter(description = "사용자 번호", example = "1", required = true) @PathVariable Long userNo,
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "수정할 댓글 내용", required = true) @RequestBody java.util.Map<String, String> body) {
                String content = body.get("content");
                commentService.updateComment(commentId, userNo, content);
                return ResponseEntity.ok().build();
        }

        @DeleteMapping("/{commentId}/{userNo}")
        @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다. 대댓글도 함께 삭제됩니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
                        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음", content = @Content)
        })
        public ResponseEntity<Void> deleteComment(
                        @Parameter(description = "삭제할 댓글 ID", example = "1", required = true) @PathVariable Long commentId,
                        @Parameter(description = "사용자 번호", example = "1", required = true) @PathVariable Long userNo) {
                commentService.deleteComment(commentId, userNo);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/board/{boardId}")
        @Operation(summary = "댓글 목록 조회", description = "게시물의 모든 댓글을 조회합니다. 댓글과 대댓글이 계층 구조로 반환됩니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공", content = @Content(schema = @Schema(implementation = SnsComment.class)))
        })
        public ResponseEntity<List<SnsComment>> getCommentsByBoardId(
                        @Parameter(description = "댓글을 조회할 게시물 ID", example = "1", required = true) @PathVariable Long boardId) {
                List<SnsComment> comments = commentService.getCommentsByBoardId(boardId);
                return ResponseEntity.ok(comments);
        }
}
