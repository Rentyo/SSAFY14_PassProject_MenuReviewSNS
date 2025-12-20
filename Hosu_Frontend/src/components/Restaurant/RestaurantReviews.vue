<template>
  <div class="reviews-container">
    <div v-if="loading" class="loading">
      <p>로딩 중...</p>
    </div>
    <div v-else-if="!displayReviews || displayReviews.length === 0" class="no-reviews">
      <p>아직 작성된 리뷰가 없습니다.</p>
    </div>
    <div v-else class="reviews-grid">
      <div
        v-for="review in displayReviews"
        :key="review.boardId"
        class="review-card"
        @click="goToReview(review.boardId)"
      >
        <div class="review-image">
          <img :src="review.boardImg || '/placeholders/no-image.png'" :alt="review.title || 'Review Item'" @error="handleImageError" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getRestaurantReviews } from '@/api/restaurants'

const props = defineProps({
  restaurantId: {
    type: [String, Number],
    required: false
  },
  reviews: {
    type: Array,
    required: false,
    default: () => []
  }
})

const router = useRouter()
const fetchedReviews = ref([])
const loading = ref(false)

// props.reviews가 있으면 사용, 없으면 API에서 가져온 데이터 사용
const displayReviews = computed(() => {
  if (props.reviews && props.reviews.length > 0) {
    return props.reviews
  }
  return fetchedReviews.value
})

const loadReviews = async () => {
  if (!props.restaurantId) return
  
  try {
    loading.value = true
    const response = await getRestaurantReviews(props.restaurantId)
    fetchedReviews.value = response.data
  } catch (error) {
    console.error('리뷰 로딩 실패:', error)
    fetchedReviews.value = []
  } finally {
    loading.value = false
  }
}

const goToReview = (reviewId) => {
  router.push(`/review/${reviewId}`)
}

onMounted(() => {
  if ((!props.reviews || props.reviews.length === 0) && props.restaurantId) {
    loadReviews()
  }
})

const handleImageError = (event) => {
  event.target.src = 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'
}
</script>

<style scoped>
.reviews-container {
  padding: 20px 0;
  max-height: 1000px;
  overflow-y: auto;
}

/* Custom Scrollbar */
.reviews-container::-webkit-scrollbar {
  width: 8px;
}

.reviews-container::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
}

.reviews-container::-webkit-scrollbar-thumb {
  background: rgba(96, 165, 250, 0.3);
  border-radius: 4px;
  transition: background 0.3s ease;
}

.reviews-container::-webkit-scrollbar-thumb:hover {
  background: rgba(96, 165, 250, 0.5);
}

.loading, .no-reviews {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.reviews-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* 4열 그리드 */
  gap: 16px; /* 간격 증가 */
  max-width: 90%; /* 카드 크기 축소 */
  margin: 0 auto; /* 중앙 정렬 */
}

.review-card {
  cursor: pointer;
  position: relative;
  padding-top: 100%; /* 1:1 비율 */
  overflow: hidden;
  background: #f0f0f0;
  border-radius: 12px;
}

.review-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.review-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.2s;
}

.review-card:hover .review-image img {
  transform: scale(1.02);
}

@media (max-width: 600px) {
  .reviews-grid {
    gap: 2px;
  }
}
</style>
