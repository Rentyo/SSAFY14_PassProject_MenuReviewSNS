<template>
  <div class="notice-create-container">
    <!-- Header -->
    <header class="hosu-header">
      <div class="hosu-logo" @click="$router.push('/mypage/ceo')">
        <span class="logo-main">HOSU</span>
        <span class="logo-sub">taste log</span>
      </div>
    </header>

    <div class="notice-create-box">
      <h2>공지사항 작성</h2>
      
      <form @submit.prevent="handleSubmit" class="notice-form">
        <!-- 중요도 선택 -->
        <div class="form-group">
          <label>중요도 <span class="required">*</span></label>
          <select v-model="form.importance" class="form-select" required>
            <option value="">중요도를 선택하세요</option>
            <option :value="1">일반</option>
            <option :value="2">중요</option>
            <option :value="3">긴급</option>
          </select>
          <div class="importance-preview" v-if="form.importance">
            <span class="notice-badge" :class="getImportanceClass(form.importance)">
              {{ getImportanceBadge(form.importance) }}
            </span>
          </div>
        </div>

        <!-- 제목 입력 -->
        <div class="form-group">
          <label>제목 <span class="required">*</span></label>
          <input
            type="text"
            v-model="form.title"
            placeholder="공지사항 제목을 입력하세요"
            class="form-input"
            required
            maxlength="100"
          />
        </div>

        <!-- 내용 입력 -->
        <div class="form-group">
          <label>내용 <span class="required">*</span></label>
          <textarea
            v-model="form.content"
            placeholder="공지사항 내용을 입력하세요"
            class="form-textarea"
            rows="10"
            required
          ></textarea>
        </div>

        <!-- 에러 메시지 -->
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <!-- 버튼 그룹 -->
        <div class="button-group">
          <button type="button" @click="goBack" class="btn-cancel">
            취소
          </button>
          <button type="submit" :disabled="submitting" class="btn-submit">
            {{ submitting ? '작성 중...' : '공지사항 등록' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getRestaurantId, insertNotice } from '@/api/restaurants'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  importance: '',
  title: '',
  content: ''
})

const submitting = ref(false)
const errorMessage = ref('')
const restaurantId = ref(null)

// 중요도에 따른 배지 텍스트
const getImportanceBadge = (importance) => {
  switch(importance) {
    case 3: return '긴급'
    case 2: return '중요'
    case 1: return '일반'
    default: return ''
  }
}

// 중요도에 따른 CSS 클래스
const getImportanceClass = (importance) => {
  switch(importance) {
    case 3: return 'urgent'
    case 2: return 'important'
    case 1: return 'normal'
    default: return ''
  }
}

// 식당 ID 가져오기
const fetchRestaurantId = async () => {
  try {
    const response = await getRestaurantId(userStore.userNo)
    restaurantId.value = response.data
    
    if (!restaurantId.value) {
      alert('등록된 식당이 없습니다.')
      router.push('/mypage/ceo')
    }
  } catch (error) {
    console.error('식당 ID 조회 실패:', error)
    alert('식당 정보를 불러오는데 실패했습니다.')
    router.push('/mypage/ceo')
  }
}

// 뒤로 가기
const goBack = () => {
  if (confirm('작성 중인 내용이 사라집니다. 취소하시겠습니까?')) {
    router.push('/mypage/ceo')
  }
}

// 공지사항 제출
const handleSubmit = async () => {
  if (!form.value.title.trim() || !form.value.content.trim() || !form.value.importance) {
    errorMessage.value = '모든 필수 항목을 입력해주세요.'
    return
  }

  if (!restaurantId.value) {
    errorMessage.value = '식당 정보를 찾을 수 없습니다.'
    return
  }

  errorMessage.value = ''
  submitting.value = true

  try {
    const noticeData = {
      restaurantId: restaurantId.value,
      title: form.value.title.trim(),
      importance: parseInt(form.value.importance),
      content: form.value.content.trim()
    }

    await insertNotice(noticeData)
    
    alert('공지사항이 등록되었습니다!')
    router.push('/mypage/ceo')
  } catch (error) {
    console.error('공지사항 등록 실패:', error)
    errorMessage.value = '공지사항 등록 중 오류가 발생했습니다.'
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (!userStore.isLoggedIn || userStore.role !== 'CEO') {
    alert('CEO 권한이 필요합니다.')
    router.push('/login')
    return
  }
  fetchRestaurantId()
})
</script>

<style scoped>
.notice-create-container {
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

.notice-create-box {
  width: 90%;
  max-width: 800px;
  margin: 40px auto 80px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.15);
  border: 1px solid #FFE4CC;
  padding: 40px;
}

.notice-create-box h2 {
  margin: 0 0 30px 0;
  font-size: 28px;
  color: #2D3436;
  text-align: center;
}

.notice-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #555;
  margin-bottom: 8px;
}

.required {
  color: #f44336;
}

.form-select,
.form-input,
.form-textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  box-sizing: border-box;
  transition: all 0.3s;
}

.form-select:focus,
.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #FF6B6B;
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 200px;
  line-height: 1.6;
}

.importance-preview {
  margin-top: 12px;
}

.notice-badge {
  display: inline-block;
  padding: 6px 16px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: bold;
}

.notice-badge.normal {
  background: #e0e0e0;
  color: #666;
}

.notice-badge.important {
  background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
  color: #2D3436;
}

.notice-badge.urgent {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  color: #2D3436;
}

.error-message {
  padding: 12px;
  background-color: #ffebee;
  color: #c62828;
  border-radius: 8px;
  font-size: 14px;
  text-align: center;
}

.button-group {
  display: flex;
  gap: 12px;
  margin-top: 20px;
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

.btn-cancel {
  background: #f5f5f5;
  color: #333;
}

.btn-cancel:hover {
  background: #e0e0e0;
}

.btn-submit {
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  color: #FFFFFF;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}

.btn-submit:disabled {
  background: #ccc;
  cursor: not-allowed;
  box-shadow: none;
}

@media (max-width: 768px) {
  .notice-create-box {
    width: 95%;
    padding: 24px;
  }

  .notice-create-box h2 {
    font-size: 24px;
  }
}
</style>
