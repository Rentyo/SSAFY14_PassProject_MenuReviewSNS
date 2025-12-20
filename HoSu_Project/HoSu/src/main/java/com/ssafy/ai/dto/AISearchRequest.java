package com.ssafy.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AISearchRequest {
    private String query; // 사용자 자연어 입력
}
