<template>
  <div class="content-wrapper">
    <div class="header-section">
      <h2 class="page-title">사용자 관리</h2>
      <p class="page-subtitle">전체 사용자를 관리하고 권한을 설정할 수 있습니다</p>
    </div>

    <!-- 통계 대시보드 -->
    <div class="stats-grid">
      <div class="stat-card stat-total">
        <div class="stat-content">
          <p class="stat-label">전체 사용자</p>
          <h3 class="stat-value">{{ users.length }}</h3>
        </div>
        <div class="stat-icon">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
        </div>
      </div>

      <div class="stat-card stat-active">
        <div class="stat-content">
          <p class="stat-label">일반 사용자</p>
          <h3 class="stat-value text-green">{{ countUser }}</h3>
        </div>
        <div class="stat-icon icon-green">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
        </div>
      </div>

      <div class="stat-card stat-admin">
        <div class="stat-content">
          <p class="stat-label">관리자</p>
          <h3 class="stat-value text-yellow">{{ countAdmin }}</h3>
        </div>
        <div class="stat-icon icon-yellow">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path></svg>
        </div>
      </div>

      <div class="stat-card stat-ceo">
        <div class="stat-content">
          <p class="stat-label">사장님</p>
          <h3 class="stat-value text-red">{{ countCEO }}</h3>
        </div>
        <div class="stat-icon icon-red">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path></svg>
        </div>
      </div>
    </div>

    <!-- 검색 및 필터 바 -->
    <div class="control-bar">
      <div class="search-wrapper">
        <svg class="search-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
        </svg>
        <input
            type="text"
            v-model="searchQuery"
            placeholder="이름 또는 이메일로 검색..."
            class="search-input"
        />
      </div>

      <div class="filter-wrapper">
        <svg class="filter-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z"></path></svg>
        <select v-model="filterRole" class="role-select">
          <option value="ALL">모든 권한</option>
          <option value="ADMIN">관리자</option>
          <option value="CEO">사장님</option>
          <option value="USER">일반</option>
        </select>
      </div>
    </div>

    <!-- 사용자 그리드 -->
    <div v-if="loading" class="loading-state">
      <p>사용자 정보를 불러오는 중입니다...</p>
    </div>

    <div v-else-if="filteredUsers.length === 0" class="empty-state">
      <div class="empty-icon">
        <svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
      </div>
      <p>검색 결과가 없습니다.</p>
    </div>

    <div v-else class="user-grid">
      <div v-for="user in filteredUsers" :key="user.userNo" class="user-card">
        <div class="card-top">
          <span :class="['role-badge', getRoleBadgeClass(user.role)]">
            <svg class="role-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24" v-if="user.role === 'ADMIN'"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path></svg>
            <svg class="role-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24" v-else-if="user.role === 'CEO'"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path></svg>
            <svg class="role-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24" v-else><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
            {{ getRoleLabel(user.role) }}
          </span>
          <button class="more-btn">
            <svg fill="currentColor" viewBox="0 0 24 24"><path d="M12 13a1 1 0 100-2 1 1 0 000 2zm0-4a1 1 0 100-2 1 1 0 000 2zm0 8a1 1 0 100-2 1 1 0 000 2z"></path></svg>
          </button>
        </div>

        <div class="card-body">
          <img v-if="user.userImg" :src="user.userImg" class="avatar-img" alt="Profile" />
          <div v-else class="avatar-circle" :style="{ backgroundColor: getAvatarColor(user.nickname) }">
            {{ user.nickname.substring(0,2).toUpperCase() }}
          </div>
          <h3 class="user-nickname">{{ user.nickname }}</h3>
          <p class="user-email">{{ user.userEmail }}</p>
        </div>

        <div class="card-stats">
          <div class="stat-item">
            <svg class="stat-icon-mini" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"></path></svg>
            <span class="val">{{ user.reviewCount || 0 }}</span>
            <span class="lbl">리뷰</span>
          </div>
          <div class="stat-item">
             <svg class="stat-icon-mini" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path></svg>
            <span class="val">{{ user.followerCount || 0 }}</span>
            <span class="lbl">팔로워</span>
          </div>
        </div>

        <div class="card-footer">
          <p class="join-date">가입일: {{ formatDate(user.createdAt) }}</p>
          <button class="btn-role-setting" @click="openRoleModal(user)">
            권한 설정
          </button>
        </div>
      </div>
    </div>

    <!-- 권한 수정 모달 -->
    <div v-if="showRoleModal" class="modal-overlay" @click.self="closeRoleModal">
      <div class="modal-box">
        <div class="modal-header">
           <h3>권한 설정</h3>
           <button class="btn-close" @click="closeRoleModal">
             <svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
           </button>
        </div>
        
        <div class="modal-body">
            <div class="user-preview">
                <div class="avatar-circle small" :style="{ backgroundColor: getAvatarColor(targetUser?.nickname || '') }">
                    {{ targetUser?.nickname?.substring(0,2).toUpperCase() }}
                </div>
                <div>
                    <h4 class="preview-name">{{ targetUser?.nickname }}</h4>
                    <p class="preview-email">{{ targetUser?.userEmail }}</p>
                </div>
            </div>

            <p class="modal-instruction">변경할 권한을 선택해주세요.</p>
            
            <div class="role-selector">
            <label :class="['role-option', { selected: selectedRole === 'ADMIN' }]">
                <input type="radio" v-model="selectedRole" value="ADMIN" class="hidden-radio">
                <div class="role-content">
                    <span class="role-title">관리자 (ADMIN)</span>
                    <span class="role-desc">모든 시스템 권한을 가집니다.</span>
                </div>
                <div class="check-circle"></div>
            </label>
            <label :class="['role-option', { selected: selectedRole === 'CEO' }]">
                <input type="radio" v-model="selectedRole" value="CEO" class="hidden-radio">
                <div class="role-content">
                    <span class="role-title">사장님 (CEO)</span>
                    <span class="role-desc">식당 관리 및 메뉴 등록이 가능합니다.</span>
                </div>
                <div class="check-circle"></div>
            </label>
            <label :class="['role-option', { selected: selectedRole === 'USER' }]">
                <input type="radio" v-model="selectedRole" value="USER" class="hidden-radio">
                <div class="role-content">
                    <span class="role-title">일반 (USER)</span>
                    <span class="role-desc">기본적인 서비스 이용만 가능합니다.</span>
                </div>
                <div class="check-circle"></div>
            </label>
            </div>
        </div>

        <div class="modal-footer">
          <button @click="closeRoleModal" class="btn btn-cancel">취소</button>
          <button @click="confirmUpdateRole" class="btn btn-confirm">저장하기</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import { userRole, updateUserRole } from '@/api/user'

export default {
  name: "AdminUsers",
  data() {
    return {
      users: [],
      loading: false,
      searchQuery: "",
      filterRole: "ALL",

      // Modal
      showRoleModal: false,
      targetUser: null,
      selectedRole: 'USER'
    }
  },
  computed: {
    filteredUsers() {
      let result = this.users;

      // Filter by Role
      if (this.filterRole !== 'ALL') {
        result = result.filter(u => u.role === this.filterRole);
      }

      // Filter by Search
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase();
        result = result.filter(u => 
          u.nickname.toLowerCase().includes(query) || 
          u.userEmail.toLowerCase().includes(query)
        );
      }

      // Add mock reviewCount and followerCount if they don't exist
      // This should ideally be done when fetching data, not in a computed property that filters
      // However, following the provided instruction to insert this logic here.
      result = result.map(u => ({
          ...u,
          reviewCount: u.reviewCount !== undefined ? u.reviewCount : Math.floor(Math.random() * 50),
          followerCount: u.followerCount !== undefined ? u.followerCount : Math.floor(Math.random() * 200)
      }));

      return result;
    },
    countAdmin() {
      return this.users.filter(u => u.role === 'ADMIN').length;
    },
    countCEO() {
      return this.users.filter(u => u.role === 'CEO').length;
    },
    countUser() {
      return this.users.filter(u => u.role === 'USER').length;
    }
  },
  mounted() {
    this.fetchUsers();
  },
  methods: {
    async fetchUsers() {
      this.loading = true;
      try {
        const response = await userRole();
        // Assuming response.data is the list of users
        this.users = response.data || [];
        console.log(this.users);

      } catch (error) {
        console.error("Failed to fetch users:", error);
      } finally {
        this.loading = false;
      }
    },
    getRoleLabel(role) {
      const map = {
        'ADMIN': '관리자',
        'CEO': '사장님',
        'USER': '일반'
      };
      return map[role] || role;
    },
    getRoleBadgeClass(role) {
      if (role === 'ADMIN') return 'badge-admin';
      if (role === 'CEO') return 'badge-ceo';
      return 'badge-user';
    },
    getAvatarColor(name) {
      const colors = ['#10B981', '#3B82F6', '#6366F1', '#8B5CF6', '#EC4899', '#F59E0B'];
      if (!name) return colors[0];
      let hash = 0;
      for (let i = 0; i < name.length; i++) {
        hash = name.charCodeAt(i) + ((hash << 5) - hash);
      }
      return colors[Math.abs(hash) % colors.length];
    },
    formatDate(dateString) {
      if (!dateString) return '-';
      return new Date(dateString).toISOString().split('T')[0];
    },
    openRoleModal(user) {
      this.targetUser = user;
      this.selectedRole = user.role;
      this.showRoleModal = true;
    },
    closeRoleModal() {
      this.showRoleModal = false;
      this.targetUser = null;
    },
    async confirmUpdateRole() {
      if (!this.targetUser) return;

      const roleMap = { 'USER': 'U', 'ADMIN': 'A', 'CEO': 'C' };
      const roleCode = roleMap[this.selectedRole] || this.selectedRole;

      try {
        const response = await updateUserRole(this.targetUser.userNo, roleCode);
        if (response.data === 1) {
          this.targetUser.role = this.selectedRole;
          this.closeRoleModal();
          alert('권한이 변경되었습니다.');
          this.fetchUsers();
        } else {
          alert('변경 실패');
        }
      } catch (error) {
        console.error("Failed to update user role:", error);
        alert('변경 중 오류가 발생했습니다.');
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

/* Stats Dashboard */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid #E5E7EB;
}

.stat-total { border-left: 4px solid #6B7280; }
.stat-active { border-left: 4px solid #10B981; background: #F0FDF4; border-color: #BBF7D0; }
.stat-admin { border-left: 4px solid #F59E0B; background: #FFFBEB; border-color: #FDE68A; }
.stat-ceo { border-left: 4px solid #EF4444; background: #FEF2F2; border-color: #FECACA; }

.stat-content { display: flex; flex-direction: column; gap: 4px; }
.stat-label { font-size: 14px; color: #6B7280; font-weight: 500; }
.stat-value { font-size: 28px; font-weight: 700; color: #111827; }

.text-green { color: #059669; }
.text-yellow { color: #D97706; }
.text-red { color: #DC2626; }

.stat-icon {
  width: 48px; height: 48px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  color: #6B7280; background: #F3F4F6;
}
.stat-icon svg { width: 24px; height: 24px; }
.icon-green { color: #059669; background: #D1FAE5; }
.icon-yellow { color: #D97706; background: #FEF3C7; }
.icon-red { color: #DC2626; background: #FEE2E2; }


/* Controls */
.control-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: white;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #E5E7EB;
  gap: 16px;
  flex-wrap: wrap;
}

.search-wrapper {
  position: relative;
  flex: 1;
  min-width: 300px;
}
.search-icon {
  position: absolute; left: 12px; top: 50%; transform: translateY(-50%);
  width: 20px; height: 20px; color: #9CA3AF;
}
.search-input {
  width: 100%;
  padding: 12px 16px 12px 40px;
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  font-size: 14px;
  background: #F9FAFB;
  transition: all 0.2s;
}
.search-input:focus {
  background: white;
  border-color: #10B981;
  outline: none;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.filter-wrapper {
  position: relative;
  min-width: 180px;
}
.filter-icon {
    position: absolute; left: 12px; top: 50%; transform: translateY(-50%);
    width: 18px; height: 18px; color: #6B7280;
    pointer-events: none;
}
.role-select {
  width: 100%;
  padding: 12px 16px 12px 36px;
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  background: white;
  font-size: 14px;
  color: #374151;
  cursor: pointer;
}
.role-select:focus { border-color: #10B981; outline: none; }


/* Grid */
.user-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.user-card {
  background: white;
  border: 1px solid #E5E7EB;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.2s;
}
.user-card:hover { border-color: #A7F3D0; box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05); transform: translateY(-2px); }

.card-top {
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  background: linear-gradient(180deg, #ECFDF5 0%, #ffffff 100%);
  border-bottom: none;
}

.role-badge {
  display: flex; align-items: center; gap: 4px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px; font-weight: 600;
  color: white;
}
.role-icon { width: 14px; height: 14px; }
.badge-admin { background: #F59E0B; }
.badge-ceo { background: #3B82F6; }
.badge-user { background: #6B7280; }

.more-btn {
    color: #9CA3AF; background: none; border: none; cursor: pointer;
}
.more-btn svg { width: 20px; height: 20px; }

.card-body {
  padding: 24px 16px 16px;
  text-align: center;
}

.avatar-circle {
  width: 64px; height: 64px;
  border-radius: 50%;
  color: white;
  font-size: 24px; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  margin: 0 auto 16px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.user-nickname { font-size: 18px; font-weight: 700; color: #111827; margin-bottom: 4px; }
.user-email { font-size: 13px; color: #6B7280; }

.card-stats {
  display: flex;
  justify-content: space-around;
  padding: 16px 24px;
  border-bottom: 1px solid #F3F4F6;
}
.stat-item { display: flex; flex-direction: column; align-items: center; }
.stat-icon-mini { width: 16px; height: 16px; color: #10B981; margin-bottom: 4px; }
.val { font-size: 16px; font-weight: 700; color: #374151; }
.lbl { font-size: 12px; color: #9CA3AF; }

.card-footer {
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.join-date { font-size: 12px; color: #9CA3AF; }
.btn-role-setting {
  flex: 1;
  padding: 10px;
  margin-left: 12px;
  background: #10B981;
  border: none;
  border-radius: 8px;
  font-size: 14px; 
  font-weight: 600; 
  color: white;
  cursor: pointer; 
  transition: all 0.2s;
  box-shadow: 0 4px 6px -1px rgba(16, 185, 129, 0.2);
}
.btn-role-setting:hover { 
  background: #059669; 
  transform: translateY(-1px);
}

.avatar-img {
  width: 64px; height: 64px;
  border-radius: 50%;
  object-fit: cover;
  margin: 0 auto 16px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  display: block;
}


/* Modal */
.modal-overlay {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex; justify-content: center; align-items: center;
  z-index: 1000;
  backdrop-filter: blur(2px);
}
.modal-box {
  background: white; width: 90%; max-width: 440px;
  border-radius: 16px;
  box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1);
  overflow: hidden;
}
.modal-header {
  padding: 24px;
  background: #10B981;
  border-bottom: none;
  display: flex; justify-content: space-between; align-items: center;
}
.modal-header h3 { font-size: 18px; font-weight: 700; color: white; }
.btn-close { background: none; border: none; color: rgba(255,255,255,0.8); cursor: pointer; transition: color 0.2s; }
.btn-close:hover { color: white; }
.btn-close svg { width: 24px; height: 24px; }

.modal-body { padding: 24px; }
.user-preview {
  display: flex; align-items: center; gap: 16px;
  padding: 16px; background: #F9FAFB; border-radius: 12px;
  margin-bottom: 24px;
}
.avatar-circle.small { width: 48px; height: 48px; font-size: 18px; margin: 0; }
.preview-name { font-size: 16px; font-weight: 700; color: #111827; }
.preview-email { font-size: 13px; color: #6B7280; }

.modal-instruction { margin-bottom: 12px; font-size: 14px; font-weight: 600; color: #374151; }

.role-selector { display: flex; flex-direction: column; gap: 8px; }
.role-option {
  position: relative;
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px;
  border: 1px solid #E5E7EB; border-radius: 12px;
  cursor: pointer; transition: all 0.2s;
}
.role-option:hover { border-color: #10B981; background: #ECFDF5; }
.role-option.selected { border-color: #10B981; background: #ECFDF5; box-shadow: 0 0 0 1px #10B981; }

.hidden-radio { position: absolute; opacity: 0; }
.role-content { display: flex; flex-direction: column; gap: 2px; }
.role-title { font-size: 14px; font-weight: 700; color: #374151; }
.role-desc { font-size: 12px; color: #6B7280; }
.check-circle {
  width: 20px; height: 20px; border: 2px solid #D1D5DB; border-radius: 50%;
  position: relative;
}
.role-option.selected .check-circle { border-color: #10B981; background: #10B981; }
.role-option.selected .check-circle::after {
  content: ''; position: absolute; left: 5px; top: 2px; width: 6px; height: 10px;
  border: solid white; border-width: 0 2px 2px 0; transform: rotate(45deg);
}

.modal-footer {
  padding: 20px 24px;
  border-top: 1px solid #E5E7EB;
  display: flex; justify-content: flex-end; gap: 12px;
  background: #F9FAFB;
}
.btn-cancel {
  padding: 10px 20px; background: white; border: 1px solid #D1D5DB; border-radius: 8px;
  font-weight: 600; color: #374151; cursor: pointer;
}
.btn-confirm {
  padding: 10px 20px; background: #10B981; border: none; border-radius: 8px;
  font-weight: 600; color: white; cursor: pointer;
}
.btn-confirm:hover { background: #059669; }

/* Empty/Loading */
.empty-state, .loading-state {
    text-align: center; padding: 80px; color: #9CA3AF;
}
.empty-icon { width: 64px; height: 64px; margin: 0 auto 16px; color: #D1D5DB; }
</style>
