<template>
  <div class="info-container">
    <!-- 지도 섹션 -->
    <div class="info-group">
      <h3>지도</h3>
      <div class="map-container">
        <KakaoMap 
          v-if="restaurant.lat && restaurant.lng"
          :lat="restaurant.lat" 
          :lng="restaurant.lng" 
          :draggable="true"
          style="width: 100%; height: 300px;"
        >
          <KakaoMapMarker 
            :lat="restaurant.lat" 
            :lng="restaurant.lng"
          />
        </KakaoMap>
        <div v-else class="empty-text">지도 정보 없음</div>
      </div>
    </div>

    <div class="info-group">
      <h3>위치</h3>
      <p>{{ restaurant.address || '주소 정보 없음' }}</p>
    </div>
    
    <div class="info-group">
      <h3>연락처</h3>
      <p>{{ restaurant.phone || '연락처 정보 없음' }}</p>
    </div>
    
    <div class="info-group">
      <h3>카테고리</h3>
      <p>{{ restaurant.category || '카테고리 정보 없음' }}</p>
    </div>
  </div>
</template>

<script setup>
import { KakaoMap, KakaoMapMarker } from 'vue3-kakao-maps'

const props = defineProps({
  restaurant: {
    type: Object,
    required: true,
    default: () => ({})
  },
  restaurantId: {
    type: [Number, String],
    required: true
  }
})
</script>

<style scoped>
.info-container {
  padding: 0;
  background: transparent;
  color: #2D3436;
  max-height: 600px;
  overflow-y: auto;
}

/* Custom Scrollbar */
.info-container::-webkit-scrollbar {
  width: 8px;
}

.info-container::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
}

.info-container::-webkit-scrollbar-thumb {
  background: #FFE4CC;
  border-radius: 4px;
  transition: background 0.3s ease;
}

.info-container::-webkit-scrollbar-thumb:hover {
  background: #FF6B6B;
}

.info-group {
  margin-bottom: 32px;
  padding: 20px;
  background: #FFF5E6;
  border-radius: 12px;
  border: 1px solid #FFE4CC;
  transition: all 0.3s ease;
}

.info-group:hover {
  background: #FFFFFF;
  border-color: #FF6B6B;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.15);
}

.info-group h3 {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 16px;
  color: #2D3436;
  padding-bottom: 10px;
  border-bottom: 2px solid #FFE4CC;
}

.info-group p {
  color: #636E72;
  line-height: 1.7;
  font-size: 15px;
}


.map-container {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e0e0e0;
  height: 300px; /* 고정 높이 추가 */
}

/* KakaoMap 컴포넌트 내부 스타일 조정이 필요한 경우 */
</style>
