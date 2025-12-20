import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api/auth'
import { getUserProfile } from '@/api/user'
import { useUserStore } from '@/stores/user'

export function useLogin() {
  const router = useRouter()
  const userStore = useUserStore()

  // 상태 관리
  const email = ref('')
  const password = ref('')
  const loading = ref(false)
  const errorMessage = ref('')

  // 로그인 처리 함수
  const handleLogin = async () => {
    loading.value = true
    errorMessage.value = ''

    try {
      const response = await login(email.value, password.value)

      console.log('로그인 성공!', response.data)

      // Pinia store에 사용자 정보 저장
      userStore.setUser(response.data)

      // 사용자 프로필 정보 가져오기 (팔로잉/팔로워/리뷰 수)
      try {
        const profileResponse = await getUserProfile(response.data.userNo, response.data.userNo)
        console.log('프로필 정보:', profileResponse.data)

        // 통계 정보 업데이트
        userStore.updateUserStats({
          followingCount: profileResponse.data.followingCount || 0,
          followerCount: profileResponse.data.followerCount || 0,
          reviewCount: profileResponse.data.postCount || 0
        })
      } catch (profileError) {
        console.error('프로필 정보 로딩 실패:', profileError)
        // 프로필 정보 로딩 실패해도 로그인은 계속 진행
      }

      alert(`환영합니다, ${response.data.nickname}님!`)

      // 식당 등록 여부 저장 (resp.data.owner가 true면 등록된 상태)
      if (response.data.owner) {
        localStorage.setItem('restaurantRegistered', 'true')
      } else {
        localStorage.setItem('restaurantRegistered', 'false')
      }

      // role에 따라 다른 페이지로 이동
      if (response.data.role === 'CEO') {
        router.push('/mypage/ceo')
      } else {
        router.push('/')
      }
    } catch (error) {
      console.error('로그인 실패:', error)

      if (error.response?.status === 401) {
        errorMessage.value = '이메일 또는 비밀번호가 잘못되었습니다.'
      } else if (error.response?.status === 404) {
        errorMessage.value = 'API를 찾을 수 없습니다.'
      } else {
        errorMessage.value = '로그인 중 오류가 발생했습니다.'
      }
    } finally {
      loading.value = false
    }
  }

  // 필요한 것들만 내보내기
  return {
    email,
    password,
    loading,
    errorMessage,
    handleLogin,
  }
}
