package com.ssafy.follow.service;

import com.ssafy.follow.dto.*;

import java.util.List;

public interface FollowService {

    /**
     * 팔로우하기
     */
    FollowActionResp follow(Long followerNo, Long followingNo);

    /**
     * 언팔로우하기
     */
    FollowActionResp unfollow(Long followerNo, Long followingNo);

    /**
     * 팔로워 목록 조회 (나를 팔로우하는 사람들)
     */
    List<FollowUser> getFollowers(Long userNo, Long myUserNo);

    /**
     * 팔로잉 목록 조회 (내가 팔로우하는 사람들)
     */
    List<FollowUser> getFollowing(Long userNo, Long myUserNo);

    /**
     * 팔로우 통계 조회
     */
    FollowStats getFollowStats(Long userNo);

    /**
     * 팔로우 상태 확인
     */
    FollowStatus getFollowStatus(Long myUserNo, Long targetUserNo);

    /**
     * 팔로워 수 조회
     */
    int getFollowerCount(Long userNo);

    /**
     * 팔로잉 수 조회
     */
    int getFollowingCount(Long userNo);

    /**
     * 팔로잉 사용자 ID 목록 조회 (피드용)
     */
    List<Long> getFollowingIds(Long userNo);
}