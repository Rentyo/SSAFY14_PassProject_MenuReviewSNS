package com.ssafy.user.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//날짜와 시간을 저장하는 자바클래스
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class User {
    private Long userNo;
    private String userEmail;
    private String userPw;
    private String nickname;
    private String userImg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String gender;
    private String role;
    private String phoneNumber;
}