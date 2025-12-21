<template>
  <div class="hosu-main-page">
    <!-- Header -->
    <header class="hosu-header">
      <div class="hosu-logo" @click="goHome">
        <span class="logo-main">HOSU</span>
        <span class="logo-sub">taste log</span>
      </div>
    </header>

    <!-- Hero Banner / Image Section -->
    <section class="hero-banner">
      <!-- 이미지 캐러셀 -->
      <div class="carousel-images">
        <img
          v-for="(image, index) in carouselImages"
          :key="index"
          :src="image"
          :class="{ active: currentImageIndex === index }"
          alt="Hero Image"
        />
      </div>

      <div class="hero-text">
        <h2>HOSU : Taste Log</h2>
        <h3>당신의 맛집 피드, Hosu</h3>
      </div>

      <!-- 캐러셀 인디케이터 -->
      <div class="carousel-indicators">
        <span
          v-for="(image, index) in carouselImages"
          :key="index"
          :class="{ active: currentImageIndex === index }"
          @click="currentImageIndex = index"
        ></span>
      </div>
    </section>

    <!-- Main Layout with Right Navigation -->
    <div class="main-layout">
      <!-- Main Content -->
      <main class="hosu-content">
      <!-- 메인 검색 바 (타베로그 스타일 가로형) -->
      <section class="search-bar-section">
        <h1 class="page-title">어떤 식당을 찾고 있으세요?</h1>

        <div class="search-row">
          <select v-model="searchForm.sidoNo">
            <option value="">시도 전체</option>
            <option v-for="sido in sidoList" :key="sido.sidoNo" :value="sido.sidoNo">
              {{ sido.sidoName }}
            </option>
          </select>

          <select v-model="searchForm.gunguCode" :disabled="gunguList.length === 0">
            <option value="">군구 선택</option>
            <option v-for="gungu in gunguList" :key="gungu.gunguCode" :value="gungu.gunguCode">
              {{ gungu.gunguName }}
            </option>
          </select>

          <select v-model="searchForm.category">
            <option value="">카테고리 전체</option>
            <option v-for="category in categories" :key="category.value" :value="category.value">
              {{ category.label }}
            </option>
          </select>

          <select v-model="searchForm.tagId" :disabled="tagList.length === 0">
            <option value="">메뉴 태그 선택</option>
            <option v-for="tag in tagList" :key="tag.tagId" :value="tag.tagId">
              {{ tag.tagName }}
            </option>
          </select>

          <button class="primary-btn" @click="onSearch">검색</button>
        </div>
      </section>

      <!-- AI 자연어 검색 섹션 -->
      <section class="ai-search-section">
        <div class="ai-search-card">
          <div class="ai-header">
            <div class="ai-icon">
              <img src="@/assets/ai.png" alt="AI Icon" class="ai-image" />
            </div>
            <h2>AI 맛집 추천</h2>
          </div>
          <p class="section-desc">자연어로 원하는 맛집을 물어보세요!</p>
          
          <div class="ai-input-wrapper">
            <input 
              v-model="aiSearchQuery"
              type="text" 
              class="ai-search-input" 
              placeholder="예: 강남역 근처 분위기 좋은 일식당 추천해줘"
              @keyup.enter="handleAISearch"
            />
            <button class="ai-search-btn" @click="handleAISearch">
              <img src="@/assets/search.png" alt="검색" class="search-icon" />
            </button>
          </div>

          <div class="ai-examples">
            <span class="example-label">예시:</span>
            <button 
              v-for="(example, index) in aiExamples" 
              :key="index"
              class="example-chip"
              @click="aiSearchQuery = example; handleAISearch()"
            >
              {{ example }}
            </button>
          </div>
        </div>
      </section>

      <!-- 3가지 검색 섹션 -->
      <section class="search-sections">
        <!-- 1. 지역으로 검색 -->
        <div class="search-section-card">
          <h2>지역으로 검색</h2>
          <p class="section-desc">자주 가는 지역을 선택해서 주변 맛집을 찾아보세요.</p>
          <div class="chip-list">
            <button
              v-for="sido in sidoList"
              :key="sido.sidoNo"
              class="chip"
              @click="searchByRegion(sido.sidoNo)"
            >
              {{ sido.sidoName }}
            </button>
          </div>
        </div>

        <!-- 2. 인기 식당 -->
        <div class="search-section-card">
          <h2>인기 식당</h2>
          <p class="section-desc">다양한 기준으로 인기 있는 맛집을 찾아보세요.</p>
          <div class="category-grid">
            <button class="category-card" @click="searchByPopularity('VIEW')">
              <span class="category-icon"><img src="@/assets/fire.png" alt="최다 조회" /></span>
              <h3 class="category-title">최다 조회</h3>
              <p class="category-desc">가장 핫한 맛집</p>
            </button>
            <button class="category-card" @click="searchByPopularity('RATING')">
              <span class="category-icon"><img src="@/assets/noheart.png" alt="평점 높은 식당" /></span>
              <h3 class="category-title">평점 높은 식당</h3>
              <p class="category-desc">추천이 많은 맛집</p>
            </button>
            <button class="category-card" @click="searchByPopularity('REVIEW')">
              <span class="category-icon"><img src="@/assets/comment.png" alt="리뷰 많은 식당" /></span>
              <h3 class="category-title">리뷰 많은 식당</h3>
              <p class="category-desc">댓글이 많은 맛집</p>
            </button>
            <button class="category-card" @click="searchByPopularity('LATEST')">
              <span class="category-icon"><img src="@/assets/shuffle.png" alt="최근 등록된 식당" /></span>
              <h3 class="category-title">최근 등록된 식당</h3>
              <p class="category-desc">새로 등록된 맛집</p>
            </button>
          </div>
        </div>

        <!-- 3. 메뉴 종류별 검색 -->
        <div class="search-section-card">
          <h2>메뉴 종류별 검색</h2>
          <p class="section-desc">먹고 싶은 메뉴 종류를 선택하면 관련 맛집을 보여드립니다.</p>
          
          <!-- 카테고리 버튼 -->
          <div class="category-buttons">
            <button
              v-for="category in categories"
              :key="category.value"
              :class="['category-btn', { active: selectedCategory === category.value }]"
              @click="toggleCategory(category.value)"
              :disabled="getTagsByCategory(category.value).length === 0"
            >
              {{ category.label }}
            </button>
          </div>

          <!-- 선택된 카테고리의 태그 표시 -->
          <transition name="slide-fade">
            <div v-if="selectedCategory && getTagsByCategory(selectedCategory).length > 0" class="tags-container">
              <h3 class="category-header">{{ getCategoryLabel(selectedCategory) }} 태그</h3>
              <div class="chip-list">
                <button
                  v-for="menuType in getTagsByCategory(selectedCategory)"
                  :key="menuType.tagId"
                  class="chip"
                  @click="searchByMenuType(selectedCategory, menuType.tagId, menuType.tagName)"
                >
                  {{ menuType.tagName }}
                </button>
              </div>
            </div>
          </transition>
        </div>
      </section>
    </main>

      <SidebarProfile class="left-navigation" />
    </div>
  </div>
</template>

<script setup>
import SidebarProfile from '@/components/common/SidebarProfile.vue'
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSido, getGungu, getMenuTag, getMenuTagAll } from '@/api/search.js'
import { getUserProfile } from '@/api/user.js'
import { parseAIQuery } from '@/api/ai.js'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 로그인 상태 체크
const isLoggedIn = computed(() => userStore.isLoggedIn)



// 캐러셀 이미지 (임시 플레이스홀더)
const carouselImages = ref([
  new URL('@/assets/main1.jpg', import.meta.url).href,
  new URL('@/assets/main2.jpg', import.meta.url).href,
  new URL('@/assets/main3.jpg', import.meta.url).href,
  new URL('@/assets/main4.jpg', import.meta.url).href,
  new URL('@/assets/main5.jpg', import.meta.url).href,
])

const currentImageIndex = ref(0)
let carouselInterval = null

// 자동 캐러셀
onMounted(async () => {
  carouselInterval = setInterval(() => {
    currentImageIndex.value = (currentImageIndex.value + 1) % carouselImages.value.length
  }, 6000) // 6초마다 전환

  // 사용자 프로필 갱신 (로그인 상태일 때)
  if (isLoggedIn.value) {
    try {
      const userNo = localStorage.getItem('userNo')
      const response = await getUserProfile(userNo)
      const data = response.data

      // 스토어 업데이트
      userStore.updateNickname(data.nickname)
      if (data.userImg) userStore.updateUserImg(data.userImg)
      
      userStore.updateUserStats({
        followingCount: data.followingCount,
        followerCount: data.followerCount,
        reviewCount: data.postCount
      })
    } catch (error) {
      console.error('프로필 갱신 실패:', error)
    }
  }
})

onUnmounted(() => {
  if (carouselInterval) {
    clearInterval(carouselInterval)
  }
})

const searchForm = ref({
  sidoNo: '',
  gunguCode: '',
  category: '',
  tagId: '',
})

// sido 데이터 불러오기
const sidoList = ref([])
const loading = ref(false)
onMounted(async () => {
  loading.value = true
  try {
    const res = await getSido()
    sidoList.value = res.data
  } catch (err) {
    console.error('시도 목록 불러오기 실패:', err)
  } finally {
    loading.value = false
  }
})

// 선택된 시도에 따라 군구를 필터링했다고 가정
const gunguList = ref([])

watch(
  () => searchForm.value.sidoNo,
  async (newSidoNo) => {
    if (!newSidoNo) {
      gunguList.value = []
      searchForm.value.gunguCode = ''
      return
    }
    try {
      const res = await getGungu(newSidoNo)
      console.log('군구 응답:', res.data)

      // 🔥 정답: 배열 그대로 넣기
      gunguList.value = res.data

      // 시도 변경 시 군구 초기화
      searchForm.value.gunguCode = ''
    } catch (err) {
      console.error('군구 로딩 실패:', err)
    }
  },
)

// 카테고리 데이터(default)
const categories = [
  { value: 'KOREAN', label: '한식' },
  { value: 'JAPANESE', label: '일식' },
  { value: 'CHINESE', label: '중식' },
  { value: 'WESTERN', label: '양식' },
  { value: 'ASIAN', label: '아시안' },
  { value: 'CURRY', label: '커리' },
  { value: 'SEAFOOD', label: '해산물' },
  { value: 'MEAT', label: '고기' },
  { value: 'CHICKEN', label: '치킨' },
  { value: 'SNACK', label: '간식류' },
  { value: 'PUB', label: '술집' },
  { value: 'BAKERY', label: '빵' },
  { value: 'FASTFOOD', label: '패스트푸드' },
  { value: 'CAFE', label: '카페' },
]

const tagList = ref([])
watch(
  () => searchForm.value.category,
  async (newCategory) => {
    if (!newCategory) {
      tagList.value = []
      searchForm.value.tagId = ''
      return
    }
    try {
      const res = await getMenuTag(newCategory)
      tagList.value = res.data // [{id, tagName}, ...]
      searchForm.value.tagId = '' // 초기화
    } catch (err) {
      console.error('메뉴 태그 로딩 실패:', err)
    }
  },
)

const menuList = ref([])
const selectedCategory = ref('') // 선택된 카테고리

onMounted(async () => {
  loading.value = true
  try {
    const res = await getMenuTagAll()
    menuList.value = res.data
  } catch (err) {
    console.error('시도 목록 불러오기 실패:', err)
  } finally {
    loading.value = false
  }
})

// 카테고리 토글
const toggleCategory = (categoryValue) => {
  if (selectedCategory.value === categoryValue) {
    selectedCategory.value = '' // 같은 카테고리 클릭 시 닫기
  } else {
    selectedCategory.value = categoryValue // 다른 카테고리 선택
  }
}

// 카테고리 라벨 가져오기
const getCategoryLabel = (categoryValue) => {
  const category = categories.find(cat => cat.value === categoryValue)
  return category ? category.label : ''
}


const goHome = () => {
  router.push('/')
}

const goLogin = () => {
  router.push('/login')
}

const onSearch = () => {
  // 검색 버튼 눌렀을 때 /search 페이지로 이동
  const { sidoNo, gunguCode, category, tagId } = searchForm.value
  const query = {
    ...(sidoNo && { sidoNo }),
    ...(gunguCode && { gunguCode }),
    ...(category && { category }),
    ...(tagId && { tagId }),
  }

  if (tagId) {
    const selectedTag = tagList.value.find((t) => t.tagId === tagId)
    if (selectedTag) query.tagName = selectedTag.tagName
  }

  router.push({
    path: '/list',
    query: query,
  })
}

// 최근 검색 이력 가져오기
const recentSearches = ref([
  // 실제로는 서버에서 최근 검색 이력 가져오기
  // { id: 1, label: '대전 서구 라멘', params: { ... } },
])

// AI 자연어 검색
const aiSearchQuery = ref('')
const aiExamples = [
  '강남역 근처 일식당 추천해줘',
  '대전에서 파스타 맛있는 곳',
  '분위기 좋은 데이트 장소'
]

const handleAISearch = async () => {
  if (!aiSearchQuery.value.trim()) {
    alert('검색어를 입력해주세요!')
    return
  }
  
  try {
    // AI API 호출하여 자연어 파싱
    const parsed = await parseAIQuery(aiSearchQuery.value)
    console.log('AI 파싱 결과:', parsed)
    
    // 파싱된 조건으로 검색
    const query = {}
    if (parsed.sidoNo) query.sidoNo = parsed.sidoNo
    // gunguCode가 없으면 'not' 전달 (필터 유지용)
    query.gunguCode = parsed.gunguCode || 'not'
    if (parsed.gunguName) query.gunguName = parsed.gunguName
    if (parsed.category) query.category = parsed.category
    // tagId가 없으면 '0' 전달 (필터 유지용)
    query.tagId = parsed.tagId || '0'
    if (parsed.tagName) query.tagName = parsed.tagName
    if (parsed.sortBy) query.sortBy = parsed.sortBy
    if (parsed.keyword) query.keyword = parsed.keyword
    
    router.push({
      path: '/list',
      query: query
    })
  } catch (error) {
    console.error('AI 검색 실패:', error)
    // 실패 시 키워드 검색으로 폴백
    router.push({
      path: '/list',
      query: { keyword: aiSearchQuery.value }
    })
  }
}

const searchByRegion = (sidoNo) => {
  router.push({
    path: '/list',
    query: { sidoNo },
  })
}

const searchByMenuType = (category, tagId, tagName) => {
  router.push({
    path: '/list',
    query: { category, tagId, tagName },
  })
}

const searchByPopularity = (sortBy) => {
  router.push({
    path: '/list',
    query: { sortBy },
  })
}

const searchByKeyword = (keyword) => {
  router.push({
    path: '/list',
    query: { keyword },
  })
}

const searchFromHistory = (item) => {
  // item.params 에 검색 조건을 저장해둔다고 가정
  router.push({
    path: '/list',
    query: item.params || { keyword: item.label },
  })
}

// 카테고리별로 태그 필터링
const getTagsByCategory = (categoryValue) => {
  return menuList.value.filter(tag => tag.category === categoryValue)
}

const encodeURIComponent = window.encodeURIComponent
</script>

<style scoped>
.hosu-main-page {
  /* Custom Requested Palette */
  --color-bg: #F2F2F2;
  --color-emphasis: #593527;
  --color-main: #F29F05;
  --color-sub-1: #F2DCB3;
  --color-sub-2: #D97904;

  --bg-base: var(--color-bg);
  --bg-inactive: var(--color-bg);
  --bg-active: #FFFFFF;
  --sub-dark: var(--color-emphasis);
  --sub-light: var(--color-sub-1);
  --main-color: var(--color-sub-1);
  --text-primary: var(--color-emphasis);
  --border-color: var(--color-sub-1);

  /* Mapping for decorative naming consistency */
  --baltic-amber: var(--color-main);
  --chocolate-martini: var(--color-emphasis);
  --chanterelle: var(--color-sub-2);
  --mocha-mousse: var(--color-emphasis);
  --cream-tan: var(--color-sub-1);
  --sirocco: var(--color-sub-1);
  --safari: var(--color-sub-2);
  --cannoli-cream: var(--color-bg);

  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  background: var(--bg-base);
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  color: var(--text-primary);
}


/* Header */
.hosu-header {
  position: relative;
  z-index: 10;
  height: 80px;
  min-height: 80px;
  flex-shrink: 0;
  padding: 0 2rem;
  background: var(--bg-active);
  border-bottom: 2px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(31, 18, 7, 0.05);
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

/* Hero Banner Section */
.hero-banner {
  max-width: 1400px;
  width: calc(100% - 4rem);
  height: 400px;
  min-height: 400px;
  flex-shrink: 0;
  background: linear-gradient(135deg, var(--sirocco) 0%, var(--mocha-mousse) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
  margin: 2rem auto;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(91, 64, 49, 0.2);
}

/* 캐러셀 이미지 컨테이너 */
.carousel-images {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

/* 이미지가 추가되면 아래 스타일 사용 */
.carousel-images img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: absolute;
  top: 0;
  left: 0;
  opacity: 0;
  transition: opacity 1s ease-in-out;
}

.carousel-images img.active {
  opacity: 1;
  z-index: 1;
}

/* 캐러셀 인디케이터 */
.carousel-indicators {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  z-index: 3;
}

.carousel-indicators span {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s ease;
}

.carousel-indicators span:hover {
  background: rgba(255, 255, 255, 0.8);
}

.carousel-indicators span.active {
  background: #ffffff;
  width: 30px;
  border-radius: 6px;
}

/* 히어로 텍스트 */
.hero-text {
  position: relative;
  z-index: 2;
  text-align: center;
  color: #ffffff;
  background: rgba(91, 64, 49, 0.2);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  padding: 2rem 3rem;
  border-radius: 20px;
  border: 1px solid var(--cream-tan);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.hero-text h2 {
  font-size: 2.5rem;
  font-weight: 700;
  margin: 0 0 0.5rem 0;
  letter-spacing: -0.5px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  color: #ffffff;
}

.hero-text h3 {
  font-size: 1.2rem;
  font-weight: 500;
  margin: 0;
  color: #ffffff;
  letter-spacing: 0.5px;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
}



/* Main Content */
.hosu-content {
  max-width: 960px;
  margin: 1.5rem auto 2.5rem;
  padding: 0 1rem;
}

/* 검색 바 섹션 */
.page-title {
  font-size: 1.6rem;
  margin-bottom: 1rem;
  color: var(--chocolate-martini);
  font-weight: 700;
  text-align: center;
}

.search-bar-section {
  position: relative;
  margin-top: -100px;
  max-width: 1050px;
  margin-left: auto;
  margin-right: auto;
  background: var(--main-color);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 1.8rem 1.5rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  margin-bottom: 1.5rem;
  z-index: 10;
}

.search-row {
  display: grid;
  grid-template-columns: 1.1fr 1.1fr 1.1fr 2fr auto;
  gap: 0.5rem;
  align-items: center;
}

.search-row select,
.search-row input {
  height: 48px;
  padding: 0 0.75rem;
  border-radius: 0.5rem;
  border: 1px solid var(--border-color);
  background: var(--bg-active);
  color: var(--sub-dark);
  font-size: 0.96rem;
}

.search-row select:focus,
.search-row input:focus {
  outline: none;
  border-color: var(--sub-dark);
}

.search-row select option {
  background: #FFFFFF;
  color: #2D3436;
}

.primary-btn {
  height: 48px;
  padding: 0 1.5rem;
  border-radius: 0.5rem;
  border: none;
  background: var(--color-main);
  color: #FFFFFF;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(242, 159, 5, 0.2);
}

.primary-btn:hover {
  transform: translateY(-2px);
  background: var(--color-sub-2);
  box-shadow: 0 6px 16px rgba(217, 121, 4, 0.3);
}

/* AI 자연어 검색 섹션 */
.ai-search-section {
  margin-bottom: 1.5rem;
}

.ai-search-card {
  background: var(--main-color);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 1.8rem;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.ai-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  margin-bottom: 0.5rem;
}

.ai-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: float 3s ease-in-out infinite;
}

.ai-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  /* 기존 필터링과 어우러지도록 세피아 필터 적용 (필요시) */
  filter: sepia(0.5) saturate(1.5) contrast(0.9);
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.ai-header h2 {
  font-size: 1.3rem;
  margin: 0;
  color: var(--chocolate-martini);
  font-weight: 700;
}

.ai-input-wrapper {
  display: flex;
  gap: 0.5rem;
  margin: 1rem 0;
}

.ai-search-input {
  flex: 1;
  height: 52px;
  padding: 0 1.25rem;
  border-radius: 26px;
  border: 1px solid var(--border-color);
  background: var(--bg-active);
  color: var(--sub-dark);
  font-size: 1rem;
  transition: all 0.3s ease;
}

.ai-search-input::placeholder {
  color: #95A5A6;
}

.ai-search-input:focus {
  outline: none;
  border-color: var(--baltic-amber);
  background: #FFFFFF;
  box-shadow: 0 0 20px rgba(186, 110, 75, 0.2);
}

.ai-search-btn {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  border: none;
  background: var(--color-main);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(242, 159, 5, 0.3);
}

.ai-search-btn:hover {
  transform: translateY(-2px) scale(1.05);
  background: var(--color-sub-2);
  box-shadow: 0 6px 20px rgba(217, 121, 4, 0.4);
}

.search-icon {
  width: 20px;
  height: 20px;
  filter: brightness(0) invert(1);
}

.ai-examples {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  align-items: center;
}

.example-label {
  font-size: 0.85rem;
  color: var(--chanterelle);
  font-weight: 500;
}

.example-chip {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  border: 1px solid var(--border-color);
  background: var(--bg-inactive);
  color: var(--sub-dark);
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.example-chip:hover {
  background: var(--bg-active);
  border-color: var(--sub-dark);
  transform: translateY(-2px);
}

/* 3개 검색 섹션 카드 */
.search-sections {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.search-section-card {
  background: var(--main-color);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 1.8rem;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.search-section-card h2 {
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
  color: var(--chocolate-martini);
  text-align: center;
  font-weight: 700;
}

.section-desc {
  font-size: 0.85rem;
  color: #666;
  margin-bottom: 1rem;
  text-align: center;
}

/* 칩 버튼 */
.chip-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 0.75rem;
}

.chip {
  border-radius: 12px;
  padding: 0.75rem 1.25rem;
  border: 1px solid var(--border-color);
  background: var(--bg-active);
  font-size: 0.95rem;
  cursor: pointer;
  color: var(--sub-dark);
  font-weight: 500;
  text-align: center;
  transition: all 0.2s ease;
}

.chip:hover {
  background: var(--sub-dark);
  color: #FFFFFF;
  border-color: transparent;
  transform: translateY(-2px);
}

/* 카테고리 버튼 */
.category-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.category-btn {
  padding: 0.6rem 1.2rem;
  border-radius: 20px;
  border: 1px solid var(--border-color);
  background: var(--bg-active);
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--sub-dark);
  cursor: pointer;
  transition: all 0.2s ease;
}

.category-btn:hover:not(:disabled) {
  background: var(--sub-dark);
  color: #FFFFFF;
  border-color: transparent;
  transform: translateY(-2px);
}

.category-btn.active {
  background: var(--sub-dark);
  color: #FFFFFF;
  border-color: transparent;
}

.category-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

/* 태그 컨테이너 */
.tags-container {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 2px solid #e5e7eb;
}

.category-header {
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--baltic-amber);
  margin-bottom: 0.75rem;
}

/* 슬라이드 페이드 애니메이션 */
.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.2s ease-in;
}

.slide-fade-enter-from {
  transform: translateY(-10px);
  opacity: 0;
}

.slide-fade-leave-to {
  transform: translateY(-10px);
  opacity: 0;
}



/* 키워드 URL 예시 */
.keyword-url-list {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.keyword-url-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.link-btn {
  background: transparent;
  border: none;
  color: var(--baltic-amber);
  cursor: pointer;
  font-size: 0.85rem;
  transition: color 0.3s ease;
}

.link-btn:hover {
  color: #93c5fd;
}

.url-preview {
  font-size: 0.75rem;
  background: rgba(255, 255, 255, 0.1);
  padding: 0.2rem 0.4rem;
  border-radius: 0.4rem;
  color: #636E72;
}

/* 최근 검색 */
.recent-search-section {
  background: var(--cannoli-cream);
  border: 1px solid var(--safari);
  border-radius: 16px;
  padding: 1.2rem;
  box-shadow: 
    0 10px 15px -3px rgba(61, 43, 31, 0.1),
    0 20px 25px -5px rgba(61, 43, 31, 0.1);
}

.recent-search-section h2 {
  font-size: 1rem;
  margin-bottom: 0.5rem;
  color: var(--chocolate-martini);
}

.login-notice {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  align-items: flex-start;
}

.login-notice p {
  font-size: 0.85rem;
  color: var(--mocha-mousse);
}

/* 인기 카테고리 그리드 */
.category-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.75rem;
}

.category-card {
  background: var(--bg-active);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 1.5rem 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  text-align: center;
}

.category-card:hover {
  background: var(--sub-dark);
  border-color: var(--sub-dark);
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.category-card:hover .category-title,
.category-card:hover .category-desc {
  color: #FFFFFF;
}

.category-card:hover .category-icon img {
  filter: brightness(0) invert(1);
}

.category-icon {
  font-size: 2rem;
  line-height: 1;
}

.category-icon img {
  width: 40px;
  height: 40px;
  object-fit: contain;
  filter: sepia(1) saturate(2) hue-rotate(340deg); /* Amber-like filter */
}

.category-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--chocolate-martini);
  margin: 0;
}

.category-desc {
  font-size: 0.8rem;
  color: var(--mocha-mousse);
  margin: 0;
}

/* 반응형 */
@media (max-width: 900px) {
  .search-row {
    grid-template-columns: 1fr 1fr;
    grid-template-rows: auto auto auto;
  }

  .search-row input {
    grid-column: 1 / span 2;
  }

  .primary-btn {
    grid-column: 1 / span 2;
  }
}

/* Main Layout with Right Navigation */
.main-layout {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  gap: 0; /* 리모컨과 메인 섹션 완전히 붙임 */
  padding: 2rem 0;
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 1240px; /* 리모컨(280px) + 메인(960px) */
  margin: 0 auto; /* 중앙 정렬 */
}

/* Right Navigation */
.left-navigation {
  order: 2; /* 오른쪽에 배치 */
  flex-shrink: 0;
  margin-top: 16px; /* 검색 바와 정확히 맞추기 위한 미세 조정 */
  position: sticky;
  top: 100px; /* 스크롤 시 상단에서 100px 위치에 고정 */
  align-self: flex-start;
  max-height: calc(100vh - 120px); /* 화면 높이에서 120px 제외 */
}

/* Main Content Area */
.hosu-content {
  order: 1; /* 왼쪽에 배치 */
  flex-shrink: 0;
  width: 960px;
  min-width: 0;
}

/* Update search bar section positioning */
.search-bar-section {
  position: relative;
  margin-top: 0;
  max-width: 100%;
  margin-left: 0;
  margin-right: 0;
}

/* Responsive Design */
@media (max-width: 1300px) {
  .hosu-content {
    width: 100%;
    max-width: 100%;
  }
  
  .main-layout {
    padding: 2rem 1rem;
  }
}

@media (max-width: 1200px) {
  .main-layout {
    flex-direction: column;
    align-items: center;
  }

  .left-navigation {
    width: 100%;
    max-width: 600px;
    position: static;
  }

  .nav-menu {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .menu-group,
  .menu-bottom {
    flex-direction: row;
    flex-wrap: wrap;
    flex: 1;
  }

  .nav-item {
    flex: 1;
    min-width: 140px;
  }
  
  .hosu-content {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .main-layout {
    padding: 1rem 0.5rem;
  }

  .left-navigation {
    padding: 1.5rem 1rem;
  }

  .nav-item {
    padding: 0.75rem 0.875rem;
    font-size: 0.875rem;
  }
}
</style>
