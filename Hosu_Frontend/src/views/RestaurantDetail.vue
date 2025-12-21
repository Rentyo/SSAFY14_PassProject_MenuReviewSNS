<template>
  <div class="restaurant-detail-container">
    <!-- Header -->
    <header class="hosu-header">
      <div class="hosu-logo" @click="$router.push('/')">
        <span class="logo-main">HOSU</span>
        <span class="logo-sub">taste log</span>
      </div>
    </header>


    <!-- Page Wrapper -->
    <div class="page-wrapper">
      <div class="content-box">
        <!-- Loading -->
        <div v-if="loading" class="loading">
          <p>로딩 중...</p>
        </div>

        <!-- Content -->
        <div v-else-if="restaurant" class="restaurant-content">
          <!-- Top Section: Restaurant Profile -->
          <div class="profile-header">
            <div class="profile-image">
               <!-- 식당 이미지가 없으면 placeholder 사용 -->
               <img :src="restaurantImageUrl" :alt="restaurant.name" />
            </div>
            
            <div class="profile-info">
               <div class="name-row">
                 <h2>{{ restaurant.name }}</h2>
                 <!-- 팔로우 버튼 -->
                 <button 
                   v-if="isLoggedIn && restaurant.userNo && restaurant.userNo != myUserNo" 
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
                 
                 <!-- 리뷰작성 버튼 -->
                 <button 
                   v-if="isLoggedIn" 
                   @click="goToReviewCreate" 
                   class="btn-write-review"
                 >
                   <img 
                     :src="editButtonImage" 
                     alt="리뷰작성"
                     class="edit-icon"
                   />
                   <span class="edit-text">리뷰작성</span>
                 </button>
               </div>

               <!-- Stats -->
               <div class="stats-row">
                 <div class="stat-item">
                   <span class="stat-value">{{ restaurant.totalContents || 0 }}</span>
                   <span class="stat-label">게시물</span>
                 </div>
                 <div class="stat-item">
                    <span class="stat-value">{{ (restaurant.totalRating && restaurant.reviewCounts) ? (restaurant.totalRating / restaurant.reviewCounts).toFixed(1) : '0.0' }}</span>
                   <span class="stat-label">평점</span>
                 </div>
                 <div class="stat-item">
                   <span class="stat-value">{{ restaurant.viewCount || 0 }}</span>
                   <span class="stat-label">조회수</span>
                 </div>
               </div>
               
               <div class="category-row">
                  <span>{{ restaurant.category }}</span>
               </div>
            </div>
          </div>

          <!-- Tab Navigation -->
          <div class="tab-navigation">
            <button 
              class="tab-btn" 
              :class="{ active: activeTab === 'reviews' }"
              @click="activeTab = 'reviews'"
            >
              <img class="tab-icon" :src="activeTab === 'reviews' ? '/src/assets/grid.png' : '/src/assets/grid_gray.png'" alt="리뷰">
              <!-- 아이콘이 없을 경우 텍스트로 대체 가능 -->
               <span v-if="!hasIcons">리뷰</span> 
            </button>
            <button 
              class="tab-btn" 
              :class="{ active: activeTab === 'menu' }"
              @click="activeTab = 'menu'"
            >
              <!-- 임시 아이콘 -->
              <img class="tab-icon" :src="activeTab === 'menu' ? '/src/assets/menu.png' : '/src/assets/menu_gray.png'" alt="메뉴">
              <span v-if="!hasIcons">메뉴</span>
            </button>
            <button 
              class="tab-btn" 
              :class="{ active: activeTab === 'info' }"
              @click="activeTab = 'info'"
            >
              <img class="tab-icon" :src="activeTab === 'info' ? '/src/assets/info.png' : '/src/assets/info_gray.png'" alt="정보">
              <span v-if="!hasIcons">정보</span>
            </button>
            <button 
              class="tab-btn" 
              :class="{ active: activeTab === 'notices' }"
              @click="activeTab = 'notices'"
            >
               <img class="tab-icon" :src="activeTab === 'notices' ? '/src/assets/notice.png' : '/src/assets/notice_gray.png'" alt="공지">
               <span v-if="!hasIcons">공지</span>
            </button>
          </div>

          <!-- Tab Content -->
          <div class="tab-content">
            <KeepAlive>
              <component 
                :is="currentTabComponent" 
                :restaurantId="restaurant.id" 
                :restaurant="restaurant"
                :reviews="restaurant.boards"
              />
            </KeepAlive>
          </div>
        </div>
        
        <!-- Error -->
        <div v-else class="error">
          <p>식당 정보를 불러올 수 없습니다.</p>
          <button @click="$router.back()" class="btn-primary">돌아가기</button>
        </div>
      </div>

      <SidebarProfile class="left-navigation" />
    </div>
  </div>
</template>

<script setup>
import SidebarProfile from '@/components/common/SidebarProfile.vue'
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getRestaurantDetail } from '@/api/restaurants'
import { follow, unfollow, getFollowStatus } from '@/api/follow'
import { useUserStore } from '@/stores/user'
import RestaurantReviews from '@/components/Restaurant/RestaurantReviews.vue'
import RestaurantInfo from '@/components/Restaurant/RestaurantInfo.vue'
import RestaurantNotice from '@/components/Restaurant/RestaurantNotice.vue'
import RestaurantMenu from '@/components/Restaurant/RestaurantMenu.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const restaurant = ref(null)
const loading = ref(true)
const activeTab = ref('reviews')
const hasIcons = ref(false)
const isFollowing = ref(false)

// 로그인 상태 확인
const isLoggedIn = computed(() => userStore.isLoggedIn)
const myUserNo = computed(() => userStore.userNo)



// 탭 컴포넌트 매핑
const currentTabComponent = computed(() => {
  switch (activeTab.value) {
    case 'reviews': return RestaurantReviews
    case 'menu': return RestaurantMenu
    case 'info': return RestaurantInfo
    case 'notices': return RestaurantNotice
    default: return RestaurantReviews
  }
})

// 레스토랑 이미지 URL
const restaurantImageUrl = computed(() => {
  return restaurant.value?.mainImage || new URL('@/assets/user.png', import.meta.url).href
})

// 팔로우 버튼 이미지
const followButtonImage = computed(() => {
  return isFollowing.value 
    ? new URL('@/assets/unfollow.png', import.meta.url).href
    : new URL('@/assets/follow.png', import.meta.url).href
})

// 리뷰작성 버튼 이미지
const editButtonImage = computed(() => {
  return new URL('@/assets/edit.png', import.meta.url).href
})



const loadRestaurant = async () => {
  const id = route.params.id
  try {
    loading.value = true
    const response = await getRestaurantDetail(id)
    console.log(response);
    restaurant.value = response.data
    console.log('식당 상세:', restaurant.value)

    // 식당 로드 후 팔로우 상태 확인
    if (isLoggedIn.value && restaurant.value.userNo) {
      checkFollowStatus()
    }
  } catch (error) {
    console.error('식당 정보 로딩 실패:', error)
  } finally {
    loading.value = false
  }
}

// 팔로우 상태 조회
const checkFollowStatus = async () => {
  // 내 식당이면 조회 안 함
  if (restaurant.value.userNo == myUserNo.value) return
  
  try {
    const response = await getFollowStatus(myUserNo.value, restaurant.value.userNo)
    isFollowing.value = response.data.isFollowing
  } catch (error) {
    console.error('팔로우 상태 조회 실패:', error)
  }
}

// 팔로우/언팔로우 토글
const toggleFollow = async () => {
  if (!restaurant.value || !restaurant.value.userNo) return
  
  try {
    if (isFollowing.value) {
      await unfollow(myUserNo.value, restaurant.value.userNo)
      isFollowing.value = false
    } else {
      await follow(myUserNo.value, restaurant.value.userNo)
      isFollowing.value = true
    }
  } catch (error) {
    console.error('팔로우 토글 실패:', error)
    alert('팔로우 처리 중 오류가 발생했습니다.')
  }
}

// 리뷰작성 페이지로 이동
const goToReviewCreate = () => {
  router.push(`/restaurant/${route.params.id}/review/create`)
}

onMounted(() => {
  loadRestaurant()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

* {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.restaurant-detail-container {
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
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

/* Header */
.hosu-header {
  position: relative;
  z-index: 10;
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

.content-box {
  flex: 1 1 auto;
  width: 100%;
  max-width: 1200px;
  background: #FFFFFF;
  border-radius: 24px;
  border: 1px solid var(--color-sub-1);
  box-shadow: 0 10px 30px rgba(89, 53, 39, 0.08);
  min-height: 700px;
  overflow: hidden;
}

.loading {
  text-align: center;
  padding: 100px;
  color: #636E72;
  font-size: 1.1rem;
}

.profile-header {
  display: flex;
  padding: 40px 80px;
  gap: 60px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.profile-image {
  flex-shrink: 0;
  width: 150px;
  height: 150px;
}

.profile-image img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid var(--color-sub-1);
  box-shadow: 0 4px 12px rgba(89, 53, 39, 0.1);
}

.profile-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 20px;
}

.name-row h2 {
  font-size: 28px;
  font-weight: 800;
  margin: 0;
  color: var(--color-emphasis);
}

.follow-btn {
  padding: 8px 12px;
  border: 1.5px solid var(--color-sub-1);
  background: #FFFFFF;
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

.follow-btn:hover {
  background: var(--color-bg);
  border-color: var(--color-main);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(242, 159, 5, 0.15);
}

.follow-icon {
  width: 22px;
  height: 22px;
  object-fit: contain;
  filter: brightness(0.2);
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

.btn-write-review {
  padding: 8px 12px;
  border: 1.5px solid var(--color-sub-1);
  background: #FFFFFF;
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

.btn-write-review:hover {
  background: var(--color-bg);
  border-color: var(--color-main);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(242, 159, 5, 0.15);
}

.edit-icon {
  width: 22px;
  height: 22px;
  object-fit: contain;
  filter: brightness(0.2);
  transition: all 0.3s ease;
}

.edit-text {
  font-size: 10px;
  color: #2D3436;
  font-weight: 500;
  letter-spacing: 0.3px;
  white-space: nowrap;
}

.btn-write-review:hover .edit-icon {
  transform: scale(1.1);
}

.stats-row {
  display: flex;
  gap: 40px;
  font-size: 16px;
}

.stat-item {
  display: flex;
  gap: 5px;
  align-items: baseline;
}

.stat-value {
  font-weight: 800;
  font-size: 18px;
  color: var(--color-main);
}

.stat-label {
  color: #636E72;
  font-weight: 500;
}

.category-row {
  font-weight: 600;
  color: #95A5A6;
  font-size: 14px;
}

/* Tab Navigation */
.tab-navigation {
  display: flex;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.03);
}

.tab-btn {
  flex: 1;
  max-width: 150px;
  height: 52px;
  background: none;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  color: #95A5A6;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  transition: all 0.3s ease;
}

.tab-btn:hover {
  color: var(--color-main);
}

.tab-btn.active {
  border-bottom-color: var(--color-main);
  color: var(--color-main);
}

.tab-icon {
  width: 18px;
  height: 18px;
  display: none;
}

.tab-btn span {
  display: block;
}

.tab-content {
  padding: 40px 80px;
  min-height: 400px;
}

.error {
  text-align: center;
  padding: 100px;
  background: #FFF5E6;
  border-radius: 12px;
  margin: 40px;
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
  font-weight: 800;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(242, 159, 5, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  background: var(--color-sub-2);
  box-shadow: 0 6px 20px rgba(217, 121, 4, 0.4);
}

/* 반응형 */
@media (max-width: 768px) {
  .content-box {
    width: 95%;
    margin: 20px auto;
  }

  .profile-header {
    flex-direction: column;
    padding: 30px 20px;
    gap: 20px;
    align-items: center;
    text-align: center;
  }
  
  .profile-image {
    width: 100px;
    height: 100px;
  }
  
  .profile-info {
    align-items: center;
  }

  .name-row {
    flex-direction: column;
    gap: 15px;
  }
  
  .stats-row {
    width: 100%;
    justify-content: space-around;
    gap: 20px;
  }
  
  .stat-item {
    flex-direction: column;
    align-items: center;
    gap: 5px;
  }

  .tab-navigation {
    flex-wrap: wrap;
  }

  .tab-btn {
    max-width: none;
    flex: 1 1 33%;
  }

  .tab-content {
    padding: 20px 15px;
  }

  .hosu-header {
    padding: 0 1rem;
  }
}

/* Page Wrapper with Sidebar */
.page-wrapper {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 3rem;
  padding: 2rem;
  width: 100%;
  max-width: 1600px;
  margin: 0 auto;
}

/* content-box styles consolidated above */

/* Right Navigation */
.left-navigation {
  order: 2;
  flex-shrink: 0;
  margin-top: 16px;
  position: sticky;
  top: 100px;
  align-self: flex-start;
  max-height: calc(100vh - 120px);
}








</style>
