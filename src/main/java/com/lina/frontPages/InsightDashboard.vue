<template>
  <div class="dashboard-content">
    <!-- Section 1: 성과관리 -->
    <FoldSection title="1) 성과관리 (Overview)" subtitle="핵심 KPI 요약" :defaultOpen="true">
      <div v-if="data" class="kpi-grid">
        <div v-for="(kpi, idx) in data.kpis" :key="idx" class="kpi-card">
          <div class="kpi-title">{{ kpi.title }}</div>
          <div class="kpi-value">
            {{ formatKpiValue(kpi) }}
            <span v-if="kpi.of" class="kpi-sub">
              ({{ kpi.of[0] }}/{{ kpi.of[1] }})
            </span>
          </div>
          <div v-if="typeof kpi.value === 'number' && !kpi.unit" class="progress-bar">
            <div class="progress-fill" :style="{ width: `${kpi.value * 100}%` }"></div>
          </div>
          <div v-if="kpi.delta !== undefined" class="kpi-delta" :class="kpi.delta >= 0 ? 'positive' : 'negative'">
            {{ kpi.delta >= 0 ? '+' : '' }}{{ (kpi.delta * 100).toFixed(1) }}%p
          </div>
        </div>
      </div>
      <div v-else class="skeleton-grid"></div>
    </FoldSection>

    <!-- Section 2: 실적관리 -->
    <FoldSection title="2) 실적관리 (Detail)" subtitle="추이/구성요소 분석" :defaultOpen="true">
      <div class="grid-2">
        <div class="chart-card">
          <h4>당월 실적 추이 (일별)</h4>
          <BarWire v-if="data" :series="data.daySeries" :goal="140" />
          <div v-else class="chart-skeleton"></div>
        </div>
        <div class="info-card">
          <h4>성과 구성요소</h4>
          <div v-if="data" class="info-list">
            <div class="info-row"><span>설계 건수</span><strong>771건</strong></div>
            <div class="info-row"><span>청약 건수</span><strong>455건</strong></div>
            <div class="info-row"><span>청약률</span><strong>59%</strong></div>
            <div class="info-row"><span>모바일 청약률</span><strong>76.5%</strong></div>
            <div class="info-row"><span>예상 달성률</span><strong>~ 84%</strong></div>
          </div>
          <button class="btn-primary">지점별 상세 보기</button>
        </div>
      </div>

      <div class="grid-3">
        <div class="chart-card">
          <h4>연도별 성과 (최근 5년)</h4>
          <YearLine v-if="data" :series="data.yearSeries" />
          <div v-else class="chart-skeleton"></div>
        </div>
        <div class="chart-card">
          <h4>월별 성과 (연간)</h4>
          <MonthBars v-if="data" :series="data.monthBars" />
          <div v-else class="chart-skeleton"></div>
        </div>
        <div class="chart-card">
          <h4>상품별 판매현황</h4>
          <ProductSeries v-if="data" :series="data.productSeries" />
          <div v-else class="chart-skeleton"></div>
        </div>
      </div>
    </FoldSection>

    <!-- Section 3: 지점 및 설계사 관리 -->
    <FoldSection title="3) 지점 및 설계사 관리 (Action)" subtitle="우선 조치 · 리스크/기회" :defaultOpen="true">
      <div class="action-grid">
        <ActionList title="위험 지점" tone="bad" :items="riskItems" />
        <ActionList title="주의 지점" tone="warn" :items="warnItems" />
        <ActionList title="기회 지점" tone="ok" :items="opportunityItems" />
      </div>

      <div class="grid-2">
        <div class="chart-card">
          <h4>지점별 판매 순위 (Top 10)</h4>
          <BranchRanking v-if="data" :series="data.ranking" />
          <div v-else class="chart-skeleton"></div>
        </div>
        <div class="table-card">
          <h4>당월 방문/교육 현황</h4>
          <div v-if="data">
            <h5>영업 방문 현황</h5>
            <table class="data-table">
              <thead>
                <tr>
                  <th>지점명</th>
                  <th>방문 수</th>
                  <th>방문율</th>
                  <th>담당자</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="v in data.visits" :key="v.branch">
                  <td>{{ v.branch }}</td>
                  <td>{{ v.visits }}</td>
                  <td>{{ Math.round(v.rate * 100) }}%</td>
                  <td>{{ v.owner }}</td>
                </tr>
              </tbody>
            </table>
            <div class="divider"></div>
            <h5>교육 현황</h5>
            <table class="data-table">
              <thead>
                <tr>
                  <th>지점명</th>
                  <th>교육 진행</th>
                  <th>이수율</th>
                  <th>최근 교육일</th>
                  <th>미이수 인원</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="e in data.edu" :key="e.branch">
                  <td>{{ e.branch }}</td>
                  <td>{{ e.status }}</td>
                  <td>{{ e.rate === null ? '—' : Math.round(e.rate * 100) + '%' }}</td>
                  <td>{{ e.last }}</td>
                  <td>{{ e.left }}명</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </FoldSection>
  </div>
</template>

<script setup>
import { BarWire, YearLine, MonthBars, ProductSeries, BranchRanking } from './InsightCharts.vue'
import FoldSection from './common/FoldSection.vue'
import ActionList from './common/ActionList.vue'

defineProps({
  data: Object,
  loading: Boolean
})

const formatKpiValue = (kpi) => {
  if (kpi.unit) return `${kpi.value}${kpi.unit}`
  if (typeof kpi.value === 'number') {
    return kpi.of ? kpi.value : `${Math.round(kpi.value * 100)}%`
  }
  return kpi.value
}

const riskItems = [
  { chip: '위험', title: '메타티지 > 보험스토어', note: '3개월 연속 하락' },
  { chip: '위험', title: '지급중코리아 > 대연', note: '핵심 인력 이탈' }
]

const warnItems = [
  { chip: '주의', title: '더블유에셋 > 안산센터', note: '계약 품질 이슈' }
]

const opportunityItems = [
  { chip: '기회', title: '글로벌금융판매 > 리더스일산', note: '실적 급상승' },
  { chip: '기회', title: '에셋현대금융 > 구미스튜디오', note: '고액 계약 체결' }
]
</script>

<style scoped>
.dashboard-content {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--space-4);
}

.kpi-card {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.kpi-title {
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
  color: var(--text-secondary);
}

.kpi-value {
  font-size: var(--fs-2xl);
  font-weight: var(--fw-bold);
  color: var(--text-primary);
}

.kpi-sub {
  font-size: var(--fs-sm);
  color: var(--text-tertiary);
}

.progress-bar {
  height: 8px;
  background: var(--surface-hover);
  border-radius: var(--radius-full);
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: var(--lina-yellow);
  transition: width 0.3s ease;
}

.kpi-delta {
  font-size: var(--fs-xs);
  font-weight: var(--fw-medium);
}

.kpi-delta.positive {
  color: var(--success);
}

.kpi-delta.negative {
  color: var(--error);
}

.grid-2 {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--space-4);
}

.grid-3 {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-4);
}

.chart-card,
.info-card,
.table-card {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
}

.chart-card h4,
.info-card h4,
.table-card h4 {
  margin: 0 0 var(--space-3) 0;
  font-size: var(--fs-base);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
}

.table-card h5 {
  margin: var(--space-3) 0 var(--space-2) 0;
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
  color: var(--text-secondary);
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  margin-bottom: var(--space-3);
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-2);
  background: var(--surface-hover);
  border-radius: var(--radius-sm);
}

.btn-primary {
  width: 100%;
  padding: var(--space-2);
  background: var(--lina-yellow);
  border: none;
  border-radius: var(--radius-md);
  font-weight: var(--fw-semibold);
  cursor: pointer;
  transition: var(--transition-fast);
}

.btn-primary:hover {
  background: var(--lina-yellow-dark);
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-4);
  margin-bottom: var(--space-4);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: var(--fs-sm);
}

.data-table th,
.data-table td {
  padding: var(--space-2);
  text-align: left;
  border-bottom: 1px solid var(--border-primary);
}

.data-table th {
  background: var(--surface-hover);
  font-weight: var(--fw-semibold);
  color: var(--text-secondary);
}

.divider {
  height: 1px;
  background: var(--border-primary);
  margin: var(--space-3) 0;
}

.chart-skeleton,
.skeleton-grid {
  height: 200px;
  background: var(--surface-hover);
  border-radius: var(--radius-md);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

@media (max-width: 1024px) {
  .grid-2,
  .grid-3,
  .action-grid {
    grid-template-columns: 1fr;
  }
  
  .kpi-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .kpi-grid {
    grid-template-columns: 1fr;
  }
}
</style>
