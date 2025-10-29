<template>
  <div class="home-view">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <div class="hero-header">
          <h1 class="hero-title">데이터를 탐색하고 인사이트를 발견하세요</h1>
          <p class="hero-subtitle">
            강력한 분석 도구와 직관적인 인터페이스로 데이터 기반 의사결정을 지원합니다
          </p>
        </div>
        
        <div class="search-container">
          <div class="search-box">
            <div class="search-icon">
              <IconSystem name="search" :size="20" />
            </div>
            <input 
              v-model="searchQuery"
              type="text" 
              placeholder="데이터셋, 테이블, 또는 키워드를 검색하세요..."
              class="search-input"
              @keyup.enter="handleSearch"
            />
            <button 
              class="search-btn" 
              @click="handleSearch"
              :disabled="!searchQuery.trim() || isSearching"
            >
              <IconSystem v-if="!isSearching" name="arrow-right" :size="16" />
              <IconSystem v-else name="clock" :size="16" class="spinning" />
              {{ isSearching ? '검색 중...' : '검색' }}
            </button>
          </div>
        </div>

        <div class="quick-actions">
          <button class="quick-action primary" @click="createNewDashboard">
            <IconSystem name="plus" :size="20" />
            <span>새 대시보드</span>
          </button>
          <button class="quick-action" @click="importData">
            <IconSystem name="upload" :size="20" />
            <span>데이터 가져오기</span>
          </button>
          <button class="quick-action" @click="quickAction('sql')">
            <IconSystem name="code" :size="20" />
            <span>SQL 쿼리</span>
          </button>
          <button class="quick-action" @click="quickAction('api')">
            <IconSystem name="api" :size="20" />
            <span>API 테스트</span>
          </button>
        </div>
      </div>
    </section>

    <!-- Search Results -->
    <section v-if="searchResults" class="search-results-section">
      <div class="section-header">
        <h2 class="section-title">
          검색 결과
          <span class="results-count">({{ getTotalResults() }}개 항목)</span>
        </h2>
        <button class="clear-search-btn" @click="clearSearch">
          <IconSystem name="x" :size="16" />
          검색 지우기
        </button>
      </div>
      
      <div class="search-results-content">
        <div 
          v-for="(items, category) in searchResults" 
          :key="category"
          v-show="items.length > 0"
          class="result-category"
        >
          <div class="category-header" @click="toggleCategory(category)">
            <div class="category-info">
              <IconSystem :name="getCategoryIcon(category)" :size="20" />
              <h3 class="category-title">{{ getCategoryLabel(category) }}</h3>
              <span class="category-count">({{ items.length }})</span>
            </div>
            <IconSystem 
              :name="collapsedCategories[category] ? 'chevron-right' : 'chevron-down'" 
              :size="16" 
              class="category-toggle"
            />
          </div>
          
          <div 
            v-show="!collapsedCategories[category]"
            class="result-items"
          >
            <div 
              v-for="item in items" 
              :key="item.id"
              class="result-item"
            >
              <div class="result-content">
                <h4 class="result-title">{{ item.title }}</h4>
                <p class="result-description">{{ item.description }}</p>
                <div class="result-tags">
                  <span 
                    v-for="tag in item.tags" 
                    :key="tag"
                    class="result-tag"
                    :class="{ highlighted: tag.toLowerCase().includes(searchQuery.toLowerCase()) }"
                  >
                    {{ tag }}
                  </span>
                </div>
              </div>
              <div class="result-actions">
                <button class="result-btn">보기</button>
              </div>
            </div>
          </div>
        </div>
        
        <div v-if="getTotalResults() === 0" class="no-results">
          <IconSystem name="search" :size="48" />
          <h3>검색 결과가 없습니다</h3>
          <p>"{{ searchQuery }}"에 대한 결과를 찾을 수 없습니다</p>
        </div>
      </div>
    </section>

    <!-- Compact Overview -->
    <section class="overview-section">
      <div class="overview-grid">
        <!-- Updates -->
        <div class="overview-card" v-if="newUpdates.length">
          <div class="card-header">
            <IconSystem name="info" :size="20" />
            <h3>업데이트</h3>
            <span class="badge">{{ newUpdates.length }}</span>
          </div>
          <div class="card-content">
            <div 
              v-for="update in newUpdates.slice(0, 2)" 
              :key="update.id"
              class="compact-item"
            >
              <span class="item-text">{{ update.message }}</span>
              <span class="item-time">{{ update.time }}</span>
            </div>
            <button v-if="newUpdates.length > 2" class="view-more-btn">
              +{{ newUpdates.length - 2 }}개 더보기
            </button>
          </div>
        </div>

        <!-- Recent Activity -->
        <div class="overview-card">
          <div class="card-header">
            <IconSystem name="activity" :size="20" />
            <h3>최근 활동</h3>
          </div>
          <div class="card-content">
            <div 
              v-for="activity in recentActivities.slice(0, 3)" 
              :key="activity.id"
              class="compact-item"
            >
              <div class="item-main">
                <span class="item-text">{{ activity.title }}</span>
                <span class="item-status" :class="activity.status">{{ activity.statusText }}</span>
              </div>
              <span class="item-time">{{ activity.time }}</span>
            </div>
          </div>
        </div>

        <!-- Popular Datasets -->
        <div class="overview-card">
          <div class="card-header">
            <IconSystem name="database" :size="20" />
            <h3>인기 데이터셋</h3>
          </div>
          <div class="card-content">
            <div 
              v-for="dataset in popularDatasets.slice(0, 3)" 
              :key="dataset.id"
              class="compact-item"
              @click="openDataset(dataset)"
            >
              <div class="item-main">
                <span class="item-text">{{ dataset.title }}</span>
                <span class="item-meta">{{ dataset.views }} views</span>
              </div>
              <span class="item-category">{{ dataset.categoryName }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>




  </div>
</template>

<script setup>
import { ref } from 'vue'
import IconSystem from './IconSystem.vue'

const searchQuery = ref('')
const searchResults = ref(null)
const isSearching = ref(false)
const collapsedCategories = ref({})

// Sample data for search
const sampleData = {
  dashboards: [
    { id: 1, title: 'Claim Summary Dashboard', description: '보험 청구 현황과 처리 상태를 종합적으로 관리하는 대시보드', tags: ['claim', 'insurance', 'summary'] },
    { id: 2, title: 'Claim Processing Monitor', description: '실시간 보험금 청구 처리 모니터링 대시보드', tags: ['claim', 'processing', 'realtime'] },
    { id: 3, title: 'Customer Analytics', description: '고객 행동 패턴 및 세그먼트 분석', tags: ['customer', 'analytics'] },
    { id: 4, title: 'Sales Performance', description: '영업 성과 및 KPI 모니터링', tags: ['sales', 'kpi'] },
    { id: 5, title: 'Claim Fraud Analytics', description: '보험금 청구 사기 패턴 분석 대시보드', tags: ['claim', 'fraud', 'analytics'] },
    { id: 6, title: 'Policy Performance', description: '보험 상품별 성과 분석', tags: ['policy', 'performance'] },
    { id: 7, title: 'Underwriting Dashboard', description: '언더라이팅 프로세스 모니터링', tags: ['underwriting', 'process'] }
  ],
  reports: [
    { id: 1, title: 'Monthly Claim Report', description: '월간 보험금 청구 분석 리포트', tags: ['claim', 'monthly', 'report'] },
    { id: 2, title: 'Claim Settlement Analysis', description: '보험금 지급 현황 및 트렌드 분석 보고서', tags: ['claim', 'settlement', 'analysis'] },
    { id: 3, title: 'Customer Satisfaction Survey', description: '고객 만족도 조사 결과', tags: ['customer', 'satisfaction'] },
    { id: 4, title: 'Risk Assessment Report', description: '리스크 평가 및 분석 보고서', tags: ['risk', 'assessment'] },
    { id: 5, title: 'Claim Frequency Report', description: '보험금 청구 빈도 분석 리포트', tags: ['claim', 'frequency', 'statistics'] },
    { id: 6, title: 'Annual Claim Summary', description: '연간 보험금 청구 종합 보고서', tags: ['claim', 'annual', 'summary'] },
    { id: 7, title: 'Regulatory Compliance Report', description: '규제 준수 현황 보고서', tags: ['compliance', 'regulatory'] }
  ],
  tables: [
    { id: 1, title: 'claims', description: '보험 청구 데이터 테이블', tags: ['claim', 'insurance', 'data'] },
    { id: 2, title: 'claim_details', description: '보험금 청구 상세 정보 테이블', tags: ['claim', 'details', 'transaction'] },
    { id: 3, title: 'customers', description: '고객 정보 마스터 테이블', tags: ['customer', 'master'] },
    { id: 4, title: 'policies', description: '보험 계약 정보 테이블', tags: ['policy', 'contract'] },
    { id: 5, title: 'claim_history', description: '보험금 청구 이력 테이블', tags: ['claim', 'history', 'audit'] },
    { id: 6, title: 'claim_documents', description: '보험금 청구 관련 문서 테이블', tags: ['claim', 'documents', 'files'] },
    { id: 7, title: 'underwriting_data', description: '언더라이팅 데이터 테이블', tags: ['underwriting', 'risk', 'assessment'] },
    { id: 8, title: 'premium_payments', description: '보험료 납입 내역 테이블', tags: ['premium', 'payment', 'billing'] }
  ],
  models: [
    { id: 1, title: 'Claim Fraud Detection', description: '보험금 청구 사기 탐지 모델', tags: ['claim', 'fraud', 'ml'] },
    { id: 2, title: 'Claim Amount Prediction', description: '보험금 청구 금액 예측 모델', tags: ['claim', 'prediction', 'amount'] },
    { id: 3, title: 'Customer Churn Prediction', description: '고객 이탈 예측 모델', tags: ['customer', 'churn', 'prediction'] },
    { id: 4, title: 'Risk Scoring Model', description: '리스크 점수 산출 모델', tags: ['risk', 'scoring'] },
    { id: 5, title: 'Claim Processing Time Prediction', description: '보험금 청구 처리 시간 예측 모델', tags: ['claim', 'processing', 'time'] },
    { id: 6, title: 'Claim Approval Probability', description: '보험금 청구 승인 확률 예측 모델', tags: ['claim', 'approval', 'probability'] },
    { id: 7, title: 'Premium Pricing Model', description: '보험료 산정 모델', tags: ['premium', 'pricing', 'actuarial'] }
  ],
  apis: [
    { id: 1, title: 'Claim Processing API', description: '보험금 청구 처리를 위한 REST API', tags: ['claim', 'api', 'rest'] },
    { id: 2, title: 'Claim Status API', description: '보험금 청구 상태 조회 API', tags: ['claim', 'status', 'inquiry'] },
    { id: 3, title: 'Customer Data API', description: '고객 데이터 조회 및 관리 API', tags: ['customer', 'data', 'api'] },
    { id: 4, title: 'Policy Management API', description: '보험 계약 관리 API', tags: ['policy', 'management'] },
    { id: 5, title: 'Claim Submission API', description: '보험금 청구 접수 API', tags: ['claim', 'submission', 'intake'] },
    { id: 6, title: 'Claim Documents API', description: '보험금 청구 문서 관리 API', tags: ['claim', 'documents', 'upload'] },
    { id: 7, title: 'Underwriting API', description: '언더라이팅 프로세스 API', tags: ['underwriting', 'process', 'automation'] },
    { id: 8, title: 'Premium Calculation API', description: '보험료 계산 API', tags: ['premium', 'calculation', 'pricing'] }
  ]
}


const recentActivities = [
  {
    id: 1,
    icon: 'dashboard',
    type: 'dashboard',
    title: '매출 분석 대시보드 업데이트',
    description: '2024년 Q1 매출 데이터 시각화 완료',
    user: '김데이터',
    time: '5분 전',
    status: 'success',
    statusText: '완료'
  },
  {
    id: 2,
    icon: 'code',
    type: 'query',
    title: '고객 세그먼트 쿼리 실행',
    description: '활성 사용자 분석을 위한 복합 쿼리',
    user: '이분석',
    time: '12분 전',
    status: 'running',
    statusText: '실행중'
  },
  {
    id: 3,
    icon: 'file',
    type: 'report',
    title: '월간 리포트 생성',
    description: '자동화된 월간 성과 리포트',
    user: '박리포트',
    time: '1시간 전',
    status: 'success',
    statusText: '완료'
  }
]

const getStatusIcon = (status) => {
  const icons = {
    success: 'check',
    running: 'clock',
    error: 'alert-circle'
  }
  return icons[status] || 'info'
}

const popularDatasets = [
  {
    id: 1,
    icon: 'database',
    category: 'finance',
    categoryName: '금융',
    title: '신용카드 거래 데이터',
    description: '실시간 신용카드 거래 내역 및 패턴 분석을 위한 종합 데이터셋',
    views: '2.1k',
    queries: '456',
    updated: '2시간 전',
    tags: ['실시간', '거래', '분석']
  },
  {
    id: 2,
    icon: 'users',
    category: 'customer',
    categoryName: '고객',
    title: '고객 행동 분석',
    description: '웹사이트 및 모바일 앱 내 고객 행동 패턴과 선호도 분석 데이터',
    views: '1.8k',
    queries: '321',
    updated: '4시간 전',
    tags: ['행동', '패턴', 'UX']
  },
  {
    id: 3,
    icon: 'trending-up',
    category: 'sales',
    categoryName: '매출',
    title: '월별 매출 현황',
    description: '제품별, 지역별 매출 현황 및 트렌드 분석을 위한 통합 데이터',
    views: '1.5k',
    queries: '289',
    updated: '1일 전',
    tags: ['매출', '트렌드', '지역']
  }
]

const handleSearch = () => {
  console.log('검색 버튼 클릭됨, 검색어:', searchQuery.value)
  if (!searchQuery.value.trim()) return
  
  isSearching.value = true
  console.log('검색 시작...')
  
  // Simulate search delay
  setTimeout(() => {
    const query = searchQuery.value.toLowerCase()
    const results = {}
    
    // Search in each category
    Object.keys(sampleData).forEach(category => {
      results[category] = sampleData[category].filter(item => 
        item.title.toLowerCase().includes(query) ||
        item.description.toLowerCase().includes(query) ||
        item.tags.some(tag => tag.toLowerCase().includes(query))
      )
    })
    
    searchResults.value = results
    isSearching.value = false
    console.log('검색 완료, 결과:', results)
  }, 800)
}

const clearSearch = () => {
  searchQuery.value = ''
  searchResults.value = null
}

const getCategoryIcon = (category) => {
  const icons = {
    dashboards: 'activity',
    reports: 'file-text',
    tables: 'database',
    models: 'box',
    apis: 'api'
  }
  return icons[category] || 'file'
}

const getCategoryLabel = (category) => {
  const labels = {
    dashboards: '대시보드',
    reports: '리포트',
    tables: '테이블',
    models: '모델',
    apis: 'API'
  }
  return labels[category] || category
}

const getTotalResults = () => {
  if (!searchResults.value) return 0
  return Object.values(searchResults.value).reduce((total, items) => total + items.length, 0)
}

const toggleCategory = (category) => {
  collapsedCategories.value[category] = !collapsedCategories.value[category]
}

const quickAction = (action) => {
  console.log('빠른 액션:', action)
  // 해당 페이지로 이동
}

const createNewDashboard = () => {
  console.log('새 대시보드 생성')
  // Navigate to dashboard creation
}

const importData = () => {
  console.log('데이터 가져오기')
  // Open data import modal
}

// New updates data
const newUpdates = ref([
  {
    id: 1,
    type: 'dashboard',
    message: '새로운 "고객 행동 분석" 대시보드가 추가되었습니다',
    time: '2시간 전',
    actionText: '확인하기'
  },
  {
    id: 2,
    type: 'data',
    message: '2025년 1분기 매출 데이터가 업데이트되었습니다',
    time: '5시간 전',
    actionText: '데이터 보기'
  },
  {
    id: 3,
    type: 'feature',
    message: 'Text-to-SQL 기능에 새로운 AI 모델이 적용되었습니다',
    time: '1일 전',
    actionText: '사용해보기'
  }
])

const viewUpdate = (update) => {
  console.log('업데이트 보기:', update)
  // Navigate to relevant page based on update type
}

const dismissUpdate = (updateId) => {
  const index = newUpdates.value.findIndex(update => update.id === updateId)
  if (index > -1) {
    newUpdates.value.splice(index, 1)
  }
}

const openDataset = (dataset) => {
  console.log('데이터셋 열기:', dataset.title)
  // 데이터셋 상세 페이지로 이동
}
</script>

<style scoped>
.home-view {
  display: flex;
  flex-direction: column;
  gap: var(--space-8);
  max-width: 1200px;
  margin: 0 auto;
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, 
    var(--surface) 0%, 
    color-mix(in srgb, var(--primary) 2%, var(--surface)) 100%);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-2xl);
  padding: var(--space-16) var(--space-8);
  text-align: center;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, 
    transparent 0%, 
    var(--primary) 50%, 
    transparent 100%);
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: var(--space-8);
}

.hero-header {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.hero-title {
  font-size: var(--fs-4xl);
  font-weight: var(--fw-bold);
  color: var(--text-primary);
  margin: 0;
  line-height: var(--lh-tight);
  letter-spacing: -0.025em;
}

.hero-subtitle {
  font-size: var(--fs-lg);
  color: var(--text-secondary);
  margin: 0;
  line-height: var(--lh-relaxed);
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.search-container {
  display: flex;
  justify-content: center;
}

.search-box {
  display: flex;
  align-items: center;
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-2);
  box-shadow: var(--shadow-sm);
  transition: var(--transition-fast);
  width: 100%;
  max-width: 600px;
  position: relative;
}

.search-box::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: var(--radius-xl);
  padding: 1px;
  background: linear-gradient(135deg, var(--primary), var(--primary-light));
  mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  mask-composite: xor;
  opacity: 0;
  transition: var(--transition-fast);
  pointer-events: none;
  z-index: -1;
}

.search-box:focus-within {
  border-color: var(--primary);
  box-shadow: var(--shadow-md);
}

.search-box:focus-within::before {
  opacity: 1;
}

.search-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 var(--space-4);
  color: var(--text-tertiary);
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  padding: var(--space-4) var(--space-2);
  font-size: var(--fs-base);
  background: transparent;
  color: var(--text-primary);
  position: relative;
  z-index: 1;
}

.search-input::placeholder {
  color: var(--text-tertiary);
}

.search-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  background: linear-gradient(135deg, var(--lina-yellow) 0%, var(--lina-orange) 100%);
  color: var(--text-primary);
  border: none;
  padding: var(--space-3) var(--space-5);
  border-radius: var(--radius-lg);
  font-weight: var(--fw-bold);
  cursor: pointer;
  transition: var(--transition-fast);
  font-size: var(--fs-sm);
  box-shadow: 0 2px 8px rgba(255, 215, 0, 0.3);
}

.search-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, var(--lina-yellow-light) 0%, var(--lina-orange-light) 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 215, 0, 0.4);
}

.search-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.spinning {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* Compact Overview Section */
.overview-section {
  margin-bottom: var(--space-8);
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: var(--space-6);
}

.overview-card {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  overflow: hidden;
  transition: var(--transition-fast);
}

.overview-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
  border-color: var(--lina-yellow);
}

.card-header {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-4) var(--space-5);
  background: var(--surface-hover);
  border-bottom: 1px solid var(--border-primary);
}

.card-header h3 {
  font-size: var(--fs-base);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0;
  flex: 1;
}

.badge {
  background: var(--lina-yellow);
  color: var(--gray-800);
  font-size: var(--fs-xs);
  font-weight: var(--fw-bold);
  padding: 2px 8px;
  border-radius: var(--radius-full);
  min-width: 20px;
  text-align: center;
}

.card-content {
  padding: var(--space-4);
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.compact-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-3);
  padding: var(--space-2) 0;
  border-bottom: 1px solid var(--border-secondary);
  cursor: pointer;
  transition: var(--transition-fast);
}

.compact-item:last-child {
  border-bottom: none;
}

.compact-item:hover {
  background: var(--surface-hover);
  margin: 0 calc(-1 * var(--space-2));
  padding: var(--space-2);
  border-radius: var(--radius-md);
}

.item-main {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
  flex: 1;
  min-width: 0;
}

.item-text {
  font-size: var(--fs-sm);
  font-weight: var(--fw-medium);
  color: var(--text-primary);
  line-height: var(--lh-snug);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-time {
  font-size: var(--fs-xs);
  color: var(--text-tertiary);
  white-space: nowrap;
}

.item-status {
  font-size: var(--fs-xs);
  font-weight: var(--fw-medium);
  padding: 2px 6px;
  border-radius: var(--radius-sm);
}

.item-status.success {
  background: color-mix(in srgb, var(--success) 10%, transparent);
  color: var(--success);
}

.item-status.running {
  background: color-mix(in srgb, var(--warning) 10%, transparent);
  color: var(--warning);
}

.item-status.error {
  background: color-mix(in srgb, var(--error) 10%, transparent);
  color: var(--error);
}

.item-meta {
  font-size: var(--fs-xs);
  color: var(--text-secondary);
}

.item-category {
  font-size: var(--fs-xs);
  color: var(--lina-orange);
  font-weight: var(--fw-medium);
  white-space: nowrap;
}

.view-more-btn {
  background: none;
  border: 1px solid var(--border-primary);
  color: var(--text-secondary);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-md);
  font-size: var(--fs-xs);
  cursor: pointer;
  transition: var(--transition-fast);
  align-self: flex-start;
}

.view-more-btn:hover {
  border-color: var(--lina-yellow);
  color: var(--lina-orange);
  background: var(--surface-hover);
}

.quick-actions {
  display: flex;
  gap: var(--space-4);
  justify-content: center;
  flex-wrap: wrap;
}

.quick-action {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-5);
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-secondary);
  font-size: var(--fs-sm);
  font-weight: var(--fw-medium);
}

.quick-action:hover {
  background: var(--surface-hover);
  border-color: var(--lina-yellow);
  color: var(--lina-orange);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.quick-action.primary {
  background: var(--lina-yellow);
  color: var(--gray-800);
  border-color: var(--lina-yellow);
}

.quick-action.primary:hover {
  background: var(--lina-yellow-light);
  border-color: var(--lina-yellow-light);
  color: var(--gray-800);
}

/* Updates Banner */
.updates-banner {
  background: linear-gradient(135deg, 
    color-mix(in srgb, var(--info) 8%, var(--surface)) 0%, 
    color-mix(in srgb, var(--info) 4%, var(--surface)) 100%);
  border: 1px solid color-mix(in srgb, var(--info) 20%, var(--border-primary));
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  margin-bottom: var(--space-8);
  position: relative;
  overflow: hidden;
}

.updates-banner::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, 
    var(--info) 0%, 
    var(--lina-yellow) 50%, 
    var(--info) 100%);
}

.banner-content {
  display: flex;
  gap: var(--space-4);
  align-items: flex-start;
}

.banner-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: var(--info);
  color: white;
  border-radius: var(--radius-full);
  flex-shrink: 0;
}

.banner-messages {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.update-message {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4);
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  transition: var(--transition-fast);
}

.update-message:hover {
  background: var(--surface-hover);
  transform: translateX(4px);
}

.update-message.dashboard {
  border-left: 4px solid var(--lina-orange);
}

.update-message.data {
  border-left: 4px solid var(--success);
}

.update-message.feature {
  border-left: 4px solid var(--info);
}

.update-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.update-text {
  color: var(--text-primary);
  font-weight: var(--fw-medium);
  line-height: var(--lh-snug);
}

.update-time {
  color: var(--text-tertiary);
  font-size: var(--fs-sm);
}

.update-action {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  background: var(--lina-yellow);
  color: var(--gray-800);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--fs-sm);
  font-weight: var(--fw-medium);
  cursor: pointer;
  transition: var(--transition-fast);
  white-space: nowrap;
}

.update-action:hover {
  background: var(--lina-yellow-light);
  color: var(--gray-800);
  transform: translateY(-1px);
}

.update-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: none;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-tertiary);
}

.update-close:hover {
  background: var(--surface-active);
  color: var(--text-primary);
}

/* Section Headers */
.section-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.section-title {
  font-size: var(--fs-2xl);
  font-weight: var(--fw-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
  letter-spacing: -0.025em;
}

.section-subtitle {
  font-size: var(--fs-base);
  color: var(--text-secondary);
  margin: 0;
  line-height: var(--lh-relaxed);
}

/* Stats Section */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-6);
}

.stat-card {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  transition: var(--transition-fast);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--primary), var(--primary-light));
  transform: scaleX(0);
  transition: var(--transition-fast);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--primary);
}

.stat-card:hover::before {
  transform: scaleX(1);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--lina-yellow), var(--lina-orange));
  transform: scaleX(0);
  transition: var(--transition-fast);
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--space-4);
}

.stat-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, 
    color-mix(in srgb, var(--lina-yellow) 20%, transparent), 
    color-mix(in srgb, var(--lina-orange) 15%, transparent));
  border-radius: var(--radius-lg);
  color: var(--lina-orange);
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-md);
}

.stat-trend.up {
  color: var(--success);
  background: color-mix(in srgb, var(--success) 10%, transparent);
}

.stat-trend.down {
  color: var(--error);
  background: color-mix(in srgb, var(--error) 10%, transparent);
}

.stat-content {
  margin-bottom: var(--space-4);
}

.stat-value {
  font-size: var(--fs-3xl);
  font-weight: var(--fw-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
  line-height: var(--lh-tight);
}

.stat-label {
  font-size: var(--fs-sm);
  color: var(--text-secondary);
  margin: 0;
  font-weight: var(--fw-medium);
}

.stat-progress {
  height: 4px;
  background: var(--surface-active);
  border-radius: var(--radius-full);
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, var(--lina-yellow), var(--lina-orange));
  border-radius: var(--radius-full);
  transition: width 1s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 1px 3px rgba(255, 215, 0, 0.3);
}

/* Section Headers */
.section-header-inline {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--space-6);
  gap: var(--space-4);
}

.view-all-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  background: var(--surface);
  border: 1px solid var(--border-primary);
  color: var(--text-secondary);
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: var(--transition-fast);
  font-size: var(--fs-sm);
  font-weight: var(--fw-medium);
  white-space: nowrap;
}

.view-all-btn:hover {
  border-color: var(--lina-yellow);
  color: var(--lina-orange);
  background: var(--surface-hover);
}

/* Activity Section */
.activity-list {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: var(--space-4);
  padding: var(--space-5);
  border-bottom: 1px solid var(--border-primary);
  transition: var(--transition-fast);
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item:hover {
  background: var(--surface-hover);
}

.activity-icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-lg);
  flex-shrink: 0;
}

.activity-icon.dashboard {
  background: color-mix(in srgb, var(--primary) 10%, transparent);
  color: var(--primary);
}

.activity-icon.query {
  background: color-mix(in srgb, var(--success) 10%, transparent);
  color: var(--success);
}

.activity-icon.report {
  background: color-mix(in srgb, var(--warning) 10%, transparent);
  color: var(--warning);
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-title {
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
  font-size: var(--fs-base);
}

.activity-description {
  color: var(--text-secondary);
  margin: 0 0 var(--space-2) 0;
  font-size: var(--fs-sm);
  line-height: var(--lh-snug);
}

.activity-meta {
  display: flex;
  gap: var(--space-3);
  font-size: var(--fs-xs);
  color: var(--text-tertiary);
}

.activity-user {
  font-weight: var(--fw-medium);
}

.activity-status {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--fs-xs);
  font-weight: var(--fw-semibold);
  white-space: nowrap;
  flex-shrink: 0;
}

.activity-status.success {
  background: color-mix(in srgb, var(--success) 10%, transparent);
  color: var(--success);
}

.activity-status.running {
  background: color-mix(in srgb, var(--warning) 10%, transparent);
  color: var(--warning);
}

.activity-status.error {
  background: color-mix(in srgb, var(--error) 10%, transparent);
  color: var(--error);
}

/* Datasets Section */
.datasets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: var(--space-6);
}

.dataset-card {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  cursor: pointer;
  transition: var(--transition-normal);
  box-shadow: var(--shadow-sm);
  position: relative;
  overflow: hidden;
}

.dataset-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--primary), var(--primary-light));
  transform: scaleX(0);
  transition: var(--transition-fast);
}

.dataset-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-xl);
  border-color: var(--primary);
}

.dataset-card:hover::before {
  transform: scaleX(1);
}

.dataset-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--lina-yellow), var(--lina-orange));
  transform: scaleX(0);
  transition: var(--transition-fast);
}

.dataset-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--space-4);
}

.dataset-icon {
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-lg);
}

.dataset-icon.finance {
  background: color-mix(in srgb, var(--lina-yellow) 20%, transparent);
  color: var(--lina-orange);
}

.dataset-icon.customer {
  background: color-mix(in srgb, var(--success) 10%, transparent);
  color: var(--success);
}

.dataset-icon.sales {
  background: color-mix(in srgb, var(--warning) 10%, transparent);
  color: var(--warning);
}

.dataset-meta {
  text-align: right;
  font-size: var(--fs-xs);
  color: var(--text-tertiary);
}

.dataset-category {
  display: block;
  font-weight: var(--fw-semibold);
  color: var(--text-secondary);
  margin-bottom: var(--space-1);
}

.dataset-updated {
  color: var(--text-tertiary);
}

.dataset-title {
  font-size: var(--fs-lg);
  font-weight: var(--fw-bold);
  margin: 0 0 var(--space-2) 0;
  color: var(--text-primary);
  line-height: var(--lh-snug);
}

.dataset-description {
  color: var(--text-secondary);
  margin: 0 0 var(--space-5) 0;
  line-height: var(--lh-relaxed);
  font-size: var(--fs-sm);
}

.dataset-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: var(--space-4);
}

.dataset-stats {
  display: flex;
  gap: var(--space-4);
}

.stat {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--fs-xs);
  color: var(--text-tertiary);
  font-weight: var(--fw-medium);
}

.dataset-tags {
  display: flex;
  gap: var(--space-1);
  flex-wrap: wrap;
}

.tag {
  background: var(--surface-active);
  color: var(--text-secondary);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-md);
  font-size: var(--fs-xs);
  font-weight: var(--fw-medium);
  border: 1px solid var(--border-primary);
}

/* Responsive Design */
@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  }
  
  .datasets-grid {
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: var(--space-12) var(--space-6);
  }
  
  .hero-title {
    font-size: var(--fs-3xl);
  }
  
  .hero-subtitle {
    font-size: var(--fs-base);
  }
  
  .quick-actions {
    flex-direction: column;
    align-items: stretch;
  }
  
  .quick-action {
    justify-content: center;
  }
  
  .updates-banner {
    padding: var(--space-4);
  }
  
  .banner-content {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .update-message {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-3);
  }
  
  .update-action {
    align-self: stretch;
    justify-content: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }
  
  .datasets-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }
  
  .section-header-inline {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-3);
  }
  
  .view-all-btn {
    align-self: flex-end;
  }
  
  .activity-item {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-3);
  }
  
  .activity-status {
    align-self: flex-end;
  }
}

@media (max-width: 480px) {
  .hero-section {
    padding: var(--space-10) var(--space-4);
  }
  
  .hero-title {
    font-size: var(--fs-2xl);
  }
  
  .search-box {
    flex-direction: column;
    gap: var(--space-2);
    padding: var(--space-3);
  }
  
  .search-btn {
    width: 100%;
    justify-content: center;
  }
  
  .dataset-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-3);
  }
  
  .result-item {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-3);
  }
  
  .result-actions {
    align-self: flex-end;
  }
  
  .category-header {
    flex-wrap: wrap;
    gap: var(--space-2);
  }
}

/* Search Results */
.search-results-section {
  margin-top: var(--space-8);
}

.results-count {
  font-size: var(--fs-base);
  color: var(--text-secondary);
  font-weight: var(--fw-normal);
}

.clear-search-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  border: 1px solid var(--border-primary);
  background: var(--surface);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}

.clear-search-btn:hover {
  background: var(--surface-hover);
  color: var(--text-primary);
}

.search-results-content {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.result-category {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.category-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
  padding: var(--space-4) var(--space-5);
  background: var(--surface-hover);
  border-bottom: 1px solid var(--border-primary);
  cursor: pointer;
  transition: var(--transition-fast);
}

.category-header:hover {
  background: var(--surface-active);
}

.category-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  flex: 1;
}

.category-toggle {
  color: var(--text-secondary);
  transition: var(--transition-fast);
}

.category-title {
  font-size: var(--fs-lg);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0;
  flex: 1;
}

.category-count {
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}

.result-items {
  padding: var(--space-4);
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
  transition: all var(--transition-normal);
  overflow: hidden;
}

.result-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-4);
  padding: var(--space-3);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  transition: var(--transition-fast);
}

.result-item:hover {
  background: var(--surface-hover);
  border-color: var(--lina-yellow);
}

.result-content {
  flex: 1;
}

.result-title {
  font-size: var(--fs-base);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.result-description {
  font-size: var(--fs-sm);
  color: var(--text-secondary);
  margin: 0 0 var(--space-3) 0;
  line-height: var(--lh-relaxed);
}

.result-tags {
  display: flex;
  gap: var(--space-1);
  flex-wrap: wrap;
}

.result-tag {
  background: var(--surface-hover);
  color: var(--text-secondary);
  padding: 2px var(--space-2);
  border-radius: var(--radius-sm);
  font-size: var(--fs-xs);
  font-weight: var(--fw-medium);
  transition: var(--transition-fast);
}

.result-tag.highlighted {
  background: var(--lina-yellow);
  color: var(--gray-800);
}

.result-actions {
  display: flex;
  gap: var(--space-2);
}

.result-btn {
  padding: var(--space-2) var(--space-3);
  border: 1px solid var(--border-primary);
  background: var(--surface);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}

.result-btn:hover {
  background: var(--lina-yellow);
  color: var(--gray-800);
  border-color: var(--lina-yellow);
}

.no-results {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-8);
  text-align: center;
  color: var(--text-secondary);
}

.no-results h3 {
  margin: var(--space-4) 0 var(--space-2) 0;
  color: var(--text-primary);
}

.no-results p {
  margin: 0;
}
</style>