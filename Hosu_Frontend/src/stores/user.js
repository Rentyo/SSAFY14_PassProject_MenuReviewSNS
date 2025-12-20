import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
    // State
    const userNo = ref(localStorage.getItem('userNo') || null)
    const userEmail = ref(localStorage.getItem('userEmail') || null)
    const nickname = ref(localStorage.getItem('nickname') || null)
    const role = ref(localStorage.getItem('role') || null)
    const userImg = ref(localStorage.getItem('userImg') || null)
    const accessToken = ref(localStorage.getItem('accessToken') || null)

    // User stats
    const followingCount = ref(parseInt(localStorage.getItem('followingCount') || '0') || 0)
    const followerCount = ref(parseInt(localStorage.getItem('followerCount') || '0') || 0)
    const reviewCount = ref(parseInt(localStorage.getItem('reviewCount') || '0') || 0)

    // Getters
    const isLoggedIn = computed(() => !!accessToken.value)
    const isCEO = computed(() => role.value === 'CEO')
    const isAdmin = computed(() => role.value === 'ADMIN')
    const isUser = computed(() => role.value === 'USER')

    // Actions
    function setUser(userData) {
        userNo.value = userData.userNo
        userEmail.value = userData.userEmail
        nickname.value = userData.nickname
        role.value = userData.role
        userImg.value = userData.userImg || null
        accessToken.value = userData.accessToken

        // User stats
        followingCount.value = userData.followingCount || 0
        followerCount.value = userData.followerCount || 0
        reviewCount.value = userData.reviewCount || userData.postCount || 0

        // localStorage에 저장
        localStorage.setItem('userNo', userData.userNo)
        localStorage.setItem('userEmail', userData.userEmail)
        localStorage.setItem('nickname', userData.nickname)
        localStorage.setItem('role', userData.role)
        localStorage.setItem('accessToken', userData.accessToken)
        localStorage.setItem('followingCount', userData.followingCount || 0)
        localStorage.setItem('followerCount', userData.followerCount || 0)
        localStorage.setItem('reviewCount', userData.reviewCount || userData.postCount || 0)

        if (userData.userImg) {
            localStorage.setItem('userImg', userData.userImg)
        }
    }

    function clearUser() {
        userNo.value = null
        userEmail.value = null
        nickname.value = null
        role.value = null
        userImg.value = null
        accessToken.value = null
        followingCount.value = 0
        followerCount.value = 0
        reviewCount.value = 0

        localStorage.clear()
    }

    function updateNickname(newNickname) {
        nickname.value = newNickname
        localStorage.setItem('nickname', newNickname)
    }

    function updateUserImg(newUserImg) {
        userImg.value = newUserImg
        localStorage.setItem('userImg', newUserImg)
    }

    function updateUserStats(stats) {
        if (stats.followingCount !== undefined) {
            followingCount.value = stats.followingCount
            localStorage.setItem('followingCount', stats.followingCount)
        }
        if (stats.followerCount !== undefined) {
            followerCount.value = stats.followerCount
            localStorage.setItem('followerCount', stats.followerCount)
        }
        if (stats.reviewCount !== undefined) {
            reviewCount.value = stats.reviewCount
            localStorage.setItem('reviewCount', stats.reviewCount)
        }
    }

    return {
        // State
        userNo,
        userEmail,
        nickname,
        role,
        userImg,
        accessToken,
        followingCount,
        followerCount,
        reviewCount,

        // Getters
        isLoggedIn,
        isCEO,
        isAdmin,
        isUser,

        // Actions
        setUser,
        clearUser,
        updateNickname,
        updateUserImg,
        updateUserStats,
    }
})
