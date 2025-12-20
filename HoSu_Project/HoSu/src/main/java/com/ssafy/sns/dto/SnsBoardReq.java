package com.ssafy.sns.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "SNS 게시물 작성/수정 요청 DTO")
public class SnsBoardReq {
    @Schema(description = "작성자 사용자 번호", example = "1", required = true)
    private Long userNo;
    @Schema(description = "식당 ID (리뷰인 경우)", example = "5")
    private Long restaurantId;
    @Schema(description = "게시글 제목", example = "최고의 점심")
    private String title;
    @Schema(description = "게시글 내용", example = "1203 맛점")
    private String content;
    @Schema(description = "메뉴 태그들", example = "jonmat")
    private List<String> tags;
    @Schema(description = "리뷰 메뉴들")
    private List<SnsBoardImage> menus;
    
}