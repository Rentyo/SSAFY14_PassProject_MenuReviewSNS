package com.ssafy.sns.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SnsBoardImageUpdateReq {
	private Long imageId;
    private Long boardId;
    private Long menuId;
    private String imageUrl;
    private double menuRating;
    private String oneLineReview;
    
}
