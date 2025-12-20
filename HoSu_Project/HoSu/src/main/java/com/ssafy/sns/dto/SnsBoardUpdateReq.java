package com.ssafy.sns.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "SNS 게시물 수정 요청 DTO")
public class SnsBoardUpdateReq {
	@Schema(description = "수정할 제목", example = "제목")
    private String title;
	@Schema(description = "수정할 게시글 내용", example = "수정할 게시글 내용")
	private String content;
	@Schema(description = "수정할 메뉴 각각의 리뷰")
    private List<SnsBoardImageUpdateReq> imageUrls;
	@Schema(description = "식당 ID", example = "5")
    private Long restaurantId;
}