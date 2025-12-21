<template>
  <div class="notices-section">
    <div class="notices-header">
      <h3>공지사항</h3>
      <button class="btn-add-notice" @click="$router.push('/notice/create')">
        <img src="@/assets/more.png" alt="공지 작성" class="add-icon" />
      </button>
    </div>

    <!-- 로딩 중 -->
    <div v-if="loading" class="loading">
      <p>공지사항을 불러오는 중...</p>
    </div>

    <!-- 공지사항이 없을 때 -->
    <div v-else-if="notices.length === 0" class="no-notices">
      <p>등록된 공지사항이 없습니다.</p>
      <button class="btn-create-notice" @click="$router.push('/notice/create')">첫 공지사항 작성하기</button>
    </div>

    <!-- 공지사항 리스트 -->
    <div v-else class="notices-list">
      <div
        v-for="notice in notices"
        :key="notice.restaurantNoticeId"
        class="notice-card"
        @click="viewNotice(notice.restaurantNoticeId)"
      >
        <div class="notice-header">
          <span class="notice-badge" :class="getImportanceClass(notice.importance)">
            {{ getImportanceBadge(notice.importance) }}
          </span>
          <span class="notice-date">{{ formatDate(notice.createdAt) }}</span>
        </div>
        <h4 class="notice-title">{{ notice.title }}</h4>
        <p class="notice-preview">{{ notice.content }}</p>
        <div class="notice-footer">
          <span class="notice-views">조회 {{ notice.viewCount || 0 }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getRestaurantId, getRestaurantNotices } from '@/api/restaurants'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const notices = ref([])
const loading = ref(false)
const restaurantId = ref(null)

// 중요도에 따른 배지 텍스트
const getImportanceBadge = (importance) => {
  switch(importance) {
    case 3: return '긴급'
    case 2: return '중요'
    case 1:
    default: return '일반'
  }
}

// 중요도 확인 및 클래스 반환
const getImportanceClass = (importance) => {
  switch(importance) {
    case 3: return 'urgent'
    case 2: return 'important'
    case 1:
    default: return 'normal'
  }
}

// 식당 ID와 공지사항 데이터 가져오기
const fetchNotices = async () => {
  loading.value = true
  try {
    // 1. 먼저 userNo로 restaurantId 가져오기
    const restaurantResponse = await getRestaurantId(userStore.userNo)
    restaurantId.value = restaurantResponse.data
    
    if (!restaurantId.value) {
      console.log('등록된 식당이 없습니다.')
      return
    }
    
    // 2. restaurantId로 공지사항 가져오기
    const noticeResponse = await getRestaurantNotices(restaurantId.value)
    notices.value = noticeResponse.data || []
  } catch (error) {
    console.error('공지사항 조회 실패:', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

const viewNotice = (noticeId) => {
  router.push(`/notice/${noticeId}/edit`)
}

onMounted(() => {
  fetchNotices()
})
</script>

<style scoped>
.notices-section {
  width: 100%;
}

.notices-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 2px solid rgba(255, 255, 255, 0.3);
}

.notices-header h3 {
  font-size: 24px;
  color: var(--color-emphasis);
  margin: 0;
  font-weight: 800;
}

.btn-add-notice {
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
  filter: brightness(0.5);
}

.btn-add-notice:hover {
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

.no-notices {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.no-notices p {
  font-size: 18px;
  color: #999;
  margin-bottom: 20px;
}

.btn-create-notice {
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

.btn-create-notice:hover {
  transform: translateY(-2px);
  background: var(--color-sub-2);
  box-shadow: 0 6px 16px rgba(217, 121, 4, 0.3);
}

.notices-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.notice-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(89, 53, 39, 0.05);
  border: 1px solid var(--color-sub-1);
  cursor: pointer;
  transition: all 0.3s;
}

.notice-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(89, 53, 39, 0.12);
  border-color: var(--color-main);
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.notice-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 800;
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

.notice-date {
  font-size: 14px;
  color: #999;
}

.notice-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-emphasis);
  margin: 0 0 10px 0;
}

.notice-preview {
  font-size: 14px;
  color: #666;
  margin: 0 0 12px 0;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.notice-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.notice-views {
  font-size: 13px;
  color: #999;
}
</style>
