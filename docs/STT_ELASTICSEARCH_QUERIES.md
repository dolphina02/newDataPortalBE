# STT Elasticsearch 쿼리 가이드

이 문서는 LINA Data Portal의 STT 키워드 검색 기능에서 사용되는 Elasticsearch 쿼리들을 설명합니다.

## 🔍 기본 검색 쿼리

### 1. 일반적인 키워드 검색
**API**: `GET /api/stt/search/general?keyword=암진단 특약`

**Elasticsearch 쿼리**:
```json
POST stt-calls/_search
{
  "query": {
    "multi_match": {
      "query": "암진단 특약",
      "fields": ["summary", "full_text"]
    }
  },
  "_source": ["call_id", "summary", "campaign_type", "cust", "agent_id"]
}
```

**설명**: summary와 full_text 필드에서 "암진단 특약" 키워드를 검색하고, 필요한 필드만 반환합니다.

---

### 2. 세그먼트 내 문장 단위 검색 (Nested)
**API**: `GET /api/stt/search/segments?phrase=납입면제`

**Elasticsearch 쿼리**:
```json
POST stt-calls/_search
{
  "query": {
    "nested": {
      "path": "segments",
      "query": {
        "match_phrase": { 
          "segments.text": "납입면제" 
        }
      },
      "inner_hits": {
        "size": 3,
        "_source": ["segments.ts_start_ms", "segments.text"]
      }
    }
  },
  "_source": ["call_id", "agent_id", "cust"]
}
```

**설명**: segments 배열 내에서 "납입면제"라는 정확한 구문을 찾고, 매칭된 세그먼트의 시간과 텍스트를 inner_hits로 반환합니다.

---

### 3. 고객 속성 필터링
**API**: `GET /api/stt/search/customer-attributes?region=Seoul&ageBand=50s`

**Elasticsearch 쿼리**:
```json
POST stt-calls/_search
{
  "query": {
    "bool": {
      "filter": [
        { "term": { "cust.region": "Seoul" } },
        { "term": { "cust.age_band": "50s" } }
      ]
    }
  },
  "_source": ["call_id", "summary", "cust", "agent_id"]
}
```

**설명**: 서울 지역의 50대 고객 통화만 필터링하여 검색합니다.

---

### 4. 특정 상담원의 키워드 관련 콜 검색
**API**: `GET /api/stt/search/agent-keyword?agentId=agent_019&keyword=해지`

**Elasticsearch 쿼리**:
```json
POST stt-calls/_search
{
  "query": {
    "bool": {
      "must": [
        { "term": { "agent_id": "agent_019" } },
        { "match": { "full_text": "해지" } }
      ]
    }
  },
  "_source": ["call_id", "summary", "full_text"]
}
```

**설명**: 특정 상담원(agent_019)이 처리한 통화 중에서 "해지" 키워드가 포함된 통화를 찾습니다.

---

## 📊 고급 분석 쿼리

### 5. 키워드 집계 분석 (Aggregation)
**API**: `GET /api/stt/analytics/top-keywords?topic=특약`

**Elasticsearch 쿼리 개념**:
```json
POST stt-calls/_search
{
  "size": 0,
  "query": {
    "match": {
      "full_text": "특약"
    }
  },
  "aggs": {
    "top_keywords": {
      "terms": {
        "field": "full_text.keyword",
        "size": 10
      }
    }
  }
}
```

**설명**: "특약" 관련 통화에서 가장 자주 언급되는 키워드 상위 10개를 집계합니다.

---

### 6. 복합 조건 검색
**API**: `GET /api/stt/search/advanced?keyword=보험금&agentId=agent_001&region=Seoul&startDate=2025-01-01T00:00:00&endDate=2025-01-31T23:59:59`

**Elasticsearch 쿼리**:
```json
POST stt-calls/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "multi_match": {
            "query": "보험금",
            "fields": ["summary", "full_text", "segments.text"]
          }
        }
      ],
      "filter": [
        { "term": { "agent_id": "agent_001" } },
        { "term": { "cust.region": "Seoul" } },
        {
          "range": {
            "call_time": {
              "gte": "2025-01-01T00:00:00",
              "lte": "2025-01-31T23:59:59"
            }
          }
        }
      ]
    }
  }
}
```

**설명**: 여러 조건을 조합한 복합 검색으로, 키워드 매칭과 필터 조건을 동시에 적용합니다.

---

## 🎯 보험업계 특화 검색

### 7. 보험 특화 키워드 검색
**API**: `GET /api/stt/search/insurance-keywords?category=특약&region=Seoul`

**주요 보험 키워드**:
- **보장 관련**: 특약, 보장, 납입면제, 암진단, 수술비, 입원비
- **계약 관련**: 해지, 만기, 갱신, 보험료, 보험금, 청구
- **상품 관련**: 종신보험, 정기보험, 변액보험, 연금보험

### 8. 감정 분석 키워드
**API**: `GET /api/stt/analytics/sentiment-keywords?agentId=agent_001`

**긍정 키워드**: 감사, 만족, 좋다, 친절, 빠르다, 정확
**부정 키워드**: 불만, 느리다, 복잡, 어렵다, 문제, 실망

---

## 🔧 쿼리 최적화 팁

### 1. 필드 선택적 반환
`_source` 파라미터를 사용하여 필요한 필드만 반환하여 성능을 향상시킵니다.

### 2. Nested 쿼리 활용
segments와 같은 nested 필드는 반드시 nested 쿼리를 사용해야 정확한 결과를 얻을 수 있습니다.

### 3. Filter vs Must
- **Filter**: 스코어링 없이 빠른 필터링 (캐시 가능)
- **Must**: 스코어링과 함께 관련도 계산

### 4. 집계 쿼리 최적화
집계만 필요한 경우 `"size": 0`으로 설정하여 문서 반환을 생략합니다.

---

## 📝 실제 사용 예시

### 상담 품질 모니터링
```bash
# 특정 상담원의 불만 관련 통화 검색
curl -X GET "localhost:8080/api/stt/search/agent-keyword?agentId=agent_019&keyword=불만"

# 지역별 보험금 청구 관련 통화 분석
curl -X GET "localhost:8080/api/stt/search/customer-attributes?region=Seoul" \
     -G -d "keyword=보험금"
```

### 키워드 트렌드 분석
```bash
# 특약 관련 상위 키워드 분석
curl -X GET "localhost:8080/api/stt/analytics/top-keywords?topic=특약"

# 상담원별 긍정/부정 키워드 분석
curl -X GET "localhost:8080/api/stt/analytics/sentiment-keywords?agentId=agent_001"
```

이러한 쿼리들을 통해 보험 상담의 품질 향상과 고객 만족도 개선에 필요한 인사이트를 얻을 수 있습니다.