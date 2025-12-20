package com.ssafy.sns.mapper;

import com.ssafy.sns.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SnsBoardMapper {

        // 게시물 작성
        int insertBoard(SnsBoard board);

        // 게시물 수정
        int updateBoard(SnsBoard board);

        // 게시물 삭제
        int deleteBoard(@Param("boardId") Long boardId);

        // 게시물 단건 조회
        SnsBoardResp selectBoardById(@Param("boardId") Long boardId,
                        @Param("myUserNo") Long myUserNo);

        // 게시물 목록 조회 (피드)
        List<SnsBoardResp> selectBoardList(@Param("myUserNo") Long myUserNo,
                        @Param("offset") int offset,
                        @Param("limit") int limit);

        // 특정 유저 게시물 목록
        List<SnsBoardResp> selectBoardListByUser(@Param("userNo") Long userNo,
                        @Param("myUserNo") Long myUserNo);

        // 팔로잉 피드 조회 (여러 유저의 게시물)
        List<SnsBoardResp> selectBoardListByUserIds(@Param("userIds") List<Long> userIds,
                        @Param("myUserNo") Long myUserNo,
                        @Param("offset") int offset,
                        @Param("limit") int limit);

        // 랜덤 게시물 목록 조회
        List<SnsBoardResp> selectRandomBoards(@Param("myUserNo") Long myUserNo, @Param("limit") int limit);

        // 식당별 게시물 목록 조회
        List<SnsBoardResp> selectBoardListByRestaurant(@Param("restaurantId") Long restaurantId,
                        @Param("myUserNo") Long myUserNo);

        // ==================== 이미지 ====================

        // 이미지 추가
        int insertImage(SnsBoardImage image);

        // 리뷰 메뉴 추가
        int insertImages(@Param("boardId")Long boardId,
                        @Param("menus")List<SnsBoardImage> menus);
        
        // 리뷰 메뉴 추가하면서 메뉴 테이블에 rating과 count 수정
        void insertChangeMenuRating(@Param("menuId") long menuId, @Param("rating") double rating);

        
        // 리뷰 메뉴 추가하면서 식당 테이블에 rating과 count 수정
        void insertChangeRestaurantRating(@Param("restaurantId") long restaurantId, @Param("sumRating") double sumRating, @Param("count") int count);
        
        // 이미지 삭제 (게시물 수정 시)
        int deleteImagesByBoardId(@Param("boardId") Long boardId);

        // 이미지 조회
        List<SnsBoardImage> selectImagesByBoardId(@Param("boardId") Long boardId);

        // 이미지 수정 전 현 메뉴 평점 가져오기
		double getNowMenuRating(Long imageId);

		// 이미지 수정
		void updateImagesByImageId(@Param("imageId")Long imageId, @Param("snsBoardImageUpdateReq")SnsBoardImageUpdateReq snsBoardImageUpdateReq);

		// 이미지 수정 후 현 메뉴 총 평점 수정하기
		void updateChangeMenuRating(@Param("menuId")Long menuId, @Param("diff")double diff);

		int updateChangeRestaurantRating(@Param("restaurantId")Long restaurantId, @Param("totalDiff")double totalDiff);
		
		// 리뷰 삭제 전 메뉴 평점 및 개수 전처리
		void deleteChangeMenuRating(@Param("boardId")Long boardId);
		
		// 리뷰 삭제 전 식당 평점 및 개수 전처리
		void deleteChangeRestaurantRating(@Param("boardId")Long boardId);
		
}