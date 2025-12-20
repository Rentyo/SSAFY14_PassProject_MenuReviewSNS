<template>
  <div class="admin-container">
    <!-- 사이드바 -->
    <aside :class="['sidebar', { 'sidebar-collapsed': !sidebarOpen }]">
      <!-- Header -->
      <div class="sidebar-header">
        <div class="logo-area">
          <div class="logo-icon">
            <svg class="icon-logo" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6V4m0 2a2 2 0 100 4m0-4a2 2 0 110 4m-6 8a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4m6 6v10m6-2a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4"></path>
            </svg>
          </div>
          <div v-if="sidebarOpen" class="logo-text">
            <h1 class="brand-title">HoSu Admin</h1>
            <span class="brand-subtitle">관리자 페이지</span>
          </div>
        </div>
      </div>
      
      <!-- Nav -->
      <nav class="sidebar-nav">
        <button
          v-for="item in menuItems"
          :key="item.id"
          @click="activeMenu = item.id"
          :class="['menu-item', { 'menu-item-active': activeMenu === item.id }]"
        >
          <div class="icon-wrapper">
             <!-- Inline SVGs based on ID -->
             <svg v-if="item.id === 'tags'" class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path></svg>
             <svg v-if="item.id === 'requests'" class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"></path></svg>
             <svg v-if="item.id === 'content'" class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>
             <svg v-if="item.id === 'users'" class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
          </div>
          <span v-if="sidebarOpen" class="menu-label">{{ item.label }}</span>
        </button>
      </nav>

      <!-- User Footer -->
      <div class="sidebar-footer" v-if="sidebarOpen">
        <div class="user-profile">
          <div class="avatar">A</div>
          <div class="user-info">
            <div class="user-name">관리자</div>
            <div class="user-email">admin@hosu.com</div>
          </div>
        </div>
      </div>
    </aside>

    <!-- 메인 -->
    <main class="main-content">
      <component :is="activeMenuComponent" />
      
      <!-- Placeholder for dashboards/settings if needed -->
      <div v-if="!activeMenuComponent" class="placeholder-view">
        <h2>{{ getMenuLabel(activeMenu) }}</h2>
        <p>준비 중인 페이지입니다.</p>
      </div>
    </main>
  </div>
</template>

<script>
import AdminUsers from './AdminUsers.vue'
import AdminRestaurants from './AdminRestaurants.vue'
import AdminContent from './AdminContent.vue'
import AdminMenuTags from './AdminMenuTags.vue'

export default {
  name: 'AdminDashboard',
  components: {
    AdminUsers,
    AdminRestaurants,
    AdminContent,
    AdminMenuTags
  },
  data() {
    return {
      sidebarOpen: true,
      activeMenu: 'requests', // Default to requests as per image context or user flow
      menuItems: [
        { id: 'tags', label: '메뉴 태그 관리' },
        { id: 'requests', label: '식당 등록 요청' },
        { id: 'content', label: '게시글 관리' },
        { id: 'users', label: '사용자 관리' },
      ]
    }
  },
  computed: {
    activeMenuComponent() {
      // Mapping new IDs to existing components
      // tags -> AdminMenuTags
      // requests -> AdminRestaurants
      // content -> AdminContent
      // users -> AdminUsers
      return {
        tags: 'AdminMenuTags',
        requests: 'AdminRestaurants',
        content: 'AdminContent',
        users: 'AdminUsers',
      }[this.activeMenu]
    }
  },
  methods: {
    getMenuLabel(id) {
      const item = this.menuItems.find(i => i.id === id);
      return item ? item.label : '';
    }
  }
}
</script>

<style scoped>
/* ==========================
   RESET & BASE
========================== */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.admin-container {
  display: flex;
  height: 100vh;
  width: 100vw;
  background: #f0fdf9; /* Light mint background for main area */
  font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif;
  color: #333;
}

/* ==========================
   SIDEBAR
========================== */
.sidebar {
  width: 260px;
  background: linear-gradient(180deg, #F3F8F7 0%, #EFF8F6 100%);
  border-right: 1px solid #E0E7E5;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  flex-shrink: 0;
}

.sidebar-collapsed {
  width: 80px;
}

/* HEADER */
.sidebar-header {
  padding: 32px 24px;
  display: flex;
  align-items: center;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: #10B981; /* Fallback */
  background: linear-gradient(135deg, #10B981, #06B6D4);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.icon-logo {
  width: 24px;
  height: 24px;
}

.brand-title {
  font-size: 18px;
  font-weight: 700;
  color: #10B981; /* Mint-ish Green */
  line-height: 1.2;
}

.brand-subtitle {
  font-size: 13px;
  color: #9CA3AF;
  font-weight: 500;
}

/* NAV */
.sidebar-nav {
  flex: 1;
  padding: 24px 16px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 16px;
  border: none;
  background: transparent;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #4B5563; /* Gray-600 */
  font-size: 15px;
  font-weight: 500;
  width: 100%;
  text-align: left;
}

.menu-item:hover {
  background: rgba(16, 185, 129, 0.05);
  color: #10B981;
}

.menu-item-active {
  background: linear-gradient(90deg, #10B981 0%, #06B6D4 100%);
  color: white !important;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.25);
  font-weight: 600;
}

.icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
}

.nav-icon {
  width: 22px;
  height: 22px;
}

/* FOOTER */
.sidebar-footer {
  padding: 24px;
  border-top: 1px solid #E5E7EB;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  background: #10B981;
  border-radius: 50%;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 18px;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.user-email {
  font-size: 12px;
  color: #9CA3AF;
}

/* ==========================
   MAIN CONTENT
========================== */
.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 0; /* Components typically handle their own padding, or add here if needed */
  background: white; /* Or keep transparent if bg is on container */
}

.placeholder-view {
  padding: 40px;
  text-align: center;
  color: #6B7280;
}
</style>
