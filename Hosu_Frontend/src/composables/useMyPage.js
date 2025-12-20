import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserProfile, updateUser, deleteUser } from '@/api/user'
import { getUserBoards } from '@/api/sns'
import { useUserStore } from '@/stores/user'

export function useMyPage() {
  const router = useRouter()

  // 상태 관리
  const userProfile = ref(null)
  const loading = ref(true)
  const showEditModal = ref(false)
  const updating = ref(false)
  const updateError = ref('')

  const editForm = ref({
    nickname: '',
    phoneNumber: '',
    userPw: '',
    userImg: '',
  })

  const reviews = ref([])

  // 날짜 포맷 함수
  const formatDate = (dateString) => {
    if (!dateString) return ''
    const date = new Date(dateString)
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    })
  }

  // 사용자 프로필 불러오기
  const loadUserProfile = async () => {
    const userNo = localStorage.getItem('userNo')

    if (!userNo) {
      router.push('/login')
      return
    }

    try {
      loading.value = true
      const response = await getUserProfile(userNo)
      userProfile.value = response.data
      console.log('프로필 데이터:', response.data)
    } catch (error) {
      console.error('프로필 로딩 실패:', error)
    } finally {
      loading.value = false
    }
  }

  // 정보 수정
  const handleUpdate = async () => {
    const userNo = localStorage.getItem('userNo')
    updateError.value = ''
    updating.value = true

    try {
      // null이나 빈 값 제거
      const updateData = {}
      if (editForm.value.nickname && editForm.value.nickname.trim()) {
        updateData.nickname = editForm.value.nickname
      }
      if (editForm.value.phoneNumber && editForm.value.phoneNumber.trim()) {
        updateData.phoneNumber = editForm.value.phoneNumber
      }
      if (editForm.value.userPw && editForm.value.userPw.trim()) {
        updateData.userPw = editForm.value.userPw
      }
      if (editForm.value.userImg && editForm.value.userImg.trim()) {
        updateData.userImg = editForm.value.userImg
      }

      await updateUser(userNo, updateData)

      // 정보 새로고침
      await loadUserProfile()

      // localStorage 업데이트
      if (updateData.nickname) {
        localStorage.setItem('nickname', updateData.nickname)
      }

      // 폼 초기화 및 모달 닫기
      editForm.value = {
        nickname: '',
        phoneNumber: '',
        userPw: '',
        userImg: '',
      }
      showEditModal.value = false

      alert('정보가 수정되었습니다!')
    } catch (error) {
      console.error('정보 수정 실패:', error)
      if (error.response?.status === 400) {
        updateError.value = '닉네임 또는 전화번호가 중복되었습니다.'
      } else {
        updateError.value = '정보 수정 중 오류가 발생했습니다.'
      }
    } finally {
      updating.value = false
    }
  }

  // 로그아웃
  const handleLogout = () => {
    if (confirm('로그아웃 하시겠습니까?')) {
      const userStore = useUserStore()
      userStore.clearUser()
      router.push('/login')
    }
  }

  // 회원탈퇴 확인
  const confirmDelete = () => {
    const confirmed = confirm(
      '정말로 탈퇴하시겠습니까?\n탈퇴 시 모든 데이터가 삭제되며 복구할 수 없습니다.',
    )

    if (confirmed) {
      handleDelete()
    }
  }

  // 회원탈퇴 실행
  const handleDelete = async () => {
    const userNo = localStorage.getItem('userNo')

    try {
      await deleteUser(userNo)
      alert('회원탈퇴가 완료되었습니다.')
      localStorage.clear()
      router.push('/login')
    } catch (error) {
      console.error('회원탈퇴 실패:', error)
      alert('회원탈퇴 중 오류가 발생했습니다.')
    }
  }
  // 사용자 리뷰 불러오기
  const loadUserReviews = async () => {
    const userNo = localStorage.getItem('userNo')

    if (!userNo) return

    try {
      // 실제 API 호출
      const response = await getUserBoards(userNo, userNo)

      // 데이터 매핑
      reviews.value = response.data.map((board) => ({
        id: board.boardId,
        title: board.title || '제목 없음',
        imageUrl: board.boardImg || 'https://via.placeholder.com/300x200',
        likeCount: board.likeCount || 0,
        createdAt: board.createdAt,
      }))

      console.log('리뷰 데이터:', reviews.value)
    } catch (error) {
      console.error('리뷰 로딩 실패:', error)
      reviews.value = []
    }
  }

  // 리뷰 상세로 이동
  const goToReview = (reviewId) => {
    console.log('리뷰 이동:', reviewId)
    router.push(`/review/${reviewId}`)
  }

  // 컴포넌트 마운트 시 프로필 로드
  onMounted(() => {
    loadUserProfile()
    loadUserReviews()
  })

  // 필요한 것들 내보내기
  return {
    // 상태
    userProfile,
    loading,
    showEditModal,
    updating,
    updateError,
    editForm,
    reviews,

    // 함수
    formatDate,
    loadUserProfile,
    handleUpdate,
    handleLogout,
    confirmDelete,
    goToReview,
  }
}
