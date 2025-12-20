package com.ssafy.aiSns.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiExtractResult {
    private String sidoName;     
    private String gunguName;   
    private String restaurantName;

    private List<String> categories; // ENUM 값들
    private List<String> menuNames;
    private List<String> tagNames;
    private List<String> features;
    
    private Long sidoNo;
    private Long gunguCode;
}
