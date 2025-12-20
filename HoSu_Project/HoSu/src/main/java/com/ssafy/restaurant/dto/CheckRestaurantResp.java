package com.ssafy.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckRestaurantResp {
	private String addressName;
	private String sidoNo;
	private String sidoName;
	private String gunguCode;
	private String gunguName;
	private String phone;
	private double lat;
	private double lng;
	private String placeName;
	
	
}
