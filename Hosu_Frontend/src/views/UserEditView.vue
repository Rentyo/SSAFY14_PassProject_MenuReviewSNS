<template>
  <div class="edit-page">
    <!-- Header -->
    <header class="hosu-header">
      <div class="hosu-logo" @click="goBack">
        <span class="logo-main">HOSU</span>
        <span class="logo-sub">taste log</span>
      </div>
    </header>

    <div class="edit-container">
      <div class="edit-box">
        <h2>정보 수정</h2>

        <!-- 로딩 상태 -->
        <div v-if="loading" class="loading">
          <p>정보를 불러오는 중...</p>
        </div>

        <!-- 수정 폼 -->
        <form v-else @submit.prevent="handleUpdate">
          <!-- 이메일 (읽기 전용) -->
          <div class="form-group">
            <label>이메일</label>
            <input v-model="userInfo.userEmail" type="email" disabled />
            <small class="input-hint">이메일은 변경할 수 없습니다</small>
          </div>

          <!-- 닉네임 -->
          <div class="form-group">
            <label>닉네임</label>
            <input
              v-model="formData.nickname"
              type="text"
              :placeholder="userInfo.nickname"
            />
          </div>

          <!-- 전화번호 -->
          <div class="form-group">
            <label>전화번호</label>
            <input
              v-model="formData.phoneNumber"
              type="tel"
              placeholder="010-1234-5678"
              pattern="[0-9]{3}-[0-9]{4}-[0-9]{4}"
            />
            <small class="input-hint">형식: 010-1234-5678</small>
          </div>

          <!-- 새 비밀번호 -->
          <div class="form-group">
            <label>새 비밀번호</label>
            <input
              v-model="formData.userPw"
              type="password"
              placeholder="변경하지 않으려면 비워두세요"
            />
            <small class="input-hint">비밀번호를 변경하지 않으려면 비워두세요</small>
          </div>

          <!-- 비밀번호 확인 -->
          <div v-if="formData.userPw" class="form-group">
            <label>비밀번호 확인</label>
            <input
              v-model="passwordConfirm"
              type="password"
              placeholder="비밀번호를 다시 입력하세요"
            />
          </div>

          <!-- 프로필 사진 업로드 -->
          <div class="form-group">
            <label>프로필 사진</label>
            
            <!-- 현재 이미지 미리보기 -->
            <div class="image-preview-container">
              <img 
                :src="imagePreview || userInfo.userImg || 'https://via.placeholder.com/150'" 
                alt="프로필 미리보기" 
                class="profile-preview"
              />
            </div>

            <!-- 파일 선택 -->
            <div class="file-input-group">
              <input 
                type="file" 
                ref="fileInput"
                @change="handleFileSelect"
                accept="image/*"
                class="file-input"
                id="profile-image-input"
              />
              <label for="profile-image-input" class="file-label">
                📷 사진 선택
              </label>
              <button 
                v-if="selectedFile" 
                type="button" 
                @click="uploadImage"
                :disabled="uploading"
                class="btn-upload"
              >
                {{ uploading ? '업로드 중...' : '업로드' }}
              </button>
            </div>
            
            <small v-if="selectedFile" class="input-hint">
              선택된 파일: {{ selectedFile.name }}
            </small>
            <small v-else class="input-hint">
              5MB 이하의 이미지 파일만 업로드 가능합니다
            </small>
          </div>

          <!-- 에러 메시지 -->
          <div v-if="errorMessage" class="error">
            {{ errorMessage }}
          </div>

          <!-- 버튼 그룹 -->
          <div class="button-group">
            <button type="submit" class="btn-primary" :disabled="updating">
              {{ updating ? '수정 중...' : '수정하기' }}
            </button>
            <button type="button" @click="goBack" class="btn-secondary">취소</button>
          </div>

          <!-- 회원탈퇴 버튼 -->
          <div class="delete-section">
            <button type="button" @click="confirmDelete" class="btn-delete-account">
              회원탈퇴
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUserInfo, updateUser, uploadProfileImage } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const updating = ref(false)
const uploading = ref(false)
const errorMessage = ref('')

// 파일 업로드 관련
const selectedFile = ref(null)
const imagePreview = ref('')
const fileInput = ref(null)

const userInfo = ref({
  userEmail: '',
  nickname: '',
  phoneNumber: '',
  userImg: ''
})

const formData = ref({
  nickname: '',
  phoneNumber: '',
  userPw: '',
  userImg: ''
})

const passwordConfirm = ref('')

const goBack = () => {
  router.push('/mypage')
}

// 파일 선택 핸들러
const handleFileSelect = (event) => {
  const file = event.target.files[0]
  
  if (!file) return

  // 파일 크기 검증 (5MB)
  if (file.size > 5 * 1024 * 1024) {
    errorMessage.value = '파일 크기는 5MB를 초과할 수 없습니다.'
    return
  }

  // 이미지 파일 검증
  if (!file.type.startsWith('image/')) {
    errorMessage.value = '이미지 파일만 업로드 가능합니다.'
    return
  }

  selectedFile.value = file
  errorMessage.value = ''

  // 미리보기 생성
  const reader = new FileReader()
  reader.onload = (e) => {
    imagePreview.value = e.target.result
  }
  reader.readAsDataURL(file)
}

// 이미지 업로드
const uploadImage = async () => {
  if (!selectedFile.value) return

  const userNo = localStorage.getItem('userNo')
  uploading.value = true
  errorMessage.value = ''

  try {
    await uploadProfileImage(userNo, selectedFile.value)

    // 사용자 정보 다시 불러오기
    await loadUserInfo()

    // UI 초기화
    imagePreview.value = ''
    selectedFile.value = null

    // 파일 input 초기화
    if (fileInput.value) {
      fileInput.value.value = ''
    }

    alert('프로필 사진이 업로드되었습니다!')
  } catch (error) {
    console.error('이미지 업로드 실패:', error)
    errorMessage.value = '이미지 업로드 중 오류가 발생했습니다.'
  } finally {
    uploading.value = false
  }
}

// 사용자 정보 불러오기
const loadUserInfo = async () => {
  const userNo = localStorage.getItem('userNo')
  
  if (!userNo) {
    router.push('/login')
    return
  }

  try {
    loading.value = true
    const response = await getUserInfo(userNo)
    userInfo.value = response.data
    // 초기값 설정
    formData.value.nickname = response.data.nickname || ''
    formData.value.phoneNumber = response.data.phoneNumber || ''
    formData.value.userImg = response.data.userImg || ''
  } catch (error) {
    console.error('정보 로딩 실패:', error)
    errorMessage.value = '사용자 정보를 불러올 수 없습니다.'
  } finally {
    loading.value = false
  }
}

// 정보 수정
const handleUpdate = async () => {
  errorMessage.value = ''

  // 비밀번호 확인
  if (formData.value.userPw && formData.value.userPw !== passwordConfirm.value) {
    errorMessage.value = '비밀번호가 일치하지 않습니다.'
    return
  }

  // 비밀번호 길이 확인
  if (formData.value.userPw && formData.value.userPw.length < 6) {
    errorMessage.value = '비밀번호는 최소 6자 이상이어야 합니다.'
    return
  }

  // 전화번호 형식 확인
  if (formData.value.phoneNumber) {
    const phonePattern = /^[0-9]{3}-[0-9]{4}-[0-9]{4}$/
    if (!phonePattern.test(formData.value.phoneNumber)) {
      errorMessage.value = '전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)'
      return
    }
  }

  const userNo = localStorage.getItem('userNo')
  updating.value = true

  try {
    // 모든 필드 포함 (비어있더라도 이전 값 유지)
    const updateData = {
      nickname: formData.value.nickname,
      phoneNumber: formData.value.phoneNumber,
      userImg: formData.value.userImg
    }
    
    // 비밀번호는 입력이 있을 때만 포함
    if (formData.value.userPw && formData.value.userPw.trim()) {
      updateData.userPw = formData.value.userPw
    }

    await updateUser(userNo, updateData)

    // userStore 업데이트 (실시간 반영)
    if (updateData.nickname) {
      userStore.updateNickname(updateData.nickname)
    }
    if (updateData.userImg) {
      userStore.updateUserImg(updateData.userImg)
    }

    alert('정보가 수정되었습니다!')
    router.push('/mypage')
  } catch (error) {
    console.error('정보 수정 실패:', error)
    if (error.response?.status === 400) {
      errorMessage.value = '닉네임 또는 전화번호가 중복되었습니다.'
    } else {
      errorMessage.value = '정보 수정 중 오류가 발생했습니다.'
    }
  } finally {
    updating.value = false
  }
}

// 회원탈퇴 확인
const confirmDelete = () => {
  const confirmed = confirm(
    '정말로 탈퇴하시겠습니까?\n탈퇴 시 모든 데이터가 삭제되며 복구할 수 없습니다.'
  )

  if (confirmed) {
    handleDelete()
  }
}

// 회원탈퇴 실행
const handleDelete = async () => {
  const userNo = localStorage.getItem('userNo')

  try {
    const { deleteUser } = await import('@/api/user')
    await deleteUser(userNo)
    alert('회원탈퇴가 완료되었습니다.')
    localStorage.clear()
    router.push('/login')
  } catch (error) {
    console.error('회원탈퇴 실패:', error)
    alert('회원탈퇴 중 오류가 발생했습니다.')
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.edit-page {
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

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
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

.edit-container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex: 1;
  padding: 2rem 1rem;
}

.edit-box {
  background: #FFFFFF;
  padding: 40px;
  border-radius: 24px;
  box-shadow: 0 10px 30px rgba(89, 53, 39, 0.08);
  width: 100%;
  max-width: 600px;
  border: 1px solid var(--color-sub-1);
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: var(--color-emphasis);
  font-size: 1.75rem;
  font-weight: 800;
  letter-spacing: -0.5px;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #636E72;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 10px;
  color: var(--color-emphasis);
  font-weight: 700;
  font-size: 14px;
}

input {
  width: 100%;
  padding: 14px 18px;
  border: 2px solid transparent;
  background: #F8F9FA;
  color: var(--color-emphasis);
  border-radius: 12px;
  font-size: 15px;
  box-sizing: border-box;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

input::placeholder {
  color: #ADB5BD;
}

input:focus {
  outline: none;
  background: white;
  border-color: var(--color-main);
  box-shadow: 0 0 0 4px rgba(242, 159, 5, 0.1);
}

input:disabled {
  background-color: #F1F3F5;
  color: #95A5A6;
  cursor: not-allowed;
  opacity: 0.7;
  border: none;
}

.input-hint {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: #95A5A6;
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
  background: var(--color-main);
  color: #FFFFFF;
  font-weight: 800;
  box-shadow: 0 4px 15px rgba(242, 159, 5, 0.3);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  background: var(--color-sub-2);
  box-shadow: 0 6px 20px rgba(217, 121, 4, 0.4);
}

.btn-primary:disabled {
  background: #ccc;
  color: #ffffff;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-secondary {
  background-color: var(--color-sub-1);
  color: var(--color-emphasis);
  font-weight: 700;
}

.btn-secondary:hover {
  background-color: #E8CC9F;
  transform: translateY(-1px);
}

/* 프로필 이미지 업로드 */
.image-preview-container {
  display: flex;
  justify-content: center;
  margin-bottom: 15px;
}

.profile-preview {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid var(--color-sub-1);
  box-shadow: 0 4px 15px rgba(89, 53, 39, 0.15);
}

.file-input-group {
  display: flex;
  gap: 10px;
  align-items: center;
}

.file-input {
  display: none;
}

.file-label {
  flex: 1;
  padding: 14px 18px;
  background: #F8F9FA;
  border: 1.5px solid var(--color-sub-1);
  border-radius: 12px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
  color: var(--color-emphasis);
  font-weight: 700;
}

.file-label:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.3);
}

.btn-upload {
  padding: 12px 20px;
  background: var(--color-main);
  color: #FFFFFF;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.btn-upload:hover:not(:disabled) {
  transform: translateY(-1px);
  background: var(--color-sub-2);
  box-shadow: 0 4px 12px rgba(217, 121, 4, 0.3);
}

.btn-upload:disabled {
  background: rgba(255, 255, 255, 0.1);
  color: #95A5A6;
  cursor: not-allowed;
  transform: none;
}

/* 회원탈퇴 섹션 */
.delete-section {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--color-sub-1);
  text-align: center;
}

.btn-delete-account {
  padding: 8px 16px;
  background: transparent;
  color: #95A5A6;
  border: none;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  text-decoration: underline;
}

.btn-delete-account:hover {
  color: #ef4444;
}
</style>
