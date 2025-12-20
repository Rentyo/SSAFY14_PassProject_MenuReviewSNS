package com.ssafy.search.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.menu.dto.Tag;
import com.ssafy.search.dto.Gungu;
import com.ssafy.search.dto.SearchKeywords;
import com.ssafy.search.dto.SearchRestaurant;
import com.ssafy.search.dto.Sido;

@Mapper
public interface SearchMapper {
	List<Sido> getSidoList() throws Exception;

	List<Gungu> getGunguList(Sido sido) throws Exception;

	List<Tag> getMenuTagList(String category) throws Exception;

	List<Tag> getMenuTagListAll() throws Exception;

	List<SearchRestaurant> searchRestaurantList(
			@Param("keywords") SearchKeywords keywords,
			@Param("offset") int offset,
			@Param("size") int size,
			@Param("sort") String sort) throws Exception;

	int searchRestaurantCount(SearchKeywords keywords);

	Sido getSidoToName(String sidoName) throws Exception;

	Gungu getGunguToName(@Param("sidoNo") String sidoNo, @Param("gunguName") String gunguName) throws Exception;

	Tag getMenuTagToName(String tagName) throws Exception;
}
