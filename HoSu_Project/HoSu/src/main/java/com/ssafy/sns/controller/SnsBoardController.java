package com.ssafy.sns.controller;

import com.ssafy.sns.dto.*;
import com.ssafy.sns.service.SnsBoardService;
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
@RequestMapping("/api/sns/boards")
@RequiredArgsConstructor
@Tag(name = "SNS Board", description = "SNS 게시물 API")
public class SnsBoardController {

        private final SnsBoardService boardService;

        @PostMapping
        @Operation(summary = "게시물 작성", description = "SNS 게시물을 작성합니다. 내용과 이미지 URL 목록을 포함할 수 있습니다. 성공 시 생성된 게시물 ID를 반환합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "게시물 작성 성공", content = @Content(schema = @Schema(implementation = Long.class))),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content)
        })
        public ResponseEntity<Long> createBoard(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "게시물 작성 정보 (사용자 번호, 내용, 이미지 URL 목록)", required = true) @RequestBody SnsBoardReq req) {
        		Long boardId = boardService.createBoard(req);
                return ResponseEntity.status(HttpStatus.CREATED).body(boardId);
        }

        @PutMapping("/{boardId}/{userNo}")
        @Operation(summary = "게시물 수정", description = "게시물의 내용과 이미지를 수정합니다. **작성자만 수정할 수 있습니다.**")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "게시물 수정 성공"),
                        @ApiResponse(responseCode = "403", description = "권한 없음 (작성자가 아님)", content = @Content),
                        @ApiResponse(responseCode = "404", description = "게시물을 찾을 수 없음", content = @Content)
        })
        public ResponseEntity<Void> updateBoard(
                        @Parameter(description = "수정할 게시물 ID", example = "1", required = true) @PathVariable Long boardId,
                        @Parameter(description = "사용자 번호", example = "1", required = true) @PathVariable Long userNo,
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "수정할 게시물 정보", required = true) @RequestBody SnsBoardUpdateReq req) {
                boardService.updateBoard(boardId, userNo, req);
                return ResponseEntity.ok().build();
        }

        @DeleteMapping("/{boardId}/{userNo}")
        @Operation(summary = "게시물 삭제", description = "게시물을 삭제합니다. 관련된 이미지, 댓글, 좋아요도 함께 삭제됩니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "게시물 삭제 성공"),
                        @ApiResponse(responseCode = "404", description = "게시물을 찾을 수 없음", content = @Content)
        })
        public ResponseEntity<Void> deleteBoard(
                        @Parameter(description = "삭제할 게시물 ID", example = "1", required = true) @PathVariable Long boardId,
                        @Parameter(description = "사용자 번호", example = "1", required = true) @PathVariable Long userNo) {
                boardService.deleteBoard(boardId, userNo);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/{boardId}")
        @Operation(summary = "게시물 상세 조회", description = "게시물의 상세 정보를 조회합니다. 이미지, 댓글(대댓글 포함), 좋아요 정보를 포함합니다. myUserNo를 전달하면 내가 좋아요를 눌렀는지 확인할 수 있습니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "게시물 조회 성공", content = @Content(schema = @Schema(implementation = SnsBoardResp.class))),
                        @ApiResponse(responseCode = "404", description = "게시물을 찾을 수 없음", content = @Content)
        })
        public ResponseEntity<SnsBoardResp> getBoard(
                        @Parameter(description = "조회할 게시물 ID", example = "1", required = true) @PathVariable Long boardId,
                        @Parameter(description = "현재 로그인한 사용자 번호 (좋아요 여부 확인용, 선택)", example = "1") @RequestParam(required = false) Long myUserNo) {
                SnsBoardResp board = boardService.getBoard(boardId, myUserNo);
                return ResponseEntity.ok(board);
        }

        @GetMapping
        @Operation(summary = "게시물 목록 조회 (피드)", description = "모든 게시물 목록을 최신순으로 조회합니다. 페이지네이션을 지원합니다. myUserNo를 전달하면 각 게시물에 대한 내 좋아요 여부를 확인할 수 있습니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "게시물 목록 조회 성공", content = @Content(schema = @Schema(implementation = SnsBoardResp.class)))
        })
        public ResponseEntity<List<SnsBoardResp>> getBoardList(
                        @Parameter(description = "현재 로그인한 사용자 번호 (좋아요 여부 확인용, 선택)", example = "1") @RequestParam(required = false) Long myUserNo,
                        @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "페이지당 게시물 수", example = "10") @RequestParam(defaultValue = "10") int size) {
                List<SnsBoardResp> boards = boardService.getBoardList(myUserNo, page, size);
                return ResponseEntity.ok(boards);
        }

        @GetMapping("/user/{userNo}")
        @Operation(summary = "유저 게시물 목록 조회", description = "특정 사용자가 작성한 게시물 목록을 조회합니다. 프로필 페이지에서 사용됩니다. myUserNo를 전달하면 각 게시물에 대한 내 좋아요 여부를 확인할 수 있습니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "유저 게시물 목록 조회 성공", content = @Content(schema = @Schema(implementation = SnsBoardResp.class)))
        })
        public ResponseEntity<List<SnsBoardResp>> getBoardListByUser(
                        @Parameter(description = "게시물을 조회할 사용자 번호", example = "1", required = true) @PathVariable Long userNo,
                        @Parameter(description = "현재 로그인한 사용자 번호 (좋아요 여부 확인용, 선택)", example = "2") @RequestParam(required = false) Long myUserNo) {
                List<SnsBoardResp> boards = boardService.getBoardListByUser(userNo, myUserNo);
                return ResponseEntity.ok(boards);
        }

        @GetMapping("/following/{userNo}")
        @Operation(summary = "팔로잉 피드 조회", description = "내가 팔로우한 사용자들의 게시물을 최신순으로 조회합니다. 페이지네이션을 지원합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "팔로잉 피드 조회 성공", content = @Content(schema = @Schema(implementation = SnsBoardResp.class)))
        })
        public ResponseEntity<List<SnsBoardResp>> getFollowingFeed(
                        @Parameter(description = "현재 로그인한 사용자 번호", example = "1", required = true) @PathVariable Long userNo,
                        @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "페이지당 게시물 수", example = "20") @RequestParam(defaultValue = "20") int size) {
                List<SnsBoardResp> boards = boardService.getFollowingFeed(userNo, page, size);
                return ResponseEntity.ok(boards);
        }

        @GetMapping("/restaurant/{restaurantId}")
        @Operation(summary = "식당별 게시물 목록 조회", description = "특정 식당에 대한 리뷰(게시물) 목록을 조회합니다. myUserNo를 전달하면 각 게시물에 대한 내 좋아요 여부를 확인할 수 있습니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "식당 게시물 목록 조회 성공", content = @Content(schema = @Schema(implementation = SnsBoardResp.class)))
        })
        public ResponseEntity<List<SnsBoardResp>> getBoardListByRestaurant(
                        @Parameter(description = "식당 ID", example = "5", required = true) @PathVariable Long restaurantId,
                        @Parameter(description = "현재 로그인한 사용자 번호 (좋아요 여부 확인용, 선택)", example = "1") @RequestParam(required = false) Long myUserNo) {
                List<SnsBoardResp> boards = boardService.getBoardListByRestaurant(restaurantId, myUserNo);
                return ResponseEntity.ok(boards);
        }

        @GetMapping("/random")
        public ResponseEntity<List<SnsBoardResp>> getRandomBoards(
                        @RequestParam(required = false) Long myUserNo,
                        @RequestParam(defaultValue = "20") int limit) {
                List<SnsBoardResp> boards = boardService.getRandomBoards(myUserNo, limit);
                return ResponseEntity.ok(boards);
        }
}