<template>
  <div class="insight-view">
    <!-- Header -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h1 class="page-title">Producer360</h1>
          <p class="page-subtitle">설계사 360도 통합 관리 플랫폼 · 기준일: {{ baselineDate }}</p>
        </div>
        <div class="header-actions">
          <div class="scope-tabs">
            <button
              v-for="t in tabs"
              :key="t.key"
              class="scope-tab"
              :class="{ active: t.key === activeScope }"
              @click="onScopeChange(t.key)"
            >
              {{ t.label }}
            </button>
          </div>
          <select v-model="selectedMonth" @change="onMonthChange" class="month-select">
            <option v-for="m in months" :key="m" :value="m">{{ m }}</option>
          </select>
          <button class="action-btn" @click="openFilter">
            <IconSystem name="filter" :size="16" />
            필터
          </button>
          <button class="action-btn primary" @click="exportPdf">
            <IconSystem name="download" :size="16" />
            주간 요약 PDF
          </button>
        </div>
      </div>
    </div>

    <!-- Content with keep-alive for performance -->
    <keep-alive>
      <DashboardContent :key="cacheKey" :data="data" :loading="loading" />
    </keep-alive>

    <div class="page-footer">
      ※ Producer360: 설계사 성과, 활동, 교육을 한눈에 관리하는 통합 인사이트 플랫폼
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue'
import IconSystem from './IconSystem.vue'
import DashboardContent from './InsightDashboard.vue'
import { throttle, getMonthlyData } from '../utils/insightUtils.js'

// Global state
const baselineDate = '2025-09-20'
const months = ['2025년 9월', '2025년 8월', '2025년 7월']
const selectedMonth = ref(months[0])
const tabs = [
  { key: 'all', label: '전체' },
  { key: 'hq', label: '본부별' }
]
const activeScope = ref('all')
const data = ref(null)
const loading = ref(false)

const cacheKey = computed(() => `${selectedMonth.value}-${activeScope.value}`)

// Load data
const loadData = async () => {
  loading.value = true
  data.value = await getMonthlyData(cacheKey.value)
  loading.value = false
}

// Event handlers
const onScopeChange = (key) => {
  activeScope.value = key
}

const onMonthChange = throttle(async () => {
  await loadData()
  // Prefetch next month
  const idx = months.indexOf(selectedMonth.value)
  const next = months[idx + 1]
  if (next) {
    getMonthlyData(`${next}-${activeScope.value}`)
  }
})

const openFilter = () => {
  alert('필터 패널은 추후 연결')
}

const exportPdf = () => {
  alert('PDF 생성 서비스와 연결하세요')
}

// Watch for scope changes
watch(activeScope, loadData)

onMounted(loadData)
</script>

<style scoped>
.insight-view {
  display: flex;
  flex-direction: column;
  padding: var(--space-4);
  gap: var(--space-4);
  background: var(--bg-primary);
}

.page-header {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--space-4);
}

.header-info {
  flex: 1;
  min-width: 200px;
}

.page-title {
  font-size: var(--fs-xl);
  font-weight: var(--fw-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
}

.page-subtitle {
  font-size: var(--fs-sm);
  color: var(--text-secondary);
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.scope-tabs {
  display: flex;
  gap: var(--space-1);
  background: var(--surface-hover);
  border-radius: var(--radius-md);
  padding: var(--space-1);
}

.scope-tab {
  padding: var(--space-2) var(--space-4);
  border: none;
  background: transparent;
  border-radius: var(--radius-sm);
  cursor: pointer;
  color: var(--text-secondary);
  font-weight: var(--fw-medium);
  transition: var(--transition-fast);
}

.scope-tab:hover {
  background: var(--surface);
}

.scope-tab.active {
  background: var(--lina-yellow);
  color: var(--gray-800);
  font-weight: var(--fw-semibold);
}

.month-select {
  padding: var(--space-2) var(--space-3);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  background: var(--surface);
  font-size: var(--fs-sm);
  cursor: pointer;
}

.action-btn {
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
}

.action-btn:hover {
  background: var(--surface-hover);
}

.action-btn.primary {
  background: var(--lina-yellow);
  border-color: var(--lina-yellow);
  color: var(--gray-800);
  font-weight: var(--fw-semibold);
}

.action-btn.primary:hover {
  background: var(--lina-yellow-dark);
}

.page-footer {
  font-size: var(--fs-xs);
  color: var(--text-tertiary);
  text-align: center;
  padding: var(--space-4);
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-actions {
    width: 100%;
    flex-direction: column;
  }
  
  .scope-tabs,
  .month-select,
  .action-btn {
    width: 100%;
  }
}
</style>
