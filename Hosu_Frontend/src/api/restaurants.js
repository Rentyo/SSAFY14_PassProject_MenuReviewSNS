import api from './index'

// 전체 식당 데이터 가져오기
export const getRestaurant = () => {
  return api.get('/restaurants');
}

// 식당 입력
export const insertRestaurant = (formData) => {
  return api.post('/restaurants', formData, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
}

//식당 개시 요청
export const openRestaurantReq = (formData) => {
  return api.post('/restaurants/open/request', formData, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
}

// 식당 장소 확인
export const checkRestaurantLocation = (keyword) => {
  return api.get('/restaurants/search', {
    params: { keyword }
  })
}

// 식당 상세 정보 가져오기
export const getRestaurantDetail = (restaurantId) => {
  return api.get(`/restaurants/${restaurantId}`);
}

// 식당 리뷰 가져오기
export const getRestaurantReviews = (restaurantId) => {
  return api.get(`/sns/boards/restaurant/${restaurantId}`);
}

// 식당 공지사항 가져오기
export const getRestaurantNotices = (restaurantId) => {
  return api.get(`/restaurants/${restaurantId}/notice`);
}

// 식당 번호 가져오기
export const getRestaurantId = (userNo) => {
  return api.get(`/restaurants/restaurantId/${userNo}`);
}

// 식당 공지사항 작성하기
export const insertNotice = (noticeData) => {
  return api.post('/restaurants/notice', noticeData, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
}

// 식당 공지사항 수정하기
export const updateNotice = (noticeData) => {
  return api.put('/restaurants/notice', noticeData, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
}

// 식당 공지사항 삭제하기
export const deleteNotice = (restaurantNoticeId) => {
  return api.delete(`/restaurants/notice/${restaurantNoticeId}`);
}

// 식당 조회수 늘리기
export const viewCountRestaurant = (restaurantId) => {
  return api.put(`/restaurants/viewCount/${restaurantId}`);
}

// 식당 사장님 등록되었는 지, 등록 중인 지, 없는 지 판단
export const getCeoLevel = (userNo) => {
  return api.get(`/restaurants/ceolevel/${userNo}`);
}

