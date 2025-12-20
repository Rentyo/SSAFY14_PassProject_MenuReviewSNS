package com.ssafy.search.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRestaurantPageResp {

    private List<SearchRestaurant> content; // 실제 데이터
    private int totalCount;                 // 전체 개수
    private int page;                       // 현재 페이지
    private int size;                       // 페이지 크기

    public int getTotalPages() {
        return (int) Math.ceil((double) totalCount / size);
    }
}