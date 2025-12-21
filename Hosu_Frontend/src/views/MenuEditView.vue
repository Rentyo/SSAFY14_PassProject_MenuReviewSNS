<template>
  <div class="menu-edit-container">
    <div class="menu-edit-content">
      <div class="menu-edit-header">
        <h2>메뉴 수정</h2>
        <button class="btn-back" @click="goBack">← 돌아가기</button>
      </div>

      <!-- 로딩 중 -->
      <div v-if="loading" class="loading">
        <p>메뉴 정보를 불러오는 중...</p>
      </div>

      <div v-else class="menu-edit-form">
        <!-- 메뉴명 -->
        <div class="form-group">
          <label for="menuName">메뉴명 <span class="required">*</span></label>
          <input
            id="menuName"
            v-model="menuForm.menuName"
            type="text"
            placeholder="메뉴명을 입력하세요"
            class="form-input"
          />
        </div>

        <!-- 가격 -->
        <div class="form-group">
          <label for="price">가격 <span class="required">*</span></label>
          <input
            id="price"
            v-model.number="menuForm.price"
            type="number"
            placeholder="가격을 입력하세요"
            class="form-input"
            min="0"
          />
        </div>

        <!-- 설명 -->
        <div class="form-group">
          <label for="description">설명</label>
          <textarea
            id="description"
            v-model="menuForm.description"
            placeholder="메뉴 설명을 입력하세요"
            class="form-textarea"
            rows="4"
          ></textarea>
        </div>

        <!-- 태그 선택 -->
        <div class="form-group">
          <label>태그 선택</label>
          <div v-if="loadingTags" class="loading-tags">
            <p>태그를 불러오는 중...</p>
          </div>
          <div v-else class="tags-container">
            <div
              v-for="tag in availableTags"
              :key="tag.tagId"
              class="tag-checkbox"
            >
              <input
                :id="`tag-${tag.tagId}`"
                v-model="menuForm.selectedTags"
                type="checkbox"
                :value="tag.tagId"
                class="checkbox-input"
              />
              <label :for="`tag-${tag.tagId}`" class="checkbox-label">
                {{ tag.tagName }}
              </label>
            </div>
          </div>
        </div>

        <!-- 선택된 태그 미리보기 -->
        <div v-if="menuForm.selectedTags.length > 0" class="selected-tags-preview">
          <p class="preview-label">선택된 태그:</p>
          <div class="preview-tags">
            <span
              v-for="tagId in menuForm.selectedTags"
              :key="tagId"
              class="tag-bubble"
            >
              {{ getTagName(tagId) }}
            </span>
          </div>
        </div>

        <!-- 버튼 -->
        <div class="form-actions">
          <button class="btn-cancel" @click="goBack">취소</button>
          <button class="btn-submit" @click="submitMenu">수정 완료</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getMenuTag } from '@/api/search'
import { updateMenu } from '@/api/menu'
import { getRestaurantId, getRestaurantDetail } from '@/api/restaurants'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const menuForm = ref({
  menuName: '',
  price: null,
  description: '',
  selectedTags: []
})

const availableTags = ref([])
const loadingTags = ref(false)
const loading = ref(false)
const restaurantId = ref(null)
const restaurantCategory = ref('')
const menuId = ref(null)

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

// 전달받은 메뉴 데이터로 폼 초기화
const initializeForm = (menuData) => {
  if (!menuData) {
    alert('메뉴 정보를 찾을 수 없습니다.')
    router.push('/mypage/ceo')
    return false
  }
  
  // 폼에 기존 데이터 채우기
  menuForm.value.menuName = menuData.menuName
  menuForm.value.price = menuData.price
  menuForm.value.description = menuData.description || ''
  
  // 기존 태그 ID 배열로 변환
  if (menuData.tags && menuData.tags.length > 0) {
    menuForm.value.selectedTags = menuData.tags.map(tag => tag.tagId)
  }
  
  return true
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
    
    // 4. sessionStorage에서 메뉴 데이터 가져오기
    const menuDataStr = sessionStorage.getItem('editMenuData')
    if (menuDataStr) {
      const menuData = JSON.parse(menuDataStr)
      initializeForm(menuData)
      // 사용 후 삭제
      sessionStorage.removeItem('editMenuData')
    }
    
    loading.value = false
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

// 메뉴 수정 제출
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
      menuId: menuId.value,
      restaurantId: restaurantId.value,
      menuName: menuForm.value.menuName,
      price: menuForm.value.price,
      description: menuForm.value.description || '',
      tagIds: menuForm.value.selectedTags
    }

    await updateMenu(menuData)
    alert('메뉴가 성공적으로 수정되었습니다.')
    router.push('/mypage/ceo')
  } catch (error) {
    console.error('메뉴 수정 실패:', error)
    alert('메뉴 수정에 실패했습니다.')
  }
}

onMounted(() => {
  // URL에서 menuId 가져오기
  menuId.value = route.params.id
  
  if (!menuId.value) {
    alert('잘못된 접근입니다.')
    router.push('/mypage/ceo')
    return
  }
  
  // sessionStorage에 메뉴 데이터가 없으면 에러
  if (!sessionStorage.getItem('editMenuData')) {
    alert('메뉴 정보를 찾을 수 없습니다.')
    router.push('/mypage/ceo')
    return
  }
  
  loading.value = true
  fetchRestaurantInfo()
})
</script>

<style scoped>
.menu-edit-container {
  /* Custom Requested Palette */
  --color-bg: #F2F2F2;
  --color-emphasis: #593527;
  --color-main: #F29F05;
  --color-sub-1: #F2DCB3;
  --color-sub-2: #D97904;

  min-height: 100vh;
  background: var(--color-bg);
  padding: 40px 20px;
}

.menu-edit-content {
  max-width: 800px;
  margin: 0 auto;
}

.menu-edit-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 2px solid var(--color-sub-1);
}

.menu-edit-header h2 {
  font-size: 28px;
  color: var(--color-emphasis);
  font-weight: 800;
  margin: 0;
  letter-spacing: -0.5px;
}

.btn-back {
  padding: 10px 20px;
  background: white;
  color: var(--color-emphasis);
  border: 1.5px solid var(--color-sub-1);
  border-radius: 12px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 10px rgba(89, 53, 39, 0.05);
}

.btn-back:hover {
  background: var(--color-bg);
  color: var(--color-main);
  border-color: var(--color-main);
  transform: translateX(-4px);
}

.loading {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.loading p {
  font-size: 16px;
  color: #666;
}

.menu-edit-form {
  background: white;
  border-radius: 24px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(89, 53, 39, 0.08);
  border: 1px solid var(--color-sub-1);
}

.form-group {
  margin-bottom: 25px;
}

.form-group label {
  display: block;
  font-size: 16px;
  font-weight: 700;
  color: var(--color-emphasis);
  margin-bottom: 10px;
}

.required {
  color: #c62828;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 14px 18px;
  background: #F8F9FA;
  border: 2px solid transparent;
  border-radius: 12px;
  font-size: 15px;
  color: var(--color-emphasis);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-sizing: border-box;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  background: white;
  border-color: var(--color-main);
  box-shadow: 0 0 0 4px rgba(242, 159, 5, 0.1);
}

.form-textarea {
  resize: vertical;
  font-family: inherit;
}

.loading-tags {
  text-align: center;
  padding: 20px;
  color: #999;
}

.tags-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 8px;
}

.tag-checkbox {
  display: flex;
  align-items: center;
}

.checkbox-input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  margin-right: 8px;
}

.checkbox-label {
  font-size: 14px;
  color: #333;
  cursor: pointer;
  user-select: none;
}

.selected-tags-preview {
  margin-top: 20px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 8px;
}

.preview-label {
  font-size: 14px;
  font-weight: 600;
  color: #666;
  margin-bottom: 10px;
}

.preview-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-bubble {
  display: inline-block;
  padding: 6px 16px;
  background: var(--color-sub-1);
  color: var(--color-emphasis);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
  box-shadow: 0 4px 10px rgba(89, 53, 39, 0.1);
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 30px;
  justify-content: flex-end;
}

.form-actions button {
  padding: 12px 30px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel {
  background: var(--color-sub-1);
  color: var(--color-emphasis);
  font-weight: 700;
}

.btn-cancel:hover {
  background: #e0e0e0;
}

.btn-submit {
  background: var(--color-main);
  color: #FFFFFF;
  font-weight: 800;
  box-shadow: 0 4px 15px rgba(242, 159, 5, 0.3);
}

.btn-submit:hover {
  transform: translateY(-2px);
  background: var(--color-sub-2);
  box-shadow: 0 6px 20px rgba(217, 121, 4, 0.35);
}

/* 반응형 */
@media (max-width: 768px) {
  .menu-edit-container {
    padding: 20px 10px;
  }

  .menu-edit-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .menu-edit-form {
    padding: 25px;
  }

  .tags-container {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions button {
    width: 100%;
  }
}
</style>
