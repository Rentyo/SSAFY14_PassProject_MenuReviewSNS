<template>
  <div class="menu-section">
    <div class="menu-header">
      <h3>메뉴 관리</h3>
      <button class="btn-add-menu" @click="router.push('/menu/add')">
        <img src="@/assets/more.png" alt="메뉴 추가" class="add-icon" />
      </button>
    </div>

    <!-- 로딩 중 -->
    <div v-if="loading" class="loading">
      <p>메뉴를 불러오는 중...</p>
    </div>

    <!-- 메뉴가 없을 때 -->
    <div v-else-if="menuItems.length === 0" class="no-menu">
      <p>등록된 메뉴가 없습니다.</p>
      <button class="btn-create-menu" @click="router.push('/menu/add')">첫 메뉴 등록하기</button>
    </div>

    <!-- 메뉴 리스트 및 필터 -->
    <div v-else>
      <!-- 태그 필터 -->
      <div v-if="categoryTags.length > 0" class="filter-tags">
        <button 
          class="filter-btn" 
          :class="{ active: selectedTagId === null }"
          @click="selectTag(null)"
        >
          전체
        </button>
        <button 
          v-for="tag in categoryTags" 
          :key="tag.tagId"
          class="filter-btn"
          :class="{ active: selectedTagId === tag.tagId }"
          @click="selectTag(tag.tagId)"
        >
          {{ tag.tagName }}
        </button>
      </div>

      <div class="menu-list">
        <div
          v-for="menu in filteredMenuItems"
          :key="menu.menuId"
          class="menu-item"
          :class="{ inactive: !menu.active }"
        >
          <!-- 메뉴 정보 -->
          <div class="menu-info">
            <div class="menu-name-row">
              <h4 class="menu-name">{{ menu.menuName }}</h4>
              <span class="status-badge" :class="{ active: menu.active, inactive: !menu.active }">
                {{ menu.active ? '활성 메뉴' : '비활성 메뉴' }}
              </span>
            </div>
            <p class="menu-price">{{ formatPrice(menu.price) }}원</p>
            
            <!-- 메뉴 태그 -->
            <div v-if="menu.tags && menu.tags.length > 0" class="menu-tags">
              <span
                v-for="tag in menu.tags"
                :key="tag.tagId"
                class="tag-bubble"
              >
                {{ tag.tagName }}
              </span>
            </div>
          </div>

          <!-- 메뉴 액션 -->
          <div class="menu-actions">
            <button class="btn-edit" @click.stop="editMenu(menu.menuId)">
              <img src="@/assets/edit.png" alt="수정" class="action-icon" />
            </button>
            <button class="btn-delete" @click.stop="deleteMenu(menu.menuId)">
              <img src="@/assets/delete.png" alt="삭제" class="action-icon" />
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getRestaurantMenu, deleteRestaurantMenu } from '@/api/menu'
import { getRestaurantId, getRestaurantDetail } from '@/api/restaurants'
import { getMenuTag } from '@/api/search'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const menuItems = ref([])
const loading = ref(false)
const restaurantId = ref(null)
const categoryTags = ref([]) // 카테고리 태그 목록
const selectedTagId = ref(null) // 선택된 태그 ID (null은 전체)

// 식당 ID와 메뉴 데이터 가져오기
const fetchMenus = async () => {
  loading.value = true
  try {
    // 1. 먼저 userNo로 restaurantId 가져오기
    const restaurantResponse = await getRestaurantId(userStore.userNo)
    restaurantId.value = restaurantResponse.data
    
    if (!restaurantId.value) {
      alert('등록된 식당이 없습니다.')
      return
    }
    
    // 2. restaurantId로 메뉴 가져오기
    const menuResponse = await getRestaurantMenu(restaurantId.value)
    menuItems.value = menuResponse.data
    console.log(menuItems.value);

    // 3. 식당 상세 정보에서 카테고리 가져오기 및 태그 목록 조회
    const detailResponse = await getRestaurantDetail(restaurantId.value)
    const category = detailResponse.data.category
    
    if (category) {
      const tagResponse = await getMenuTag(category)
      categoryTags.value = tagResponse.data
    }

  } catch (error) {
    console.error('메뉴 조회 실패:', error)
    alert('메뉴를 불러오는데 실패했습니다.')
  } finally {
    loading.value = false
  }
}

const filteredMenuItems = computed(() => {
  if (!selectedTagId.value) {
    return menuItems.value
  }
  return menuItems.value.filter(menu => 
    menu.tags && menu.tags.some(tag => tag.tagId === selectedTagId.value)
  )
})

const selectTag = (tagId) => {
  selectedTagId.value = tagId
}

const formatPrice = (price) => {
  return price.toLocaleString('ko-KR')
}

const editMenu = (menuId) => {
  // 메뉴 데이터 찾기
  const menu = menuItems.value.find(m => m.menuId === menuId)
  if (!menu) return
  
  // sessionStorage에 메뉴 데이터 임시 저장
  sessionStorage.setItem('editMenuData', JSON.stringify(menu))
  
  // 수정 페이지로 이동
  router.push(`/menu/${menuId}/edit`)
}

const deleteMenu = async (menuId) => {
  // 현재 메뉴 찾기
  const menu = menuItems.value.find(m => m.menuId === menuId)
  if (!menu) return

  const action = menu.active ? '비활성화' : '활성화'
  if (!confirm(`이 메뉴를 ${action}하시겠습니까?`)) {
    return
  }

  try {
    const newActiveValue = menu.active ? 1 : 0
    await deleteRestaurantMenu(menuId, newActiveValue)
    
    // UI 업데이트: active 상태 토글
    menu.active = !menu.active
    
    alert(`메뉴가 ${action}되었습니다.`)
  } catch (error) {
    console.error('메뉴 상태 변경 실패:', error)
    alert('메뉴 상태 변경에 실패했습니다.')
  }
}

onMounted(() => {
  fetchMenus()
})
</script>

<style scoped>
.menu-section {
  width: 100%;
}

.menu-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 2px solid rgba(255, 255, 255, 0.3);
}

.menu-header h3 {
  font-size: 24px;
  color: var(--color-emphasis);
  margin: 0;
  font-weight: 800;
}

.btn-add-menu {
  padding: 8px;
  background: var(--color-sub-1);
  border: 1px solid var(--color-sub-1);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-icon {
  width: 24px;
  height: 24px;
  object-fit: contain;
  filter: brightness(0) saturate(100%) invert(20%) sepia(12%) peachpuff(13%) hue-rotate(334deg) brightness(95%) contrast(92%); /* Approximating brown */
  filter: brightness(0.5);
}

.btn-add-menu:hover {
  background: #FFFFFF;
  border-color: var(--color-main);
  transform: translateY(-2px);
}

.loading {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.loading p {
  font-size: 16px;
  color: #666;
}

.no-menu {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.no-menu p {
  font-size: 18px;
  color: #999;
  margin-bottom: 20px;
}

.btn-create-menu {
  padding: 12px 30px;
  background: var(--color-main);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(242, 159, 5, 0.2);
}

.btn-create-menu:hover {
  transform: translateY(-2px);
  background: var(--color-sub-2);
  box-shadow: 0 6px 16px rgba(217, 121, 4, 0.3);
}

.menu-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(89, 53, 39, 0.05);
  border: 1px solid var(--color-sub-1);
  transition: all 0.3s;
}

.menu-item.inactive {
  opacity: 0.5;
}

.menu-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(89, 53, 39, 0.12);
}

.menu-info {
  flex: 1;
}

.menu-name-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.menu-name {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-emphasis);
  margin: 0;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.active {
  background: #e8f5e9;
  color: #2e7d32;
}

.status-badge.inactive {
  background: var(--color-sub-1);
  color: var(--color-emphasis);
}

.menu-price {
  font-size: 18px;
  font-weight: bold;
  color: var(--color-sub-2);
  margin: 0 0 12px 0;
}

.menu-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-bubble {
  display: inline-block;
  padding: 6px 14px;
  background: var(--color-sub-1);
  color: var(--color-emphasis);
  border: 1px solid var(--color-sub-1);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.2s;
}

.tag-bubble:hover {
  transform: translateY(-1px);
  background: white;
  border-color: var(--color-main);
  box-shadow: 0 4px 10px rgba(242, 159, 5, 0.2);
}

.menu-actions {
  display: flex;
  gap: 8px;
  margin-left: 20px;
}

.menu-actions button {
  padding: 8px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-icon {
  width: 20px;
  height: 20px;
  object-fit: contain;
}

.btn-edit {
  background: var(--color-sub-1);
  border: 1px solid var(--color-sub-1);
}

.btn-edit:hover {
  background: #FFFFFF;
  border-color: var(--color-main);
}

.btn-delete {
  background: var(--color-sub-1);
  border: 1px solid var(--color-sub-1);
}

.btn-delete:hover {
  background: #FFFFFF;
  border-color: var(--color-emphasis);
}

/* 반응형 */
@media (max-width: 768px) {
  .menu-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .menu-actions {
    margin-left: 0;
    margin-top: 15px;
    width: 100%;
  }


  .menu-actions button {
    flex: 1;
  }
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
  padding: 10px 0;
}

.filter-btn {
  padding: 8px 16px;
  border: 1px solid #ccc;
  background-color: white;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-btn:hover {
  border-color: var(--color-main);
  color: var(--color-main);
}

.filter-btn.active {
  background: var(--color-main);
  color: white;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(242, 159, 5, 0.2);
}
</style>
