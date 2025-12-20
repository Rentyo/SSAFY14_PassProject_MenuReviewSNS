package com.ssafy.restaurant.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.restaurant.dto.Notice;
import com.ssafy.restaurant.dto.Restaurant;
import com.ssafy.restaurant.dto.RestaurantOpenRequest;
import com.ssafy.restaurant.dto.UpdateNoticeReq;
import com.ssafy.restaurant.dto.insertNoticeReq;
import com.ssafy.sns.dto.SnsBoardInRestaurant;
import com.ssafy.user.dto.Signup;
import com.ssafy.user.dto.User;

@Mapper
public interface RestaurantMapper {
    void insertRestaurant(Restaurant rest) throws Exception;
    void updateRestaurant(Restaurant rest) throws Exception;
    void deleteRestaurant(Restaurant rest) throws Exception;
    void insertOpenRequest(RestaurantOpenRequest req) throws Exception;
	List<SnsBoardInRestaurant> selectSnsBoardInRestaurantsById(long restaurantId) throws Exception;
	Restaurant selectRestaurantById(long restaurantId) throws Exception;
	Long selectRestaurantIdByUserNo(Long userNo) throws Exception;
	List<Notice> selectNoticesIdByRestaurantId(Long restaurantId) throws Exception;
	void insertNotice(insertNoticeReq req) throws Exception;
	void updateNotice(UpdateNoticeReq req) throws Exception;
	void deleteNotice(long restaurantNoticeId) throws Exception;
	void viewCountUp(Long restaurantId) throws Exception;
	int existRestaurantOwn(Long userNo) throws Exception;
	int existRestaurantRequest(Long userNo) throws Exception;
	void updateNickName(@Param("userNo") Long userNo, @Param("restaurantName") String restaurantName) throws Exception;
	void updateImage(Long userNo) throws Exception;
}
