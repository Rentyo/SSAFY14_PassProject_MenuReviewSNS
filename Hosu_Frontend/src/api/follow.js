import api from './index'



// 팔로우
export const follow = (myUserNo, targetUserNo) => {
    return api.post(`/follow/${myUserNo}/${targetUserNo}`)
}

// 언팔로우
export const unfollow = (myUserNo, targetUserNo) => {
    return api.delete(`/follow/${myUserNo}/${targetUserNo}`)
}

// 팔로우 상태 확인
export const getFollowStatus = (myUserNo, targetUserNo) => {
    return api.get(`/follow/${myUserNo}/status/${targetUserNo}`)
}
