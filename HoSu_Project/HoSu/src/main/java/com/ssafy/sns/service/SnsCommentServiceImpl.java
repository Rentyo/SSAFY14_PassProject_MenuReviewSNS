package com.ssafy.sns.service;

import com.ssafy.sns.dto.SnsComment;
import com.ssafy.sns.dto.SnsCommentReq;
import com.ssafy.sns.mapper.SnsCommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SnsCommentServiceImpl implements SnsCommentService {

    private final SnsCommentMapper commentMapper;

    @Override
    @Transactional
    public Long createComment(SnsCommentReq req) {
        SnsComment comment = SnsComment.builder()
                .boardId(req.getBoardId())
                .userNo(req.getUserNo())
                .parentId(req.getParentId())
                .content(req.getContent())
                .build();

        commentMapper.insertComment(comment);
        return comment.getCommentId();
    }

    @Override
    @Transactional
    public void updateComment(Long commentId, Long userNo, String content) {
        // 댓글 조회
        SnsComment comment = commentMapper.selectCommentById(commentId);

        // 검증
        if (comment == null) {
            throw new RuntimeException("댓글을 찾을 수 없습니다.");
        }
        if (!comment.getUserNo().equals(userNo)) {
            throw new RuntimeException("본인 댓글만 수정할 수 있습니다.");
        }

        SnsComment updateComment = SnsComment.builder()
                .commentId(commentId)
                .content(content)
                .build();
        commentMapper.updateComment(updateComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userNo) {
        // 댓글 조회
        SnsComment comment = commentMapper.selectCommentById(commentId);

        // 검증
        if (comment == null) {
            throw new RuntimeException("댓글을 찾을 수 없습니다.");
        }
        if (!comment.getUserNo().equals(userNo)) {
            throw new RuntimeException("본인 댓글만 삭제할 수 있습니다.");
        }

        commentMapper.deleteComment(commentId);
    }

    @Override
    public List<SnsComment> getCommentsByBoardId(Long boardId) {
        List<SnsComment> comments = commentMapper.selectCommentsByBoardId(boardId);

        // 각 댓글에 대댓글 추가
        for (SnsComment comment : comments) {
            comment.setReplies(commentMapper.selectRepliesByParentId(comment.getCommentId()));
        }

        return comments;
    }
}
