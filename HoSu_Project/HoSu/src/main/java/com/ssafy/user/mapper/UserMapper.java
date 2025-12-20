package com.ssafy.user.mapper;

import com.ssafy.user.dto.*;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/*
 * 이 인터페이스는 User 관련 DB 작업을 위한 MyBatis Mapper입니다.
 * 
 * 주요 기능:
 * 1. insertUser        -> 회원가입 (사용자 정보 INSERT)
 * 2. selectUserByEmail -> 이메일로 사용자 조회 (로그인, 중복 체크용)
 * 3. selectUserByUserNo -> userNo로 사용자 조회
 * 
 * ※ 실제 SQL 쿼리는 resources/mapper/UserMapper.xml에 작성됩니다.
 */

@Mapper
public interface UserMapper {
    void insertUser(Signup signup); // User가 아니라 Signup!

    User selectUserByEmail(String userEmail);

    User selectUserByUserNo(Long userNo);

    void updateUser(User user);

    //회원 탈퇴 전 메뉴 평점 수정
    void deleteChangeMenuRatingByUser(Long userNo);
    
    //회원 탈퇴 전 식당 평점 수정
    void deleteChangeRestaurantRatingByUser(Long userNo);
    
    void deleteUser(Long userNo);

    int existsByEmail(String userEmail);

    int existsByNickname(String nickname);

    int existsByPhoneNumber(String phoneNumber);

    int existsByNicknameExcludingUser(@Param("nickname") String nickname, @Param("userNo") Long userNo);

    int existsByPhoneNumberExcludingUser(@Param("phoneNumber") String phoneNumber, @Param("userNo") Long userNo);

    /**
     * 사용자 프로필 조회 (팔로우 정보 포함)
     */
    UserProfile selectUserProfile(@Param("userNo") Long userNo,
            @Param("myUserNo") Long myUserNo);

    List<UserRole> getUserRole() throws Exception;

    int updateRole(Long userNo, String role) throws Exception;

    // 랜덤 사용자 목록 조회 (추천용)
    List<LoginResp> selectRandomUsers(@Param("myUserNo") Long myUserNo, @Param("limit") int limit);

    // 식당을 소유하고 있는 지
    boolean isOwner(Long userNo);

    // 사용자 프로필 이미지 업데이트
    void updateUserImage(User user);

    void updateRestaurantName(@Param("nickname") String nickname, @Param("userImg")String userImg, @Param("userNo") Long userNo);

    // 식당 메인 이미지 업데이트 (CEO 프로필 이미지와 동기화)
    void updateRestaurantImage(@Param("imageUrl") String imageUrl, @Param("userNo") Long userNo);
    
    

}