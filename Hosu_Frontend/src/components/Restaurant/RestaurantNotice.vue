<template>
  <div class="notices-container">
    <div v-if="loading" class="loading">
      로딩 중...
    </div>
    <div v-else-if="notices.length === 0" class="no-notices">
      <p>공지사항이 없습니다.</p>
    </div>
    <div v-else class="notice-list">
      <div v-for="notice in notices" :key="notice.id" class="notice-item">
        <div class="notice-header">
          <div class="title-with-badge">
            <span class="notice-badge" :class="getImportanceClass(notice.importance)">
              {{ getImportanceBadge(notice.importance) }}
            </span>
            <span class="notice-title">{{ notice.title }}</span>
          </div>
          <span class="notice-date">{{ formatDate(notice.createdAt) }}</span>
        </div>
        <div class="notice-content">
          {{ notice.content }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getRestaurantNotices } from '@/api/restaurants'

const props = defineProps({
  restaurantId: {
    type: [String, Number],
    required: true
  }
})

const notices = ref([])
const loading = ref(true)

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 중요도 배지 텍스트
const getImportanceBadge = (importance) => {
  switch(importance) {
    case 3: return '긴급'
    case 2: return '중요'
    case 1:
    default: return '일반'
  }
}

// 중요도 클래스 반환
const getImportanceClass = (importance) => {
  switch(importance) {
    case 3: return 'urgent'
    case 2: return 'important'
    case 1:
    default: return 'normal'
  }
}

const loadNotices = async () => {
  try {
    loading.value = true
    const response = await getRestaurantNotices(props.restaurantId)
    // 데이터 구조에 맞춰 수정 필요
    notices.value = response.data 
  } catch (error) {
    console.error('공지사항 로딩 실패:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadNotices()
})
</script>

<style scoped>
.notices-container {
  padding: 0;
  max-height: 600px;
  overflow-y: auto;
  background: transparent;
}

/* Custom Scrollbar */
.notices-container::-webkit-scrollbar {
  width: 8px;
}

.notices-container::-webkit-scrollbar-track {
  background: rgba(89, 53, 39, 0.05);
  border-radius: 4px;
}

.notices-container::-webkit-scrollbar-thumb {
  background: #F2DCB3;
  border-radius: 4px;
  transition: background 0.3s ease;
}

.notices-container::-webkit-scrollbar-thumb:hover {
  background: #F29F05;
}

.loading, .no-notices {
  text-align: center;
  padding: 60px 0;
  color: #593527;
  opacity: 0.5;
  font-size: 15px;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 8px 4px; /* Added padding to prevent clipping on hover */
}

.notice-item {
  border: 1px solid #F2DCB3;
  border-radius: 16px;
  padding: 24px;
  background: #FFFFFF;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(89, 53, 39, 0.04);
}

.notice-item:hover {
  background: #FFFFFF;
  border-color: #F29F05;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(89, 53, 39, 0.08);
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  border-bottom: 1px solid #F2DCB3;
  padding-bottom: 16px;
}

.title-with-badge {
  display: flex;
  align-items: center;
  gap: 12px;
}

.notice-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 800;
  white-space: nowrap;
}

.notice-badge.normal {
  background: #F1F3F5;
  color: #495057;
  border: 1px solid #DEE2E6;
}

.notice-badge.important {
  background: #E7F5FF;
  color: #1C7ED6;
  border: 1px solid #A5D8FF;
}

.notice-badge.urgent {
  background: linear-gradient(135deg, #FF6B6B 0%, #EE5A6F 100%);
  color: white;
  box-shadow: 0 2px 4px rgba(255, 107, 107, 0.2);
}

.notice-title {
  font-weight: 800;
  font-size: 18px;
  color: #593527;
}

.notice-date {
  font-size: 13px;
  color: #593527;
  opacity: 0.5;
  white-space: nowrap;
}

.notice-content {
  color: #593527;
  opacity: 0.8;
  line-height: 1.7;
  white-space: pre-wrap;
  font-size: 15px;
  font-weight: 500;
}
</style>
