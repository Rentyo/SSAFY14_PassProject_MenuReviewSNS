package com.ssafy.user.service;

import java.util.List;

import com.ssafy.user.dto.*;

/*
 * User 관련 비즈니스 로직을 처리하는 Service 인터페이스
 *
 * 주요 기능:
 * 1. signup - 회원가입
 * 2. login - 로그인
 * 3. getUserInfo - 사용자 정보 조회
 */
public interface UserService {

    // 회원가입 (생성된 userNo 반환)
    Long signup(Signup signup);

    // 로그인
    LoginResp login(LoginReq loginReq);

    // 사용자 정보 조회
    LoginResp getUserInfo(Long userNo);

    // 회원 삭제
    void deleteUser(Long userNo);

    // 회원 정보 수정 추가
    void updateUser(Long userNo, Update update);

    // 사용자 프로필 조회
    UserProfile getUserProfile(Long userNo, Long myUserNo);

    // 관리자 페이지 내 사용자 권한 조회
    List<UserRole> getUserRole() throws Exception;

    // 관리자 페이지 내 사용자 권한 수정
    int updateRole(Long userNo, String role) throws Exception;

    // 랜덤 사용자 목록 조회
    List<LoginResp> getRandomUsers(Long myUserNo, int limit);

    // 프로필 사진 업로드
    String uploadProfileImage(Long userNo, org.springframework.web.multipart.MultipartFile file);
}