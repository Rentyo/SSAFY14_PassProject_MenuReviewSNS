<template>
  <div class="notice-edit-container">
    <!-- Header -->
    <header class="hosu-header">
      <div class="hosu-logo" @click="$router.push('/mypage/ceo')">
        <span class="logo-main">HOSU</span>
        <span class="logo-sub">taste log</span>
      </div>
    </header>

    <div class="notice-edit-box">
      <h2>공지사항 수정</h2>
      
      <!-- 로딩 중 -->
      <div v-if="loading" class="loading">
        <p>공지사항을 불러오는 중...</p>
      </div>

      <form v-else @submit.prevent="handleSubmit" class="notice-form">
        <!-- 중요도 선택 (카드 스타일) -->
        <div class="form-group">
          <label>중요도 <span class="required">*</span></label>
          <div class="importance-cards">
            <label 
              v-for="level in [
                { val: 1, label: '일반', class: 'normal' },
                { val: 2, label: '중요', class: 'important' },
                { val: 3, label: '긴급', class: 'urgent' }
              ]" 
              :key="level.val"
              :class="['importance-card', level.class, { active: form.importance === level.val }]"
            >
              <input 
                type="radio" 
                v-model="form.importance" 
                :value="level.val" 
                class="hidden-radio"
              />
              <span class="card-label">{{ level.label }}</span>
              <div class="active-check" v-if="form.importance === level.val">✓</div>
            </label>
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
          <button type="button" @click="handleDelete" :disabled="submitting" class="btn-delete">
            {{ submitting ? '삭제 중...' : '삭제' }}
          </button>
          <button type="submit" :disabled="submitting" class="btn-submit">
            {{ submitting ? '수정 중...' : '공지사항 수정' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getRestaurantNotices, updateNotice, getRestaurantId, deleteNotice } from '@/api/restaurants'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const form = ref({
  restaurantNoticeId: null,
  importance: '',
  title: '',
  content: ''
})

const loading = ref(false)
const submitting = ref(false)
const errorMessage = ref('')

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

// 공지사항 데이터 가져오기
const fetchNotice = async () => {
  loading.value = true
  try {
    // 1. userNo로 restaurantId 가져오기
    const restaurantResponse = await getRestaurantId(userStore.userNo)
    const restaurantId = restaurantResponse.data
    
    if (!restaurantId) {
      alert('등록된 식당이 없습니다.')
      router.push('/mypage/ceo')
      return
    }
    
    // 2. 해당 식당의 공지사항 목록 가져오기
    const noticesResponse = await getRestaurantNotices(restaurantId)
    const notices = noticesResponse.data || []
    
    // 3. URL의 ID와 일치하는 공지사항 찾기
    const noticeId = parseInt(route.params.id)
    const notice = notices.find(n => n.restaurantNoticeId === noticeId)
    
    if (!notice) {
      alert('공지사항을 찾을 수 없습니다.')
      router.push('/mypage/ceo')
      return
    }
    
    // 4. 폼에 데이터 채우기
    form.value = {
      restaurantNoticeId: notice.restaurantNoticeId,
      importance: notice.importance,
      title: notice.title,
      content: notice.content
    }
  } catch (error) {
    console.error('공지사항 조회 실패:', error)
    alert('공지사항을 불러오는데 실패했습니다.')
    router.push('/mypage/ceo')
  } finally {
    loading.value = false
  }
}

// 뒤로 가기
const goBack = () => {
  if (confirm('수정 중인 내용이 사라집니다. 취소하시겠습니까?')) {
    router.push('/mypage/ceo')
  }
}

// 공지사항 수정 제출
const handleSubmit = async () => {
  if (!form.value.title.trim() || !form.value.content.trim() || !form.value.importance) {
    errorMessage.value = '모든 필수 항목을 입력해주세요.'
    return
  }

  errorMessage.value = ''
  submitting.value = true

  try {
    const noticeData = {
      restaurantNoticeId: form.value.restaurantNoticeId,
      title: form.value.title.trim(),
      importance: parseInt(form.value.importance),
      content: form.value.content.trim()
    }

    await updateNotice(noticeData)
    
    alert('공지사항이 수정되었습니다!')
    router.push('/mypage/ceo')
  } catch (error) {
    console.error('공지사항 수정 실패:', error)
    errorMessage.value = '공지사항 수정 중 오류가 발생했습니다.'
  } finally {
    submitting.value = false
  }
}

// 공지사항 삭제
const handleDelete = async () => {
  if (!confirm('정말 이 공지사항을 삭제하시겠습니까?')) {
    return
  }

  submitting.value = true
  errorMessage.value = ''

  try {
    await deleteNotice(form.value.restaurantNoticeId)
    
    alert('공지사항이 삭제되었습니다!')
    router.push('/mypage/ceo')
  } catch (error) {
    console.error('공지사항 삭제 실패:', error)
    errorMessage.value = '공지사항 삭제 중 오류가 발생했습니다.'
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
  fetchNotice()
})
</script>

<style scoped>
.notice-edit-container {
  /* Custom Requested Palette */
  --color-bg: #F2F2F2;
  --color-emphasis: #593527;
  --color-main: #F29F05;
  --color-sub-1: #F2DCB3;
  --color-sub-2: #D97904;

  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  background: var(--color-bg);
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
  border-bottom: 2px solid var(--color-sub-1);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(89, 53, 39, 0.08);
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
  color: var(--color-main);
  letter-spacing: -0.5px;
}

.logo-sub {
  font-size: 0.7rem;
  color: var(--color-emphasis);
  font-weight: 600;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.notice-edit-box {
  width: 90%;
  max-width: 800px;
  margin: 40px auto 80px;
  background: white;
  border-radius: 24px;
  box-shadow: 0 10px 30px rgba(89, 53, 39, 0.08);
  border: 1px solid var(--color-sub-1);
  padding: 40px;
}

.notice-edit-box h2 {
  margin: 0 0 30px 0;
  font-size: 32px;
  font-weight: 800;
  color: var(--color-emphasis);
  text-align: center;
  letter-spacing: -1px;
}

.loading {
  text-align: center;
  padding: 60px 20px;
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
  font-weight: 700;
  color: var(--color-emphasis);
  margin-bottom: 10px;
}

.required {
  color: #f44336;
}

.form-select,
.form-input,
.form-textarea {
  width: 100%;
  padding: 14px 18px;
  background: #F8F9FA;
  border: 2px solid transparent;
  border-radius: 12px;
  font-size: 15px;
  font-family: inherit;
  box-sizing: border-box;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: var(--color-emphasis);
}

.form-select:focus,
.form-input:focus,
.form-textarea:focus {
  outline: none;
  background: white;
  border-color: var(--color-main);
  box-shadow: 0 0 0 4px rgba(242, 159, 5, 0.1);
}

.form-textarea {
  resize: none;
  min-height: 240px;
  line-height: 1.6;
}

/* 중요도 카드 스타일 */
.importance-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-top: 4px;
}

.importance-card {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: #F8F9FA;
  border: 2.5px solid transparent;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none;
}

.importance-card:hover {
  transform: translateY(-2px);
  background: #FFFFFF;
  border-color: var(--color-sub-1);
}

.card-label {
  font-size: 15px;
  font-weight: 700;
  color: #666;
}

.active-check {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 24px;
  height: 24px;
  background: var(--color-main);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 800;
  box-shadow: 0 4px 8px rgba(242, 159, 5, 0.3);
  z-index: 2;
}

.hidden-radio {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
}

/* 각 등급별 활성화 스타일 */
.importance-card.normal.active {
  background: #F1F3F5;
  border-color: #ADB5BD;
}
.importance-card.normal.active .card-label { color: #495057; }
.importance-card.normal.active .active-check { background: #ADB5BD; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }

.importance-card.important.active {
  background: #E7F5FF;
  border-color: #339AF0;
}
.importance-card.important.active .card-label { color: #1C7ED6; }
.importance-card.important.active .active-check { background: #339AF0; }

.importance-card.urgent.active {
  background: #FFF5F5;
  border-color: #FF6B6B;
}
.importance-card.urgent.active .card-label { color: #FF6B6B; }
.importance-card.urgent.active .active-check { background: #FF6B6B; }

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
  background: var(--color-sub-1);
  color: var(--color-emphasis);
  font-weight: 700;
}

.btn-cancel:hover {
  background: #E8CC9F;
}

.btn-submit {
  background: var(--color-main);
  color: #FFFFFF;
  font-weight: 800;
  box-shadow: 0 4px 15px rgba(242, 159, 5, 0.3);
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  background: var(--color-sub-2);
  box-shadow: 0 6px 20px rgba(217, 121, 4, 0.4);
}

.btn-submit:disabled {
  background: #ccc;
  cursor: not-allowed;
  box-shadow: none;
}

.btn-delete {
  background: #F1F3F5;
  color: #95A5A6;
  font-weight: 700;
  border: 1.5px solid #E0E0E0;
}

.btn-delete:hover:not(:disabled) {
  background: #FFF5F5;
  color: #f44336;
  border-color: #f44336;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(244, 67, 54, 0.15);
}

.btn-delete:disabled {
  background: #ccc;
  cursor: not-allowed;
  box-shadow: none;
}

@media (max-width: 768px) {
  .notice-edit-box {
    width: 95%;
    padding: 24px;
  }

  .notice-edit-box h2 {
    font-size: 24px;
  }
}
</style>
