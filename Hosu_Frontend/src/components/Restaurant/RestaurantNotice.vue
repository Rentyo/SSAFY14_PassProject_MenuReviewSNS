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
          <span class="notice-title">{{ notice.title }}</span>
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
  return new Date(dateString).toLocaleDateString()
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
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
}

.notices-container::-webkit-scrollbar-thumb {
  background: #FFE4CC;
  border-radius: 4px;
  transition: background 0.3s ease;
}

.notices-container::-webkit-scrollbar-thumb:hover {
  background: #FF6B6B;
}

.loading, .no-notices {
  text-align: center;
  padding: 60px 0;
  color: #95A5A6;
  font-size: 15px;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.notice-item {
  border: 1px solid #FFE4CC;
  border-radius: 12px;
  padding: 20px;
  background: #FFF5E6;
  transition: all 0.3s ease;
}

.notice-item:hover {
  background: #FFFFFF;
  border-color: #FF6B6B;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.15);
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  border-bottom: 1px solid #FFE4CC;
  padding-bottom: 12px;
}

.notice-title {
  font-weight: 700;
  font-size: 18px;
  color: #2D3436;
}

.notice-date {
  font-size: 13px;
  color: #95A5A6;
}

.notice-content {
  color: #636E72;
  line-height: 1.6;
  white-space: pre-wrap;
  font-size: 15px;
}
</style>
