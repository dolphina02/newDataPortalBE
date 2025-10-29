<template>
  <div class="dashboard-view">
    <!-- Dashboard Slider -->
    <section class="dashboard-slider">
      <div class="slider-header">
        <div class="dashboard-info">
          <h2 class="current-dashboard-title">{{ dashboards[currentSlide]?.title || 'Dashboard' }}</h2>
          <div class="slide-counter">
            {{ currentSlide + 1 }} / {{ dashboards.length }}
          </div>
        </div>
        <div class="slider-actions">
          <button class="action-btn" @click="refreshData">
            <IconSystem name="refresh" :size="16" />
            새로고침
          </button>
        </div>
      </div>
      
      <div class="slider-container">
        <button 
          class="slider-btn prev" 
          @click="previousSlide"
          :disabled="currentSlide === 0"
        >
          <IconSystem name="chevron-left" :size="24" />
        </button>
        
        <div class="slider-content">
          <div 
            class="slides-wrapper" 
            :style="{ transform: `translateX(-${currentSlide * 100}%)` }"
          >
            <!-- Insurance KPI Dashboard -->
            <div class="slide">
              <div class="dashboard-page">
                
                <!-- KPI Cards -->
                <div class="kpi-grid">
                  <div class="kpi-card">
                    <div class="kpi-icon">📋</div>
                    <div class="kpi-content">
                      <div class="kpi-value">15,847</div>
                      <div class="kpi-label">총 보험계약</div>
                      <div class="kpi-change positive">+8.2%</div>
                    </div>
                  </div>
                  <div class="kpi-card">
                    <div class="kpi-icon">💼</div>
                    <div class="kpi-content">
                      <div class="kpi-value">₩124.5억</div>
                      <div class="kpi-label">보험료 수입</div>
                      <div class="kpi-change positive">+12.3%</div>
                    </div>
                  </div>
                  <div class="kpi-card">
                    <div class="kpi-icon">⚡</div>
                    <div class="kpi-content">
                      <div class="kpi-value">2.1일</div>
                      <div class="kpi-label">평균 심사기간</div>
                      <div class="kpi-change positive">-0.3일</div>
                    </div>
                  </div>
                  <div class="kpi-card">
                    <div class="kpi-icon">🎯</div>
                    <div class="kpi-content">
                      <div class="kpi-value">94.2%</div>
                      <div class="kpi-label">고객 만족도</div>
                      <div class="kpi-change positive">+1.8%</div>
                    </div>
                  </div>
                </div>

                <!-- Charts -->
                <div class="charts-grid">
                  <div class="chart-card large">
                    <div class="chart-header">
                      <h4>월별 보험료 수입 추이</h4>
                    </div>
                    <div class="chart-container">
                      <canvas ref="salesChart"></canvas>
                    </div>
                  </div>
                  
                  <div class="chart-card">
                    <div class="chart-header">
                      <h4>보험 상품별 비율</h4>
                    </div>
                    <div class="chart-container">
                      <canvas ref="productChart"></canvas>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Claim Summary Dashboard -->
            <div class="slide">
              <div class="dashboard-page">
                
                <!-- Claim Charts - Full Width Layout -->
                <div class="claims-grid">
                  <div class="chart-card">
                    <div class="chart-header">
                      <h4>월별 청구 건수 추이</h4>
                    </div>
                    <div class="chart-container">
                      <canvas ref="claimTrendChart"></canvas>
                    </div>
                  </div>
                  
                  <div class="chart-card">
                    <div class="chart-header">
                      <h4>청구 유형별 분포</h4>
                    </div>
                    <div class="chart-container">
                      <canvas ref="claimTypeChart"></canvas>
                    </div>
                  </div>
                  
                  <div class="chart-card">
                    <div class="chart-header">
                      <h4>청구 상태별 현황</h4>
                    </div>
                    <div class="chart-container">
                      <canvas ref="claimStatusChart"></canvas>
                    </div>
                  </div>
                  
                  <div class="chart-card">
                    <div class="chart-header">
                      <h4>평균 처리 시간</h4>
                    </div>
                    <div class="chart-container">
                      <canvas ref="processingTimeChart"></canvas>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <button 
          class="slider-btn next" 
          @click="nextSlide"
          :disabled="currentSlide === dashboards.length - 1"
        >
          <IconSystem name="chevron-right" :size="24" />
        </button>
      </div>
      
      <!-- Slide Indicators -->
      <div class="slide-indicators">
        <button 
          v-for="(dashboard, index) in dashboards" 
          :key="index"
          class="indicator" 
          :class="{ active: currentSlide === index }"
          @click="goToSlide(index)"
        >
          <span class="indicator-label">{{ dashboard.title }}</span>
        </button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import IconSystem from './IconSystem.vue'

// Reactive state
const currentSlide = ref(0)

// Chart refs
const salesChart = ref(null)
const productChart = ref(null)
const claimTrendChart = ref(null)
const claimTypeChart = ref(null)
const claimStatusChart = ref(null)
const processingTimeChart = ref(null)

// Chart instances
let salesChartInstance = null
let productChartInstance = null
let claimTrendChartInstance = null
let claimTypeChartInstance = null
let claimStatusChartInstance = null
let processingTimeChartInstance = null

// Dashboard data
const dashboards = [
  {
    title: 'Insurance KPI',
    description: '보험사 핵심 성과 지표'
  },
  {
    title: 'Claim Summary',
    description: '보험 청구 현황 분석'
  }
]

// Methods
const nextSlide = () => {
  if (currentSlide.value < dashboards.length - 1) {
    currentSlide.value++
  }
}

const previousSlide = () => {
  if (currentSlide.value > 0) {
    currentSlide.value--
  }
}

const goToSlide = (index) => {
  currentSlide.value = index
}

const refreshData = () => {
  console.log('데이터 새로고침')
  initializeCharts()
}

const initializeCharts = async () => {
  try {
    // Dynamic import Chart.js
    const { Chart, registerables } = await import('chart.js')
    Chart.register(...registerables)

    // Destroy existing charts
    if (salesChartInstance) {
      salesChartInstance.destroy()
      salesChartInstance = null
    }
    if (productChartInstance) {
      productChartInstance.destroy()
      productChartInstance = null
    }
    if (claimTrendChartInstance) {
      claimTrendChartInstance.destroy()
      claimTrendChartInstance = null
    }
    if (claimTypeChartInstance) {
      claimTypeChartInstance.destroy()
      claimTypeChartInstance = null
    }
    if (claimStatusChartInstance) {
      claimStatusChartInstance.destroy()
      claimStatusChartInstance = null
    }
    if (processingTimeChartInstance) {
      processingTimeChartInstance.destroy()
      processingTimeChartInstance = null
    }

    // Wait for DOM update
    await nextTick()

    if (currentSlide.value === 0) {
      // Sales Analytics Charts
      if (salesChart.value) {
        salesChartInstance = new Chart(salesChart.value, {
          type: 'line',
          data: {
            labels: ['1월', '2월', '3월', '4월', '5월', '6월'],
            datasets: [{
              label: '보험료 수입 (억원)',
              data: [98, 115, 124, 142, 138, 156],
              borderColor: '#FF6B35',
              backgroundColor: 'rgba(255, 107, 53, 0.1)',
              tension: 0.4,
              fill: true
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                display: false
              }
            },
            scales: {
              y: {
                beginAtZero: true,
                grid: {
                  color: 'rgba(0, 0, 0, 0.1)'
                }
              },
              x: {
                grid: {
                  display: false
                }
              }
            }
          }
        })
      }

      if (productChart.value) {
        productChartInstance = new Chart(productChart.value, {
          type: 'doughnut',
          data: {
            labels: ['생명보험', '건강보험', '자동차보험', '화재보험', '여행보험'],
            datasets: [{
              data: [42, 28, 18, 8, 4],
              backgroundColor: [
                '#FF6B35',
                '#F7931E',
                '#FFD23F',
                '#06D6A0',
                '#118AB2'
              ]
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                position: 'bottom'
              }
            }
          }
        })
      }
    } else if (currentSlide.value === 1) {
      // Claim Summary Charts
      if (claimTrendChart.value) {
        claimTrendChartInstance = new Chart(claimTrendChart.value, {
          type: 'line',
          data: {
            labels: ['1월', '2월', '3월', '4월', '5월', '6월'],
            datasets: [{
              label: '청구 건수',
              data: [1250, 1380, 1420, 1650, 1580, 1720],
              borderColor: '#FF6B35',
              backgroundColor: 'rgba(255, 107, 53, 0.1)',
              tension: 0.4,
              fill: true
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                display: false
              }
            },
            scales: {
              y: {
                beginAtZero: true,
                grid: {
                  color: 'rgba(0, 0, 0, 0.1)'
                }
              },
              x: {
                grid: {
                  display: false
                }
              }
            }
          }
        })
      }

      if (claimTypeChart.value) {
        claimTypeChartInstance = new Chart(claimTypeChart.value, {
          type: 'doughnut',
          data: {
            labels: ['의료비', '사고보상', '생명보험', '재산피해', '기타'],
            datasets: [{
              data: [45, 25, 15, 10, 5],
              backgroundColor: [
                '#FF6B35',
                '#F7931E',
                '#FFD23F',
                '#06D6A0',
                '#118AB2'
              ]
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                position: 'bottom'
              }
            }
          }
        })
      }

      if (claimStatusChart.value) {
        claimStatusChartInstance = new Chart(claimStatusChart.value, {
          type: 'bar',
          data: {
            labels: ['접수', '심사중', '승인', '거절', '보류'],
            datasets: [{
              label: '건수',
              data: [320, 180, 850, 45, 25],
              backgroundColor: ['#FFD23F', '#F7931E', '#06D6A0', '#FF6B35', '#118AB2'],
              borderRadius: 4
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                display: false
              }
            },
            scales: {
              y: {
                beginAtZero: true,
                grid: {
                  color: 'rgba(0, 0, 0, 0.1)'
                }
              },
              x: {
                grid: {
                  display: false
                }
              }
            }
          }
        })
      }

      if (processingTimeChart.value) {
        processingTimeChartInstance = new Chart(processingTimeChart.value, {
          type: 'bar',
          data: {
            labels: ['의료비', '사고보상', '생명보험', '재산피해', '기타'],
            datasets: [{
              label: '처리시간 (일)',
              data: [2.1, 4.5, 3.2, 5.8, 2.9],
              backgroundColor: '#FF6B35',
              borderRadius: 4
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                display: false
              }
            },
            scales: {
              y: {
                beginAtZero: true,
                grid: {
                  color: 'rgba(0, 0, 0, 0.1)'
                }
              },
              x: {
                grid: {
                  display: false
                }
              }
            }
          }
        })
      }
    }
  } catch (error) {
    console.error('Chart initialization error:', error)
  }
}

// Watch for slide changes
watch(currentSlide, () => {
  setTimeout(() => {
    initializeCharts()
  }, 100)
})

onMounted(() => {
  setTimeout(() => {
    initializeCharts()
  }, 100)
})
</script><style
 scoped>
.dashboard-view {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
  height: 100vh;
  padding: var(--space-4);
}

/* Header */

.slide-counter {
  padding: var(--space-1) var(--space-3);
  background: var(--surface-hover);
  border-radius: var(--radius-md);
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
  color: var(--text-secondary);
  border: 1px solid var(--border-primary);
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  background: var(--lina-orange);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  font-size: var(--fs-sm);
  font-weight: var(--fw-medium);
}

.refresh-btn:hover {
  background: var(--lina-yellow);
}

/* Dashboard Slider */
.dashboard-slider {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
  flex: 1;
}

.slider-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-2) 0;
}

.dashboard-info {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.current-dashboard-title {
  font-size: var(--fs-xl);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0;
}

.slider-actions {
  display: flex;
  gap: var(--space-3);
}

.slider-actions .action-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  background: var(--surface);
  color: var(--text-secondary);
  cursor: pointer;
  transition: var(--transition-fast);
  font-weight: var(--fw-medium);
  font-size: var(--fs-sm);
}

.slider-actions .action-btn:hover {
  background: var(--surface-hover);
  border-color: var(--lina-yellow);
  color: var(--text-primary);
}

.slider-container {
  position: relative;
  flex: 1;
  overflow: hidden;
  border-radius: var(--radius-lg);
  min-height: 0;
}

.slider-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-secondary);
  z-index: 20;
  box-shadow: var(--shadow-md);
}

.slider-btn.prev {
  left: var(--space-3);
}

.slider-btn.next {
  right: var(--space-3);
}

.slider-btn:hover:not(:disabled) {
  background: var(--lina-orange);
  color: white;
  border-color: var(--lina-orange);
  transform: translateY(-50%) scale(1.1);
}

.slider-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.slider-content {
  width: 100%;
  height: 100%;
  overflow: hidden;
  border-radius: var(--radius-lg);
}

.slides-wrapper {
  display: flex;
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide {
  width: 100%;
  flex-shrink: 0;
}

/* Dashboard Page */
.dashboard-page {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  height: 100%;
  overflow-y: auto;
}

.page-header {
  margin-bottom: var(--space-6);
  text-align: center;
}

.page-header .page-title {
  font-size: var(--fs-xl);
  font-weight: var(--fw-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.page-header .page-subtitle {
  font-size: var(--fs-base);
  color: var(--text-secondary);
  margin: 0;
}

/* KPI Cards */
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--space-4);
  margin-bottom: var(--space-6);
}

.kpi-card {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  background: var(--surface-hover);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  transition: var(--transition-fast);
}

.kpi-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.kpi-icon {
  font-size: 2rem;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--lina-orange);
  border-radius: var(--radius-lg);
  flex-shrink: 0;
}

.kpi-content {
  flex: 1;
}

.kpi-value {
  font-size: var(--fs-xl);
  font-weight: var(--fw-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
}

.kpi-label {
  font-size: var(--fs-sm);
  color: var(--text-secondary);
  margin: 0 0 var(--space-1) 0;
}

.kpi-change {
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
}

.kpi-change.positive {
  color: var(--success);
}

.kpi-change.negative {
  color: var(--error);
}

/* Charts */
.charts-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--space-4);
  flex: 1;
}

.claims-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-4);
  flex: 1;
}

.chart-card {
  background: var(--surface-hover);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  box-shadow: var(--shadow-sm);
  display: flex;
  flex-direction: column;
}

.chart-card.large {
  grid-column: span 1;
}

.chart-header {
  margin-bottom: var(--space-4);
  flex-shrink: 0;
}

.chart-header h4 {
  font-size: var(--fs-lg);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0;
}

.chart-container {
  position: relative;
  flex: 1;
  min-height: 350px;
}

/* Slide Indicators */
.slide-indicators {
  display: flex;
  justify-content: center;
  gap: var(--space-2);
  flex-wrap: wrap;
  flex-shrink: 0;
  padding: var(--space-2) 0;
}

.indicator {
  padding: var(--space-1) var(--space-3);
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  font-size: var(--fs-xs);
  color: var(--text-secondary);
}

.indicator:hover {
  background: var(--surface-hover);
  color: var(--text-primary);
}

.indicator.active {
  background: var(--lina-orange);
  color: white;
  border-color: var(--lina-orange);
}

.indicator-label {
  font-weight: var(--fw-medium);
}

/* Responsive */
@media (max-width: 1200px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-card.large {
    grid-column: span 1;
  }
}

@media (max-width: 768px) {
  .dashboard-view {
    padding: var(--space-3);
  }

  .slider-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-3);
  }
  
  .dashboard-info {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-2);
  }
    align-items: flex-start;
    gap: var(--space-3);
  }

  .header-actions {
    width: 100%;
    justify-content: space-between;
  }

  .dashboard-slider {
    height: calc(100vh - 180px);
  }

  .slider-btn {
    width: 32px;
    height: 32px;
  }

  .slider-btn.prev {
    left: var(--space-2);
  }

  .slider-btn.next {
    right: var(--space-2);
  }

  .dashboard-page {
    padding: var(--space-4);
  }

  .kpi-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--space-3);
  }

  .kpi-card {
    padding: var(--space-3);
  }

  .chart-container {
    min-height: 280px;
  }

  .slide-indicators {
    gap: var(--space-1);
  }

  .indicator {
    padding: var(--space-1) var(--space-2);
    font-size: var(--fs-xs);
  }

@media (max-width: 480px) {
  .dashboard-view {
    padding: var(--space-2);
  }

  .dashboard-page {
    padding: var(--space-3);
  }

  .kpi-grid {
    grid-template-columns: 1fr;
  }

  .kpi-card {
    flex-direction: column;
    text-align: center;
    gap: var(--space-2);
  }

  .chart-container {
    min-height: 250px;
  }

  .slide-indicators {
    flex-direction: column;
    align-items: center;
    gap: var(--space-1);
  }

  .indicator {
    width: 100%;
    max-width: 200px;
    text-align: center;
  }
}
</style>