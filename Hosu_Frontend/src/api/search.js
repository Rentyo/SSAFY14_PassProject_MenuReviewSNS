import api from './index'

// 지역(시도) 데이터 가져오기
export const getSido = () => {
  return api.get('/search/sido')
}

// 지역(군구) 데이터 가져오기
export const getGungu = (sidoNo) => {
  return api.get(`/search/gungu/${sidoNo}`)
}

// 메뉴 태그 데이터 가져오기
export const getMenuTag = (category) => {
  return api.get(`/search/tag/${category}`)
}

// 매뉴 태그 전체 데이터 가져오기
export const getMenuTagAll = () => {
  return api.get(`/search/tag`)
}

// 식당 검색 결과 가져오기
export const getRestaurant = (sidoNo, gunguCode, category, tagId, page, size, sort, gunguName, tagName, sortBy) => {
  const encodedCategory = encodeURIComponent(category);
  const params = {
    page,
    size,
    sort
  };

  // 추가 파라미터 (AI 검색용)
  if (gunguName) params.gunguName = gunguName;
  if (tagName) params.tagName = tagName;
  if (sortBy) params.sortBy = sortBy;

  return api.get(`/search/restaurant/${sidoNo}/${gunguCode}/${encodedCategory}/${tagId}`, { params });
}
