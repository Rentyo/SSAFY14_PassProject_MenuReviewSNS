<template>
  <div class="restaurant-reviews-section">
    <div class="reviews-header">
      <h3>우리 식당 리뷰</h3>
      <span class="review-count">{{ reviews.length }}개</span>
    </div>

    <!-- 로딩 중 -->
    <div v-if="loading" class="loading">
      <p>리뷰를 불러오는 중...</p>
    </div>

    <!-- 리뷰가 없을 때 -->
    <div v-else-if="reviews.length === 0" class="no-reviews">
      <p>아직 작성된 리뷰가 없습니다.</p>
    </div>

    <!-- 리뷰 그리드 (이미지만 표시) -->
    <div v-else class="reviews-grid">
      <div
        v-for="review in reviews"
        :key="review.boardId"
        class="review-card"
        @click="goToReview(review.boardId)"
      >
        <!-- 대문 이미지만 표시 -->
        <div class="review-image">
          <img :src="review.boardImg" :alt="'리뷰 이미지'" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getRestaurantId, getRestaurantReviews } from '@/api/restaurants'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const reviews = ref([])
const loading = ref(false)
const restaurantId = ref(null)

// 식당 ID와 리뷰 데이터 가져오기
const fetchReviews = async () => {
  loading.value = true
  try {
    // 1. 먼저 userNo로 restaurantId 가져오기
    const restaurantResponse = await getRestaurantId(userStore.userNo)
    restaurantId.value = restaurantResponse.data
    
    if (!restaurantId.value) {
      console.log('등록된 식당이 없습니다.')
      return
    }
    
    // 2. restaurantId로 리뷰 가져오기
    const reviewResponse = await getRestaurantReviews(restaurantId.value)
    reviews.value = reviewResponse.data
  } catch (error) {
    console.error('리뷰 조회 실패:', error)
  } finally {
    loading.value = false
  }
}

// 리뷰 상세 페이지로 이동
const goToReview = (reviewId) => {
  router.push(`/review/${reviewId}`)
}

onMounted(() => {
  fetchReviews()
})
</script>

<style scoped>
.restaurant-reviews-section {
  width: 100%;
}

.reviews-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 2px solid rgba(255, 255, 255, 0.3);
}

.reviews-header h3 {
  font-size: 24px;
  color: #2D3436;
  margin: 0;
}

.review-count {
  font-size: 16px;
  color: #2D3436;
  font-weight: bold;
  background: #FFF5E6;
  border: 1px solid #FFE4CC;
  padding: 6px 16px;
  border-radius: 20px;
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

.no-reviews {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.no-reviews p {
  font-size: 18px;
  color: #999;
  margin: 0;
}

.reviews-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.review-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.review-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.review-image {
  position: relative;
  width: 100%;
  padding-top: 100%; /* 정사각형 비율 */
  overflow: hidden;
  background: #f5f5f5;
}

.review-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.review-card:hover .review-image img {
  transform: scale(1.05);
}

/* 반응형 */
@media (max-width: 1024px) {
  .reviews-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .reviews-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }
}

@media (max-width: 480px) {
  .reviews-grid {
    grid-template-columns: 1fr;
  }
}
</style>
