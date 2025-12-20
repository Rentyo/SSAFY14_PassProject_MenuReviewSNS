package com.ssafy.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.user.dto.Signup;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long userNo;

    @BeforeEach
    public void setup() throws Exception {
        // 테스트용 사용자 생성
        Signup signup = new Signup();
        signup.setUserEmail("test@example.com");
        signup.setUserPw("password123");
        signup.setNickname("테스트유저");
        signup.setGender("MALE");
        signup.setPhoneNumber("010-1234-5678");

        MvcResult result = mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated())  // 201 상태 코드
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        this.userNo = Long.parseLong(responseString);
    }

    @Test
    public void testSignup() throws Exception {
        // 새로운 유저 생성 테스트
        Signup newSignup = new Signup();
        newSignup.setUserEmail("newuser@example.com");
        newSignup.setUserPw("password456");
        newSignup.setNickname("신규유저");
        newSignup.setGender("FEMALE");
        newSignup.setPhoneNumber("010-1111-2222");

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSignup)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void testDuplicateEmail() throws Exception {
        // 이메일 중복 테스트
        Signup duplicateSignup = new Signup();
        duplicateSignup.setUserEmail("test@example.com");  // 중복 이메일
        duplicateSignup.setUserPw("password789");
        duplicateSignup.setNickname("중복테스트");
        duplicateSignup.setGender("MALE");
        duplicateSignup.setPhoneNumber("010-2222-3333");

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateSignup)))
                .andDo(print())
                .andExpect(status().isConflict())  // 409 상태 코드
                .andExpect(content().string("이미 사용 중인 이메일입니다."));
    }

    @Test
    public void testDuplicateNickname() throws Exception {
        // 닉네임 중복 테스트
        Signup duplicateSignup = new Signup();
        duplicateSignup.setUserEmail("another@example.com");
        duplicateSignup.setUserPw("password789");
        duplicateSignup.setNickname("테스트유저");  // 중복 닉네임
        duplicateSignup.setGender("MALE");
        duplicateSignup.setPhoneNumber("010-3333-4444");

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateSignup)))
                .andDo(print())
                .andExpect(status().isConflict())  // 409 상태 코드
                .andExpect(content().string("이미 사용 중인 닉네임입니다."));
    }
}