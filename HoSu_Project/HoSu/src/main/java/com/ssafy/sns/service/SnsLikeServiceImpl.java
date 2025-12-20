package com.ssafy.sns.service;

import com.ssafy.sns.dto.SnsLikeResp;
import com.ssafy.sns.mapper.SnsLikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SnsLikeServiceImpl implements SnsLikeService {

    private final SnsLikeMapper likeMapper;

    @Override
    @Transactional
    public SnsLikeResp toggleLike(Long boardId, Long userNo) {
        boolean exists = likeMapper.existsLike(boardId, userNo) > 0;

        if (exists) {
            // 좋아요 취소
            likeMapper.deleteLike(boardId, userNo);
        } else {
            // 좋아요 추가
            likeMapper.insertLike(boardId, userNo);
        }

        int likeCount = likeMapper.countLikes(boardId);

        return SnsLikeResp.builder()
                .success(true)
                .isLiked(!exists)
                .likeCount(likeCount)
                .build();
    }

    @Override
    public int getLikeCount(Long boardId) {
        return likeMapper.countLikes(boardId);
    }

    @Override
    public boolean isLiked(Long boardId, Long userNo) {
        return likeMapper.existsLike(boardId, userNo) > 0;
    }
}