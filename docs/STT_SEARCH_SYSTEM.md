# 🎙️ STT 키워드 검색 시스템 (Speech-to-Text Search System)

## 🎯 개요

LINA Data Portal의 STT 키워드 검색 시스템은 Elasticsearch를 기반으로 음성 인식 데이터를 분석하고, 보험업계 특화 키워드 검색 및 통계 분석 기능을 제공합니다.

## 🏗️ 시스템 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   CallRecord    │    │  CallSegment    │    │ KeywordMatch    │
│   (통화 기록)    │────│  (통화 세그먼트) │────│  (키워드 매칭)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Elasticsearch                                │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │ stt_calls   │  │ call_segments│  │    keyword_matches      │ │
│  │   Index     │  │    Index    │  │        Index            │ │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## 📊 데이터 모델

### 1. CallRecord (통화 기록)

```java
@Entity
public class CallRecord {
    private String callId;              // 통화 고유 ID
    private LocalDateTime callDate;     // 통화 일시
    private CallType callType;          // INBOUND, OUTBOUND, INTERNAL
    private Integer duration;           // 통화 시간 (초)
    private String agentId;             // 상담원 ID
    private String agentName;           // 상담원 이름
    private String customerId;          // 고객 ID
    private String customerPhone;       // 고객 전화번호
    private String customerRegion;      // 고객 지역
    private String customerAgeGroup;    // 고객 연령대
    private String campaignType;        // 캠페인 타입
    private String summary;             // 통화 요약
    private String fullText;            // 전체 텍스트
    private Double sentimentScore;      // 감정 점수 (-1.0 ~ 1.0)
    private String[] keywords;          // 추출된 키워드 배열
    private String[] topics;            // 주제 태그 배열
}
```

### 2. CallSegment (통화 세그먼트)

```java
@Entity
public class CallSegment {
    private String segmentId;           // 세그먼트 고유 ID
    private String callId;              // 통화 ID (외래키)
    private Integer startTime;          // 시작 시간 (밀리초)
    private Integer endTime;            // 종료 시간 (밀리초)
    private String speaker;             // 화자 (AGENT, CUSTOMER)
    private String text;                // 세그먼트 텍스트
    private Double confidence;          // 인식 신뢰도 (0.0 ~ 1.0)
    private String[] keywords;          // 세그먼트별 키워드
    private Double sentimentScore;      // 세그먼트별 감정 점수
}
```

### 3. KeywordMatch (키워드 매칭)

```java
@Entity
public class KeywordMatch {
    private String matchId;             // 매칭 고유 ID
    private String callId;              // 통화 ID
    private String segmentId;           // 세그먼트 ID (선택적)
    private String keyword;             // 매칭된 키워드
    private String category;            // 키워드 카테고리
    private Integer frequency;          // 빈도수
    private Double relevanceScore;      // 관련성 점수
    private String context;             // 키워드 컨텍스트
    private LocalDateTime matchedAt;    // 매칭 시간
}
```

## 🔍 Elasticsearch 인덱스 구조

### 1. stt_calls 인덱스

```json
{
  "mappings": {
    "properties": {
      "call_id": { "type": "keyword" },
      "call_date": { "type": "date" },
      "call_type": { "type": "keyword" },
      "duration": { "type": "integer" },
      "agent_id": { "type": "keyword" },
      "agent_name": { "type": "text", "analyzer": "korean" },
      "customer_id": { "type": "keyword" },
      "customer_phone": { "type": "keyword" },
      "customer_region": { "type": "keyword" },
      "customer_age_group": { "type": "keyword" },
      "campaign_type": { "type": "keyword" },
      "summary": { 
        "type": "text", 
        "analyzer": "korean",
        "fields": {
          "keyword": { "type": "keyword" }
        }
      },
      "full_text": { 
        "type": "text", 
        "analyzer": "korean" 
      },
      "sentiment_score": { "type": "float" },
      "keywords": { "type": "keyword" },
      "topics": { "type": "keyword" }
    }
  }
}
```

### 2. call_segments 인덱스 (Nested)

```json
{
  "mappings": {
    "properties": {
      "call_id": { "type": "keyword" },
      "segments": {
        "type": "nested",
        "properties": {
          "segment_id": { "type": "keyword" },
          "start_time": { "type": "integer" },
          "end_time": { "type": "integer" },
          "speaker": { "type": "keyword" },
          "text": { 
            "type": "text", 
            "analyzer": "korean" 
          },
          "confidence": { "type": "float" },
          "keywords": { "type": "keyword" },
          "sentiment_score": { "type": "float" }
        }
      }
    }
  }
}
```

## 🔎 검색 기능

### 1. 기본 키워드 검색

```java
@RestController
public class STTController {
    
    // 일반 키워드 검색
    @GetMapping("/api/stt/search/general")
    public SearchResponse<CallRecord> searchGeneral(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        return sttService.searchByKeyword(keyword, page, size);
    }
}
```

```json
// 검색 쿼리 예시
{
  "query": {
    "bool": {
      "should": [
        {
          "match": {
            "summary": {
              "query": "보험료 할인",
              "boost": 2.0
            }
          }
        },
        {
          "match": {
            "full_text": {
              "query": "보험료 할인",
              "boost": 1.0
            }
          }
        }
      ]
    }
  },
  "highlight": {
    "fields": {
      "summary": {},
      "full_text": {}
    }
  }
}
```

### 2. 고급 필터링 검색

```java
// 고급 필터링 검색
@GetMapping("/api/stt/search/advanced")
public SearchResponse<CallRecord> searchAdvanced(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String callType,
        @RequestParam(required = false) String agentId,
        @RequestParam(required = false) String customerRegion,
        @RequestParam(required = false) String campaignType,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(required = false) Double minSentiment,
        @RequestParam(required = false) Double maxSentiment,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    
    return sttService.searchAdvanced(
        keyword, callType, agentId, customerRegion, campaignType,
        startDate, endDate, minSentiment, maxSentiment, page, size
    );
}
```

### 3. 세그먼트 내 검색 (Nested Query)

```java
// 세그먼트 내 문장 단위 검색
@GetMapping("/api/stt/search/segments")
public SearchResponse<CallSegment> searchSegments(
        @RequestParam String keyword,
        @RequestParam(required = false) String speaker,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    
    return sttService.searchInSegments(keyword, speaker, page, size);
}
```

```json
// Nested 검색 쿼리
{
  "query": {
    "nested": {
      "path": "segments",
      "query": {
        "bool": {
          "must": [
            {
              "match": {
                "segments.text": "보험료 할인"
              }
            },
            {
              "term": {
                "segments.speaker": "AGENT"
              }
            }
          ]
        }
      },
      "inner_hits": {
        "highlight": {
          "fields": {
            "segments.text": {}
          }
        }
      }
    }
  }
}
```

## 📊 보험업계 특화 키워드 분석

### 1. 보험 특화 키워드 카테고리

```java
public enum InsuranceKeywordCategory {
    PRODUCT("상품"),           // 보험상품, 특약, 보장내용
    CLAIM("청구"),             // 보험금, 청구절차, 서류
    PREMIUM("보험료"),         // 보험료, 할인, 납입
    CUSTOMER_SERVICE("고객서비스"), // 상담, 문의, 불만
    POLICY("약관"),           // 약관, 조건, 제한사항
    RENEWAL("갱신"),          // 갱신, 연장, 해지
    UNDERWRITING("인수"),     // 심사, 가입조건, 거절
    MARKETING("마케팅");      // 캠페인, 프로모션, 이벤트
}
```

### 2. 키워드 빈도 분석

```java
@GetMapping("/api/stt/analytics/keyword-frequency")
public List<KeywordFrequency> getKeywordFrequency(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(defaultValue = "10") int topN) {
    
    return sttService.getTopKeywords(category, startDate, endDate, topN);
}
```

```json
// 키워드 빈도 분석 결과
{
  "keywords": [
    {
      "keyword": "보험료 할인",
      "category": "PREMIUM",
      "frequency": 1250,
      "percentage": 15.2,
      "trend": "UP"
    },
    {
      "keyword": "보험금 청구",
      "category": "CLAIM", 
      "frequency": 980,
      "percentage": 11.9,
      "trend": "STABLE"
    }
  ]
}
```

### 3. 감정 분석 기반 키워드

```java
@GetMapping("/api/stt/analytics/sentiment-keywords")
public SentimentKeywordAnalysis getSentimentKeywords(
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "30") int days) {
    
    return sttService.analyzeSentimentByKeyword(keyword, days);
}
```

## 🎯 실시간 모니터링 & 알림

### 1. 실시간 키워드 모니터링

```java
@Component
public class RealTimeKeywordMonitor {
    
    @EventListener
    public void handleNewCall(CallProcessedEvent event) {
        CallRecord call = event.getCallRecord();
        
        // 위험 키워드 감지
        List<String> riskKeywords = Arrays.asList("불만", "해지", "환불", "소송");
        
        for (String keyword : riskKeywords) {
            if (call.getFullText().contains(keyword)) {
                alertService.sendRiskAlert(call, keyword);
            }
        }
        
        // 기회 키워드 감지
        List<String> opportunityKeywords = Arrays.asList("추가가입", "상품문의", "추천");
        
        for (String keyword : opportunityKeywords) {
            if (call.getFullText().contains(keyword)) {
                alertService.sendOpportunityAlert(call, keyword);
            }
        }
    }
}
```

### 2. 품질 모니터링 대시보드

```java
@GetMapping("/api/stt/dashboard")
public STTDashboardData getDashboardData() {
    return STTDashboardData.builder()
        .totalCalls(sttService.getTotalCallsToday())
        .avgSentimentScore(sttService.getAverageSentimentToday())
        .topKeywords(sttService.getTopKeywordsToday(10))
        .riskAlerts(sttService.getRiskAlertsToday())
        .qualityScore(sttService.getQualityScoreToday())
        .build();
}
```

## 📈 분석 & 통계 API

### 1. 통화 트렌드 분석

```java
@GetMapping("/api/stt/analytics/call-trend")
public CallTrendAnalysis getCallTrend(
        @RequestParam(defaultValue = "30") int days,
        @RequestParam(defaultValue = "day") String interval) {
    
    return sttService.analyzeCallTrend(days, interval);
}
```

### 2. 캠페인 효과 분석

```java
@GetMapping("/api/stt/analytics/campaign-distribution")
public List<CampaignDistribution> getCampaignDistribution(
        @RequestParam(required = false) String campaignType,
        @RequestParam(defaultValue = "30") int days) {
    
    return sttService.analyzeCampaignEffectiveness(campaignType, days);
}
```

### 3. 상담원 성과 분석

```java
@GetMapping("/api/stt/analytics/agent-performance")
public List<AgentPerformance> getAgentPerformance(
        @RequestParam(required = false) String agentId,
        @RequestParam(defaultValue = "30") int days) {
    
    return sttService.analyzeAgentPerformance(agentId, days);
}
```

## 🔧 설정 & 최적화

### 1. Elasticsearch 설정

```yaml
# application.yml
elasticsearch:
  host: localhost
  port: 9200
  username: elastic
  password: ${ELASTICSEARCH_PASSWORD}
  
  indices:
    stt-calls:
      name: stt_calls
      shards: 3
      replicas: 1
    call-segments:
      name: call_segments
      shards: 3
      replicas: 1
    keyword-matches:
      name: keyword_matches
      shards: 2
      replicas: 1
      
  analyzers:
    korean:
      tokenizer: nori_tokenizer
      filters:
        - nori_part_of_speech
        - lowercase
        - stop
```

### 2. 성능 최적화

```java
@Configuration
public class ElasticsearchConfig {
    
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        return ElasticsearchClient.builder()
            .hosts("localhost:9200")
            .connectionTimeout(Duration.ofSeconds(30))
            .socketTimeout(Duration.ofSeconds(60))
            .maxRetryTimeout(Duration.ofMinutes(5))
            .build();
    }
    
    @Bean
    public SearchTemplate searchTemplate() {
        return SearchTemplate.builder()
            .defaultPageSize(20)
            .maxPageSize(100)
            .highlightEnabled(true)
            .build();
    }
}
```

## 📊 API 엔드포인트 전체 목록

### 기본 검색 API
```http
GET /api/stt/calls                          # 상담 녹취 목록 조회
GET /api/stt/calls/{callId}                 # 상담 녹취 상세 조회
GET /api/stt/calls/{callId}/segments        # 상담 세그먼트 조회
GET /api/stt/calls/{callId}/segments/at/{timeMs} # 특정 시간대 세그먼트
GET /api/stt/keywords/search                # 키워드 검색
GET /api/stt/calls/{callId}/keywords        # 상담별 키워드 추출
```

### 고급 검색 API
```http
GET /api/stt/search/general                 # 일반 키워드 검색
GET /api/stt/search/segments                # 세그먼트 내 검색
GET /api/stt/search/customer-attributes     # 고객 속성 필터링
GET /api/stt/search/agent-keyword           # 상담원별 키워드 검색
GET /api/stt/search/advanced                # 고급 필터링 검색
GET /api/stt/search/insurance-keywords      # 보험 특화 키워드 검색
```

### 분석 & 통계 API
```http
GET /api/stt/analytics/keyword-frequency    # 키워드 빈도 분석
GET /api/stt/analytics/campaign-distribution # 캠페인 분포 분석
GET /api/stt/analytics/call-trend           # 통화 트렌드 분석
GET /api/stt/analytics/top-keywords         # 상위 키워드 Top 10
GET /api/stt/analytics/sentiment-keywords   # 감정별 키워드 분석
GET /api/stt/analytics/agent-performance    # 상담원 성과 분석
GET /api/stt/calls/{callId}/highlights      # 키워드 하이라이팅
GET /api/stt/dashboard                      # STT 대시보드 데이터
GET /api/stt/export                         # 분석 결과 내보내기
```

## 🚀 향후 개발 계획

### Phase 1 - 고급 분석 기능
- [ ] 실시간 감정 분석 및 알림
- [ ] 고객 여정 분석 (Customer Journey)
- [ ] 상담 품질 자동 평가 시스템
- [ ] 키워드 기반 상담 스크립트 추천

### Phase 2 - AI/ML 통합
- [ ] 자연어 처리 기반 의도 분석
- [ ] 고객 만족도 예측 모델
- [ ] 이상 상담 패턴 탐지
- [ ] 자동 태깅 및 분류 시스템

### Phase 3 - 고급 시각화
- [ ] 실시간 워드 클라우드
- [ ] 감정 변화 타임라인
- [ ] 상담 플로우 시각화
- [ ] 인터랙티브 분석 대시보드

---

**문서 버전**: v1.0  
**최종 업데이트**: 2024년 1월 20일  
**담당자**: STT Analytics Team