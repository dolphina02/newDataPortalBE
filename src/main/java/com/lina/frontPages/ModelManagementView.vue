<template>
  <div class="model-management-view">
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h1 class="page-title">모델 관리</h1>
          <p class="page-subtitle">AI/ML 모델의 성능과 버전을 관리하세요</p>
        </div>
        <div class="header-actions">
          <button class="action-btn">
            <IconSystem name="upload" :size="16" />
            모델 업로드
          </button>
          <button class="action-btn primary">
            <IconSystem name="plus" :size="16" />
            새 모델
          </button>
        </div>
      </div>
    </div>

    <div class="model-workspace">
      <!-- Models List -->
      <div class="models-panel">
        <div class="models-header">
          <h3 class="models-title">모델 목록</h3>
        </div>
        
        <div class="models-list">
          <div 
            v-for="model in models" 
            :key="model.id"
            class="model-item"
            :class="{ active: selectedModel?.id === model.id }"
            @click="selectModel(model)"
          >
            <div class="model-header">
              <div class="model-name">{{ model.name }}</div>
              <div class="model-status" :class="model.status">{{ getStatusLabel(model.status) }}</div>
            </div>
            <div class="model-type">{{ model.type }}</div>
            <div class="model-metrics">
              <span>정확도: {{ model.accuracy }}%</span>
              <span>F1: {{ model.f1Score }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Model Details -->
      <div class="model-details-panel">
        <div v-if="!selectedModel" class="empty-state">
          <IconSystem name="box" :size="48" />
          <p>모델을 선택하여 상세 정보를 확인하세요</p>
        </div>
        
        <div v-else class="model-details">
          <!-- Model Header -->
          <div class="details-header">
            <h2 class="model-title">{{ selectedModel.name }}</h2>
            <div class="model-badges">
              <span class="badge" :class="selectedModel.status">{{ getStatusLabel(selectedModel.status) }}</span>
              <span class="badge version">v{{ selectedModel.version }}</span>
            </div>
          </div>

          <!-- Model Tabs -->
          <div class="details-tabs">
            <button 
              v-for="tab in detailTabs" 
              :key="tab.id"
              class="tab-btn"
              :class="{ active: activeDetailTab === tab.id }"
              @click="setActiveTab(tab.id)"
            >
              <IconSystem :name="tab.icon" :size="16" />
              {{ tab.label }}
            </button>
          </div>

          <!-- Tab Content -->
          <div class="tab-content">
            <!-- Overview Tab -->
            <div v-if="activeDetailTab === 'overview'" class="overview-content">
              <div class="info-grid">
                <div class="info-card">
                  <h4>기본 정보</h4>
                  <div class="info-list">
                    <div class="info-item">
                      <span class="label">모델 타입:</span>
                      <span class="value">{{ selectedModel.type }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">생성일:</span>
                      <span class="value">{{ selectedModel.createdAt }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">훈련 데이터셋:</span>
                      <span class="value">{{ selectedModel.trainingDataset }}</span>
                    </div>
                  </div>
                </div>
                
                <div class="info-card">
                  <h4>성능 지표</h4>
                  <div class="metrics-grid">
                    <div class="metric-card">
                      <div class="metric-value">{{ selectedModel.accuracy }}%</div>
                      <div class="metric-label">Accuracy</div>
                    </div>
                    <div class="metric-card">
                      <div class="metric-value">{{ selectedModel.f1Score }}</div>
                      <div class="metric-label">F1 Score</div>
                    </div>
                    <div class="metric-card">
                      <div class="metric-value">{{ selectedModel.recall }}%</div>
                      <div class="metric-label">Recall</div>
                    </div>
                    <div class="metric-card">
                      <div class="metric-value">{{ selectedModel.precision }}%</div>
                      <div class="metric-label">Precision</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Performance Tab -->
            <div v-if="activeDetailTab === 'performance'" class="performance-content">
              <div class="charts-grid">
                <div class="chart-card">
                  <h4>Confusion Matrix</h4>
                  <div class="confusion-matrix">
                    <div class="matrix-grid">
                      <div class="matrix-cell header"></div>
                      <div class="matrix-cell header">Pred 0</div>
                      <div class="matrix-cell header">Pred 1</div>
                      <div class="matrix-cell header">Actual 0</div>
                      <div class="matrix-cell tn">{{ selectedModel.confusionMatrix.tn }}</div>
                      <div class="matrix-cell fp">{{ selectedModel.confusionMatrix.fp }}</div>
                      <div class="matrix-cell header">Actual 1</div>
                      <div class="matrix-cell fn">{{ selectedModel.confusionMatrix.fn }}</div>
                      <div class="matrix-cell tp">{{ selectedModel.confusionMatrix.tp }}</div>
                    </div>
                  </div>
                </div>
                
                <div class="chart-card">
                  <h4>ROC Curve</h4>
                  <div class="chart-container">
                    <canvas ref="rocChart"></canvas>
                  </div>
                </div>
                
                <div class="chart-card">
                  <h4>Feature Importance</h4>
                  <div class="feature-importance">
                    <div 
                      v-for="feature in selectedModel.featureImportance" 
                      :key="feature.name"
                      class="feature-item"
                    >
                      <div class="feature-name">{{ feature.name }}</div>
                      <div class="feature-bar">
                        <div 
                          class="feature-fill" 
                          :style="{ width: feature.importance + '%' }"
                        ></div>
                      </div>
                      <div class="feature-value">{{ feature.importance }}%</div>
                    </div>
                  </div>
                </div>
                
                <div class="chart-card">
                  <h4>Score Distribution</h4>
                  <div class="chart-container">
                    <canvas ref="scoreChart"></canvas>
                  </div>
                </div>
              </div>
            </div>

            <!-- Versions Tab -->
            <div v-if="activeDetailTab === 'versions'" class="versions-content">
              <div class="versions-list">
                <div 
                  v-for="version in selectedModel.versions" 
                  :key="version.version"
                  class="version-item"
                  :class="{ current: version.version === selectedModel.version }"
                >
                  <div class="version-header">
                    <div class="version-info">
                      <div class="version-number">v{{ version.version }}</div>
                      <div class="version-date">{{ version.date }}</div>
                    </div>
                    <div class="version-metrics">
                      <span>Acc: {{ version.accuracy }}%</span>
                      <span>F1: {{ version.f1Score }}</span>
                    </div>
                  </div>
                  <div class="version-description">{{ version.description }}</div>
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
import { ref, nextTick, onMounted } from 'vue'
import IconSystem from './IconSystem.vue'

const selectedModel = ref(null)
const activeDetailTab = ref('overview')

// Chart refs
const rocChart = ref(null)
const scoreChart = ref(null)

const models = ref([
  {
    id: 1,
    name: 'Fraud Detection Model',
    type: 'Classification',
    status: 'active',
    version: '2.1',
    accuracy: 94.2,
    f1Score: 0.89,
    recall: 91.5,
    precision: 96.8,
    createdAt: '2024-01-15',
    trainingDataset: 'fraud_data_v3.csv (2.3M records)',
    confusionMatrix: { tp: 1250, fp: 45, fn: 89, tn: 2156 },
    featureImportance: [
      { name: 'transaction_amount', importance: 85 },
      { name: 'user_behavior_score', importance: 72 },
      { name: 'time_of_day', importance: 58 },
      { name: 'location_risk', importance: 45 },
      { name: 'payment_method', importance: 32 }
    ],
    versions: [
      { version: '2.1', date: '2024-03-10', accuracy: 94.2, f1Score: 0.89, description: '성능 최적화 및 새로운 피처 추가' },
      { version: '2.0', date: '2024-02-15', accuracy: 92.8, f1Score: 0.85, description: '모델 아키텍처 개선' }
    ]
  },
  {
    id: 2,
    name: 'Risk Assessment Model',
    type: 'Regression',
    status: 'training',
    version: '1.3',
    accuracy: 87.6,
    f1Score: 0.83,
    recall: 85.2,
    precision: 89.4,
    createdAt: '2024-02-01',
    trainingDataset: 'risk_assessment_v2.csv (1.8M records)',
    confusionMatrix: { tp: 980, fp: 78, fn: 125, tn: 1567 },
    featureImportance: [
      { name: 'credit_score', importance: 92 },
      { name: 'income_level', importance: 78 },
      { name: 'employment_history', importance: 65 },
      { name: 'debt_ratio', importance: 54 },
      { name: 'age', importance: 41 }
    ],
    versions: [
      { version: '1.3', date: '2024-03-08', accuracy: 87.6, f1Score: 0.83, description: '데이터 전처리 개선' },
      { version: '1.2', date: '2024-02-20', accuracy: 85.9, f1Score: 0.80, description: '하이퍼파라미터 튜닝' }
    ]
  }
])

const detailTabs = [
  { id: 'overview', label: '개요', icon: 'info' },
  { id: 'performance', label: '성능 분석', icon: 'trending-up' },
  { id: 'versions', label: '버전 관리', icon: 'clock' }
]

const selectModel = (model) => {
  selectedModel.value = model
  activeDetailTab.value = 'overview'
}

const setActiveTab = (tabId) => {
  activeDetailTab.value = tabId
  if (tabId === 'performance') {
    nextTick(() => {
      initializeCharts()
    })
  }
}

const getStatusLabel = (status) => {
  const labels = {
    'active': '활성',
    'training': '훈련중',
    'inactive': '비활성'
  }
  return labels[status] || status
}

const initializeCharts = async () => {
  try {
    const { Chart, registerables } = await import('chart.js')
    Chart.register(...registerables)

    // ROC Curve
    if (rocChart.value) {
      new Chart(rocChart.value, {
        type: 'line',
        data: {
          labels: ['0.0', '0.2', '0.4', '0.6', '0.8', '1.0'],
          datasets: [{
            label: 'ROC Curve',
            data: [0, 0.15, 0.35, 0.65, 0.85, 1.0],
            borderColor: '#FF6B35',
            backgroundColor: 'rgba(255, 107, 53, 0.1)',
            tension: 0.4,
            fill: true
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { display: false } },
          scales: {
            x: { title: { display: true, text: 'False Positive Rate' } },
            y: { title: { display: true, text: 'True Positive Rate' } }
          }
        }
      })
    }

    // Score Distribution
    if (scoreChart.value) {
      new Chart(scoreChart.value, {
        type: 'bar',
        data: {
          labels: ['0-0.2', '0.2-0.4', '0.4-0.6', '0.6-0.8', '0.8-1.0'],
          datasets: [{
            label: 'Positive',
            data: [25, 45, 78, 156, 234],
            backgroundColor: '#FF6B35'
          }, {
            label: 'Negative',
            data: [234, 156, 78, 45, 25],
            backgroundColor: '#06D6A0'
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { position: 'top' } }
        }
      })
    }
  } catch (error) {
    console.error('Chart initialization error:', error)
  }
}

onMounted(() => {
  if (models.value.length > 0) {
    selectedModel.value = models.value[0]
  }
})
</script>

<style scoped>
.model-management-view {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding: var(--space-4);
  gap: var(--space-4);
}

.page-header {
  flex-shrink: 0;
}

.model-workspace {
  display: grid;
  grid-template-columns: 350px 1fr;
  gap: var(--space-4);
  flex: 1;
  min-height: 0;
}


.models-panel {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.models-header {
  padding: var(--space-4);
  border-bottom: 1px solid var(--border-primary);
  background: var(--surface-hover);
}

.models-title {
  font-size: var(--fs-lg);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0;
}

.models-list {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-2);
}

.model-item {
  padding: var(--space-3);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  margin-bottom: var(--space-2);
  border: 1px solid transparent;
}

.model-item:hover {
  background: var(--surface-hover);
}

.model-item.active {
  background: color-mix(in srgb, var(--lina-orange) 10%, transparent);
  border-color: var(--lina-orange);
}

.model-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-2);
}

.model-name {
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  font-size: var(--fs-sm);
}

.model-status {
  padding: 2px var(--space-2);
  border-radius: var(--radius-sm);
  font-size: var(--fs-xs);
  font-weight: var(--fw-medium);
}

.model-status.active {
  background: var(--success);
  color: white;
}

.model-status.training {
  background: var(--lina-yellow);
  color: var(--text-primary);
}

.model-type {
  font-size: var(--fs-sm);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
}

.model-metrics {
  display: flex;
  gap: var(--space-2);
  font-size: var(--fs-xs);
  color: var(--text-tertiary);
}

.model-details-panel {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--text-secondary);
  gap: var(--space-3);
}

.model-details {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.details-header {
  padding: var(--space-4);
  border-bottom: 1px solid var(--border-primary);
  background: var(--surface-hover);
}

.model-title {
  font-size: var(--fs-xl);
  font-weight: var(--fw-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.model-badges {
  display: flex;
  gap: var(--space-2);
}

.badge {
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  font-size: var(--fs-xs);
  font-weight: var(--fw-semibold);
}

.badge.active {
  background: var(--success);
  color: white;
}

.badge.training {
  background: var(--lina-yellow);
  color: var(--text-primary);
}

.badge.version {
  background: var(--surface-active);
  color: var(--text-secondary);
}

.details-tabs {
  display: flex;
  padding: 0 var(--space-4);
  background: var(--surface-hover);
  border-bottom: 1px solid var(--border-primary);
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-4);
  border: none;
  background: none;
  cursor: pointer;
  transition: var(--transition-fast);
  font-size: var(--fs-sm);
  font-weight: var(--fw-medium);
  color: var(--text-secondary);
  border-bottom: 2px solid transparent;
}

.tab-btn:hover {
  color: var(--text-primary);
}

.tab-btn.active {
  color: var(--lina-orange);
  border-bottom-color: var(--lina-orange);
}

.tab-content {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-4);
}

/* Overview Content */
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-4);
}

.info-card {
  background: var(--surface-hover);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
}

.info-card h4 {
  font-size: var(--fs-lg);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-3) 0;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-item .label {
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}

.info-item .value {
  font-size: var(--fs-sm);
  font-weight: var(--fw-medium);
  color: var(--text-primary);
}

.metrics-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-3);
}

.metric-card {
  text-align: center;
  padding: var(--space-3);
  background: var(--surface);
  border-radius: var(--radius-md);
}

.metric-card .metric-value {
  font-size: var(--fs-xl);
  font-weight: var(--fw-bold);
  color: var(--lina-orange);
  margin-bottom: var(--space-1);
}

.metric-card .metric-label {
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}

/* Performance Content */
.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-4);
}

.chart-card {
  background: var(--surface-hover);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
}

.chart-card h4 {
  font-size: var(--fs-base);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-3) 0;
}

.chart-container {
  height: 250px;
  position: relative;
}

.confusion-matrix {
  display: flex;
  justify-content: center;
}

.matrix-grid {
  display: grid;
  grid-template-columns: repeat(3, 80px);
  gap: 2px;
}

.matrix-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  border-radius: var(--radius-sm);
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
}

.matrix-cell.header {
  background: var(--surface-active);
  color: var(--text-secondary);
}

.matrix-cell.tp, .matrix-cell.tn {
  background: var(--success);
  color: white;
}

.matrix-cell.fp, .matrix-cell.fn {
  background: var(--error);
  color: white;
}

.feature-importance {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.feature-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-2);
}

.feature-name {
  width: 160px;
  font-size: var(--fs-sm);
  color: var(--text-primary);
  font-family: 'JetBrains Mono', monospace;
  flex-shrink: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.feature-bar {
  flex: 1;
  height: 24px;
  background: var(--surface-active);
  border-radius: var(--radius-sm);
  position: relative;
  overflow: hidden;
  min-width: 100px;
}

.feature-fill {
  height: 100%;
  background: var(--lina-yellow);
  border-radius: var(--radius-sm);
  transition: width 0.5s ease;
}

.feature-value {
  width: 50px;
  text-align: right;
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  flex-shrink: 0;
}

/* Versions Content */
.versions-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.version-item {
  background: var(--surface-hover);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  transition: var(--transition-fast);
}

.version-item.current {
  border-color: var(--lina-orange);
  background: color-mix(in srgb, var(--lina-orange) 5%, transparent);
}

.version-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-2);
}

.version-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.version-number {
  font-size: var(--fs-lg);
  font-weight: var(--fw-bold);
  color: var(--text-primary);
}

.version-date {
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}

.version-metrics {
  display: flex;
  gap: var(--space-3);
  font-size: var(--fs-sm);
  font-weight: var(--fw-medium);
  color: var(--text-secondary);
}

.version-description {
  font-size: var(--fs-sm);
  color: var(--text-secondary);
  line-height: var(--lh-relaxed);
}

/* Responsive */
@media (max-width: 768px) {
  .model-workspace {
    grid-template-columns: 1fr;
  }
  
  .info-grid, .charts-grid {
    grid-template-columns: 1fr;
  }
  
  .metrics-grid {
    grid-template-columns: repeat(4, 1fr);
  }
  
  .feature-item {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-2);
  }
  
  .feature-name {
    width: 100%;
    text-align: left;
    margin-bottom: var(--space-1);
  }
  
  .feature-bar {
    height: 20px;
  }
  
  .feature-value {
    width: 100%;
    text-align: center;
    margin-top: var(--space-1);
  }
}
</style>