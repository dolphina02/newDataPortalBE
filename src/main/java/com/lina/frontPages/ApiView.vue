<template>
  <div class="api-view">
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h1 class="page-title">API Explorer</h1>
          <p class="page-subtitle">REST API를 탐색하고 테스트해보세요</p>
        </div>
        <div class="header-actions">
          <button class="action-btn">
            <IconSystem name="book" :size="16" />
            문서
          </button>
          <button class="action-btn primary">
            <IconSystem name="play" :size="16" />
            테스트
          </button>
        </div>
      </div>
    </div>

    <div class="api-workspace">
      <!-- API Sidebar -->
      <aside class="api-sidebar">
        <div class="sidebar-header">
          <h3>API 목록</h3>
          <button class="refresh-btn" @click="refreshApis">🔄</button>
        </div>
        
        <div class="api-search">
          <input 
            v-model="apiSearchQuery"
            type="text" 
            placeholder="API 검색..."
            class="search-input"
          />
        </div>

        <div class="api-categories">
          <div 
            v-for="category in filteredApiCategories" 
            :key="category.name"
            class="api-category"
          >
            <div 
              class="category-header"
              @click="toggleCategory(category.name)"
            >
              <span class="category-icon">{{ category.expanded ? '📂' : '📁' }}</span>
              <span class="category-name">{{ category.name }}</span>
              <span class="category-count">({{ category.endpoints.length }})</span>
            </div>
            
            <div v-if="category.expanded" class="category-endpoints">
              <div 
                v-for="endpoint in category.endpoints" 
                :key="endpoint.id"
                class="endpoint-item"
                :class="{ active: selectedEndpoint?.id === endpoint.id }"
                @click="selectEndpoint(endpoint)"
              >
                <span class="method-badge" :class="endpoint.method.toLowerCase()">
                  {{ endpoint.method }}
                </span>
                <span class="endpoint-path">{{ endpoint.path }}</span>
              </div>
            </div>
          </div>
        </div>
      </aside>

      <!-- Main Content -->
      <main class="api-main">
        <div v-if="!selectedEndpoint" class="empty-state">
          <div class="empty-icon">🔌</div>
          <h3>API를 선택해주세요</h3>
          <p>왼쪽 목록에서 테스트할 API를 선택하세요</p>
        </div>

        <div v-else class="endpoint-details">
          <!-- Endpoint Header -->
          <div class="endpoint-header">
            <div class="endpoint-title">
              <span class="method-badge large" :class="selectedEndpoint.method.toLowerCase()">
                {{ selectedEndpoint.method }}
              </span>
              <span class="endpoint-path">{{ selectedEndpoint.path }}</span>
            </div>
            <div class="endpoint-meta">
              <span class="endpoint-category">{{ selectedEndpoint.category }}</span>
              <span class="endpoint-version">v{{ selectedEndpoint.version }}</span>
            </div>
          </div>

          <div class="endpoint-description">
            {{ selectedEndpoint.description }}
          </div>

          <!-- Request Configuration -->
          <div class="request-section">
            <div class="section-tabs">
              <button 
                v-for="tab in requestTabs" 
                :key="tab.id"
                class="tab-btn"
                :class="{ active: activeRequestTab === tab.id }"
                @click="activeRequestTab = tab.id"
              >
                {{ tab.label }}
              </button>
            </div>

            <div class="tab-content">
              <!-- Parameters Tab -->
              <div v-if="activeRequestTab === 'params'" class="params-section">
                <div v-if="selectedEndpoint.parameters?.length" class="param-groups">
                  <div class="param-group">
                    <h4>Path Parameters</h4>
                    <div class="param-list">
                      <div 
                        v-for="param in pathParams" 
                        :key="param.name"
                        class="param-item"
                      >
                        <div class="param-info">
                          <span class="param-name">{{ param.name }}</span>
                          <span class="param-type">{{ param.type }}</span>
                          <span v-if="param.required" class="param-required">*</span>
                        </div>
                        <input 
                          v-model="requestParams[param.name]"
                          :type="param.type === 'number' ? 'number' : 'text'"
                          :placeholder="param.example"
                          class="param-input"
                        />
                      </div>
                    </div>
                  </div>

                  <div class="param-group">
                    <h4>Query Parameters</h4>
                    <div class="param-list">
                      <div 
                        v-for="param in queryParams" 
                        :key="param.name"
                        class="param-item"
                      >
                        <div class="param-info">
                          <span class="param-name">{{ param.name }}</span>
                          <span class="param-type">{{ param.type }}</span>
                          <span v-if="param.required" class="param-required">*</span>
                        </div>
                        <input 
                          v-model="requestParams[param.name]"
                          :type="param.type === 'number' ? 'number' : 'text'"
                          :placeholder="param.example"
                          class="param-input"
                        />
                      </div>
                    </div>
                  </div>
                </div>
                
                <div v-else class="no-params">
                  이 API는 매개변수가 필요하지 않습니다.
                </div>
              </div>

              <!-- Headers Tab -->
              <div v-if="activeRequestTab === 'headers'" class="headers-section">
                <div class="header-list">
                  <div 
                    v-for="(header, index) in requestHeaders" 
                    :key="index"
                    class="header-item"
                  >
                    <input 
                      v-model="header.key"
                      placeholder="Header Name"
                      class="header-input"
                    />
                    <input 
                      v-model="header.value"
                      placeholder="Header Value"
                      class="header-input"
                    />
                    <button 
                      class="remove-btn"
                      @click="removeHeader(index)"
                    >
                      ×
                    </button>
                  </div>
                </div>
                <button class="add-header-btn" @click="addHeader">
                  + 헤더 추가
                </button>
              </div>

              <!-- Body Tab -->
              <div v-if="activeRequestTab === 'body'" class="body-section">
                <div v-if="selectedEndpoint.method !== 'GET'" class="body-editor">
                  <div class="body-type-selector">
                    <label>
                      <input 
                        v-model="requestBodyType" 
                        type="radio" 
                        value="json"
                      />
                      JSON
                    </label>
                    <label>
                      <input 
                        v-model="requestBodyType" 
                        type="radio" 
                        value="form"
                      />
                      Form Data
                    </label>
                  </div>
                  
                  <textarea 
                    v-if="requestBodyType === 'json'"
                    v-model="requestBody"
                    class="body-textarea"
                    placeholder='{"key": "value"}'
                  ></textarea>
                  
                  <div v-else class="form-data">
                    <div 
                      v-for="(field, index) in formData" 
                      :key="index"
                      class="form-field"
                    >
                      <input 
                        v-model="field.key"
                        placeholder="Key"
                        class="form-input"
                      />
                      <input 
                        v-model="field.value"
                        placeholder="Value"
                        class="form-input"
                      />
                      <button 
                        class="remove-btn"
                        @click="removeFormField(index)"
                      >
                        ×
                      </button>
                    </div>
                    <button class="add-field-btn" @click="addFormField">
                      + 필드 추가
                    </button>
                  </div>
                </div>
                
                <div v-else class="no-body">
                  GET 요청은 본문이 필요하지 않습니다.
                </div>
              </div>
            </div>
          </div>

          <!-- Send Request Button -->
          <div class="request-actions">
            <button 
              class="send-btn"
              :disabled="isLoading"
              @click="sendRequest"
            >
              <span v-if="isLoading" class="loading-spinner"></span>
              <span v-else>📤</span>
              {{ isLoading ? '요청 중...' : '요청 보내기' }}
            </button>
            <button class="save-btn" @click="saveRequest">
              💾 저장
            </button>
          </div>

          <!-- Response Section -->
          <div v-if="response" class="response-section">
            <div class="response-header">
              <h3>응답</h3>
              <div class="response-meta">
                <span class="status-code" :class="getStatusClass(response.status)">
                  {{ response.status }}
                </span>
                <span class="response-time">{{ response.time }}ms</span>
                <span class="response-size">{{ response.size }}</span>
              </div>
            </div>

            <div class="response-tabs">
              <button 
                v-for="tab in responseTabs" 
                :key="tab.id"
                class="tab-btn"
                :class="{ active: activeResponseTab === tab.id }"
                @click="activeResponseTab = tab.id"
              >
                {{ tab.label }}
              </button>
            </div>

            <div class="response-content">
              <div v-if="activeResponseTab === 'body'" class="response-body">
                <pre><code>{{ formatJson(response.body) }}</code></pre>
              </div>
              
              <div v-if="activeResponseTab === 'headers'" class="response-headers">
                <div 
                  v-for="(value, key) in response.headers" 
                  :key="key"
                  class="response-header-item"
                >
                  <span class="header-key">{{ key }}:</span>
                  <span class="header-value">{{ value }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const apiSearchQuery = ref('')
const selectedEndpoint = ref(null)
const activeRequestTab = ref('params')
const activeResponseTab = ref('body')
const isLoading = ref(false)
const response = ref(null)

const requestParams = ref({})
const requestHeaders = ref([{ key: 'Content-Type', value: 'application/json' }])
const requestBody = ref('')
const requestBodyType = ref('json')
const formData = ref([{ key: '', value: '' }])

// Sample API data
const apiCategories = ref([
  {
    name: '사용자 관리',
    expanded: true,
    endpoints: [
      {
        id: 1,
        method: 'GET',
        path: '/api/users',
        category: '사용자 관리',
        version: '1.0',
        description: '모든 사용자 목록을 조회합니다.',
        parameters: [
          { name: 'page', type: 'number', required: false, example: '1', location: 'query' },
          { name: 'limit', type: 'number', required: false, example: '10', location: 'query' }
        ]
      },
      {
        id: 2,
        method: 'GET',
        path: '/api/users/{id}',
        category: '사용자 관리',
        version: '1.0',
        description: '특정 사용자의 상세 정보를 조회합니다.',
        parameters: [
          { name: 'id', type: 'number', required: true, example: '123', location: 'path' }
        ]
      },
      {
        id: 3,
        method: 'POST',
        path: '/api/users',
        category: '사용자 관리',
        version: '1.0',
        description: '새로운 사용자를 생성합니다.',
        parameters: []
      }
    ]
  },
  {
    name: '주문 관리',
    expanded: false,
    endpoints: [
      {
        id: 4,
        method: 'GET',
        path: '/api/orders',
        category: '주문 관리',
        version: '1.0',
        description: '주문 목록을 조회합니다.',
        parameters: [
          { name: 'status', type: 'string', required: false, example: 'pending', location: 'query' }
        ]
      }
    ]
  }
])

const requestTabs = [
  { id: 'params', label: '매개변수' },
  { id: 'headers', label: '헤더' },
  { id: 'body', label: '본문' }
]

const responseTabs = [
  { id: 'body', label: '응답 본문' },
  { id: 'headers', label: '응답 헤더' }
]

const filteredApiCategories = computed(() => {
  if (!apiSearchQuery.value) return apiCategories.value
  
  return apiCategories.value.map(category => ({
    ...category,
    endpoints: category.endpoints.filter(endpoint =>
      endpoint.path.toLowerCase().includes(apiSearchQuery.value.toLowerCase()) ||
      endpoint.description.toLowerCase().includes(apiSearchQuery.value.toLowerCase())
    )
  })).filter(category => category.endpoints.length > 0)
})

const pathParams = computed(() => {
  if (!selectedEndpoint.value?.parameters) return []
  return selectedEndpoint.value.parameters.filter(p => p.location === 'path')
})

const queryParams = computed(() => {
  if (!selectedEndpoint.value?.parameters) return []
  return selectedEndpoint.value.parameters.filter(p => p.location === 'query')
})

const toggleCategory = (categoryName) => {
  const category = apiCategories.value.find(c => c.name === categoryName)
  if (category) {
    category.expanded = !category.expanded
  }
}

const selectEndpoint = (endpoint) => {
  selectedEndpoint.value = endpoint
  requestParams.value = {}
  response.value = null
  
  // Initialize parameters with default values
  if (endpoint.parameters) {
    endpoint.parameters.forEach(param => {
      if (param.example) {
        requestParams.value[param.name] = param.example
      }
    })
  }
}

const addHeader = () => {
  requestHeaders.value.push({ key: '', value: '' })
}

const removeHeader = (index) => {
  requestHeaders.value.splice(index, 1)
}

const addFormField = () => {
  formData.value.push({ key: '', value: '' })
}

const removeFormField = (index) => {
  formData.value.splice(index, 1)
}

const sendRequest = async () => {
  if (!selectedEndpoint.value) return
  
  isLoading.value = true
  
  // Simulate API call
  setTimeout(() => {
    response.value = {
      status: 200,
      time: Math.floor(Math.random() * 500) + 100,
      size: '2.1 KB',
      headers: {
        'Content-Type': 'application/json',
        'Cache-Control': 'no-cache',
        'X-RateLimit-Remaining': '99'
      },
      body: {
        success: true,
        data: [
          { id: 1, name: '김철수', email: 'kim@example.com' },
          { id: 2, name: '이영희', email: 'lee@example.com' }
        ],
        pagination: {
          page: 1,
          limit: 10,
          total: 2
        }
      }
    }
    isLoading.value = false
  }, 1000)
}

const saveRequest = () => {
  console.log('요청 저장')
}

const refreshApis = () => {
  console.log('API 목록 새로고침')
}

const getStatusClass = (status) => {
  if (status >= 200 && status < 300) return 'success'
  if (status >= 400 && status < 500) return 'client-error'
  if (status >= 500) return 'server-error'
  return 'info'
}

const formatJson = (obj) => {
  return JSON.stringify(obj, null, 2)
}
</script>

<style scoped>
.api-view {
  display: flex;
  flex-direction: column;
  padding: var(--space-4);
  gap: var(--space-4);
  height: 100vh;
}



.api-workspace {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
  flex: 1;
  overflow: hidden;
}

/* API Sidebar */
.api-sidebar {
  background: var(--card);
  border: 1px solid var(--line);
  border-radius: var(--radius);
  padding: 20px;
  box-shadow: var(--shadow-sm);
  overflow-y: auto;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  color: var(--ink);
}

.refresh-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  padding: 4px;
  border-radius: 4px;
  transition: var(--transition-fast);
}

.refresh-btn:hover {
  background: var(--card-hover);
}

.api-search {
  margin-bottom: 20px;
}

.search-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--line);
  border-radius: var(--radius-sm);
  background: white;
  font-size: var(--fs-body);
}

.search-input:focus {
  outline: none;
  border-color: var(--primary);
}

/* API Categories */
.api-category {
  margin-bottom: 16px;
}

.category-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  cursor: pointer;
  border-radius: var(--radius-sm);
  transition: var(--transition-fast);
}

.category-header:hover {
  background: var(--card-hover);
}

.category-name {
  flex: 1;
  font-weight: 600;
  color: var(--ink);
}

.category-count {
  font-size: var(--fs-small);
  color: var(--muted);
}

.category-endpoints {
  margin-left: 16px;
  margin-top: 8px;
}

.endpoint-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  cursor: pointer;
  border-radius: var(--radius-sm);
  transition: var(--transition-fast);
  margin-bottom: 4px;
}

.endpoint-item:hover {
  background: var(--card-hover);
}

.endpoint-item.active {
  background: color-mix(in oklab, var(--primary) 12%, white);
  border-left: 3px solid var(--primary);
}

.method-badge {
  padding: 2px 6px;
  border-radius: 4px;
  font-size: var(--fs-xs);
  font-weight: 600;
  text-transform: uppercase;
  min-width: 45px;
  text-align: center;
}

.method-badge.large {
  padding: 6px 12px;
  font-size: var(--fs-small);
  min-width: 60px;
}

.method-badge.get {
  background: color-mix(in oklab, var(--good) 20%, white);
  color: var(--good);
}

.method-badge.post {
  background: color-mix(in oklab, var(--primary) 20%, white);
  color: var(--primary);
}

.method-badge.put {
  background: color-mix(in oklab, var(--accent-yellow) 20%, white);
  color: #B45309;
}

.method-badge.delete {
  background: color-mix(in oklab, var(--bad) 20%, white);
  color: var(--bad);
}

.endpoint-path {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: var(--fs-small);
  color: var(--ink);
}

/* Main Content */
.api-main {
  background: var(--card);
  border: 1px solid var(--line);
  border-radius: var(--radius);
  box-shadow: var(--shadow-sm);
  overflow-y: auto;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  color: var(--muted);
  padding: 40px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

/* Endpoint Details */
.endpoint-details {
  padding: 24px;
}

.endpoint-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.endpoint-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.endpoint-meta {
  display: flex;
  gap: 12px;
  font-size: var(--fs-small);
  color: var(--muted);
}

.endpoint-category {
  background: var(--card-hover);
  padding: 4px 8px;
  border-radius: 6px;
}

.endpoint-version {
  background: color-mix(in oklab, var(--primary) 15%, white);
  color: var(--primary);
  padding: 4px 8px;
  border-radius: 6px;
}

.endpoint-description {
  color: var(--muted);
  margin-bottom: 32px;
  line-height: 1.5;
}

/* Request Section */
.request-section {
  margin-bottom: 32px;
}

.section-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 20px;
  border-bottom: 1px solid var(--line);
}

.tab-btn {
  padding: 10px 16px;
  border: none;
  background: none;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: var(--transition-fast);
  color: var(--muted);
}

.tab-btn.active {
  color: var(--primary);
  border-bottom-color: var(--primary);
}

.tab-content {
  min-height: 200px;
}

/* Parameters */
.param-groups {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.param-group h4 {
  margin: 0 0 12px 0;
  color: var(--ink);
  font-size: 16px;
}

.param-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.param-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--card-hover);
  border-radius: var(--radius-sm);
}

.param-info {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 200px;
}

.param-name {
  font-weight: 600;
  color: var(--ink);
}

.param-type {
  font-size: var(--fs-small);
  color: var(--muted);
  background: var(--line);
  padding: 2px 6px;
  border-radius: 4px;
}

.param-required {
  color: var(--bad);
  font-weight: 600;
}

.param-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid var(--line);
  border-radius: var(--radius-sm);
  background: white;
}

.no-params {
  text-align: center;
  color: var(--muted);
  padding: 40px;
}

/* Headers */
.header-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.header-item {
  display: flex;
  gap: 8px;
  align-items: center;
}

.header-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid var(--line);
  border-radius: var(--radius-sm);
  background: white;
}

.remove-btn {
  background: var(--bad);
  color: white;
  border: none;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-header-btn,
.add-field-btn {
  background: var(--primary);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: var(--transition-fast);
}

.add-header-btn:hover,
.add-field-btn:hover {
  background: var(--primary-light);
}

/* Body */
.body-type-selector {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.body-type-selector label {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}

.body-textarea {
  width: 100%;
  height: 200px;
  padding: 12px;
  border: 1px solid var(--line);
  border-radius: var(--radius-sm);
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  resize: vertical;
}

.form-data {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-field {
  display: flex;
  gap: 8px;
  align-items: center;
}

.form-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid var(--line);
  border-radius: var(--radius-sm);
  background: white;
}

.no-body {
  text-align: center;
  color: var(--muted);
  padding: 40px;
}

/* Request Actions */
.request-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 32px;
}

.send-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--primary);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: var(--transition-fast);
  font-weight: 600;
}

.send-btn:hover:not(:disabled) {
  background: var(--primary-light);
}

.send-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.save-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: white;
  color: var(--ink);
  border: 1px solid var(--line);
  padding: 12px 24px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: var(--transition-fast);
}

.save-btn:hover {
  background: var(--card-hover);
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

/* Response Section */
.response-section {
  border-top: 1px solid var(--line);
  padding-top: 24px;
}

.response-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.response-header h3 {
  margin: 0;
  color: var(--ink);
}

.response-meta {
  display: flex;
  gap: 12px;
  align-items: center;
}

.status-code {
  padding: 4px 8px;
  border-radius: 6px;
  font-weight: 600;
  font-size: var(--fs-small);
}

.status-code.success {
  background: color-mix(in oklab, var(--good) 20%, white);
  color: var(--good);
}

.status-code.client-error,
.status-code.server-error {
  background: color-mix(in oklab, var(--bad) 20%, white);
  color: var(--bad);
}

.response-time,
.response-size {
  font-size: var(--fs-small);
  color: var(--muted);
}

.response-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 16px;
  border-bottom: 1px solid var(--line);
}

.response-content {
  background: var(--card-hover);
  border-radius: var(--radius-sm);
  padding: 16px;
  max-height: 400px;
  overflow-y: auto;
}

.response-body pre {
  margin: 0;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  line-height: 1.4;
  color: var(--ink);
}

.response-headers {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.response-header-item {
  display: flex;
  gap: 8px;
}

.header-key {
  font-weight: 600;
  color: var(--ink);
  min-width: 200px;
}

.header-value {
  color: var(--muted);
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: var(--fs-small);
}

@media (max-width: 1024px) {
  .api-workspace {
    grid-template-columns: 1fr;
    grid-template-rows: auto 1fr;
  }
  
  .api-sidebar {
    max-height: 300px;
  }
}

@media (max-width: 768px) {
  .endpoint-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .param-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .param-info {
    min-width: auto;
  }
  
  .param-input {
    width: 100%;
  }
  
  .request-actions {
    flex-direction: column;
  }
}
</style>