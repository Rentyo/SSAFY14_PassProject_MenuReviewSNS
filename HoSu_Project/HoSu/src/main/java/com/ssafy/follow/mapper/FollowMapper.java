package com.ssafy.follow.mapper;

import com.ssafy.follow.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FollowMapper {

    // 팔로우 추가
    /*
     * @param followerNo 팔로우 하는 사람
     * 
     * @param followingNo 팔로우 받는 사람
     * 
     * @return 삽입된 행 수
     */
    int insertFollow(@Param("followerNo") Long followerNo,
            @Param("followingNo") Long followingNo);

    /*
     * 언팔로우 (사용자 번호로 삭제)
     * 
     * @param followerNo 팔로우 하는 사람
     * 
     * @param followingNo 팔로우 받는 사람
     * 
     * @return 삭제된 행 수
     */
    int deleteFollow(@Param("followerNo") Long followerNo,
            @Param("followingNo") Long followingNo);

    /*
     * 팔로워 수 조회 (나를 팔로우하는 사람 수)
     * 
     * @param userNo 사용자 번호
     * 
     * @return 팔로워 수
     */
    int countFollowers(@Param("userNo") Long userNo);

    /*
     * 팔로잉 수 조회 (내가 팔로우하는 사람 수)
     * 
     * @param userNo 사용자 번호
     * 
     * @return 팔로잉 수
     */
    int countFollowing(@Param("userNo") Long userNo);

    /*
     * 팔로우 통계 조회
     * 
     * @param userNo 사용자 번호
     * 
     * @return FollowStats 객체
     */
    FollowStats selectFollowStats(@Param("userNo") Long userNo);

    /*
     * 팔로워 목록 조회 (나를 팔로우하는 사람들)
     * 
     * @param userNo 사용자 번호
     * 
     * @param myUserNo 현재 로그인한 사용자 번호 (맞팔 확인용)
     * 
     * @return 팔로워 목록
     */
    List<FollowUser> selectFollowers(@Param("userNo") Long userNo,
            @Param("myUserNo") Long myUserNo);

    /*
     * 팔로잉 목록 조회 (내가 팔로우하는 사람들)
     * 
     * @param userNo 사용자 번호
     * 
     * @param myUserNo 현재 로그인한 사용자 번호 (맞팔 확인용)
     * 
     * @return 팔로잉 목록
     */
    List<FollowUser> selectFollowing(@Param("userNo") Long userNo,
            @Param("myUserNo") Long myUserNo);

    /*
     * 팔로우 상태 조회 (특정 사용자와의 관계)
     * 
     * @param myUserNo 내 사용자 번호
     * 
     * @param targetUserNo 대상 사용자 번호
     * 
     * @return FollowStatus 객체
     */
    FollowStatus selectFollowStatus(@Param("myUserNo") Long myUserNo,
            @Param("targetUserNo") Long targetUserNo);

    /*
     * 팔로잉 사용자 ID 목록 조회 (피드용)
     * 
     * @param userNo 사용자 번호
     * 
     * @return 팔로잉 사용자 ID 목록
     */
    List<Long> selectFollowingIds(@Param("userNo") Long userNo);
}
