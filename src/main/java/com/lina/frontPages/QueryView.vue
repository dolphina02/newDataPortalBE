<template>
  <div class="query-view">
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h1 class="page-title">SQL Editor</h1>
          <p class="page-subtitle">SQL 쿼리를 작성하고 실행해보세요</p>
        </div>
        <div class="header-actions">
          <button class="action-btn">
            <IconSystem name="save" :size="16" />
            저장
          </button>
          <button class="action-btn primary">
            <IconSystem name="play" :size="16" />
            실행
          </button>
        </div>
      </div>
    </div>

    <div class="query-workspace">
      <!-- Editor Row -->
      <div class="editor-row">
        <!-- Schema Panel -->
        <div class="schema-panel">
          <div class="schema-header">
            <div class="schema-title">
              <IconSystem name="database" :size="16" />
              <span>스키마 정보</span>
            </div>
            <button class="refresh-schema-btn" @click="refreshSchema">
              <IconSystem name="refresh" :size="14" />
            </button>
          </div>
          <div class="schema-content">
            <div class="schema-search">
              <IconSystem name="search" :size="14" />
              <input 
                v-model="schemaSearchQuery"
                type="text" 
                placeholder="테이블 검색..."
                class="schema-search-input"
              />
            </div>
            
            <div class="schema-tree">
              <div 
                v-for="schema in filteredSchemas" 
                :key="schema.name"
                class="schema-group"
              >
                <div 
                  class="schema-header-item"
                  @click="toggleSchema(schema.name)"
                >
                  <IconSystem 
                    :name="expandedSchemas.includes(schema.name) ? 'chevron-down' : 'chevron-right'" 
                    :size="14" 
                  />
                  <IconSystem name="folder" :size="14" />
                  <span class="schema-name">{{ schema.name }}</span>
                  <span class="table-count">({{ schema.tables.length }})</span>
                </div>
                
                <div 
                  v-if="expandedSchemas.includes(schema.name)"
                  class="tables-list"
                >
                  <div 
                    v-for="table in schema.tables" 
                    :key="table.name"
                    class="table-item"
                  >
                    <div 
                      class="table-header"
                      @click="toggleTable(table.name)"
                    >
                      <IconSystem 
                        :name="expandedTables.includes(table.name) ? 'chevron-down' : 'chevron-right'" 
                        :size="12" 
                      />
                      <IconSystem name="table" :size="14" />
                      <span class="table-name" @click.stop="insertTableName(table.name)">{{ table.name }}</span>
                      <span class="row-count">{{ table.rowCount }}</span>
                      <button 
                        class="select-query-btn"
                        @click.stop="generateSelectQuery(table)"
                        title="SELECT 쿼리 생성"
                      >
                        &lt;&gt;
                      </button>
                    </div>
                    <div class="table-description">{{ table.description }}</div>
                    
                    <!-- Columns List -->
                    <div 
                      v-if="expandedTables.includes(table.name)"
                      class="columns-list"
                    >
                      <div 
                        v-for="column in table.columns" 
                        :key="column.name"
                        class="column-item"
                        @click="insertColumnName(column.name)"
                      >
                        <div class="column-header">
                          <IconSystem name="file-text" :size="12" />
                          <span class="column-name">{{ column.name }}</span>
                          <span class="column-type">{{ column.type }}</span>
                        </div>
                        <div class="column-description">{{ column.description }}</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Query Editor -->
        <div class="editor-panel">
          <div class="editor-header">
            <div class="editor-tabs">
              <div 
                v-for="(tab, index) in tabs" 
                :key="tab.id"
                class="tab" 
                :class="{ active: activeTabId === tab.id }"
                @click="setActiveTab(tab.id)"
              >
                <span class="tab-title">{{ tab.title }}</span>
                <button 
                  v-if="tabs.length > 1"
                  class="tab-close" 
                  @click.stop="closeTab(tab.id)"
                >
                  <IconSystem name="x" :size="12" />
                </button>
              </div>
              <button class="add-tab-btn" @click="addNewTab">
                <IconSystem name="plus" :size="14" />
              </button>
            </div>
            <div class="editor-actions">
              <button class="action-btn" @click="formatQuery">
                <IconSystem name="code" :size="16" />
                포맷
              </button>
              <button 
                class="action-btn primary" 
                @click="executeQuery" 
                :disabled="!currentTab?.query.trim()"
              >
                <IconSystem name="play" :size="16" />
                실행
              </button>
            </div>
          </div>
          
          <div class="editor-container">
            <textarea 
              v-model="currentTab.query"
              class="sql-editor"
              placeholder="SELECT * FROM insurance_policies WHERE created_date > '2024-01-01';"
              spellcheck="false"
              @keydown="handleKeydown"
            ></textarea>
          </div>
        </div>
      </div>

      <!-- Results Panel -->
      <div class="results-panel">
        <div class="results-header">
          <h3>결과 ({{ queryResults.length }})</h3>
        </div>
        
        <div class="results-content">
          <div v-if="queryResults.length === 0" class="empty-state">
            <p>쿼리를 실행하여 결과를 확인하세요</p>
          </div>
          
          <div v-else class="data-grid">
            <table class="results-table">
              <thead>
                <tr>
                  <th v-for="column in columns" :key="column">{{ column }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(row, index) in queryResults" :key="index">
                  <td v-for="column in columns" :key="column">{{ row[column] }}</td>
                </tr>
              </tbody>
            </table>
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
const tabs = ref([
  { id: 1, title: '새 쿼리', query: '' }
])
const activeTabId = ref(1)
const queryResults = ref([])
const columns = ref([])
const schemaSearchQuery = ref('')
const expandedSchemas = ref(['insurance', 'customer'])
let tabIdCounter = 1

const expandedTables = ref([])

// Schema data with hierarchical structure including columns
const schemas = ref([
  {
    name: 'insurance',
    tables: [
      { 
        name: 'insurance_policies', 
        description: '보험 계약 정보', 
        rowCount: '1.2M',
        columns: [
          { name: 'policy_id', type: 'VARCHAR(20)', description: '보험계약번호' },
          { name: 'customer_id', type: 'VARCHAR(20)', description: '고객ID' },
          { name: 'product_id', type: 'VARCHAR(10)', description: '상품ID' },
          { name: 'policy_status', type: 'VARCHAR(10)', description: '계약상태' },
          { name: 'start_date', type: 'DATE', description: '계약시작일' },
          { name: 'end_date', type: 'DATE', description: '계약종료일' },
          { name: 'premium_amount', type: 'DECIMAL(15,2)', description: '보험료' },
          { name: 'created_at', type: 'TIMESTAMP', description: '생성일시' }
        ]
      },
      { 
        name: 'customers', 
        description: '고객 기본 정보', 
        rowCount: '850K',
        columns: [
          { name: 'customer_id', type: 'VARCHAR(20)', description: '고객ID' },
          { name: 'customer_name', type: 'VARCHAR(100)', description: '고객명' },
          { name: 'birth_date', type: 'DATE', description: '생년월일' },
          { name: 'gender', type: 'CHAR(1)', description: '성별' },
          { name: 'phone_number', type: 'VARCHAR(20)', description: '전화번호' },
          { name: 'email', type: 'VARCHAR(100)', description: '이메일' },
          { name: 'address', type: 'VARCHAR(200)', description: '주소' },
          { name: 'registration_date', type: 'TIMESTAMP', description: '등록일시' }
        ]
      },
      { 
        name: 'claims', 
        description: '보험 청구 내역', 
        rowCount: '2.3M',
        columns: [
          { name: 'claim_id', type: 'VARCHAR(20)', description: '청구ID' },
          { name: 'policy_id', type: 'VARCHAR(20)', description: '보험계약번호' },
          { name: 'claim_type', type: 'VARCHAR(20)', description: '청구유형' },
          { name: 'claim_amount', type: 'DECIMAL(15,2)', description: '청구금액' },
          { name: 'claim_status', type: 'VARCHAR(10)', description: '청구상태' },
          { name: 'claim_date', type: 'DATE', description: '청구일자' },
          { name: 'settlement_amount', type: 'DECIMAL(15,2)', description: '지급금액' },
          { name: 'processed_at', type: 'TIMESTAMP', description: '처리일시' }
        ]
      },
      { 
        name: 'agents', 
        description: '보험 설계사 정보', 
        rowCount: '15K',
        columns: [
          { name: 'agent_id', type: 'VARCHAR(20)', description: '설계사ID' },
          { name: 'agent_name', type: 'VARCHAR(100)', description: '설계사명' },
          { name: 'license_number', type: 'VARCHAR(20)', description: '자격증번호' },
          { name: 'branch_code', type: 'VARCHAR(10)', description: '지점코드' },
          { name: 'hire_date', type: 'DATE', description: '입사일' },
          { name: 'status', type: 'VARCHAR(10)', description: '상태' }
        ]
      },
      { 
        name: 'products', 
        description: '보험 상품 정보', 
        rowCount: '245',
        columns: [
          { name: 'product_id', type: 'VARCHAR(10)', description: '상품ID' },
          { name: 'product_name', type: 'VARCHAR(100)', description: '상품명' },
          { name: 'product_type', type: 'VARCHAR(20)', description: '상품유형' },
          { name: 'base_premium', type: 'DECIMAL(15,2)', description: '기본보험료' },
          { name: 'coverage_amount', type: 'DECIMAL(15,2)', description: '보장금액' },
          { name: 'is_active', type: 'BOOLEAN', description: '활성여부' }
        ]
      }
    ]
  },
  {
    name: 'customer',
    tables: [
      { 
        name: 'customer_contacts', 
        description: '고객 연락처 정보', 
        rowCount: '1.1M',
        columns: [
          { name: 'contact_id', type: 'VARCHAR(20)', description: '연락처ID' },
          { name: 'customer_id', type: 'VARCHAR(20)', description: '고객ID' },
          { name: 'contact_type', type: 'VARCHAR(10)', description: '연락처유형' },
          { name: 'contact_value', type: 'VARCHAR(100)', description: '연락처값' },
          { name: 'is_primary', type: 'BOOLEAN', description: '주연락처여부' }
        ]
      },
      { 
        name: 'customer_segments', 
        description: '고객 세그먼트 정보', 
        rowCount: '125',
        columns: [
          { name: 'segment_id', type: 'VARCHAR(10)', description: '세그먼트ID' },
          { name: 'segment_name', type: 'VARCHAR(50)', description: '세그먼트명' },
          { name: 'description', type: 'TEXT', description: '설명' },
          { name: 'criteria', type: 'JSON', description: '기준' }
        ]
      }
    ]
  },
  {
    name: 'claims',
    tables: [
      { 
        name: 'claim_documents', 
        description: '청구 서류 정보', 
        rowCount: '4.5M',
        columns: [
          { name: 'document_id', type: 'VARCHAR(20)', description: '서류ID' },
          { name: 'claim_id', type: 'VARCHAR(20)', description: '청구ID' },
          { name: 'document_type', type: 'VARCHAR(20)', description: '서류유형' },
          { name: 'file_path', type: 'VARCHAR(500)', description: '파일경로' },
          { name: 'upload_date', type: 'TIMESTAMP', description: '업로드일시' }
        ]
      }
    ]
  }
])

// Computed
const currentTab = computed(() => {
  return tabs.value.find(tab => tab.id === activeTabId.value)
})

const filteredSchemas = computed(() => {
  if (!schemaSearchQuery.value.trim()) {
    return schemas.value
  }
  
  const query = schemaSearchQuery.value.toLowerCase()
  return schemas.value.map(schema => ({
    ...schema,
    tables: schema.tables.filter(table => 
      table.name.toLowerCase().includes(query) ||
      table.description.toLowerCase().includes(query)
    )
  })).filter(schema => schema.tables.length > 0)
})

const sampleData = [
  {
    policy_id: 'POL001',
    customer_name: '김철수',
    product_type: '생명보험',
    premium: '₩150,000',
    start_date: '2024-01-15',
    status: '활성'
  },
  {
    policy_id: 'POL002',
    customer_name: '이영희',
    product_type: '건강보험',
    premium: '₩85,000',
    start_date: '2024-02-01',
    status: '활성'
  },
  {
    policy_id: 'POL003',
    customer_name: '박민수',
    product_type: '자동차보험',
    premium: '₩120,000',
    start_date: '2024-01-28',
    status: '만료'
  }
]

// Methods
const addNewTab = () => {
  tabIdCounter++
  const newTab = {
    id: tabIdCounter,
    title: `쿼리 ${tabIdCounter}`,
    query: ''
  }
  tabs.value.push(newTab)
  activeTabId.value = newTab.id
}

const closeTab = (tabId) => {
  if (tabs.value.length === 1) return
  
  const tabIndex = tabs.value.findIndex(tab => tab.id === tabId)
  tabs.value.splice(tabIndex, 1)
  
  if (activeTabId.value === tabId) {
    activeTabId.value = tabs.value[Math.max(0, tabIndex - 1)].id
  }
}

const setActiveTab = (tabId) => {
  activeTabId.value = tabId
}

const toggleSchema = (schemaName) => {
  const index = expandedSchemas.value.indexOf(schemaName)
  if (index > -1) {
    expandedSchemas.value.splice(index, 1)
  } else {
    expandedSchemas.value.push(schemaName)
  }
}

const toggleTable = (tableName) => {
  const index = expandedTables.value.indexOf(tableName)
  if (index > -1) {
    expandedTables.value.splice(index, 1)
  } else {
    expandedTables.value.push(tableName)
  }
}

const refreshSchema = () => {
  console.log('스키마 새로고침')
}

const generateSelectQuery = (table) => {
  if (!currentTab.value) return
  
  // Generate column list
  const columnNames = table.columns.map(col => col.name).join(', ')
  const query = `SELECT ${columnNames}\nFROM ${table.name}\nLIMIT 10;`
  
  currentTab.value.query = query
  
  // Focus on editor
  setTimeout(() => {
    const textarea = document.querySelector('.sql-editor')
    if (textarea) {
      textarea.focus()
    }
  }, 0)
}

const insertColumnName = (columnName) => {
  if (!currentTab.value) return
  
  const textarea = document.querySelector('.sql-editor')
  if (textarea) {
    const start = textarea.selectionStart
    const end = textarea.selectionEnd
    const currentQuery = currentTab.value.query
    
    const newQuery = currentQuery.substring(0, start) + columnName + currentQuery.substring(end)
    currentTab.value.query = newQuery
    
    // Focus back to textarea
    setTimeout(() => {
      textarea.focus()
      textarea.selectionStart = textarea.selectionEnd = start + columnName.length
    }, 0)
  }
}

const formatQuery = () => {
  if (!currentTab.value?.query.trim()) return
  
  // Simple SQL formatting
  let formatted = currentTab.value.query
    .replace(/\s+/g, ' ')
    .replace(/,/g, ',\n  ')
    .replace(/\bFROM\b/gi, '\nFROM')
    .replace(/\bWHERE\b/gi, '\nWHERE')
    .replace(/\bORDER BY\b/gi, '\nORDER BY')
    .replace(/\bGROUP BY\b/gi, '\nGROUP BY')
    .replace(/\bHAVING\b/gi, '\nHAVING')
    .trim()
  
  currentTab.value.query = formatted
}

const executeQuery = () => {
  if (!currentTab.value?.query.trim()) return
  
  // Simulate query execution
  const resultCount = Math.floor(Math.random() * 3) + 2
  const results = []
  
  for (let i = 0; i < resultCount; i++) {
    const randomIndex = Math.floor(Math.random() * sampleData.length)
    results.push({ ...sampleData[randomIndex] })
  }
  
  queryResults.value = results
  columns.value = results.length > 0 ? Object.keys(results[0]) : []
}

const insertTableName = (tableName) => {
  if (!currentTab.value) return
  
  const textarea = document.querySelector('.sql-editor')
  if (textarea) {
    const start = textarea.selectionStart
    const end = textarea.selectionEnd
    const currentQuery = currentTab.value.query
    
    const newQuery = currentQuery.substring(0, start) + tableName + currentQuery.substring(end)
    currentTab.value.query = newQuery
    
    // Focus back to textarea
    setTimeout(() => {
      textarea.focus()
      textarea.selectionStart = textarea.selectionEnd = start + tableName.length
    }, 0)
  }
}

const handleKeydown = (event) => {
  // Ctrl/Cmd + Enter to execute
  if ((event.ctrlKey || event.metaKey) && event.key === 'Enter') {
    event.preventDefault()
    executeQuery()
  }
  
  // Tab key for indentation
  if (event.key === 'Tab') {
    event.preventDefault()
    const textarea = event.target
    const start = textarea.selectionStart
    const end = textarea.selectionEnd
    
    textarea.value = textarea.value.substring(0, start) + '  ' + textarea.value.substring(end)
    textarea.selectionStart = textarea.selectionEnd = start + 2
    
    // Update v-model
    currentTab.value.query = textarea.value
  }
}
</script>

<style scoped>
.query-view {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding: var(--space-4);
  gap: var(--space-4);
}

.query-workspace {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
  flex: 1;
  min-height: 0;
}

.editor-row {
  display: grid;
  grid-template-columns: 350px 1fr;
  gap: var(--space-4);
  flex: 1;
  min-height: 300px;
}

.schema-panel {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.schema-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-3) var(--space-4);
  border-bottom: 1px solid var(--border-primary);
  background: var(--surface-hover);
}

.schema-title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--fs-base);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0;
}

.refresh-schema-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border: 1px solid var(--border-primary);
  background: var(--surface);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-secondary);
}

.refresh-schema-btn:hover {
  background: var(--lina-yellow);
  color: var(--gray-800);
  border-color: var(--lina-yellow);
}

.schema-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.schema-search {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3);
  border-bottom: 1px solid var(--border-primary);
}

.schema-search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: var(--fs-sm);
  color: var(--text-primary);
}

.schema-search-input::placeholder {
  color: var(--text-tertiary);
}

.schema-tree {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-2);
}

.schema-group {
  margin-bottom: var(--space-2);
}

.schema-header-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2);
  cursor: pointer;
  border-radius: var(--radius-md);
  transition: var(--transition-fast);
  font-weight: var(--fw-medium);
  color: var(--text-primary);
}

.schema-header-item:hover {
  background: var(--surface-hover);
}

.schema-name {
  flex: 1;
  font-size: var(--fs-sm);
}

.table-count {
  font-size: var(--fs-xs);
  color: var(--text-tertiary);
}

.tables-list {
  margin-left: var(--space-6);
  padding: var(--space-1) 0;
}

.table-item {
  padding: var(--space-2);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  margin-bottom: var(--space-1);
}

.table-item:hover {
  background: var(--surface-hover);
}

.table-header {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-bottom: var(--space-1);
}

.table-name {
  flex: 1;
  font-size: var(--fs-sm);
  font-weight: var(--fw-medium);
  color: var(--text-primary);
  font-family: 'JetBrains Mono', monospace;
  cursor: pointer;
  padding: 2px 4px;
  border-radius: var(--radius-sm);
  transition: var(--transition-fast);
}

.table-name:hover {
  background: var(--lina-yellow);
  color: var(--gray-800);
}

.row-count {
  font-size: var(--fs-xs);
  color: var(--text-tertiary);
  background: var(--surface-hover);
  padding: 2px var(--space-1);
  border-radius: var(--radius-sm);
}

.table-description {
  font-size: var(--fs-xs);
  color: var(--text-secondary);
  margin-left: 22px;
  margin-bottom: var(--space-2);
}

.select-query-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border: 1px solid var(--border-primary);
  background: var(--surface);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: var(--transition-fast);
  font-size: 10px;
  font-weight: var(--fw-bold);
  color: var(--text-secondary);
  font-family: monospace;
}

.select-query-btn:hover {
  background: var(--lina-orange);
  color: white;
  border-color: var(--lina-orange);
}

.columns-list {
  margin-left: var(--space-6);
  padding: var(--space-1) 0;
  border-left: 2px solid var(--border-primary);
  margin-left: 44px;
}

.column-item {
  padding: var(--space-1) var(--space-2);
  margin-bottom: 1px;
  cursor: pointer;
  transition: var(--transition-fast);
  border-radius: var(--radius-sm);
}

.column-item:hover {
  background: var(--surface-hover);
}

.column-header {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.column-name {
  font-size: var(--fs-xs);
  font-weight: var(--fw-medium);
  color: var(--text-primary);
  font-family: 'JetBrains Mono', monospace;
  min-width: 120px;
}

.column-type {
  font-size: var(--fs-xs);
  color: var(--lina-yellow-dark);
  background: rgba(255, 165, 0, 0.1);
  padding: 1px var(--space-1);
  border-radius: var(--radius-sm);
  font-family: 'JetBrains Mono', monospace;
}

.column-description {
  font-size: var(--fs-xs);
  color: var(--text-secondary);
  margin-left: 26px;
  margin-top: 2px;
}

.editor-panel {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-3) var(--space-4);
  border-bottom: 1px solid var(--border-primary);
  background: var(--surface-hover);
}

.editor-tabs {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.tab {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md) var(--radius-md) 0 0;
  cursor: pointer;
  transition: var(--transition-fast);
  font-size: var(--fs-sm);
  color: var(--text-secondary);
  border-bottom: none;
}

.tab:hover {
  background: var(--surface-hover);
  color: var(--text-primary);
}

.tab.active {
  background: var(--surface);
  color: var(--text-primary);
  border-color: var(--lina-orange);
  position: relative;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--lina-orange);
}

.tab-title {
  font-weight: var(--fw-medium);
}

.tab-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  border: none;
  background: none;
  cursor: pointer;
  border-radius: var(--radius-sm);
  color: var(--text-tertiary);
  transition: var(--transition-fast);
}

.tab-close:hover {
  background: var(--surface-active);
  color: var(--text-primary);
}

.add-tab-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border: 1px solid var(--border-primary);
  background: var(--surface);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-secondary);
}

.add-tab-btn:hover {
  background: var(--lina-orange);
  color: white;
  border-color: var(--lina-orange);
}

.editor-actions {
  display: flex;
  gap: var(--space-2);
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
  font-weight: var(--fw-medium);
  color: var(--text-secondary);
}

.action-btn:hover:not(:disabled) {
  background: var(--surface-hover);
  color: var(--text-primary);
}

.action-btn.primary {
  background: var(--lina-orange);
  color: white;
  border-color: var(--lina-orange);
}

.action-btn.primary:hover:not(:disabled) {
  background: var(--lina-yellow);
  border-color: var(--lina-yellow);
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.editor-container {
  flex: 1;
}

.sql-editor {
  width: 100%;
  height: 100%;
  border: none;
  outline: none;
  padding: var(--space-4);
  font-family: 'JetBrains Mono', monospace;
  font-size: 14px;
  color: var(--text-primary);
  background: var(--surface);
  resize: none;
}

.results-panel {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 300px;
}

.results-header {
  padding: var(--space-3);
  border-bottom: 1px solid var(--border-primary);
  background: var(--surface-hover);
}

.results-header h3 {
  font-size: var(--fs-base);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0;
}

.results-content {
  flex: 1;
  overflow: auto;
  padding: var(--space-4);
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--text-secondary);
}

.results-table {
  width: 100%;
  border-collapse: collapse;
  font-family: 'JetBrains Mono', monospace;
  font-size: 13px;
}

.results-table th {
  background: var(--surface-hover);
  padding: var(--space-2);
  text-align: left;
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  border: 1px solid var(--border-primary);
}

.results-table td {
  padding: var(--space-2);
  border: 1px solid var(--border-primary);
  color: var(--text-primary);
}

.results-table tr:nth-child(even) {
  background: var(--surface-hover);
}

@media (max-width: 768px) {
  .editor-row {
    grid-template-columns: 1fr;
  }
  
  .schema-panel {
    max-height: 200px;
  }
}
</style>