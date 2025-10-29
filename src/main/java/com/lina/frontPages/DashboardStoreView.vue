<template>
  <div class="dashboard-store-view">
    <!-- Header -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h1 class="page-title">Dashboard Store</h1>
          <p class="page-subtitle">다양한 대시보드 템플릿을 찾아보고 설치하세요</p>
        </div>
        <div class="header-actions">
          <button class="create-dashboard-btn" @click="createNewDashboard">
            <IconSystem name="plus" :size="16" />
            신규 대시보드
          </button>
          <div class="search-box">
            <IconSystem name="search" :size="16" />
            <input 
              v-model="searchQuery"
              type="text" 
              placeholder="대시보드 검색..."
              class="search-input"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- Featured Dashboards -->
    <section class="featured-section">
      <div class="section-header">
        <h2 class="section-title">추천 대시보드</h2>
      </div>
      
      <div class="featured-grid">
        <div 
          v-for="dashboard in filteredDashboards" 
          :key="dashboard.id"
          class="featured-card"
          @click="viewDashboard(dashboard)"
        >
          <div class="card-content">
            <div class="card-header">
              <h3 class="card-title">{{ dashboard.title }}</h3>
              <div v-if="dashboard.isNew" class="new-badge">NEW</div>
            </div>
            <p class="card-description">{{ dashboard.description }}</p>
            
            <div class="card-tags">
              <span v-for="tag in dashboard.tags?.slice(0, 3)" :key="tag" class="tag">
                {{ tag }}
              </span>
            </div>
            
            <div class="card-meta">
              <span class="downloads">{{ dashboard.downloads }} 다운로드</span>
              <span class="rating">⭐ {{ dashboard.rating }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Dashboard Detail Modal -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">{{ selectedDashboard?.title }}</h2>
          <button class="modal-close" @click="closeDetailModal">
            <IconSystem name="x" :size="20" />
          </button>
        </div>
        
        <div class="modal-body">
          <div class="dashboard-preview">
            <img 
              :src="selectedDashboard?.image" 
              :alt="selectedDashboard?.title"
              class="dashboard-image"
              @error="handleImageError"
              @load="handleImageLoad"
            />
            <div v-if="imageError" class="image-placeholder">
              <IconSystem name="file" :size="48" />
              <p>이미지를 불러올 수 없습니다</p>
            </div>
          </div>
          
          <div class="dashboard-info">
            <div class="dashboard-stats">
              <div class="stat-item">
                <span class="stat-label">평점</span>
                <span class="stat-value">⭐ {{ selectedDashboard?.rating }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">다운로드</span>
                <span class="stat-value">{{ selectedDashboard?.downloads }}</span>
              </div>
            </div>
            
            <div class="dashboard-description">
              <h3>상세 설명</h3>
              <p>{{ selectedDashboard?.detailedDescription }}</p>
            </div>
            
            <div class="dashboard-tags">
              <h4>태그</h4>
              <div class="tags-list">
                <span v-for="tag in selectedDashboard?.tags" :key="tag" class="modal-tag">
                  {{ tag }}
                </span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeDetailModal">취소</button>
          <button class="btn-primary" @click="registerDashboard">My Dashboard에 등록</button>
        </div>
      </div>
    </div>

    <!-- Confirmation Modal -->
    <div v-if="showConfirmModal" class="modal-overlay" @click="cancelNavigation">
      <div class="modal-content small" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">등록 완료</h2>
        </div>
        
        <div class="modal-body">
          <p>등록되었습니다. 해당 Dashboard로 이동하시겠습니까?</p>
        </div>
        
        <div class="modal-footer">
          <button class="btn-secondary" @click="cancelNavigation">취소</button>
          <button class="btn-primary" @click="confirmNavigation">이동</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import IconSystem from './IconSystem.vue'

// Reactive state
const searchQuery = ref('')

// Reactive state for modal
const showDetailModal = ref(false)
const selectedDashboard = ref(null)
const showConfirmModal = ref(false)
const imageError = ref(false)

// Sample data
const featuredDashboards = [
  {
    id: 1,
    title: '매출 분석 대시보드',
    description: '실시간 매출 현황과 트렌드를 한눈에 확인할 수 있는 종합 대시보드',
    detailedDescription: '이 대시보드는 회사의 매출 데이터를 실시간으로 분석하고 시각화합니다. 월별, 분기별, 연도별 매출 트렌드를 확인할 수 있으며, 제품별, 지역별 매출 현황도 한눈에 파악할 수 있습니다.',
    rating: 4.8,
    downloads: '2.1k',
    image: '/sampleDashboard.png',
    tags: ['매출', '분석', '트렌드', 'KPI']
  },
  {
    id: 2,
    title: '고객 행동 분석',
    description: '고객 여정과 행동 패턴을 시각화하여 인사이트를 제공하는 대시보드',
    detailedDescription: '고객의 웹사이트 방문 패턴, 구매 행동, 이탈률 등을 분석하여 마케팅 전략 수립에 도움을 주는 대시보드입니다. 고객 세그먼트별 분석과 개인화 추천 기능도 포함되어 있습니다.',
    rating: 4.6,
    downloads: '1.8k',
    image: '/sampleDashboard.png',
    tags: ['고객', '행동분석', '마케팅', '인사이트']
  },
  {
    id: 3,
    title: '운영 효율성 모니터',
    description: '시스템 성능과 운영 지표를 실시간으로 모니터링하는 대시보드',
    detailedDescription: '서버 성능, 네트워크 상태, 애플리케이션 응답시간 등 IT 인프라의 전반적인 상태를 모니터링합니다. 알림 기능과 자동화된 보고서 생성 기능이 포함되어 있습니다.',
    rating: 4.9,
    downloads: '1.5k',
    image: '/sampleDashboard.png',
    tags: ['운영', '모니터링', '성능', '인프라']
  },
  {
    id: 4,
    title: 'Claim Summary',
    description: '보험 청구 현황과 처리 상태를 종합적으로 관리하는 대시보드',
    detailedDescription: '보험 청구 건수, 처리 현황, 승인/거절률, 평균 처리 시간 등을 실시간으로 모니터링할 수 있습니다. 청구 유형별 분석과 지연 건에 대한 알림 기능도 제공합니다.',
    rating: 4.7,
    downloads: '1.3k',
    image: '/sampleDashboard2.png',
    tags: ['청구', 'claim', '보험', '처리현황'],
    isNew: true
  },
  {
    id: 5,
    title: 'APE Report',
    description: '연간보험료 환산 실적을 분석하는 핵심 KPI 대시보드',
    detailedDescription: 'Annual Premium Equivalent 지표를 통해 보험 영업 성과를 측정하고 분석합니다. 지역별, 상품별, 채널별 APE 실적과 목표 대비 달성률을 실시간으로 확인할 수 있습니다.',
    rating: 4.5,
    downloads: '980',
    image: '/sampleDashboard.png',
    tags: ['APE', 'KPI', '영업실적', '보험료'],
    isNew: true
  },
  {
    id: 6,
    title: 'PLR Report',
    description: '보험계약 지속률 분석을 위한 핵심 지표 대시보드',
    detailedDescription: 'Persistency Lapse Rate를 통해 보험계약의 유지 현황을 분석합니다. 월별, 분기별 해지율 추이와 상품별 지속률을 모니터링하여 고객 유지 전략 수립을 지원합니다.',
    rating: 4.3,
    downloads: '756',
    image: '/sampleDashboard.png',
    tags: ['PLR', '지속률', '해지율', 'KPI']
  },
  {
    id: 7,
    title: 'UW Decline',
    description: '언더라이팅 거절 현황 분석 대시보드',
    detailedDescription: '보험 인수심사 과정에서의 거절 사유와 패턴을 분석합니다. 상품별, 연령대별, 위험도별 거절률을 시각화하여 인수정책 개선과 리스크 관리에 활용할 수 있습니다.',
    rating: 4.4,
    downloads: '642',
    image: '/sampleDashboard.png',
    tags: ['언더라이팅', '거절률', '인수심사', '리스크']
  },
  {
    id: 8,
    title: 'Daily Sales Report',
    description: '일일 영업 실적 현황을 실시간으로 모니터링하는 대시보드',
    detailedDescription: '당일 보험 계약 체결 현황, 영업팀별 실적, 상품별 판매량을 실시간으로 확인할 수 있습니다. 목표 대비 달성률과 전일 대비 증감률도 함께 제공합니다.',
    rating: 4.6,
    downloads: '1.1k',
    image: '/sampleDashboard.png',
    tags: ['일일실적', '영업', '계약', '실시간']
  }
]

// Methods
const viewDashboard = (dashboard) => {
  selectedDashboard.value = dashboard
  showDetailModal.value = true
}

const closeDetailModal = () => {
  showDetailModal.value = false
  selectedDashboard.value = null
}

const registerDashboard = () => {
  showDetailModal.value = false
  showConfirmModal.value = true
}

const emit = defineEmits(['navigate-to-dashboard'])

const confirmNavigation = () => {
  showConfirmModal.value = false
  // Emit event to parent component to navigate to My Dashboard
  emit('navigate-to-dashboard')
}

const cancelNavigation = () => {
  showConfirmModal.value = false
}

const createNewDashboard = () => {
  console.log('신규 대시보드 생성 페이지로 이동')
  // 향후 라우터로 페이지 이동 구현
  alert('신규 대시보드 생성 페이지로 이동합니다.')
}

const handleImageError = () => {
  imageError.value = true
}

const handleImageLoad = () => {
  imageError.value = false
}

// Computed for filtering dashboards
const filteredDashboards = computed(() => {
  if (!searchQuery.value.trim()) {
    return featuredDashboards
  }
  
  const query = searchQuery.value.toLowerCase()
  return featuredDashboards.filter(dashboard => 
    dashboard.title.toLowerCase().includes(query) ||
    dashboard.description.toLowerCase().includes(query) ||
    dashboard.tags?.some(tag => tag.toLowerCase().includes(query))
  )
})
</script>

<style scoped>
.dashboard-store-view {
  display: flex;
  flex-direction: column;
  padding: var(--space-4);
  gap: var(--space-4);
}

/* Header */
.page-header {
  margin-bottom: var(--space-6);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-4);
}

.header-info h1 {
  font-size: var(--fs-2xl);
  font-weight: var(--fw-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.header-info p {
  color: var(--text-secondary);
  margin: 0;
}

.header-actions {
  display: flex;
  gap: var(--space-3);
  align-items: center;
}

.create-dashboard-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-4);
  background: var(--lina-orange);
  color: white;
  border: none;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: var(--transition-fast);
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
  white-space: nowrap;
}

.create-dashboard-btn:hover {
  background: var(--lina-yellow);
  color: var(--text-primary);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.search-box {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  background: var(--surface);
  min-width: 250px;
}

.search-input {
  border: none;
  outline: none;
  background: transparent;
  font-size: var(--fs-sm);
  color: var(--text-primary);
  flex: 1;
}

/* Section Headers */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-6);
}

.section-title {
  font-size: var(--fs-xl);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0;
}

/* Featured Dashboards */
.featured-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: var(--space-6);
}

.featured-card {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
  cursor: pointer;
  transition: var(--transition-fast);
  box-shadow: var(--shadow-sm);
}

.featured-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--lina-orange);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-3);
}

.card-title {
  font-size: var(--fs-lg);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0;
  flex: 1;
}

.new-badge {
  font-size: 9px;
  font-weight: var(--fw-bold);
  padding: 2px 5px;
  border-radius: var(--radius-sm);
  line-height: 1;
  background: #ef4444;
  color: white;
  animation: pulse 2s infinite;
  flex-shrink: 0;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

.card-description {
  color: var(--text-secondary);
  margin: 0 0 var(--space-3) 0;
  line-height: var(--lh-relaxed);
}

.card-tags {
  display: flex;
  gap: var(--space-1);
  margin-bottom: var(--space-3);
  flex-wrap: wrap;
}

.tag {
  background: var(--surface-hover);
  color: var(--text-secondary);
  padding: 2px var(--space-2);
  border-radius: var(--radius-sm);
  font-size: var(--fs-xs);
  font-weight: var(--fw-medium);
  border: 1px solid var(--border-primary);
  transition: var(--transition-fast);
}

.tag:hover {
  background: var(--lina-orange);
  color: white;
  border-color: var(--lina-orange);
}

.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: var(--fs-sm);
  color: var(--text-tertiary);
}

.downloads {
  color: var(--text-secondary);
}

.rating {
  color: var(--lina-yellow);
  font-weight: var(--fw-semibold);
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--space-4);
}

.modal-content {
  background: var(--surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-xl);
  max-width: 800px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content.small {
  max-width: 400px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6);
  border-bottom: 1px solid var(--border-primary);
}

.modal-title {
  font-size: var(--fs-xl);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0;
}

.modal-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  background: var(--surface-hover);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-secondary);
}

.modal-close:hover {
  background: var(--surface-active);
  color: var(--text-primary);
}

.modal-body {
  padding: var(--space-6);
}

.dashboard-preview {
  margin-bottom: var(--space-6);
}

.dashboard-image {
  width: 100%;
  height: auto;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-primary);
}

.image-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  background: var(--surface-hover);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-primary);
  color: var(--text-secondary);
}

.image-placeholder p {
  margin: var(--space-2) 0 0 0;
  font-size: var(--fs-sm);
}

.dashboard-stats {
  display: flex;
  gap: var(--space-6);
  margin-bottom: var(--space-6);
  padding: var(--space-4);
  background: var(--surface-hover);
  border-radius: var(--radius-md);
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.stat-label {
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}

.stat-value {
  font-size: var(--fs-lg);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
}

.dashboard-description h3 {
  font-size: var(--fs-lg);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-3) 0;
}

.dashboard-description p {
  color: var(--text-secondary);
  line-height: var(--lh-relaxed);
  margin: 0;
}

.dashboard-tags {
  margin-top: var(--space-4);
}

.dashboard-tags h4 {
  font-size: var(--fs-base);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.tags-list {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
}

.modal-tag {
  background: var(--lina-orange);
  color: white;
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-md);
  font-size: var(--fs-sm);
  font-weight: var(--fw-medium);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
  padding: var(--space-6);
  border-top: 1px solid var(--border-primary);
}

.btn-primary,
.btn-secondary {
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-md);
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
  cursor: pointer;
  transition: var(--transition-fast);
  border: 1px solid;
}

.btn-primary {
  background: var(--lina-orange);
  color: white;
  border-color: var(--lina-orange);
}

.btn-primary:hover {
  background: var(--lina-yellow);
  border-color: var(--lina-yellow);
}

.btn-secondary {
  background: var(--surface);
  color: var(--text-secondary);
  border-color: var(--border-primary);
}

.btn-secondary:hover {
  background: var(--surface-hover);
  color: var(--text-primary);
}

/* Responsive */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-actions {
    width: 100%;
    flex-direction: column;
    gap: var(--space-3);
  }

  .create-dashboard-btn {
    width: 100%;
    justify-content: center;
  }
  
  .search-box {
    min-width: auto;
    width: 100%;
  }
  
  .featured-grid {
    grid-template-columns: 1fr;
  }

  .modal-overlay {
    padding: var(--space-2);
  }

  .modal-header,
  .modal-body,
  .modal-footer {
    padding: var(--space-4);
  }

  .dashboard-stats {
    flex-direction: column;
    gap: var(--space-3);
  }

  .modal-footer {
    flex-direction: column;
  }
}
</style>