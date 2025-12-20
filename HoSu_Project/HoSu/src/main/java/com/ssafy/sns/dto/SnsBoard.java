package com.ssafy.sns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsBoard {
    private Long boardId;
    private Long userNo;
    private Long restaurantId;
    private String title;
    private String content;
    private String boardImg;
    private String boardTag;
    private Integer likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}