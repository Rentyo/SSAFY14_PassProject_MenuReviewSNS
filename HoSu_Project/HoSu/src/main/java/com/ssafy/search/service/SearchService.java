package com.ssafy.search.service;

import java.util.List;

import com.ssafy.menu.dto.Tag;
import com.ssafy.search.dto.Gungu;
import com.ssafy.search.dto.SearchKeywords;
import com.ssafy.search.dto.SearchRestaurantPageResp;
import com.ssafy.search.dto.Sido;

public interface SearchService {
	List<Sido> getSidoList() throws Exception;

	List<Gungu> getGunguList(Sido sido) throws Exception;

	List<Tag> getMenuTagList(String category) throws Exception;

	List<Tag> getMenuTagListAll() throws Exception;

	SearchRestaurantPageResp searchRestaurantList(SearchKeywords keyword, int page, int size, String sort)
			throws Exception;

	Sido getSidoToName(String sidoName) throws Exception;

	Gungu getGunguToName(String sidoNo, String gunguName) throws Exception;

	Tag getMenuTagToName(String tagName) throws Exception;
}
