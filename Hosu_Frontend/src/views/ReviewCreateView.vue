<template>
  <div class="review-create-container">
    <!-- Header -->
    <header class="hosu-header">
      <div class="hosu-logo" @click="$router.push('/')">
        <span class="logo-main">HOSU</span>
        <span class="logo-sub">taste log</span>
      </div>
    </header>

    <!-- Main Layout with Remote Control -->
    <div class="main-layout">
      <!-- Main Content -->
      <div class="main-content">
        <div class="review-detail">
          <!-- 작성자 정보 -->
          <div class="author-section">
            <div class="author-info">
              <img
                :src="authorImageUrl"
                :key="userStore.userImg"
                :alt="userStore.nickname"
                class="author-avatar"
              />
              <div class="author-details">
                <span class="author-name">{{ userStore.nickname }}</span>
                <span class="review-date">지금</span>
              </div>
            </div>
          </div>

          <!-- 메인 이미지 미리보기 -->
          <div class="review-image-main">
            <img 
              v-if="representativeImage" 
              :src="representativeImage" 
              alt="메인 이미지"
              @error="handleImageError"
            />
            <div v-else class="image-placeholder">
              <p>메뉴 평가에 이미지를 등록하면 여기에 표시됩니다</p>
            </div>
          </div>

          <!-- 리뷰 정보 -->
          <div class="review-content">
            <!-- 제목 입력 -->
            <div class="title-input-section">
              <input
                type="text"
                v-model="form.title"
                placeholder="리뷰 제목을 입력하세요"
                class="title-input"
                required
              />
              <div v-if="restaurant" class="restaurant-info">
                <span class="restaurant-name">{{ restaurant.name }}</span>
              </div>
            </div>

            <!-- 내용 입력 -->
            <div class="content-input-section">
              <textarea
                v-model="form.content"
                placeholder="이 식당에 대한 솔직한 리뷰를 작성해주세요..."
                rows="10"
                class="content-textarea"
                required
              ></textarea>
            </div>

            <!-- 메뉴 평가 섹션 -->
            <div class="menu-evaluation-section">
              <div class="section-header">
                <h3>메뉴 평가 (최대 3개)</h3>
                <button 
                  v-if="menuEvaluations.length < 3 && restaurantMenus.length > 0" 
                  type="button" 
                  @click="addMenuEvaluation" 
                  class="btn-add-menu"
                >
                  + 메뉴 추가
                </button>
              </div>
              
              <div v-if="restaurantMenus.length === 0" class="no-menu-msg">
                등록된 메뉴가 없습니다.
              </div>

              <div v-for="(evalItem, index) in menuEvaluations" :key="index" class="menu-eval-card">
                <div class="card-header">
                  <span class="eval-num">메뉴 {{ index + 1 }}</span>
                  <button @click="removeMenuEvaluation(index)" class="btn-remove-menu">삭제</button>
                </div>
                
                <div class="eval-row">
                  <label>메뉴 선택</label>
                  <select v-model="evalItem.menuId" class="menu-select">
                    <option value="" disabled>메뉴를 선택하세요</option>
                    <option 
                      v-for="menu in getAvailableMenus(index)" 
                      :key="menu.menuId" 
                      :value="menu.menuId"
                    >
                      {{ menu.menuName }} ({{ menu.price.toLocaleString() }}원)
                    </option>
                  </select>
                </div>

                <div class="eval-row">
                  <label>평점</label>
                  <div class="rating-input">
                    <select v-model="evalItem.rating">
                      <option v-for="n in 5" :key="n" :value="n">{{ n }}점</option>
                    </select>
                    <span class="stars">{{ '⭐'.repeat(evalItem.rating) }}</span>
                  </div>
                </div>

                <div class="eval-row">
                  <label>메뉴 사진 (선택)</label>
                  
                  <!-- 파일 선택 및 업로드 버튼 -->
                  <div class="menu-image-upload-group">
                    <input 
                      type="file" 
                      :ref="el => menuFileInputs[index] = el"
                      @change="handleMenuImageSelect(index, $event)"
                      accept="image/*"
                      class="file-input"
                      :id="`menu-image-${index}`"
                    />
                    <label :for="`menu-image-${index}`" class="file-label">
                      📷 사진 선택
                    </label>
                    
                    <button 
                      v-if="evalItem.imageFile" 
                      type="button"
                      @click="uploadMenuImage(index)"
                      :disabled="evalItem.uploading"
                      class="btn-upload-menu"
                    >
                      {{ evalItem.uploading ? '업로드 중...' : '업로드' }}
                    </button>
                    
                    <button 
                      v-if="evalItem.imageUrl" 
                      type="button"
                      @click="removeMenuImage(index)"
                      class="btn-remove-menu-img"
                    >
                      삭제
                    </button>
                  </div>
                  
                  <!-- 미리보기 -->
                  <div class="menu-image-preview">
                    <img 
                      v-if="evalItem.imagePreview || evalItem.imageUrl" 
                      :src="evalItem.imagePreview || evalItem.imageUrl" 
                      alt="Menu Preview" 
                      @error="handleImageError"
                    />
                    <div v-else class="preview-placeholder">No Image</div>
                  </div>
                  
                  <small v-if="evalItem.imageFile" class="file-hint">
                    선택된 파일: {{ evalItem.imageFile.name }}
                  </small>
                </div>

                <div class="eval-row">
                  <label>한줄평</label>
                  <input 
                    type="text" 
                    v-model="evalItem.content" 
                    placeholder="이 메뉴는 어떠셨나요?" 
                    class="menu-comment-input"
                  />
                </div>
              </div>
            </div>

            <!-- 에러 메시지 -->
            <div v-if="errorMessage" class="error-message">
              {{ errorMessage }}
            </div>

            <!-- 버튼 -->
            <div class="button-group">
              <button type="button" @click="goBack" class="btn-cancel">
                취소
              </button>
              <button type="button" @click="handleSubmit" :disabled="submitting" class="btn-submit">
                {{ submitting ? '작성 중...' : '리뷰 작성' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <SidebarProfile class="remote-control" />
    </div>
  </div>
</template>

<script setup>
import SidebarProfile from '@/components/common/SidebarProfile.vue'
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getRestaurantMenu } from '@/api/menu'
import { getRestaurantDetail } from '@/api/restaurants'
import { createBoard, uploadImage } from '@/api/sns'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const restaurant = ref(null)
const form = ref({
  title: '',
  content: ''
})
const submitting = ref(false)
const errorMessage = ref('')

// 메뉴 관련 상태
const restaurantMenus = ref([])
const menuEvaluations = ref([])
const menuFileInputs = ref([])

// Representative Image (First non-empty image from menu evaluations)
const representativeImage = computed(() => {
  const firstWithImage = menuEvaluations.value.find(item => item.imageUrl && item.imageUrl.trim() !== '')
  return firstWithImage ? firstWithImage.imageUrl : null
})

// 작성자 이미지 URL
const authorImageUrl = computed(() => {
  return userStore.userImg || new URL('@/assets/user.png', import.meta.url).href
})

// 로그아웃 핸들러
const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

// 메뉴 평가 추가
const addMenuEvaluation = () => {
  if (menuEvaluations.value.length >= 3) {
    alert('메뉴 평가는 최대 3개까지만 가능합니다.')
    return
  }
  menuEvaluations.value.push({
    menuId: '',
    rating: 5,
    content: '',
    imageUrl: '',
    imageFile: null,
    imagePreview: '',
    uploading: false
  })
}

// 메뉴 평가 삭제
const removeMenuEvaluation = (index) => {
  menuEvaluations.value.splice(index, 1)
}

// 메뉴 로드
const loadRestaurantMenus = async () => {
  try {
    const response = await getRestaurantMenu(route.params.id)
    restaurantMenus.value = (response.data || []).filter(menu => menu.active)
  } catch (error) {
    console.error('메뉴 로딩 실패:', error)
  }
}

// 식당 정보 로드
const loadRestaurant = async () => {
  try {
    const response = await getRestaurantDetail(route.params.id)
    restaurant.value = response.data
  } catch (error) {
    console.error('식당 정보 로딩 실패:', error)
  }
}

// 선택 가능한 메뉴 목록 (중복 방지)
const getAvailableMenus = (currentIndex) => {
  const selectedMenuIds = menuEvaluations.value
    .map((item, idx) => idx !== currentIndex ? item.menuId : null)
    .filter(id => id)
  
  return restaurantMenus.value.filter(menu => !selectedMenuIds.includes(menu.menuId))
}

// 이미지 로드 에러 처리
const handleImageError = (event) => {
  event.target.src = 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'
}

// 메뉴 이미지 파일 선택
const handleMenuImageSelect = (index, event) => {
  const file = event.target.files[0]
  if (!file) return

  // 파일 크기 검증 (5MB)
  if (file.size > 5 * 1024 * 1024) {
    alert('파일 크기는 5MB를 초과할 수 없습니다.')
    return
  }

  // 이미지 파일 검증
  if (!file.type.startsWith('image/')) {
    alert('이미지 파일만 업로드 가능합니다.')
    return
  }

  menuEvaluations.value[index].imageFile = file

  // 미리보기 생성
  const reader = new FileReader()
  reader.onload = (e) => {
    menuEvaluations.value[index].imagePreview = e.target.result
  }
  reader.readAsDataURL(file)
}

// 메뉴 이미지 업로드
const uploadMenuImage = async (index) => {
  const evalItem = menuEvaluations.value[index]
  if (!evalItem.imageFile) return

  evalItem.uploading = true

  try {
    const response = await uploadImage(evalItem.imageFile)
    
    // S3 URL 저장
    evalItem.imageUrl = response.data
    evalItem.imageFile = null
    evalItem.imagePreview = ''
    
    // 파일 input 초기화
    if (menuFileInputs.value[index]) {
      menuFileInputs.value[index].value = ''
    }
    
    alert('이미지가 업로드되었습니다!')
  } catch (error) {
    console.error('이미지 업로드 실패:', error)
    alert('이미지 업로드 중 오류가 발생했습니다.')
  } finally {
    evalItem.uploading = false
  }
}

// 메뉴 이미지 삭제
const removeMenuImage = (index) => {
  const evalItem = menuEvaluations.value[index]
  
  evalItem.imageUrl = ''
  evalItem.imageFile = null
  evalItem.imagePreview = ''
  
  // 파일 input 초기화
  if (menuFileInputs.value[index]) {
    menuFileInputs.value[index].value = ''
  }
  
  alert('이미지가 삭제되었습니다.')
}

// 뒤로 가기
const goBack = () => {
  router.back()
}

// 리뷰 제출
const handleSubmit = async () => {
  if (!form.value.title.trim() || !form.value.content.trim()) {
    errorMessage.value = '제목과 내용을 모두 입력해주세요.'
    return
  }

  errorMessage.value = ''
  submitting.value = true

  try {
    // 메뉴 평가 데이터 필터링 및 SnsBoardImage 형식으로 변환
    const menus = menuEvaluations.value
      .filter(evalItem => evalItem.menuId)
      .map((item, index) => ({
        menuId: item.menuId,
        menuRating: item.rating,
        oneLineReview: item.content,
        imageUrl: item.imageUrl || null,
        imageOrder: index + 1
      }))

    if (menus.length === 0) {
      errorMessage.value = '최소 1개의 메뉴를 평가해주세요.'
      submitting.value = false
      return
    }

    const data = {
      userNo: userStore.userNo,
      restaurantId: parseInt(route.params.id),
      title: form.value.title,
      content: form.value.content,
      tags: [], // 태그 기능이 없으므로 빈 배열
      menus: menus.length > 0 ? menus : null
    }

    await createBoard(data)
    
    alert('리뷰가 작성되었습니다!')
    router.push(`/restaurant/${route.params.id}`)
  } catch (error) {
    console.error('리뷰 작성 실패:', error)
    errorMessage.value = '리뷰 작성 중 오류가 발생했습니다.'
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    alert('로그인이 필요합니다.')
    router.push('/login')
    return
  }
  loadRestaurant()
  loadRestaurantMenus()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

* {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.review-create-container {
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
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
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
  letter-spacing: -0.5px;
}

.logo-sub {
  font-size: 0.7rem;
  color: var(--color-emphasis);
  font-weight: 600;
  letter-spacing: 2px;
  text-transform: uppercase;
}

/* Main Layout */
.main-layout {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 2rem;
  padding: 2rem;
  max-width: 1400px; /* Reduced from 1800px to match typical layouts better */
  margin: 0 auto;
  width: 100%;
}

.main-content {
  flex: 1;
  width: 100%;
  max-width: 960px; /* Limit content width */
}

.remote-control {
  flex-shrink: 0;
  width: 280px;
  position: sticky;
  top: 100px;
  max-height: calc(100vh - 120px);
  overflow-y: auto;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.remote-control::-webkit-scrollbar {
  display: none;
}

@media (max-width: 1200px) {
  .main-layout {
    flex-direction: column;
    align-items: center;
  }
  
  .remote-control {
    order: -1; /* Place above content on small screens */
    width: 100%;
    position: static;
    margin-bottom: 2rem;
    max-height: none;
  }
  
  .main-content {
    min-width: 0; /* Override previous min-width */
  }
}

/* Review Detail Style */
.review-detail {
  width: 100%;
  background: #FFFFFF;
  border-radius: 24px;
  border: 1px solid var(--color-sub-1);
  box-shadow: 0 10px 30px rgba(89, 53, 39, 0.08);
  overflow: hidden;
}

/* 작성자 섹션 */
.author-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  gap: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.author-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid var(--color-sub-1);
  box-shadow: 0 4px 15px rgba(89, 53, 39, 0.12);
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.author-name {
  font-weight: 700;
  color: var(--color-emphasis);
  font-size: 16px;
}

.review-date {
  font-size: 14px;
  color: #ADB5BD;
}

/* 메인 이미지 */
.review-image-main {
  position: relative;
  width: 100%;
  padding-top: 75%;
  overflow: hidden;
  background: #F8F9FA;
}

.review-image-main img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.03);
  color: #95A5A6;
  font-size: 16px;
}

/* 리뷰 컨텐츠 */
.review-content {
  padding: 40px;
}

/* 제목 입력 */
.title-input-section {
  margin-bottom: 30px;
}

.title-input {
  width: 100%;
  font-size: 28px;
  font-weight: 800;
  border: none;
  border-bottom: 2px solid var(--color-sub-1);
  padding: 12px 0;
  color: var(--color-emphasis);
  background: transparent;
  box-sizing: border-box;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.title-input:focus {
  outline: none;
  border-bottom-color: var(--color-main);
}

.title-input::placeholder {
  color: #ADB5BD;
}

.restaurant-info {
  margin-top: 14px;
  padding: 12px 18px;
  background: var(--color-sub-1);
  border: 1px solid var(--color-sub-1);
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.restaurant-label {
  font-size: 14px;
  color: var(--color-emphasis);
  font-weight: 700;
  opacity: 0.7;
}

.restaurant-name {
  font-size: 16px;
  color: var(--color-emphasis);
  font-weight: 800;
}

/* 내용 입력 */
.content-input-section {
  margin-bottom: 30px;
}

.content-textarea {
  width: 100%;
  font-size: 16px;
  line-height: 1.8;
  border: 2px solid transparent;
  background: #F8F9FA;
  border-radius: 16px;
  padding: 24px;
  color: var(--color-emphasis);
  resize: vertical;
  font-family: inherit;
  box-sizing: border-box;
  min-height: 200px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.content-textarea:focus {
  outline: none;
  border-color: var(--color-main);
  background: #FFFFFF;
  box-shadow: 0 0 0 4px rgba(242, 159, 5, 0.1);
}

.content-textarea::placeholder {
  color: #95A5A6;
}

/* 메뉴 평가 섹션 */
.menu-evaluation-section {
  margin-bottom: 30px;
  padding: 32px;
  background: #F8F9FA;
  border: 1px solid var(--color-sub-1);
  border-radius: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  font-size: 20px;
  color: var(--color-emphasis);
  font-weight: 800;
  letter-spacing: -0.5px;
}

.btn-add-menu {
  background: white;
  border: 1.5px solid var(--color-sub-1);
  color: var(--color-emphasis);
  padding: 8px 18px;
  border-radius: 10px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 700;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(89, 53, 39, 0.05);
}

.btn-add-menu:hover {
  background: var(--color-bg);
  border-color: var(--color-main);
  color: var(--color-main);
  transform: translateY(-2px);
}

.no-menu-msg {
  color: #95A5A6;
  font-size: 14px;
  text-align: center;
  padding: 20px;
}

.menu-eval-card {
  background: #FFFFFF;
  border: 1px solid var(--color-sub-1);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(89, 53, 39, 0.03);
}

.card-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.eval-num {
  font-weight: 800;
  color: var(--color-main);
}

.btn-remove-menu {
  color: #fca5a5;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 13px;
  text-decoration: underline;
  transition: color 0.2s;
}

.btn-remove-menu:hover {
  color: #f44336;
}

.eval-row {
  margin-bottom: 16px;
}

.eval-row label {
  display: block;
  font-size: 13px;
  color: var(--color-emphasis);
  margin-bottom: 10px;
  font-weight: 700;
}

.menu-select, .menu-comment-input {
  width: 100%;
  padding: 12px 14px;
  border: 2px solid transparent;
  background: #F8F9FA;
  color: var(--color-emphasis);
  border-radius: 10px;
  box-sizing: border-box;
  font-size: 14px;
  transition: all 0.3s ease;
}

.menu-select:focus, .menu-comment-input:focus {
  outline: none;
  border-color: var(--color-main);
  background: #FFFFFF;
  box-shadow: 0 0 0 4px rgba(242, 159, 5, 0.1);
}

.menu-select option {
  background: #FFFFFF;
  color: #2D3436;
}

.rating-input {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rating-input select {
  padding: 10px 16px;
  border: 2px solid transparent;
  background: #F8F9FA;
  color: var(--color-emphasis);
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.rating-input select:focus {
  outline: none;
  border-color: var(--color-main);
  background: white;
}

.rating-input select option {
  background: #FFFFFF;
  color: #2D3436;
}

.stars {
  font-size: 20px;
}

/* 파일 업로드 스타일 */
.menu-image-upload-group {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 12px;
}

.file-input {
  display: none;
}

.file-label {
  flex: 1;
  padding: 12px 16px;
  background: #F8F9FA;
  border: 1.5px solid var(--color-sub-1);
  border-radius: 10px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
  color: var(--color-emphasis);
  font-weight: 700;
}

.file-label:hover {
  background: white;
  border-color: var(--color-main);
}

.btn-upload-menu {
  padding: 12px 24px;
  background: var(--color-main);
  color: #FFFFFF;
  border: none;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 800;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  white-space: nowrap;
  box-shadow: 0 4px 15px rgba(242, 159, 5, 0.3);
}

.btn-upload-menu:hover:not(:disabled) {
  transform: translateY(-2px);
  background: var(--color-sub-2);
  box-shadow: 0 6px 20px rgba(217, 121, 4, 0.4);
}

.btn-upload-menu:disabled {
  background: #ccc;
  color: #ffffff;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-remove-menu-img {
  padding: 12px 20px;
  background: #F1F3F5;
  color: #ADB5BD;
  border: 1.5px solid #E0E0E0;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.btn-remove-menu-img:hover {
  background: #FFF5F5;
  color: #f44336;
  border-color: #f44336;
  transform: translateY(-2px);
}

.menu-image-preview {
  width: 100%;
  max-width: 200px;
  height: 150px;
  border: 4px solid var(--color-sub-1);
  border-radius: 16px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F8F9FA;
  margin-top: 14px;
  box-shadow: 0 4px 10px rgba(89, 53, 39, 0.05);
}

.menu-image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-placeholder {
  font-size: 12px;
  color: #95A5A6;
  text-align: center;
}

.file-hint {
  display: block;
  font-size: 12px;
  color: #95A5A6;
  margin-top: 8px;
}

/* 에러 메시지 */
.error-message {
  padding: 16px;
  background: #FFF5F5;
  color: #f44336;
  border: 1px solid rgba(244, 67, 54, 0.2);
  border-radius: 12px;
  font-size: 14px;
  margin-bottom: 24px;
  text-align: center;
}

/* 버튼 그룹 */
.button-group {
  display: flex;
  gap: 12px;
  margin-top: 30px;
}

.button-group button {
  flex: 1;
  padding: 16px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel {
  background: var(--color-sub-1);
  color: var(--color-emphasis);
}

.btn-cancel:hover {
  background: #E8CC9F;
  transform: translateY(-2px);
}

.btn-submit {
  background: var(--color-main);
  color: #FFFFFF;
  font-weight: 800;
  box-shadow: 0 4px 15px rgba(242, 159, 5, 0.3);
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  background: var(--color-sub-2);
  box-shadow: 0 6px 20px rgba(217, 121, 4, 0.4);
}

.btn-submit:disabled {
  background: #ccc;
  color: #ffffff;
  cursor: not-allowed;
  box-shadow: none;
}

/* 반응형 */
@media (max-width: 768px) {
  .review-detail {
    width: 95%;
    margin: 20px auto 40px;
  }

  .review-content {
    padding: 24px;
  }

  .title-input {
    font-size: 22px;
  }

  .menu-evaluation-section {
    padding: 16px;
  }

  .menu-image-upload-group {
    flex-direction: column;
    align-items: stretch;
  }

  .file-label {
    text-align: center;
  }

  .hosu-header {
    padding: 0 1rem;
  }

  .main-layout {
    flex-direction: column;
    padding: 1rem;
  }

  .remote-control {
    display: none;
  }
}

/* Remote Control Section */
.remote-control {
  order: 2;
  flex-shrink: 0;
  margin-top: 16px;
  position: sticky;
  top: 100px;
  align-self: flex-start;
  max-height: calc(100vh - 120px);
}

@media (max-width: 1200px) {
  .remote-control {
    display: none;
  }

  .main-layout {
    padding: 1rem;
  }

  .main-content {
    max-width: 100%;
  }
}
</style>
