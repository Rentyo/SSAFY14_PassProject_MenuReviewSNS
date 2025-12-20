import api from './index'

// 로그인 API
export const login = (email, password) => {
  return api.post('/users/login', {
    userEmail: email,
    userPw: password,
  })
}

// 로그아웃 API (필요시)
export const logout = () => {
  return api.post('/users/logout')
}

// 사용자 정보 조회 API (필요시)
export const getUserInfo = (userNo) => {
  return api.get(`/users/${userNo}`)
}
