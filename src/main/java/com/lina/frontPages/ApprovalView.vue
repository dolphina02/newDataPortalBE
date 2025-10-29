<template>
  <div class="approval-view">
    <div class="approval-header">
      <h2>승인관리</h2>
      <p>대시보드 배포, 구독, 데이터 접근에 대한 승인 요청을 관리합니다.</p>
    </div>

    <!-- Tab Navigation -->
    <div class="tab-navigation">
      <button 
        v-for="tab in tabs" 
        :key="tab.id"
        class="tab-btn" 
        :class="{ active: activeTab === tab.id }"
        @click="activeTab = tab.id"
      >
        <IconSystem :name="tab.icon" :size="16" />
        {{ tab.label }}
        <span v-if="tab.count > 0" class="tab-count">{{ tab.count }}</span>
      </button>
    </div>

    <!-- Tab Content -->
    <div class="tab-content">
      <!-- 상신 탭 -->
      <div v-if="activeTab === 'submitted'" class="tab-panel">
        <div class="panel-header">
          <h3>내가 요청한 승인</h3>
          <div class="filter-actions">
            <select v-model="submittedFilter" class="filter-select">
              <option value="all">전체</option>
              <option value="pending">대기중</option>
              <option value="approved">승인됨</option>
              <option value="rejected">거절됨</option>
            </select>
          </div>
        </div>

        <div class="request-list">
          <div 
            v-for="request in filteredSubmittedRequests" 
            :key="request.id"
            class="request-card"
          >
            <div class="request-header">
              <div class="request-type">
                <IconSystem :name="getRequestIcon(request.type)" :size="20" />
                <span class="type-label">{{ getRequestTypeLabel(request.type) }}</span>
              </div>
              <div class="request-status" :class="request.status">
                {{ getStatusLabel(request.status) }}
              </div>
            </div>
            
            <div class="request-content">
              <h4>{{ request.title }}</h4>
              <p>{{ request.description }}</p>
              
              <div class="request-meta">
                <span class="meta-item">
                  <IconSystem name="calendar" :size="14" />
                  요청일: {{ formatDate(request.requestDate) }}
                </span>
                <span v-if="request.reviewDate" class="meta-item">
                  <IconSystem name="check" :size="14" />
                  처리일: {{ formatDate(request.reviewDate) }}
                </span>
              </div>

              <!-- 진행 상태 표시 (배포 요청의 경우) -->
              <div v-if="request.type === 'deploy'" class="progress-steps">
                <div class="step" :class="{ active: request.currentStep >= 1, completed: request.currentStep > 1 }">
                  <div class="step-icon">1</div>
                  <span>배포요청</span>
                </div>
                <div class="step-line" :class="{ active: request.currentStep > 1 }"></div>
                <div class="step" :class="{ active: request.currentStep >= 2, completed: request.currentStep > 2 }">
                  <div class="step-icon">2</div>
                  <span>검토</span>
                </div>
                <div class="step-line" :class="{ active: request.currentStep > 2 }"></div>
                <div class="step" :class="{ active: request.currentStep >= 3, completed: request.currentStep > 3 }">
                  <div class="step-icon">3</div>
                  <span>배포완료</span>
                </div>
              </div>
            </div>

            <div v-if="request.reviewComment" class="request-comment">
              <strong>검토 의견:</strong>
              <p>{{ request.reviewComment }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 승인 탭 -->
      <div v-if="activeTab === 'approval'" class="tab-panel">
        <div class="panel-header">
          <h3>승인 대기 목록</h3>
          <div class="filter-actions">
            <select v-model="approvalFilter" class="filter-select">
              <option value="all">전체</option>
              <option value="dashboard">대시보드 구독</option>
              <option value="publish">대시보드 배포</option>
              <option value="data">데이터 접근</option>
            </select>
          </div>
        </div>

        <div class="request-list">
          <div 
            v-for="request in filteredApprovalRequests" 
            :key="request.id"
            class="request-card approval-card"
          >
            <div class="request-header">
              <div class="request-type">
                <IconSystem :name="getRequestIcon(request.type)" :size="20" />
                <span class="type-label">{{ getRequestTypeLabel(request.type) }}</span>
                <span class="priority" :class="request.priority">{{ request.priority }}</span>
              </div>
              <div class="request-actions">
                <button class="approve-btn" @click="approveRequest(request.id)">
                  <IconSystem name="check" :size="16" />
                  승인
                </button>
                <button class="reject-btn" @click="rejectRequest(request.id)">
                  <IconSystem name="x" :size="16" />
                  거절
                </button>
              </div>
            </div>
            
            <div class="request-content">
              <h4>{{ request.title }}</h4>
              <p>{{ request.description }}</p>
              
              <div class="request-details">
                <div class="detail-item">
                  <strong>요청자:</strong> {{ request.requester }}
                </div>
                <div class="detail-item">
                  <strong>요청일:</strong> {{ formatDate(request.requestDate) }}
                </div>
                <div v-if="request.reason" class="detail-item">
                  <strong>요청 사유:</strong> {{ request.reason }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 결재완료 탭 -->
      <div v-if="activeTab === 'completed'" class="tab-panel">
        <div class="panel-header">
          <h3>결재 완료 목록</h3>
          <div class="filter-actions">
            <select v-model="completedFilter" class="filter-select">
              <option value="all">전체</option>
              <option value="approved">승인됨</option>
              <option value="rejected">거절됨</option>
            </select>
            <input 
              type="date" 
              v-model="dateFilter" 
              class="date-filter"
              placeholder="날짜 필터"
            >
          </div>
        </div>

        <div class="request-list">
          <div 
            v-for="request in filteredCompletedRequests" 
            :key="request.id"
            class="request-card completed-card"
          >
            <div class="request-header">
              <div class="request-type">
                <IconSystem :name="getRequestIcon(request.type)" :size="20" />
                <span class="type-label">{{ getRequestTypeLabel(request.type) }}</span>
              </div>
              <div class="request-status" :class="request.status">
                {{ getStatusLabel(request.status) }}
              </div>
            </div>
            
            <div class="request-content">
              <h4>{{ request.title }}</h4>
              <p>{{ request.description }}</p>
              
              <div class="request-meta">
                <span class="meta-item">
                  <IconSystem name="user" :size="14" />
                  요청자: {{ request.requester }}
                </span>
                <span class="meta-item">
                  <IconSystem name="calendar" :size="14" />
                  처리일: {{ formatDate(request.reviewDate) }}
                </span>
                <span class="meta-item">
                  <IconSystem name="user-check" :size="14" />
                  처리자: {{ request.reviewer }}
                </span>
              </div>
            </div>

            <div v-if="request.reviewComment" class="request-comment">
              <strong>처리 의견:</strong>
              <p>{{ request.reviewComment }}</p>
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
const activeTab = ref('submitted')
const submittedFilter = ref('all')
const approvalFilter = ref('all')
const completedFilter = ref('all')
const dateFilter = ref('')

// Tab configuration
const tabs = [
  { id: 'submitted', label: '상신', icon: 'upload', count: 5 },
  { id: 'approval', label: '승인', icon: 'check-circle', count: 3 },
  { id: 'completed', label: '결재완료', icon: 'archive', count: 12 }
]

// Sample data
const submittedRequests = ref([
  {
    id: 'req_001',
    type: 'deploy',
    title: 'Sales Dashboard 배포 요청',
    description: '영업팀 대시보드를 프로덕션 환경에 배포 요청합니다.',
    status: 'pending',
    currentStep: 2,
    requestDate: '2024-01-15',
    reviewDate: null,
    reviewComment: null
  },
  {
    id: 'req_002',
    type: 'dashboard',
    title: '민감정보 포함 대시보드 구독',
    description: '고객 개인정보가 포함된 마케팅 대시보드 구독을 요청합니다.',
    status: 'approved',
    requestDate: '2024-01-10',
    reviewDate: '2024-01-12',
    reviewComment: '보안 검토 완료 후 승인합니다.'
  },
  {
    id: 'req_003',
    type: 'data',
    title: 'Customer DB 테이블 접근 권한',
    description: 'customer_info 테이블에 대한 읽기 권한을 요청합니다.',
    status: 'rejected',
    requestDate: '2024-01-08',
    reviewDate: '2024-01-09',
    reviewComment: '추가 보안 승인이 필요합니다.'
  }
])

const approvalRequests = ref([
  {
    id: 'app_001',
    type: 'dashboard',
    title: '재무 대시보드 구독 요청',
    description: '재무팀 전용 대시보드 구독을 요청합니다.',
    requester: '김영희',
    requestDate: '2024-01-16',
    reason: '월간 재무 보고서 작성을 위해 필요합니다.',
    priority: 'high'
  },
  {
    id: 'app_002',
    type: 'publish',
    title: 'HR Analytics Dashboard 배포',
    description: '인사 분석 대시보드의 신규 배포를 요청합니다.',
    requester: '박민수',
    requestDate: '2024-01-15',
    reason: '인사팀 업무 효율성 향상을 위해 필요합니다.',
    priority: 'medium'
  },
  {
    id: 'app_003',
    type: 'data',
    title: 'Sales Data 접근 권한',
    description: 'sales_transactions 테이블 접근 권한을 요청합니다.',
    requester: '이철수',
    requestDate: '2024-01-14',
    reason: '분기별 영업 실적 분석을 위해 필요합니다.',
    priority: 'low'
  }
])

const completedRequests = ref([
  {
    id: 'comp_001',
    type: 'dashboard',
    title: 'Marketing Dashboard 구독',
    description: '마케팅 대시보드 구독이 승인되었습니다.',
    requester: '정수진',
    reviewer: '관리자',
    status: 'approved',
    reviewDate: '2024-01-12',
    reviewComment: '검토 완료 후 승인합니다.'
  },
  {
    id: 'comp_002',
    type: 'data',
    title: 'Product DB 접근 권한',
    description: 'product_catalog 테이블 접근이 거절되었습니다.',
    requester: '홍길동',
    reviewer: '보안팀',
    status: 'rejected',
    reviewDate: '2024-01-10',
    reviewComment: '보안 정책상 접근이 제한됩니다.'
  }
])

// Computed properties
const filteredSubmittedRequests = computed(() => {
  if (submittedFilter.value === 'all') return submittedRequests.value
  return submittedRequests.value.filter(req => req.status === submittedFilter.value)
})

const filteredApprovalRequests = computed(() => {
  if (approvalFilter.value === 'all') return approvalRequests.value
  return approvalRequests.value.filter(req => req.type === approvalFilter.value)
})

const filteredCompletedRequests = computed(() => {
  let filtered = completedRequests.value
  
  if (completedFilter.value !== 'all') {
    filtered = filtered.filter(req => req.status === completedFilter.value)
  }
  
  if (dateFilter.value) {
    filtered = filtered.filter(req => req.reviewDate >= dateFilter.value)
  }
  
  return filtered
})

// Methods
const getRequestIcon = (type) => {
  const icons = {
    deploy: 'upload-cloud',
    dashboard: 'bar-chart',
    publish: 'globe',
    data: 'database'
  }
  return icons[type] || 'file'
}

const getRequestTypeLabel = (type) => {
  const labels = {
    deploy: '대시보드 배포',
    dashboard: '대시보드 구독',
    publish: '대시보드 발행',
    data: '데이터 접근'
  }
  return labels[type] || type
}

const getStatusLabel = (status) => {
  const labels = {
    pending: '대기중',
    approved: '승인됨',
    rejected: '거절됨',
    reviewing: '검토중'
  }
  return labels[status] || status
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('ko-KR')
}

const approveRequest = (requestId) => {
  console.log('승인:', requestId)
  // 실제 승인 로직 구현
  alert(`요청 ${requestId}이 승인되었습니다.`)
}

const rejectRequest = (requestId) => {
  console.log('거절:', requestId)
  // 실제 거절 로직 구현
  const reason = prompt('거절 사유를 입력해주세요:')
  if (reason) {
    alert(`요청 ${requestId}이 거절되었습니다.\n사유: ${reason}`)
  }
}
</script>

<style scoped>
.approval-view {
  padding: var(--space-6);
  max-width: 1200px;
  margin: 0 auto;
}

.approval-header {
  margin-bottom: var(--space-6);
}

.approval-header h2 {
  font-size: var(--fs-2xl);
  font-weight: var(--fw-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.approval-header p {
  color: var(--text-secondary);
  margin: 0;
}

/* Tab Navigation */
.tab-navigation {
  display: flex;
  gap: var(--space-1);
  margin-bottom: var(--space-6);
  border-bottom: 1px solid var(--border-primary);
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-4);
  border: none;
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  transition: var(--transition-fast);
  border-bottom: 2px solid transparent;
  font-weight: var(--fw-medium);
}

.tab-btn:hover {
  color: var(--text-primary);
  background: var(--surface-hover);
}

.tab-btn.active {
  color: var(--lina-orange);
  border-bottom-color: var(--lina-orange);
}

.tab-count {
  background: var(--lina-orange);
  color: white;
  font-size: var(--fs-xs);
  padding: 2px 6px;
  border-radius: var(--radius-full);
  min-width: 18px;
  text-align: center;
}

/* Tab Content */
.tab-content {
  min-height: 500px;
}

.tab-panel {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-4);
}

.panel-header h3 {
  font-size: var(--fs-xl);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0;
}

.filter-actions {
  display: flex;
  gap: var(--space-3);
}

.filter-select, .date-filter {
  padding: var(--space-2) var(--space-3);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  background: var(--surface);
  color: var(--text-primary);
  font-size: var(--fs-sm);
}

/* Request Cards */
.request-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.request-card {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  transition: var(--transition-fast);
}

.request-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-1px);
}

.request-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-3);
}

.request-type {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.type-label {
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
}

.priority {
  padding: 2px 8px;
  border-radius: var(--radius-sm);
  font-size: var(--fs-xs);
  font-weight: var(--fw-semibold);
  text-transform: uppercase;
}

.priority.high {
  background: var(--error-light);
  color: var(--error);
}

.priority.medium {
  background: var(--warning-light);
  color: var(--warning);
}

.priority.low {
  background: var(--success-light);
  color: var(--success);
}

.request-status {
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-md);
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
}

.request-status.pending {
  background: var(--warning-light);
  color: var(--warning);
}

.request-status.approved {
  background: var(--success-light);
  color: var(--success);
}

.request-status.rejected {
  background: var(--error-light);
  color: var(--error);
}

.request-actions {
  display: flex;
  gap: var(--space-2);
}

.approve-btn, .reject-btn {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-2) var(--space-3);
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  font-size: var(--fs-sm);
  font-weight: var(--fw-medium);
}

.approve-btn {
  background: var(--success);
  color: white;
}

.approve-btn:hover {
  background: var(--success-dark);
}

.reject-btn {
  background: var(--error);
  color: white;
}

.reject-btn:hover {
  background: var(--error-dark);
}

.request-content h4 {
  font-size: var(--fs-lg);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.request-content p {
  color: var(--text-secondary);
  margin: 0 0 var(--space-3) 0;
  line-height: 1.5;
}

.request-meta, .request-details {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-4);
}

.meta-item, .detail-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}

.request-comment {
  margin-top: var(--space-3);
  padding: var(--space-3);
  background: var(--surface-hover);
  border-radius: var(--radius-md);
  border-left: 3px solid var(--lina-orange);
}

.request-comment strong {
  color: var(--text-primary);
}

.request-comment p {
  margin: var(--space-1) 0 0 0;
  color: var(--text-secondary);
}

/* Progress Steps */
.progress-steps {
  display: flex;
  align-items: center;
  margin-top: var(--space-4);
  padding: var(--space-3);
  background: var(--surface-hover);
  border-radius: var(--radius-md);
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-1);
  flex: 1;
  position: relative;
}

.step-icon {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: var(--fw-bold);
  font-size: var(--fs-sm);
  background: var(--border-primary);
  color: var(--text-secondary);
  transition: var(--transition-fast);
}

.step.active .step-icon {
  background: var(--lina-orange);
  color: white;
}

.step.completed .step-icon {
  background: var(--success);
  color: white;
}

.step span {
  font-size: var(--fs-xs);
  color: var(--text-secondary);
  font-weight: var(--fw-medium);
}

.step.active span {
  color: var(--text-primary);
}

.step-line {
  height: 2px;
  background: var(--border-primary);
  flex: 1;
  margin: 0 var(--space-2);
  transition: var(--transition-fast);
}

.step-line.active {
  background: var(--lina-orange);
}

/* Responsive */
@media (max-width: 768px) {
  .approval-view {
    padding: var(--space-4);
  }

  .tab-navigation {
    flex-wrap: wrap;
  }

  .panel-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-3);
  }

  .filter-actions {
    width: 100%;
    justify-content: space-between;
  }

  .request-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-2);
  }

  .request-meta, .request-details {
    flex-direction: column;
    gap: var(--space-2);
  }

  .progress-steps {
    flex-direction: column;
    gap: var(--space-3);
  }

  .step-line {
    width: 2px;
    height: 20px;
    margin: 0;
  }
}
</style>