<template>
  <div class="review-edit-container">
    <!-- Header -->
    <header class="hosu-header">
      <div class="hosu-logo" @click="$router.push('/')">
        <span class="logo-main">HOSU</span>
        <span class="logo-sub">taste log</span>
      </div>
    </header>

    <!-- Loading -->
    <div v-if="loading" class="loading">
      <p>로딩 중...</p>
    </div>

    <div v-else class="review-detail">
      <!-- 작성자 정보 -->
      <div class="author-section">
        <div class="author-info">
          <img
            :src="userStore.userImg || 'https://via.placeholder.com/50'"
            :alt="userStore.nickname"
            class="author-avatar"
          />
          <div class="author-details">
            <span class="author-name">{{ userStore.nickname }}</span>
            <span class="review-date">수정 중</span>
          </div>
        </div>
      </div>

      <!-- 메인 이미지 (Carousel) -->
      <div class="review-image-main">
        <img 
          :src="currentImageUrl" 
          alt="메인 이미지"
          class="main-img"
          @error="handleImageError"
        />
        
        <!-- 캐러셀 컨트롤 -->
        <div v-if="form.menuItems.length > 1" class="carousel-controls">
          <button @click.stop="prevImage" class="nav-btn prev" aria-label="이전">&lt;</button>
          <button @click.stop="nextImage" class="nav-btn next" aria-label="다음">&gt;</button>
          
          <div class="carousel-indicators">
            <span 
              v-for="(item, idx) in form.menuItems" 
              :key="idx"
              class="indicator-dot"
              :class="{ active: idx === currentImageIndex }"
              @click.stop="currentImageIndex = idx"
            ></span>
          </div>
        </div>
      </div>

      <!-- 리뷰 정보 -->
      <div class="review-content">
        <!-- 제목 입력 -->
        <div class="title-input-section">
          <input
            type="text"
            v-model="form.title"
            placeholder="리뷰 제목"
            class="title-input"
            required
          />
        </div>

        <!-- 내용 입력 -->
        <div class="content-input-section">
          <textarea
            v-model="form.content"
            placeholder="이 식당에 대한 솔직한 리뷰를 작성해주세요..."
            rows="6"
            class="content-textarea"
            required
          ></textarea>
        </div>

        <!-- 메뉴별 상세 편집 리스트 -->
        <div v-if="form.menuItems.length > 0" class="menu-edit-list">
          <h3 class="section-title">메뉴별 리뷰 편집</h3>
          
          <div class="menu-list-items">
            <div 
              v-for="(item, index) in form.menuItems"
              :key="index"
              class="menu-edit-item"
              :class="{ 'active': index === currentImageIndex }"
              @click="currentImageIndex = index"
            >
              <div class="menu-header">
                <span class="menu-id">메뉴 #{{ item.menuId }}</span>
                <div class="menu-rating-display">
                  <span class="star-icon">⭐</span>
                  <span class="rating-val">{{ item.menuRating || 0 }}</span>
                </div>
              </div>
              
              <div class="edit-fields">
                <div class="field-group">
                  <label>메뉴 사진</label>
                  
                  <!-- 파일 선택 및 업로드 -->
                  <div class="menu-image-upload-group">
                    <input 
                      type="file" 
                      :ref="el => menuFileInputs[index] = el"
                      @change="handleMenuImageSelect(index, $event)"
                      accept="image/*"
                      class="file-input"
                      :id="`menu-image-edit-${index}`"
                    />
                    <label :for="`menu-image-edit-${index}`" class="file-label">
                      📷 사진 선택
                    </label>
                    
                    <button 
                      v-if="item.imageFile" 
                      type="button"
                      @click="uploadMenuImage(index)"
                      :disabled="item.uploading"
                      class="btn-upload-menu"
                    >
                      {{ item.uploading ? '업로드 중...' : '업로드' }}
                    </button>
                    
                    <button 
                      v-if="item.imageUrl" 
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
                      v-if="item.imagePreview || item.imageUrl" 
                      :src="item.imagePreview || item.imageUrl" 
                      alt="Menu Preview" 
                    />
                    <div v-else class="preview-placeholder">No Image</div>
                  </div>
                  
                  <small v-if="item.imageFile" class="file-hint">
                    선택된 파일: {{ item.imageFile.name }}
                  </small>
                </div>
                
                <div class="field-group">
                  <label>메뉴 평점 (0-5)</label>
                  <input 
                    type="number" 
                    v-model="item.menuRating" 
                    step="0.5" 
                    min="0" 
                    max="5" 
                    class="input-field" 
                  />
                </div>
                
                <div class="field-group">
                  <label>한줄평</label>
                  <input 
                    type="text" 
                    v-model="item.oneLineReview" 
                    placeholder="이 메뉴 어땠나요?" 
                    class="input-field" 
                  />
                </div>
              </div>
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
            {{ submitting ? '수정 중...' : '수정 완료' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getBoardDetail, updateBoard, uploadImage } from '@/api/sns'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const form = ref({
  title: '',
  content: '',
  restaurantId: null,
  menuItems: [] // { imageId, menuId, imageUrl, menuRating, oneLineReview, imageFile, imagePreview, uploading }
})
const currentImageIndex = ref(0)
const submitting = ref(false)
const errorMessage = ref('')
const menuFileInputs = ref([])

// 현재 표시할 이미지 URL
const currentImageUrl = computed(() => {
  if (form.value.menuItems && form.value.menuItems.length > 0) {
    return form.value.menuItems[currentImageIndex.value]?.imageUrl || 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="600" height="600"%3E%3Crect width="600" height="600" fill="%23f0f0f0"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="24"%3ENo Image%3C/text%3E%3C/svg%3E'
  }
  return 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="600" height="600"%3E%3Crect width="600" height="600" fill="%23f0f0f0"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="24"%3ENo Image%3C/text%3E%3C/svg%3E'
})

// 이전 이미지
const prevImage = () => {
  if (!form.value.menuItems || form.value.menuItems.length <= 1) return
  if (currentImageIndex.value > 0) {
    currentImageIndex.value--
  } else {
    currentImageIndex.value = form.value.menuItems.length - 1
  }
}

// 다음 이미지
const nextImage = () => {
  if (!form.value.menuItems || form.value.menuItems.length <= 1) return
  if (currentImageIndex.value < form.value.menuItems.length - 1) {
    currentImageIndex.value++
  } else {
    currentImageIndex.value = 0
  }
}

// 리뷰 데이터 로드
const loadReview = async () => {
  try {
    loading.value = true
    const response = await getBoardDetail(route.params.id, userStore.userNo)
    const review = response.data
    
    // 권한 체크
    if (Number(review.userNo) !== Number(userStore.userNo)) {
      alert('본인이 작성한 리뷰만 수정할 수 있습니다.')
      router.back()
      return
    }
    
    // 폼 데이터 설정
    form.value.title = review.title || ''
    form.value.content = review.content || ''
    form.value.restaurantId = review.restaurantId // 식당 ID 저장
    
    // 이미지/메뉴 매핑
    if (review.images && review.images.length > 0) {
      form.value.menuItems = review.images.map(img => ({
        imageId: img.imageId,
        menuId: img.menuId,
        imageUrl: img.imageUrl,
        menuRating: img.menuRating || 0,
        oneLineReview: img.oneLineReview || ''
      }))
    } else {
      // 이미지가 없는 경우 빈 배열
      form.value.menuItems = []
    }
  } catch (error) {
    console.error('리뷰 로딩 실패:', error)
    alert('리뷰를 불러오는 중 오류가 발생했습니다.')
    router.back()
  } finally {
    loading.value = false
  }
}

// 이미지 로드 에러 처리
const handleImageError = (event) => {
  // 무한 루프 방지: 에러 핸들러 제거
  event.target.onerror = null
  // 투명 이미지로 대체
  event.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="600" height="600"%3E%3Crect width="600" height="600" fill="%23f0f0f0"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="24"%3EImage Not Found%3C/text%3E%3C/svg%3E'
}

// 메뉴 이미지 파일 선택
const handleMenuImageSelect = (index, event) => {
  const file = event.target.files[0]
  if (!file) return

  // 파일 크기 체크 (10MB)
  if (file.size > 10 * 1024 * 1024) {
    alert('파일 크기는 10MB 이하여야 합니다.')
    return
  }

  // 이미지 파일 타입 체크
  if (!file.type.startsWith('image/')) {
    alert('이미지 파일만 업로드 가능합니다.')
    return
  }

  form.value.menuItems[index].imageFile = file

  // 미리보기 생성
  const reader = new FileReader()
  reader.onload = (e) => {
    form.value.menuItems[index].imagePreview = e.target.result
  }
  reader.readAsDataURL(file)
}

// 메뉴 이미지 업로드
const uploadMenuImage = async (index) => {
  const item = form.value.menuItems[index]
  if (!item.imageFile) return

  try {
    item.uploading = true
    
    // uploadImage는 file 객체를 직접 받음
    const response = await uploadImage(item.imageFile)
    item.imageUrl = response.data
    item.imageFile = null
    item.imagePreview = null
    
    alert('이미지가 업로드되었습니다!')
  } catch (error) {
    console.error('이미지 업로드 실패:', error)
    alert('이미지 업로드 중 오류가 발생했습니다.')
  } finally {
    item.uploading = false
  }
}

// 메뉴 이미지 삭제
const removeMenuImage = (index) => {
  form.value.menuItems[index].imageUrl = ''
  form.value.menuItems[index].imageFile = null
  form.value.menuItems[index].imagePreview = null
  
  // 파일 input 초기화
  if (menuFileInputs.value[index]) {
    menuFileInputs.value[index].value = ''
  }
}

// 뒤로 가기
const goBack = () => {
  router.back()
}

// 리뷰 수정 제출
const handleSubmit = async () => {
  if (!form.value.title.trim() || !form.value.content.trim()) {
    errorMessage.value = '제목과 내용을 모두 입력해주세요.'
    return
  }

  errorMessage.value = ''
  submitting.value = true

  try {
    // SnsBoardUpdateReq 형식에 맞춰 데이터 구성
    const data = {
      title: form.value.title,
      content: form.value.content,
      restaurantId: form.value.restaurantId, // 식당 ID
      imageUrls: form.value.menuItems.map(item => ({
        imageId: item.imageId,
        boardId: route.params.id, // 게시글 ID
        menuId: item.menuId,
        imageUrl: item.imageUrl,
        menuRating: Number(item.menuRating),
        oneLineReview: item.oneLineReview
      }))
    }

    await updateBoard(route.params.id, userStore.userNo, data)
    
    alert('리뷰가 수정되었습니다!')
    router.push(`/review/${route.params.id}`)
  } catch (error) {
    console.error('리뷰 수정 실패:', error)
    errorMessage.value = '리뷰 수정 중 오류가 발생했습니다.'
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
  
  loadReview()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

* {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.review-edit-container {
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
  overflow-x: hidden;
}

@keyframes gradientShift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

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

.loading {
  text-align: center;
  padding: 100px 20px;
  color: #636E72;
}

.review-detail {
  width: 90%;
  max-width: 1000px;
  margin: 40px auto 80px;
  background: #FFFFFF;
  border-radius: 24px;
  border: 1px solid var(--color-sub-1);
  box-shadow: 0 10px 30px rgba(89, 53, 39, 0.08);
  overflow: visible;
}

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
  box-shadow: 0 4px 15px rgba(89, 53, 39, 0.1);
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

.review-content {
  padding: 40px;
}

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
  transition: all 0.3s;
}

.title-input:focus {
  outline: none;
  border-bottom-color: var(--color-main);
}

.title-input::placeholder {
  color: #95A5A6;
}

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
  padding: 20px;
  color: var(--color-emphasis);
  resize: vertical;
  font-family: inherit;
  box-sizing: border-box;
  min-height: 180px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.content-textarea:focus {
  outline: none;
  border-color: var(--color-main);
  background: #FFFFFF;
  box-shadow: 0 0 0 4px rgba(242, 159, 5, 0.1);
}

.carousel-controls {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(0, 0, 0, 0.5);
  color: #2D3436;
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

.menu-edit-list {
  margin-top: 40px;
  margin-bottom: 30px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 30px;
}

.section-title {
  font-size: 20px;
  font-weight: 800;
  margin-bottom: 24px;
  color: var(--color-emphasis);
  letter-spacing: -0.5px;
}

.menu-list-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.menu-edit-item {
  padding: 24px;
  border: 1px solid var(--color-sub-1);
  background: #F8F9FA;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.menu-edit-item:hover {
  border-color: rgba(255, 107, 107, 0.15);
  background: #FFFFFF;
}

.menu-edit-item.active {
  border-color: var(--color-main);
  background: #FFFFFF;
  box-shadow: 0 4px 15px rgba(242, 159, 5, 0.1);
}

.menu-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.menu-id {
  font-weight: 800;
  font-size: 16px;
  color: var(--color-main);
}

.menu-rating-display {
  display: flex;
  align-items: center;
  gap: 4px;
  background: var(--color-sub-1);
  padding: 5px 12px;
  border-radius: 20px;
}

.star-icon {
  font-size: 14px;
}

.rating-val {
  font-weight: 800;
  font-size: 14px;
  color: var(--color-emphasis);
}

.edit-fields {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-group label {
  font-size: 13px;
  font-weight: 700;
  color: var(--color-emphasis);
}

.input-field {
  padding: 12px 14px;
  border: 2px solid transparent;
  background: #FFFFFF;
  border-radius: 10px;
  font-size: 14px;
  color: var(--color-emphasis);
  transition: all 0.2s;
}

.input-field:focus {
  outline: none;
  background: white;
  border-color: var(--color-main);
  box-shadow: 0 0 0 4px rgba(242, 159, 5, 0.1);
}

/* 이미지 업로드 */
.menu-image-upload-group {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.file-input {
  display: none;
}

.file-label {
  padding: 10px 16px;
  background: #F8F9FA;
  color: var(--color-emphasis);
  border: 1.5px solid var(--color-sub-1);
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 700;
  transition: all 0.3s ease;
  display: inline-block;
}

.file-label:hover {
  background: white;
  border-color: var(--color-main);
}

.btn-upload-menu {
  padding: 10px 20px;
  background: var(--color-main);
  color: #FFFFFF;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 700;
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(242, 159, 5, 0.2);
}

.btn-upload-menu:hover:not(:disabled) {
  transform: translateY(-2px);
  background: var(--color-sub-2);
  box-shadow: 0 6px 15px rgba(217, 121, 4, 0.3);
}

.btn-upload-menu:disabled {
  background: #ccc;
  color: #ffffff;
  cursor: not-allowed;
  box-shadow: none;
}

.btn-remove-menu-img {
  padding: 10px 16px;
  background: #F1F3F5;
  color: #ADB5BD;
  border: 1.5px solid #E0E0E0;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 700;
  transition: all 0.3s ease;
}

.btn-remove-menu-img:hover {
  background: #FFF5F5;
  color: #f44336;
  border-color: #f44336;
}

.menu-image-preview {
  width: 100%;
  max-width: 300px;
  aspect-ratio: 1;
  border: 1.5px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.03);
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-image-preview img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.preview-placeholder {
  color: #95A5A6;
  font-size: 14px;
}

.file-hint {
  display: block;
  margin-top: 8px;
  color: #95A5A6;
  font-size: 12px;
}

.error-message {
  padding: 14px 16px;
  background: #FFF5F5;
  color: #f44336;
  border: 1px solid rgba(244, 67, 54, 0.2);
  border-radius: 12px;
  font-size: 14px;
  margin-bottom: 20px;
  text-align: center;
}

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
  font-weight: 700;
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

@media (max-width: 768px) {
  .review-detail {
    width: 95%;
  }

  .review-content {
    padding: 20px;
  }

  .title-input {
    font-size: 24px;
  }

  .review-images {
    grid-template-columns: 1fr;
  }
}
</style>
