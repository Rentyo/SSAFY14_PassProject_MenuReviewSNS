<template>
  <div class="menu-container">
    <div class="menu-section">
      <h3>메뉴</h3>
      <div v-if="loadingMenus" class="loading-text">메뉴 정보 로딩 중...</div>
      <div v-else-if="menus.length === 0" class="empty-text">등록된 메뉴가 없습니다.</div>
      <ul v-else class="menu-list">
        <li v-for="menu in menus" :key="menu.menuId">
          <div class="menu-info">
            <span class="menu-name">{{ menu.menuName }}</span>
            <span class="menu-desc" v-if="menu.description">{{ menu.description }}</span>
            <div class="menu-tags" v-if="menu.tags && menu.tags.length > 0">
               <span v-for="tag in menu.tags" :key="tag.tagId" class="tag-badge">#{{ tag.tagName }}</span>
            </div>
          </div>
          <div class="menu-meta">
            <span class="menu-price">{{ formatPrice(menu.price) }}원</span>
            <span class="menu-rating">
              <span class="rating-icon">★</span>
              {{ calculateRating(menu.totalRating, menu.reviewCount) }}
            </span>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { getRestaurantMenu } from '@/api/menu'

const props = defineProps({
  restaurantId: {
    type: [Number, String],
    required: true
  }
})

const menus = ref([])
const loadingMenus = ref(false)

const fetchMenus = async () => {
  if (!props.restaurantId) return
  
  loadingMenus.value = true
  try {
    const response = await getRestaurantMenu(props.restaurantId)
    // active가 true인 메뉴만 필터링
    menus.value = (response.data || []).filter(menu => menu.active)
    console.log('Menus fetched:', menus.value)
  } catch (error) {
    console.error('메뉴 로딩 실패:', error)
  } finally {
    loadingMenus.value = false
  }
}

const formatPrice = (price) => {
  return price ? price.toLocaleString() : '0'
}

const calculateRating = (total, count) => {
  if (!count || count === 0) return '0.0'
  return (total / count).toFixed(1)
}

onMounted(() => {
  fetchMenus()
})

// restaurantId가 변경될 경우를 대비
watch(() => props.restaurantId, () => {
  fetchMenus()
})
</script>

<style scoped>
.menu-container {
  padding: 0;
  background: transparent;
  min-height: 400px;
  max-height: 600px;
  overflow-y: auto;
}

/* Custom Scrollbar */
.menu-container::-webkit-scrollbar {
  width: 8px;
}

.menu-container::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
}

.menu-container::-webkit-scrollbar-thumb {
  background: #FFE4CC;
  border-radius: 4px;
  transition: background 0.3s ease;
}

.menu-container::-webkit-scrollbar-thumb:hover {
  background: #FF6B6B;
}

.menu-section {
  width: 100%;
}

.menu-section h3 {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 24px;
  color: #2D3436;
  padding-bottom: 12px;
  border-bottom: 2px solid #FFE4CC;
}

.loading-text, .empty-text {
  color: #95A5A6;
  font-size: 15px;
  padding: 60px 0;
  text-align: center;
}

.menu-list {
  list-style: none;
  padding: 0;
}

.menu-list li {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 18px 16px;
  border-bottom: 1px solid #FFE4CC;
  transition: all 0.3s ease;
  border-radius: 12px;
  margin-bottom: 8px;
  background: #FFF5E6;
}

.menu-list li:hover {
  background: #FFFFFF;
  border-color: #FF6B6B;
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.15);
}

.menu-list li:last-child {
  border-bottom: none;
}

.menu-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
  padding-right: 20px;
}

.menu-name {
  font-weight: 700;
  font-size: 18px;
  color: #2D3436;
}

.menu-desc {
  font-size: 14px;
  color: #636E72;
  line-height: 1.5;
}

.menu-tags {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
  margin-top: 4px;
}

.tag-badge {
  font-size: 12px;
  color: #FF6B6B;
  background: rgba(255, 107, 107, 0.1);
  border: 1px solid #FFE4CC;
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 600;
}

.menu-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
  min-width: 80px;
}

.menu-price {
  font-weight: 700;
  color: #FF6B6B;
  font-size: 17px;
}

.menu-rating {
  font-size: 13px;
  color: #FFA94D;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
  background: rgba(255, 169, 77, 0.1);
  border: 1px solid #FFE4CC;
  padding: 4px 8px;
  border-radius: 12px;
}

.rating-icon {
  font-size: 12px;
}
</style>
