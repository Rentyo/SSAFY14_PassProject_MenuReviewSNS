<template>
  <div class="admin-tags-container">
    <div class="header-row">
      <div class="header-text">
        <h2 class="page-title">메뉴 태그 관리</h2>
        <p class="page-subtitle">카테고리별 메뉴 태그를 관리할 수 있습니다</p>
      </div>
      <button class="btn-add" @click="openAddModal">
        <svg class="icon-plus" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
        </svg>
        태그 추가
      </button>
    </div>

    <!-- Tabs Header -->
    <div class="tabs-control">
      <div class="category-list">
        <button
            v-for="cat in categories" 
            :key="cat.value"
            :class="['category-pill', { active: selectedCategory === cat.value }]"
            @click="selectedCategory = cat.value"
        >
            {{ cat.label }}
        </button>
      </div>
    </div>

    <!-- Tag List Content -->
    <div class="content-area">
      <div class="area-header">
        <h3>{{ getCategoryLabel(selectedCategory) }} <span class="count">({{ filteredTags.length }})</span></h3>
      </div>

      <div class="tags-grid" v-if="filteredTags.length > 0">
        <div v-for="tag in filteredTags" :key="tag.tagId" class="tag-card">
          <div class="tag-info">
              <!-- Bookmark Icon -->
              <svg class="tag-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M7 2H17C18.1046 2 19 2.89543 19 4V20.061C19 21.0569 17.8465 21.5833 17.097 20.9317L12 16.5L6.90302 20.9317C6.15347 21.5833 5 21.0569 5 20.061V4C5 2.89543 5.89543 2 7 2Z" 
                      stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
              </svg>
              <span class="tag-name">#{{ tag.tagName }}</span>
          </div>
          <div class="tag-actions">
            <button class="btn-icon edit" @click="openEditModal(tag)" title="수정">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"/>
              </svg>
            </button>
            <button class="btn-icon delete" @click="confirmDelete(tag)" title="삭제">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <p>등록된 태그가 없습니다.</p>
      </div>
    </div>

    <!-- Modal -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ isEditing ? '태그 수정' : '태그 추가' }}</h3>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label>카테고리</label>
            <select v-model="form.category" :disabled="isEditing" class="input-field">
              <option v-for="cat in categories" :key="cat.value" :value="cat.value">
                {{ cat.label }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>태그 이름</label>
            <input 
              v-model="form.tagName" 
              type="text" 
              placeholder="태그 이름을 입력하세요"
              class="input-field"
              @keyup.enter="saveTag"
            />
          </div>

          <div class="modal-actions">
            <button class="btn-cancel" @click="closeModal">취소</button>
            <button class="btn-save" @click="saveTag">{{ isEditing ? '수정' : '저장' }}</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getMenuTagAll } from '@/api/search'
import {createMenuTag, updateMenuTag, deleteMenuTag} from '@/api/menu'
export default {
  name: 'AdminMenuTags',
  data() {
    return {
      tags: [],
      selectedCategory: 'KOREAN',
      categories: [
        { value: 'KOREAN', label: '한식' },
        { value: 'CHINESE', label: '중식' },
        { value: 'JAPANESE', label: '일식' },
        { value: 'WESTERN', label: '양식' },
        { value: 'SNACK', label: '분식' },
        { value: 'CHICKEN', label: '치킨' },
        { value: 'MEAT', label: '고기/구이' },
        { value: 'SEAFOOD', label: '회/해물' },
        { value: 'CAFE', label: '카페/디저트' },
        { value: 'PUB', label: '술집' },
        { value: 'BAKERY', label: '베이커리' },
        { value: 'FASTFOOD', label: '패스트푸드' },
        { value: 'ASIAN', label: '아시안' },
        { value: 'CURRY', label: '카레/돈가스' } 
      ],
      showModal: false,
      isEditing: false,
      form: {
        tagId: null,
        tagName: '',
        category: 'KOREAN'
      }
    }
  },
  computed: {
    filteredTags() {
      return this.tags.filter(tag => tag.category === this.selectedCategory)
    }
  },
  async created() {
    await this.fetchTags()
  },
  methods: {
    getCategoryLabel(value) {
      const cat = this.categories.find(c => c.value === value)
      return cat ? cat.label : value
    },
    async fetchTags() {
      try {
        const response = await getMenuTagAll()
        this.tags = response.data || [] 
      } catch (error) {
        console.error('태그 목록 불러오기 실패:', error)
      }
    },
    openAddModal() {
      this.isEditing = false
      this.form = {
        tagId: null,
        tagName: '',
        category: this.selectedCategory
      }
      this.showModal = true
    },
    openEditModal(tag) {
      this.isEditing = true
      this.form = {
        tagId: tag.tagId,
        tagName: tag.tagName,
        category: tag.category
      }
      this.showModal = true
    },
    closeModal() {
      this.showModal = false
      this.form = { tagId: null, tagName: '', category: '' }
    },
    async saveTag() {
      if (!this.form.tagName.trim()) {
        alert('태그 이름을 입력해주세요.')
        return
      }
      try {
        if (this.isEditing) {
          await updateMenuTag(this.form)
          alert('수정되었습니다.')
        } else {
          await createMenuTag(this.form)
          alert('추가되었습니다.')
        }
        await this.fetchTags()
        this.closeModal()
      } catch (error) {
        console.error('태그 저장 실패:', error)
        alert('저장 중 오류가 발생했습니다.')
      }
    },
    async confirmDelete(tag) {
      if (confirm(`'${tag.tagName}' 태그를 삭제하시겠습니까?`)) {
        try {
          await deleteMenuTag(tag.tagId)
          await this.fetchTags()
          alert('삭제되었습니다.')
        } catch (error) {
          console.error('태그 삭제 실패:', error)
          alert('삭제 중 오류가 발생했습니다.')
        }
      }
    }
  }
}
</script>

<style scoped>
/* Container & General */
.admin-tags-container {
  padding: 32px 40px;
  max-width: 1600px;
  margin: 0 auto;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

/* Tabs */
.tabs-control {
  background: white;
  padding: 16px; 
  border-radius: 12px;
  border: 1px solid #E5E7EB;
  margin-bottom: 24px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.category-pill {
  padding: 8px 20px;
  border-radius: 9999px;
  font-size: 14px;
  font-weight: 500;
  color: #6B7280;
  background: #F3F4F6;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}

.category-pill:hover {
  background: #E5E7EB;
}

.category-pill.active {
  background: #10B981;
  color: white;
  font-weight: 600;
  box-shadow: 0 2px 4px rgba(16, 185, 129, 0.3);
}

/* Add Button Area removed, button is now in header-row */

.btn-add {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background-color: #10b981;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-add:hover {
  background-color: #059669;
}

.icon-plus {
  width: 18px;
  height: 18px;
}


/* CONTENT AREA */
.content-area {
  background: white;
  padding: 32px;
  border-radius: 16px;
  border: 1px solid #E5E7EB;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.area-header {
  margin-bottom: 24px;
}

.area-header h3 {
  font-size: 18px;
  font-weight: 700;
  color: #374151;
}

.area-header .count {
  color: #6B7280;
  font-weight: 500;
  font-size: 16px;
  margin-left: 4px;
}


/* Grid */
.tags-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}

.tag-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background-color: #F0FDF9; /* Mint light */
  border: 1px solid #CCFBF1;
  border-radius: 12px;
  transition: all 0.2s;
}

.tag-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);
  border-color: #99F6E4;
}

.tag-info {
    display: flex;
    align-items: center;
    gap: 8px;
}

.tag-icon {
    width: 18px;
    height: 18px;
    color: #10B981;
    fill: none;
}

.tag-name {
  font-weight: 600;
  color: #1F2937;
  font-size: 15px;
}

.tag-actions {
  display: flex;
  gap: 4px;
  opacity: 0.6; /* Slight fade by default */
  transition: opacity 0.2s;
}

.tag-card:hover .tag-actions {
    opacity: 1;
}

.btn-icon {
  padding: 6px;
  color: #9ca3af;
  background: white;
  border: 1px solid #E5E7EB;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-icon svg {
  width: 16px;
  height: 16px;
}

.btn-icon.edit:hover {
  color: #3b82f6;
  border-color: #3b82f6;
  background-color: #eff6ff;
}

.btn-icon.delete:hover {
  color: #ef4444;
  border-color: #ef4444;
  background-color: #fef2f2;
}

.empty-state {
  text-align: center;
  padding: 60px;
  color: #9ca3af;
  font-size: 15px;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 0;
  border-radius: 16px;
  width: 90%;
  max-width: 440px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.modal-header {
  padding: 24px;
  background: #10B981;
  border-bottom: none;
}

.modal-body {
  padding: 32px;
}

.modal-content h3 {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  color: white;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.input-field {
  width: 100%;
  padding: 12px;
  border: 1px solid #D1D5DB;
  border-radius: 8px;
  font-size: 15px;
  transition: border-color 0.2s;
  background: #F9FAFB;
}

.input-field:focus {
  border-color: #10b981;
  background: white;
  outline: none;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
}

.btn-cancel {
  padding: 10px 20px;
  background-color: white;
  border: 1px solid #D1D5DB;
  border-radius: 8px;
  color: #374151;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel:hover {
  background-color: #F3F4F6;
  color: #111827;
}

.btn-save {
  padding: 10px 20px;
  background-color: #10b981;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-save:hover {
  background-color: #059669;
  transform: translateY(-1px);
}
</style>
