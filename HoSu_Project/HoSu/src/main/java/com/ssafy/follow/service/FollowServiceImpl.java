package com.ssafy.follow.service;

import com.ssafy.follow.dto.*;
import com.ssafy.follow.mapper.FollowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowServiceImpl implements FollowService {

    private final FollowMapper followMapper;

    @Override
    @Transactional
    public FollowActionResp follow(Long followerNo, Long followingNo) {
        // 자기 자신 팔로우 방지
        if (followerNo.equals(followingNo)) {
            return FollowActionResp.builder()
                    .success(false)
                    .message("자기 자신을 팔로우할 수 없습니다.")
                    .build();
        }

        // 이미 팔로우 중인지 확인
        FollowStatus status = followMapper.selectFollowStatus(followerNo, followingNo);
        if (status != null && status.getIsFollowing()) {
            return FollowActionResp.builder()
                    .success(false)
                    .message("이미 팔로우 중입니다.")
                    .build();
        }

        // 팔로우 추가
        int result = followMapper.insertFollow(followerNo, followingNo);

        if (result > 0) {
            FollowStats stats = followMapper.selectFollowStats(followingNo);
            return FollowActionResp.builder()
                    .success(true)
                    .message("팔로우 성공")
                    .stats(stats)
                    .build();
        }

        return FollowActionResp.builder()
                .success(false)
                .message("팔로우 실패")
                .build();
    }

    @Override
    @Transactional
    public FollowActionResp unfollow(Long followerNo, Long followingNo) {
        int result = followMapper.deleteFollow(followerNo, followingNo);

        if (result > 0) {
            FollowStats stats = followMapper.selectFollowStats(followingNo);
            return FollowActionResp.builder()
                    .success(true)
                    .message("언팔로우 성공")
                    .stats(stats)
                    .build();
        }

        return FollowActionResp.builder()
                .success(false)
                .message("팔로우 관계가 존재하지 않습니다.")
                .build();
    }

    @Override
    public List<FollowUser> getFollowers(Long userNo, Long myUserNo) {
        return followMapper.selectFollowers(userNo, myUserNo);
    }

    @Override
    public List<FollowUser> getFollowing(Long userNo, Long myUserNo) {
        return followMapper.selectFollowing(userNo, myUserNo);
    }

    @Override
    public FollowStats getFollowStats(Long userNo) {
        return followMapper.selectFollowStats(userNo);
    }

    @Override
    public FollowStatus getFollowStatus(Long myUserNo, Long targetUserNo) {
        return followMapper.selectFollowStatus(myUserNo, targetUserNo);
    }

    @Override
    public int getFollowerCount(Long userNo) {
        return followMapper.countFollowers(userNo);
    }

    @Override
    public int getFollowingCount(Long userNo) {
        return followMapper.countFollowing(userNo);
    }

    @Override
    public List<Long> getFollowingIds(Long userNo) {
        return followMapper.selectFollowingIds(userNo);
    }
}
