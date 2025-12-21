<template>
  <div class="login-page">
    <!-- Animated Background -->
    <div class="background-gradient"></div>
    
    <!-- Header -->
    <header class="hosu-header">
      <div class="hosu-logo" @click="goHome">
        <span class="logo-main">HOSU</span>
        <span class="logo-sub">taste log</span>
      </div>
    </header>
    
    <div class="login-container">
      <div class="login-box">
        <div class="login-header">
          <h2>Welcome Back</h2>
          <p class="subtitle">맛의 기록을 이어가세요</p>
        </div>

        <form @submit.prevent="handleLogin">
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

          <!-- 로그인 버튼 -->
          <button type="submit" :disabled="loading" class="login-button">
            <span v-if="!loading">로그인</span>
            <span v-else class="loading-spinner">
              <svg class="spinner" viewBox="0 0 50 50">
                <circle cx="25" cy="25" r="20" fill="none" stroke-width="5"></circle>
              </svg>
              로그인 중...
            </span>
          </button>
          
          <!-- 회원가입 링크 -->
          <div class="signup-link">
            <span>계정이 없으신가요?</span>
            <a @click="goSignup">회원가입</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useLogin } from '@/composables/useLogin'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goSignup = () => {
  router.push('/signup')
}

const { email, password, loading, errorMessage, handleLogin } = useLogin()
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

* {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.login-page {
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
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  background: var(--color-bg);
}

/* Removed animated gradient background */

.hosu-header {
  position: relative;
  z-index: 10;
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

.login-container {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  flex: 1;
  padding: 3rem 1rem;
}

.login-box {
  background: #FFFFFF;
  padding: 3.5rem 2.5rem;
  border-radius: 24px;
  border: 1px solid var(--color-sub-1);
  box-shadow: 
    0 20px 25px -5px rgba(89, 53, 39, 0.1),
    0 10px 10px -5px rgba(89, 53, 39, 0.04);
  width: 100%;
  max-width: 440px;
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

.login-header {
  text-align: center;
  margin-bottom: 2.5rem;
}

h2 {
  font-size: 2rem;
  font-weight: 800;
  color: var(--color-emphasis);
  margin: 0 0 0.5rem 0;
  letter-spacing: -1px;
}

.subtitle {
  font-size: 1rem;
  color: var(--color-emphasis);
  opacity: 0.8;
  margin: 0;
  font-weight: 500;
}

.form-group {
  margin-bottom: 1.5rem;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 1.25rem;
  width: 20px;
  height: 20px;
  color: var(--color-emphasis);
  opacity: 0.5;
  pointer-events: none;
  transition: all 0.3s ease;
  z-index: 1;
}

.styled-input {
  width: 100%;
  padding: 1.125rem 1.125rem 1.125rem 3.5rem;
  background: #FFFFFF;
  border: 1.5px solid var(--color-sub-1);
  border-radius: 12px;
  font-size: 0.95rem;
  color: var(--color-emphasis);
  box-sizing: border-box;
  transition: all 0.3s ease;
  font-weight: 400;
}

.styled-input::placeholder {
  color: var(--color-emphasis);
  opacity: 0.4;
}

.styled-input:focus {
  outline: none;
  background: #FFFFFF;
  border-color: var(--color-main);
  box-shadow: 0 0 0 4px rgba(242, 159, 5, 0.1);
}

/* Override browser autofill styles */
.styled-input:-webkit-autofill,
.styled-input:-webkit-autofill:hover,
.styled-input:-webkit-autofill:focus,
.styled-input:-webkit-autofill:active {
  -webkit-background-clip: text;
  -webkit-text-fill-color: #2D3436;
  transition: background-color 5000s ease-in-out 0s;
  box-shadow: inset 0 0 20px 20px #FFF5E6;
  border: 1.5px solid #FFE4CC;
}

.styled-input:focus + .input-icon,
.input-wrapper:focus-within .input-icon {
  color: var(--baltic-amber);
}

/* Login Button */
.login-button {
  width: 100%;
  padding: 1.125rem;
  background: var(--color-main);
  color: #FFFFFF;
  border: none;
  border-radius: 12px;
  font-size: 1.1rem;
  font-weight: 700;
  cursor: pointer;
  margin-top: 1rem;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(242, 159, 5, 0.2);
  position: relative;
  overflow: hidden;
}

.login-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  background: var(--color-sub-2);
  box-shadow: 0 8px 20px rgba(217, 121, 4, 0.3);
}

.login-button:hover:not(:disabled)::before {
  left: 100%;
}

.login-button:active:not(:disabled) {
  transform: translateY(0);
}

.login-button:disabled {
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
  background: rgba(217, 121, 4, 0.1);
  border: 1px solid var(--color-sub-2);
  color: var(--color-sub-2);
  border-radius: 10px;
  font-size: 0.875rem;
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

/* Signup Link */
.signup-link {
  text-align: center;
  margin-top: 1.75rem;
  font-size: 0.95rem;
  color: var(--color-emphasis);
  opacity: 0.8;
}

.signup-link a {
  color: var(--color-sub-2);
  text-decoration: none;
  font-weight: 700;
  margin-left: 0.5rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.signup-link a:hover {
  color: var(--color-main);
  text-decoration: underline;
}

/* Responsive Design */
@media (max-width: 640px) {
  .login-box {
    padding: 2rem 1.5rem;
    margin: 1rem;
  }
  
  h2 {
    font-size: 1.75rem;
  }
  
  .hosu-header {
    padding: 0 1rem;
  }
}
</style>
