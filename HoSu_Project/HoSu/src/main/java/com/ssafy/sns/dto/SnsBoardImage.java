package com.ssafy.sns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
게시글 이미지 dto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsBoardImage {
    private Long imageId;
    private Long boardId;
    private String imageUrl;
    private Integer imageOrder;
    private LocalDateTime createdAt;
    private double menuRating;
    private long menuId;
    private String oneLineReview;
    private String menuName;
}