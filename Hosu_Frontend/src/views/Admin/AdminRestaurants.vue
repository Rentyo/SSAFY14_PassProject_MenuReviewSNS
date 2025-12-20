<template>
  <div class="content-wrapper">
    <!-- Header: Title & View Toggle -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">식당 등록 요청 관리</h1>
        <p class="page-subtitle">총 {{ totalCount }}개의 요청</p>
      </div>
      <div class="view-toggle">
        <button 
          :class="['toggle-btn', { active: viewMode === 'card' }]" 
          @click="viewMode = 'card'"
        >
          <svg class="toggle-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path></svg>
          카드 뷰
        </button>
        <button 
          :class="['toggle-btn', { active: viewMode === 'table' }]" 
          @click="viewMode = 'table'"
        >
          <svg class="toggle-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path></svg>
          테이블 뷰
        </button>
      </div>
    </div>

    <!-- Summary Dashboard (Filters) -->
    <div class="summary-dashboard">
      <div class="summary-card" :class="{ active: currentFilter === 'ALL' }" @click="setFilter('ALL')">
        <div class="summary-label">전체 요청</div>
        <div class="summary-count">{{ allCount }}</div>
      </div>
      <div class="summary-card warning" :class="{ active: currentFilter === 'PENDING' }" @click="setFilter('PENDING')">
        <div class="summary-label">대기중</div>
        <div class="summary-count">{{ penCount }}</div>
      </div>
      <div class="summary-card success" :class="{ active: currentFilter === 'APPROVED' }" @click="setFilter('APPROVED')">
        <div class="summary-label">승인됨</div>
        <div class="summary-count">{{ appCount }}</div>
      </div>
    </div>

    <!-- Content Area -->
    <div class="content-container">
      
      <!-- CARD VIEW -->
      <div v-if="viewMode === 'card'" class="card-grid">
        <div v-if="requests.length === 0" class="no-data">
            해당 상태의 요청이 없습니다.
        </div>
        <div 
          v-else
          v-for="req in requests" 
          :key="req.reqId" 
          class="request-card"
        >
          <div class="card-header">
            <h3 class="card-title">{{ req.restaurantName }}</h3>
            <span :class="['card-badge', getStatusClass(req.isApprove)]">{{ getStatusText(req.isApprove) }}</span>
          </div>
          
          <div class="card-body">
            <div class="category-tag">{{ req.category }}</div>
            
            <div class="info-row" title="주소">
              <svg class="info-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path></svg>
              <span>{{ req.address }}</span>
            </div>
            
            <div class="info-row" title="회원 번호">
              <svg class="info-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
              <span>#{{ req.userNo || '번호 없음' }}</span> 
            </div>

            <div class="info-row" title="신청일">
              <svg class="info-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>
              <span>{{ formatDate(req.date) }}</span>
            </div>
          </div>

          <div class="card-footer" v-if="req.isApprove === 0">
            <button class="btn btn-card-approve" @click="openApproveDialog(req)">
                <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
                승인
            </button>
            <button class="btn btn-card-reject" @click="handleReject(req.reqId)">
                <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
                거부
            </button>
          </div>
        </div>
      </div>

      <!-- TABLE VIEW -->
      <div v-else class="table-container">
        <table class="request-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>식당명</th>
              <th>카테고리</th>
              <th>위치</th>
              <th>회원 번호</th>
              <th>신청일</th>
              <th>상태</th>
              <th>작업</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading" class="loading-row">
              <td colspan="8">로딩 중...</td>
            </tr>
            <tr v-else-if="requests.length === 0" class="empty-row">
              <td colspan="8">해당 상태의 요청이 없습니다.</td>
            </tr>
            <tr 
              v-else
              v-for="(req, index) in requests"
              :key="req.reqId" 
              class="table-row"
            >
              <td class="id-col">#{{ req.reqId }}</td>
              <td class="name-col">{{ req.restaurantName }}</td>
              <td><span class="category-badge">{{ req.category }}</span></td>
              <td class="address-col">{{ req.address }}</td>
              <td>#{{ req.userNo || '-' }}</td> <!-- Phone data check -->
              <td>{{ formatDate(req.date) }}</td>
              <td>
                <span :class="['status-badge', getStatusClass(req.isApprove)]">
                  {{ getStatusText(req.isApprove) }}
                </span>
              </td>
              <td>
                <div class="action-buttons" v-if="req.isApprove === 0">
                  <button class="icon-btn btn-check" @click="openApproveDialog(req)" title="승인">
                     <svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
                  </button>
                  <button class="icon-btn btn-x" @click="handleReject(req.reqId)" title="거부">
                     <svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
                  </button>
                </div>
                <div v-else class="action-buttons disabled">
                    <button class="icon-btn disabled">
                        <svg class="dots-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z"></path></svg>
                    </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 승인 확인 및 주소 검증 다이얼로그 (기존 기능 유지) -->
    <div v-if="showApprovalDialog" class="dialog-overlay">
      <div class="dialog">
        <div class="dialog-header">
          <h2>식당 승인 확인</h2>
        </div>
        
        <div class="dialog-content">
          <div class="info-section">
            <h3>요청 정보</h3>
            <div class="info-row"><strong>식당명:</strong> {{ selectedRequest.restaurantName }}</div>
            <div class="info-row"><strong>주소:</strong> {{ selectedRequest.address }}</div>
            <div class="info-row"><strong>카테고리:</strong> {{ selectedRequest.category }}</div>
            <div class="info-row"><strong>회원번호:</strong> {{ selectedRequest.userNo }}</div>
            <div class="info-row"><strong>날짜:</strong> {{ formatDate(selectedRequest.date) }}</div>
          </div>

          <div class="location-section">
            <h3>📍 위치 정보 선택 (Kakao API)</h3>
            <p class="section-desc">요청 주소 기반 검색 결과입니다. <strong>실제 매칭되는 위치를 선택해주세요.</strong></p>
            
            <div v-if="checkingLocation" class="checking-msg">위치 정보를 조회 중입니다...</div>
            <div v-else-if="candidateLocations.length === 0" class="no-location-msg">
              ⚠️ 검색된 위치 정보가 없습니다. 주소를 다시 확인해주세요.
            </div>
            
            <ul v-else class="location-list">
              <li 
                v-for="(loc, index) in candidateLocations" 
                :key="index" 
                class="location-item"
                :class="{ 'selected': selectedLocation === loc }"
                @click="selectedLocation = loc"
              >
                <div class="loc-name">{{ loc.addressName }}</div>
                <div class="loc-detail">
                  {{ loc.sidoName }} {{ loc.gunguName }} / 상세: {{ loc.placeName || '-' }} / 📞 {{ loc.phone || '번호없음' }}
                </div>
              </li>
            </ul>
          </div>
        </div>

        <div class="dialog-footer">
          <button class="btn btn-cancel" @click="closeDialog">취소</button>
          <button class="btn btn-reject-modal" @click="confirmReject">거절</button>
          <button class="btn btn-approve-modal" @click="confirmApprove">최종 승인</button>
        </div>
      </div>
    </div>
    
    <!-- Pagination -->
    <div v-if="totalPages > 0" class="pagination-wrapper">
      <div class="pagination-info">
        <select v-model="pageSize" @change="handleSizeChange" class="size-select">
          <option :value="10">10개씩 보기</option>
          <option :value="15">15개씩 보기</option>
          <option :value="20">20개씩 보기</option>
        </select>
      </div>

      <div class="pagination">
        <button 
          class="page-btn prev" 
          :disabled="currentPage === 0" 
          @click="goToPage(currentPage - 1)"
        >
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path></svg>
        </button>

        <button 
          v-for="p in visiblePages" 
          :key="p" 
          class="page-btn" 
          :class="{ active: currentPage === p - 1 }"
          @click="goToPage(p - 1)"
        >
          {{ p }}
        </button>

        <button 
          class="page-btn next" 
          :disabled="currentPage === totalPages - 1" 
          @click="goToPage(currentPage + 1)"
        >
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path></svg>
        </button>
      </div>
    </div>

  </div>
</template>

<script>
import { getRestaurantRequests, approveRestaurant, rejectRestaurant } from '@/api/admin';
import { checkRestaurantLocation, insertRestaurant } from '@/api/restaurants';

export default {
  name: 'AdminRestaurants',
  data() {
    return {
      requests: [],
      loading: false,
      
      // Pagination State
      currentPage: 0,
      pageSize: 10,
      totalCount: 0,
      totalPages: 0,
      allCount: 0,
      penCount: 0,
      appCount: 0,

      // Dialog State
      showApprovalDialog: false,
      selectedRequest: null,
      candidateLocations: [],
      selectedLocation: null, 
      checkingLocation: false,
      
      currentFilter: 'ALL', // ALL, PENDING, APPROVED
      viewMode: 'card', // 'card' or 'table'
    }
  },
  computed: {
    visiblePages() {
      const groupSize = 10;
      const currentGroup = Math.floor(this.currentPage / groupSize);
      const start = currentGroup * groupSize + 1;
      const end = Math.min(start + groupSize - 1, this.totalPages);
      
      const pages = [];
      for (let i = start; i <= end; i++) {
        pages.push(i);
      }
      return pages;
    }
  },
  async mounted() {
    await this.fetchRequests();
  },
  methods: {
    async fetchRequests() {
      this.loading = true;
      try {
        const response = await getRestaurantRequests(this.currentPage, this.pageSize, this.currentFilter);
        const { content, totalCount, totalPages, allCount, penCount, appCount } = response.data;
        
        this.requests = content || [];
        this.totalCount = totalCount || 0;
        this.totalPages = totalPages || 0;
        
        this.allCount = allCount || 0;
        this.penCount = penCount || 0;
        this.appCount = appCount || 0;

      } catch (error) {
        console.error('요청 목록을 불러오는데 실패했습니다:', error);
      } finally {
        this.loading = false;
      }
    },

    setFilter(filter) {
      if (this.currentFilter === filter) return;
      this.currentFilter = filter;
      this.currentPage = 0; // 필터 변경 시 첫 페이지로
      this.fetchRequests();
    },

    goToPage(page) {
      if (page < 0 || page >= this.totalPages) return;
      this.currentPage = page;
      this.fetchRequests();
    },

    handleSizeChange() {
      this.currentPage = 0; // 사이즈 변경 시 첫 페이지로
      this.fetchRequests();
    },

    // 1단계: 승인 버튼 클릭 -> 다이얼로그 오픈 및 위치 검색
    async openApproveDialog(request) {
      this.selectedRequest = request;
      this.showApprovalDialog = true;
      this.candidateLocations = [];
      this.selectedLocation = null;
      this.checkingLocation = true;

      try {
        const keyword = request.address;
        const response = await checkRestaurantLocation(keyword);
        this.candidateLocations = response.data || [];
      } catch (error) {
        console.error("위치 검색 실패:", error);
        this.candidateLocations = [];
      } finally {
        this.checkingLocation = false;
      }
    },

    closeDialog() {
      this.showApprovalDialog = false;
      this.selectedRequest = null;
      this.candidateLocations = [];
      this.selectedLocation = null;
    },

    // 2단계: 최종 승인
    async confirmApprove() {
      if (!this.selectedRequest) return;
      if (!this.selectedLocation) {
        alert("일치하는 위치 정보를 선택해주세요.");
        return;
      }
      try {
        const payload = {
          name: this.selectedLocation.placeName, 
          address: this.selectedLocation.addressName,
          category: this.selectedRequest.category,
          totalRating: 0,
          totalContents: 0,
          viewCount: 0,
          reviewCounts : 0,
          sidoNo: this.selectedLocation.sidoNo,
          gunguCode: this.selectedLocation.gunguCode,
          lat: this.selectedLocation.lat, 
          lng: this.selectedLocation.lng,
          phone: this.selectedLocation.phone, 
          userNo: this.selectedRequest.userNo,
          mainImage: null,
        };

        const insertResponse = await insertRestaurant(payload);
        
        if (insertResponse.status === 200) {
            const id = this.selectedRequest.reqId;
            const approveResponse = await approveRestaurant(id);
            
            if (approveResponse.data === 1) {
              alert('식당이 등록되고 승인 처리되었습니다.');
              const req = this.requests.find(r => r.reqId === id);
              if (req) req.isApprove = 1;
              this.closeDialog();
            } else {
              alert('식당은 등록되었으나, 요청 승인 상태 업데이트에 실패했습니다.');
            }
        } else {
             alert('식당 등록에 실패했습니다.');
        }
      } catch (error) {
        console.error('최종 승인 처리 실패:', error);
        alert('처리 중 오류가 발생했습니다.');
      } finally{
        this.fetchRequests(); // Refresh data to be sure
      }
    },

    // 2단계: 거절
    async confirmReject() {
      if (!this.selectedRequest) return;
      await this.handleReject(this.selectedRequest.reqId);
      this.closeDialog(); 
    },

    async handleReject(id) {
      if (!confirm('정말 거절하시겠습니까?')) return;
      try {
        const response = await rejectRestaurant(id);
        if (response.data === -1) {
          alert('거절되었습니다.');
          const req = this.requests.find(r => r.reqId === id);
          if (req) req.isApprove = -1;
        } else {
          alert('거절 처리에 실패했습니다.');
        }
      } catch (error) {
        console.error('거절 실패:', error);
        alert('거절 처리에 실패했습니다.');
      }
    },

    formatDate(dateString) {
      if (!dateString) return '-';
      const date = new Date(dateString);
      return date.toISOString().split('T')[0];
    },
    getStatusText(status) {
      if (status === 1) return '승인됨';
      if (status === 0) return '대기중';
      if (status === -1) return '거부됨';
      return '-';
    },
    getStatusClass(status) {
      if (status === 1) return 'status-approved';
      if (status === 0) return 'status-pending';
      if (status === -1) return 'status-rejected';
      return '';
    }
  }
}
</script>

<style scoped>
/* GENERAL */
.content-wrapper {
  padding: 32px 40px;
  max-width: 1600px;
  margin: 0 auto;
}

/* HEADER */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 4px;
}

.page-subtitle {
  color: #6B7280;
  font-size: 14px;
}

.view-toggle {
  display: flex;
  gap: 8px;
  background: white; /* or #F3F4F6 */
}

.toggle-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid #E5E7EB;
  background: white;
  color: #6B7280;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.toggle-btn:hover {
  background: #F9FAFB;
}

.toggle-btn.active {
  background: #10B981;
  border-color: #10B981;
  color: white;
}

.toggle-icon {
  width: 16px;
  height: 16px;
}


/* SUMMARY DASHBOARD */
.summary-dashboard {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
}

.summary-card {
  flex: 1;
  background: white;
  border: 1px solid #E5E7EB;
  border-radius: 12px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 200px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
}

.summary-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.summary-card.active {
  border-width: 2px;
}

/* Status variants for Summary Card */
/* Default (ALL) */
.summary-card:first-child.active {
   border-color: #3B82F6;
}

/* Warning (Pending) */
.summary-card.warning {
  background: #FFFBEB; /* Default light yellow */
  border-color: #FDE68A;
}
.summary-card.warning.active {
  border-color: #F59E0B;
  background: #FCD34D; /* Darker yellow when active */
  color: #78350F;
}
.summary-card.warning.active .summary-label,
.summary-card.warning.active .summary-count {
  color: #78350F;
}

/* Success (Approved) */
.summary-card.success {
  background: #F0FDF4; /* Default light green */
  border-color: #BBF7D0;
}
.summary-card.success.active {
  border-color: #10B981;
  background: #6EE7B7; /* Darker green */
  color: #064E3B;
}
.summary-card.success.active .summary-label,
.summary-card.success.active .summary-count {
  color: #064E3B;
}

.summary-card.success.active .summary-label,
.summary-card.success.active .summary-count {
  color: #064E3B;
}

.summary-label {
  font-size: 14px;
  color: #6B7280;
  font-weight: 500;
  margin-bottom: 12px;
}

.summary-card.active .summary-label {
  color: #374151; /* Darker when active */
}

.summary-count {
  font-size: 36px;
  font-weight: 700;
  color: #111827;
  line-height: 1;
}

/* Status colors for count */
.summary-card.warning .summary-count { color: #D97706; }
.summary-card.success .summary-count { color: #059669; }
.summary-card.danger .summary-count { color: #DC2626; }


/* CONTENT AREA */

/* Card Grid */
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.no-data {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px;
  color: #9CA3AF;
  font-size: 16px;
  background: white;
  border-radius: 12px;
  border: 1px dashed #E5E7EB;
}

.request-card {
  background: white;
  border: 1px solid #E5E7EB;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.2s;
}

.request-card:hover {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin: -24px -24px 20px -24px;
  padding: 24px;
  background: linear-gradient(180deg, #ECFDF5 0%, #ffffff 100%);
  border-radius: 16px 16px 0 0;
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: #1F2937;
  margin: 0;
  line-height: 1.4;
}

.card-badge {
  font-size: 12px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 6px;
  white-space: nowrap;
  margin-left: 8px;
}

.card-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.category-tag {
  align-self: flex-start;
  background: #EFF6FF;
  color: #3B82F6;
  font-size: 13px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 4px;
  margin-bottom: 8px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #6B7280;
}

.info-icon {
  width: 18px;
  height: 18px;
  color: #9CA3AF;
  flex-shrink: 0;
}

.card-footer {
  display: flex;
  gap: 12px;
  margin-top: auto;
}

.btn-card-approve {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 8px;
  background: #10B981;
  color: white;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  transition: background 0.2s;
}

.btn-card-approve:hover {
  background: #059669;
}

.btn-card-reject {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 8px;
  background: #F43F5E;
  color: white;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  transition: background 0.2s;
}

.btn-card-reject:hover {
  background: #E11D48;
}

.btn-icon {
  width: 16px;
  height: 16px;
}

/* TABLE VIEW REFINEMENTS */
.table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  border: 1px solid #E5E7EB;
}

.request-table {
  width: 100%;
  border-collapse: collapse;
}

.request-table th {
  background: #d6fada;
  padding: 16px 24px;
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 1px solid #E5E7EB;
}

.request-table td {
  padding: 16px 24px;
  border-bottom: 1px solid #F3F4F6;
  color: #4B5563;
  font-size: 14px;
  vertical-align: middle;
}

.id-col { color: #9CA3AF; font-size: 13px; }
.name-col { font-weight: 600; color: #111827; }

.status-approved { background: #ECFDF5; color: #10B981; }
.status-pending { background: #FFFBEB; color: #F59E0B; }
.status-rejected { background: #FEF2F2; color: #EF4444; }

/* Table Action Buttons */
.action-buttons {
  display: flex;
  gap: 8px;
}

.icon-btn {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.icon-btn svg {
  width: 18px;
  height: 18px;
}

.btn-check {
  background: #10B981;
  color: white;
}
.btn-check:hover { background: #059669; }

.btn-x {
  background: #EF4444;
  color: white;
}
.btn-x:hover { background: #DC2626; }

.action-buttons.disabled .icon-btn {
    background: transparent;
    color: #9CA3AF;
    cursor: default;
}
.dots-icon {
    width: 20px;
    height: 20px;
}

/* DIALOG (Modal) Styles - Keeping functional */
.dialog-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100vw; height: 100vh;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dialog {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden; /* Ensure header radius is respected */
}

.dialog-header {
  padding: 24px;
  background: #10B981;
  border-bottom: none;
}
.dialog-header h2 { margin: 0; font-size: 20px; font-weight: 700; color: white; }

.dialog-content { padding: 24px; flex: 1; overflow-y: auto; }

.info-section {
  background: #F9FAFB;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 24px;
}
.info-section h3 { margin: 0 0 12px 0; font-size: 15px; font-weight: 600; color: #374151; }
.info-row { margin-bottom: 8px; color: #4B5563; }
.info-row strong { color: #111827; width: 80px; display: inline-block; font-weight: 600; }

.btn-approve-modal {
  padding: 10px 20px;
  background: #10B981;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}
.btn-reject-modal {
  padding: 10px 20px;
  background: #EF4444;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}
.btn-cancel {
  padding: 10px 20px;
  background: white;
  border: 1px solid #D1D5DB;
  color: #374151;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}

/* Location List in Dialog */
.location-list {
  list-style: none; padding: 0; margin: 0;
  border: 1px solid #E5E7EB; border-radius: 8px;
  max-height: 200px; overflow-y: auto;
}
.location-item {
  padding: 12px; border-bottom: 1px solid #E5E7EB; cursor: pointer;
}
.location-item:last-child { border-bottom: none; }
.location-item:hover { background: #F9FAFB; }
.location-item.selected { background: #EFF6FF; border-left: 4px solid #3B82F6; }
.loc-name { font-weight: 600; color: #111827; font-size: 14px; }
.loc-detail { font-size: 12px; color: #6B7280; margin-top: 2px; }

.dialog-footer {
  padding: 24px;
  border-top: 1px solid #E5E7EB;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* PAGINATION */
.pagination-wrapper {
  margin-top: 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-top: 1px solid #E5E7EB;
}

.size-select {
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #D1D5DB;
  background: white;
  color: #374151;
  font-size: 14px;
  outline: none;
  cursor: pointer;
}

.pagination {
  display: flex;
  gap: 8px;
}

.page-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  border: 1px solid #E5E7EB;
  background: white;
  color: #374151;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  background: #F3F4F6;
  border-color: #D1D5DB;
}

.page-btn.active {
  background: #10B981;
  border-color: #10B981;
  color: white;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-btn svg {
  width: 20px;
  height: 20px;
}
</style>