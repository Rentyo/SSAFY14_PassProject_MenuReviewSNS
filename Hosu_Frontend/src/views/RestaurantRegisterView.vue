<template>
  <div class="restaurant-register-container">
    <!-- Header -->
    <header class="hosu-header">
      <div class="hosu-logo" @click="goBack">
        <span class="logo-main">HOSU</span>
        <span class="logo-sub">taste log</span>
      </div>
    </header>

    <div class="register-content">
      <div class="register-card">
        <div class="header-badge">
          <span class="role-badge">CEO</span>
          <h2>식당 개시 요청</h2>
          <p class="subtitle">새로운 맛집을 공유해주세요</p>
        </div>

        <form @submit.prevent="submitRestaurant" class="register-form">
          <!-- 카테고리 -->
          <div class="form-group">
            <label>카테고리</label>
            <select v-model="form.category" required class="form-select">
              <template v-for="cat in categories" :key="cat.value">
                <option :value="cat.value">{{ cat.label }}</option>
              </template>
            </select>
          </div>

          <!-- 주소 검색 -->
          <div class="form-group">
            <label>주소</label>
            <div class="address-input-group">
              <input 
                type="text" 
                v-model="form.address" 
                placeholder="주소 검색 버튼을 눌러주세요"
                readonly
                required
                class="form-input address-input"
              />
              <button type="button" @click="openAddressSearch" class="btn-search">
                주소 검색
              </button>
            </div>
          </div>

          <!-- 식당 이름 -->
          <div class="form-group">
            <label>식당 이름</label>
            <input 
              type="text" 
              v-model="form.restaurantName" 
              placeholder="식당 이름을 입력하세요"
              required
              class="form-input"
            />
          </div>

          <!-- 가게 설명 -->
          <div class="form-group">
            <label>가게 설명</label>
            <textarea 
              v-model="form.description" 
              placeholder="가게에 대한 설명을 입력해주세요."
              rows="4"
              class="form-textarea"
            ></textarea>
          </div>

          <!-- 에러 메시지 (CeoEditView 스타일) -->
          <div v-if="errorMessage" class="error">
            {{ errorMessage }}
          </div>

          <!-- 버튼 그룹 -->
          <div class="button-group">
            <button type="submit" class="btn-primary" :disabled="loading">
              {{ loading ? '처리 중...' : '식당 개시 요청' }}
            </button>
            <button type="button" @click="goBack" class="btn-secondary">취소</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { openRestaurantReq } from '@/api/restaurants'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const errorMessage = ref('')

// 카테고리 목록
const categories = [
  { value: 'KOREAN', label: '한식 (KOREAN)' },
  { value: 'CHINESE', label: '중식 (CHINESE)' },
  { value: 'JAPANESE', label: '일식 (JAPANESE)' },
  { value: 'WESTERN', label: '양식 (WESTERN)' },
  { value: 'SNACK', label: '분식 (SNACK)' },
  { value: 'CHICKEN', label: '치킨 (CHICKEN)' },
  { value: 'MEAT', label: '고기 (MEAT)' },
  { value: 'SEAFOOD', label: '해산물 (SEAFOOD)' },
  { value: 'CAFE', label: '카페 (CAFE)' },
  { value: 'PUB', label: '주점 (PUB)' },
  { value: 'BAKERY', label: '베이커리 (BAKERY)' },
  { value: 'FASTFOOD', label: '패스트푸드 (FASTFOOD)' },
  { value: 'ASIAN', label: '아시안 (ASIAN)' },
  { value: 'CURRY', label: '카레 (CURRY)' },
]

const form = ref({
  restaurantName: '', 
  category: 'KOREAN', // 기본값
  address: '',
  description: '' 
})

const goBack = () => {
  router.push('/mypage/ceo')
}

// 주소 검색 (Daum Postcode API)
const openAddressSearch = () => {
  new daum.Postcode({
    oncomplete: function(data) {
      form.value.address = data.address; // 기본 주소
    }
  }).open();
}

// 등록 제출
const submitRestaurant = async () => {
  errorMessage.value = '';
  
  if (!form.value.address) {
    errorMessage.value = "주소를 입력해주세요.";
    return;
  }

  loading.value = true;
  try {
    const fullAddress = form.value.address + " " + form.value.restaurantName;
    
    const payload = {
      address: fullAddress,
      category: form.value.category,
      description: form.value.description || '',
      userNo: localStorage.getItem('userNo') || userStore.userNo
    };
    
    if (payload.description) {
        payload.description = `[${form.value.restaurantName}] ${payload.description}`;
    } else {
        payload.description = `식당 이름: ${form.value.restaurantName}`;
    }

    await openRestaurantReq(payload);
    
    alert("식당 개시 요청이 완료되었습니다.");
    router.push('/mypage/ceo');
    
  } catch (error) {
    console.error("식당 등록 요청 실패:", error);
    errorMessage.value = "요청에 실패했습니다.";
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.restaurant-register-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  background: #FFF8F0;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

/* Header */
.hosu-header {
  height: 80px;
  min-height: 80px;
  flex-shrink: 0;
  padding: 0 2rem;
  background: #FFFFFF;
  border-bottom: 1px solid #FFE4CC;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 12px rgba(255, 107, 107, 0.08);
}

.hosu-logo {
  cursor: pointer;
  display: flex;
  flex-direction: column;
  line-height: 1.2;
  transition: transform 0.3s ease;
}

.hosu-logo:hover {
  transform: translateY(-2px);
}

.logo-main {
  font-weight: 800;
  font-size: 1.75rem;
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.5px;
}

.logo-sub {
  font-size: 0.7rem;
  color: #95A5A6;
  font-weight: 500;
  letter-spacing: 2px;
  text-transform: uppercase;
}

/* Content */
.register-content {
  display: flex;
  justify-content: center;
  align-items: center;
  flex: 1;
  padding: 2rem 1rem;
}

.register-card {
  background: white;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.15);
  border: 1px solid #FFE4CC;
  width: 100%;
  max-width: 600px;
}

.header-badge {
  text-align: center;
  margin-bottom: 30px;
}

.role-badge {
  display: inline-block;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  color: #FFFFFF;
  margin-bottom: 10px;
}

h2 {
  margin: 10px 0 5px 0;
  color: #1f2937;
  font-size: 1.75rem;
  font-weight: 700;
}

.subtitle {
  color: #9ca3af;
  font-size: 14px;
}

/* Form Styles */
.register-form {
  display: flex;
  flex-direction: column;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #374151;
  font-weight: 600;
  font-size: 14px;
}

.form-input, .form-select, .form-textarea {
  width: 100%;
  padding: 12px 14px;
  border: 1.5px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  box-sizing: border-box;
  transition: all 0.2s ease;
  background: #fff;
  font-family: inherit;
}

.form-input:focus, .form-select:focus, .form-textarea:focus {
  outline: none;
  border-color: #FF6B6B;
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
}

.form-input:disabled {
  background-color: #f9fafb;
  color: #9ca3af;
  cursor: not-allowed;
}

.address-input-group {
  display: flex;
  gap: 10px;
}

.address-input {
  flex: 1;
  background-color: #f9fafb;
  cursor: pointer;
}

.btn-search {
  padding: 0 20px;
  background-color: #FFF5E6;
  color: #2D3436;
  border: 1px solid #FFE4CC;
  border-radius: 8px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.btn-search:hover {
  border-color: #FF6B6B;
  background-color: #FFFFFF;
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
}

.error {
  padding: 12px 16px;
  margin-bottom: 15px;
  background-color: #fef2f2;
  color: #dc2626;
  border-radius: 8px;
  border-left: 4px solid #dc2626;
  font-size: 14px;
  font-weight: 500;
}

/* Button Group */
.button-group {
  display: flex;
  gap: 10px;
  margin-top: 30px;
}

.button-group button {
  flex: 1;
  padding: 14px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  color: #FFFFFF;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}

.btn-primary:disabled {
  background: #d1d5db;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  background-color: #FFF5E6;
  color: #2D3436;
  border: 1px solid #FFE4CC;
}

.btn-secondary:hover {
  background-color: #FFFFFF;
  border-color: #FF6B6B;
}
</style>
