<template>
  <div class="signup-page">
    <!-- Animated Background -->
    <div class="background-gradient"></div>
    
    <!-- Header -->
    <header class="hosu-header">
      <div class="hosu-logo" @click="goHome">
        <span class="logo-main">HOSU</span>
        <span class="logo-sub">taste log</span>
      </div>
    </header>
    
    <div class="signup-container">
      <div class="signup-box">
        <div class="signup-header">
          <h2>Join Us</h2>
          <p class="subtitle">맛의 여정을 시작하세요</p>
        </div>

        <!-- 회원 유형 선택 탭 -->
        <div class="user-type-tabs">
          <button 
            type="button"
            :class="['tab', { active: userType === 'USER' }]"
            @click="userType = 'USER'"
          >
            <svg class="tab-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
            일반회원
          </button>
          <button 
            type="button"
            :class="['tab', { active: userType === 'CEO' }]"
            @click="userType = 'CEO'"
          >
            <svg class="tab-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
              <polyline points="9 22 9 12 15 12 15 22"></polyline>
            </svg>
            기업회원
          </button>
        </div>

        <form @submit.prevent="handleSignup">
          <!-- 이메일 입력 -->
          <div class="form-group">
            <div class="input-wrapper">
              <svg class="input-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path>
                <polyline points="22,6 12,13 2,6"></polyline>
              </svg>
              <input 
                v-model="email" 
                type="email" 
                placeholder="이메일을 입력하세요" 
                required 
                class="styled-input"
              />
            </div>
          </div>

          <!-- 비밀번호 입력 -->
          <div class="form-group">
            <div class="input-wrapper">
              <svg class="input-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
              </svg>
              <input 
                v-model="password" 
                type="password" 
                placeholder="비밀번호를 입력하세요" 
                required 
                class="styled-input"
              />
            </div>
          </div>

          <!-- 비밀번호 확인 -->
          <div class="form-group">
            <div class="input-wrapper">
              <svg class="input-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
              </svg>
              <input 
                v-model="passwordConfirm" 
                type="password" 
                placeholder="비밀번호를 다시 입력하세요" 
                required 
                class="styled-input"
              />
            </div>
          </div>

          <!-- 닉네임/업체명 입력 -->
          <div class="form-group">
            <div class="input-wrapper">
              <svg class="input-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
              <input 
                v-model="nickname" 
                type="text" 
                :placeholder="userType === 'CEO' ? '업체명을 입력하세요' : '닉네임을 입력하세요'" 
                required 
                class="styled-input"
              />
            </div>
          </div>


          <!-- 성별 선택 -->
          <div class="form-group">
            <label class="field-label">성별</label>
            <div class="gender-options">
              <label class="radio-label">
                <input type="radio" v-model="gender" value="MALE" required />
                <span class="radio-custom"></span>
                <svg class="radio-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="10" cy="14" r="7"></circle>
                  <line x1="14.5" y1="9.5" x2="21" y2="3"></line>
                  <polyline points="15 3 21 3 21 9"></polyline>
                </svg>
                <span class="radio-text">남성</span>
              </label>
              <label class="radio-label">
                <input type="radio" v-model="gender" value="FEMALE" required />
                <span class="radio-custom"></span>
                <svg class="radio-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="9" r="7"></circle>
                  <line x1="12" y1="16" x2="12" y2="22"></line>
                  <line x1="8" y1="19" x2="16" y2="19"></line>
                </svg>
                <span class="radio-text">여성</span>
              </label>
              <label class="radio-label">
                <input type="radio" v-model="gender" value="OTHER" required />
                <span class="radio-custom"></span>
                <svg class="radio-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="3"></circle>
                  <path d="M12 1v6m0 6v6m6-9h-6m-6 0h6"></path>
                </svg>
                <span class="radio-text">기타</span>
              </label>
            </div>
          </div>


          <!-- 전화번호 입력 -->
          <div class="form-group">
            <div class="input-wrapper">
              <svg class="input-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
              </svg>
              <input 
                v-model="phoneNumber" 
                type="tel" 
                placeholder="010-1234-5678" 
                pattern="[0-9]{3}-[0-9]{4}-[0-9]{4}"
                required 
                class="styled-input"
              />
            </div>
            <small class="input-hint">형식: 010-1234-5678</small>
          </div>

          <!-- 에러 메시지 -->
          <transition name="error-fade">
            <div v-if="errorMessage" class="error">
              <svg class="error-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="8" x2="12" y2="12"></line>
                <line x1="12" y1="16" x2="12.01" y2="16"></line>
              </svg>
              {{ errorMessage }}
            </div>
          </transition>

          <!-- 회원가입 버튼 -->
          <button type="submit" :disabled="loading" class="signup-button">
            <span v-if="!loading">회원가입</span>
            <span v-else class="loading-spinner">
              <svg class="spinner" viewBox="0 0 50 50">
                <circle cx="25" cy="25" r="20" fill="none" stroke-width="5"></circle>
              </svg>
              가입 중...
            </span>
          </button>
          
          <!-- 로그인 링크 -->
          <div class="login-link">
            <span>이미 계정이 있으신가요?</span>
            <a @click="goLogin">로그인</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { signup } from '@/api/user'

const router = useRouter()

const userType = ref('USER') // 'USER' 또는 'CEO'
const email = ref('')
const password = ref('')
const passwordConfirm = ref('')
const nickname = ref('')
const gender = ref('')
const phoneNumber = ref('')
const loading = ref(false)
const errorMessage = ref('')

const goHome = () => {
  router.push('/')
}

const goLogin = () => {
  router.push('/login')
}

const handleSignup = async () => {
  errorMessage.value = ''
  
  // 비밀번호 확인
  if (password.value !== passwordConfirm.value) {
    errorMessage.value = '비밀번호가 일치하지 않습니다.'
    return
  }
  
  // 비밀번호 길이 확인
  if (password.value.length < 6) {
    errorMessage.value = '비밀번호는 최소 6자 이상이어야 합니다.'
    return
  }

  // 전화번호 형식 확인
  const phonePattern = /^[0-9]{3}-[0-9]{4}-[0-9]{4}$/
  if (!phonePattern.test(phoneNumber.value)) {
    errorMessage.value = '전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)'
    return
  }
  
  loading.value = true
  
  try {
    // 기본 프로필 이미지 경로
    const defaultProfileImage = new URL('@/assets/user.png', import.meta.url).href
    
    const signupData = {
      userEmail: email.value,
      userPw: password.value,
      nickname: nickname.value,
      gender: gender.value,
      role: userType.value,
      phoneNumber: phoneNumber.value,
      userImg: defaultProfileImage  // 기본 프로필 이미지 설정
    }
    
    const response = await signup(signupData)
    
    // 성공 시 로그인 페이지로 이동
    alert(`${userType.value === 'CEO' ? '기업' : '일반'}회원 가입이 완료되었습니다!`)
    router.push('/login')
  } catch (error) {
    console.error('회원가입 에러:', error)
    
    // 에러 메시지 처리
    if (error.response) {
      const status = error.response.status
      const message = error.response.data?.message || error.response.data
      
      if (status === 400) {
        // 중복 에러 처리
        if (message.includes('이메일')) {
          errorMessage.value = '이미 사용 중인 이메일입니다.'
        } else if (message.includes('닉네임')) {
          errorMessage.value = '이미 사용 중인 닉네임입니다.'
        } else if (message.includes('전화번호')) {
          errorMessage.value = '이미 사용 중인 전화번호입니다.'
        } else {
          errorMessage.value = message || '입력 정보를 확인해주세요.'
        }
      } else {
        errorMessage.value = '회원가입에 실패했습니다. 다시 시도해주세요.'
      }
    } else if (error.request) {
      errorMessage.value = '서버에 연결할 수 없습니다. 백엔드 서버가 실행 중인지 확인해주세요.'
    } else {
      errorMessage.value = '회원가입 중 오류가 발생했습니다.'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

* {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.signup-page {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  background: #0f172a;
}

/* Animated Gradient Background */
.background-gradient {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  background: linear-gradient(
    135deg,
    #FFF8F0 0%,
    #FFF8F0 25%,
    #FFF8F0 50%,
    #FFF8F0 75%,
    #FFF8F0 100%
  );
  
  
}

@keyframes gradientShift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

/* Header */
.hosu-header {
  position: relative;
  z-index: 10;
  height: 80px;
  min-height: 80px;
  flex-shrink: 0;
  padding: 0 2rem;
  background: #FFF5E6;
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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

.signup-container {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  flex: 1;
  padding: 2rem 1rem;
}

/* Glassmorphism Signup Box */
.signup-box {
  background: #FFFFFF;  padding: 2.5rem 2.5rem;
  border-radius: 24px;
  border: 1px solid #FFE4CC;
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.15),
    inset 0 1px 0 0 rgba(255, 255, 255, 0.1);
  width: 100%;
  max-width: 500px;
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.signup-header {
  text-align: center;
  margin-bottom: 2rem;
}

h2 {
  font-size: 2rem;
  font-weight: 700;
  color: #2D3436;
  margin: 0 0 0.5rem 0;
  letter-spacing: -0.5px;
}

.subtitle {
  font-size: 0.95rem;
  color: #95A5A6;
  margin: 0;
  font-weight: 400;
}

/* User Type Tabs */
.user-type-tabs {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 2rem;
  background: #FFF5E6;
  padding: 0.375rem;
  border-radius: 14px;
  border: 1px solid #FFE4CC;
}

.user-type-tabs .tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.875rem 1rem;
  background: transparent;
  border: none;
  border-radius: 10px;
  font-size: 0.9rem;
  font-weight: 600;
  color: #95A5A6;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-icon {
  width: 18px;
  height: 18px;
  stroke: currentColor;
}

.user-type-tabs .tab:hover {
  color: #636E72;
  background: #FFF5E6;
}

.user-type-tabs .tab.active {
  background: rgba(255, 107, 107, 0.15);
  color: #FF6B6B;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.15);
}

.form-group {
  margin-bottom: 1.25rem;
}

.field-label {
  display: block;
  margin-bottom: 0.625rem;
  color: #636E72;
  font-weight: 600;
  font-size: 0.875rem;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 1rem;
  width: 20px;
  height: 20px;
  color: #95A5A6;
  pointer-events: none;
  transition: color 0.3s ease;
  z-index: 1;
}

.styled-input {
  width: 100%;
  padding: 0.875rem 0.875rem 0.875rem 3rem;
  background: #FFF5E6;
  border: 1.5px solid #FFE4CC;
  border-radius: 12px;
  font-size: 0.9rem;
  color: #2D3436;
  box-sizing: border-box;
  transition: all 0.3s ease;
  font-weight: 400;
}

.styled-input::placeholder {
  color: #95A5A6;
}

.styled-input:focus {
  outline: none;
  background: #FFFFFF;
  border-color: #FF6B6B;
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.15);
}

/* Override browser autofill styles */
.styled-input:-webkit-autofill,
.styled-input:-webkit-autofill:hover,
.styled-input:-webkit-autofill:focus,
.styled-input:-webkit-autofill:active {
  -webkit-background-clip: text;
  -webkit-text-fill-color: #ffffff;
  transition: background-color 5000s ease-in-out 0s;
  box-shadow: inset 0 0 20px 20px rgba(255, 255, 255, 0.05);
  border: 1.5px solid #FFE4CC;
}

.styled-input:focus + .input-icon,
.input-wrapper:focus-within .input-icon {
  color: #FF6B6B;
}

/* Gender Radio Buttons */
.gender-options {
  display: flex;
  gap: 0.625rem;
}

.radio-label {
  flex: 1;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem 0.75rem;
  background: #FFF5E6;
  border: 1.5px solid #FFE4CC;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.radio-label:hover {
  border-color: #FF6B6B;
  background: rgba(255, 107, 107, 0.15);
  transform: translateY(-2px);
}

/* Hide default radio button */
.radio-label input[type="radio"] {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

/* Custom radio indicator */
.radio-custom {
  position: absolute;
  top: 0.625rem;
  right: 0.625rem;
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  transition: all 0.3s ease;
}

.radio-custom::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) scale(0);
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #60a5fa;
  transition: transform 0.3s ease;
}

/* Gender icon */
.radio-icon {
  width: 28px;
  height: 28px;
  stroke: rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
}

.radio-text {
  color: #95A5A6;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.3s ease;
}

/* Checked state */
.radio-label:has(input[type="radio"]:checked) {
  border-color: #FF6B6B;
  background: rgba(255, 107, 107, 0.15);
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.15);
}

.radio-label:has(input[type="radio"]:checked) .radio-custom {
  border-color: #FF6B6B;
  background: rgba(255, 107, 107, 0.15);
}

.radio-label:has(input[type="radio"]:checked) .radio-custom::after {
  transform: translate(-50%, -50%) scale(1);
}

.radio-label:has(input[type="radio"]:checked) .radio-icon {
  stroke: #60a5fa;
  transform: scale(1.1);
}

.radio-label:has(input[type="radio"]:checked) .radio-text {
  color: #FF6B6B;
  font-weight: 600;
}


.input-hint {
  display: block;
  margin-top: 0.5rem;
  font-size: 0.8rem;
  color: #95A5A6;
  padding-left: 0.25rem;
}

/* Signup Button */
.signup-button {
  width: 100%;
  padding: 1rem;
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  color: #2D3436;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  margin-top: 0.5rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
  position: relative;
  overflow: hidden;
}

.signup-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.signup-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
}

.signup-button:hover:not(:disabled)::before {
  left: 100%;
}

.signup-button:active:not(:disabled) {
  transform: translateY(0);
}

.signup-button:disabled {
  background: rgba(255, 255, 255, 0.1);
  cursor: not-allowed;
  box-shadow: none;
}

.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.spinner {
  width: 20px;
  height: 20px;
  animation: rotate 1s linear infinite;
}

.spinner circle {
  stroke: currentColor;
  stroke-linecap: round;
  animation: dash 1.5s ease-in-out infinite;
}

@keyframes rotate {
  100% { transform: rotate(360deg); }
}

@keyframes dash {
  0% {
    stroke-dasharray: 1, 150;
    stroke-dashoffset: 0;
  }
  50% {
    stroke-dasharray: 90, 150;
    stroke-dashoffset: -35;
  }
  100% {
    stroke-dasharray: 90, 150;
    stroke-dashoffset: -124;
  }
}

/* Error Message */
.error {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.875rem 1rem;
  margin-bottom: 1rem;
  background: rgba(239, 68, 68, 0.15);
  border: 1px solid rgba(239, 68, 68, 0.3);
  color: #fca5a5;
  border-radius: 10px;
  font-size: 0.875rem;
  backdrop-filter: blur(10px);
}

.error-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

.error-fade-enter-active,
.error-fade-leave-active {
  transition: all 0.3s ease;
}

.error-fade-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.error-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Login Link */
.login-link {
  text-align: center;
  margin-top: 1.5rem;
  font-size: 0.9rem;
  color: #95A5A6;
}

.login-link a {
  color: #FF6B6B;
  text-decoration: none;
  font-weight: 600;
  margin-left: 0.375rem;
  cursor: pointer;
  transition: color 0.3s ease;
}

.login-link a:hover {
  color: #93c5fd;
  text-decoration: underline;
}

/* Responsive Design */
@media (max-width: 640px) {
  .signup-box {
    padding: 2rem 1.5rem;
    margin: 1rem;
  }
  
  h2 {
    font-size: 1.75rem;
  }
  
  .hosu-header {
    padding: 0 1rem;
  }
  
  .gender-options {
    flex-direction: column;
  }
}
</style>
