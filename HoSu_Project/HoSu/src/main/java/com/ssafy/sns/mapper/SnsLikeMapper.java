package com.ssafy.sns.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SnsLikeMapper {

    // 좋아요 추가
    int insertLike(@Param("boardId") Long boardId,
                   @Param("userNo") Long userNo);

    // 좋아요 삭제
    int deleteLike(@Param("boardId") Long boardId,
                   @Param("userNo") Long userNo);

    // 좋아요 여부 확인
    int existsLike(@Param("boardId") Long boardId,
                   @Param("userNo") Long userNo);

    // 좋아요 수 조회
    int countLikes(@Param("boardId") Long boardId);
}