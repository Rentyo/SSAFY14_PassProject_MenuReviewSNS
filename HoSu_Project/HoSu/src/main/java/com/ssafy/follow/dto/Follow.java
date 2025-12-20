package com.ssafy.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
 * 팔로우 테이블의 기본 DTO
 * DB 테이블과 1:1 매칭
 */


@NoArgsConstructor
@AllArgsConstructor
@Data
public class Follow {
    private Long followId;
    private Long followerNo;
    private Long followingNo;
    private LocalDateTime createdAt;
}
