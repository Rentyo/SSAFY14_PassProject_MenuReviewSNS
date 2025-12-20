import api from './index'

// 해당 식당 메뉴 가져오기
export const getRestaurantMenu = (restaurantId) => {
    return api.get(`/menus/${restaurantId}`);
}

// 해당 식당 메뉴 삭제하기(비활성화)
export const deleteRestaurantMenu = (menuId, active) => {
    return api.put(`/menus/deactive/${menuId}/${active}`);
}

// 메뉴 추가하기
export const createMenu = (menuData) => {
    return api.post('/menus', menuData, {
        headers: {
            'Content-Type': 'application/json'
        }
    });
}

// 메뉴 수정하기
export const updateMenu = (menuData) => {
    return api.put('/menus', menuData, {
        headers: {
            'Content-Type': 'application/json'
        }
    });
}

// 메뉴 태그 생성
export const createMenuTag = (tagData) => {
    return api.post('/menus/tag', tagData);
}

// 메뉴 태그 수정
export const updateMenuTag = (tagData) => {
    return api.put('/menus/tag', tagData);
}

// 메뉴 태그 삭제
export const deleteMenuTag = (tagId) => {
    return api.delete(`/menus/tag/${tagId}`);
}
