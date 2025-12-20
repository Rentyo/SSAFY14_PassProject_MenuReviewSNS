package com.ssafy.menu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.menu.dto.InsertMenu;
import com.ssafy.menu.dto.InsertMenuReq;
import com.ssafy.menu.dto.InsertMenuTagMapping;
import com.ssafy.menu.dto.InsertTagReq;
import com.ssafy.menu.dto.Menu;
import com.ssafy.menu.dto.UpdateMenuReq;
import com.ssafy.menu.dto.UpdateTagReq;
import com.ssafy.menu.mapper.MenuMapper;

@Service("MenuServiceImpl")
public class MenuServiceImpl implements MenuService {
	
	@Autowired
	private MenuMapper mMapper;
	
	@Transactional
	@Override
	public void insertMenu(InsertMenuReq menu) throws Exception {
		//1. 메뉴를 먼저 입력한다.
		InsertMenu iMenu = new InsertMenu(-1,menu.getRestaurantId(), menu.getMenuName(), menu.getPrice(), menu.getDescription());
		mMapper.insertMenu(iMenu);
		
		//2. 요청한 태그를 추가한다.
		if(menu.getTagIds() != null && menu.getTagIds().length != 0) {
			for(long tag : menu.getTagIds()) {
				InsertMenuTagMapping mapping = new InsertMenuTagMapping(iMenu.getMenuId(), tag); 
				mMapper.insertMenuTagMapping(mapping);
			}
		}
	}
	@Override
	@Transactional 
	public void updateMenu(UpdateMenuReq menu) throws Exception {
		// 1. 메뉴 기본 정보를 먼저 수정합니다.
		mMapper.updateMenu(menu);
		
		// 2. 기존 메뉴에 대한 태그를 전부 삭제합니다.
		mMapper.deleteMenuTagMapping(menu.getMenuId());
		
		// 3. 새로운 태그 등록합니다.
		if(menu.getTagIds() != null && menu.getTagIds().length != 0){
			for(long tag : menu.getTagIds()) {
				InsertMenuTagMapping mapping = new InsertMenuTagMapping(menu.getMenuId(), tag); 
				mMapper.insertMenuTagMapping(mapping);
			}
		}
	}
	
	@Override
	public void deleteMenu(Long menuId) throws Exception {
		//ON DELETE CASCADING 외래키 설정
		mMapper.deleteMenu(menuId);
	}
	@Override
	public List<Menu> getMenus(Long restaurantId) throws Exception {
		return mMapper.getMenus(restaurantId);
	}
	@Override
	public void insertTag(InsertTagReq tag) throws Exception {
		mMapper.insertTag(tag);
	}
	@Override
	public void updateTag(UpdateTagReq tag) throws Exception {
		mMapper.updateTag(tag);
	}
	@Override
	public void deleteTag(Long tagId) throws Exception {
		//ON DELETE CASCADING 외래키 설정
		mMapper.deleteTag(tagId);		
	}
	@Override
	public void activeMenuChange(Long menuId, int i) throws Exception{
		mMapper.activeMenuChange(menuId, i);
		
	}
}
