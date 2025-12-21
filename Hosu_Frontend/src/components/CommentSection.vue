<template>
  <div class="comments-section">
    <h3>댓글 {{ comments.length }}개</h3>

    <!-- 댓글 작성 폼 -->
    <div v-if="showForm" class="comment-form">
      <textarea
        v-model="newComment"
        placeholder="댓글을 입력하세요..."
        rows="3"
        @keydown.ctrl.enter="handleSubmit"
      ></textarea>
      <button @click="handleSubmit" :disabled="!newComment.trim() || submitting" class="btn-submit">
        {{ submitting ? '작성 중...' : '댓글 작성' }}
      </button>
    </div>

    <!-- 댓글 목록 -->
    <div class="comments-list">
      <div v-if="loading" class="loading">댓글을 불러오는 중...</div>

      <div v-else-if="comments.length === 0" class="no-comments">첫 댓글을 작성해보세요!</div>

      <div v-else>
        <div v-for="comment in topLevelComments" :key="comment.commentId" class="comment-item">
          <!-- 댓글 내용 -->
          <div class="comment-header">
            <img
              :src="comment.userImg || defaultProfileImage"
              :alt="comment.nickName"
              class="comment-avatar"
            />
            <div class="comment-info">
              <span class="comment-author">{{ comment.nickName }}</span>
              <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
            </div>
            <div v-if="isMyComment(comment.userNo)" class="comment-actions">
              <button
                v-if="editingComment !== comment.commentId"
                @click="startEdit(comment)"
                class="btn-icon"
                title="수정"
              >
                <img src="@/assets/edit.png" alt="수정" class="action-icon" />
              </button>
              <button
                @click="handleDelete(comment.commentId)"
                class="btn-icon"
                title="삭제"
              >
                <img src="@/assets/delete.png" alt="삭제" class="action-icon" />
              </button>
            </div>
          </div>
          
          <!-- 수정 모드 -->
          <div v-if="editingComment === comment.commentId" class="edit-form">
            <textarea v-model="editContent" rows="3"></textarea>
            <div class="edit-buttons">
              <button @click="handleEditSubmit(comment.commentId)" class="btn-submit">
                저장
              </button>
              <button @click="cancelEdit" class="btn-cancel">취소</button>
            </div>
          </div>
          
          <!-- 일반 모드 -->
          <div v-else class="comment-content">{{ comment.content }}</div>

          <!-- 답글 버튼 -->
          <button @click="toggleReply(comment.commentId)" class="btn-reply">
            답글 {{ comment.replies?.length || 0 }}개
          </button>

          <!-- 대댓글 목록 -->
          <div v-if="comment.replies && comment.replies.length > 0" class="replies">
            <div v-for="reply in comment.replies" :key="reply.commentId" class="comment-item reply">
              <div class="comment-header">
                  <img
                    :src="reply.userImg || defaultProfileImage"
                    :alt="reply.nickName"
                    class="comment-avatar reply-avatar"
                  />
                <div class="comment-info">
                  <span class="comment-author">{{ reply.nickName }}</span>
                  <span class="comment-date">{{ formatDate(reply.createdAt) }}</span>
                </div>
                <div v-if="isMyComment(reply.userNo)" class="comment-actions">
                  <button
                    v-if="editingComment !== reply.commentId"
                    @click="startEdit(reply)"
                    class="btn-icon"
                    title="수정"
                  >
                    <img src="@/assets/edit.png" alt="수정" class="action-icon" />
                  </button>
                  <button
                    @click="handleDelete(reply.commentId)"
                    class="btn-icon"
                    title="삭제"
                  >
                    <img src="@/assets/delete.png" alt="삭제" class="action-icon" />
                  </button>
                </div>
              </div>
              
              <!-- 수정 모드 -->
              <div v-if="editingComment === reply.commentId" class="edit-form">
                <textarea v-model="editContent" rows="2"></textarea>
                <div class="edit-buttons">
                  <button @click="handleEditSubmit(reply.commentId)" class="btn-submit">
                    저장
                  </button>
                  <button @click="cancelEdit" class="btn-cancel">취소</button>
                </div>
              </div>
              
              <!-- 일반 모드 -->
              <div v-else class="comment-content">{{ reply.content }}</div>
            </div>
          </div>

          <!-- 답글 작성 폼 -->
          <div v-if="replyingTo === comment.commentId" class="reply-form">
            <textarea v-model="replyContent" placeholder="답글을 입력하세요..." rows="2"></textarea>
            <div class="reply-buttons">
              <button @click="handleReplySubmit(comment.commentId)" class="btn-submit">
                답글 작성
              </button>
              <button @click="cancelReply" class="btn-cancel">취소</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getComments, createComment, updateComment, deleteComment } from '@/api/sns'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  boardId: {
    type: Number,
    required: true,
  },
  showForm: {
    type: Boolean,
    default: true,
  },
})

// 기본 프로필 이미지
const defaultProfileImage = new URL('@/assets/user.png', import.meta.url).href

const userStore = useUserStore()

const emit = defineEmits(['commentCountChanged'])

const comments = ref([])
const loading = ref(false)
const newComment = ref('')
const submitting = ref(false)
const replyingTo = ref(null)
const replyContent = ref('')
const editingComment = ref(null)
const editContent = ref('')

const myUserNo = ref(localStorage.getItem('userNo'))

// 최상위 댓글만 필터링
const topLevelComments = computed(() => {
  return comments.value.filter((comment) => !comment.parentId)
})

// 날짜 포맷
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date

  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '방금 전'
  if (minutes < 60) return `${minutes}분 전`
  if (hours < 24) return `${hours}시간 전`
  if (days < 7) return `${days}일 전`

  return date.toLocaleDateString('ko-KR')
}

// 내 댓글인지 확인
const isMyComment = (userNo) => {
  return String(userNo) === String(myUserNo.value)
}

// 댓글 불러오기
const loadComments = async () => {
  try {
    loading.value = true
    const response = await getComments(props.boardId)
    comments.value = response.data
    
    // 댓글 개수 변경 이벤트 발생
    const totalCount = comments.value.length
    emit('commentCountChanged', totalCount)
    
    console.log('댓글 데이터:', response.data)
  } catch (error) {
    console.error('댓글 로딩 실패:', error)
  } finally {
    loading.value = false
  }
}

// 댓글 작성
const handleSubmit = async () => {
  if (!newComment.value.trim()) return
  if (!myUserNo.value) {
    alert('로그인이 필요합니다.')
    return
  }

  try {
    submitting.value = true
    await createComment({
      boardId: props.boardId,
      userNo: Number(myUserNo.value),
      content: newComment.value,
      parentId: null,
    })

    newComment.value = ''
    await loadComments()
  } catch (error) {
    console.error('댓글 작성 실패:', error)
    alert('댓글 작성 중 오류가 발생했습니다.')
  } finally {
    submitting.value = false
  }
}

// 답글 토글
const toggleReply = (commentId) => {
  if (replyingTo.value === commentId) {
    replyingTo.value = null
    replyContent.value = ''
  } else {
    replyingTo.value = commentId
    replyContent.value = ''
  }
}

// 답글 취소
const cancelReply = () => {
  replyingTo.value = null
  replyContent.value = ''
}

// 답글 작성
const handleReplySubmit = async (parentId) => {
  if (!replyContent.value.trim()) return
  if (!myUserNo.value) {
    alert('로그인이 필요합니다.')
    return
  }

  try {
    await createComment({
      boardId: props.boardId,
      userNo: Number(myUserNo.value),
      content: replyContent.value,
      parentId: parentId,
    })

    replyContent.value = ''
    replyingTo.value = null
    await loadComments()
  } catch (error) {
    console.error('답글 작성 실패:', error)
    alert('답글 작성 중 오류가 발생했습니다.')
  }
}

// 수정 시작
const startEdit = (comment) => {
  editingComment.value = comment.commentId
  editContent.value = comment.content
}

// 수정 취소
const cancelEdit = () => {
  editingComment.value = null
  editContent.value = ''
}

// 수정 저장
const handleEditSubmit = async (commentId) => {
  if (!editContent.value.trim()) return

  try {
    await updateComment(commentId, myUserNo.value, editContent.value)
    editingComment.value = null
    editContent.value = ''
    await loadComments()
  } catch (error) {
    console.error('댓글 수정 실패:', error)
    alert('댓글 수정 중 오류가 발생했습니다.')
  }
}

// 댓글 삭제
const handleDelete = async (commentId) => {
  if (!confirm('정말 삭제하시겠습니까?')) return

  try {
    await deleteComment(commentId, myUserNo.value)
    await loadComments()
  } catch (error) {
    console.error('댓글 삭제 실패:', error)
    alert('댓글 삭제 중 오류가 발생했습니다.')
  }
}

onMounted(() => {
  loadComments()
})
</script>

<style scoped>
.comments-section {
  /* Custom Requested Palette */
  --color-bg: #F2F2F2;
  --color-emphasis: #593527;
  --color-main: #F29F05;
  --color-sub-1: #F2DCB3;
  --color-sub-2: #D97904;

  --bg-base: var(--color-bg);
  --bg-active: #FFFFFF;
  --text-primary: var(--color-emphasis);
  --border-color: var(--color-sub-1);

  padding: 40px;
  border-top: 1px solid var(--border-color);
  color: var(--text-primary);
}

.comments-section h3 {
  font-size: 20px;
  margin-bottom: 20px;
  color: var(--text-primary);
  font-weight: 700;
}

/* 댓글 작성 폼 */
.comment-form {
  margin-bottom: 30px;
}

.comment-form textarea {
  width: 100%;
  padding: 15px;
  border: 1.5px solid var(--border-color);
  background: var(--bg-active);
  border-radius: 12px;
  font-size: 14px;
  color: var(--text-primary);
  resize: vertical;
  margin-bottom: 10px;
  font-family: inherit;
  box-sizing: border-box;
}

.comment-form textarea:focus {
  outline: none;
  border-color: var(--color-main);
  background: #FFFFFF;
  box-shadow: 0 0 0 3px rgba(242, 159, 5, 0.1);
}

.comment-form textarea::placeholder {
  color: var(--text-primary);
  opacity: 0.5;
}

.btn-submit {
  padding: 12px 24px;
  background: var(--color-main);
  color: #FFFFFF;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(242, 159, 5, 0.3);
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(242, 159, 5, 0.4);
  background: var(--color-sub-2);
}

.btn-submit:disabled {
  background: var(--bg-base);
  color: var(--text-primary);
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 댓글 목록 */
.comments-list {
  margin-top: 20px;
}

.loading {
  text-align: center;
  padding: 40px;
  color: var(--text-primary);
  opacity: 0.6;
}

.no-comments {
  text-align: center;
  padding: 40px;
  color: var(--text-primary);
  opacity: 0.6;
}

/* 댓글 아이템 */
.comment-item {
  padding: 20px;
  border: 1px solid var(--border-color);
  background: #FFFFFF;
  border-radius: 12px;
  margin-bottom: 15px;
  transition: all 0.3s ease;
}

.comment-item:hover {
  background: #FFFFFF;
  border-color: var(--color-main);
  box-shadow: 0 2px 8px rgba(242, 159, 5, 0.15);
}

.comment-item.reply {
  margin-left: 40px;
  background: var(--bg-base);
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--border-color);
}

.comment-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.comment-actions {
  display: flex;
  gap: 8px;
}

.btn-icon {
  padding: 6px;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-icon:hover {
  background: rgba(255, 255, 255, 0.1);
}

.action-icon {
  width: 20px;
  height: 20px;
  display: block;
  filter: brightness(0.2);
}

.btn-icon:hover .action-icon {
  filter: brightness(0);
}

.comment-author {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 14px;
}

.comment-date {
  font-size: 12px;
  color: var(--text-primary);
  opacity: 0.6;
}

/* 수정 폼 */
.edit-form {
  margin: 10px 0;
}

.edit-form textarea {
  width: 100%;
  padding: 12px;
  border: 1.5px solid var(--border-color);
  background: var(--bg-active);
  color: var(--text-primary);
  border-radius: 8px;
  font-size: 14px;
  margin-bottom: 10px;
  font-family: inherit;
  box-sizing: border-box;
}

.edit-form textarea:focus {
  outline: none;
  border-color: var(--color-main);
  background: #FFFFFF;
  box-shadow: 0 0 0 3px rgba(242, 159, 5, 0.1);
}

.edit-form textarea::placeholder {
  color: var(--text-primary);
  opacity: 0.5;
}

.edit-buttons {
  display: flex;
  gap: 10px;
}

.comment-content {
  margin: 10px 0;
  color: var(--text-primary);
  line-height: 1.6;
  white-space: pre-wrap;
}

.btn-reply {
  padding: 6px 14px;
  background: transparent;
  border: 1px solid var(--border-color);
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  color: var(--text-primary);
  margin-top: 5px;
  transition: all 0.2s ease;
  font-weight: 590;
}

.btn-reply:hover {
  background: var(--bg-active);
  border-color: var(--color-main);
  color: var(--color-main);
}

/* 대댓글 */
.replies {
  margin-top: 15px;
}

/* 답글 작성 폼 */
.reply-form {
  margin-top: 15px;
  padding: 15px;
  background: var(--bg-base);
  border: 1px solid var(--border-color);
  border-radius: 12px;
}

.reply-form textarea {
  width: 100%;
  padding: 12px;
  border: 1.5px solid var(--border-color);
  background: var(--bg-active);
  color: var(--text-primary);
  border-radius: 8px;
  font-size: 14px;
  margin-bottom: 10px;
  font-family: inherit;
  box-sizing: border-box;
}

.reply-form textarea:focus {
  outline: none;
  border-color: var(--color-main);
  background: #FFFFFF;
  box-shadow: 0 0 0 3px rgba(242, 159, 5, 0.1);
}

.reply-form textarea::placeholder {
  color: var(--text-primary);
  opacity: 0.5;
}

.reply-buttons {
  display: flex;
  gap: 10px;
}

.btn-cancel {
  padding: 12px 24px;
  background: var(--bg-active);
  color: var(--text-primary);
  border: 1.5px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-cancel:hover {
  background: #FFFFFF;
  border-color: var(--color-main);
  transform: translateY(-2px);
}

/* 반응형 */
@media (max-width: 768px) {
  .comments-section {
    padding: 20px;
  }

  .comment-item.reply {
    margin-left: 20px;
  }
}
</style>
