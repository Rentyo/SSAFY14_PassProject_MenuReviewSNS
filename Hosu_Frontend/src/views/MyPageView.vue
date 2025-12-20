<template>
  <div class="mypage-container">
    <!-- Header -->
    <header class="hosu-header">
      <div class="hosu-logo" @click="$router.push('/')">
        <span class="logo-main">HOSU</span>
        <span class="logo-sub">taste log</span>
      </div>
    </header>

    <!-- Main Layout with Right Navigation -->
    <div class="main-layout-mypage">
      <!-- Main Content -->
      <div class="main-content-mypage">
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
                      <span class="stat-label">리뷰수</span>
                    </div>
                  </div>
                </div>

                <!-- 관리자 대시보드 버튼 -->
                <button 
                  v-if="isMyProfile && userStore.isAdmin"
                  @click="$router.push('/admin')"
                  class="btn-admin-dashboard"
                >
                  관리자 대시보드
                </button>
              </div>
            </div>
          </div>

          <!-- 에러 메시지 -->
          <div v-else class="error">
            <p>사용자 정보를 불러올 수 없습니다.</p>
            <button @click="$router.push('/login')" class="btn-primary">로그인하기</button>
          </div>
        </div>

        <!-- 내 리뷰 섹션 -->
        <div v-if="userProfile" class="reviews-container">
          <div class="reviews-header">
            <h3>My Reviews</h3>
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

      <SidebarProfile class="right-navigation" />
    </div>

    <!-- 정보 수정 모달 -->
    <div v-if="showEditModal" class="modal-overlay" @click="showEditModal = false">
      <div class="modal-content" @click.stop>
        <h3>정보 수정</h3>
        <form @submit.prevent="handleUpdate">
          <div class="form-group">
            <label>닉네임</label>
            <input v-model="editForm.nickname" type="text" :placeholder="userProfile.nickname" />
          </div>

          <div class="form-group">
            <label>전화번호</label>
            <input v-model="editForm.phoneNumber" type="tel" placeholder="전화번호" />
          </div>

          <div class="form-group">
            <label>새 비밀번호 (변경 시에만 입력)</label>
            <input
              v-model="editForm.userPw"
              type="password"
              placeholder="비밀번호 변경하지 않으려면 비워두세요"
            />
          </div>

          <div class="form-group">
            <label>프로필 이미지 URL</label>
            <input v-model="editForm.userImg" type="url" :placeholder="userProfile.userImg" />
          </div>

          <div v-if="updateError" class="error-message">
            {{ updateError }}
          </div>

          <div class="modal-buttons">
            <button type="submit" class="btn-primary" :disabled="updating">
              {{ updating ? '수정 중...' : '수정하기' }}
            </button>
            <button type="button" @click="showEditModal = false" class="btn-secondary">취소</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import SidebarProfile from '@/components/common/SidebarProfile.vue'
import { ref, computed, watch } from 'vue'
import { useMyPage } from '@/composables/useMyPage'
import { useUserStore } from '@/stores/user'
import { follow, unfollow, getFollowStatus } from '@/api/follow'

const {
  userProfile,
  loading,
  showEditModal,
  updating,
  updateError,
  editForm,
  reviews,
  formatDate,
  handleUpdate,
  handleLogout,
  confirmDelete,
  goToReview,
} = useMyPage()

const userStore = useUserStore()

// 팔로우 상태
const isFollowing = ref(false)
const myUserNo = localStorage.getItem('userNo')

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


// 팔로우 상태 조회
const checkFollowStatus = async () => {
  if (!userProfile.value || isMyProfile.value) return
  
  try {
    const response = await getFollowStatus(myUserNo, userProfile.value.userNo)
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
      await unfollow(myUserNo, userProfile.value.userNo)
      isFollowing.value = false
      // 팔로워 수 감소
      if (userProfile.value.followerCount > 0) {
        userProfile.value.followerCount--
      }
    } else {
      await follow(myUserNo, userProfile.value.userNo)
      isFollowing.value = true
      // 팔로워 수 증가
      userProfile.value.followerCount++
    }
  } catch (error) {
    console.error('팔로우 토글 실패:', error)
    alert('팔로우 처리 중 오류가 발생했습니다.')
  }
}

// userProfile이 로드되면 팔로우 상태 확인
watch(userProfile, (newVal) => {
  if (newVal) {
    checkFollowStatus()
  }
})
</script>

<style scoped>
.mypage-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  background: linear-gradient(
    135deg,
    #FFF8F0 0%,
    #FFF8F0 25%,
    #FFF8F0 50%,
    #FFF8F0 75%,
    #FFF8F0 100%
  );
  
  
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.hosu-header {
  height: 80px;
  min-height: 80px;
  flex-shrink: 0;
  padding: 0 2rem;
  background: #FFFFFF;
  border-bottom: 1px solid #FFE4CC;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 12px rgba(255, 107, 107, 0.08);
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
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.5px;
}

.logo-sub {
  font-size: 0.7rem;
  color: #95A5A6;
  font-weight: 500;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.mypage-box {
  width: 60%;
  max-width: 1400px;
  margin: 40px auto;
  background: rgba(255, 255, 255, 0.1);  border: 1px solid #FFE4CC;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.15);
  padding: 40px 80px;
  flex-shrink: 0;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #636E72;
}

/* 프로필 헤더 */
.profile-header {
  display: flex;
  align-items: center;
  gap: 30px;
  padding-bottom: 30px;
  border-bottom: 2px solid #f0f0f0;
  margin-bottom: 30px;
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
  border: 3px solid rgba(255, 107, 107, 0.15);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.15);
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
  border: 2px solid rgba(255, 255, 255, 0.8);
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
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 1);
  transform: translateY(-2px);
}

.follow-icon {
  width: 22px;
  height: 22px;
  object-fit: contain;
  filter: brightness(0) invert(1);
  transition: all 0.3s ease;
}

.follow-text {
  font-size: 10px;
  color: #2D3436;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.follow-btn:hover .follow-icon {
  transform: scale(1.1);
}

.profile-info h2 {
  margin: 0;
  font-size: 28px;
  color: #2D3436;
}

.role-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.role-badge.user {
  background-color: #e3f2fd;
  color: #1976d2;
}

/* 통계 섹션 */
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
  font-weight: bold;
  color: #FF6B6B;
}

.stat-label {
  font-size: 14px;
  color: #636E72;
  font-weight: 500;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background-color: #e0e0e0;
}

/* 정보 섹션 */
.info-section {
  margin-bottom: 30px;
}

.info-section h3 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
}

.info-item {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item .label {
  width: 120px;
  font-weight: 500;
  color: #666;
}

.info-item .value {
  flex: 1;
  color: #333;
}

/* 버튼 그룹 */
.button-group {
  display: flex;
  gap: 10px;
  margin-top: 30px;
}

.button-group button {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-edit {
  background-color: #4caf50;
  color: #2D3436;
}

.btn-edit:hover {
  background-color: #45a049;
}

.btn-logout {
  background-color: #2196f3;
  color: #2D3436;
}

.btn-logout:hover {
  background-color: #0b7dda;
}

.btn-delete {
  background-color: #f44336;
  color: #2D3436;
}

.btn-delete:hover {
  background-color: #da190b;
}

/* 모달 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: rgba(255, 255, 255, 0.1);  border: 1px solid #FFE4CC;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.15);
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content h3 {
  margin: 0 0 20px 0;
  font-size: 24px;
  color: #2D3436;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #2D3436;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1.5px solid rgba(255, 255, 255, 0.2);
  background: #FFF5E6;
  color: #2D3436;
  border-radius: 8px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group input::placeholder {
  color: #95A5A6;
}

.form-group input:focus {
  outline: none;
  border-color: #FF6B6B;
  background: rgba(255, 255, 255, 0.1);
}

.error-message {
  padding: 10px;
  margin-bottom: 15px;
  background-color: #ffebee;
  color: #c62828;
  border-radius: 4px;
  font-size: 14px;
}

.modal-buttons {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.modal-buttons button {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
}

.btn-primary {
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  color: #2D3436;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
}

.btn-primary:disabled {
  background: rgba(255, 255, 255, 0.1);
  color: #95A5A6;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: rgba(255, 255, 255, 0.1);
  border: 1px solid #FFE4CC;
  color: #2D3436;
}

.btn-secondary:hover {
  background-color: rgba(255, 255, 255, 0.15);
}

.error {
  text-align: center;
  padding: 40px;
}

.error p {
  margin-bottom: 20px;
  color: #636E72;
}

.btn-admin-dashboard {
  margin-top: 12px;
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  color: #FFFFFF;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
}

.btn-admin-dashboard:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}

.reviews-container {
  width: 60%;
  max-width: 1400px;
  margin: 40px auto;
  padding: 0 40px;
}

.reviews-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e0e0e0;
}

.reviews-header h3 {
  font-size: 24px;
  color: #2D3436;
  margin: 0;
}

.review-count {
  font-size: 16px;
  color: #FF6B6B;
  font-weight: bold;
}

/* 리뷰가 없을 때 */
.no-reviews {
  text-align: center;
  padding: 80px 20px;
  background: #FFF5E6;
  backdrop-filter: blur(10px);
  border: 1px solid #FFE4CC;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.no-reviews p {
  font-size: 18px;
  color: #95A5A6;
  margin-bottom: 20px;
}

.btn-write-review {
  padding: 12px 30px;
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  color: #2D3436;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-write-review:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
}


.reviews-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

/* 리뷰 카드 */
.review-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid rgba(139, 69, 19, 0.5);
}

.review-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

/* 리뷰 이미지 */
.review-image {
  position: relative;
  width: 100%;
  padding-top: 100%; /* 정사각형 비율 */
  overflow: hidden;
  background: #f5f5f5;
}

.review-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.review-card:hover .review-image img {
  transform: scale(1.05);
}

/* 좋아요 배지 */
.like-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.7);
  color: #2D3436;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
  backdrop-filter: blur(5px);
}

/* 리뷰 정보 */
.review-info {
  padding: 15px;
}

.review-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.review-date {
  font-size: 14px;
  color: #999;
  margin: 0;
}

/* Main Layout with Right Navigation */
.main-layout-mypage {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  gap: 0;
  padding: 2rem 0;
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 1680px; /* 메인(1400px) + 네비(280px) */
  margin: 0 auto;
}

.main-content-mypage {
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

/* Scrollbar styling */


/* ===== 반응형 ===== */

/* 태블릿 (3열) */
@media (max-width: 1024px) {
  .reviews-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

/* 작은 태블릿 (2열) */
@media (max-width: 768px) {
  .reviews-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }

  .reviews-header h3 {
    font-size: 20px;
  }
}

/* 모바일 (1열) */
@media (max-width: 480px) {
  .reviews-grid {
    grid-template-columns: 1fr;
  }

  .reviews-container {
    padding: 0 15px;
  }
}
</style>
