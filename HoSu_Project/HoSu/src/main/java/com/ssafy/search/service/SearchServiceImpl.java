package com.ssafy.search.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssafy.menu.dto.Tag;
import com.ssafy.search.dto.Gungu;
import com.ssafy.search.dto.SearchKeywords;
import com.ssafy.search.dto.SearchRestaurant;
import com.ssafy.search.dto.SearchRestaurantPageResp;
import com.ssafy.search.dto.Sido;
import com.ssafy.search.mapper.SearchMapper;

@Service("searchServiceImpl")
public class SearchServiceImpl implements SearchService {

	@Autowired
	SearchMapper smapper;

	@Override
	public List<Sido> getSidoList() throws Exception {
		return smapper.getSidoList();
	}

	@Override
	public List<Gungu> getGunguList(Sido sido) throws Exception {
		return smapper.getGunguList(sido);
	}

	@Override
	public List<Tag> getMenuTagList(String category) throws Exception {
		return smapper.getMenuTagList(category);
	}

	@Override
	public SearchRestaurantPageResp searchRestaurantList(SearchKeywords keyword, int page, int size, String sort)
			throws Exception {
		int offset = page * size;

		List<SearchRestaurant> list = smapper.searchRestaurantList(keyword, offset, size, sort);

		int totalCount = smapper.searchRestaurantCount(keyword);

		return new SearchRestaurantPageResp(
				list,
				totalCount,
				page,
				size);
	}

	@Override
	public List<Tag> getMenuTagListAll() throws Exception {
		return smapper.getMenuTagListAll();
	}

	@Override
	public Sido getSidoToName(String sidoName) throws Exception {
		return smapper.getSidoToName(sidoName);
	}

	@Override
	public Gungu getGunguToName(String sidoNo, String gunguName) throws Exception {
		// 공공 데이터 형식대로 맞추려면 세종은 걍 세종특별자치시를 군구로 잡는게 맞는 거 같아요
		if (sidoNo.equals("8")) {
			return smapper.getGunguToName(sidoNo, "세종특별자치시");
		}
		return smapper.getGunguToName(sidoNo, gunguName);
	}

	@Override
	public Tag getMenuTagToName(String tagName) throws Exception {
		return smapper.getMenuTagToName(tagName);
	}

}
