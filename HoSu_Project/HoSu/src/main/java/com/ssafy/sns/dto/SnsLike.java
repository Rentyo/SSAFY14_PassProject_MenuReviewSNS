package com.ssafy.sns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/*
좋아요 dto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsLike {
    private Long likeId;
    private Long boardId;
    private Long userNo;
    private LocalDateTime createdAt;
}