package com.ssafy.admin.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenRequestPage {
	private List<OpenRequest> content;
    private int page;      // 1-based
    private int size;
    private long total;    // 전체 개수
    private int totalPages;
    
    private long allCount;
    private long penCount;
    private long appCount;
    
    public static OpenRequestPage of(List<OpenRequest> content, int page, int size, long total) {
        OpenRequestPage p = new OpenRequestPage();
        p.setContent(content);
        p.setPage(page);
        p.setSize(size);
        p.setTotal(total);
        p.setTotalPages((int) Math.ceil((double) total / size));
        return p;
    }
}
