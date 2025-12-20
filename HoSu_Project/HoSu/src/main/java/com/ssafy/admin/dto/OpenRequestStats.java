package com.ssafy.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenRequestStats {
    private long allCount;
    private long penCount;
    private long appCount;
}