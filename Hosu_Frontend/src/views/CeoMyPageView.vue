<template>
  <div class="ceo-mypage-container">
    <!-- Header -->
    <header class="hosu-header">
      <div class="hosu-logo" @click="$router.push('/')">
        <span class="logo-main">HOSU</span>
        <span class="logo-sub">taste log</span>
      </div>
    </header>

    <div class="main-layout-ceo">
      <div class="main-content-ceo">
        <div class="ceo-mypage-box">
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
                  :src="userProfile.userImg || defaultProfileImage"
                  :alt="userProfile.nickname"
                />
              </div>
              <div class="profile-info">
                <h2>{{ userProfile.nickname }}</h2>
                <span class="role-badge ceo">CEO PROFILE</span>
                <p class="business-desc">기업회원 전용 페이지</p>
                
                <!-- 본인 페이지일 때만 식당 등록 버튼 표시 -->
                <button 
                  v-if="isOwnProfile"
                  @click="goToRestaurantRegister"
                  :class="[
                    'btn-register',
                    registrationStatus === 1
                      ? 'status-complete'
                      : registrationStatus === -1
                      ? 'status-pending'
                      : 'status-default'
                  ]"
                >
                  {{ registrationButtonText }}
                </button>
              </div>
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

            <!-- 상세 정보 -->
            <div class="info-section">
              <h3>업체 정보</h3>
              <div class="info-item">
                <span class="label">업체명:</span>
                <span class="value">{{ userProfile.nickname }}</span>
              </div>
              <div class="info-item">
                <span class="label">성별:</span>
                <span class="value">{{ userProfile.gender || '미설정' }}</span>
              </div>
              <div class="info-item">
                <span class="label">가입일:</span>
                <span class="value">{{ formatDate(userProfile.createdAt) }}</span>
              </div>
            </div>
          </div>

          <!-- 에러 메시지 -->
          <div v-else class="error">
            <p>사용자 정보를 불러올 수 없습니다.</p>
            <button @click="$router.push('/login')" class="btn-primary">로그인하기</button>
          </div>
        </div>

        <!-- 탭 섹션 -->
        <div v-if="userProfile" class="tabs-container">
          <!-- 탭 네비게이션 -->
          <div class="tab-navigation">
            <button
              :class="['tab-button', { active: activeTab === 'reviews' }]"
              @click="activeTab = 'reviews'"
            >
              우리식당리뷰
            </button>
            <button
              :class="['tab-button', { active: activeTab === 'notices' }]"
              @click="activeTab = 'notices'"
            >
              공지사항
            </button>
            <button
              :class="['tab-button', { active: activeTab === 'menu' }]"
              @click="activeTab = 'menu'"
            >
              메뉴
            </button>
          </div>

          <!-- 탭 컨텐츠 -->
          <div class="tab-content">
            <CeoRestaurantReviews
              v-if="activeTab === 'reviews'"
              :reviews="reviews"
              :formatDate="formatDate"
              :goToReview="goToReview"
            />
            <CeoNotices v-if="activeTab === 'notices'" />
            <CeoMenu v-if="activeTab === 'menu'" />
          </div>
        </div>
      </div>

      <SidebarProfile class="right-profile-section" />
    </div>

    <!-- 정보 수정 모달 -->
    <div v-if="showEditModal" class="modal-overlay" @click="showEditModal = false">
      <div class="modal-content" @click.stop>
        <h3>정보 수정</h3>
        <form @submit.prevent="handleUpdate">
          <div class="form-group">
            <label>업체명</label>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMyPage } from '@/composables/useMyPage'
import CeoRestaurantReviews from '@/components/CeoRestaurantReviews.vue'
import CeoNotices from '@/components/CeoNotices.vue'
import CeoMenu from '@/components/CeoMenu.vue'
import { getCeoLevel } from '@/api/restaurants'
import defaultUserImg from '@/assets/user.png'

const defaultProfileImage = defaultUserImg;

const router = useRouter()

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

// 탭 상태 관리
const activeTab = ref('reviews')

// 식당 등록 상태: 1 = 완료, 0 = 신청, -1 = 등록 중
const registrationStatus = ref(null)

// 본인 페이지인지 확인
const isOwnProfile = computed(() => {
  const currentUserNo = localStorage.getItem('userNo')
  return currentUserNo && userProfile.value && currentUserNo === String(userProfile.value.userNo)
})

// 식당 등록 상태 확인
const checkRegistrationStatus = () => {
  getCeoLevel(localStorage.getItem('userNo')).then(response => {
    console.log(response.data);
    /**
     * response 값에 따라 상태 설정
     * 1  → 등록 완료
     * 0  → 아무것도 아닌상태
     * -1 → 등록 중
     */
    registrationStatus.value = response.data
  })
}

// 버튼 텍스트 바꾸기
const registrationButtonText = computed(() => {
  switch (registrationStatus.value) {
    case 1:
      return '등록 완료'
    case 0:
      return '식당 등록 신청'
    case -1:
      return '등록 중'
    default:
      return '식당 등록 신청'
  }
})


// 식당 등록 페이지로 이동
const goToRestaurantRegister = () => {
  if (registrationStatus.value === 1) {
    // 이미 등록된 경우 알림만 표시
    alert('이미 식당 등록이 완료되었습니다.')
  }
  else if (registrationStatus.value === -1) {
    // 이미 등록된 경우 알림만 표시
    alert('식당 등록 중입니다.')
  }  
  else {
    router.push('/restaurant/register')
  }
}

// 컴포넌트 마운트 시 등록 상태 확인
onMounted(() => {
  checkRegistrationStatus()
})
</script>

<style scoped>
.ceo-mypage-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  background: #FFF8F0;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
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

/* Main Layout */
.main-layout-ceo {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 2rem;
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.main-content-ceo {
  flex: 1;
  max-width: 900px;
}

.ceo-mypage-box {
  width: 100%;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.15);
  border: 1px solid #FFE4CC;
  padding: 40px;
  margin-bottom: 2rem;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #666;
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
}

.profile-image img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid rgba(255, 107, 107, 0.3);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.15);
}

.profile-info h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
  color: #333;
}

.business-desc {
  color: #666;
  font-size: 14px;
  margin-top: 8px;
}

.role-badge {
  display: inline-block;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
}

.role-badge.ceo {
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  color: #FFFFFF;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
}

.btn-register {
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

.status-default {
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
}

.status-default:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}

.status-complete {
  background: linear-gradient(135deg, #9e9e9e 0%, #757575 100%);
  cursor: default;
  box-shadow: 0 2px 8px rgba(158, 158, 158, 0.3);
}

.status-complete:hover {
  transform: none;
  box-shadow: 0 2px 8px rgba(158, 158, 158, 0.3);
}

.status-pending {
  background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
  cursor: default;
  box-shadow: 0 2px 8px rgba(255, 152, 0, 0.3);
}

.status-pending:hover {
  transform: none;
  box-shadow: 0 2px 8px rgba(255, 152, 0, 0.3);
}

.btn-register:disabled {
  opacity: 0.8;
  cursor: not-allowed;
}

/* 통계 섹션 */
.stats-section {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 30px 0;
  border-bottom: 2px solid #f0f0f0;
  margin-bottom: 30px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #FF6B6B;
}

.stat-label {
  font-size: 14px;
  color: #666;
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
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-edit {
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  color: #FFFFFF;
}

.btn-edit:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}

.btn-logout {
  background-color: #2196f3;
  color: #2D3436;
}

.btn-logout:hover {
  background-color: #0b7dda;
  transform: translateY(-2px);
}

.btn-delete {
  background-color: #f44336;
  color: #2D3436;
}

.btn-delete:hover {
  background-color: #da190b;
  transform: translateY(-2px);
}

/* 모달 - 기존 MyPageView와 동일 */
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
  background: white;
  padding: 30px;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content h3 {
  margin: 0 0 20px 0;
  font-size: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #FF6B6B;
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
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
  color: #FFFFFF;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}

.btn-primary:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #f5f5f5;
  color: #333;
}

.btn-secondary:hover {
  background-color: #e0e0e0;
}

.error {
  text-align: center;
  padding: 40px;
}

.error p {
  margin-bottom: 20px;
  color: #666;
}

/* 탭 섹션 */
.tabs-container {
  width: 100%;
}

/* 탭 네비게이션 */
.tab-navigation {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  background: rgba(255, 255, 255, 0.1);
  padding: 8px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.tab-button {
  flex: 1;
  padding: 14px 24px;
  background: transparent;
  color: #636E72;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.tab-button:hover {
  color: #2D3436;
  background: rgba(255, 255, 255, 0.1);
}

.tab-button.active {
  background: white;
  color: #FF6B6B;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 탭 컨텐츠 */
.tab-content {
  min-height: 400px;
}

/* Right Profile Section */
.right-profile-section {
  order: 2;
  flex-shrink: 0;
  margin-top: 16px;
  position: sticky;
  top: 100px;
  align-self: flex-start;
  max-height: calc(100vh - 120px);
}
</style>
