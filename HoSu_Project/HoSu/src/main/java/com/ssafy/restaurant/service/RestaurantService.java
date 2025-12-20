package com.ssafy.restaurant.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.ssafy.restaurant.dto.CheckRestaurantResp;
import com.ssafy.restaurant.dto.Notice;
import com.ssafy.restaurant.dto.Restaurant;
import com.ssafy.restaurant.dto.RestaurantDetailById;
import com.ssafy.restaurant.dto.RestaurantOpenRequest;
import com.ssafy.restaurant.dto.UpdateNoticeReq;
import com.ssafy.restaurant.dto.insertNoticeReq;

public interface RestaurantService {
	List<CheckRestaurantResp> search(String keyword) throws Exception;
	void insertRestaurant(Restaurant rest) throws Exception;
    void updateRestaurant(Restaurant rest) throws Exception;
    void deleteRestaurant(Restaurant rest) throws Exception;
    void insertOpenRequest(RestaurantOpenRequest req) throws Exception;
    RestaurantDetailById selectRestaurantById(long restaurantId) throws Exception;
	Long selectRestaurantIdByUserNo(Long userNo) throws Exception;
	List<Notice> selectNoticesIdByRestaurantId(Long restaurantId) throws Exception;
	void insertNotice(insertNoticeReq req) throws Exception;
	void updateNotice(UpdateNoticeReq req) throws Exception;
	void deleteNotice(long restaurantNoticeId) throws Exception;
	void viewCountUp(Long restaurantId) throws Exception;
	int getCeoLevel(Long userNo) throws Exception;
}
