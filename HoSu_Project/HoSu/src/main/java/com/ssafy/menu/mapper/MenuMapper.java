package com.ssafy.menu.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.menu.dto.InsertMenu;
import com.ssafy.menu.dto.InsertMenuReq;
import com.ssafy.menu.dto.InsertMenuTagMapping;
import com.ssafy.menu.dto.InsertTagReq;
import com.ssafy.menu.dto.Menu;
import com.ssafy.menu.dto.UpdateMenuReq;
import com.ssafy.menu.dto.UpdateTagReq;

@Mapper
public interface MenuMapper {
	// 메뉴 추가
	public long insertMenu(InsertMenu menu) throws Exception;
	
	// 메뉴 태그 추가
	public void insertMenuTagMapping(InsertMenuTagMapping mapping) throws Exception;
	
	// 메뉴 수정
	public long updateMenu(UpdateMenuReq menu) throws Exception;
	
	//메뉴와 태그 매핑 삭제
	public void deleteMenuTagMapping(Long menuId) throws Exception;
	
	// 그냥 메뉴 삭제
	public void deleteMenu(Long menuId) throws Exception;
	
	// 태그 추가
	public void insertTag(InsertTagReq tag) throws Exception;
	// 태그 수정
	public void updateTag(UpdateTagReq tag) throws Exception;
	// 태그 삭제
	public void deleteTag(Long tagId) throws Exception;
	
	// 식당 메뉴 조회
	List<Menu> getMenus(Long restaurantId) throws Exception;

	public void activeMenuChange(@Param("menuId") Long menuId, @Param("active")int active) throws Exception;
	
}
