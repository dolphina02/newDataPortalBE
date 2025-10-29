<template>
  <div class="data-report-view">
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h1 class="page-title">Data Report</h1>
          <p class="page-subtitle">월간 리포트 및 분석 문서를 확인하세요</p>
        </div>
        <div class="header-actions">
          <div class="search-box">
            <IconSystem name="search" :size="16" />
            <input 
              v-model="searchQuery"
              type="text" 
              placeholder="리포트 검색..."
              class="search-input"
            />
          </div>
          <select v-model="selectedCategory" class="category-filter">
            <option value="">전체 카테고리</option>
            <option value="customer">고객 분석</option>
            <option value="kpi">KPI 리포트</option>
            <option value="monitoring">모니터링</option>
            <option value="monthly">월간 리포트</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Report Categories -->
    <div class="categories-section">
      <div class="category-tabs">
        <button 
          v-for="category in categories" 
          :key="category.id"
          class="category-tab"
          :class="{ active: activeCategory === category.id }"
          @click="activeCategory = category.id"
        >
          <IconSystem :name="category.icon" :size="16" />
          <span>{{ category.label }}</span>
          <span class="count">({{ getCategoryCount(category.id) }})</span>
        </button>
      </div>
    </div>

    <!-- Reports Grid -->
    <div class="reports-section">
      <div class="reports-grid">
        <div 
          v-for="report in filteredReports" 
          :key="report.id"
          class="report-card"
          @click="openReport(report)"
        >
          <div class="report-header">
            <div class="report-icon">
              <IconSystem :name="getFileIcon(report.type)" :size="20" />
              <div class="file-type-badge">{{ report.type.toUpperCase() }}</div>
            </div>
            <div class="report-title-section">
              <div class="title-row">
                <h3 class="report-title">{{ report.title }}</h3>
                <div class="badges">
                  <div v-if="report.isNew" class="new-badge">NEW</div>
                  <div class="report-type-badge" :class="report.reportType">
                    {{ report.reportType === 'internal' ? '내부' : '외부' }}
                  </div>
                </div>
              </div>
              <p class="report-description">{{ report.description }}</p>
            </div>
            <div class="report-actions">
              <button class="action-btn" @click.stop="downloadReport(report)" title="다운로드">
                <IconSystem name="download" :size="14" />
              </button>
              <button class="action-btn" @click.stop="shareReport(report)" title="공유">
                <IconSystem name="share" :size="14" />
              </button>
            </div>
          </div>
          
          <div class="report-footer">
            <div class="report-meta">
              <div class="meta-item">
                <IconSystem name="calendar" :size="12" />
                <span>{{ formatDate(report.date) }}</span>
              </div>
              <div class="meta-item">
                <IconSystem name="user" :size="12" />
                <span>{{ report.author }}</span>
              </div>
              <div class="meta-item">
                <IconSystem name="file" :size="12" />
                <span>{{ formatFileSize(report.size) }}</span>
              </div>
            </div>
            
            <div class="report-tags">
              <span 
                v-for="tag in report.tags.slice(0, 2)" 
                :key="tag"
                class="tag"
              >
                {{ tag }}
              </span>
              <span v-if="report.tags.length > 2" class="tag more-tags">
                +{{ report.tags.length - 2 }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Document Viewer Modal -->
    <div v-if="showViewer" class="modal-overlay" :class="{ fullscreen: isFullscreen }" @click="closeViewer">
      <div class="modal-content viewer-modal" :class="{ fullscreen: isFullscreen }" @click.stop>
        <div class="modal-header">
          <div class="viewer-title">
            <IconSystem :name="getFileIcon(selectedReport?.type)" :size="20" />
            <h3>{{ selectedReport?.title }}</h3>
          </div>
          <div class="viewer-actions">
            <button class="viewer-btn" @click="toggleFullscreen" :title="isFullscreen ? '창 모드' : '전체화면'">
              <IconSystem :name="isFullscreen ? 'minimize' : 'maximize'" :size="16" />
            </button>
            <button class="viewer-btn" @click="closeViewer" title="닫기">
              <IconSystem name="x" :size="16" />
            </button>
          </div>
        </div>
        
        <div class="modal-body">
          <div class="document-viewer">
            <!-- PDF Viewer -->
            <div v-if="selectedReport?.type === 'pdf'" class="pdf-viewer">
              <!-- Real PDF Embed for actual PDF files -->
              <div v-if="selectedReport?.pdfUrl" class="real-pdf-container">
                <embed 
                  :src="selectedReport.pdfUrl" 
                  type="application/pdf" 
                  class="real-pdf-embed"
                />
              </div>
              <!-- Mock PDF content for sample reports -->
              <div v-else class="mock-pdf-container">
                <div class="pdf-controls">
                  <div class="pdf-nav-controls">
                    <button class="pdf-btn" @click="previousPage" :disabled="currentPage <= 1">
                      <IconSystem name="chevron-left" :size="16" />
                    </button>
                    <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
                    <button class="pdf-btn" @click="nextPage" :disabled="currentPage >= totalPages">
                      <IconSystem name="chevron-right" :size="16" />
                    </button>
                  </div>
                  <div class="zoom-controls">
                    <button class="pdf-btn" @click="zoomOut">
                      <IconSystem name="zoom-out" :size="16" />
                    </button>
                    <span class="zoom-level">{{ zoomLevel }}%</span>
                    <button class="pdf-btn" @click="zoomIn">
                      <IconSystem name="zoom-in" :size="16" />
                    </button>
                    <button class="pdf-btn" @click="resetZoom">
                      <IconSystem name="maximize" :size="16" />
                    </button>
                  </div>
                </div>
                <div class="pdf-content">
                  <div class="pdf-page" :style="{ transform: `scale(${zoomLevel / 100})` }">
                    <div class="pdf-mock">
                      <div class="pdf-header">
                        <h2>{{ selectedReport?.title }}</h2>
                        <div class="pdf-date">{{ formatDate(selectedReport?.date) }}</div>
                      </div>
                      <div class="pdf-body">
                        <div class="pdf-section">
                          <h3>1. 개요</h3>
                          <p>본 리포트는 {{ selectedReport?.title }}에 대한 상세 분석 결과를 제공합니다.</p>
                        </div>
                        <div class="pdf-section">
                          <h3>2. 주요 지표</h3>
                          <div class="pdf-chart-placeholder">
                            <IconSystem name="bar-chart" :size="48" />
                            <p>차트 영역</p>
                          </div>
                        </div>
                        <div class="pdf-section">
                          <h3>3. 분석 결과</h3>
                          <p>분석 결과에 따르면 다음과 같은 인사이트를 도출할 수 있습니다.</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Word/PPT Viewer -->
            <div v-else class="office-viewer">
              <div class="office-content">
                <div class="office-mock">
                  <div class="office-header">
                    <h2>{{ selectedReport?.title }}</h2>
                    <div class="office-meta">
                      <span>작성자: {{ selectedReport?.author }}</span>
                      <span>작성일: {{ formatDate(selectedReport?.date) }}</span>
                    </div>
                  </div>
                  <div class="office-body">
                    <div v-if="selectedReport?.type === 'docx'" class="word-content">
                      <h3>문서 내용</h3>
                      <p>이 문서는 {{ selectedReport?.title }}에 대한 상세한 내용을 담고 있습니다.</p>
                      <p>Microsoft Word 형식으로 작성된 문서입니다.</p>
                    </div>
                    <div v-else-if="selectedReport?.type === 'pptx'" class="ppt-content">
                      <div class="slide-container">
                        <div class="slide">
                          <h3>슬라이드 {{ currentSlide }}</h3>
                          <p>PowerPoint 프레젠테이션 내용</p>
                        </div>
                      </div>
                      <div class="slide-controls">
                        <button class="slide-btn" @click="previousSlide" :disabled="currentSlide <= 1">
                          이전
                        </button>
                        <span>{{ currentSlide }} / {{ totalSlides }}</span>
                        <button class="slide-btn" @click="nextSlide" :disabled="currentSlide >= totalSlides">
                          다음
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
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
const selectedCategory = ref('')
const activeCategory = ref('all')
const showViewer = ref(false)
const selectedReport = ref(null)
const currentPage = ref(1)
const totalPages = ref(15)
const zoomLevel = ref(100)
const currentSlide = ref(1)
const totalSlides = ref(8)
const isFullscreen = ref(false)

// Categories
const categories = [
  { id: 'all', label: '전체', icon: 'grid' },
  { id: 'customer', label: '고객 분석', icon: 'users' },
  { id: 'kpi', label: 'KPI 리포트', icon: 'trending-up' },
  { id: 'monitoring', label: '모니터링', icon: 'monitor' },
  { id: 'monthly', label: '월간 리포트', icon: 'calendar' }
]

// Sample reports data
const reports = [
  {
    id: 1,
    title: '보험통계월보 25년7월호',
    description: '보험업계 주요 통계 및 동향 분석 (367호)',
    type: 'pdf',
    category: 'monthly',
    date: '2025-07-31',
    author: '보험개발원',
    size: 2456789,
    tags: ['보험통계', '월보', '업계동향'],
    pdfUrl: '/보험통계월보_25년 7월호(367호).pdf',
    reportType: 'external',
    isNew: true
  },
  {
    id: 2,
    title: '월간생명보험 25년9월호',
    description: '생명보험업계 월간 리포트',
    type: 'pdf',
    category: 'monthly',
    date: '2025-09-30',
    author: '생명보험협회',
    size: 1834567,
    tags: ['생명보험', '월간', '업계리포트'],
    pdfUrl: '/월간생명보험_25년9월호.pdf',
    reportType: 'external',
    isNew: true
  },
  {
    id: 3,
    title: '고객통합리포트',
    description: '고객 행동 패턴 및 세그먼트 분석 종합 리포트',
    type: 'pdf',
    category: 'customer',
    date: '2024-09-30',
    author: '김분석',
    size: 2456789,
    tags: ['고객분석', '세그먼트', '행동패턴'],
    reportType: 'internal'
  },
  {
    id: 2,
    title: '고객DB모니터링리포트',
    description: '고객 데이터베이스 품질 및 정합성 모니터링 결과',
    type: 'pdf',
    category: 'monitoring',
    date: '2024-09-28',
    author: '이모니터',
    size: 1834567,
    tags: ['데이터품질', '모니터링', 'DB']
  },
  {
    id: 3,
    title: 'Monthly KPI 리포트',
    description: '9월 주요 성과 지표 및 목표 달성률 분석',
    type: 'pptx',
    category: 'kpi',
    date: '2024-09-30',
    author: '박KPI',
    size: 3245678,
    tags: ['KPI', '성과지표', '월간'],
    reportType: 'internal'
  },
  {
    id: 4,
    title: '보험상품 판매현황 분석',
    description: '상품별 판매 실적 및 트렌드 분석 리포트',
    type: 'docx',
    category: 'monthly',
    date: '2024-09-25',
    author: '정상품',
    size: 1567890,
    tags: ['상품분석', '판매실적', '트렌드'],
    reportType: 'internal'
  },
  {
    id: 5,
    title: '고객 이탈 예측 모델 성과',
    description: 'ML 기반 고객 이탈 예측 모델의 성과 평가 리포트',
    type: 'pdf',
    category: 'customer',
    date: '2024-09-20',
    author: '최ML',
    size: 2789012,
    tags: ['머신러닝', '이탈예측', '모델성과'],
    reportType: 'internal'
  },
  {
    id: 6,
    title: '시스템 성능 모니터링',
    description: '데이터 플랫폼 시스템 성능 및 안정성 모니터링',
    type: 'pdf',
    category: 'monitoring',
    date: '2024-09-15',
    author: '한시스템',
    size: 1456789,
    tags: ['시스템성능', '안정성', '인프라'],
    reportType: 'internal'
  },
  {
    id: 7,
    title: '디지털 채널 이용 현황',
    description: '모바일 앱 및 웹사이트 이용 패턴 분석',
    type: 'pptx',
    category: 'monthly',
    date: '2024-09-10',
    author: '송디지털',
    size: 2134567,
    tags: ['디지털채널', '모바일', '웹분석'],
    reportType: 'internal'
  },
  {
    id: 8,
    title: '영업 성과 KPI 대시보드',
    description: '지역별, 상품별 영업 성과 지표 종합 분석',
    type: 'docx',
    category: 'kpi',
    date: '2024-09-05',
    author: '윤영업',
    size: 1789012,
    tags: ['영업성과', '지역분석', '대시보드'],
    reportType: 'internal'
  }
]

// Computed
const filteredReports = computed(() => {
  let filtered = reports

  // Category filter
  if (activeCategory.value !== 'all') {
    filtered = filtered.filter(report => report.category === activeCategory.value)
  }

  // Search filter
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(report => 
      report.title.toLowerCase().includes(query) ||
      report.description.toLowerCase().includes(query) ||
      report.tags.some(tag => tag.toLowerCase().includes(query))
    )
  }

  // Category dropdown filter
  if (selectedCategory.value) {
    filtered = filtered.filter(report => report.category === selectedCategory.value)
  }

  return filtered
})

// Methods
const getCategoryCount = (categoryId) => {
  if (categoryId === 'all') return reports.length
  return reports.filter(report => report.category === categoryId).length
}

const getFileIcon = (type) => {
  const icons = {
    pdf: 'file-text',
    docx: 'file-text',
    pptx: 'presentation'
  }
  return icons[type] || 'file'
}

const formatDate = (dateString) => {
  return new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  }).format(new Date(dateString))
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const openReport = (report) => {
  selectedReport.value = report
  showViewer.value = true
  currentPage.value = 1
  currentSlide.value = 1
  zoomLevel.value = 100
  
  // Add keyboard event listener
  document.addEventListener('keydown', handleKeydown)
}

const handleKeydown = (event) => {
  if (!showViewer.value) return
  
  switch (event.key) {
    case 'Escape':
      if (isFullscreen.value) {
        isFullscreen.value = false
      } else {
        closeViewer()
      }
      break
    case 'F11':
      event.preventDefault()
      toggleFullscreen()
      break
    case 'f':
    case 'F':
      if (event.ctrlKey || event.metaKey) {
        event.preventDefault()
        toggleFullscreen()
      }
      break
  }
}

const closeViewer = () => {
  showViewer.value = false
  selectedReport.value = null
  isFullscreen.value = false
  
  // Remove keyboard event listener
  document.removeEventListener('keydown', handleKeydown)
}

const downloadReport = (report) => {
  if (report.pdfUrl) {
    // Create a temporary link element for download
    const link = document.createElement('a')
    link.href = report.pdfUrl
    link.download = `${report.title}.pdf`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  } else {
    console.log('Downloading report:', report.title)
    // Simulate download for mock reports
  }
}

const shareReport = (report) => {
  console.log('Sharing report:', report.title)
  // Simulate share
}

const printReport = () => {
  console.log('Printing report')
  // Simulate print
}

// PDF Controls
const previousPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

const zoomIn = () => {
  if (zoomLevel.value < 200) {
    zoomLevel.value += 25
  }
}

const zoomOut = () => {
  if (zoomLevel.value > 50) {
    zoomLevel.value -= 25
  }
}

const resetZoom = () => {
  zoomLevel.value = 100
}

const toggleFullscreen = () => {
  isFullscreen.value = !isFullscreen.value
}

// PPT Controls
const previousSlide = () => {
  if (currentSlide.value > 1) {
    currentSlide.value--
  }
}

const nextSlide = () => {
  if (currentSlide.value < totalSlides.value) {
    currentSlide.value++
  }
}
</script>

<style scoped>
.data-report-view {
  display: flex;
  flex-direction: column;
  padding: var(--space-4);
  gap: var(--space-4);
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

.category-filter {
  padding: var(--space-2) var(--space-3);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  background: var(--surface);
  font-size: var(--fs-sm);
  cursor: pointer;
}

/* Categories */
.categories-section {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
}

.category-tabs {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
}

.category-tab {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-4);
  border: 1px solid var(--border-primary);
  background: var(--surface);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}

.category-tab:hover {
  background: var(--surface-hover);
  color: var(--text-primary);
}

.category-tab.active {
  background: var(--lina-yellow);
  color: var(--gray-800);
  border-color: var(--lina-yellow);
}

.count {
  font-size: var(--fs-xs);
  opacity: 0.8;
}

/* Reports Grid */
.reports-section {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
}

.reports-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: var(--space-3);
}

.report-card {
  display: flex;
  flex-direction: column;
  padding: var(--space-3);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  background: var(--surface);
  position: relative;
  gap: var(--space-2);
}

.report-card:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
  border-color: var(--lina-yellow);
}

.report-header {
  display: flex;
  align-items: flex-start;
  gap: var(--space-3);
}

.report-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: var(--surface-hover);
  border-radius: var(--radius-md);
  position: relative;
  color: var(--lina-yellow-dark);
  flex-shrink: 0;
}

.file-type-badge {
  position: absolute;
  bottom: -4px;
  right: -4px;
  background: var(--lina-orange);
  color: white;
  font-size: 8px;
  font-weight: var(--fw-bold);
  padding: 1px 4px;
  border-radius: var(--radius-sm);
  line-height: 1;
}

.report-title-section {
  flex: 1;
  min-width: 0;
}

.title-row {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-bottom: var(--space-1);
}

.report-title {
  font-size: var(--fs-base);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0;
  line-height: var(--lh-tight);
  flex: 1;
}

.badges {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  flex-shrink: 0;
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
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

.report-type-badge {
  font-size: 10px;
  font-weight: var(--fw-bold);
  padding: 2px 6px;
  border-radius: var(--radius-sm);
  line-height: 1;
}

.report-type-badge.internal {
  background: var(--lina-orange);
  color: white;
}

.report-type-badge.external {
  background: #3b82f6;
  color: white;
  border: 1px solid #3b82f6;
}

.report-description {
  color: var(--text-secondary);
  margin: 0;
  line-height: var(--lh-snug);
  font-size: var(--fs-sm);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.report-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: var(--space-2);
}

.report-meta {
  display: flex;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--fs-xs);
  color: var(--text-tertiary);
}

.report-tags {
  display: flex;
  gap: var(--space-1);
  flex-wrap: wrap;
  align-items: center;
}

.tag {
  background: var(--surface-hover);
  color: var(--text-secondary);
  padding: 1px var(--space-1);
  border-radius: var(--radius-sm);
  font-size: 10px;
  font-weight: var(--fw-medium);
  line-height: 1.2;
}

.tag.more-tags {
  background: var(--lina-orange);
  color: white;
}

.report-actions {
  display: flex;
  gap: var(--space-1);
  flex-shrink: 0;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border: 1px solid var(--border-primary);
  background: var(--surface);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-secondary);
}

.action-btn:hover {
  background: var(--lina-yellow);
  color: var(--gray-800);
  border-color: var(--lina-yellow);
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--space-4);
  transition: var(--transition-normal);
}

.modal-overlay.fullscreen {
  padding: 0;
  background: rgba(0, 0, 0, 0.95);
}

.modal-content {
  background: var(--surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-xl);
  width: 95%;
  height: 90vh;
  max-width: 1400px;
  display: flex;
  flex-direction: column;
  transition: var(--transition-normal);
}

.modal-content.fullscreen {
  width: 100vw;
  height: 100vh;
  max-width: none;
  border-radius: 0;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4) var(--space-5);
  border-bottom: 1px solid var(--border-primary);
  flex-shrink: 0;
}

.fullscreen .modal-header {
  background: var(--surface);
  box-shadow: var(--shadow-sm);
}

.viewer-title {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.viewer-title h3 {
  font-size: var(--fs-lg);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0;
}

.viewer-actions {
  display: flex;
  gap: var(--space-2);
}

.viewer-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: 1px solid var(--border-primary);
  background: var(--surface);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-secondary);
}

.viewer-btn:hover {
  background: var(--surface-hover);
  color: var(--text-primary);
}

.modal-body {
  flex: 1;
  overflow: hidden;
}

/* Document Viewer */
.document-viewer {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* PDF Viewer */
.pdf-viewer {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* Mock PDF Container */
.mock-pdf-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.pdf-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-4);
  border-bottom: 1px solid var(--border-primary);
  background: var(--surface-hover);
}

.pdf-nav-controls {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.pdf-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: 1px solid var(--border-primary);
  background: var(--surface);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-secondary);
}

.pdf-btn:hover:not(:disabled) {
  background: var(--lina-yellow);
  color: var(--gray-800);
}

.pdf-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info,
.zoom-level {
  font-size: var(--fs-sm);
  color: var(--text-primary);
  font-weight: var(--fw-medium);
}

.zoom-controls {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.pdf-content {
  flex: 1;
  overflow: auto;
  padding: var(--space-4);
  background: var(--bg-secondary);
}

.pdf-page {
  background: white;
  margin: 0 auto;
  box-shadow: var(--shadow-lg);
  border-radius: var(--radius-md);
  transform-origin: top center;
  transition: transform var(--transition-fast);
}

.pdf-mock {
  padding: var(--space-8);
  min-height: 800px;
  font-family: serif;
}

.pdf-header {
  text-align: center;
  margin-bottom: var(--space-8);
  border-bottom: 2px solid var(--border-primary);
  padding-bottom: var(--space-4);
}

.pdf-header h2 {
  font-size: var(--fs-xl);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.pdf-date {
  color: var(--text-secondary);
  font-size: var(--fs-sm);
}

.pdf-section {
  margin-bottom: var(--space-6);
}

.pdf-section h3 {
  font-size: var(--fs-lg);
  color: var(--text-primary);
  margin: 0 0 var(--space-3) 0;
}

.pdf-chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  border: 2px dashed var(--border-primary);
  border-radius: var(--radius-md);
  color: var(--text-tertiary);
  margin: var(--space-4) 0;
}

/* Real PDF Embed */
.real-pdf-container {
  width: 100%;
  height: 100%;
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 600px;
}

.real-pdf-embed {
  width: 100%;
  height: 100%;
  min-height: 600px;
  border: none;
}

.fullscreen .real-pdf-embed {
  min-height: calc(100vh - 80px);
}

/* Office Viewer */
.office-viewer {
  height: 100%;
  overflow: auto;
}

.office-content {
  padding: var(--space-6);
}

.office-mock {
  background: white;
  padding: var(--space-8);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-md);
  min-height: 600px;
}

.office-header {
  margin-bottom: var(--space-6);
  border-bottom: 1px solid var(--border-primary);
  padding-bottom: var(--space-4);
}

.office-header h2 {
  font-size: var(--fs-xl);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.office-meta {
  display: flex;
  gap: var(--space-4);
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}

.word-content,
.ppt-content {
  line-height: var(--lh-relaxed);
}

.slide-container {
  background: var(--surface-hover);
  border-radius: var(--radius-md);
  padding: var(--space-6);
  margin-bottom: var(--space-4);
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.slide {
  text-align: center;
}

.slide-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-3);
}

.slide-btn {
  padding: var(--space-2) var(--space-3);
  border: 1px solid var(--border-primary);
  background: var(--surface);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-secondary);
}

.slide-btn:hover:not(:disabled) {
  background: var(--lina-yellow);
  color: var(--gray-800);
}

.slide-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Responsive */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-actions {
    width: 100%;
    flex-direction: column;
  }
  
  .search-box {
    min-width: auto;
    width: 100%;
  }
  
  .reports-grid {
    grid-template-columns: 1fr;
  }
  
  .report-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-2);
  }
  
  .report-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-2);
  }
  
  .report-meta {
    flex-direction: column;
    gap: var(--space-1);
  }
  
  .category-tabs {
    justify-content: center;
  }
  
  .modal-overlay {
    padding: var(--space-1);
  }
  
  .modal-overlay.fullscreen {
    padding: 0;
  }
  
  .modal-content {
    width: 100%;
    height: 95vh;
  }
  
  .modal-content.fullscreen {
    width: 100vw;
    height: 100vh;
  }
  
  .pdf-controls {
    flex-direction: column;
    gap: var(--space-2);
    align-items: stretch;
  }
  
  .pdf-nav-controls,
  .zoom-controls {
    justify-content: center;
  }
  
  .real-pdf-embed {
    min-height: 400px;
  }
  
  .fullscreen .real-pdf-embed {
    min-height: calc(100vh - 80px);
  }
}
</style>