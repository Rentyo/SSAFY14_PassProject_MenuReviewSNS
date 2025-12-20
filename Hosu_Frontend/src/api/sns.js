import api from './index'
// 전체 게시글 조회
export const getBoardAll = () => {
  return api.get('/sns/boards', {
    params: {
      page: 0,
      size: 10000
    }
  })
}

// 사용자 게시글 목록 조회
export const getUserBoards = (userNo, myUserNo = null) => {
  const params = myUserNo ? { myUserNo } : {}
  return api.get(`/sns/boards/user/${userNo}`, { params })
}

// 게시글 상세 조회
export const getBoardDetail = (boardId, myUserNo = null) => {
  const params = myUserNo ? { myUserNo } : {}
  return api.get(`/sns/boards/${boardId}`, { params })
}
export const getComments = (boardId) => {
  return api.get(`/sns/comments/board/${boardId}`)
}

// 댓글 작성
export const createComment = (data) => {
  return api.post('/sns/comments', data)
}

// 댓글 수정
export const updateComment = (commentId, userNo, content) => {
  return api.put(`/sns/comments/${commentId}/${userNo}`, { content })
}

// 댓글 삭제
export const deleteComment = (commentId, userNo) => {
  return api.delete(`/sns/comments/${commentId}/${userNo}`)
}

// 좋아요 토글
export const toggleBoardLike = (boardId, userNo) => {
  return api.post(`/sns/likes/${boardId}/${userNo}`)
}

// 팔로잉 피드 조회
export const getFollowingFeed = (userNo, page = 0, size = 20) => {
  return api.get(`/sns/boards/following/${userNo}`, {
    params: { page, size }
  })
}

// 게시물 작성
export const createBoard = (data) => {
  return api.post('/sns/boards', data)
}

// 게시물 수정
export const updateBoard = (boardId, userNo, data) => {
  return api.put(`/sns/boards/${boardId}/${userNo}`, data)
}

// 게시물 삭제
export const deleteBoard = (boardId, userNo) => {
  return api.delete(`/sns/boards/${boardId}/${userNo}`)
}

// 랜덤 게시물 조회
export const getRandomBoards = (myUserNo = null, limit = 5) => {
  const params = {}
  if (myUserNo) params.myUserNo = myUserNo
  if (limit) params.limit = limit
  return api.get('/sns/boards/random', { params })
}

// 이미지 업로드 (단일)
export const uploadImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)

  return api.post('/images/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 이미지 업로드 (다중)
export const uploadImages = (files) => {
  const formData = new FormData()
  files.forEach(file => formData.append('files', file))

  return api.post('/images/upload/multiple', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}


// ai를 통해서 sns 피드 검색
export const getFeedUsingAI = (query) => {
  return api.get(`/aisns`, {
    params: { query }
  })
}