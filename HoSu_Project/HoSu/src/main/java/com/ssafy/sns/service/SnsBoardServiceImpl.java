package com.ssafy.sns.service;

import com.ssafy.sns.dto.*;
import com.ssafy.sns.mapper.SnsBoardMapper;
import com.ssafy.sns.mapper.SnsCommentMapper;
import com.ssafy.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SnsBoardServiceImpl implements SnsBoardService {

    private final SnsBoardMapper boardMapper;
    private final SnsCommentMapper commentMapper;
    private final FollowService followService;

    @Override
    @Transactional
    public Long createBoard(SnsBoardReq req) {
        SnsBoard board = SnsBoard.builder()
                .userNo(req.getUserNo())
                .content(req.getContent())
                .restaurantId(req.getRestaurantId())
                .title(req.getTitle())
                .boardImg(req.getMenus().get(0).getImageUrl())
                .build();
        boardMapper.insertBoard(board);
        
    	boardMapper.insertImages(board.getBoardId(), req.getMenus());
    	
    	double sumRating = 0.0d;
        for(int i = 0; i < req.getMenus().size(); i++) {
        	boardMapper.insertChangeMenuRating(req.getMenus().get(i).getMenuId(), req.getMenus().get(i).getMenuRating());
        	sumRating += req.getMenus().get(i).getMenuRating();
        }
        
        boardMapper.insertChangeRestaurantRating(req.getRestaurantId(), sumRating, req.getMenus().size());
        

        return board.getBoardId();
    }

    @Override
    @Transactional
    public void updateBoard(Long boardId, Long userNo, SnsBoardUpdateReq req) {
        SnsBoardResp board = boardMapper.selectBoardById(boardId, null);

        if (board == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다.");
        }
        if (!board.getUserNo().equals(userNo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 게시물만 수정할 수 있습니다.");
        }
        
        SnsBoard updateBoard = SnsBoard.builder()
                .boardId(boardId)
                .title(req.getTitle())
                .content(req.getContent())
                .boardImg(req.getImageUrls().get(0).getImageUrl())
                .build();
        // 게시글 수정
        boardMapper.updateBoard(updateBoard);
        
        double totalDiff = 0.0;
        // 게시글 내 아이템 수정
        for(int i = 0; i < req.getImageUrls().size(); i ++) {
        	double nowrate = boardMapper.getNowMenuRating(req.getImageUrls().get(i).getImageId());
        	double diff =  req.getImageUrls().get(i).getMenuRating() - nowrate;
        	
        	boardMapper.updateImagesByImageId(req.getImageUrls().get(i).getImageId(), req.getImageUrls().get(i));
        	
        	boardMapper.updateChangeMenuRating(req.getImageUrls().get(i).getMenuId(), diff);
        	totalDiff += diff;
        }
        
        System.out.println(req.getRestaurantId());
        int updated = boardMapper.updateChangeRestaurantRating(req.getRestaurantId(), totalDiff);        
        System.out.println("총 차이 " + updated);
    }

    @Override
    @Transactional
    public void deleteBoard(Long boardId, Long userNo) {
        SnsBoardResp board = boardMapper.selectBoardById(boardId, null);

        if (board == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다.");
        }
        if (!board.getUserNo().equals(userNo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 게시물만 삭제할 수 있습니다.");
        }
        
        // 삭제 하기 전 메뉴 리뷰 개수 및 총 평점 처리
        boardMapper.deleteChangeMenuRating(boardId);
        // 삭제 하기 전 식당 메뉴 리뷰 개수 및 총 평점 처리
        boardMapper.deleteChangeRestaurantRating(boardId);
        
        // 게시판 삭제(삭제하면서 안에 있는 메뉴 평가도 Cascade로 삭제됨)
        boardMapper.deleteBoard(boardId);
    }

    @Override
    public SnsBoardResp getBoard(Long boardId, Long myUserNo) {
        SnsBoardResp board = boardMapper.selectBoardById(boardId, myUserNo);
        if (board == null) {
            throw new RuntimeException("게시물을 찾을 수 없습니다.");
        }

        board.setImages(boardMapper.selectImagesByBoardId(boardId));

        List<SnsComment> comments = commentMapper.selectCommentsByBoardId(boardId);
        for (SnsComment comment : comments) {
            comment.setReplies(commentMapper.selectRepliesByParentId(comment.getCommentId()));
        }
        board.setComments(comments);

        return board;
    }

    @Override
    public List<SnsBoardResp> getBoardList(Long myUserNo, int page, int size) {
        int offset = page * size;
        List<SnsBoardResp> boards = boardMapper.selectBoardList(myUserNo, offset, size);

        for (SnsBoardResp board : boards) {
            board.setImages(boardMapper.selectImagesByBoardId(board.getBoardId()));
        }

        return boards;
    }

    @Override
    public List<SnsBoardResp> getBoardListByUser(Long userNo, Long myUserNo) {
        List<SnsBoardResp> boards = boardMapper.selectBoardListByUser(userNo, myUserNo);

        for (SnsBoardResp board : boards) {
            board.setImages(boardMapper.selectImagesByBoardId(board.getBoardId()));
        }

        return boards;
    }

    @Override
    public List<SnsBoardResp> getFollowingFeed(Long userNo, int page, int size) {
        // 1. 팔로잉하는 사용자 ID 목록 조회
        List<Long> followingIds = followService.getFollowingIds(userNo);

        // 2. 팔로잉하는 사용자가 없으면 빈 리스트 반환
        if (followingIds == null || followingIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 3. 팔로잉 사용자들의 게시물 조회
        int offset = page * size;
        List<SnsBoardResp> boards = boardMapper.selectBoardListByUserIds(followingIds, userNo, offset, size);

        // 4. 각 게시물의 이미지 조회
        for (SnsBoardResp board : boards) {
            board.setImages(boardMapper.selectImagesByBoardId(board.getBoardId()));
        }

        return boards;
    }

    @Override
    public List<SnsBoardResp> getBoardListByRestaurant(Long restaurantId, Long myUserNo) {
        List<SnsBoardResp> boards = boardMapper.selectBoardListByRestaurant(restaurantId, myUserNo);

        for (SnsBoardResp board : boards) {
            board.setImages(boardMapper.selectImagesByBoardId(board.getBoardId()));
        }

        return boards;
    }

    @Override
    public List<SnsBoardResp> getRandomBoards(Long myUserNo, int limit) {
        List<SnsBoardResp> boards = boardMapper.selectRandomBoards(myUserNo, limit);

        for (SnsBoardResp board : boards) {
            board.setImages(boardMapper.selectImagesByBoardId(board.getBoardId()));
        }

        return boards;
    }
}