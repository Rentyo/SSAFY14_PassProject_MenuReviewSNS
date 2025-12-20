import { createRouter, createWebHistory } from 'vue-router'
import HoeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import ListView from '../views/ListView.vue'
import AdminDashboard from '../views/Admin/AdminDashboard.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => import('../views/SignupView.vue'),
    },
    {
      path: '/mypage',
      name: 'mypage',
      component: () => import('../views/MyPageView.vue'),
      beforeEnter: (to, from, next) => {
        const role = localStorage.getItem('role')
        // CEO 사용자는 CEO 마이페이지로 리다이렉트
        if (role === 'CEO') {
          next('/mypage/ceo')
        } else {
          next()
        }
      },
    },
    {
      path: '/mypage/ceo',
      name: 'ceo-mypage',
      component: () => import('../views/CeoMyPageView.vue'),
      beforeEnter: (to, from, next) => {
        const role = localStorage.getItem('role')
        // CEO가 아닌 사용자는 일반 마이페이지로 리다이렉트
        if (role !== 'CEO') {
          next('/mypage')
        } else {
          next()
        }
      },
    },
    {
      path: '/review/:id',
      name: 'review-detail',
      component: () => import('../views/ReviewDetailView.vue'),
    },
    {
      path: '/feed',
      name: 'following-feed',
      component: () => import('../views/FollowingFeedView.vue'),
      beforeEnter: (to, from, next) => {
        const userNo = localStorage.getItem('userNo')
        if (!userNo) {
          next('/login')
        } else {
          next()
        }
      },
    },
    {
      path: '/profile/:userNo',
      name: 'user-profile',
      component: () => import('../views/UserProfileView.vue'),
    },
    {
      path: '/mypage/edit',
      name: 'user-edit',
      component: () => import('../views/UserEditView.vue'),
      beforeEnter: (to, from, next) => {
        const role = localStorage.getItem('role')
        // CEO는 CEO 정보수정 페이지로 리다이렉트
        if (role === 'CEO') {
          next('/mypage/ceo/edit')
        } else {
          next()
        }
      },
    },
    {
      path: '/mypage/ceo/edit',
      name: 'ceo-edit',
      component: () => import('../views/CeoEditView.vue'),
      beforeEnter: (to, from, next) => {
        const role = localStorage.getItem('role')
        // CEO가 아니면 일반 정보수정 페이지로 리다이렉트
        if (role !== 'CEO') {
          next('/mypage/edit')
        } else {
          next()
        }
      },
    },
    {
      path: '/list', // 추가
      name: 'list', // 추가
      component: ListView, // 추가
    },
    {
      path: '/admin', // 추가
      name: 'admin', // 추가
      component: AdminDashboard, // 추가
      beforeEnter: (to, from, next) => {
        const role = localStorage.getItem('role')
        if (role !== 'ADMIN') {
          // 관리자가 아니면 홈으로 리다이렉트 (또는 알림 표시)
          alert("관리자 권한이 필요합니다.")
          next('/')
        } else {
          next()
        }
      },
    },
    {
      path: '/restaurant/register',
      name: 'restaurant-register',
      component: () => import('../views/RestaurantRegisterView.vue'),
      beforeEnter: (to, from, next) => {
        const role = localStorage.getItem('role')
        // CEO만 접근 가능
        if (role !== 'CEO') {
          next('/mypage')
        } else {
          next()
        }
      },
    },
    {
      path: '/restaurant/:id',
      name: 'restaurant-detail',
      component: () => import('../views/RestaurantDetail.vue'),
    },
    {
      path: '/restaurant/:id/review/create',
      name: 'review-create',
      component: () => import('../views/ReviewCreateView.vue'),
      beforeEnter: (to, from, next) => {
        const userNo = localStorage.getItem('userNo')
        if (!userNo) {
          alert('로그인이 필요합니다.')
          next('/login')
        } else {
          next()
        }
      },
    },
    {
      path: '/review/:id/edit',
      name: 'review-edit',
      component: () => import('../views/ReviewEditView.vue'),
      beforeEnter: (to, from, next) => {
        const userNo = localStorage.getItem('userNo')
        if (!userNo) {
          alert('로그인이 필요합니다.')
          next('/login')
        } else {
          next()
        }
      },
    },
    {
      path: '/notice/create',
      name: 'notice-create',
      component: () => import('../views/NoticeCreateView.vue'),
      beforeEnter: (to, from, next) => {
        const role = localStorage.getItem('role')
        // CEO만 접근 가능
        if (role !== 'CEO') {
          alert('CEO 권한이 필요합니다.')
          next('/mypage')
        } else {
          next()
        }
      },
    },
    {
      path: '/notice/:id/edit',
      name: 'notice-edit',
      component: () => import('../views/NoticeEditView.vue'),
      beforeEnter: (to, from, next) => {
        const role = localStorage.getItem('role')
        // CEO만 접근 가능
        if (role !== 'CEO') {
          alert('CEO 권한이 필요합니다.')
          next('/mypage')
        } else {
          next()
        }
      },
    },
    {
      path: '/menu/add',
      name: 'menu-add',
      component: () => import('../views/MenuAddView.vue'),
      beforeEnter: (to, from, next) => {
        const role = localStorage.getItem('role')
        // CEO만 접근 가능
        if (role !== 'CEO') {
          alert('CEO 권한이 필요합니다.')
          next('/mypage')
        } else {
          next()
        }
      },
    },
    {
      path: '/menu/:id/edit',
      name: 'menu-edit',
      component: () => import('../views/MenuEditView.vue'),
      beforeEnter: (to, from, next) => {
        const role = localStorage.getItem('role')
        // CEO만 접근 가능
        if (role !== 'CEO') {
          alert('CEO 권한이 필요합니다.')
          next('/mypage')
        } else {
          next()
        }
      },
    },
  ],
})

export default router
