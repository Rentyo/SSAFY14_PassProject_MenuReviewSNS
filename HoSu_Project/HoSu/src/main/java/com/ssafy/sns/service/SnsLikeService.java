package com.ssafy.sns.service;

import com.ssafy.sns.dto.SnsLikeResp;

public interface SnsLikeService {

    // 좋아요 토글 (있으면 삭제, 없으면 추가)
    SnsLikeResp toggleLike(Long boardId, Long userNo);

    // 좋아요 수 조회
    int getLikeCount(Long boardId);

    // 좋아요 여부 확인
    boolean isLiked(Long boardId, Long userNo);
}