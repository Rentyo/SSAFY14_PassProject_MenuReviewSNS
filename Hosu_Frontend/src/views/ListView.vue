<template>
  <div class="list-view-container">
    <!-- Header -->
    <header class="hosu-header">
      <div class="hosu-logo" @click="$router.push('/')">
        <span class="logo-main">HOSU</span>
        <span class="logo-sub">taste log</span>
      </div>
    </header>

    <div class="content-wrapper">
      <!-- 왼쪽 사이드바 -->
      <aside class="sidebar">
        <div class="filter-box">
          <h3 class="filter-title">🎨 검색 필터</h3>

          <div class="filter-group">
            <label class="filter-label">시/도</label>
            <select v-model="searchInfo.sido" class="filter-select">
              <option value="">시도 전체</option>
              <option v-for="sido in sidoList" :key="sido.sidoNo" :value="sido.sidoNo">
                {{ sido.sidoName }}
              </option>
            </select>
          </div>

          <div class="filter-group">
            <label class="filter-label">군/구</label>
            <select v-model="searchInfo.gungu" class="filter-select" :disabled="!searchInfo.sido || gunguList.length === 0">
              <option value="">군구 전체 (시도 선택 필요)</option>
              <option v-for="gungu in gunguList" :key="gungu.gunguCode" :value="gungu.gunguCode">
                {{ gungu.gunguName }}
              </option>
            </select>
          </div>

          <div class="filter-group">
            <label class="filter-label">카테고리</label>
            <select v-model="searchInfo.category" class="filter-select">
              <option value="">카테고리 전체</option>
              <option v-for="category in categories" :key="category.value" :value="category.value">
                {{ category.label }}
              </option>
            </select>
          </div>

          <div class="filter-group">
            <label class="filter-label">메뉴 태그</label>
            <select v-model="searchInfo.tagId" class="filter-select" :disabled="!searchInfo.category || tagList.length === 0">
              <option value="">태그 전체 (카테고리 선택 필요)</option>
              <option v-for="tag in tagList" :key="tag.tagId" :value="tag.tagId">
                {{ tag.tagName }}
              </option>
            </select>
          </div>

          <button class="search-btn" @click="executeSearch">검색하기</button>
        </div>

        <div class="filter-box">
          <h3 class="filter-title">📊 정렬 기준</h3>

          <!-- Desktop Sort: Select Box -->
          <div class="desktop-sort">
            <select class="sort-select" v-model="sortOption" @change="setSortOption($event.target.value)">
              <option value="LATEST">최신 순</option>
              <option value="RATING">평점 높은 순</option>
              <option value="REVIEW">리뷰 많은 순</option>
              <option value="VIEW">조회수 많은 순</option>
            </select>
          </div>

          <!-- Mobile Sort: Button Group -->
          <div class="sort-btn-group mobile-sort">
            <button
              class="sort-btn"
              :class="{ active: sortOption === 'LATEST' }"
              @click="setSortOption('LATEST')"
            >
              최신 순
            </button>
            <button
              class="sort-btn"
              :class="{ active: sortOption === 'RATING' }"
              @click="setSortOption('RATING')"
            >
              평점 높은 순
            </button>
            <button
              class="sort-btn"
              :class="{ active: sortOption === 'REVIEW' }"
              @click="setSortOption('REVIEW')"
            >
              리뷰 많은 순
            </button>
            <button
              class="sort-btn"
              :class="{ active: sortOption === 'VIEW' }"
              @click="setSortOption('VIEW')"
            >
              조회수 많은 순
            </button>
          </div>
        </div>
      </aside>

      <!-- 메인 컨텐츠 -->
      <main class="main-content">
        <div class="result-header">
          <h1 class="result-title">{{ searchResultTitle }} 검색 결과</h1>
          <p class="result-count">총 <strong>{{ totalCount }}</strong>개의 맛집</p>
        </div>

        <!-- 로딩 상태 -->
        <div v-if="loading" class="state-box loading">
          <div class="spinner"></div>
          <p>맛집 정보를 불러오는 중입니다...</p>
        </div>

        <!-- 에러 상태 -->
        <div v-else-if="error" class="state-box error">
          <p>❌ {{ error }}</p>
        </div>

        <!-- 결과 없음 -->
        <div v-else-if="restaurants.length === 0" class="state-box no-results">
          <p>😔 검색 결과가 없습니다.</p>
        </div>

        <!-- 맛집 리스트 -->
        <div v-else class="restaurant-list">
          <article
            v-for="restaurant in restaurants"
            :key="restaurant.id"
            class="restaurant-card"
            @click="goToDetail(restaurant.id)"
          >
            <div class="image-section">
              <img
                :src="restaurant.mainImage || 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&q=80'"
                :alt="restaurant.name"
                @error="handleImageError"
              />
              <div class="image-overlay"></div>
            </div>

            <div class="info-section">
              <div>
                <div class="header-area">
                  <h2 class="restaurant-name">{{ restaurant.name }}</h2>
                  <span class="category-badge">{{ restaurant.category }}</span>
                </div>

                <div class="rating-area">
                  <span class="rating-score">⭐ {{ (restaurant.totalRating && restaurant.reviewCounts) ? (restaurant.totalRating / restaurant.reviewCounts).toFixed(1) : '0.0' }}</span>
                  <span class="divider">|</span>
                  <span class="info-text">리뷰 {{ restaurant.totalContents }}개</span>
                  <span class="divider">|</span>
                  <span class="info-text">조회 {{ restaurant.viewCount.toLocaleString() }}회</span>
                </div>

                <div class="address-area">
                  <span class="icon">📍</span>
                  <span class="address-text">{{ restaurant.address }}</span>
                </div>

                <div class="phone-area" v-if="restaurant.phone">
                  <span class="icon">📞</span>
                  <span class="phone-text">{{ restaurant.phone }}</span>
                </div>
              </div>
            </div>
          </article>
        </div>
        <!-- Pagination -->
        <div v-if="totalPages > 0" class="pagination-container">
          <!-- Page Size Selector -->
          <div class="page-size-selector">
            <select v-model="pageSize" @change="changeSize" class="size-select">
              <option :value="10">10개씩 보기</option>
              <option :value="15">15개씩 보기</option>
              <option :value="20">20개씩 보기</option>
            </select>
          </div>

          <!-- Page Navigation -->
          <div class="pagination-buttons">
            <!-- Prev Button -->
            <button 
              class="page-btn nav-btn" 
              :disabled="currentPage === 0"
              @click="changePage(currentPage - 1)"
            >
              &lt;
            </button>

            <!-- Page Numbers -->
             <button
              v-for="p in visiblePages"
              :key="p"
              class="page-btn"
              :class="{ active: p === currentPage + 1 }"
              @click="changePage(p - 1)"
            >
              {{ p }}
            </button>

            <!-- Next Button -->
            <button 
              class="page-btn nav-btn" 
              :disabled="currentPage >= totalPages - 1"
              @click="changePage(currentPage + 1)"
            >
              &gt;
            </button>
          </div>
        </div>
      </main>

      <SidebarProfile class="remote-control" />
    </div>
  </div>
</template>

<script>
import SidebarProfile from '@/components/common/SidebarProfile.vue';
import { ref, onMounted, watch, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getRestaurant, getSido, getGungu, getMenuTag } from '@/api/search';
import { viewCountRestaurant } from '@/api/restaurants';
import { useUserStore } from '@/stores/user';

export default {
  name: 'ListView',
  components: {
    SidebarProfile
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const userStore = useUserStore();

    const restaurants = ref([]);
    const loading = ref(true);
    const error = ref(null);

    // Pagination State
    const currentPage = ref(0);
    const pageSize = ref(10);
    const totalPages = ref(0);
    const totalCount = ref(0);

    const sortOption = ref('LATEST');

    // 사용자 정보
    const isLoggedIn = computed(() => userStore.isLoggedIn);



    // 검색 조건 (writable)
    const searchInfo = ref({
      sido: '',
      gungu: '',
      category: '',
      tagId: ''
    });


    // 데이터 리스트
    const sidoList = ref([]);
    const gunguList = ref([]);
    const tagList = ref([]);

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
    ];

    // 검색 결과 타이틀 생성
    const searchResultTitle = computed(() => {
      const parts = [];
      const q = route.query;
      console.log(q);
      // 시/도
      if (q.sidoNo && q.sidoNo !== 'not') {
        const sido = sidoList.value.find(s => s.sidoNo == q.sidoNo);
        if (sido) parts.push(sido.sidoName);
      }

      // 군/구
      if (q.gunguCode && q.gunguCode !== 'not') {
        // 주의: gunguList는 현재 선택된 시도의 군구 목록임.
        // 필터를 변경하면 gunguList가 바뀌어서 타이틀의 군구명을 못 찾을 수 있음.
        // 하지만 사용자가 '새 검색'을 위한 필터라고 했으므로, 
        // 결과창의 타이틀은 현재 보여지는 리스트(route.query) 기준이어야 함.
        // 데이터가 없으면 코드를 못 찾으므로 이름 표시 불가 (감수)
        const gungu = gunguList.value.find(g => g.gunguCode == q.gunguCode);
        if (gungu) parts.push(gungu.gunguName);
      }

      // 카테고리
      if (q.category && q.category !== 'not') {
        const cat = categories.find(c => c.value == q.category);
        if (cat) parts.push(cat.label);
      }

      // 태그
      if (q.tagId && q.tagId !== '0' && q.tagId !== 'not') {
        const tag = tagList.value.find(t => t.tagId == q.tagId);
        if (tag) {
          parts.push(`#${tag.tagName}`);
        } else if (q.tagName) {
          parts.push(`#${q.tagName}`);
        }
      }

      return parts.length > 0 ? parts.join(' ') : '전체 맛집';
    });

    const initializeSearchInfo = async () => {
      // 1. URL 파라미터로 searchInfo 초기화
      searchInfo.value.sido = (route.query.sidoNo && route.query.sidoNo !== 'not') ? route.query.sidoNo : '';
      searchInfo.value.category = (route.query.category && route.query.category !== 'not') ? route.query.category : '';

      // sortBy 파라미터 처리
      if (route.query.sortBy) {
        sortOption.value = route.query.sortBy;
      } else {
        sortOption.value = 'LATEST';
      }

      // 2. 초기 리스트 로딩
      await fetchSidoList();

      // 3. 만약 sido가 있다면 군구 로딩
      if (searchInfo.value.sido) {
        await fetchGunguList(searchInfo.value.sido);
        
        // gunguCode가 있으면 그대로 사용
        if (route.query.gunguCode && route.query.gunguCode !== 'not') {
          searchInfo.value.gungu = route.query.gunguCode;
        }
        // gunguName이 있으면 gunguList에서 찾아서 gunguCode 설정
        else if (route.query.gunguName) {
          const foundGungu = gunguList.value.find(g => g.gunguName === route.query.gunguName);
          if (foundGungu) {
            searchInfo.value.gungu = foundGungu.gunguCode;
          }
        }
      }

      // 4. 만약 category가 있다면 태그 로딩
      if (searchInfo.value.category) {
        await fetchTagList(searchInfo.value.category);
        searchInfo.value.tagId = (route.query.tagId && route.query.tagId !== '0') ? route.query.tagId : '';
      }
    };

    const fetchSidoList = async () => {
      try {
        const response = await getSido();
        sidoList.value = response.data;
      } catch (err) {
        console.error("시도 목록 로딩 실패:", err);
      }
    };

    const fetchGunguList = async (sidoNo) => {
      try {
        const response = await getGungu(sidoNo);
        gunguList.value = response.data;
      } catch (err) {
        console.error("군구 목록 로딩 실패:", err);
        gunguList.value = [];
      }
    };

    const fetchTagList = async (category) => {
      try {
        const response = await getMenuTag(category);
        tagList.value = response.data;
      } catch (err) {
        console.error("태그 목록 로딩 실패:", err);
        tagList.value = [];
      }
    };

    // Watcher: 시도 변경 -> 군구 목록 갱신 및 군구 초기화
    watch(() => searchInfo.value.sido, async (newVal, oldVal) => {
      if (newVal) {
         if (newVal !== oldVal) {
             searchInfo.value.gungu = ''; // 값 변경 시 초기화
             await fetchGunguList(newVal);
         }
      } else {
        gunguList.value = [];
        searchInfo.value.gungu = '';
      }
    });

    // Watcher: 카테고리 변경 -> 태그 목록 갱신 및 태그 초기화
    watch(() => searchInfo.value.category, async (newVal, oldVal) => {
       if (newVal) {
          if (newVal !== oldVal) {
            searchInfo.value.tagId = ''; // 값 변경 시 초기화
            await fetchTagList(newVal);
          }
       } else {
         tagList.value = [];
         searchInfo.value.tagId = '';
       }
    });

    const fetchRestaurants = async (page = 0, size = 10) => {
      try {
        loading.value = true;
        error.value = null;

        const sidoNo = route.query.sidoNo || 'not';
        const gunguCode = route.query.gunguCode || 'not';
        const category = route.query.category || 'not';
        const tagId = route.query.tagId || '0';
        const sort = route.query.sortBy || 'LATEST';
        const gunguName = route.query.gunguName || null;
        const tagName = route.query.tagName || null;
        const sortBy = route.query.sortBy || null;

        // Update state
        currentPage.value = page;
        pageSize.value = size;
        sortOption.value = sort;


        const response = await getRestaurant(sidoNo, gunguCode, category, tagId, page, size, sort, gunguName, tagName, sortBy);
        // Handle response
        restaurants.value = response.data.content || [];
        totalPages.value = response.data.totalPages || 0;
        totalCount.value = response.data.totalCount || 0;

        // sortRestaurants(); // Client-side sort removed
      } catch (err) {
        console.error(err);
        error.value = '맛집 정보를 불러오는데 실패했습니다.';
      } finally {
        loading.value = false;
      }
    };

    // Client-side sort removed
    /*
    const sortRestaurants = () => {
      restaurants.value.sort((a, b) => {
        if (sortOption.value === 'rating') {
          return (b.totalRating || 0) - (a.totalRating || 0);
        }
        if (sortOption.value === 'reviews') {
          return (b.totalContents || 0) - (a.totalContents || 0);
        }
        if (sortOption.value === 'views') {
          return (b.viewCount || 0) - (a.viewCount || 0);
        }
        return 0;
      });
    };
    */

    const setSortOption = (option) => {
      // sortOption.value = option; // fetchRestaurants will update this
      // Update route, which triggers fetch
      router.push({
        path: '/list',
        query: {
          ...route.query,
          sortBy: option,
          page: 0 // Reset to first page on sort change
        }
      });
    };

    const executeSearch = () => {
      const query = {};

      if (searchInfo.value.sido) query.sidoNo = searchInfo.value.sido;
      if (searchInfo.value.gungu) query.gunguCode = searchInfo.value.gungu;
      if (searchInfo.value.category) query.category = searchInfo.value.category;
      if (searchInfo.value.tagId) query.tagId = searchInfo.value.tagId;

      if (searchInfo.value.tagId) query.tagId = searchInfo.value.tagId;

      // Reset to page 0 on new search
      router.push({ 
        path: '/list', 
        query: { ...query, page: 0, size: pageSize.value } 
      });
    };

    const changePage = (page) => {
      if (page < 0 || page >= totalPages.value) return;
      
      // Update URL which triggers watcher or direct fetch if we want to optimize
      // Here we push to router and let watcher handle it? 
      // The user implementation had fetchRestaurants in watcher.
      // But user request says "페이지를 넘어갈 때 getRestaurant()를 실행하면 돼".
      // We can do direct call or router push. Router push is better for history.
      
      // However, previous watcher logic calls fetchRestaurants on route change.
      // Let's implement direct call optimization if route watcher exists, 
      // OR just rely on router push. 
      // User said: "그 때는 getRestaurant의 인자로 size를 주면 돼"
      // Let's update router to keep URL in sync.
      
      router.push({
        path: '/list',
        query: {
          ...route.query,
          page: page,
          size: pageSize.value
        }
      });
    };

    const changeSize = () => {
      router.push({
        path: '/list',
        query: {
          ...route.query,
          page: 0, // Reset to page 0 when size changes
          size: pageSize.value
        }
      });
    };

    const visiblePages = computed(() => {
        const currentGroup = Math.floor(currentPage.value / 10);
        const start = currentGroup * 10 + 1;
        const end = Math.min((currentGroup + 1) * 10, totalPages.value);
        
        const pages = [];
        for (let i = start; i <= end; i++) {
            pages.push(i);
        }
        return pages;
    });

    const goToDetail = async (id) => {
      try {
        // 조회수 증가
        await viewCountRestaurant(id);
      } catch (error) {
        console.error('조회수 증가 실패:', error);
        // 조회수 증가 실패해도 페이지 이동은 계속 진행
      }

      // 상세 페이지로 이동
      router.push(`/restaurant/${id}`);
    };

    const handleImageError = (e) => {
      e.target.src = 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&q=80';
    };

    // Route query 변경 감지 -> 데이터 Fetch
    // URL이 변경되면 리스트를 다시 불러온다.
    // 필터 상태 동기화는 initializeSearchInfo가 담당하지만,
    // 단순 검색(executeSearch)시에는 이미 searchInfo가 최신이므로 데이터만 fetch하면 됨.
    // 하지만 뒤로가기 대응을 위해 initializeSearchInfo를 호출하는게 안전함.
    watch(() => route.query, async (newQuery) => {
      const page = parseInt(newQuery.page) || 0;
      const size = parseInt(newQuery.size) || pageSize.value;
      
      await fetchRestaurants(page, size);

      // 만약 URL과 현재 searchInfo가 다르면 동기화 (뒤로가기 시)
      const currentSido = searchInfo.value.sido || 'not';
      const urlSido = newQuery.sidoNo || 'not';

      // 변경이 감지되면 전체 초기화 다시 실행 (조금 비효율적일 수 있으나 안전)
      // 단, executeSearch로 인해 push 된 경우는 searchInfo가 이미 맞음.
      // 뒤로가기 한 경우에만 searchInfo를 url에 맞춰야 함.
      // 간단히: 항상 URL priority.
      const isSidoMatch = (searchInfo.value.sido || '') == (newQuery.sidoNo || '');
      // ...비교 로직 복잡하므로 그냥 재초기화 호출
      await initializeSearchInfo();
      // 주의: initializeSearchInfo 내부에서 값 할당 시 watch 트리거 되어 중복 fetch 발생 가능성 있음.
      // 하지만 watch는 '변경'될 때만 동작하므로,
      // 이미 같은 값이면 동작 안 함.
    });

    onMounted(async () => {
      await initializeSearchInfo();
      await fetchRestaurants();
    });

    return {
      restaurants,
      loading,
      error,
      searchInfo,
      sortOption,
      sidoList,
      gunguList,
      tagList,
      categories,
      setSortOption,
      executeSearch,
      goToDetail,
      handleImageError,
      // sortRestaurants, // Removed

      isLoggedIn,
      searchResultTitle,
      // Pagination
      currentPage,
      pageSize,
      totalPages,
      totalCount,
      changePage,
      changeSize,
      visiblePages
    };
  },
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

/* === GLOBAL === */
* {
  box-sizing: border-box;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

/* === HEADER === */
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

/* === CONTAINER === */
.list-view-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  background: linear-gradient(
    135deg,
    #FFF8F0 0%,
    #FFF8F0 25%,
    #FFF8F0 50%,
    #FFF8F0 75%,
    #FFF8F0 100%
  );
  
  
  overflow-y: auto;
}

@keyframes gradientShift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.content-wrapper {
  max-width: 1600px;
  margin: 0 auto;
  padding: 32px;
  display: grid;
  grid-template-columns: 320px 1fr 280px;
  gap: 32px;
}

/* === SIDEBAR === */
.sidebar {
  position: sticky;
  top: 80px;
  height: fit-content;
}

.sidebar-header {
  background: #FFFFFF;
  color: #2D3436;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.15);
  border: 1px solid #FFE4CC;
}

.sidebar-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
}

.filter-box {
  background: #FFFFFF;  border-radius: 12px;
  padding: 24px 22px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.15);
  border: 1px solid #FFE4CC;
}

.filter-title {
  font-size: 18px;
  font-weight: 700;
  color: #2D3436;
  margin-top: 0;
  margin-bottom: 18px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.filter-group {
  margin-bottom: 16px;
}

.filter-label {
  display: block;
  font-size: 13px;
  font-weight: 700;
  color: #2D3436;
  margin-bottom: 6px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.filter-select {
  width: 100%;
  padding: 10px 12px;
  border: 1.5px solid #FFE4CC;
  border-radius: 8px;
  font-size: 14px;
  color: #2D3436;
  transition: all 0.3s ease;
  background: #FFF5E6;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%23ffffff'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M19 9l-7 7-7-7'%3E%3C/path%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  background-size: 16px;
  padding-right: 32px;
}

.filter-select:focus {
  background-color: rgba(255, 255, 255, 0.08);
  border-color: #FF6B6B;
  outline: none;
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.15);
}

.filter-select:disabled {
  background-color: #FFF5E6;
  color: #95A5A6;
  cursor: not-allowed;
  border-color: #FFE4CC;
  opacity: 0.7;
}

.filter-select option {
  background: #FFFFFF;
  color: #2D3436;
}

.search-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  border: none;
  border-radius: 10px;
  color: #2D3436;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
  margin-top: 8px;
  font-size: 15px;
  position: relative;
  overflow: hidden;
}

.search-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
}

.search-btn:hover::before {
  left: 100%;
}

.search-btn:active {
  transform: translateY(0);
}

/* === SORT SELECT (Desktop) === */
.sort-select {
  width: 100%;
  padding: 12px;
  border-radius: 8px;
  border: 1.5px solid #FFE4CC;
  font-size: 15px;
  color: #2D3436;
  cursor: pointer;
  background: #FFF5E6;
  transition: all 0.3s ease;
  font-weight: 600;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%23ffffff'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M19 9l-7 7-7-7'%3E%3C/path%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  background-size: 16px;
  padding-right: 32px;
}

.sort-select option {
  background: #FFFFFF;
  color: #2D3436;
}

.sort-select:focus {
  border-color: #FF6B6B;
  background-color: rgba(255, 255, 255, 0.08);
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.15);
  outline: none;
}

/* === PAGINATION === */
.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 40px;
  gap: 20px;
  position: relative;
  padding-bottom: 40px;
}

.page-size-selector {
  position: absolute;
  left: 0;
}

.size-select {
  padding: 8px 12px;
  border-radius: 8px;
  background: #FFF5E6;
  border: 1px solid #FFE4CC;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
}

.size-select option {
  background: #FFFFFF;
  color: #2D3436;
}

.pagination-buttons {
  display: flex;
  gap: 8px;
}

.page-btn {
  min-width: 36px;
  height: 36px;
  padding: 0 6px;
  border-radius: 8px;
  border: 1px solid #FFE4CC;
  background: #FFF5E6;
  color: #636E72;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 500;
  font-size: 14px;
}

.page-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
}

.page-btn.active {
  background: #60a5fa;
  border-color: #FF6B6B;
  color: #fff;
  font-weight: 700;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.nav-btn {
  font-weight: 700;
}
.sort-btn-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.sort-btn {
  padding: 12px;
  border: 1.5px solid #FFE4CC;
  border-radius: 8px;
  background: #FFF5E6;
  color: #636E72;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: left;
}

.sort-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #2D3436;
  border-color: rgba(255, 255, 255, 0.2);
}

.sort-btn.active {
  background: rgba(255, 107, 107, 0.15);
  color: #FF6B6B;
  border-color: #FF6B6B;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.15);
}

/* === RESPONSIVE LOGIC for Sort UI === */
/* Default (Desktop >= 1024px): Show Desktop Sort, Hide Mobile Sort */
.desktop-sort {
  display: block;
}
.mobile-sort {
  display: none;
}

/* === MAIN CONTENT === */
.main-content {
  background: transparent;
}

.result-header {
  margin-bottom: 24px;
  background: #FFFFFF;  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.15);
  border: 1px solid #FFE4CC;
}

.result-title {
  font-size: 28px;
  font-weight: 800;
  color: #2D3436;
  margin: 0 0 8px 0;
}

.result-count {
  font-size: 16px;
  color: #636E72;
  margin: 0;
}

.result-count strong {
  color: #2D3436;
  font-weight: 700;
}

/* === STATE BOX === */
.state-box {
  background: #FFFFFF;  padding: 60px 20px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.15);
  border: 1px solid #FFE4CC;
  color: #2D3436;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(255, 107, 107, 0.15);
  border-top: 4px solid #60a5fa;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* === RESTAURANT CARD === */
.restaurant-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.restaurant-card {
  display: flex;
  background: #FFFFFF;  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.15);
  border: 1px solid #FFE4CC;
}

.restaurant-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 48px rgba(0,0,0,0.4);
  border-color: #FF6B6B;
  background: rgba(255, 255, 255, 0.12);
}

/* === IMAGE SECTION === */
.image-section {
  width: 300px;
  height: 200px;
  position: relative;
  overflow: hidden;
}

.image-section img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.35s ease;
}

.restaurant-card:hover img {
  transform: scale(1.08);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.05);
  pointer-events: none;
}

/* === INFO SECTION === */
.info-section {
  padding: 24px 28px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.header-area {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.restaurant-name {
  font-size: 22px;
  line-height: 1.3;
  font-weight: 700;
  color: #2D3436;
  margin: 0;
}

.category-badge {
  background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
  color: #2D3436;
  padding: 6px 14px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
}

.rating-area {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 10px 0 16px 0;
}

.rating-score {
  font-size: 20px;
  font-weight: 700;
  color: #2D3436;
}

.divider {
  color: rgba(255, 255, 255, 0.3);
}

.info-text {
  color: #636E72;
  font-size: 14px;
}

.address-area,
.phone-area {
  font-size: 15px;
  display: flex;
  gap: 6px;
  color: #636E72;
  margin-top: 8px;
}

.icon {
  font-size: 16px;
}

/* === RESPONSIVE === */
@media (max-width: 1400px) {
  .content-wrapper {
    grid-template-columns: 280px 1fr 260px;
    gap: 24px;
  }
}

@media (max-width: 1200px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }

  .sidebar {
    position: static;
  }

  .remote-control {
    display: none; /* 중간 크기에서는 리모컨 숨김 */
  }

  /* 화면이 작을 때 (1200px 미만) Sort UI 전환 */
  .desktop-sort {
    display: none;
  }
  .mobile-sort {
    display: flex;
    flex-direction: row;
  }

  .sort-btn {
    flex: 1;
    text-align: center;
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .restaurant-card {
    flex-direction: column;
  }

  .image-section {
    width: 100%;
    height: 240px;
  }

  .header-nav {
    display: none;
  }

  /* 모바일에서도 가로 배치 유지 */
  .mobile-sort {
    flex-direction: row;
  }
}

/* === REMOTE CONTROL SECTION === */
.remote-control {
  order: 2;
  flex-shrink: 0;
  margin-top: 16px;
  position: sticky;
  top: 100px;
  align-self: flex-start;
  max-height: calc(100vh - 120px);
}
</style>