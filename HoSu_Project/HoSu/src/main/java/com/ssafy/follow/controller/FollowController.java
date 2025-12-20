package com.ssafy.follow.controller;

import com.ssafy.follow.dto.*;
import com.ssafy.follow.service.FollowService;
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

import java.util.List;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
@Tag(name = "Follow", description = "팔로우 관리 API")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{myUserNo}/{targetUserNo}")
    @Operation(summary = "팔로우", description = "다른 사용자를 팔로우합니다. 이미 팔로우 중이거나 자기 자신을 팔로우하려는 경우 실패합니다. 성공 시 업데이트된 팔로우 통계를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "팔로우 성공 또는 실패 (success 필드로 구분)", content = @Content(schema = @Schema(implementation = FollowActionResp.class)))
    })
    public ResponseEntity<FollowActionResp> follow(
            @Parameter(description = "팔로우하는 사용자 번호 (나)", example = "1", required = true) @PathVariable Long myUserNo,
            @Parameter(description = "팔로우 대상 사용자 번호", example = "2", required = true) @PathVariable Long targetUserNo) {

        FollowActionResp response = followService.follow(myUserNo, targetUserNo);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{myUserNo}/{targetUserNo}")
    @Operation(summary = "언팔로우", description = "팔로우를 취소합니다. 팔로우 관계가 없는 경우 실패합니다. 성공 시 업데이트된 팔로우 통계를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "언팔로우 성공 또는 실패 (success 필드로 구분)", content = @Content(schema = @Schema(implementation = FollowActionResp.class)))
    })
    public ResponseEntity<FollowActionResp> unfollow(
            @Parameter(description = "언팔로우하는 사용자 번호 (나)", example = "1", required = true) @PathVariable Long myUserNo,
            @Parameter(description = "언팔로우 대상 사용자 번호", example = "2", required = true) @PathVariable Long targetUserNo) {

        FollowActionResp response = followService.unfollow(myUserNo, targetUserNo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userNo}/followers")
    @Operation(summary = "팔로워 목록 조회", description = "나를 팔로우하는 사용자 목록을 조회합니다. myUserNo를 전달하면 각 팔로워와의 맞팔 여부를 확인할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "팔로워 목록 조회 성공", content = @Content(schema = @Schema(implementation = FollowUser.class)))
    })
    public ResponseEntity<List<FollowUser>> getFollowers(
            @Parameter(description = "팔로워 목록을 조회할 사용자 번호", example = "1", required = true) @PathVariable Long userNo,
            @Parameter(description = "현재 로그인한 사용자 번호 (맞팔 확인용, 선택)", example = "2") @RequestParam(required = false) Long myUserNo) {

        List<FollowUser> followers = followService.getFollowers(userNo, myUserNo);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userNo}/following")
    @Operation(summary = "팔로잉 목록 조회", description = "내가 팔로우하는 사용자 목록을 조회합니다. myUserNo를 전달하면 각 사용자와의 맞팔 여부를 확인할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "팔로잉 목록 조회 성공", content = @Content(schema = @Schema(implementation = FollowUser.class)))
    })
    public ResponseEntity<List<FollowUser>> getFollowing(
            @Parameter(description = "팔로잉 목록을 조회할 사용자 번호", example = "1", required = true) @PathVariable Long userNo,
            @Parameter(description = "현재 로그인한 사용자 번호 (맞팔 확인용, 선택)", example = "2") @RequestParam(required = false) Long myUserNo) {

        List<FollowUser> following = followService.getFollowing(userNo, myUserNo);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/{userNo}/stats")
    @Operation(summary = "팔로우 통계 조회", description = "사용자의 팔로워 수와 팔로잉 수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "통계 조회 성공", content = @Content(schema = @Schema(implementation = FollowStats.class)))
    })
    public ResponseEntity<FollowStats> getFollowStats(
            @Parameter(description = "통계를 조회할 사용자 번호", example = "1", required = true) @PathVariable Long userNo) {
        FollowStats stats = followService.getFollowStats(userNo);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{myUserNo}/status/{targetUserNo}")
    @Operation(summary = "팔로우 상태 확인", description = "두 사용자 간의 팔로우 관계를 확인합니다. 내가 상대방을 팔로우하는지, 상대방이 나를 팔로우하는지(맞팔 여부) 등을 확인할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "팔로우 상태 조회 성공", content = @Content(schema = @Schema(implementation = FollowStatus.class)))
    })
    public ResponseEntity<FollowStatus> getFollowStatus(
            @Parameter(description = "기준 사용자 번호 (나)", example = "1", required = true) @PathVariable Long myUserNo,
            @Parameter(description = "대상 사용자 번호", example = "2", required = true) @PathVariable Long targetUserNo) {

        FollowStatus status = followService.getFollowStatus(myUserNo, targetUserNo);
        return ResponseEntity.ok(status);
    }
}