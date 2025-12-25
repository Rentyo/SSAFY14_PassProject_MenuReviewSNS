<template>
  <div class="feed-container">
    <!-- Header -->
    <header class="hosu-header">
      <div class="hosu-logo" @click="$router.push('/')">
        <span class="logo-main">HOSU</span>
        <span class="logo-sub">taste log</span>
      </div>
    </header>

    <!-- Main Layout with Right Navigation -->
    <div class="main-layout-feed">
      <!-- Main Content -->
      <div class="main-content-feed">
        <div class="feed-content">
          <!-- 타이틀 -->
          <div class="feed-title">
            <h1>{{ isRandomFeed ? '추천 리뷰' : '팔로잉 피드' }}</h1>
            <p>{{ isRandomFeed ? '다른 사용자들의 최신 리뷰를 확인해보세요' : '내가 팔로우한 사람들의 최신 리뷰' }}</p>
          </div>

          <!-- AI 검색 섹션 -->
          <div class="ai-search-section">
            <div class="search-header">
              <div class="search-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M21 21L16.65 16.65M19 11C19 15.4183 15.4183 19 11 19C6.58172 19 3 15.4183 3 11C3 6.58172 6.58172 3 11 3C15.4183 3 19 6.58172 19 11Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </div>
              <h2>AI 맛집 검색</h2>
              <div class="ai-badge">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" fill="currentColor"/>
                </svg>
                AI
              </div>
            </div>
            
            <p class="search-description">
              원하는 맛집을 자연어로 물어보세요. AI가 딱 맞는 리뷰를 찾아드립니다.
            </p>

            <div class="search-input-wrapper">
              <input
                v-model="searchQuery"
                type="text"
                placeholder="예: 대전에서 분위기 좋은 파스타 맛집 추천해줘"
                class="search-input"
                @keyup.enter="handleSearch"
              />
              <button 
                class="search-button"
                @click="handleSearch"
                :disabled="!searchQuery.trim()"
              >
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M5 12H19M19 12L12 5M19 12L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>

            <!-- 검색 예시 -->
            <div class="search-examples">
              <span class="example-label">예시:</span>
              <button 
                v-for="(example, index) in searchExamples" 
                :key="index"
                class="example-chip"
                @click="searchQuery = example"
              >
                {{ example }}
              </button>
            </div>
          </div>

          <!-- 로딩 -->
          <div v-if="loading" class="loading">
            <p>피드를 불러오는 중...</p>
          </div>

          <!-- 빈 피드 메시지 (팔로잉 없을 때) -->
          <div v-else-if="feedItems.length === 0 && !isRandomFeed" class="empty-feed">
            <h2>팔로우한 사람이 없습니다</h2>
            <p>다른 사용자를 팔로우하고 그들의 리뷰를 확인해보세요!</p>
            <button @click="loadRandomFeed" class="btn-load-random">
              추천 리뷰 보기
            </button>
          </div>

          <div v-else>
            <!-- 추천 사용자 섹션 (랜덤 피드일 때만) -->
            <div v-if="isRandomFeed && recommendedUsers.length > 0" class="recommended-users-section">
              <h2>추천 사용자</h2>
              <p class="section-subtitle">이 사용자들을 팔로우해보세요</p>
              <div class="recommended-users">
                <div
                  v-for="user in recommendedUsers"
                  :key="user.userNo"
                  class="user-card"
                  @click="goToProfile(user.userNo)"
                >
                  <img
                    :src="getUserImageUrl(user.userImg)"
                    :alt="user.nickName"
                    class="user-avatar"
                  />
                  <div class="user-info">
                    <span class="user-name">{{ user.nickname || user.nickName || '사용자' }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div
              v-for="item in feedItems"
              :key="item.boardId"
              class="feed-item"
              @click="goToReview(item.boardId)"
            >
              <!-- 작성자 정보 -->
              <div class="author-section" @click.stop="goToProfile(item.userNo)">
                <img
                  :src="getUserImageUrl(item.userImg)"
                  :alt="item.nickName"
                  class="author-avatar"
                />
                <div class="author-info">
                  <span class="author-name">{{ item.nickName }}</span>
                  <span class="post-time">{{ formatTime(item.createdAt) }}</span>
                </div>
              </div>

              <!-- 리뷰 이미지 -->
              <div class="feed-image">
                <img :src="item.boardImg" :alt="item.title" />
              </div>

              <!-- 리뷰 정보 -->
              <div class="feed-info">
                <h3 class="feed-title-text">{{ item.title || '제목 없음' }}</h3>
                <p class="feed-content-text">{{ item.content }}</p>
                
                <!-- 통계 -->
                <div class="feed-stats">
                  <span class="stat">
                    <img src="@/assets/noheart.png" alt="좋아요" class="stat-icon" />
                    {{ item.likeCount || 0 }}
                  </span>
                  <span class="stat">
                    <img src="@/assets/comment.png" alt="댓글" class="stat-icon" />
                    {{ item.commentCount || 0 }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <SidebarProfile class="right-navigation" />
    </div>

    <!-- AI Search Result Modal -->
    <div v-if="showSearchDialog" class="modal-overlay" @click.self="showSearchDialog = false">
      <div class="modal-content">
        <button class="modal-close" @click="showSearchDialog = false">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <div v-if="isAiSearching" class="loading-state">
          <div class="spinner"></div>
          <p>데이터를 불러오는 중입니다</p>
        </div>

        <div v-else class="result-container">
           <h3 class="result-title">AI 검색 결과</h3>
           <div v-if="aiSearchResults.length === 0" class="no-result">
              검색 결과가 없습니다.
           </div>
           <div v-else class="result-grid">
             <div 
               v-for="item in aiSearchResults" 
               :key="item.boardId" 
               class="grid-item" 
               @click="goToReview(item.boardId)"
             >
               <div class="image-wrapper">
                 <img :src="item.boardImg" :alt="item.title" />
                 <div class="grid-item-overlay">
                   <span class="view-text">리뷰 보기</span>
                 </div>
               </div>
               <div class="item-description">
                 {{ item.oneLineReview || '이 메뉴를 강력 추천합니다! ✨' }}
               </div>
             </div>
           </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import SidebarProfile from '@/components/common/SidebarProfile.vue'
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFollowingFeed, getRandomBoards, getFeedUsingAI } from '@/api/sns'
import { getRandomUsers } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const feedItems = ref([])
const loading = ref(true)
const isRandomFeed = ref(false)
const recommendedUsers = ref([])

// AI Dialog State
const showSearchDialog = ref(false)
const isAiSearching = ref(false)
const aiSearchResults = ref([])



// AI 검색 관련
const searchQuery = ref('')
const searchExamples = [
  '대전 파스타 맛집',
  '분위기 좋은 카페',
  '혼밥하기 좋은 곳'
]

// AI 검색 핸들러
const handleSearch = async () => {
  if (!searchQuery.value.trim()){
    console.log("빈값");
    return;
  } 
  
  showSearchDialog.value = true
  isAiSearching.value = true
  aiSearchResults.value = []

  try {
    const result = await getFeedUsingAI(searchQuery.value)
    // Take top 5 items for 1x5 grid
    aiSearchResults.value = result.data.slice(0, 5)
  } catch (error) {
    console.error("AI Search Error:", error)
  } finally {
    isAiSearching.value = false
  }
}

// Logout handler
const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

// 시간 포맷 (상대 시간)
const formatTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date

  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '방금 전'
  if (minutes < 60) return `${minutes}분 전`
  if (hours < 24) return `${hours}시간 전`
  if (days < 7) return `${days}일 전`

  return date.toLocaleDateString('ko-KR')
}

// 추천 사용자 추출 (중복 제거)
const extractRecommendedUsers = (items) => {
  const userMap = new Map()
  const myUserNo = localStorage.getItem('userNo')
  
  console.log('추천 사용자 추출 시작, 아이템 수:', items.length)
  console.log('내 userNo:', myUserNo)
  
  items.forEach(item => {
    console.log('아이템 userNo:', item.userNo, 'nickName:', item.nickName)
    // 본인은 제외
    if (item.userNo && String(item.userNo) !== String(myUserNo) && !userMap.has(item.userNo)) {
      userMap.set(item.userNo, {
        userNo: item.userNo,
        nickName: item.nickName,
        userImg: item.userImg
      })
    }
  })
  
  console.log('추출된 사용자 수:', userMap.size)
  
  // 최대 5명까지만
  return Array.from(userMap.values()).slice(0, 5)
}

// 피드 불러오기
const loadFeed = async () => {
  const userNo = localStorage.getItem('userNo')
  
  if (!userNo) {
    router.push('/login')
    return
  }

  try {
    loading.value = true
    const response = await getFollowingFeed(userNo)
    feedItems.value = response.data
    
    // 팔로잉 피드가 비어있으면 자동으로 랜덤 피드 로드
    if (feedItems.value.length === 0) {
      await loadRandomFeed()
    }
    
    console.log('피드 데이터:', response.data)
  } catch (error) {
    console.error('피드 로딩 실패:', error)
    // 에러 발생 시에도 랜덤 피드 시도
    await loadRandomFeed()
  } finally {
    loading.value = false
  }
}

// 랜덤 피드 불러오기
const loadRandomFeed = async () => {
  const userNo = localStorage.getItem('userNo')
  
  try {
    loading.value = true
    isRandomFeed.value = true
    
    // 랜덤 리뷰와 추천 사용자를 동시에 로드
    const [boardsResponse, usersResponse] = await Promise.all([
      getRandomBoards(userNo, 20),
      getRandomUsers(userNo, 5)
    ])
    
    feedItems.value = boardsResponse.data
    recommendedUsers.value = usersResponse.data
    
    console.log('랜덤 피드 데이터:', boardsResponse.data)
    console.log('추천 사용자:', usersResponse.data)
  } catch (error) {
    console.error('랜덤 피드 로딩 실패:', error)
  } finally {
    loading.value = false
  }
}

// 리뷰 상세 페이지로 이동
const goToReview = (boardId) => {
  router.push(`/review/${boardId}`)
}

// 프로필 페이지로 이동
const goToProfile = (userNo) => {
  const myUserNo = localStorage.getItem('userNo')
  if (userNo == myUserNo) {
    router.push(`/mypage`)
  } else {
    router.push(`/profile/${userNo}`)
  }
}

// 사용자 이미지 URL 가져오기 (기본 이미지 포함)
const getUserImageUrl = (userImg) => {
  return userImg || new URL('@/assets/user.png', import.meta.url).href
}

onMounted(() => {
  loadFeed()
})
</script>

<style scoped>
.feed-container {
  /* Custom Requested Palette */
  --color-bg: #F2F2F2;
  --color-emphasis: #593527;
  --color-main: #F29F05;
  --color-sub-1: #F2DCB3;
  --color-sub-2: #D97904;

  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  background: var(--color-bg);
  display: flex;
  flex-direction: column;
  overflow-y: auto;
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
  background: #FFFFFF;
  border-bottom: 2px solid var(--color-sub-1);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(89, 53, 39, 0.08);
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
  letter-spacing: -0.5px;
}

.logo-sub {
  font-size: 0.7rem;
  color: var(--color-emphasis);
  font-weight: 600;
  letter-spacing: 2px;
  text-transform: uppercase;
}

/* Content */
.feed-content {
  max-width: 800px;
  margin: 40px auto;
  padding: 0 20px;
}

.feed-title {
  margin-bottom: 40px;
  text-align: center;
}

.feed-title h1 {
  font-size: 32px;
  font-weight: 800;
  color: var(--color-emphasis);
  margin: 0 0 10px 0;
  letter-spacing: -1px;
}

.feed-title p {
  font-size: 16px;
  color: var(--color-emphasis);
  opacity: 0.7;
  margin: 0;
}

/* AI Search Section */
.ai-search-section {
  background: white;
  border: 1px solid var(--color-sub-1);
  border-radius: 24px;
  padding: 32px;
  margin-bottom: 30px;
  box-shadow: 0 10px 30px rgba(89, 53, 39, 0.08);
  position: relative;
  overflow: hidden;
}

.ai-search-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, 
    transparent 0%,
    rgba(255, 107, 107, 0.15) 50%,
    transparent 100%
  );
}

.search-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.search-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-sub-1);
  border-radius: 12px;
  color: var(--color-emphasis);
}

.search-header h2 {
  font-size: 22px;
  font-weight: 800;
  color: var(--color-emphasis);
  margin: 0;
  flex: 1;
}

.ai-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 5px 14px;
  background: var(--color-main);
  border-radius: 20px;
  color: #FFFFFF;
  font-size: 12px;
  font-weight: 800;
  box-shadow: 0 4px 8px rgba(242, 159, 5, 0.2);
}

.search-description {
  font-size: 14.5px;
  color: var(--color-emphasis);
  opacity: 0.8;
  margin: 0 0 24px 0;
  line-height: 1.6;
}

.search-input-wrapper {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.search-input {
  flex: 1;
  padding: 14px 20px;
  background: #F8F9FA;
  border: 2px solid transparent;
  border-radius: 14px;
  color: var(--color-emphasis);
  font-size: 15px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-input::placeholder {
  color: #ADB5BD;
}

.search-input:focus {
  outline: none;
  background: white;
  border-color: var(--color-main);
  box-shadow: 0 0 0 4px rgba(242, 159, 5, 0.1);
}

.search-button {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-main);
  border: none;
  border-radius: 14px;
  color: #FFFFFF;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 15px rgba(242, 159, 5, 0.3);
}

.search-button:hover:not(:disabled) {
  transform: translateY(-2px) scale(1.05);
  background: var(--color-sub-2);
  box-shadow: 0 6px 20px rgba(217, 121, 4, 0.4);
}

.search-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.search-examples {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.example-label {
  font-size: 13px;
  color: var(--color-emphasis);
  font-weight: 700;
}

.example-chip {
  padding: 6px 14px;
  background: #F8F9FA;
  border: 1.5px solid transparent;
  border-radius: 20px;
  color: var(--color-emphasis);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.example-chip:hover {
  background: white;
  border-color: var(--color-sub-1);
  color: var(--color-main);
  transform: translateY(-1px);
}


/* Loading & Empty */
.loading,
.empty-feed {
  text-align: center;
  padding: 60px 20px;
  color: #636E72;
}

.empty-feed h2 {
  font-size: 24px;
  color: var(--color-emphasis);
  margin: 0 0 10px 0;
  font-weight: 800;
}

.empty-feed p {
  margin: 5px 0 24px 0;
  font-size: 16px;
  color: var(--color-emphasis);
  opacity: 0.7;
}

.btn-load-random {
  padding: 14px 28px;
  background: var(--color-main);
  color: #FFFFFF;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 800;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 4px 15px rgba(242, 159, 5, 0.3);
}

.btn-load-random:hover {
  transform: translateY(-2px);
  background: var(--color-sub-2);
  box-shadow: 0 6px 20px rgba(217, 121, 4, 0.4);
}

/* Recommended Users Section */
.recommended-users-section {
  background: white;
  border: 1px solid var(--color-sub-1);
  border-radius: 24px;
  padding: 24px;
  margin-bottom: 30px;
  box-shadow: 0 10px 30px rgba(89, 53, 39, 0.08);
}

.recommended-users-section h2 {
  font-size: 20px;
  font-weight: 800;
  color: var(--color-emphasis);
  margin: 0 0 5px 0;
}

.section-subtitle {
  font-size: 14px;
  color: var(--color-emphasis);
  opacity: 0.6;
  margin: 0 0 20px 0;
}

.recommended-users {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  padding-bottom: 10px;
}

.user-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  background: #FFF5E6;
  border: 1px solid #FFE4CC;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 120px;
  flex-shrink: 0;
}

.user-card:hover {
  background: white;
  border-color: var(--color-main);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(242, 159, 5, 0.1);
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid var(--color-sub-1);
  box-shadow: 0 4px 15px rgba(89, 53, 39, 0.15);
  margin-bottom: 10px;
}

.user-info {
  text-align: center;
  width: 100%;
}

.user-name {
  font-weight: 700;
  color: var(--color-emphasis);
  font-size: 13px;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100px;
}

/* Feed List */
.feed-list {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.feed-item {
  background: white;
  border: 1px solid var(--color-sub-1);
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(89, 53, 39, 0.08);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 40px;
}

.feed-item:last-child {
  margin-bottom: 0;
}

.feed-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(89, 53, 39, 0.12);
  border-color: var(--color-sub-1);
}

/* Author Section */
.author-section {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  cursor: pointer;
}

.author-section:hover {
  background: #F8F9FA;
}

.author-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  border: 3.5px solid var(--color-sub-1);
  box-shadow: 0 4px 10px rgba(89, 53, 39, 0.1);
}

.author-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-weight: 700;
  color: var(--color-emphasis);
  font-size: 15px;
}

.post-time {
  font-size: 13px;
  color: #ADB5BD;
}

/* Feed Image */
.feed-image {
  position: relative;
  width: 100%;
  padding-top: 100%;
  overflow: hidden;
  background: #f0f0f0;
}

.feed-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* Feed Info */
.feed-info {
  padding: 20px;
}

.feed-title-text {
  font-size: 20px;
  font-weight: 800;
  color: var(--color-emphasis);
  margin: 0 0 12px 0;
  letter-spacing: -0.5px;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(5px);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn 0.3s ease;
}

.modal-content {
  background: #FFFFFF;
  border-radius: 24px;
  padding: 40px;
  width: 90%;
  max-width: 1000px; /* Wide enough for 5 items */
  position: relative;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  animation: slideUp 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.modal-close {
  position: absolute;
  top: 20px;
  right: 20px;
  background: none;
  border: none;
  cursor: pointer;
  color: var(--color-emphasis);
  padding: 8px;
  border-radius: 50%;
  transition: background 0.2s;
}

.modal-close:hover {
  background: #F8F9FA;
}

/* Loading State */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid var(--color-sub-1);
  border-top-color: var(--color-main);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 24px;
}

.loading-state p {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-emphasis);
  margin: 0;
}

/* Result Grid */
.result-container {
  text-align: center;
}

.result-title {
  font-size: 24px;
  font-weight: 800;
  color: var(--color-emphasis);
  margin: 0 0 30px 0;
}

.result-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
  width: 100%;
}

.grid-item {
  display: flex;
  flex-direction: column;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.grid-item:hover {
  transform: translateY(-5px);
}

.image-wrapper {
  position: relative;
  aspect-ratio: 1;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.grid-item:hover .image-wrapper {
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}

.image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.grid-item:hover .image-wrapper img {
  transform: scale(1.1);
}

.grid-item-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.grid-item:hover .grid-item-overlay {
  opacity: 1;
}

.view-text {
  color: white;
  font-weight: 700;
  font-size: 14px;
  padding: 8px 16px;
  background: var(--color-main);
  border-radius: 20px;
  transform: translateY(10px);
  transition: transform 0.3s ease;
}

.grid-item:hover .view-text {
  transform: translateY(0);
}

.item-description {
  font-size: 13px;
  color: var(--color-emphasis);
  line-height: 1.4;
  font-weight: 600;
  background: #FFF5E6;
  padding: 10px;
  border-radius: 12px;
  border: 1px solid var(--color-sub-1);
}

.no-result {
  padding: 40px;
  color: #636E72;
  font-size: 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { 
    opacity: 0;
    transform: translateY(20px);
  }
  to { 
    opacity: 1;
    transform: translateY(0);
  }
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .result-grid {
    grid-template-columns: repeat(2, 1fr); /* Fallback for mobile if needed, though user asked for 1x5 specifically, likely assuming desktop. I will keep it 1x5 but maybe scrollable on small screens if strict 1x5 is required, OR just allow wrap. But specific request '1 x 5' usually implies desktop layout. I'll make it scrollable on mobile if it squishes too much. */
    /* Actually for strict 1x5 on mobile it would be tiny. I'll stick to 1x5 but handle min-width? */
  }
  .result-grid {
      /* Override for mobile to be usable */
      overflow-x: auto;
      display: flex; /* Horizontal scroll */
      padding-bottom: 10px;
  }
  .grid-item {
      min-width: 120px;
      width: 120px;
  }
}

.feed-content-text {
  font-size: 15px;
  color: var(--color-emphasis);
  opacity: 0.8;
  line-height: 1.6;
  margin: 0 0 20px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Stats */
.feed-stats {
  display: flex;
  gap: 20px;
  align-items: center;
}

.stat {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  color: var(--color-emphasis);
  font-weight: 600;
}

.stat-icon {
  width: 20px;
  height: 20px;
  object-fit: contain;
}

/* Main Layout with Right Navigation */
.main-layout-feed {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  gap: 0;
  padding: 2rem 0;
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 1240px; /* 피드(960px) + 네비(280px) */
  margin: 0 auto;
}

.main-content-feed {
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
  width: 280px; /* Fixed width restored */
  
  max-height: calc(100vh - 120px);
  overflow-y: auto;
  -ms-overflow-style: none; /* IE and Edge */
  scrollbar-width: none; /* Firefox */
}

.right-navigation::-webkit-scrollbar {
  display: none;
}

/* Responsive: 1200px 이하일 때 레이아웃 변경 (예: 사이드바 숨기거나 하단 배치) */
@media (max-width: 1200px) {
  .main-layout-feed {
    flex-direction: column;
    align-items: center;
  }

  .right-navigation {
    order: 1; /* 상단으로 올리거나 
                 HomeView처럼 2로 두고 width 100% 할 수도 있음. 
                 여기서는 HomeView와 비슷하게 처리 */
    width: 100%;
    max-width: 600px;
    position: static;
    margin-bottom: 20px;
    max-height: none; /* 높이 제한 해제 */
  }
  
  .main-content-feed {
    width: 100%;
    padding: 0 1rem;
    order: 2;
  }
}

/* 화면 높이가 낮을 때 Sticky 풀기 (공통 로직) */
@media (max-height: 750px) {
  .right-navigation {
    position: static;
  }
}

</style>
