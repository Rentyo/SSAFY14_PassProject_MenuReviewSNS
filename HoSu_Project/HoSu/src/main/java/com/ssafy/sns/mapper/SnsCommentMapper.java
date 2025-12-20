package com.ssafy.sns.mapper;

import com.ssafy.sns.dto.SnsComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SnsCommentMapper {

    // 댓글 작성
    int insertComment(SnsComment comment);

    // 댓글 수정
    int updateComment(SnsComment comment);

    // 댓글 삭제
    int deleteComment(@Param("commentId") Long commentId);

    // 댓글 목록 조회 (부모 댓글만)
    List<SnsComment> selectCommentsByBoardId(@Param("boardId") Long boardId);

    // 대댓글 조회
    List<SnsComment> selectRepliesByParentId(@Param("parentId") Long parentId);

    // 댓글 수 조회
    int countComments(@Param("boardId") Long boardId);

    SnsComment selectCommentById(@Param("commentId") Long commentId);
}