import axios from 'axios'

// 개발/프로덕션 환경에 따라 baseURL 자동 선택
const getBaseURL = () => {
  // 환경 변수가 있으면 사용
  if (import.meta.env.VITE_API_BASE_URL) {
    return import.meta.env.VITE_API_BASE_URL
  }

  // 로컬 개발 환경 (localhost에서 실행 중)
  if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
    return 'http://localhost:8080/api'
  }

  // 프로덕션 환경
  return 'https://3.39.4.23.sslip.io/api'
}

//Axios 인스턴스 생성
const api = axios.create({
  baseURL: getBaseURL(),
  timeout: 5000, //5초안에 응답이없으면 에러
  headers: {
    'Content-Type': 'application/json', //json으로 데이터전송
  },
})

// 요청 인터셉터 추가
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

export default api
