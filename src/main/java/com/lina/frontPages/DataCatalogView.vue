<template>
  <div class="data-catalog-view">
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h1 class="page-title">Data Catalog</h1>
          <p class="page-subtitle">데이터 메타정보, 계보, 품질 정보를 관리하세요</p>
        </div>
        <div class="header-actions">
          <button class="action-btn">
            <IconSystem name="upload" :size="16" />
            메타데이터 가져오기
          </button>
          <button class="action-btn primary">
            <IconSystem name="plus" :size="16" />
            새 테이블 등록
          </button>
        </div>
      </div>
    </div>

    <div class="catalog-workspace">
      <!-- Tables List -->
      <div class="tables-panel">
        <div class="panel-header">
          <h3 class="panel-title">테이블 목록</h3>
          <div class="panel-controls">
            <select v-model="selectedDatabase" class="db-selector">
              <option value="">전체 데이터베이스</option>
              <option v-for="db in databases" :key="db.id" :value="db.id">
                {{ db.name }}
              </option>
            </select>
            <div class="search-box">
              <IconSystem name="search" :size="16" />
              <input 
                v-model="searchQuery"
                type="text" 
                placeholder="테이블 검색..."
                class="search-input"
              />
            </div>
          </div>
        </div>
        
        <div class="tables-list">
          <div 
            v-for="table in filteredTables" 
            :key="table.id"
            class="table-item"
            :class="{ active: selectedTable?.id === table.id }"
            @click="selectTable(table)"
          >
            <div class="table-header">
              <div class="table-icon-container">
                <div class="table-icon" :class="`icon-${table.engine?.toLowerCase()}`">
                  <IconSystem :name="getTableIcon(table)" :size="20" />
                </div>
                <div class="engine-label" :class="`engine-${table.engine?.toLowerCase().replace(' ', '-')}`">
                  {{ table.engine }}
                </div>
              </div>
              <div class="table-info">
                <div class="table-name">{{ table.name }}</div>
                <div class="table-schema">{{ table.schema }}</div>
              </div>
            </div>
            <div class="table-meta">
              <span class="table-type">{{ table.type }}</span>
              <span class="table-rows">{{ table.rowCount?.toLocaleString() }} rows</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Table Details -->
      <div class="details-panel" v-if="selectedTable">
        <div class="panel-header">
          <h3 class="panel-title">{{ selectedTable.name }}</h3>
          <div class="table-actions">
            <button class="action-btn">
              <IconSystem name="edit" :size="16" />
              편집
            </button>
            <button class="action-btn">
              <IconSystem name="refresh-cw" :size="16" />
              프로파일 새로고침
            </button>
          </div>
        </div>
        
        <div class="details-content">
          <!-- Tab Navigation -->
          <div class="tab-nav">
            <button 
              v-for="tab in tabs" 
              :key="tab.id"
              class="tab-btn"
              :class="{ active: activeTab === tab.id }"
              @click="activeTab = tab.id"
            >
              <IconSystem :name="tab.icon" :size="16" />
              {{ tab.label }}
            </button>
          </div>
          
          <!-- Tab Content -->
          <div class="tab-content">
            <!-- Overview Tab -->
            <div v-if="activeTab === 'overview'" class="tab-pane">
              <div class="overview-grid">
                <div class="info-card">
                  <h4>기본 정보</h4>
                  <div class="info-row">
                    <span class="label">테이블명:</span>
                    <span class="value">{{ selectedTable.name }}</span>
                  </div>
                  <div class="info-row">
                    <span class="label">스키마:</span>
                    <span class="value">{{ selectedTable.schema }}</span>
                  </div>
                  <div class="info-row">
                    <span class="label">타입:</span>
                    <span class="value">{{ selectedTable.type }}</span>
                  </div>
                  <div class="info-row">
                    <span class="label">설명:</span>
                    <span class="value">{{ selectedTable.description }}</span>
                  </div>
                </div>
                
                <div class="info-card">
                  <h4>데이터 소유자</h4>
                  <div class="info-row">
                    <span class="label">Data Owner:</span>
                    <span class="value">{{ selectedTable.dataOwner }}</span>
                  </div>
                  <div class="info-row">
                    <span class="label">Data Steward:</span>
                    <span class="value">{{ selectedTable.dataSteward }}</span>
                  </div>
                  <div class="info-row">
                    <span class="label">담당 부서:</span>
                    <span class="value">{{ selectedTable.department }}</span>
                  </div>
                </div> 
               
                <div class="info-card">
                  <h4>데이터 통계</h4>
                  <div class="info-row">
                    <span class="label">총 행 수:</span>
                    <span class="value">{{ selectedTable.rowCount?.toLocaleString() }}</span>
                  </div>
                  <div class="info-row">
                    <span class="label">컬럼 수:</span>
                    <span class="value">{{ selectedTable.columnCount }}</span>
                  </div>
                  <div class="info-row">
                    <span class="label">데이터 크기:</span>
                    <span class="value">{{ selectedTable.dataSize }}</span>
                  </div>
                  <div class="info-row">
                    <span class="label">마지막 업데이트:</span>
                    <span class="value">{{ selectedTable.lastUpdated }}</span>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Schema Tab -->
            <div v-if="activeTab === 'schema'" class="tab-pane">
              <div class="schema-table">
                <div class="table-header">
                  <div class="col-name">컬럼명</div>
                  <div class="col-type">데이터 타입</div>
                  <div class="col-nullable">Nullable</div>
                  <div class="col-key">Key</div>
                  <div class="col-description">설명</div>
                </div>
                <div class="table-body">
                  <div 
                    v-for="column in selectedTable.columns" 
                    :key="column.name"
                    class="table-row"
                  >
                    <div class="col-name">
                      <IconSystem :name="getColumnIcon(column.type)" :size="16" />
                      {{ column.name }}
                    </div>
                    <div class="col-type">{{ column.type }}</div>
                    <div class="col-nullable">
                      <span class="nullable-badge" :class="{ nullable: column.nullable }">
                        {{ column.nullable ? 'YES' : 'NO' }}
                      </span>
                    </div>
                    <div class="col-key">
                      <span v-if="column.isPrimaryKey" class="key-badge primary">PK</span>
                      <span v-if="column.isForeignKey" class="key-badge foreign">FK</span>
                    </div>
                    <div class="col-description">{{ column.description || '-' }}</div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Lineage Tab -->
            <div v-if="activeTab === 'lineage'" class="tab-pane">
              <div class="lineage-graph-container">
                <div class="graph-controls">
                  <button class="graph-btn" @click="resetZoom">
                    <IconSystem name="maximize" :size="16" />
                    전체보기
                  </button>
                  <button class="graph-btn" @click="centerGraph">
                    <IconSystem name="target" :size="16" />
                    중앙정렬
                  </button>
                </div>
                
                <div class="lineage-graph" ref="lineageGraph">
                  <svg class="graph-svg" :viewBox="`0 0 ${graphWidth} ${graphHeight}`">
                    <!-- Connections (arrows) -->
                    <defs>
                      <marker id="arrowhead" markerWidth="10" markerHeight="7" 
                              refX="9" refY="3.5" orient="auto">
                        <polygon points="0 0, 10 3.5, 0 7" fill="#64748b" />
                      </marker>
                    </defs>
                    
                    <!-- Upstream connections -->
                    <line 
                      v-for="upstream in selectedTable.upstream" 
                      :key="`up-${upstream.id}`"
                      :x1="getNodePosition(upstream.id).x + 60"
                      :y1="getNodePosition(upstream.id).y + 30"
                      :x2="getNodePosition('current').x"
                      :y2="getNodePosition('current').y + 30"
                      stroke="#64748b"
                      stroke-width="2"
                      marker-end="url(#arrowhead)"
                      class="connection-line"
                    />
                    
                    <!-- Downstream connections -->
                    <line 
                      v-for="downstream in selectedTable.downstream" 
                      :key="`down-${downstream.id}`"
                      :x1="getNodePosition('current').x + 120"
                      :y1="getNodePosition('current').y + 30"
                      :x2="getNodePosition(downstream.id).x"
                      :y2="getNodePosition(downstream.id).y + 30"
                      stroke="#64748b"
                      stroke-width="2"
                      marker-end="url(#arrowhead)"
                      class="connection-line"
                    />
                    
                    <!-- Upstream nodes -->
                    <g 
                      v-for="(upstream, index) in selectedTable.upstream" 
                      :key="`upstream-${upstream.id}`"
                      class="graph-node upstream"
                      :transform="`translate(${getNodePosition(upstream.id).x}, ${getNodePosition(upstream.id).y})`"
                    >
                      <rect 
                        width="120" 
                        height="60" 
                        rx="8" 
                        class="node-rect upstream"
                      />
                      <text x="60" y="25" text-anchor="middle" class="node-title">
                        {{ upstream.name }}
                      </text>
                      <text x="60" y="40" text-anchor="middle" class="node-type">
                        {{ upstream.type }}
                      </text>
                    </g>
                    
                    <!-- Current table (center) -->
                    <g 
                      class="graph-node current"
                      :transform="`translate(${getNodePosition('current').x}, ${getNodePosition('current').y})`"
                    >
                      <rect 
                        width="120" 
                        height="60" 
                        rx="8" 
                        class="node-rect current"
                      />
                      <text x="60" y="25" text-anchor="middle" class="node-title">
                        {{ selectedTable.name }}
                      </text>
                      <text x="60" y="40" text-anchor="middle" class="node-type">
                        {{ selectedTable.type }}
                      </text>
                    </g>
                    
                    <!-- Downstream nodes -->
                    <g 
                      v-for="(downstream, index) in selectedTable.downstream" 
                      :key="`downstream-${downstream.id}`"
                      class="graph-node downstream"
                      :transform="`translate(${getNodePosition(downstream.id).x}, ${getNodePosition(downstream.id).y})`"
                    >
                      <rect 
                        width="120" 
                        height="60" 
                        rx="8" 
                        class="node-rect downstream"
                      />
                      <text x="60" y="25" text-anchor="middle" class="node-title">
                        {{ downstream.name }}
                      </text>
                      <text x="60" y="40" text-anchor="middle" class="node-type">
                        {{ downstream.type }}
                      </text>
                    </g>
                  </svg>
                </div>
                
                <!-- Legend -->
                <div class="graph-legend">
                  <div class="legend-item">
                    <div class="legend-color upstream"></div>
                    <span>업스트림 (데이터 소스)</span>
                  </div>
                  <div class="legend-item">
                    <div class="legend-color current"></div>
                    <span>현재 테이블</span>
                  </div>
                  <div class="legend-item">
                    <div class="legend-color downstream"></div>
                    <span>다운스트림 (데이터 활용)</span>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Quality Tab -->
            <div v-if="activeTab === 'quality'" class="tab-pane">
              <div class="quality-metrics">
                <div class="metric-card">
                  <div class="metric-header">
                    <h4>데이터 품질 점수</h4>
                    <div class="quality-score" :class="getQualityClass(selectedTable.qualityScore)">
                      {{ selectedTable.qualityScore }}%
                    </div>
                  </div>
                  <div class="metric-details">
                    <div class="metric-item">
                      <span class="metric-label">완전성:</span>
                      <div class="metric-bar">
                        <div class="metric-fill" :style="{ width: selectedTable.completeness + '%' }"></div>
                      </div>
                      <span class="metric-value">{{ selectedTable.completeness }}%</span>
                    </div>
                    <div class="metric-item">
                      <span class="metric-label">정확성:</span>
                      <div class="metric-bar">
                        <div class="metric-fill" :style="{ width: selectedTable.accuracy + '%' }"></div>
                      </div>
                      <span class="metric-value">{{ selectedTable.accuracy }}%</span>
                    </div>
                    <div class="metric-item">
                      <span class="metric-label">일관성:</span>
                      <div class="metric-bar">
                        <div class="metric-fill" :style="{ width: selectedTable.consistency + '%' }"></div>
                      </div>
                      <span class="metric-value">{{ selectedTable.consistency }}%</span>
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

<script>
import { ref, computed, onMounted } from 'vue'
import IconSystem from './IconSystem.vue'

export default {
  name: 'DataCatalogView',
  components: {
    IconSystem
  },
  setup() {
    const searchQuery = ref('')
    const selectedTable = ref(null)
    const selectedDatabase = ref('')
    const activeTab = ref('overview')
    
    const tabs = [
      { id: 'overview', label: '개요', icon: 'info' },
      { id: 'schema', label: '스키마', icon: 'table' },
      { id: 'lineage', label: '계보', icon: 'git-branch' },
      { id: 'quality', label: '품질', icon: 'check-circle' }
    ]
    
    const databases = ref([
      { id: 'sales_system', name: '영업시스템', engine: 'Oracle' },
      { id: 'channel_system', name: '채널시스템', engine: 'SQL Server' },
      { id: 'data_platform', name: '데이터플랫폼', engine: 'Databricks' },
      { id: 'analytics_system', name: '분석시스템', engine: 'MySQL' }
    ])
    
    const tables = ref([
      {
        id: 1,
        name: 'customer_info',
        schema: 'public',
        type: 'Table',
        engine: 'Oracle',
        database: 'sales_system',
        description: '고객 기본 정보 테이블',
        rowCount: 125000,
        columnCount: 15,
        dataSize: '2.3 GB',
        lastUpdated: '2024-01-15 14:30:00',
        dataOwner: '김데이터',
        dataSteward: '이관리',
        department: '마케팅팀',
        qualityScore: 92,
        completeness: 95,
        accuracy: 88,
        consistency: 93,
        columns: [
          { name: 'customer_id', type: 'BIGINT', nullable: false, isPrimaryKey: true, description: '고객 ID' },
          { name: 'customer_name', type: 'VARCHAR(100)', nullable: false, description: '고객명' },
          { name: 'email', type: 'VARCHAR(255)', nullable: true, description: '이메일 주소' },
          { name: 'phone', type: 'VARCHAR(20)', nullable: true, description: '전화번호' },
          { name: 'created_at', type: 'TIMESTAMP', nullable: false, description: '생성일시' }
        ],
        upstream: [
          { id: 'crm_system', name: 'CRM System', type: 'External System' }
        ],
        downstream: [
          { id: 'customer_analytics', name: 'Customer Analytics', type: 'View' },
          { id: 'marketing_campaign', name: 'Marketing Campaign', type: 'Table' }
        ]
      },
      {
        id: 2,
        name: 'order_history',
        schema: 'sales',
        type: 'Table',
        engine: 'SQL Server',
        database: 'channel_system',
        description: '주문 이력 정보',
        rowCount: 890000,
        columnCount: 12,
        dataSize: '5.7 GB',
        lastUpdated: '2024-01-15 16:45:00',
        dataOwner: '박영업',
        dataSteward: '최분석',
        department: '영업팀',
        qualityScore: 87,
        completeness: 92,
        accuracy: 85,
        consistency: 84,
        columns: [
          { name: 'order_id', type: 'BIGINT', nullable: false, isPrimaryKey: true, description: '주문 ID' },
          { name: 'customer_id', type: 'BIGINT', nullable: false, isForeignKey: true, description: '고객 ID' },
          { name: 'product_id', type: 'BIGINT', nullable: false, description: '상품 ID' },
          { name: 'quantity', type: 'INTEGER', nullable: false, description: '수량' },
          { name: 'order_date', type: 'DATE', nullable: false, description: '주문일자' }
        ],
        upstream: [
          { id: 'pos_system', name: 'POS System', type: 'External System' },
          { id: 'customer_info', name: 'Customer Info', type: 'Table' }
        ],
        downstream: [
          { id: 'sales_report', name: 'Sales Report', type: 'View' }
        ]
      },
      {
        id: 3,
        name: 'product_delta',
        schema: 'bronze',
        type: 'Delta Table',
        engine: 'Databricks',
        database: 'data_platform',
        description: '상품 정보 델타 테이블',
        rowCount: 45000,
        columnCount: 8,
        dataSize: '890 MB',
        lastUpdated: '2024-01-15 18:15:00',
        dataOwner: '이데이터',
        dataSteward: '박관리',
        department: '상품팀',
        qualityScore: 95,
        completeness: 98,
        accuracy: 94,
        consistency: 93,
        columns: [
          { name: 'product_id', type: 'BIGINT', nullable: false, isPrimaryKey: true, description: '상품 ID' },
          { name: 'product_name', type: 'STRING', nullable: false, description: '상품명' },
          { name: 'category', type: 'STRING', nullable: true, description: '카테고리' },
          { name: 'price', type: 'DECIMAL(10,2)', nullable: false, description: '가격' }
        ],
        upstream: [
          { id: 'product_api', name: 'Product API', type: 'External API' }
        ],
        downstream: [
          { id: 'product_silver', name: 'Product Silver', type: 'Delta Table' }
        ]
      },
      {
        id: 4,
        name: 'analytics_summary',
        schema: 'analytics',
        type: 'View',
        engine: 'MySQL',
        database: 'analytics_system',
        description: '분석용 요약 뷰',
        rowCount: 12500,
        columnCount: 20,
        dataSize: '156 MB',
        lastUpdated: '2024-01-15 20:00:00',
        dataOwner: '최분석',
        dataSteward: '김관리',
        department: '분석팀',
        qualityScore: 89,
        completeness: 91,
        accuracy: 87,
        consistency: 89,
        columns: [
          { name: 'summary_id', type: 'INT', nullable: false, isPrimaryKey: true, description: '요약 ID' },
          { name: 'period', type: 'DATE', nullable: false, description: '기간' },
          { name: 'total_sales', type: 'DECIMAL(15,2)', nullable: true, description: '총 매출' },
          { name: 'customer_count', type: 'INT', nullable: true, description: '고객 수' }
        ],
        upstream: [
          { id: 'order_history', name: 'Order History', type: 'Table' },
          { id: 'customer_info', name: 'Customer Info', type: 'Table' }
        ],
        downstream: []
      }
    ])
    
    const filteredTables = computed(() => {
      let filtered = tables.value
      
      // Filter by database
      if (selectedDatabase.value) {
        filtered = filtered.filter(table => table.database === selectedDatabase.value)
      }
      
      // Filter by search query
      if (searchQuery.value) {
        filtered = filtered.filter(table => 
          table.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          table.description.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          table.schema.toLowerCase().includes(searchQuery.value.toLowerCase())
        )
      }
      
      return filtered
    })
    
    const selectTable = (table) => {
      selectedTable.value = table
      activeTab.value = 'overview'
    }
    
    const getTableIcon = (table) => {
      if (table.type === 'Delta Table') return 'layers'
      if (table.engine === 'Databricks') return 'cloud'
      if (table.engine === 'Oracle' || table.engine === 'SQL Server') return 'hard-drive'
      if (table.engine === 'MySQL') return 'database'
      if (table.type === 'View') return 'eye'
      return 'table'
    }
    
    const getColumnIcon = (type) => {
      if (type.includes('INT') || type.includes('NUMERIC') || type.includes('DECIMAL')) return 'hash'
      if (type.includes('VARCHAR') || type.includes('TEXT') || type.includes('STRING')) return 'type'
      if (type.includes('DATE') || type.includes('TIMESTAMP')) return 'calendar'
      if (type.includes('BOOLEAN')) return 'toggle-left'
      return 'database'
    }
    
    const getQualityClass = (score) => {
      if (score >= 90) return 'excellent'
      if (score >= 80) return 'good'
      if (score >= 70) return 'fair'
      return 'poor'
    }
    
    // Graph layout properties
    const graphWidth = ref(800)
    const graphHeight = ref(400)
    
    const getNodePosition = (nodeId) => {
      if (!selectedTable.value) return { x: 0, y: 0 }
      
      const centerX = graphWidth.value / 2 - 60 // 60 is half of node width
      const centerY = graphHeight.value / 2 - 30 // 30 is half of node height
      
      if (nodeId === 'current') {
        return { x: centerX, y: centerY }
      }
      
      // Position upstream nodes to the left
      const upstreamIndex = selectedTable.value.upstream?.findIndex(item => item.id === nodeId)
      if (upstreamIndex !== -1) {
        const upstreamCount = selectedTable.value.upstream.length
        const spacing = Math.min(100, 200 / upstreamCount)
        const startY = centerY - ((upstreamCount - 1) * spacing) / 2
        return {
          x: centerX - 200,
          y: startY + (upstreamIndex * spacing)
        }
      }
      
      // Position downstream nodes to the right
      const downstreamIndex = selectedTable.value.downstream?.findIndex(item => item.id === nodeId)
      if (downstreamIndex !== -1) {
        const downstreamCount = selectedTable.value.downstream.length
        const spacing = Math.min(100, 200 / downstreamCount)
        const startY = centerY - ((downstreamCount - 1) * spacing) / 2
        return {
          x: centerX + 200,
          y: startY + (downstreamIndex * spacing)
        }
      }
      
      return { x: 0, y: 0 }
    }
    
    const resetZoom = () => {
      // Reset graph view to default
      console.log('Reset zoom')
    }
    
    const centerGraph = () => {
      // Center the graph
      console.log('Center graph')
    }
    
    onMounted(() => {
      if (tables.value.length > 0) {
        selectedTable.value = tables.value[0]
      }
    })
    
    return {
      searchQuery,
      selectedTable,
      selectedDatabase,
      activeTab,
      tabs,
      databases,
      tables,
      filteredTables,
      selectTable,
      getTableIcon,
      getColumnIcon,
      getQualityClass,
      graphWidth,
      graphHeight,
      getNodePosition,
      resetZoom,
      centerGraph
    }
  }
}
</script><style s
coped>
.data-catalog-view {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
}

.page-header {
  background: #f8fafc;
  padding: 1.5rem 2rem;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 1.875rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 0.25rem 0;
}

.page-subtitle {
  color: #64748b;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 0.75rem;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

.action-btn.primary {
  background: #3b82f6;
  border-color: #3b82f6;
  color: white;
}

.action-btn.primary:hover {
  background: #2563eb;
}

.catalog-workspace {
  flex: 1;
  display: flex;
  gap: 1rem;
  padding: 1rem 2rem;
  overflow: hidden;
}

.tables-panel {
  min-width: 350px;
  max-width: 400px;
  flex: 0 0 350px;
  background: white;
  border-radius: 0.5rem;
  border: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
}

.details-panel {
  flex: 1;
  background: white;
  border-radius: 0.5rem;
  border: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
}

.panel-header {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

@media (min-width: 768px) {
  .panel-header {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    gap: 0;
  }
}

.panel-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.panel-controls {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  min-width: 200px;
}

@media (min-width: 768px) {
  .panel-controls {
    min-width: 250px;
  }
}

.db-selector {
  padding: 0.5rem 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  background: white;
  cursor: pointer;
}

.db-selector:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-input {
  padding: 0.5rem 0.75rem 0.5rem 2.25rem;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  width: 100%;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.search-box svg {
  position: absolute;
  left: 0.75rem;
  color: #9ca3af;
}

.tables-list {
  flex: 1;
  overflow-y: auto;
  padding: 0.5rem;
}

.table-item {
  padding: 0.75rem;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 0.25rem;
}

.table-item:hover {
  background: #f1f5f9;
}

.table-item.active {
  background: #eff6ff;
  border: 1px solid #3b82f6;
}

.table-header {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  margin-bottom: 0.5rem;
}

.table-icon-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
  min-width: 60px;
}

.table-icon {
  color: #3b82f6;
}

.table-icon.icon-oracle {
  color: #f80000;
}

.table-icon.icon-databricks {
  color: #ff3621;
}

.table-icon.icon-mysql {
  color: #00758f;
}

.table-icon.icon-sql-server {
  color: #cc2927;
}

.engine-label {
  font-size: 0.625rem;
  font-weight: 600;
  text-align: center;
  padding: 0.125rem 0.25rem;
  border-radius: 0.25rem;
  text-transform: uppercase;
  line-height: 1;
}

.engine-label.engine-oracle {
  background: #fee2e2;
  color: #dc2626;
}

.engine-label.engine-databricks {
  background: #fef2f2;
  color: #ef4444;
}

.engine-label.engine-mysql {
  background: #ecfdf5;
  color: #059669;
}

.engine-label.engine-sql-server {
  background: #eff6ff;
  color: #2563eb;
}

.table-info {
  flex: 1;
  min-width: 0;
}

.table-name {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 0.125rem;
}

.table-schema {
  font-size: 0.75rem;
  color: #64748b;
}

.table-meta {
  display: flex;
  gap: 0.75rem;
  font-size: 0.75rem;
}

.table-type {
  background: #f1f5f9;
  color: #475569;
  padding: 0.125rem 0.5rem;
  border-radius: 0.25rem;
}

.table-rows {
  color: #64748b;
}

.table-actions {
  display: flex;
  gap: 0.5rem;
}

.details-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.tab-nav {
  display: flex;
  border-bottom: 1px solid #e2e8f0;
  padding: 0 1.5rem;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  border: none;
  background: none;
  color: #64748b;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
}

.tab-btn:hover {
  color: #3b82f6;
}

.tab-btn.active {
  color: #3b82f6;
  border-bottom-color: #3b82f6;
}

.tab-content {
  flex: 1;
  overflow-y: auto;
}

.tab-pane {
  padding: 1.5rem;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.info-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  padding: 1.25rem;
}

.info-card h4 {
  margin: 0 0 1rem 0;
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.75rem;
}

.info-row:last-child {
  margin-bottom: 0;
}

.label {
  color: #64748b;
  font-weight: 500;
}

.value {
  color: #1e293b;
  font-weight: 600;
}

.schema-table {
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 1.5fr 1fr 1fr 2fr;
  background: #f8fafc;
  padding: 0.75rem;
  font-weight: 600;
  color: #374151;
  border-bottom: 1px solid #e2e8f0;
}

.table-body {
  max-height: 400px;
  overflow-y: auto;
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 1.5fr 1fr 1fr 2fr;
  padding: 0.75rem;
  border-bottom: 1px solid #f1f5f9;
  align-items: center;
}

.table-row:hover {
  background: #f9fafb;
}

.col-name {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 500;
}

.nullable-badge {
  padding: 0.125rem 0.5rem;
  border-radius: 0.25rem;
  font-size: 0.75rem;
  font-weight: 500;
  background: #fee2e2;
  color: #dc2626;
}

.nullable-badge.nullable {
  background: #dcfce7;
  color: #16a34a;
}

.key-badge {
  padding: 0.125rem 0.375rem;
  border-radius: 0.25rem;
  font-size: 0.625rem;
  font-weight: 600;
  margin-right: 0.25rem;
}

.key-badge.primary {
  background: #fef3c7;
  color: #d97706;
}

.key-badge.foreign {
  background: #e0e7ff;
  color: #4f46e5;
}

.lineage-graph-container {
  display: flex;
  flex-direction: column;
  height: 500px;
}

.graph-controls {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.graph-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
}

.graph-btn:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

.lineage-graph {
  flex: 1;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  background: #fafafa;
  overflow: hidden;
  position: relative;
}

.graph-svg {
  width: 100%;
  height: 100%;
}

.connection-line {
  transition: stroke-width 0.2s ease, stroke 0.2s ease;
}

.connection-line:hover {
  stroke-width: 3;
  stroke: #3b82f6;
}

.graph-node {
  cursor: pointer;
}

.node-rect {
  stroke-width: 2;
  transition: stroke-width 0.2s ease, filter 0.2s ease;
}

.node-rect.upstream {
  fill: #dbeafe;
  stroke: #3b82f6;
}

.node-rect.current {
  fill: #fef3c7;
  stroke: #f59e0b;
  stroke-width: 3;
}

.node-rect.downstream {
  fill: #dcfce7;
  stroke: #10b981;
}

.graph-node:hover .node-rect {
  stroke-width: 3;
  filter: brightness(0.9);
}

.graph-node:hover .node-title {
  font-weight: 700;
}

.graph-node:hover .node-type {
  font-weight: 600;
}

.node-title {
  font-size: 12px;
  font-weight: 600;
  fill: #1e293b;
}

.node-type {
  font-size: 10px;
  fill: #64748b;
}

.graph-legend {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-top: 1rem;
  padding: 1rem;
  background: #f8fafc;
  border-radius: 0.375rem;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: #374151;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 0.25rem;
  border: 2px solid;
}

.legend-color.upstream {
  background: #dbeafe;
  border-color: #3b82f6;
}

.legend-color.current {
  background: #fef3c7;
  border-color: #f59e0b;
}

.legend-color.downstream {
  background: #dcfce7;
  border-color: #10b981;
}

.quality-metrics {
  max-width: 600px;
}

.metric-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  padding: 1.5rem;
}

.metric-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.metric-header h4 {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e293b;
}

.quality-score {
  font-size: 2rem;
  font-weight: 700;
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
}

.quality-score.excellent {
  background: #dcfce7;
  color: #16a34a;
}

.quality-score.good {
  background: #fef3c7;
  color: #d97706;
}

.quality-score.fair {
  background: #fed7aa;
  color: #ea580c;
}

.quality-score.poor {
  background: #fee2e2;
  color: #dc2626;
}

.metric-details {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.metric-item {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.metric-label {
  min-width: 80px;
  font-weight: 500;
  color: #374151;
}

.metric-bar {
  flex: 1;
  height: 8px;
  background: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
}

.metric-fill {
  height: 100%;
  background: #3b82f6;
  transition: width 0.3s ease;
}

.metric-value {
  min-width: 50px;
  text-align: right;
  font-weight: 600;
  color: #1e293b;
}
</style>