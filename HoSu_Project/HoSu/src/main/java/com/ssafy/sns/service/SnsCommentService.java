package com.ssafy.sns.service;

import com.ssafy.sns.dto.SnsComment;
import com.ssafy.sns.dto.SnsCommentReq;

import java.util.List;

public interface SnsCommentService {

    // 댓글 작성
    Long createComment(SnsCommentReq req);

    // 댓글 수정
    void updateComment(Long commentId, Long userNo, String content);

    // 댓글 삭제
    void deleteComment(Long commentId, Long userNo);

    // 댓글 목록 조회 (대댓글 포함)
    List<SnsComment> getCommentsByBoardId(Long boardId);
}