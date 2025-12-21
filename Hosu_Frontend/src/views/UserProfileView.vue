<template>
  <div class="mypage-container">
    <!-- Header -->
    <header class="hosu-header">
      <div class="hosu-logo" @click="$router.push('/')">
        <span class="logo-main">HOSU</span>
        <span class="logo-sub">taste log</span>
      </div>
    </header>

    <div class="mypage-box">
      <!-- 로딩 상태 -->
      <div v-if="loading" class="loading">
        <p>로딩 중...</p>
      </div>

      <!-- 사용자 정보 -->
      <div v-else-if="userProfile" class="profile-section">
        <!-- 프로필 헤더 -->
        <div class="profile-header">
          <div class="profile-image">
            <img
              :src="profileImageUrl"
              :alt="userProfile.nickname"
            />
          </div>
          <div class="profile-info">
            <div class="name-stats-row">
              <div class="name-section">
                <h2>{{ userProfile.nickname }}</h2>
                <!-- 팔로우 버튼 (다른 사용자 프로필일 때만 표시) -->
                <button 
                  v-if="!isMyProfile" 
                  @click="toggleFollow"
                  class="follow-btn"
                  :class="{ following: isFollowing }"
                >
                  <img 
                    :src="followButtonImage" 
                    :alt="isFollowing ? '언팔로우' : '팔로우'"
                    class="follow-icon"
                  />
                  <span class="follow-text">{{ isFollowing ? '언팔로우' : '팔로우' }}</span>
                </button>
                <!-- 식당 이동 버튼 -->
                <button
                  v-if="userProfile.restaurantId"
                  @click="goToRestaurant(userProfile.restaurantId)"
                  class="btn-restaurant"
                >
                  <img 
                    :src="restaurantButtonImage" 
                    alt="식당 구경하기"
                    class="restaurant-icon"
                  />
                  <span class="restaurant-text">페이지</span>
                </button>
              </div>
              
              <!-- 통계 섹션 -->
              <div class="stats-section">
                <div class="stat-item">
                  <span class="stat-number">{{ userProfile.followerCount || 0 }}</span>
                  <span class="stat-label">팔로워</span>
                </div>
                <div class="stat-divider"></div>
                <div class="stat-item">
                  <span class="stat-number">{{ userProfile.followingCount || 0 }}</span>
                  <span class="stat-label">팔로잉</span>
                </div>
                <div class="stat-divider"></div>
                <div class="stat-item">
                  <span class="stat-number">{{ userProfile.postCount || 0 }}</span>
                  <span class="stat-label">게시글</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 에러 메시지 -->
      <div v-else class="error">
        <p>사용자 정보를 불러올 수 없습니다.</p>
        <button @click="$router.push('/')" class="btn-primary">홈으로</button>
      </div>
    </div>

    <!-- 내 리뷰 섹션 -->
    <div v-if="userProfile" class="reviews-container">
      <div class="reviews-header">
        <h3>{{ isMyProfile ? '내가 작성한 리뷰' : `${userProfile.nickname}님의 리뷰` }}</h3>
        <span class="review-count">{{ reviews.length }}개</span>
      </div>

      <!-- 리뷰가 없을 때 -->
      <div v-if="reviews.length === 0" class="no-reviews">
        <p>아직 작성한 리뷰가 없습니다.</p>
      </div>

      <!-- 리뷰 그리드 -->
      <div v-else class="reviews-grid">
        <div
          v-for="review in reviews"
          :key="review.id"
          class="review-card"
          @click="goToReview(review.id)"
        >
          <!-- 대문 이미지 -->
          <div class="review-image">
            <img :src="review.imageUrl" :alt="review.title" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserProfile } from '@/api/user'
import { getUserBoards } from '@/api/sns'
import { follow, unfollow, getFollowStatus } from '@/api/follow'

const route = useRoute()
const router = useRouter()

const userProfile = ref(null)
const reviews = ref([])
const loading = ref(true)
const isFollowing = ref(false)

const myUserNo = localStorage.getItem('userNo')
const targetUserNo = route.params.userNo

// 내 프로필인지 확인
const isMyProfile = computed(() => {
  return userProfile.value && userProfile.value.userNo == myUserNo
})

// 프로필 이미지 URL
const profileImageUrl = computed(() => {
  return userProfile.value?.userImg || new URL('@/assets/user.png', import.meta.url).href
})

// 팔로우 버튼 이미지
const followButtonImage = computed(() => {
  return isFollowing.value 
    ? new URL('@/assets/unfollow.png', import.meta.url).href
    : new URL('@/assets/follow.png', import.meta.url).href
})

// 식당 구경하기 버튼 이미지
const restaurantButtonImage = computed(() => {
  return new URL('@/assets/search.png', import.meta.url).href
})



// 사용자 프로필 로드
const loadUserProfile = async () => {
  try {
    loading.value = true
    const response = await getUserProfile(targetUserNo, myUserNo)
    userProfile.value = response.data
    console.log('프로필 데이터:', response.data)
  } catch (error) {
    console.error('프로필 로딩 실패:', error)
  } finally {
    loading.value = false
  }
}

// 사용자 리뷰 로드
const loadUserReviews = async () => {
  try {
    const response = await getUserBoards(targetUserNo, myUserNo)
    reviews.value = response.data.map(board => ({
      id: board.boardId,
      title: board.title,
      imageUrl: board.boardImg,
      createdAt: board.createdAt
    }))
    console.log('리뷰 데이터:', reviews.value)
  } catch (error) {
    console.error('리뷰 로딩 실패:', error)
  }
}

// 팔로우 상태 조회
const checkFollowStatus = async () => {
  if (isMyProfile.value) return
  
  try {
    const response = await getFollowStatus(myUserNo, targetUserNo)
    isFollowing.value = response.data.isFollowing
  } catch (error) {
    console.error('팔로우 상태 조회 실패:', error)
  }
}

// 팔로우/언팔로우 토글
const toggleFollow = async () => {
  if (!userProfile.value) return
  
  try {
    if (isFollowing.value) {
      await unfollow(myUserNo, targetUserNo)
      isFollowing.value = false
      if (userProfile.value.followerCount > 0) {
        userProfile.value.followerCount--
      }
    } else {
      await follow(myUserNo, targetUserNo)
      isFollowing.value = true
      userProfile.value.followerCount++
    }
  } catch (error) {
    console.error('팔로우 토글 실패:', error)
    alert('팔로우 처리 중 오류가 발생했습니다.')
  }
}

// 리뷰 상세 페이지로 이동
const goToReview = (reviewId) => {
  router.push(`/review/${reviewId}`)
}

// 식당 상세 페이지로 이동
const goToRestaurant = (restaurantId) => {
  router.push(`/restaurant/${restaurantId}`)
}

onMounted(() => {
  loadUserProfile()
  loadUserReviews()
})

// userProfile이 로드되면 팔로우 상태 확인
watch(userProfile, (newVal) => {
  if (newVal && !isMyProfile.value) {
    checkFollowStatus()
  }
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

* {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.mypage-container {
  /* Custom Requested Palette */
  --color-bg: #F2F2F2;
  --color-emphasis: #593527;
  --color-main: #F29F05;
  --color-sub-1: #F2DCB3;
  --color-sub-2: #D97904;

  --bg-base: var(--color-bg);
  --bg-active: #FFFFFF;
  --text-primary: var(--color-emphasis);
  --border-color: var(--color-sub-1);

  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  background: var(--bg-base);
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  color: var(--text-primary);
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* Header */
.hosu-header {
  position: relative;
  z-index: 10;
  height: 80px;
  min-height: 80px;
  flex-shrink: 0;
  padding: 0 2rem;
  background: var(--bg-active);
  border-bottom: 2px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(31, 18, 7, 0.05);
}

.hosu-logo {
  cursor: pointer;
  display: flex;
  flex-direction: column;
  line-height: 1.2;
  transition: transform 0.3s ease;
}

.hosu-logo:hover {
  transform: translateY(-2px);
}

.logo-main {
  font-weight: 800;
  font-size: 1.75rem;
  color: var(--color-main);
  background: none;
  -webkit-background-clip: unset;
  -webkit-text-fill-color: unset;
  letter-spacing: -0.5px;
}

.logo-sub {
  font-size: 0.7rem;
  color: var(--text-primary);
  font-weight: 600;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.mypage-box {
  width: 90%;
  max-width: 1200px;
  margin: 40px auto 0;
  padding: 40px;
  background: #FFFFFF;
  border-radius: 24px;
  border: 1px solid var(--border-color);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.loading {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-primary);
  font-size: 1.1rem;
  opacity: 0.6;
}

.profile-section {
  margin-bottom: 40px;
}

.profile-header {
  display: flex;
  gap: 30px;
  align-items: center;
  padding-bottom: 30px;
  border-bottom: 1px solid var(--border-color);
}

.profile-image {
  flex-shrink: 0;
  width: 120px;
  height: 120px;
}

.profile-image img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid var(--border-color);
  box-shadow: 0 4px 12px rgba(89, 53, 39, 0.15);
  display: block;
}

.profile-info {
  flex: 1;
}

.name-stats-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.name-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.follow-btn {
  padding: 8px 12px;
  border: 2px solid var(--border-color);
  background: transparent;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  min-width: 60px;
  margin-left: 12px;
}

.follow-btn:hover {
  background: var(--bg-base);
  border-color: var(--color-main);
  transform: translateY(-2px);
}

.follow-icon {
  width: 22px;
  height: 22px;
  object-fit: contain;
  transition: all 0.3s ease;
  /* 아이콘 색상을 테마에 맞게 조정 (SVG가 아니므로 filter 사용 고려) */
  filter: brightness(0.2); 
}

.follow-text {
  font-size: 10px;
  color: var(--text-primary);
  font-weight: 600;
  letter-spacing: 0.3px;
}

.follow-btn:hover .follow-icon {
  transform: scale(1.1);
  filter: none; /* hover 시 원래 색상 혹은 강조 색상 */
}

.btn-restaurant {
  padding: 8px 12px;
  border: 2px solid var(--border-color);
  background: transparent;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  min-width: 60px;
  margin-left: 3px;
}

.btn-restaurant:hover {
  background: var(--bg-base);
  border-color: var(--color-main);
  transform: translateY(-2px);
}

.restaurant-icon {
  width: 22px;
  height: 22px;
  object-fit: contain;
  filter: brightness(0.2);
  transition: all 0.3s ease;
}

.restaurant-text {
  font-size: 10px;
  color: var(--text-primary);
  font-weight: 600;
  letter-spacing: 0.3px;
}

.btn-restaurant:hover .restaurant-icon {
  transform: scale(1.1);
  filter: none;
}

.profile-info h2 {
  margin: 0;
  font-size: 28px;
  color: var(--text-primary);
  font-weight: 800;
}

.stats-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-main);
}

.stat-label {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
  opacity: 0.7;
}

.stat-divider {
  width: 1px;
  height: 30px;
  background: var(--border-color);
}

/* 리뷰 섹션 */
.reviews-container {
  width: 90%;
  max-width: 1200px;
  margin: 30px auto 40px;
  padding: 0 20px;
}

.reviews-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 24px;
  background: #FFFFFF;
  border-radius: 12px;
  border: 1px solid var(--border-color);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05); /* Soft shadow */
}

.reviews-header h3 {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.review-count {
  font-size: 18px;
  color: var(--text-primary);
  font-weight: 600;
  opacity: 0.7;
}

.no-reviews {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-primary);
  font-size: 1.1rem;
  background: var(--bg-base);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  opacity: 0.8;
}

.reviews-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 24px;
}

.review-card {
  background: #FFFFFF;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  border: 1px solid var(--border-color);
}

.review-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 30px rgba(89, 53, 39, 0.15);
  border-color: var(--color-main);
  background: #FFFFFF;
}

.review-image {
  position: relative;
  width: 100%;
  padding-top: 100%;
  overflow: hidden;
  background: var(--bg-base);
}

.review-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.review-card:hover .review-image img {
  transform: scale(1.05);
}

.error {
  text-align: center;
  padding: 60px 20px;
  background: #FFFFFF;
  border-radius: 12px;
  border: 1px solid var(--border-color);
}

.error p {
  color: #fca5a5;
  margin-bottom: 20px;
  font-size: 1.1rem;
}

.btn-primary {
  padding: 12px 24px;
  background: var(--color-main);
  color: #FFFFFF;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(242, 159, 5, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(217, 121, 4, 0.4);
  background: var(--color-sub-2);
}

/* 반응형 */
@media (max-width: 768px) {
  .mypage-box {
    width: 95%;
    padding: 24px;
    margin: 20px auto 0;
  }

  .profile-header {
    flex-direction: column;
    text-align: center;
  }

  .name-stats-row {
    flex-direction: column;
    gap: 20px;
  }

  .reviews-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 16px;
  }

  .hosu-header {
    padding: 0 1rem;
  }
}
</style>
