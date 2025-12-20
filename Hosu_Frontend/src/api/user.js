import api from './index'

// 회원가입
export const signup = (signupData) => {
  return api.post('/users/signup', signupData)
}

// 로그인
export const login = (loginData) => {
  return api.post('/users/login', loginData)
}

// 사용자 정보 조회
export const getUserInfo = (userNo) => {
  return api.get(`/users/${userNo}`)
}

// 사용자 프로필 조회 (팔로워/팔로잉 수 포함)
export const getUserProfile = (userNo, myUserNo = null) => {
  const params = myUserNo ? { myUserNo } : {}
  return api.get(`/users/${userNo}/profile`, { params })
}

// 사용자 정보 수정
export const updateUser = (userNo, updateData) => {
  return api.put(`/users/${userNo}`, updateData)
}

// 회원탈퇴
export const deleteUser = (userNo) => {
  return api.delete(`/users/${userNo}`)
}

// 사용자 권한 조회
export const userRole = () => {
  return api.get(`/users/role`)
}

// 사용자 권한 수정
export const updateUserRole = (userNo, role) => {
  return api.put(`/users/${userNo}/role`, { role })
}

// 랜덤 사용자 목록 조회 (추천용)
export const getRandomUsers = (myUserNo = null, limit = 5) => {
  const params = {}
  if (myUserNo) params.myUserNo = myUserNo
  if (limit) params.limit = limit
  return api.get('/users/random', { params })
}

// 프로필 사진 업로드
export const uploadProfileImage = (userNo, file) => {
  const formData = new FormData()
  formData.append('file', file)

  return api.post(`/users/${userNo}/profile-image`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
