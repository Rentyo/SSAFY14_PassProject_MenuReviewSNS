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
  background: rgba(89, 53, 39, 0.05);
  border-radius: 4px;
}

.reviews-container::-webkit-scrollbar-thumb {
  background: #F2DCB3;
  border-radius: 4px;
  transition: background 0.3s ease;
}

.reviews-container::-webkit-scrollbar-thumb:hover {
  background: #F29F05;
}

.loading, .no-reviews {
  text-align: center;
  padding: 60px 0;
  color: #593527;
  opacity: 0.5;
}

.reviews-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  width: 100%;
  margin: 0 auto;
}

.review-card {
  cursor: pointer;
  position: relative;
  padding-top: 100%;
  overflow: hidden;
  background: #FFFFFF;
  border-radius: 16px;
  border: 1px solid #F2DCB3;
  transition: all 0.3s ease;
}

.review-card:hover {
  transform: translateY(-4px);
  border-color: #F29F05;
  box-shadow: 0 8px 20px rgba(89, 53, 39, 0.1);
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
  transition: transform 0.3s ease;
}

.review-card:hover .review-image img {
  transform: scale(1.08);
}

@media (max-width: 768px) {
  .reviews-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 10px;
  }
}

@media (max-width: 480px) {
  .reviews-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 8px;
  }
}
</style>
