package com.ssafy.sns.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 이 DTO는 식당 상세 페이지에 표시하는 리뷰들에 대한 DTO를 명세합니다.
 * 1. boardId : 게시물 ID 로 이미지를 클릭했을 때 ReviewDetail 페이지로 이동할 수 있도록
 * 2. boardImg : n x n 형태로 이미지를 보여줌
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "식당 페이지 게시물 이미지를 보여주는 DTO")
public class SnsBoardInRestaurant {
	@Schema(description = "게시물 고유 번호", example = "1")
	private Long boardId;
	@Schema(description = "이미지 URL")
	private String boardImg;
}
