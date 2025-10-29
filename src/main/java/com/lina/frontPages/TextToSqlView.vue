<template>
  <div class="text-to-sql-view">
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h1 class="page-title">Text To SQL</h1>
          <p class="page-subtitle">자연어로 SQL 쿼리를 생성하세요</p>
        </div>
        <div class="header-actions">
          <select v-model="selectedModel" class="model-select">
            <option value="gpt-4">GPT-4</option>
            <option value="claude-3">Claude-3</option>
            <option value="gemini-pro">Gemini Pro</option>
          </select>
          <button class="action-btn" @click="clearChat">
            <IconSystem name="trash" :size="16" />
            대화 초기화
          </button>
        </div>
      </div>
    </div>

    <div class="chat-workspace">
      <!-- Chat Messages -->
      <div class="chat-messages" ref="messagesContainer">
        <div v-if="chatMessages.length === 0" class="welcome-message">
          <div class="welcome-content">
            <IconSystem name="database" :size="48" />
            <h3>Text to SQL 어시스턴트</h3>
            <p>자연어로 질문하시면 SQL 쿼리를 생성해드립니다</p>
            <div class="example-questions">
              <h4>예시 질문:</h4>
              <div class="example-list">
                <button 
                  v-for="example in exampleQuestions" 
                  :key="example"
                  class="example-btn"
                  @click="sendMessage(example)"
                >
                  {{ example }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <div 
          v-for="message in chatMessages" 
          :key="message.id"
          class="message"
          :class="{ 'user-message': message.type === 'user', 'assistant-message': message.type === 'assistant' }"
        >
          <div class="message-avatar">
            <IconSystem v-if="message.type === 'user'" name="user" :size="20" />
            <IconSystem v-else name="database" :size="20" />
          </div>
          
          <div class="message-content">
            <div class="message-header">
              <span class="message-sender">{{ message.type === 'user' ? '사용자' : 'SQL Assistant' }}</span>
              <span class="message-time">{{ formatMessageTime(message.timestamp) }}</span>
            </div>
            
            <div class="message-body">
              <div v-if="message.type === 'user'" class="user-text">
                {{ message.content }}
              </div>
              
              <div v-else class="assistant-response">
                <div v-if="message.isGenerating" class="generating-indicator">
                  <div class="typing-dots">
                    <span></span>
                    <span></span>
                    <span></span>
                  </div>
                  <span>SQL을 생성하고 있습니다...</span>
                </div>
                
                <div v-if="message.isExecuting" class="executing-indicator">
                  <div class="typing-dots">
                    <span></span>
                    <span></span>
                    <span></span>
                  </div>
                  <span>쿼리를 실행하고 있습니다...</span>
                </div>
                
                <div v-else>
                  <div v-if="message.explanation" class="response-explanation">
                    {{ message.explanation }}
                  </div>
                  
                  <div v-if="message.sql" class="response-sql">
                    <div class="sql-header">
                      <span class="sql-label">생성된 SQL</span>
                      <div class="sql-actions">
                        <button class="sql-action-btn" @click="copySQL(message.sql)" title="복사">
                          <IconSystem name="copy" :size="14" />
                        </button>
                        <button class="sql-action-btn" @click="formatSQL(message)" title="포맷">
                          <IconSystem name="code" :size="14" />
                        </button>
                        <button 
                          class="sql-action-btn primary" 
                          @click="executeSQL(message.sql)" 
                          title="실행"
                          :disabled="isExecuting"
                        >
                          <IconSystem v-if="isExecuting" name="loader" :size="14" class="spinning" />
                          <IconSystem v-else name="play" :size="14" />
                        </button>
                      </div>
                    </div>
                    <pre class="sql-code">{{ message.sql }}</pre>
                  </div>
                  
                  <!-- Execution Results -->
                  <div v-if="message.executionResults" class="execution-results">
                    <div class="results-header">
                      <span class="results-label">실행 결과</span>
                      <div class="results-meta">
                        <span class="results-count">{{ message.rowCount }}행</span>
                        <span class="results-time">{{ message.executionTime }}ms</span>
                      </div>
                    </div>
                    
                    <div class="results-table-container">
                      <table class="results-table">
                        <thead>
                          <tr>
                            <th v-for="column in Object.keys(message.executionResults[0] || {})" :key="column">
                              {{ column }}
                            </th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr v-for="(row, index) in message.executionResults" :key="index">
                            <td v-for="column in Object.keys(row)" :key="column">
                              {{ row[column] }}
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                  </div>
                  
                  <div v-if="message.metadata" class="response-metadata">
                    <div class="metadata-item">
                      <span class="metadata-label">모델:</span>
                      <span class="metadata-value">{{ message.metadata.model }}</span>
                    </div>
                    <div class="metadata-item">
                      <span class="metadata-label">생성 시간:</span>
                      <span class="metadata-value">{{ message.metadata.generationTime }}ms</span>
                    </div>
                    <div class="metadata-item">
                      <span class="metadata-label">신뢰도:</span>
                      <span class="metadata-value confidence" :class="getConfidenceClass(message.metadata.confidence)">
                        {{ message.metadata.confidence }}%
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Chat Input -->
      <div class="chat-input-section">
        <div class="input-container">
          <div class="input-wrapper">
            <textarea 
              v-model="currentMessage"
              class="message-input"
              placeholder="SQL 쿼리에 대해 질문해보세요... (예: 2024년 매출이 높은 고객 TOP 10을 조회해줘)"
              rows="1"
              @keydown="handleKeydown"
              @input="adjustTextareaHeight"
              ref="messageInput"
            ></textarea>
            
            <div class="input-actions">
              <button class="input-action-btn" @click="loadExample" title="예시 불러오기">
                <IconSystem name="lightbulb" :size="16" />
              </button>
              <button 
                class="send-btn"
                :disabled="!currentMessage.trim() || isGenerating"
                @click="sendMessage()"
              >
                <IconSystem v-if="isGenerating" name="loader" :size="16" class="spinning" />
                <IconSystem v-else name="send" :size="16" />
              </button>
            </div>
          </div>
          
          <div class="input-footer">
            <div class="input-stats">
              <span class="char-count">{{ currentMessage.length }}/1000</span>
            </div>
            <div class="input-tips">
              <span class="tip">Shift + Enter로 줄바꿈, Enter로 전송</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import IconSystem from './IconSystem.vue'

// Reactive state
const selectedModel = ref('gpt-4')
const currentMessage = ref('')
const isGenerating = ref(false)
const chatMessages = ref([])
const messagesContainer = ref(null)
const messageInput = ref(null)
const isExecuting = ref(false)

let messageIdCounter = 1

// Sample data
const exampleQuestions = [
  "2024년 보험료 수입이 높은 고객 TOP 10을 조회해줘",
  "가장 많이 판매된 보험상품 순으로 보여줘", 
  "월별 신규 계약 건수 추이를 분석해줘",
  "고객별 보험금 청구 이력을 조회해줘"
]

// Methods
const sendMessage = async (message = null) => {
  const messageText = message || currentMessage.value.trim()
  if (!messageText || isGenerating.value) return
  
  // Add user message
  const userMessage = {
    id: messageIdCounter++,
    type: 'user',
    content: messageText,
    timestamp: new Date()
  }
  chatMessages.value.push(userMessage)
  
  // Clear input
  currentMessage.value = ''
  
  // Add generating assistant message
  const assistantMessage = {
    id: messageIdCounter++,
    type: 'assistant',
    isGenerating: true,
    timestamp: new Date()
  }
  chatMessages.value.push(assistantMessage)
  
  // Scroll to bottom
  await nextTick()
  scrollToBottom()
  
  isGenerating.value = true
  const startTime = Date.now()
  
  // Simulate AI processing
  setTimeout(() => {
    const endTime = Date.now()
    const generationTime = endTime - startTime
    
    // Mock generated SQL based on message
    const { sql, explanation } = generateMockSQL(messageText)
    
    // Update assistant message
    const messageIndex = chatMessages.value.findIndex(m => m.id === assistantMessage.id)
    if (messageIndex > -1) {
      chatMessages.value[messageIndex] = {
        ...assistantMessage,
        isGenerating: false,
        explanation,
        sql,
        metadata: {
          model: selectedModel.value,
          generationTime,
          confidence: Math.floor(Math.random() * 20) + 80
        }
      }
    }
    
    isGenerating.value = false
    
    // Scroll to bottom
    nextTick(() => scrollToBottom())
  }, 2000)
}

const generateMockSQL = (message) => {
  const lowerMessage = message.toLowerCase()
  
  if (lowerMessage.includes('top') || lowerMessage.includes('상위') || lowerMessage.includes('보험료')) {
    return {
      explanation: "보험료 수입이 높은 상위 고객들을 조회하는 쿼리를 생성했습니다. 고객별 총 보험료를 계산하여 내림차순으로 정렬합니다.",
      sql: `SELECT 
  c.customer_name,
  c.customer_id,
  SUM(p.premium_amount) as total_premium,
  COUNT(p.policy_id) as policy_count
FROM customers c
JOIN insurance_policies p ON c.customer_id = p.customer_id
WHERE p.start_date >= '2024-01-01'
  AND p.policy_status = 'ACTIVE'
GROUP BY c.customer_id, c.customer_name
ORDER BY total_premium DESC
LIMIT 10;`
    }
  } else if (lowerMessage.includes('상품') || lowerMessage.includes('보험상품') || lowerMessage.includes('판매')) {
    return {
      explanation: "가장 많이 판매된 보험상품들을 조회하는 쿼리입니다. 상품별 계약 건수와 총 보험료를 계산하여 내림차순으로 정렬합니다.",
      sql: `SELECT 
  pr.product_name,
  pr.product_type,
  COUNT(p.policy_id) as contract_count,
  SUM(p.premium_amount) as total_premium,
  AVG(p.premium_amount) as avg_premium
FROM products pr
JOIN insurance_policies p ON pr.product_id = p.product_id
WHERE p.start_date >= '2024-01-01'
GROUP BY pr.product_id, pr.product_name, pr.product_type
ORDER BY contract_count DESC;`
    }
  } else if (lowerMessage.includes('월별') || lowerMessage.includes('추이') || lowerMessage.includes('계약')) {
    return {
      explanation: "월별 신규 계약 건수 추이를 분석하는 쿼리입니다. 각 월의 신규 계약 건수와 전월 대비 증감률을 계산합니다.",
      sql: `SELECT 
  DATE_FORMAT(start_date, '%Y-%m') as contract_month,
  COUNT(*) as new_contracts,
  SUM(premium_amount) as monthly_premium,
  LAG(COUNT(*)) OVER (ORDER BY DATE_FORMAT(start_date, '%Y-%m')) as prev_month_contracts
FROM insurance_policies
WHERE start_date >= '2024-01-01'
  AND policy_status = 'ACTIVE'
GROUP BY DATE_FORMAT(start_date, '%Y-%m')
ORDER BY contract_month;`
    }
  } else if (lowerMessage.includes('청구') || lowerMessage.includes('보험금')) {
    return {
      explanation: "고객별 보험금 청구 이력을 조회하는 쿼리입니다. 청구 건수, 총 청구금액, 지급금액을 함께 표시합니다.",
      sql: `SELECT 
  c.customer_name,
  c.customer_id,
  COUNT(cl.claim_id) as claim_count,
  SUM(cl.claim_amount) as total_claim_amount,
  SUM(cl.settlement_amount) as total_settlement,
  AVG(cl.settlement_amount / cl.claim_amount) as settlement_ratio
FROM customers c
JOIN insurance_policies p ON c.customer_id = p.customer_id
JOIN claims cl ON p.policy_id = cl.policy_id
WHERE cl.claim_date >= '2024-01-01'
GROUP BY c.customer_id, c.customer_name
ORDER BY total_claim_amount DESC;`
    }
  } else {
    return {
      explanation: "요청하신 내용을 바탕으로 일반적인 보험 데이터 조회 쿼리를 생성했습니다.",
      sql: `SELECT 
  c.customer_name,
  p.policy_id,
  pr.product_name,
  p.premium_amount,
  p.start_date,
  p.policy_status
FROM customers c
JOIN insurance_policies p ON c.customer_id = p.customer_id
JOIN products pr ON p.product_id = pr.product_id
WHERE p.start_date >= '2024-01-01'
ORDER BY p.start_date DESC
LIMIT 100;`
    }
  }
}

const clearChat = () => {
  chatMessages.value = []
  currentMessage.value = ''
}

const loadExample = () => {
  const randomExample = exampleQuestions[Math.floor(Math.random() * exampleQuestions.length)]
  currentMessage.value = randomExample
}

const copySQL = async (sql) => {
  try {
    await navigator.clipboard.writeText(sql)
    // Show toast notification
  } catch (err) {
    console.error('Failed to copy SQL:', err)
  }
}

const formatSQL = (message) => {
  // Simple SQL formatting
  const formatted = message.sql
    .replace(/\s+/g, ' ')
    .replace(/SELECT/gi, 'SELECT')
    .replace(/FROM/gi, '\nFROM')
    .replace(/WHERE/gi, '\nWHERE')
    .replace(/JOIN/gi, '\nJOIN')
    .replace(/GROUP BY/gi, '\nGROUP BY')
    .replace(/ORDER BY/gi, '\nORDER BY')
    .replace(/LIMIT/gi, '\nLIMIT')
    .trim()
  
  message.sql = formatted
}

const executeSQL = async (sql) => {
  if (isExecuting.value) return
  
  isExecuting.value = true
  
  // Add execution message
  const executionMessage = {
    id: messageIdCounter++,
    type: 'assistant',
    isExecuting: true,
    timestamp: new Date()
  }
  chatMessages.value.push(executionMessage)
  
  // Scroll to bottom
  await nextTick()
  scrollToBottom()
  
  // Simulate SQL execution
  setTimeout(() => {
    // Generate mock results based on SQL
    const results = generateMockResults(sql)
    
    // Update execution message with results
    const messageIndex = chatMessages.value.findIndex(m => m.id === executionMessage.id)
    if (messageIndex > -1) {
      chatMessages.value[messageIndex] = {
        ...executionMessage,
        isExecuting: false,
        explanation: `쿼리가 성공적으로 실행되었습니다. ${results.length}개의 결과를 반환했습니다.`,
        executionResults: results,
        executionTime: Math.floor(Math.random() * 500) + 100,
        rowCount: results.length
      }
    }
    
    isExecuting.value = false
    
    // Scroll to bottom
    nextTick(() => scrollToBottom())
  }, 1500)
}

const generateMockResults = (sql) => {
  const lowerSQL = sql.toLowerCase()
  
  if (lowerSQL.includes('customer') || lowerSQL.includes('고객')) {
    return [
      { customer_name: '김철수', total_sales: '₩2,450,000', order_count: 12 },
      { customer_name: '이영희', total_sales: '₩1,890,000', order_count: 8 },
      { customer_name: '박민수', total_sales: '₩1,650,000', order_count: 6 },
      { customer_name: '정수진', total_sales: '₩1,420,000', order_count: 9 },
      { customer_name: '최동훈', total_sales: '₩1,280,000', order_count: 5 }
    ]
  } else if (lowerSQL.includes('product') || lowerSQL.includes('상품')) {
    return [
      { product_name: '생명보험 프리미엄', total_sold: 245, total_revenue: '₩12,250,000' },
      { product_name: '건강보험 플러스', total_sold: 189, total_revenue: '₩9,450,000' },
      { product_name: '자동차보험 종합', total_sold: 156, total_revenue: '₩7,800,000' },
      { product_name: '여행자보험', total_sold: 98, total_revenue: '₩2,940,000' }
    ]
  } else if (lowerSQL.includes('month') || lowerSQL.includes('월별')) {
    return [
      { month: '2024-01', monthly_sales: '₩15,600,000', order_count: 234 },
      { month: '2024-02', monthly_sales: '₩18,200,000', order_count: 267 },
      { month: '2024-03', monthly_sales: '₩21,800,000', order_count: 298 },
      { month: '2024-04', monthly_sales: '₩19,400,000', order_count: 276 },
      { month: '2024-05', monthly_sales: '₩23,100,000', order_count: 312 }
    ]
  } else {
    return [
      { id: 1, name: '샘플 데이터 1', value: '₩1,000,000', status: '활성' },
      { id: 2, name: '샘플 데이터 2', value: '₩850,000', status: '활성' },
      { id: 3, name: '샘플 데이터 3', value: '₩720,000', status: '비활성' }
    ]
  }
}

const handleKeydown = (event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    sendMessage()
  }
}

const adjustTextareaHeight = () => {
  const textarea = messageInput.value
  if (textarea) {
    textarea.style.height = 'auto'
    textarea.style.height = Math.min(textarea.scrollHeight, 120) + 'px'
  }
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const getConfidenceClass = (conf) => {
  if (conf >= 90) return 'high'
  if (conf >= 70) return 'medium'
  return 'low'
}

const formatMessageTime = (timestamp) => {
  return new Intl.DateTimeFormat('ko-KR', {
    hour: '2-digit',
    minute: '2-digit'
  }).format(timestamp)
}
</script>

<style scoped>
.text-to-sql-view {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding: var(--space-4);
  gap: var(--space-4);
}

.model-select {
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
  font-size: var(--fs-sm);
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-secondary);
}

.action-btn:hover {
  background: var(--surface-hover);
  color: var(--text-primary);
}

/* Chat Workspace */
.chat-workspace {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-4);
  background: var(--surface-hover);
}

/* Welcome Message */
.welcome-message {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 400px;
}

.welcome-content {
  text-align: center;
  max-width: 600px;
  padding: var(--space-8);
}

.welcome-content h3 {
  font-size: var(--fs-xl);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: var(--space-4) 0 var(--space-2) 0;
}

.welcome-content p {
  color: var(--text-secondary);
  margin-bottom: var(--space-6);
}

.example-questions h4 {
  font-size: var(--fs-base);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-3) 0;
}

.example-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.example-btn {
  padding: var(--space-3) var(--space-4);
  border: 1px solid var(--border-primary);
  background: var(--surface);
  border-radius: var(--radius-md);
  text-align: left;
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-secondary);
}

.example-btn:hover {
  background: var(--lina-yellow);
  color: var(--gray-800);
  border-color: var(--lina-yellow);
}

/* Messages */
.message {
  display: flex;
  gap: var(--space-3);
  margin-bottom: var(--space-4);
  max-width: 100%;
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  flex-shrink: 0;
}

.user-message .message-avatar {
  background: var(--lina-yellow);
  color: var(--gray-800);
}

.assistant-message .message-avatar {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  color: var(--text-secondary);
}

.message-content {
  flex: 1;
  min-width: 0;
  max-width: 70%;
}

.user-message .message-content {
  align-self: flex-end;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-2);
}

.message-sender {
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
}

.message-time {
  font-size: var(--fs-xs);
  color: var(--text-tertiary);
}

.message-body {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
}

.user-message .message-body {
  background: var(--lina-yellow);
  color: var(--gray-800);
  border-color: var(--lina-yellow);
}

.user-text {
  line-height: var(--lh-relaxed);
}

/* Assistant Response */
.generating-indicator,
.executing-indicator {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  color: var(--text-secondary);
}

.typing-dots {
  display: flex;
  gap: 4px;
}

.typing-dots span {
  width: 6px;
  height: 6px;
  background: var(--text-tertiary);
  border-radius: 50%;
  animation: typing 1.4s ease-in-out infinite both;
}

.typing-dots span:nth-child(1) { animation-delay: -0.32s; }
.typing-dots span:nth-child(2) { animation-delay: -0.16s; }
.typing-dots span:nth-child(3) { animation-delay: 0s; }

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.response-explanation {
  margin-bottom: var(--space-4);
  line-height: var(--lh-relaxed);
  color: var(--text-secondary);
}

.response-sql {
  margin-bottom: var(--space-4);
}

.sql-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-2);
}

.sql-label {
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
}

.sql-actions {
  display: flex;
  gap: var(--space-1);
}

.sql-action-btn {
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

.sql-action-btn:hover {
  background: var(--surface-hover);
  color: var(--text-primary);
}

.sql-action-btn.primary {
  background: var(--lina-yellow);
  color: var(--gray-800);
  border-color: var(--lina-yellow);
}

.sql-code {
  background: var(--surface-hover);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  padding: var(--space-3);
  font-family: 'JetBrains Mono', monospace;
  font-size: var(--fs-sm);
  line-height: var(--lh-relaxed);
  color: var(--text-primary);
  overflow-x: auto;
  white-space: pre-wrap;
  margin: 0;
}

.response-metadata {
  display: flex;
  gap: var(--space-4);
  padding: var(--space-3);
  background: var(--surface-hover);
  border-radius: var(--radius-md);
  font-size: var(--fs-sm);
}

.metadata-item {
  display: flex;
  gap: var(--space-2);
}

.metadata-label {
  color: var(--text-secondary);
}

.metadata-value {
  color: var(--text-primary);
  font-weight: var(--fw-medium);
}

.confidence.high {
  color: var(--success);
}

.confidence.medium {
  color: var(--lina-yellow);
}

.confidence.low {
  color: var(--error);
}

/* Execution Results */
.execution-results {
  margin-top: var(--space-4);
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-2);
}

.results-label {
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
}

.results-meta {
  display: flex;
  gap: var(--space-3);
  font-size: var(--fs-xs);
  color: var(--text-secondary);
}

.results-count {
  color: var(--lina-yellow-dark);
  font-weight: var(--fw-semibold);
}

.results-table-container {
  max-height: 300px;
  overflow: auto;
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
}

.results-table {
  width: 100%;
  border-collapse: collapse;
  font-size: var(--fs-sm);
  font-family: 'JetBrains Mono', monospace;
}

.results-table th {
  background: var(--surface-hover);
  padding: var(--space-2);
  text-align: left;
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  border-bottom: 1px solid var(--border-primary);
  position: sticky;
  top: 0;
}

.results-table td {
  padding: var(--space-2);
  border-bottom: 1px solid var(--border-primary);
  color: var(--text-secondary);
}

.results-table tr:hover {
  background: var(--surface-hover);
}

/* Chat Input */
.chat-input-section {
  border-top: 1px solid var(--border-primary);
  background: var(--surface);
  padding: var(--space-4);
  border-radius: 0 0 var(--radius-lg) var(--radius-lg);
  flex-shrink: 0;
}

.input-container {
  max-width: 100%;
}

.input-wrapper {
  display: flex;
  gap: var(--space-2);
  align-items: flex-end;
}

.message-input {
  flex: 1;
  min-height: 44px;
  max-height: 120px;
  padding: var(--space-3);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  font-size: var(--fs-base);
  line-height: var(--lh-relaxed);
  resize: none;
  background: var(--surface);
  color: var(--text-primary);
  font-family: inherit;
}

.message-input:focus {
  outline: none;
  border-color: var(--lina-yellow);
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--lina-yellow) 20%, transparent);
}

.input-actions {
  display: flex;
  gap: var(--space-2);
  align-items: flex-end;
}

.input-action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border: 1px solid var(--border-primary);
  background: var(--surface);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-secondary);
}

.input-action-btn:hover {
  background: var(--surface-hover);
  color: var(--text-primary);
}

.send-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  background: var(--lina-yellow);
  color: white;
  border: none;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: var(--transition-fast);
}

.send-btn:hover:not(:disabled) {
  background: var(--lina-yellow);
  transform: translateY(-1px);
}

.send-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.spinning {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--space-2);
}

.input-stats {
  font-size: var(--fs-sm);
  color: var(--text-tertiary);
}

.input-tips {
  font-size: var(--fs-sm);
  color: var(--text-tertiary);
}

/* Responsive */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .message-content {
    max-width: 85%;
  }
  
  .response-metadata {
    flex-direction: column;
    gap: var(--space-2);
  }
  
  .input-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-2);
  }
}
</style>