<template>
  <div class="stt-search-view">
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h1 class="page-title">STT 키워드 검색</h1>
          <p class="page-subtitle">상담 녹취 내용에서 키워드를 검색하여 관련 상담을 찾아보세요</p>
        </div>
        <div class="header-actions">
          <button class="action-btn" @click="clearAllFilters">
            <IconSystem name="refresh-cw" :size="16" />
            초기화
          </button>
        </div>
      </div>
    </div>

    <!-- Search Filters -->
    <div class="search-filters-section">
      <div class="section-header">
        <h3 class="section-title">검색 조건</h3>
        <button class="toggle-btn" @click="showFilters = !showFilters">
          <IconSystem :name="showFilters ? 'chevron-up' : 'chevron-down'" :size="16" />
        </button>
      </div>
      
      <Transition name="slide-down">
        <div v-show="showFilters" class="filters-content">
          <div class="filters-grid">
            <!-- Date Range -->
            <div class="filter-group">
              <label class="filter-label">상담 일자</label>
              <div class="date-range">
                <input 
                  v-model="searchFilters.startDate"
                  type="date" 
                  class="date-input"
                />
                <span class="date-separator">~</span>
                <input 
                  v-model="searchFilters.endDate"
                  type="date" 
                  class="date-input"
                />
              </div>
            </div>

            <!-- Consultant ID -->
            <div class="filter-group">
              <label class="filter-label">상담사 번호</label>
              <input 
                v-model="searchFilters.consultantId"
                type="text" 
                placeholder="상담사 번호 입력"
                class="filter-input"
              />
            </div>

            <!-- Contract Number -->
            <div class="filter-group">
              <label class="filter-label">계약 번호</label>
              <input 
                v-model="searchFilters.contractNumber"
                type="text" 
                placeholder="계약 번호 입력"
                class="filter-input"
              />
            </div>

            <!-- Customer ID -->
            <div class="filter-group">
              <label class="filter-label">고객 ID</label>
              <input 
                v-model="searchFilters.customerId"
                type="text" 
                placeholder="고객 ID 입력"
                class="filter-input"
              />
            </div>

            <!-- Call Type -->
            <div class="filter-group">
              <label class="filter-label">상담 유형</label>
              <select v-model="searchFilters.callType" class="filter-select">
                <option value="">전체</option>
                <option value="inbound">인바운드</option>
                <option value="outbound">아웃바운드</option>
                <option value="claim">보험금청구</option>
                <option value="consultation">상담</option>
                <option value="complaint">불만처리</option>
              </select>
            </div>

            <!-- Duration -->
            <div class="filter-group">
              <label class="filter-label">통화 시간</label>
              <div class="duration-range">
                <input 
                  v-model="searchFilters.minDuration"
                  type="number" 
                  placeholder="최소(분)"
                  class="duration-input"
                />
                <span class="duration-separator">~</span>
                <input 
                  v-model="searchFilters.maxDuration"
                  type="number" 
                  placeholder="최대(분)"
                  class="duration-input"
                />
              </div>
            </div>
          </div>
        </div>
      </Transition>
    </div>

    <!-- Keyword Search -->
    <div class="keyword-search-section">
      <div class="section-header">
        <h3 class="section-title">키워드 검색</h3>
        <div class="search-stats" v-if="searchResults.length">
          <span class="results-count">{{ searchResults.length }}건</span>
          <span class="search-time">{{ searchTime }}ms</span>
        </div>
      </div>
      
      <div class="search-container">
        <div class="search-input-wrapper">
          <IconSystem name="search" :size="20" />
          <input 
            v-model="keywordQuery"
            type="text" 
            placeholder="검색할 키워드를 입력하세요 (예: 보험금, 청구, 해지 등)"
            class="keyword-input"
            @keydown.enter="performSearch"
          />
          <button 
            class="search-btn"
            :disabled="!keywordQuery.trim() || isSearching"
            @click="performSearch"
          >
            <IconSystem v-if="isSearching" name="loader" :size="16" class="spinning" />
            <IconSystem v-else name="search" :size="16" />
            {{ isSearching ? '검색 중...' : '검색' }}
          </button>
        </div>
        
        <div class="search-options">
          <label class="search-option">
            <input type="checkbox" v-model="searchOptions.exactMatch" />
            <span>정확히 일치</span>
          </label>
          <label class="search-option">
            <input type="checkbox" v-model="searchOptions.caseSensitive" />
            <span>대소문자 구분</span>
          </label>
          <label class="search-option">
            <input type="checkbox" v-model="searchOptions.includeContext" />
            <span>문맥 포함</span>
          </label>
        </div>
      </div>
    </div>

    <!-- Search Results -->
    <div v-if="searchResults.length" class="results-section">
      <div class="section-header">
        <h3 class="section-title">검색 결과</h3>
        <div class="results-actions">
          <select v-model="sortBy" class="sort-select">
            <option value="relevance">관련도순</option>
            <option value="date">날짜순</option>
            <option value="duration">통화시간순</option>
            <option value="consultant">상담사순</option>
          </select>
          <button class="action-btn" @click="exportResults">
            <IconSystem name="download" :size="16" />
            내보내기
          </button>
        </div>
      </div>
      
      <div class="results-list">
        <div 
          v-for="result in sortedResults" 
          :key="result.id"
          class="result-item"
          @click="selectResult(result)"
          :class="{ selected: selectedResult?.id === result.id }"
        >
          <div class="result-header">
            <div class="result-meta">
              <div class="meta-item">
                <IconSystem name="calendar" :size="14" />
                <span>{{ formatDate(result.callDate) }}</span>
              </div>
              <div class="meta-item">
                <IconSystem name="user" :size="14" />
                <span>상담사 {{ result.consultantId }}</span>
              </div>
              <div class="meta-item">
                <IconSystem name="clock" :size="14" />
                <span>{{ formatDuration(result.duration) }}</span>
              </div>
              <div class="meta-item">
                <IconSystem name="phone" :size="14" />
                <span>{{ result.callType }}</span>
              </div>
            </div>
            <div class="result-score">
              <span class="score-label">일치도</span>
              <span class="score-value">{{ result.relevanceScore }}%</span>
            </div>
          </div>
          
          <div class="result-content">
            <div class="result-info">
              <div class="info-item">
                <span class="info-label">계약번호:</span>
                <span class="info-value">{{ result.contractNumber || 'N/A' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">고객ID:</span>
                <span class="info-value">{{ result.customerId || 'N/A' }}</span>
              </div>
            </div>
            
            <div class="result-highlights">
              <div class="highlight-title">매칭된 내용:</div>
              <div class="highlight-text" v-html="result.highlightedText"></div>
            </div>
          </div>
          
          <div class="result-actions">
            <button class="result-action-btn" @click.stop="playRecording(result)">
              <IconSystem name="play" :size="16" />
              재생
            </button>
            <button class="result-action-btn" @click.stop="viewFullTranscript(result)">
              <IconSystem name="file-text" :size="16" />
              전체 내용
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Audio Player Modal -->
    <div v-if="showAudioPlayer" class="modal-overlay" @click="closeAudioPlayer">
      <div class="modal-content audio-modal" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">녹취 재생</h3>
          <button class="modal-close" @click="closeAudioPlayer">
            <IconSystem name="x" :size="20" />
          </button>
        </div>
        
        <div class="modal-body">
          <div class="audio-info">
            <div class="audio-meta">
              <div class="meta-row">
                <span class="meta-label">상담일시:</span>
                <span class="meta-value">{{ formatDateTime(selectedResult?.callDate) }}</span>
              </div>
              <div class="meta-row">
                <span class="meta-label">상담사:</span>
                <span class="meta-value">{{ selectedResult?.consultantId }}</span>
              </div>
              <div class="meta-row">
                <span class="meta-label">통화시간:</span>
                <span class="meta-value">{{ formatDuration(selectedResult?.duration) }}</span>
              </div>
            </div>
          </div>
          
          <div class="audio-player">
            <audio 
              ref="audioElement"
              :src="selectedResult?.audioUrl"
              @loadedmetadata="updateAudioDuration"
              @timeupdate="updateProgress"
              @ended="audioEnded"
              controls
            ></audio>
            
            <div class="player-controls">
              <button class="player-btn" @click="togglePlay">
                <IconSystem :name="isPlaying ? 'pause' : 'play'" :size="20" />
              </button>
              
              <div class="progress-container">
                <div class="progress-bar" @click="seekAudio">
                  <div class="progress-fill" :style="{ width: progressPercentage + '%' }"></div>
                </div>
                <div class="time-display">
                  <span>{{ formatTime(currentTime) }}</span>
                  <span>{{ formatTime(audioDuration) }}</span>
                </div>
              </div>
              
              <div class="volume-control">
                <IconSystem name="volume-2" :size="16" />
                <input 
                  type="range" 
                  min="0" 
                  max="1" 
                  step="0.1" 
                  v-model="volume"
                  @input="updateVolume"
                  class="volume-slider"
                />
              </div>
            </div>
          </div>
          
          <div class="transcript-section">
            <h4>녹취 내용</h4>
            <div class="transcript-content">
              <div 
                v-for="segment in selectedResult?.transcript" 
                :key="segment.id"
                class="transcript-segment"
                :class="{ active: currentSegment === segment.id }"
                @click="seekToSegment(segment.startTime)"
              >
                <div class="segment-time">{{ formatTime(segment.startTime) }}</div>
                <div class="segment-speaker">{{ segment.speaker }}</div>
                <div class="segment-text">{{ segment.text }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Full Transcript Modal -->
    <div v-if="showTranscriptModal" class="modal-overlay" @click="closeTranscriptModal">
      <div class="modal-content transcript-modal" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">전체 녹취 내용</h3>
          <button class="modal-close" @click="closeTranscriptModal">
            <IconSystem name="x" :size="20" />
          </button>
        </div>
        
        <div class="modal-body">
          <div class="transcript-full">
            <div 
              v-for="segment in selectedResult?.transcript" 
              :key="segment.id"
              class="transcript-line"
            >
              <div class="line-header">
                <span class="line-time">{{ formatTime(segment.startTime) }}</span>
                <span class="line-speaker">{{ segment.speaker }}</span>
              </div>
              <div class="line-text">{{ segment.text }}</div>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeTranscriptModal">닫기</button>
          <button class="btn-primary" @click="copyTranscript">복사</button>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="!searchResults.length && !isSearching && hasSearched" class="empty-state">
      <IconSystem name="search" :size="48" />
      <h3>검색 결과가 없습니다</h3>
      <p>다른 키워드로 검색해보시거나 검색 조건을 조정해보세요</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import IconSystem from './IconSystem.vue'

// Reactive state
const showFilters = ref(true)
const keywordQuery = ref('')
const isSearching = ref(false)
const hasSearched = ref(false)
const searchResults = ref([])
const selectedResult = ref(null)
const showAudioPlayer = ref(false)
const showTranscriptModal = ref(false)
const searchTime = ref(0)
const sortBy = ref('relevance')

// Audio player state
const isPlaying = ref(false)
const currentTime = ref(0)
const audioDuration = ref(0)
const volume = ref(1)
const currentSegment = ref(null)
const audioElement = ref(null)

// Search filters
const searchFilters = ref({
  startDate: '',
  endDate: '',
  consultantId: '',
  contractNumber: '',
  customerId: '',
  callType: '',
  minDuration: '',
  maxDuration: ''
})

// Search options
const searchOptions = ref({
  exactMatch: false,
  caseSensitive: false,
  includeContext: true
})

// Sample data
const sampleResults = [
  {
    id: 1,
    callDate: '2024-10-01T09:30:00',
    consultantId: 'CS001',
    contractNumber: 'INS-2024-001234',
    customerId: 'CUST-789012',
    callType: '보험금청구',
    duration: 1245, // seconds
    relevanceScore: 95,
    highlightedText: '고객님의 <mark>보험금 청구</mark> 건에 대해 안내드리겠습니다. <mark>청구</mark> 서류를 확인한 결과...',
    audioUrl: '/sample-audio-1.mp3',
    transcript: [
      {
        id: 1,
        startTime: 0,
        endTime: 5.2,
        speaker: '상담사',
        text: '안녕하세요. 고객님의 보험금 청구 건에 대해 안내드리겠습니다.'
      },
      {
        id: 2,
        startTime: 5.2,
        endTime: 12.8,
        speaker: '고객',
        text: '네, 지난주에 청구 서류를 제출했는데 처리 현황이 궁금합니다.'
      },
      {
        id: 3,
        startTime: 12.8,
        endTime: 20.5,
        speaker: '상담사',
        text: '청구 서류를 확인한 결과 추가 서류가 필요한 상황입니다.'
      }
    ]
  },
  {
    id: 2,
    callDate: '2024-09-28T14:15:00',
    consultantId: 'CS002',
    contractNumber: 'INS-2024-001189',
    customerId: 'CUST-456789',
    callType: '상담',
    duration: 892,
    relevanceScore: 88,
    highlightedText: '계약 <mark>해지</mark>에 대한 문의를 주셨는데, <mark>해지</mark> 시 발생하는 수수료에 대해 설명드리겠습니다.',
    audioUrl: '/sample-audio-2.mp3',
    transcript: [
      {
        id: 1,
        startTime: 0,
        endTime: 4.8,
        speaker: '상담사',
        text: '안녕하세요. 무엇을 도와드릴까요?'
      },
      {
        id: 2,
        startTime: 4.8,
        endTime: 11.2,
        speaker: '고객',
        text: '보험 계약 해지를 생각하고 있는데 절차가 어떻게 되나요?'
      }
    ]
  },
  {
    id: 3,
    callDate: '2024-09-25T11:45:00',
    consultantId: 'CS003',
    contractNumber: null,
    customerId: 'CUST-123456',
    callType: '불만처리',
    duration: 1567,
    relevanceScore: 82,
    highlightedText: '고객님의 <mark>불만</mark> 사항에 대해 진심으로 사과드립니다. <mark>처리</mark> 과정에서 문제가 있었던 것 같습니다.',
    audioUrl: '/sample-audio-3.mp3',
    transcript: [
      {
        id: 1,
        startTime: 0,
        endTime: 6.5,
        speaker: '상담사',
        text: '고객님의 불만 사항에 대해 진심으로 사과드립니다.'
      }
    ]
  }
]

// Computed
const sortedResults = computed(() => {
  const results = [...searchResults.value]
  
  switch (sortBy.value) {
    case 'date':
      return results.sort((a, b) => new Date(b.callDate) - new Date(a.callDate))
    case 'duration':
      return results.sort((a, b) => b.duration - a.duration)
    case 'consultant':
      return results.sort((a, b) => a.consultantId.localeCompare(b.consultantId))
    case 'relevance':
    default:
      return results.sort((a, b) => b.relevanceScore - a.relevanceScore)
  }
})

const progressPercentage = computed(() => {
  if (!audioDuration.value) return 0
  return (currentTime.value / audioDuration.value) * 100
})

// Methods
const performSearch = async () => {
  if (!keywordQuery.value.trim()) return
  
  isSearching.value = true
  hasSearched.value = true
  const startTime = Date.now()
  
  // Simulate Elasticsearch search
  setTimeout(() => {
    // Filter sample results based on keyword and filters
    let results = sampleResults.filter(result => {
      // Keyword matching - search in both highlighted text and transcript
      const cleanText = result.highlightedText.replace(/<[^>]*>/g, '').toLowerCase()
      const transcriptText = result.transcript.map(t => t.text).join(' ').toLowerCase()
      const keywordMatch = cleanText.includes(keywordQuery.value.toLowerCase()) || 
                          transcriptText.includes(keywordQuery.value.toLowerCase())
      
      // Apply filters
      let passesFilters = true
      
      if (searchFilters.value.startDate) {
        passesFilters = passesFilters && new Date(result.callDate) >= new Date(searchFilters.value.startDate)
      }
      
      if (searchFilters.value.endDate) {
        passesFilters = passesFilters && new Date(result.callDate) <= new Date(searchFilters.value.endDate)
      }
      
      if (searchFilters.value.consultantId) {
        passesFilters = passesFilters && result.consultantId.includes(searchFilters.value.consultantId)
      }
      
      if (searchFilters.value.contractNumber) {
        passesFilters = passesFilters && result.contractNumber?.includes(searchFilters.value.contractNumber)
      }
      
      if (searchFilters.value.customerId) {
        passesFilters = passesFilters && result.customerId.includes(searchFilters.value.customerId)
      }
      
      if (searchFilters.value.callType) {
        passesFilters = passesFilters && result.callType === searchFilters.value.callType
      }
      
      return keywordMatch && passesFilters
    })
    
    console.log('Search keyword:', keywordQuery.value)
    console.log('Filtered results:', results)
    console.log('Sample data check:', sampleResults.map(r => ({
      id: r.id,
      cleanText: r.highlightedText.replace(/<[^>]*>/g, ''),
      transcriptText: r.transcript.map(t => t.text).join(' ')
    })))
    
    searchResults.value = results
    searchTime.value = Date.now() - startTime
    isSearching.value = false
  }, 1000)
}

const clearAllFilters = () => {
  keywordQuery.value = ''
  searchFilters.value = {
    startDate: '',
    endDate: '',
    consultantId: '',
    contractNumber: '',
    customerId: '',
    callType: '',
    minDuration: '',
    maxDuration: ''
  }
  searchOptions.value = {
    exactMatch: false,
    caseSensitive: false,
    includeContext: true
  }
  searchResults.value = []
  hasSearched.value = false
}

const selectResult = (result) => {
  selectedResult.value = result
}

const playRecording = (result) => {
  selectedResult.value = result
  showAudioPlayer.value = true
}

const viewFullTranscript = (result) => {
  selectedResult.value = result
  showTranscriptModal.value = true
}

const closeAudioPlayer = () => {
  showAudioPlayer.value = false
  if (isPlaying.value) {
    audioElement.value?.pause()
    isPlaying.value = false
  }
}

const closeTranscriptModal = () => {
  showTranscriptModal.value = false
}

const togglePlay = () => {
  if (audioElement.value) {
    if (isPlaying.value) {
      audioElement.value.pause()
    } else {
      audioElement.value.play()
    }
    isPlaying.value = !isPlaying.value
  }
}

const updateAudioDuration = () => {
  if (audioElement.value) {
    audioDuration.value = audioElement.value.duration
  }
}

const updateProgress = () => {
  if (audioElement.value) {
    currentTime.value = audioElement.value.currentTime
    
    // Update current segment
    const current = selectedResult.value?.transcript.find(segment => 
      currentTime.value >= segment.startTime && currentTime.value <= segment.endTime
    )
    currentSegment.value = current?.id || null
  }
}

const audioEnded = () => {
  isPlaying.value = false
  currentTime.value = 0
}

const seekAudio = (event) => {
  if (audioElement.value && audioDuration.value) {
    const rect = event.target.getBoundingClientRect()
    const percent = (event.clientX - rect.left) / rect.width
    const newTime = percent * audioDuration.value
    audioElement.value.currentTime = newTime
    currentTime.value = newTime
  }
}

const seekToSegment = (time) => {
  if (audioElement.value) {
    audioElement.value.currentTime = time
    currentTime.value = time
    if (!isPlaying.value) {
      togglePlay()
    }
  }
}

const updateVolume = () => {
  if (audioElement.value) {
    audioElement.value.volume = volume.value
  }
}

const exportResults = () => {
  // Export search results to CSV or Excel
  console.log('Exporting results...')
}

const copyTranscript = async () => {
  const fullText = selectedResult.value?.transcript
    .map(segment => `[${formatTime(segment.startTime)}] ${segment.speaker}: ${segment.text}`)
    .join('\n')
  
  try {
    await navigator.clipboard.writeText(fullText)
    // Show toast notification
  } catch (err) {
    console.error('Failed to copy transcript:', err)
  }
}

// Utility functions
const formatDate = (dateString) => {
  return new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  }).format(new Date(dateString))
}

const formatDateTime = (dateString) => {
  return new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(new Date(dateString))
}

const formatDuration = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}분 ${secs}초`
}

const formatTime = (seconds) => {
  if (!seconds) return '0:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins}:${secs.toString().padStart(2, '0')}`
}
</script>

<style scoped>
.stt-search-view {
  display: flex;
  flex-direction: column;
  padding: var(--space-4);
  gap: var(--space-4);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-6);
}

.header-content {
  flex: 1;
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

/* Section Styles */
.search-filters-section,
.keyword-search-section,
.results-section {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4) var(--space-5);
  border-bottom: 1px solid var(--border-primary);
  background: var(--surface-hover);
}

.section-title {
  font-size: var(--fs-lg);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0;
}

.toggle-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: 1px solid var(--border-primary);
  background: var(--surface);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-secondary);
}

.toggle-btn:hover {
  background: var(--surface-hover);
  color: var(--text-primary);
}

/* Search Filters */
.filters-content {
  padding: var(--space-5);
}

.filters-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--space-4);
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.filter-label {
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
}

.filter-input,
.filter-select,
.date-input,
.duration-input {
  padding: var(--space-2) var(--space-3);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  font-size: var(--fs-sm);
  background: var(--surface);
  color: var(--text-primary);
}

.date-input {
  width: 130px;
  font-size: var(--fs-xs);
  padding: var(--space-1) var(--space-2);
}

.date-input::-webkit-calendar-picker-indicator {
  cursor: pointer;
}

.date-input::-webkit-datetime-edit-text {
  color: transparent;
}

.date-input::-webkit-datetime-edit-month-field,
.date-input::-webkit-datetime-edit-day-field,
.date-input::-webkit-datetime-edit-year-field {
  color: var(--text-primary);
}

.date-input:focus::-webkit-datetime-edit-text {
  color: var(--text-secondary);
}

.filter-input:focus,
.filter-select:focus,
.date-input:focus,
.duration-input:focus {
  outline: none;
  border-color: var(--lina-orange);
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--lina-orange) 20%, transparent);
}

.date-range,
.duration-range {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.date-separator,
.duration-separator {
  color: var(--text-secondary);
}

.duration-input {
  flex: 1;
}

/* Keyword Search */
.search-container {
  padding: var(--space-5);
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  background: var(--surface);
  margin-bottom: var(--space-4);
}

.search-input-wrapper:focus-within {
  border-color: var(--lina-orange);
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--lina-orange) 20%, transparent);
}

.keyword-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: var(--fs-base);
  color: var(--text-primary);
}

.search-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-4);
  background: var(--lina-yellow);
  color: var(--gray-800);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
  cursor: pointer;
  transition: var(--transition-fast);
}

.search-btn:hover:not(:disabled) {
  background: var(--lina-yellow-light);
  color: var(--gray-800);
}

.search-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.spinning {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.search-options {
  display: flex;
  gap: var(--space-4);
}

.search-option {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--fs-sm);
  color: var(--text-secondary);
  cursor: pointer;
}

.search-stats {
  display: flex;
  gap: var(--space-3);
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}

.results-count {
  color: var(--text-primary);
  font-weight: var(--fw-semibold);
}

.results-actions {
  display: flex;
  gap: var(--space-2);
  align-items: center;
}

.sort-select {
  padding: var(--space-2) var(--space-3);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  background: var(--surface);
  font-size: var(--fs-sm);
  cursor: pointer;
}

/* Results */
.results-list {
  padding: var(--space-5);
}

.result-item {
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  margin-bottom: var(--space-4);
  cursor: pointer;
  transition: var(--transition-fast);
  background: var(--surface);
}

.result-item:hover {
  border-color: var(--lina-orange);
  box-shadow: var(--shadow-md);
}

.result-item.selected {
  border-color: var(--lina-orange);
  background: color-mix(in srgb, var(--lina-orange) 5%, transparent);
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-3);
}

.result-meta {
  display: flex;
  gap: var(--space-4);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}

.result-score {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.score-label {
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}

.score-value {
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
  color: var(--lina-orange);
}

.result-content {
  margin-bottom: var(--space-3);
}

.result-info {
  display: flex;
  gap: var(--space-4);
  margin-bottom: var(--space-3);
}

.info-item {
  display: flex;
  gap: var(--space-2);
  font-size: var(--fs-sm);
}

.info-label {
  color: var(--text-secondary);
}

.info-value {
  color: var(--text-primary);
  font-weight: var(--fw-medium);
}

.result-highlights {
  background: var(--surface-hover);
  padding: var(--space-3);
  border-radius: var(--radius-md);
}

.highlight-title {
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.highlight-text {
  line-height: var(--lh-relaxed);
  color: var(--text-secondary);
}

.highlight-text mark {
  background: var(--lina-yellow);
  color: var(--text-primary);
  padding: 2px 4px;
  border-radius: var(--radius-sm);
}

.result-actions {
  display: flex;
  gap: var(--space-2);
}

.result-action-btn {
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

.result-action-btn:hover {
  background: var(--lina-orange);
  color: white;
  border-color: var(--lina-orange);
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--space-4);
}

.modal-content {
  background: var(--surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-xl);
  max-width: 800px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
}

.audio-modal {
  max-width: 900px;
}

.transcript-modal {
  max-width: 700px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6);
  border-bottom: 1px solid var(--border-primary);
}

.modal-title {
  font-size: var(--fs-xl);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0;
}

.modal-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  background: var(--surface-hover);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-secondary);
}

.modal-close:hover {
  background: var(--surface-active);
  color: var(--text-primary);
}

.modal-body {
  padding: var(--space-6);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
  padding: var(--space-6);
  border-top: 1px solid var(--border-primary);
}

.btn-primary,
.btn-secondary {
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-md);
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
  cursor: pointer;
  transition: var(--transition-fast);
  border: 1px solid;
}

.btn-primary {
  background: var(--lina-orange);
  color: white;
  border-color: var(--lina-orange);
}

.btn-primary:hover {
  background: var(--lina-yellow);
  border-color: var(--lina-yellow);
}

.btn-secondary {
  background: var(--surface);
  color: var(--text-secondary);
  border-color: var(--border-primary);
}

.btn-secondary:hover {
  background: var(--surface-hover);
  color: var(--text-primary);
}

/* Audio Player */
.audio-info {
  margin-bottom: var(--space-4);
}

.audio-meta {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  padding: var(--space-3);
  background: var(--surface-hover);
  border-radius: var(--radius-md);
}

.meta-row {
  display: flex;
  gap: var(--space-2);
  font-size: var(--fs-sm);
}

.meta-label {
  color: var(--text-secondary);
  min-width: 80px;
}

.meta-value {
  color: var(--text-primary);
  font-weight: var(--fw-medium);
}

.audio-player {
  margin-bottom: var(--space-4);
}

.player-controls {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  margin-top: var(--space-3);
}

.player-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: 1px solid var(--border-primary);
  background: var(--surface);
  border-radius: 50%;
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-secondary);
}

.player-btn:hover {
  background: var(--lina-orange);
  color: white;
  border-color: var(--lina-orange);
}

.progress-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.progress-bar {
  height: 6px;
  background: var(--surface-hover);
  border-radius: var(--radius-full);
  cursor: pointer;
  position: relative;
}

.progress-fill {
  height: 100%;
  background: var(--lina-orange);
  border-radius: var(--radius-full);
  transition: width 0.1s ease;
}

.time-display {
  display: flex;
  justify-content: space-between;
  font-size: var(--fs-sm);
  color: var(--text-secondary);
}

.volume-control {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.volume-slider {
  width: 80px;
}

/* Transcript */
.transcript-section h4 {
  font-size: var(--fs-base);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-3) 0;
}

.transcript-content {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
}

.transcript-segment {
  display: flex;
  gap: var(--space-3);
  padding: var(--space-3);
  border-bottom: 1px solid var(--border-primary);
  cursor: pointer;
  transition: var(--transition-fast);
}

.transcript-segment:hover {
  background: var(--surface-hover);
}

.transcript-segment.active {
  background: color-mix(in srgb, var(--lina-orange) 10%, transparent);
}

.transcript-segment:last-child {
  border-bottom: none;
}

.segment-time {
  font-size: var(--fs-xs);
  color: var(--text-tertiary);
  min-width: 50px;
}

.segment-speaker {
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  min-width: 60px;
}

.segment-text {
  flex: 1;
  font-size: var(--fs-sm);
  color: var(--text-secondary);
  line-height: var(--lh-relaxed);
}

.transcript-full {
  max-height: 500px;
  overflow-y: auto;
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
}

.transcript-line {
  padding: var(--space-3);
  border-bottom: 1px solid var(--border-primary);
}

.transcript-line:last-child {
  border-bottom: none;
}

.line-header {
  display: flex;
  gap: var(--space-3);
  margin-bottom: var(--space-2);
}

.line-time {
  font-size: var(--fs-xs);
  color: var(--text-tertiary);
}

.line-speaker {
  font-size: var(--fs-sm);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
}

.line-text {
  font-size: var(--fs-sm);
  color: var(--text-secondary);
  line-height: var(--lh-relaxed);
}

/* Empty State */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-8);
  text-align: center;
  color: var(--text-secondary);
}

.empty-state h3 {
  margin: var(--space-4) 0 var(--space-2) 0;
  color: var(--text-primary);
}

/* Transitions */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all var(--transition-normal);
  overflow: hidden;
}

.slide-down-enter-from,
.slide-down-leave-to {
  max-height: 0;
  opacity: 0;
}

.slide-down-enter-to,
.slide-down-leave-from {
  max-height: 500px;
  opacity: 1;
}

/* Responsive */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .filters-grid {
    grid-template-columns: 1fr;
  }
  
  .result-meta {
    flex-direction: column;
    gap: var(--space-2);
  }
  
  .result-info {
    flex-direction: column;
    gap: var(--space-2);
  }
  
  .modal-overlay {
    padding: var(--space-2);
  }
  
  .modal-header,
  .modal-body,
  .modal-footer {
    padding: var(--space-4);
  }
  
  .player-controls {
    flex-direction: column;
    gap: var(--space-3);
  }
  
  .progress-container {
    width: 100%;
  }
}
</style>