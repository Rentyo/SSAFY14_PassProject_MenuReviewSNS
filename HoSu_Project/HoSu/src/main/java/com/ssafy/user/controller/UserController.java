package com.ssafy.user.controller;

import com.ssafy.user.dto.*;
import com.ssafy.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관리 API")
public class UserController {

        private final UserService userService;

        @PostMapping("/signup")
        @Operation(summary = "사용자 회원가입", description = "새로운 사용자를 등록합니다. 이메일과 닉네임은 중복될 수 없으며, 역할(role)은 기본값으로 USER가 설정됩니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = Long.class))),
                        @ApiResponse(responseCode = "400", description = "이메일 또는 닉네임 중복", content = @Content)
        })
        public ResponseEntity<?> signup(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "회원가입 정보 (이메일, 비밀번호, 닉네임, 성별, 전화번호 필수)", required = true) @RequestBody Signup signup) {
                Long userNo = userService.signup(signup);
                return ResponseEntity.status(HttpStatus.CREATED).body(userNo);
        }

        @DeleteMapping("/{userNo}")
        @Operation(summary = "사용자 회원탈퇴", description = "사용자 계정을 삭제합니다. 삭제된 데이터는 복구할 수 없습니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "회원탈퇴 성공"),
                        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
        })
        public ResponseEntity<Void> deleteUser(
                        @Parameter(description = "삭제할 사용자 번호", example = "1", required = true) @PathVariable Long userNo) {
                userService.deleteUser(userNo);
                return ResponseEntity.noContent().build();
        }

        @PutMapping("/{userNo}")
        @Operation(summary = "사용자 정보 수정", description = "사용자의 비밀번호, 닉네임, 전화번호, 프로필 이미지를 수정합니다. 변경하지 않을 필드는 null로 전송하면 됩니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "정보 수정 성공"),
                        @ApiResponse(responseCode = "400", description = "닉네임 또는 전화번호 중복", content = @Content),
                        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
        })
        public ResponseEntity<Void> updateUser(
                        @Parameter(description = "수정할 사용자 번호", example = "1", required = true) @PathVariable Long userNo,
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "수정할 정보 (비밀번호, 닉네임, 전화번호, 프로필 이미지 중 변경할 항목만 포함)", required = true) @RequestBody Update update) {
                userService.updateUser(userNo, update);
                return ResponseEntity.ok().build();
        }

        @PostMapping("/{userNo}/profile-image")
        @Operation(summary = "프로필 사진 업로드", description = "사용자의 프로필 사진을 S3에 업로드하고 URL을 저장합니다. 기존 이미지가 있으면 삭제됩니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "업로드 성공", content = @Content(schema = @Schema(implementation = String.class))),
                        @ApiResponse(responseCode = "400", description = "잘못된 파일 형식 또는 크기 초과", content = @Content),
                        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
        })
        public ResponseEntity<String> uploadProfileImage(
                        @Parameter(description = "사용자 번호", example = "1", required = true) @PathVariable Long userNo,
                        @Parameter(description = "업로드할 이미지 파일 (5MB 이하, 이미지 형식만 가능)", required = true) @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
                String imageUrl = userService.uploadProfileImage(userNo, file);
                return ResponseEntity.ok(imageUrl);
        }

        @PostMapping("/login")
        @Operation(summary = "사용자 로그인", description = "이메일과 비밀번호로 로그인합니다. 성공 시 사용자 정보를 반환합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = LoginResp.class))),
                        @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치", content = @Content)
        })
        public ResponseEntity<LoginResp> login(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "로그인 정보 (이메일, 비밀번호)", required = true) @RequestBody LoginReq loginReq) {
                LoginResp loginResp = userService.login(loginReq);
                return ResponseEntity.ok(loginResp);
        }

        @GetMapping("/{userNo}")
        @Operation(summary = "사용자 정보 조회", description = "사용자 번호로 사용자의 기본 정보를 조회합니다. 비밀번호는 포함되지 않습니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = LoginResp.class))),
                        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
        })
        public ResponseEntity<LoginResp> getUserInfo(
                        @Parameter(description = "조회할 사용자 번호", example = "1", required = true) @PathVariable Long userNo) {
                LoginResp loginResp = userService.getUserInfo(userNo);
                return ResponseEntity.ok(loginResp);
        }

        @GetMapping("/{userNo}/profile")
        @Operation(summary = "사용자 프로필 조회", description = "사용자의 프로필 정보를 조회합니다. 팔로워/팔로잉 수, 게시글 수, 팔로우 관계 정보를 포함합니다. myUserNo를 전달하면 해당 사용자와의 팔로우 관계를 확인할 수 있습니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "프로필 조회 성공", content = @Content(schema = @Schema(implementation = UserProfile.class))),
                        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
        })
        public ResponseEntity<UserProfile> getUserProfile(
                        @Parameter(description = "조회할 사용자 번호", example = "2", required = true) @PathVariable Long userNo,
                        @Parameter(description = "현재 로그인한 사용자 번호 (팔로우 관계 확인용, 선택)", example = "1") @RequestParam(required = false) Long myUserNo) {

                UserProfile profile = userService.getUserProfile(userNo, myUserNo);
                return ResponseEntity.ok(profile);
        }

        @GetMapping("/role")
        @Operation(summary = "사용자 권한 정보 조회", description = "사용자의 권한 정보를 조회합니다. 기본적인 사용자 정보도 포함됩니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = UserProfile.class))),
                        @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content)
        })
        public ResponseEntity<List<UserRole>> getUserRole() throws Exception {
                List<UserRole> list = userService.getUserRole();
                return ResponseEntity.ok(list);
        }

        @PutMapping("/{userNo}/role")
        @Operation(summary = "사용자 권한 수정", description = "사용자의 권한 정보를 수정합니다. U(사용자), A(관리자), C(식당 사장님) 세 권한으로 나뉩니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "권한 수정 성공"),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청(검증 실패)"),
                        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
        })
        public ResponseEntity<Integer> updateRole(
                        @Parameter(description = "수정할 사용자 번호", example = "1", required = true) @PathVariable Long userNo,
                        @org.springframework.web.bind.annotation.RequestBody UserRoleUpdate request) throws Exception {
                int result = userService.updateRole(userNo, request.getRole());
                return ResponseEntity.ok(result);
        }

        @GetMapping("/random")
        @Operation(summary = "랜덤 사용자 목록 조회", description = "랜덤으로 사용자 목록을 조회합니다. 추천 사용자 기능에 사용됩니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = LoginResp.class)))
        })
        public ResponseEntity<List<LoginResp>> getRandomUsers(
                        @Parameter(description = "현재 로그인한 사용자 번호 (본인 제외용, 선택)", example = "1") @RequestParam(required = false) Long myUserNo,
                        @Parameter(description = "조회할 사용자 수", example = "5") @RequestParam(defaultValue = "5") int limit) {
                List<LoginResp> users = userService.getRandomUsers(myUserNo, limit);
                return ResponseEntity.ok(users);
        }

}