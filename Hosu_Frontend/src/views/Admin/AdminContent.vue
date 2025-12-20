<template>
  <div class="content-wrapper">
    <div class="header-section">
      <h2 class="page-title">게시물 관리</h2>
      <p class="page-subtitle">전체 게시물을 조회하고 관리할 수 있습니다</p>
    </div>

    <!-- 필터 섹션 -->
    <div class="filter-section">
      <!-- 검색 -->
      <div class="search-row">
        <label class="section-label">게시물 검색</label>
        <div class="search-input-wrapper">
           <svg class="search-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
          </svg>
          <input
            type="text"
            v-model="searchQuery"
            placeholder="작성자, 식당명, 내용으로 검색..."
            class="search-input"
          />
        </div>
      </div>

      <!-- 날짜 필터 -->
      <div class="date-row">
        <div class="date-group">
          <label class="section-label">시작일</label>
          <input type="date" v-model="startDate" class="date-input" />
        </div>
        <div class="date-group">
          <label class="section-label">종료일</label>
          <input type="date" v-model="endDate" class="date-input" />
        </div>
      </div>

      <!-- 카테고리 필터 -->
      <div class="category-row">
        <label class="section-label">카테고리</label>
        <div class="category-list">
          <button 
            :class="['category-pill', { active: selectedCategory === 'ALL' }]"
            @click="selectedCategory = 'ALL'"
          >
            전체
          </button>
          <button 
            v-for="cat in categoryOptions" :key="cat.value"
            :class="['category-pill', { active: selectedCategory === cat.value }]"
            @click="selectedCategory = cat.value"
          >
            {{ cat.label }}
          </button>
        </div>
      </div>
    </div>

    <!-- 게시글 테이블 -->
    <div class="table-container">
      <table class="content-table">
        <thead>
          <tr>
            <th>게시글 ID</th>
            <th>작성자</th>
            <th>식당명</th>
            <th>카테고리</th>
            <th>작성일</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="post in filteredPosts" :key="post.boardId" class="table-row">
            <td>{{ post.boardId }}</td>
            <td class="writer-cell">{{ post.nickName }}</td>
            <td>{{ post.restaurantName }}</td>
            <td>
              <span class="category-badge">{{ getCategoryLabel(post.category) }}</span>
            </td>
            <td>{{ formatDate(post.createdAt) }}</td>
            <td class="action-cell">
               <button class="icon-btn view-btn" @click="openDetailDialog(post)" title="상세보기">
                 <svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path></svg>
               </button>
               <button class="icon-btn delete-btn" @click="confirmDeleteOnTable(post)" title="삭제">
                 <svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
               </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="filteredPosts.length === 0" class="empty-state">
        검색 결과가 없습니다.
      </div>
    </div>

    <!-- 통계 -->
    <div class="stats-grid">
      <div class="stat-card stat-green">
        <p class="stat-label">전체 게시물</p>
        <h3 class="stat-value">{{ filteredPosts.length }}개</h3>
      </div>

      <div class="stat-card stat-sky">
        <p class="stat-label">오늘의 게시물<span v-if="selectedCategory !== 'ALL'"> ({{ getCategoryLabel(selectedCategory) }})</span></p>
        <h3 class="stat-value">{{ todayCount }}개</h3>
      </div>
    </div>

    <!-- Detail Dialog -->
    <div v-if="showDetailDialog" class="dialog-overlay" @click.self="closeDetailDialog">
      <div class="dialog">
        <div class="dialog-header">
          <h2>게시글 상세</h2>
          <button class="close-btn" @click="closeDetailDialog">✕</button>
        </div>
        
        <div class="dialog-content">
          <div class="detail-section">
            <h3>{{ selectedDetailPost.title }} <span class="writer-info">by {{ selectedDetailPost.nickName }}</span></h3>
            <p class="post-meta">
              {{ selectedDetailPost.restaurantName }} · 
              <span class="category-tag">{{ getCategoryLabel(selectedDetailPost.category) }}</span> · 
              {{ formatDate(selectedDetailPost.createdAt) }}
            </p>
            <p class="post-content">{{ selectedDetailPost.content }}</p>
          </div>

          <div class="images-section" v-if="selectedDetailPost.images && selectedDetailPost.images.length > 0">
            <h4>포함된 메뉴 리뷰 ({{ selectedDetailPost.images.length }})</h4>
            <div class="images-grid">
              <div v-for="(img, idx) in selectedDetailPost.images" :key="img.imageId || idx" class="image-card">
                <div class="img-wrapper">
                  <img :src="img.imageUrl" class="review-img" alt="Review Image" />
                </div>
                <div class="img-info">
                  <p class="menu-name">{{ img.menuName }}</p>
                  <div class="rating-row">
                    <span class="star">★</span> {{ img.menuRating }}
                  </div>
                  <p class="one-line-review">"{{ img.oneLineReview }}"</p>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="no-images">
            이미지가 없는 게시글입니다.
          </div>
        </div>

        <div class="dialog-footer">
          <button class="btn btn-cancel" @click="closeDetailDialog">닫기</button>
          <button class="btn btn-delete-dialog" @click="confirmDeleteBoard">삭제</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import { getBoardAll, deleteBoard } from '@/api/sns';

export default {
  name: "AdminContent",
  data() {
    return {
      searchQuery: "",
      startDate: "",
      endDate: "",
      selectedCategory: "ALL",
      posts: [],
      
      // Detail Dialog
      showDetailDialog: false,
      selectedDetailPost: null,

      categoryOptions: [
        { label: '한식', value: 'KOREAN' },
        { label: '중식', value: 'CHINESE' },
        { label: '일식', value: 'JAPANESE' },
        { label: '양식', value: 'WESTERN' },
        { label: '분식', value: 'SNACK' },
        { label: '치킨', value: 'CHICKEN' },
        { label: '고기', value: 'MEAT' },
        { label: '해산물', value: 'SEAFOOD' },
        { label: '카페', value: 'CAFE' },
        { label: '술집', value: 'PUB' },
        { label: '베이커리', value: 'BAKERY' },
        { label: '패스트푸드', value: 'FASTFOOD' },
        { label: '아시안', value: 'ASIAN' },
        { label: '커리', value: 'CURRY' },
      ]
    }
  },
  async mounted() {
    await this.fetchPosts();
  },
  computed: {
    filteredPosts() {
      let result = this.posts;

      // 1. Search Query
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase();
        result = result.filter(p => 
          (p.nickName || '').toLowerCase().includes(query) ||
          (p.restaurantName || '').toLowerCase().includes(query) ||
          (p.content || '').toLowerCase().includes(query)
        );
      }

      // 2. Date Filter
      if (this.startDate) {
        result = result.filter(p => p.createdAt >= this.startDate);
      }
      if (this.endDate) {
         // Include the end date fully by comparing with next day or string compare
         result = result.filter(p => p.createdAt.startsWith(this.endDate) || p.createdAt < this.endDate);
         // Simple string comparison YYYY-MM-DD works if time is iso formatted
         // To be safe for inclusive end date:
         const end = new Date(this.endDate);
         end.setDate(end.getDate() + 1);
         const endStr = end.toISOString().slice(0, 10);
         result = result.filter(p => p.createdAt < endStr);
      }

      // 3. Category Filter
      if (this.selectedCategory !== 'ALL') {
        result = result.filter(p => p.category === this.selectedCategory);
      }

      return result;
    },
    todayCount() {
       const today = new Date().toISOString().slice(0, 10);
       return this.filteredPosts.filter(p => (p.createdAt || '').startsWith(today)).length;
    }
  },
  methods: {
    async fetchPosts() {
        try {
            const response = await getBoardAll();
            this.posts = response.data.content || response.data; 
        } catch (error) {
            console.error("게시글 목록 로딩 실패:", error);
        }
    },
    getCategoryLabel(code) {
      if (!code) return '-';
      const found = this.categoryOptions.find(c => c.value === code);
      return found ? found.label : code;
    },
    formatDate(dateString) {
        if (!dateString) return '-';
        return new Date(dateString).toLocaleDateString().replace(/\.$/, '');
    },
    openDetailDialog(post) {
      this.selectedDetailPost = post;
      this.showDetailDialog = true;
    },
    closeDetailDialog() {
      this.showDetailDialog = false;
      this.selectedDetailPost = null;
    },
    confirmDeleteOnTable(post) {
      if (confirm(`'${post.title || '게시글'}'을 삭제하시겠습니까?`)) {
          this.executeDelete(post.boardId, post.userNo);
      }
    },
    async confirmDeleteBoard() {
      if (!this.selectedDetailPost) return;
      if (confirm("정말 이 게시글을 삭제하시겠습니까?")) {
          await this.executeDelete(this.selectedDetailPost.boardId, this.selectedDetailPost.userNo);
          this.closeDetailDialog();
      }
    },
    async executeDelete(boardId, userNo) {
        try {
          await deleteBoard(boardId, userNo);
          alert("삭제되었습니다.");
          await this.fetchPosts();
        } catch (error) {
           console.error("삭제 실패:", error);
           alert("삭제에 실패했습니다.");
        }
    }
  }
}
</script>

<style scoped>
.content-wrapper {
  padding: 32px 40px;
  max-width: 1600px;
  margin: 0 auto;
}

.header-section { margin-bottom: 32px; }
.page-title { font-size: 28px; font-weight: 700; color: #111827; margin-bottom: 4px; }
.page-subtitle { color: #6B7280; font-size: 14px; }

/* Filter Section */
.filter-section {
  background: #F0FDF9;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.section-label {
  font-size: 14px; font-weight: 600; color: #374151; margin-bottom: 8px; display: block;
}

.search-row { margin-bottom: 20px; }
.search-input-wrapper { position: relative; width: 100%; }
.search-icon { position: absolute; left: 14px; top: 50%; transform: translateY(-50%); width: 20px; height: 20px; color: #9CA3AF; }
.search-input {
  width: 100%;
  padding: 12px 16px 12px 42px;
  border: 1px solid #E5E7EB; border-radius: 8px;
  font-size: 14px; background: #F9FAFB;
}
.search-input:focus { outline: none; border-color: #10B981;  background: white;}

.date-row {
  display: flex; gap: 24px; margin-bottom: 24px;
}
.date-group { flex: 1; }
.date-input {
  width: 100%; padding: 10px 14px;
  border: 1px solid #E5E7EB; border-radius: 8px;
  font-size: 14px;
}

.category-row { width: 100%; overflow: hidden; }
.category-list {
  display: flex; gap: 8px; overflow-x: auto; padding-bottom: 4px;
}
/* Hide scrollbar */
.category-list::-webkit-scrollbar { height: 4px; }
.category-list::-webkit-scrollbar-thumb { background: #E5E7EB; border-radius: 4px; }

.category-pill {
  white-space: nowrap;
  padding: 8px 18px;
  border-radius: 8px;
  border: 1px solid #E5E7EB;
  background: white;
  color: #374151;
  font-size: 14px; font-weight: 500;
  cursor: pointer; transition: all 0.2s;
}
.category-pill:hover { background: #F3F4F6; }
.category-pill.active {
  background: #10B981; color: white; border-color: #10B981;
  box-shadow: 0 2px 4px rgba(16, 185, 129, 0.2);
}


/* Table */
.table-container { 
  background: white; border-radius: 12px; 
  box-shadow: 0 1px 3px rgba(0,0,0,0.05); 
  overflow: hidden; 
  margin-bottom: 24px;
  border: 1px solid #E5E7EB;
}
.content-table { width: 100%; border-collapse: collapse; }
.content-table thead { background: #EFF6FF; } /* Slightly blueish header */
.content-table th {
  padding: 16px; font-size: 13px; font-weight: 600; color: #374151; text-align: center;
  border-bottom: 1px solid #E5E7EB;
}
.table-row { border-bottom: 1px solid #F3F4F6; transition: background 0.1s; }
.table-row:hover { background: #F9FAFB; }
.content-table td { padding: 14px 16px; font-size: 14px; color: #374151; text-align: center; vertical-align: middle; }

.writer-cell { font-weight: 600; }
.category-badge {
  padding: 4px 10px; background: #ECFDF5; color: #059669;
  border-radius: 9999px; font-size: 12px; font-weight: 600;
}

.action-cell {
  display: flex; justify-content: center; gap: 8px; align-items: center;
}
.icon-btn {
  width: 32px; height: 32px; border-radius: 6px; border: none; background: none;
  display: flex; align-items: center; justify-content: center; cursor: pointer;
  transition: background 0.2s;
}
.view-btn { color: #10B981; }
.view-btn:hover { background: #D1FAE5; }
.delete-btn { color: #EF4444; }
.delete-btn:hover { background: #FEE2E2; }
.icon-btn svg { width: 20px; height: 20px; }


/* Stats */
.stats-grid {
  display: flex; gap: 24px;
}
.stat-card {
  flex: 1; padding: 20px;
  border-radius: 8px; 
  display: flex; flex-direction: column; gap: 8px;
}
.stat-green { background: #ECFDF5; border: 1px solid #A7F3D0; }
.stat-sky { background: #EFF6FF; border: 1px solid #BFDBFE; }

.stat-label { font-size: 14px; color: #6B7280; font-weight: 500; }
.stat-value { font-size: 20px; font-weight: 700; color: #111827; }
.empty-state { padding: 40px; text-align: center; color: #9CA3AF; }


/* Dialog */
.dialog-overlay {
  position: fixed; top: 0; left: 0; width: 100vw; height: 100vh;
  background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000;
}
.dialog {
  background: white; width: 90%; max-width: 700px; max-height: 90vh;
  border-radius: 12px; display: flex; flex-direction: column;
  overflow: hidden;
}
.dialog-header {
  padding: 24px; 
  background: #10B981;
  border-bottom: none;
  display: flex; justify-content: space-between; align-items: center;
}
.dialog-header h2 { font-size: 18px; font-weight: 700; color: white; }
.close-btn { background: none; border: none; font-size: 24px; color: rgba(255,255,255,0.8); cursor: pointer; transition: color 0.2s; }
.close-btn:hover { color: white; }

.dialog-content { padding: 24px; overflow-y: auto; }
.detail-section { margin-bottom: 24px; }
.detail-section h3 { font-size: 18px; font-weight: 700; margin-bottom: 8px; }
.writer-info { font-weight: 400; color: #6B7280; font-size: 14px; margin-left: 8px; }
.post-meta { font-size: 13px; color: #6B7280; margin-bottom: 16px; }
.category-tag { font-weight: 600; color: #10B981; }
.post-content { background: #F9FAFB; padding: 16px; border-radius: 8px; white-space: pre-wrap; line-height: 1.6; }

.images-section h4 { font-size: 16px; font-weight: 600; margin-bottom: 12px; }
.images-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; }
.image-card { border: 1px solid #E5E7EB; border-radius: 8px; overflow: hidden; }
.img-wrapper { height: 160px; background: #F3F4F6; }
.review-img { width: 100%; height: 100%; object-fit: cover; }
.img-info { padding: 12px; }
.menu-name { font-weight: 600; margin-bottom: 4px; }
.rating-row { color: #F59E0B; font-weight: 700; font-size: 14px; margin-bottom: 6px; }
.one-line-review { font-size: 13px; color: #6B7280; font-style: italic; }

.dialog-footer {
  padding: 16px 24px; border-top: 1px solid #E5E7EB;
  display: flex; justify-content: flex-end; gap: 12px;
}
.btn { padding: 9px 18px; border-radius: 8px; font-weight: 600; font-size: 14px; cursor: pointer; border: none; }
.btn-cancel { background: #F3F4F6; color: #374151; }
.btn-delete-dialog { background: #EF4444; color: white; }
.btn-delete-dialog:hover { background: #DC2626; }
</style>
