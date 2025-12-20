package com.ssafy.menu.service;

import java.util.List;

import com.ssafy.menu.dto.InsertMenuReq;
import com.ssafy.menu.dto.InsertTagReq;
import com.ssafy.menu.dto.Menu;
import com.ssafy.menu.dto.UpdateMenuReq;
import com.ssafy.menu.dto.UpdateTagReq;

public interface MenuService {
	public void insertMenu(InsertMenuReq menu) throws Exception;
	public void updateMenu(UpdateMenuReq menu) throws Exception;
	public void deleteMenu(Long menuId) throws Exception;
	public void insertTag(InsertTagReq tag) throws Exception;
	public void updateTag(UpdateTagReq tag) throws Exception;
	public void deleteTag(Long tagId) throws Exception;
	public List<Menu> getMenus(Long restaurantId) throws Exception;
	public void activeMenuChange(Long menuId, int i) throws Exception;
}
