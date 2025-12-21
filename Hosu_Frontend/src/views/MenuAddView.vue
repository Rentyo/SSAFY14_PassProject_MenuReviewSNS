<template>
  <div class="menu-add-container">
    <div class="menu-add-content">
      <div class="menu-add-header">
        <div class="header-title">
          <h2>메뉴 추가</h2>
          <p class="header-subtitle">새로운 메뉴 정보를 입력해주세요</p>
        </div>
        <button class="btn-back" @click="goBack">
          <span>←</span> 돌아가기
        </button>
      </div>

      <div class="menu-add-form-card">
        <!-- 메뉴명 & 가격 (2열 Layout) -->
        <div class="form-row">
          <div class="form-group grow-2">
            <label for="menuName">메뉴명 <span class="required">*</span></label>
            <div class="input-wrapper">
              <input
                id="menuName"
                v-model="menuForm.menuName"
                type="text"
                placeholder="예: 호수 스테이크"
                class="form-input"
              />
            </div>
          </div>

          <div class="form-group grow-1">
            <label for="price">가격 <span class="required">*</span></label>
            <div class="input-wrapper price-wrapper">
              <input
                id="price"
                :value="displayPrice"
                @input="handlePriceInput"
                type="text"
                placeholder="0"
                class="form-input price-input"
              />
              <span class="unit-text">원</span>
            </div>
          </div>
        </div>

        <!-- 설명 -->
        <div class="form-group">
          <label for="description">설명</label>
          <div class="input-wrapper">
            <textarea
              id="description"
              v-model="menuForm.description"
              placeholder="메뉴에 대한 설명을 자유롭게 적어주세요"
              class="form-textarea"
              rows="4"
            ></textarea>
          </div>
        </div>

        <!-- 태그 선택 (Interaction Enhanced) -->
        <div class="form-group">
          <label>태그 선택 <span class="required">*</span></label>
          <div v-if="loadingTags" class="loading-tags">
            <div class="spinner"></div>
            <p>태그를 불러오는 중...</p>
          </div>
          <div v-else class="tags-grid">
            <label
              v-for="tag in availableTags"
              :key="tag.tagId"
              :class="['tag-card', { 'is-selected': menuForm.selectedTags.includes(tag.tagId) }]"
            >
              <input
                type="checkbox"
                v-model="menuForm.selectedTags"
                :value="tag.tagId"
                class="hidden-checkbox"
              />
              <span class="tag-name">{{ tag.tagName }}</span>
              <div class="check-icon" v-if="menuForm.selectedTags.includes(tag.tagId)">
                ✓
              </div>
            </label>
          </div>
        </div>

        <!-- 선택된 태그 요약 -->
        <div class="selection-summary" v-if="menuForm.selectedTags.length > 0">
          <span class="summary-label">선택됨:</span>
          <div class="summary-tags">
            <span v-for="tagId in menuForm.selectedTags" :key="tagId" class="summary-pill">
              # {{ getTagName(tagId) }}
            </span>
          </div>
        </div>

        <!-- 버튼 -->
        <div class="form-actions">
          <button class="btn-cancel" @click="goBack">취소</button>
          <button class="btn-submit" @click="submitMenu">
            메뉴 등록하기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMenuTag } from '@/api/search'
import { createMenu } from '@/api/menu'
import { getRestaurantId, getRestaurantDetail } from '@/api/restaurants'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const menuForm = ref({
  menuName: '',
  price: null,
  description: '',
  selectedTags: []
})

const displayPrice = ref('')

// 가격 포맷팅 (콤마 추가)
const formatPrice = (val) => {
  if (val === null || val === undefined || val === '') return ''
  return val.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

// 가격 입력 핸들러
const handlePriceInput = (e) => {
  let value = e.target.value.replace(/,/g, '') // 콤마 제거
  
  // 숫자만 허용
  if (value && !/^\d+$/.test(value)) {
    e.target.value = displayPrice.value
    return
  }

  if (value === '') {
    menuForm.value.price = null
    displayPrice.value = ''
    return
  }

  const numericValue = parseInt(value, 10)
  menuForm.value.price = numericValue
  displayPrice.value = formatPrice(numericValue)
}

const availableTags = ref([])
const loadingTags = ref(false)
const restaurantId = ref(null)
const restaurantCategory = ref('')

// 태그 데이터 가져오기 (카테고리별)
const fetchTags = async (category) => {
  loadingTags.value = true
  try {
    const response = await getMenuTag(category)
    availableTags.value = response.data
  } catch (error) {
    console.error('태그 조회 실패:', error)
    alert('태그를 불러오는데 실패했습니다.')
  } finally {
    loadingTags.value = false
  }
}

// 식당 정보 가져오기
const fetchRestaurantInfo = async () => {
  try {
    // 1. 식당 ID 가져오기
    const idResponse = await getRestaurantId(userStore.userNo)
    restaurantId.value = idResponse.data
    
    if (!restaurantId.value) {
      alert('등록된 식당이 없습니다.')
      router.push('/mypage/ceo')
      return
    }
    
    // 2. 식당 상세 정보 가져오기 (카테고리 포함)
    const detailResponse = await getRestaurantDetail(restaurantId.value)
    restaurantCategory.value = detailResponse.data.category
    
    // 3. 카테고리에 맞는 태그 가져오기
    if (restaurantCategory.value) {
      await fetchTags(restaurantCategory.value)
    }
  } catch (error) {
    console.error('식당 정보 조회 실패:', error)
    alert('식당 정보를 불러오는데 실패했습니다.')
    router.push('/mypage/ceo')
  }
}

// 태그 이름 가져오기
const getTagName = (tagId) => {
  const tag = availableTags.value.find(t => t.tagId === tagId)
  return tag ? tag.tagName : ''
}

// 뒤로가기
const goBack = () => {
  router.push('/mypage/ceo')
}

// 메뉴 제출
const submitMenu = async () => {
  // 유효성 검사
  if (!menuForm.value.menuName.trim()) {
    alert('메뉴명을 입력해주세요.')
    return
  }
  
  if (!menuForm.value.price || menuForm.value.price <= 0) {
    alert('올바른 가격을 입력해주세요.')
    return
  }

  if (menuForm.value.selectedTags.length === 0) {
    alert('적어도 한 개의 태그를 선택해 주세요.')
    return
  }

  if (!restaurantId.value) {
    alert('식당 정보를 찾을 수 없습니다.')
    return
  }

  try {
    const menuData = {
      restaurantId: restaurantId.value,
      menuName: menuForm.value.menuName,
      price: menuForm.value.price,
      description: menuForm.value.description || '',
      tagIds: menuForm.value.selectedTags
    }

    await createMenu(menuData)
    alert('메뉴가 성공적으로 추가되었습니다.')
    router.push('/mypage/ceo')
  } catch (error) {
    console.error('메뉴 추가 실패:', error)
    alert('메뉴 추가에 실패했습니다.')
  }
}

onMounted(() => {
  fetchRestaurantInfo()
})
</script>

<style scoped>
.menu-add-container {
  /* Custom Requested Palette */
  --color-bg: #F2F2F2;
  --color-emphasis: #593527;
  --color-main: #F29F05;
  --color-sub-1: #F2DCB3;
  --color-sub-2: #D97904;

  min-height: 100vh;
  background: var(--color-bg);
  padding: 60px 20px;
}

.menu-add-content {
  max-width: 720px;
  margin: 0 auto;
}

.menu-add-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
}

.header-title h2 {
  font-size: 32px;
  font-weight: 800;
  color: var(--color-emphasis);
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.header-subtitle {
  font-size: 16px;
  color: var(--color-emphasis);
  opacity: 0.7;
  margin: 0;
}

.btn-back {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  background: white;
  color: var(--color-emphasis);
  border: 1.5px solid var(--color-sub-1);
  border-radius: 12px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 4px rgba(89, 53, 39, 0.05);
}

.btn-back:hover {
  border-color: var(--color-main);
  color: var(--color-main);
  transform: translateX(-4px);
  background: var(--color-bg);
}

.menu-add-form-card {
  background: white;
  border-radius: 24px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(89, 53, 39, 0.08);
  border: 1px solid var(--color-sub-1);
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
}

.grow-2 { flex: 2; }
.grow-1 { flex: 1; }

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  font-size: 15px;
  font-weight: 700;
  color: var(--color-emphasis);
  margin-bottom: 10px;
  padding-left: 2px;
}

.required {
  color: #FF4D4D;
  margin-left: 2px;
}

.input-wrapper {
  position: relative;
  transition: all 0.2s;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 14px 18px;
  background: #F8F9FA;
  border: 2px solid transparent;
  border-radius: 14px;
  font-size: 16px;
  color: var(--color-emphasis);
  transition: all 0.2s ease;
  box-sizing: border-box;
}

.form-input::placeholder,
.form-textarea::placeholder {
  color: #ADB5BD;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  background: white;
  border-color: var(--color-main);
  box-shadow: 0 0 0 4px rgba(242, 159, 5, 0.1);
}

.price-wrapper {
  display: flex;
  align-items: center;
}

.price-input {
  padding-right: 45px;
  text-align: right;
  font-weight: 800;
  color: var(--color-sub-2);
}

.unit-text {
  position: absolute;
  right: 18px;
  font-size: 16px;
  font-weight: 700;
  color: var(--color-emphasis);
}

.form-textarea {
  resize: none;
  min-height: 120px;
  line-height: 1.6;
}

/* 태그 카드 스타일 */
.tags-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(130px, 150px));
  justify-content: center;
  gap: 12px;
  margin-top: 8px;
}

.tag-card {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 14px;
  background: #F8F9FA;
  border: 2px solid transparent;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none;
}

.tag-card:hover {
  background: var(--color-bg);
  border-color: var(--color-sub-1);
  transform: translateY(-2px);
}

.tag-card.is-selected {
  background: #FFFFFF;
  border-color: var(--color-main);
  box-shadow: 0 4px 12px rgba(242, 159, 5, 0.15);
}

.tag-name {
  display: block;
  width: 100%;
  font-size: 14px;
  font-weight: 600;
  color: #495057;
  text-align: center;
  transition: all 0.2s;
}

.tag-card.is-selected .tag-name {
  color: var(--color-main);
}

.hidden-checkbox {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
  pointer-events: none;
}

.check-icon {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 24px;
  height: 24px;
  background: var(--color-main);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 800;
  box-shadow: 0 4px 8px rgba(242, 159, 5, 0.3);
  animation: scaleIn 0.2s ease-out;
  z-index: 2;
}

@keyframes scaleIn {
  from { transform: scale(0); }
  to { transform: scale(1); }
}

.selection-summary {
  margin-top: 24px;
  padding: 16px;
  background: #FFF9F9;
  border-radius: 14px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.summary-label {
  font-size: 13px;
  font-weight: 800;
  color: var(--color-emphasis);
  white-space: nowrap;
}

.summary-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.summary-pill {
  font-size: 12px;
  font-weight: 700;
  color: var(--color-emphasis);
  background: white;
  padding: 4px 10px;
  border-radius: 8px;
  border: 1px solid var(--color-sub-1);
}

.loading-tags {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  color: #999;
}

.spinner {
  width: 24px;
  height: 24px;
  border: 3px solid #F3F3F3;
  border-top: 3px solid var(--color-main);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.form-actions {
  display: flex;
  gap: 16px;
  margin-top: 40px;
}

.btn-cancel {
  flex: 1;
  padding: 16px;
  background: var(--color-sub-1);
  color: var(--color-emphasis);
  border: none;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel:hover {
  background: #E9ECEF;
}

.btn-submit {
  flex: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 16px;
  background: var(--color-main);
  color: white;
  border: none;
  border-radius: 16px;
  font-size: 17px;
  font-weight: 800;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 8px 20px rgba(242, 159, 5, 0.2);
}

.btn-submit:hover {
  transform: translateY(-3px);
  background: var(--color-sub-2);
  box-shadow: 0 12px 28px rgba(217, 121, 4, 0.35);
}

.btn-submit:active {
  transform: translateY(-1px);
}

/* 반응형 */
@media (max-width: 600px) {
  .menu-add-container {
    padding: 30px 16px;
  }
  
  .menu-add-form-card {
    padding: 24px;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .btn-submit {
    flex: 1;
  }
  
  .tags-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
