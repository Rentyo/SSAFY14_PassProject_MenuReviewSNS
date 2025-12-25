<template>
  <aside class="sidebar-profile">
    <!-- User Profile Card -->
    <div v-if="userStore.isLoggedIn" class="user-profile-card">
      <div class="profile-image-wrapper">
        <img :src="userProfileImage" :key="userStore.userImg" alt="프로필" class="profile-image" />
      </div>
      <p class="user-name">{{ userStore.nickname }}님</p>
      <div class="user-stats">
        <div class="stat-item">
          <span class="stat-value">{{ userStore.followingCount }}</span>
          <span class="stat-label">팔로잉</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value">{{ userStore.followerCount }}</span>
          <span class="stat-label">팔로워</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value">{{ userStore.reviewCount }}</span>
          <span class="stat-label">리뷰</span>
        </div>
      </div>
      
      <!-- 관리자 대시보드 버튼 (관리자일 때만 표시) -->
      <button 
        v-if="userStore.isAdmin" 
        @click="goToAdminDashboard" 
        class="btn-admin-dashboard"
      >
        관리자 대시보드
      </button>
    </div>

    <!-- Login Notice (로그인하지 않았을 때) -->
    <div v-else class="login-notice-card">
      <div class="profile-image-wrapper placeholder">
        <img src="@/assets/user.png" alt="Guest" class="profile-image" />
      </div>
      <p class="user-name">Guest</p>
      <p class="notice-text">로그인하고 더 많은 기능을 이용해보세요!</p>
      <router-link to="/login" class="nav-item login-btn">
        <span class="icon">
          <img src="@/assets/login.png" alt="로그인" class="icon-image" />
        </span>
        <span class="text">로그인</span>
      </router-link>
    </div>

    <nav class="nav-menu">
      <div class="menu-group">
        <router-link to="/" class="nav-item" active-class="active">
          <span class="icon">
            <img src="@/assets/home.png" alt="홈" class="icon-image" />
          </span>
          <span class="text">홈</span>
        </router-link>

        <router-link v-if="userStore.isLoggedIn" to="/feed" class="nav-item" active-class="active">
          <span class="icon">
            <img src="@/assets/list.png" alt="피드" class="icon-image" />
          </span>
          <span class="text">피드</span>
        </router-link>

        <router-link v-if="userStore.isLoggedIn" to="/mypage" class="nav-item" active-class="active">
          <span class="icon">
            <img src="@/assets/mypage.png" alt="마이페이지" class="icon-image" />
          </span>
          <span class="text">마이페이지</span>
        </router-link>
      </div>

      <div v-if="userStore.isLoggedIn" class="menu-bottom">
        <router-link to="/mypage/edit" class="nav-item" active-class="active">
          <span class="icon">
            <img src="@/assets/setting.png" alt="설정" class="icon-image" />
          </span>
          <span class="text">설정</span>
        </router-link>

        <button type="button" @click="handleLogout" class="nav-item logout-item">
          <span class="icon">
            <img src="@/assets/logout.png" alt="로그아웃" class="icon-image" />
          </span>
          <span class="text">로그아웃</span>
        </button>
      </div>
    </nav>
  </aside>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUserProfile } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()

const userProfileImage = computed(() => userStore.userImg || new URL('@/assets/user.png', import.meta.url).href)

const handleLogout = () => {
  userStore.clearUser()
  router.push('/login')
}

const goToAdminDashboard = () => {
  router.push('/admin')
}

// 프로필 데이터 갱신 로직
const refreshProfileData = async () => {
  const userNo = localStorage.getItem('userNo')
  if (userNo && userStore.isLoggedIn) {
    try {
      const response = await getUserProfile(userNo)
      if (response.data) {
        userStore.updateNickname(response.data.nickname)
        userStore.updateUserImg(response.data.userImg)
        userStore.updateUserStats({
          followingCount: response.data.followingCount || 0,
          followerCount: response.data.followerCount || 0,
          reviewCount: response.data.postCount || response.data.reviewCount || 0
        })
      }
    } catch (error) {
      console.error('사이드바 프로필 갱신 실패:', error)
    }
  }
}

onMounted(() => {
  refreshProfileData()
})
</script>

<style scoped>
.sidebar-profile {
  /* Custom Requested Palette */
  --color-bg: #F2F2F2;
  --color-emphasis: #593527;
  --color-main: #F29F05;
  --color-sub-1: #F2DCB3;
  --color-sub-2: #D97904;

  --bg-base: var(--color-bg);
  --bg-inactive: #FFFFFF;
  --bg-active: #FFFFFF;
  --sub-dark: var(--color-emphasis);
  --sub-light: var(--color-sub-1);
  --main-color: var(--color-sub-1);
  --border-color: var(--color-sub-1);

  width: 100%;
  background: var(--bg-active);
  border-radius: 24px;
  border: 1px solid var(--border-color);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  padding: 2rem 1.5rem;
  /* Scroll handling */
  max-height: calc(100vh - 40px);
  overflow-y: auto;
  -ms-overflow-style: none; /* IE and Edge */
  scrollbar-width: none; /* Firefox */
}

.sidebar-profile::-webkit-scrollbar {
  display: none;
}

/* User Profile Card */
.user-profile-card, .login-notice-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2.5rem 1.5rem;
  background: var(--sub-light);
  border-radius: 16px;
  border: 1px solid var(--border-color);
}

.profile-image-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid var(--bg-active);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 1rem;
}

.profile-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-name {
  font-size: 1.1rem;
  color: var(--sub-dark);
  font-weight: 700;
  margin: 0 0 1rem 0;
  text-align: center;
}

.user-stats {
  display: flex;
  align-items: center;
  justify-content: space-around;
  width: 100%;
  gap: 0.5rem;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
  flex: 1;
}

.stat-value {
  font-size: 1.25rem;
  font-weight: 800;
  color: var(--sub-dark);
}

.stat-label {
  font-size: 0.75rem;
  color: #666;
  font-weight: 500;
}

.stat-divider {
  width: 1px;
  height: 30px;
  background: var(--border-color);
}

.btn-admin-dashboard {
  margin-top: 1.5rem;
  padding: 0.75rem 1rem;
  background: var(--color-main);
  color: #FFFFFF;
  border: none;
  border-radius: 12px;
  font-size: 0.9rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 100%;
}

.btn-admin-dashboard:hover {
  transform: translateY(-2px);
  background: var(--color-sub-2);
  box-shadow: 0 6px 16px rgba(217, 121, 4, 0.3);
  filter: brightness(1.1);
}

/* Nav Menu */
.nav-menu {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.menu-group, .menu-bottom {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.875rem 1rem;
  background: var(--bg-inactive);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  color: var(--sub-dark);
  text-decoration: none;
  font-weight: 600;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  cursor: pointer;
}

.nav-item:hover {
  background: var(--bg-active);
  border-color: var(--sub-dark);
  transform: translateX(4px);
}

.nav-item.active {
  background: var(--bg-active);
  border-color: var(--sub-dark);
  color: var(--sub-dark);
  box-shadow: 0 0 0 3px rgba(0, 0, 0, 0.05);
}

.icon {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon-image {
  width: 24px;
  height: 24px;
  object-fit: contain;
  filter: sepia(1) saturate(1) contrast(0.8);
  transition: all 0.3s ease;
}

.nav-item:hover .icon-image,
.nav-item.active .icon-image {
  transform: scale(1.1);
  filter: none;
}

.notice-text {
  font-size: 0.85rem;
  color: #666;
  text-align: center;
  margin-bottom: 1.5rem;
  line-height: 1.4;
}

.login-btn {
  width: 100%;
  justify-content: center;
  background: var(--color-main);
  color: white;
  border: none;
}

.login-btn:hover {
  background: var(--color-sub-2);
  color: white;
}

.logout-item {
  width: 100%;
}

/* Response: Height Adaptation (Shrink Logic) */
@media (max-height: 850px) {
  .sidebar-profile {
    padding: 1.5rem 1rem;
    gap: 1rem;
    border-radius: 16px;
  }
  
  .user-profile-card, .login-notice-card {
    padding: 1.5rem 1rem;
  }

  .profile-image-wrapper {
    width: 60px;
    height: 60px;
    margin-bottom: 0.75rem;
  }

  .user-name {
    margin-bottom: 0.75rem;
    font-size: 1rem;
  }
  
  .stat-value {
    font-size: 1.1rem;
  }
  
  .nav-menu {
    gap: 1rem;
  }
  
  .nav-item {
    padding: 0.75rem 0.875rem;
  }
}

@media (max-height: 700px) {
  .sidebar-profile {
    padding: 1rem 0.75rem;
    gap: 0.75rem;
  }

  .user-profile-card {
    padding: 1rem;
  }
  
  .profile-image-wrapper {
    width: 50px;
    height: 50px;
  }
  
  .nav-item {
     padding: 0.6rem 0.75rem;
     font-size: 0.9rem;
  }
  
  .icon {
    width: 20px;
    height: 20px;
  }
  
  .icon-image {
    width: 20px;
    height: 20px;
  }
}
</style>
