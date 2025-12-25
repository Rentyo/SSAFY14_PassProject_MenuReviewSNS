<template>
  <div class="review-detail-container">
    <!-- Header -->
    <header class="hosu-header">
      <div class="hosu-logo" @click="$router.push('/')">
        <span class="logo-main">HOSU</span>
        <span class="logo-sub">taste log</span>
      </div>
    </header>

    <!-- Main Layout with Right Navigation -->
    <div class="main-layout-review">
      <!-- Main Content -->
      <div class="main-content-review">
        <!-- 로딩 -->
        <div v-if="loading" class="loading">
          <p>로딩 중...</p>
        </div>

        <!-- 리뷰 상세 -->
        <div v-else-if="review" class="review-detail">
          <!-- 작성자 정보 -->
          <div class="author-section">
            <div class="author-info" @click="goToProfile">
              <img
                :src="review.userImg || defaultProfileImage"
                :alt="review.nickName"
                class="author-avatar"
              />
              <div class="author-details">
                <span class="author-name">{{ review.nickName }}</span>
                <span class="review-date">{{ formatDate(review.createdAt) }}</span>
              </div>
            </div>
            
            <!-- 작성자 전용 버튼 -->
            <div v-if="isMyReview" class="author-actions">
              <button @click="goToEdit" class="btn-icon" title="수정">
                <img src="/src/assets/edit.png" alt="수정" class="action-icon" />
              </button>
              <button @click="handleDelete" class="btn-icon" title="삭제">
                <img src="/src/assets/delete.png" alt="삭제" class="action-icon" />
              </button>
            </div>
          </div>

          <!-- 메인 이미지 (Carousel) -->
          <div class="review-image-main">
            <img :src="currentImageUrl" :alt="review.title" class="main-img" />
            
            <!-- 네비게이션 버튼 (이미지가 2장 이상일 때만) -->
            <div v-if="review.images && review.images.length > 1" class="carousel-controls">
              <button @click.stop="prevImage" class="nav-btn prev" aria-label="이전 이미지">&lt;</button>
              <button @click.stop="nextImage" class="nav-btn next" aria-label="다음 이미지">&gt;</button>
              
              <!-- 인디케이터 -->
              <div class="carousel-indicators">
                <span 
                  v-for="(img, idx) in review.images" 
                  :key="img.imageId"
                  class="indicator-dot"
                  :class="{ active: idx === currentImageIndex }"
                  @click.stop="currentImageIndex = idx"
                ></span>
              </div>
            </div>
          </div>

          <!-- 리뷰 정보 -->
          <div class="review-content">
            <!-- 제목과 통계 -->
            <div class="title-stats-row">
              <h1 class="review-title">{{ review.title || '제목 없음' }}</h1>
              
              <!-- 통계 -->
              <div class="review-stats">
                <button @click="toggleLike" class="stat-button">
                  <img 
                    :src="review.isLiked ? '/src/assets/yesheart.png' : '/src/assets/noheart.png'" 
                    alt="좋아요" 
                    class="stat-icon"
                  />
                  <span>{{ review.likeCount || 0 }}</span>
                </button>
                <button @click="showComments = !showComments" class="stat-button">
                  <img src="/src/assets/comment.png" alt="댓글" class="stat-icon" />
                  <span>{{ review.commentCount || 0 }}</span>
                </button>
              </div>
            </div>

            <!-- 식당 정보 -->
            <div v-if="restaurant" class="restaurant-info">
              <span class="restaurant-name" @click="goToRestaurant">{{ restaurant.name }}</span>
            </div>

            <!-- 태그 -->
            <div v-if="review.boardTag" class="review-tags">
              {{ review.boardTag }}
            </div>

            <!-- 내용 -->
            <div class="review-text">
              {{ review.content }}
            </div>

            <!-- 메뉴별 상세 리뷰 리스트 -->
            <div v-if="review.images && review.images.length > 0" class="menu-review-list">
              <h3 class="section-title">주문 메뉴 & 리뷰</h3>
              <div class="menu-list-items">
                <div 
                  v-for="(image, index) in review.images"
                  :key="image.imageId"
                  class="menu-review-item"
                  :class="{ 'active': index === currentImageIndex }"
                  @click="currentImageIndex = index"
                >
                  <!-- 이미지 썸네일 제거 -->
                  
                  <div class="menu-info-right">
                    <div class="menu-header">
                      <span class="menu-name">메뉴 #{{ image.menuName }}</span>
                      <div class="menu-rating">
                        <span class="star-icon">⭐</span>
                        <span class="rating-val">{{ image.menuRating || 0 }}</span>
                      </div>
                    </div>
                    
                    <p class="one-line-review">
                      {{ image.oneLineReview || '한줄평이 없습니다.' }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 댓글 섹션 -->
          <CommentSection 
            v-if="review" 
            :boardId="review.boardId" 
            :showForm="showComments"
            @commentCountChanged="updateCommentCount"
          />
        </div>
        <!-- 에러 -->
        <div v-else class="error">
          <p>리뷰를 찾을 수 없습니다.</p>
          <button @click="$router.back()" class="btn-primary">돌아가기</button>
        </div>
      </div>

      <SidebarProfile class="right-navigation" />
    </div>
  </div>
</template>

<script setup>
import SidebarProfile from '@/components/common/SidebarProfile.vue'
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getBoardDetail, toggleBoardLike, deleteBoard } from '@/api/sns'
import { getRestaurantDetail } from '@/api/restaurants'
import CommentSection from '@/components/CommentSection.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const review = ref(null)
const restaurant = ref(null)
const loading = ref(true)
const showComments = ref(false)
const currentImageIndex = ref(0)



// 기본 프로필 이미지
const defaultProfileImage = new URL('@/assets/user.png', import.meta.url).href

// Logout handler
const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

// 현재 표시할 이미지 URL (이미지 목록이 없으면 boardImg 사용)
const currentImageUrl = computed(() => {
  if (review.value && review.value.images && review.value.images.length > 0) {
    return review.value.images[currentImageIndex.value].imageUrl
  }
  return review.value?.boardImg || 'https://via.placeholder.com/500'
})

// 이전 이미지
const prevImage = () => {
  if (!review.value || !review.value.images || review.value.images.length <= 1) return
  if (currentImageIndex.value > 0) {
    currentImageIndex.value--
  } else {
    currentImageIndex.value = review.value.images.length - 1
  }
}

// 다음 이미지
const nextImage = () => {
  if (!review.value || !review.value.images || review.value.images.length <= 1) return
  if (currentImageIndex.value < review.value.images.length - 1) {
    currentImageIndex.value++
  } else {
    currentImageIndex.value = 0
  }
}

// 내 리뷰인지 확인
const isMyReview = computed(() => {
  if (!review.value || !userStore.userNo) return false
  return Number(review.value.userNo) === Number(userStore.userNo)
})

// 날짜 포맷
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

// 리뷰 불러오기
const loadReview = async () => {
  const boardId = route.params.id

  try {
    loading.value = true
    const response = await getBoardDetail(boardId, userStore.userNo)
    review.value = response.data
    console.log('리뷰 상세:', response.data)
    
    // 식당 정보 로드
    if (response.data.restaurantId) {
      loadRestaurant(response.data.restaurantId)
    }
  } catch (error) {
    console.error('리뷰 로딩 실패:', error)
  } finally {
    loading.value = false
  }
}

// 식당 정보 로드
const loadRestaurant = async (restaurantId) => {
  try {
    const response = await getRestaurantDetail(restaurantId)
    restaurant.value = response.data
  } catch (error) {
    console.error('식당 정보 로딩 실패:', error)
  }
}

// 식당 페이지로 이동
const goToRestaurant = () => {
  if (restaurant.value) {
    router.push(`/restaurant/${restaurant.value.id}`)
  }
}

// 수정 페이지로 이동
const goToEdit = () => {
  router.push(`/review/${route.params.id}/edit`)
}

// 삭제
const handleDelete = async () => {
  if (!confirm('정말 이 리뷰를 삭제하시겠습니까?')) return

  try {
    await deleteBoard(review.value.boardId, userStore.userNo)
    
    alert('리뷰가 삭제되었습니다.')
    router.push('/feed')
  } catch (error) {
    console.error('리뷰 삭제 실패:', error)
    alert('리뷰 삭제 중 오류가 발생했습니다.')
  }
}

// 좋아요 토글
const toggleLike = async () => {
  const boardId = route.params.id

  if (!userStore.userNo) {
    alert('로그인이 필요합니다.')
    return
  }

  try {
    await toggleBoardLike(boardId, userStore.userNo)
    
    // 좋아요 상태 토글
    review.value.isLiked = !review.value.isLiked
    
    // 좋아요 개수 업데이트
    if (review.value.isLiked) {
      review.value.likeCount = (review.value.likeCount || 0) + 1
    } else {
      review.value.likeCount = Math.max((review.value.likeCount || 0) - 1, 0)
    }
  } catch (error) {
    console.error('좋아요 처리 실패:', error)
    alert('좋아요 처리 중 오류가 발생했습니다.')
  }
}

// 프로필 페이지로 이동
const goToProfile = () => {
  if (!review.value) return
  
  if (review.value.userNo == userStore.userNo) {
    router.push('/mypage')
  } else {
    router.push(`/profile/${review.value.userNo}`)
  }
}

// 댓글 개수 업데이트
const updateCommentCount = (count) => {
  if (review.value) {
    review.value.commentCount = count
  }
}

onMounted(() => {
  loadReview()
})
</script>


<style scoped>
.review-detail-container {
  /* Custom Requested Palette */
  --color-bg: #F2F2F2;
  --color-emphasis: #593527;
  --color-main: #F29F05;
  --color-sub-1: #F2DCB3;
  --color-sub-2: #D97904;

  --bg-base: var(--color-bg);
  --bg-inactive: var(--color-bg);
  --bg-active: #FFFFFF;
  --sub-dark: var(--color-emphasis);
  --sub-light: var(--color-sub-1);
  --main-color: var(--color-sub-1);
  --text-primary: var(--color-emphasis);
  --border-color: var(--color-sub-1);

  /* Mapping for decorative naming consistency */
  --baltic-amber: var(--color-main);
  --chocolate-martini: var(--color-emphasis);
  --chanterelle: var(--color-sub-2);
  --mocha-mousse: var(--color-emphasis);
  --cream-tan: var(--color-sub-1);
  --sirocco: var(--color-sub-1);
  --safari: var(--color-sub-2);
  --cannoli-cream: var(--color-bg);

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
  color: var(--color-emphasis);
  font-weight: 600;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.main-layout-review {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 2rem;
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.main-content-review {
  flex: 1;
  width: 100%;
  max-width: 800px;
  min-width: 0;
}

.right-navigation {
  order: 2;
  flex-shrink: 0;
  width: 280px;
  position: sticky;
  top: 100px;
  max-height: calc(100vh - 120px);
  overflow-y: auto;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.right-navigation::-webkit-scrollbar {
  display: none;
}

@media (max-width: 1200px) {
  .main-layout-review {
    flex-direction: column;
    align-items: center;
  }
  
  .right-navigation {
    order: 1;
    width: 100%;
    position: static;
    margin-bottom: 2rem;
    max-height: none;
  }
}

.review-detail {
  width: 100%;
  max-width: 675px;
  margin: 0 auto 80px;
  background: #FFFFFF;
  border: 1px solid var(--color-sub-1);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}


/* 메인 이미지 */
.review-image-main {
  position: relative;
  width: 100%;
  padding-top: 100%; /* 정사각형 비율 */
  overflow: hidden;
  background: #f0f0f0;
}

.review-image-main img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: rgba(0, 0, 0, 0.05);
}

/* 컨텐츠 */
.review-content {
  padding: 40px;
}

/* 제목과 통계 행 */
.title-stats-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 20px;
}

.review-title {
  font-size: 32px;
  font-weight: 800;
  margin: 0;
  color: var(--color-emphasis);
  flex: 1;
}

.edit-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.btn-icon {
  padding: 8px;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 50%;
}

.btn-icon:hover {
  background: rgba(0, 0, 0, 0.05);
}

.action-icon {
  width: 24px;
  height: 24px;
  display: block;
}

.btn-action {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-edit {
  background: #4caf50;
  color: #2D3436;
}

.btn-edit:hover {
  background: #45a049;
}

.btn-delete {
  background: #f44336;
  color: #2D3436;
}

.btn-delete:hover {
  background: #da190b;
}

.btn-save {
  background: #2196f3;
  color: #2D3436;
}

.btn-save:hover {
  background: #0b7dda;
}

.btn-cancel-edit {
  background: #9e9e9e;
  color: #2D3436;
}

.btn-cancel-edit:hover {
  background: #757575;
}

/* 수정 모드 입력 */
.edit-title-input {
  width: 100%;
  padding: 12px;
  font-size: 28px;
  font-weight: bold;
  border: 2px solid #ddd;
  border-radius: 8px;
  box-sizing: border-box;
}

.edit-title-input:focus {
  outline: none;
  border-color: #2196f3;
}

.edit-content-textarea {
  width: 100%;
  padding: 15px;
  font-size: 16px;
  line-height: 1.8;
  border: 2px solid #ddd;
  border-radius: 8px;
  resize: vertical;
  font-family: inherit;
  box-sizing: border-box;
  margin-bottom: 20px;
}

.edit-content-textarea:focus {
  outline: none;
  border-color: #2196f3;
}

/* 통계 */
.review-stats {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-shrink: 0;
}

.stat-button {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 20px;
  background: white;
  border: 1.5px solid var(--color-sub-1);
  border-radius: 24px;
  cursor: pointer;
  font-size: 18px;
  color: var(--color-emphasis);
  transition: all 0.3s;
}

.stat-button:hover {
  background: #f5f5f5;
  border-color: var(--color-main);
}

.stat-icon {
  width: 28px;
  height: 28px;
  object-fit: contain;
}

.review-tags {
  font-size: 14px;
  color: var(--color-main);
  margin-bottom: 30px;
}

.restaurant-info {
  margin-bottom: 20px;
  padding: 12px 16px;
  background: var(--color-sub-1);
  border: 1px solid var(--color-sub-1);
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.restaurant-label {
  font-size: 14px;
  color: var(--color-emphasis);
  font-weight: 500;
}

.restaurant-name {
  font-size: 16px;
  color: var(--color-emphasis);
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
}

.restaurant-name:hover {
  color: var(--color-main);
  text-decoration: underline;
}

/* 작성자 섹션 */
.author-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  gap: 12px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: background 0.2s;
  flex: 1;
}

.author-info:hover {
  opacity: 0.8;
}

.author-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--color-sub-1);
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.author-name {
  font-weight: 600;
  color: var(--color-emphasis);
  font-size: 16px;
}

.review-date {
  font-size: 14px;
  color: #95A5A6;
}

/* 내용 */
.review-text {
  font-size: 16px;
  line-height: 1.8;
  color: var(--color-emphasis);
  margin-bottom: 40px;
  white-space: pre-wrap;
}

/* --- Carousel Controls --- */
.carousel-controls {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none; /* 버튼만 클릭 가능하게 */
}

.nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(0, 0, 0, 0.4);
  color: #FFFFFF;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  cursor: pointer;
  pointer-events: auto;
  transition: 0.2s;
  z-index: 2;
}

.nav-btn:hover {
  background: rgba(0, 0, 0, 0.7);
}

.nav-btn.prev {
  left: 10px;
}

.nav-btn.next {
  right: 10px;
}

.carousel-indicators {
  position: absolute;
  bottom: 15px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
  pointer-events: auto;
  z-index: 2;
}

.indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: 0.2s;
}

.indicator-dot.active {
  background: white;
  transform: scale(1.2);
}

/* --- Menu Review List --- */
.menu-review-list {
  margin-top: 40px;
  border-top: 1px solid #eee;
  padding-top: 30px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 20px;
  color: var(--color-emphasis);
}

.menu-list-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.menu-review-item {
  display: flex;
  gap: 20px;
  padding: 16px;
  border: 1px solid var(--color-sub-1);
  border-radius: 12px;
  background: #FFFFFF;
  cursor: pointer;
  transition: all 0.2s;
}

.menu-review-item:hover {
  border-color: var(--color-main);
  background: var(--bg-base);
}

.menu-review-item.active {
  border-color: var(--color-main);
  background: var(--color-sub-1);
  box-shadow: 0 4px 15px rgba(242, 159, 5, 0.1);
}



.menu-info-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.menu-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.menu-name {
  font-weight: 700;
  font-size: 16px;
  color: var(--color-emphasis);
}

.menu-rating {
  display: flex;
  align-items: center;
  gap: 4px;
  background: white;
  padding: 4px 8px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.star-icon {
  font-size: 12px;
}

.rating-val {
  font-weight: 700;
  font-size: 14px;
  color: #333;
}

.one-line-review {
  margin: 0;
  font-size: 14px;
  color: var(--color-emphasis);
  line-height: 1.5;
  background: #FFF5E6;
  padding: 10px;
  border-radius: 8px;
  border: 1.5px solid var(--color-sub-1);
}

/* 에러 */
.error {
  text-align: center;
  padding: 100px 20px;
}

.error p {
  margin-bottom: 20px;
  color: #636E72;
}

.btn-primary {
  padding: 12px 30px;
  background: var(--color-main);
  color: #FFFFFF;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(217, 121, 4, 0.4);
  background: var(--color-sub-2);
}

/* 반응형 */
@media (max-width: 768px) {
  .review-image-main {
    height: 250px;
  }

  .review-content {
    padding: 20px;
  }

  .review-title {
    font-size: 24px;
  }

  .review-images {
    grid-template-columns: 1fr;
  }
}

/* Main Layout with Right Navigation */
.main-layout-review {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 0;
  padding: 2rem 0;
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 1180px;
  margin: 0 auto;
}

.main-content-review {
  order: 1;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* Right Navigation */
.right-navigation {
  order: 2;
  flex-shrink: 0;
  margin-top: 16px;
  position: sticky;
  top: 100px;
  align-self: flex-start;
  max-height: calc(100vh - 120px);
}
</style>
