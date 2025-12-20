package com.ssafy.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenRequest {
	private long reqId;
	private String restaurantName;
	private String address;
	private String category;
	private long userNo;
	private String date;
	private int isApprove;
	private String description;
}
