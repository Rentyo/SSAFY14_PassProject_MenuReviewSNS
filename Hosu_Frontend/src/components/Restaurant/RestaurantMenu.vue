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
            <div class="menu-rating">
              <div class="stars-container">
                <div class="stars-bg">
                  <span v-for="i in 5" :key="'bg-'+i">☆</span>
                </div>
                <div class="stars-fill" :style="{ width: (calculateRating(menu.totalRating, menu.reviewCount) / 5 * 100) + '%' }">
                  <span v-for="i in 5" :key="'fill-'+i">★</span>
                </div>
              </div>
              <span class="rating-value">
                <template v-if="calculateRating(menu.totalRating, menu.reviewCount) > 0">
                  {{ calculateRating(menu.totalRating, menu.reviewCount).toFixed(1) }}
                </template>
                <span v-else class="no-review-text">리뷰 없음</span>
              </span>
            </div>
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
  if (!count || count === 0) return 0
  return total / count
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
  background: rgba(89, 53, 39, 0.05);
  border-radius: 4px;
}

.menu-container::-webkit-scrollbar-thumb {
  background: #F2DCB3;
  border-radius: 4px;
  transition: background 0.3s ease;
}

.menu-container::-webkit-scrollbar-thumb:hover {
  background: #F29F05;
}

.menu-section {
  width: 100%;
}

.menu-section h3 {
  font-size: 22px;
  font-weight: 800;
  margin-bottom: 24px;
  color: #593527;
  padding-bottom: 12px;
  border-bottom: 2px solid #F2DCB3;
}

.loading-text, .empty-text {
  color: #593527;
  opacity: 0.5;
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
  padding: 24px 60px 24px 30px;
  border-bottom: 1.5px solid #F2DCB3;
  transition: all 0.3s ease;
  background: #FFFFFF;
  margin-bottom: 0;
  border-left: none;
  border-right: none;
  border-top: none;
  border-radius: 0;
}

.menu-list li:hover {
  background: rgba(242, 159, 5, 0.02);
  border-color: #F29F05;
  padding-left: 40px;
}

.menu-list li:last-child {
  margin-bottom: 0;
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
  color: #593527;
}

.menu-desc {
  font-size: 14px;
  color: #593527;
  opacity: 0.7;
  line-height: 1.5;
}

.menu-tags {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
  margin-top: 4px;
}

.tag-badge {
  font-size: 11px;
  color: #593527;
  background: #F2DCB3;
  border: 1px solid #F2DCB3;
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 700;
}

.menu-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  min-width: 140px;
  border-left: 1px solid #F2DCB3;
  padding-left: 30px;
}

.menu-price {
  font-weight: 800;
  color: #D97904;
  font-size: 17px;
}

.menu-rating {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  background: transparent;
  padding: 0;
  margin-top: 4px;
}

.stars-container {
  position: relative;
  display: inline-block;
  font-size: 24px;
  line-height: 1;
}

.stars-bg {
  display: flex;
  color: #FFFFFF;
  letter-spacing: -2px;
  -webkit-text-stroke: 0.5px #95A5A6;
}

.stars-fill {
  position: absolute;
  top: 0;
  left: 0;
  display: flex;
  color: var(--color-main);
  overflow: hidden;
  white-space: nowrap;
  letter-spacing: -2px;
}

.rating-value {
  font-size: 20px;
  font-weight: 800;
  color: var(--color-emphasis);
  line-height: 1.2;
}

.no-review-text {
  font-size: 14px;
  color: #95A5A6;
  font-weight: 500;
}
</style>
