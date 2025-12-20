import api from './index'

// 식당 등록 요청 목록 가져오기
export const getRestaurantRequests = (page, size, type) => {
    return api.get('/admin/restaurants/requests', {
        params: {
            page,
            size,
            type
        }
    });
}

// 식당 등록 요청 승인
export const approveRestaurant = (id) => {
    return api.put(`/admin/restaurants/approve/${id}`);
}

// 식당 등록 요청 거절
export const rejectRestaurant = (id) => {
    return api.put(`/admin/restaurants/reject/${id}`);
}

// 사용자 게시글 목록 조회
export const getUserBoards = () => {
    return api.get(`/sns/boards/`);
}
