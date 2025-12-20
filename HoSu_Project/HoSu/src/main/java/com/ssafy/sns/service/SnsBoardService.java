package com.ssafy.sns.service;

import com.ssafy.sns.dto.*;

import java.util.List;

public interface SnsBoardService {

    // 게시물 작성
    Long createBoard(SnsBoardReq req);

    // 게시물 수정
    void updateBoard(Long boardId, Long userNo, SnsBoardUpdateReq req);

    // 게시물 삭제
    void deleteBoard(Long boardId, Long userNo);

    // 게시물 단건 조회
    SnsBoardResp getBoard(Long boardId, Long myUserNo);

    // 게시물 목록 조회 (피드)
    List<SnsBoardResp> getBoardList(Long myUserNo, int page, int size);

    // 특정 유저 게시물 목록
    List<SnsBoardResp> getBoardListByUser(Long userNo, Long myUserNo);

    // 팔로잉 피드 조회
    List<SnsBoardResp> getFollowingFeed(Long userNo, int page, int size);

    // 식당별 게시물 목록 조회
    List<SnsBoardResp> getBoardListByRestaurant(Long restaurantId, Long myUserNo);

    // 랜덤 게시물 목록 조회
    List<SnsBoardResp> getRandomBoards(Long myUserNo, int limit);

}