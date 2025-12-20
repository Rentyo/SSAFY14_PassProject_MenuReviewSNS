package com.ssafy.follow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FollowReq {

    @NotNull(message = "팔로우할 사용자의 번호는 필수값 입니다.")
    private Long followingNo;


    //followerNo은 로그인사용자 정보에서 가져올 예정입니다.
}
