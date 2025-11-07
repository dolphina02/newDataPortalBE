# 🔐 민감도 레벨 시스템 개선 (Sensitivity Level Enhancement)

## 🎯 개선 목표

기존의 단순한 민감도 레벨을 **2단계 구조**로 체계화하여 더 명확하고 실용적인 데이터 분류 시스템 구축

## 🏗️ 새로운 민감도 시스템 구조

### 1단계: 기본 카테고리 (SensitivityCategory)

```
┌─────────────────┐    ┌─────────────────┐
│     NORMAL      │    │    SENSITIVE    │
│   (일반 정보)    │    │   (민감 정보)    │
└─────────────────┘    └─────────────────┘
         │                       │
    ┌────┴────┐              ┌───┴────┐
    │ 표준보안 │              │ 강화보안 │
    │ 긴 기간  │              │ 짧은기간 │
    └─────────┘              └────────┘
```

### 2단계: 세부 레벨 (SensitivityLevel)

#### 🟢 NORMAL 카테고리 (일반 정보)
| 레벨 | 이름 | 설명 | 기본 사용기간 | 추가승인 | 마스킹 |
|------|------|------|---------------|----------|--------|
| 0 | `PUBLIC` | 공개 정보 | 90일 | 불필요 | 불필요 |
| 1 | `INTERNAL` | 내부 정보 | 90일 | 불필요 | 불필요 |
| 2 | `NORMAL` | 일반 업무 정보 | 90일 | 불필요 | 불필요 |

#### 🔴 SENSITIVE 카테고리 (민감 정보)
| 레벨 | 이름 | 설명 | 기본 사용기간 | 추가승인 | 마스킹 |
|------|------|------|---------------|----------|--------|
| 3 | `SENSITIVE` | 민감 정보 | 30일 | 필요 | 권장 |
| 4 | `CONFIDENTIAL` | 기밀 정보 | 30일 | 필요 | 필수 |
| 5 | `RESTRICTED` | 극비 정보 | 7일 | 필수 | 필수 |

#### 🏛️ 특수 카테고리 (규제 대상)
| 레벨 | 이름 | 설명 | 기본 사용기간 | 추가승인 | 마스킹 |
|------|------|------|---------------|----------|--------|
| 4 | `PII` | 개인정보 | 30일 | 필수 | 필수 |
| 4 | `PHI` | 의료정보 | 30일 | 필수 | 필수 |
| 4 | `FINANCIAL` | 금융정보 | 30일 | 필수 | 필수 |
| 4 | `REGULATORY` | 규제정보 | 30일 | 필수 | 필수 |

## 📊 구현된 주요 기능

### 1. SensitivityLevel Enum

```java
public enum SensitivityLevel {
    // NORMAL 카테고리
    PUBLIC("공개", "PUBLIC", SensitivityCategory.NORMAL, 0, 90, false, false, false),
    INTERNAL("내부", "INTERNAL", SensitivityCategory.NORMAL, 1, 90, false, false, false),
    NORMAL("일반", "NORMAL", SensitivityCategory.NORMAL, 2, 90, false, false, false),
    
    // SENSITIVE 카테고리
    SENSITIVE("민감", "SENSITIVE", SensitivityCategory.SENSITIVE, 3, 30, true, true, true),
    CONFIDENTIAL("기밀", "CONFIDENTIAL", SensitivityCategory.SENSITIVE, 4, 30, true, true, true),
    RESTRICTED("극비", "RESTRICTED", SensitivityCategory.SENSITIVE, 5, 7, true, true, true),
    
    // 특수 카테고리 (규제 대상)
    PII("개인정보", "PII", SensitivityCategory.SENSITIVE, 4, 30, true, true, true),
    PHI("의료정보", "PHI", SensitivityCategory.SENSITIVE, 4, 30, true, true, true),
    FINANCIAL("금융정보", "FINANCIAL", SensitivityCategory.SENSITIVE, 4, 30, true, true, true),
    REGULATORY("규제정보", "REGULATORY", SensitivityCategory.SENSITIVE, 4, 30, true, true, true);
}
```

### 2. 카테고리 판별 메서드

```java
// 기본 카테고리 확인
public boolean isNormal()           // NORMAL 카테고리 여부
public boolean isSensitive()        // SENSITIVE 카테고리 여부
public boolean isHighSensitive()    // 고민감도 (레벨 4 이상)

// 특수 분류 확인
public boolean isPersonalData()     // 개인정보 관련 (PII, PHI)
public boolean isRegulated()        // 규제 대상 정보
```

### 3. 보안 정책 자동 적용

```java
private void applySecurityPolicies(Approval approval) {
    SensitivityLevel level = approval.getSensitivityLevel();
    
    // 1. 기본 정책 자동 적용
    approval.setRequiresMasking(level.requiresMasking());
    approval.setRequiresAuditLog(level.requiresAuditLog());
    
    // 2. 사용 기간 설정
    approval.setUsageDurationDays(level.getDefaultUsageDays());
    
    // 3. 카테고리별 특수 정책
    if (level.isSensitive()) {
        // 민감 정보 공통 정책
        approval.setRequiresAuditLog(true);
        
        // 개인정보 특수 처리
        if (level.isPersonalData()) {
            approval.setMaskingPolicyId(getPiiMaskingPolicyId(level));
        }
        
        // 극비 정보 특수 처리
        if (level == SensitivityLevel.RESTRICTED) {
            approval.setUsageDurationDays(7); // 강제 1주일 제한
        }
    }
}
```

## 🔄 사용 기간 관리 개선

### 민감도별 사용 기간 정책

| 카테고리 | 기본 기간 | 최대 허용 기간 | 연장 정책 |
|----------|-----------|----------------|-----------|
| **NORMAL** | 90일 (3개월) | 180일 (6개월) | 자유로운 연장 |
| **SENSITIVE** | 30일 (1개월) | 60일 (2개월) | 제한적 연장 |
| **RESTRICTED** | 7일 (1주일) | 7일 (1주일) | 연장 불가 |

### 동적 기간 조정

```java
// 접근 범위와 민감도 조합에 따른 동적 조정
if (accessScope.isHighRisk() && sensitivityLevel.isSensitive()) {
    int restrictedDays = Math.min(currentDays, 7); // 최대 1주일로 제한
    approval.setUsageDurationDays(restrictedDays);
}
```

## 📊 새로운 API 엔드포인트

### 민감도 관련 조회 API

```http
# 민감도 레벨별 승인 요청 조회
GET /api/approvals/by-sensitivity/{sensitivityLevel}

# 민감도 카테고리별 승인 요청 조회
GET /api/approvals/by-sensitivity-category/{category}

# 개인정보 관련 승인 요청 조회
GET /api/approvals/personal-data

# 규제 대상 승인 요청 조회
GET /api/approvals/regulated

# 민감도 통계 조회
GET /api/approvals/analytics/sensitivity-stats
```

### 민감도 정보 API

```http
# 민감도 레벨 목록 (상세 정보 포함)
GET /api/enums/sensitivity-levels

# 민감도 카테고리 목록
GET /api/enums/sensitivity-categories

# 특정 민감도 레벨 상세 정보
GET /api/enums/sensitivity-levels/{level}

# 민감도별 권장 접근 범위
GET /api/enums/sensitivity-levels/{level}/recommended-access-scopes
```

## 🎯 실제 사용 시나리오

### 1. 일반 업무 데이터 접근

```json
{
  "type": "DATA_ACCESS",
  "title": "마케팅 분석용 데이터 접근",
  "targetType": "DATASET",
  "targetId": "marketing_data_001",
  "accessScope": "READ",
  "sensitivityLevel": "NORMAL"
}
```

**자동 적용 정책:**
- 사용 기간: 90일 (3개월)
- 추가 승인: 불필요
- 마스킹: 불필요
- 감사 로그: 불필요

### 2. 개인정보 데이터 접근

```json
{
  "type": "DATA_ACCESS", 
  "title": "고객 만족도 조사 분석",
  "targetType": "DATASET",
  "targetId": "customer_survey_pii",
  "accessScope": "MASKED_READ",
  "sensitivityLevel": "PII"
}
```

**자동 적용 정책:**
- 사용 기간: 30일 (1개월)
- 추가 승인: 필수
- 마스킹: 필수 (PII 전용 정책)
- 감사 로그: 필수

### 3. 극비 정보 접근

```json
{
  "type": "DATA_ACCESS",
  "title": "경영진 전략 데이터 분석", 
  "targetType": "DATASET",
  "targetId": "executive_strategy_data",
  "accessScope": "VIEW_ONLY",
  "sensitivityLevel": "RESTRICTED"
}
```

**자동 적용 정책:**
- 사용 기간: 7일 (1주일, 강제 제한)
- 추가 승인: 필수
- 마스킹: 필수
- 감사 로그: 필수
- 연장: 불가

## 📈 통계 및 모니터링

### 민감도별 승인 현황

```json
{
  "byLevel": {
    "PUBLIC": 45,
    "INTERNAL": 123,
    "NORMAL": 234,
    "SENSITIVE": 67,
    "CONFIDENTIAL": 23,
    "RESTRICTED": 5,
    "PII": 34,
    "PHI": 12,
    "FINANCIAL": 18,
    "REGULATORY": 8
  },
  "byCategory": {
    "NORMAL": 402,
    "SENSITIVE": 167
  },
  "summary": {
    "totalApprovals": 569,
    "normalPercentage": 70.6,
    "sensitivePercentage": 29.4
  }
}
```

### 보안 준수 지표

| 지표 | 목표 | 현재 |
|------|------|------|
| 민감정보 마스킹 적용률 | 100% | 98.5% |
| 고민감도 감사로그 기록률 | 100% | 99.2% |
| 사용기간 준수율 | 95% | 97.8% |
| 개인정보 접근 승인률 | 85% | 82.3% |

## 🔍 민감도 레벨 선택 가이드

### 데이터 분류 기준

```
📊 데이터 분류 플로우차트

외부 공개 가능? ──Yes──> PUBLIC
     │
     No
     ▼
조직 내부만? ──Yes──> INTERNAL  
     │
     No
     ▼
개인정보 포함? ──Yes──> PII/PHI
     │
     No
     ▼
금융정보? ──Yes──> FINANCIAL
     │
     No
     ▼
규제 대상? ──Yes──> REGULATORY
     │
     No
     ▼
기밀 수준은?
├─ 일반 ──> NORMAL
├─ 민감 ──> SENSITIVE  
├─ 기밀 ──> CONFIDENTIAL
└─ 극비 ──> RESTRICTED
```

### 접근 범위 권장사항

| 민감도 레벨 | 권장 접근 범위 | 비권장 접근 범위 |
|-------------|----------------|------------------|
| **PUBLIC** | READ, EXPORT, SHARE | ADMIN, DELETE |
| **INTERNAL** | READ, WRITE, EXPORT | ADMIN, DELETE |
| **NORMAL** | READ, WRITE, EXPORT, SHARE | ADMIN |
| **SENSITIVE** | READ, MASKED_READ, EXPORT | WRITE, ADMIN, DELETE |
| **CONFIDENTIAL** | MASKED_READ, VIEW_ONLY | WRITE, EXPORT, SHARE |
| **RESTRICTED** | VIEW_ONLY | 모든 수정/공유 권한 |
| **PII/PHI** | MASKED_READ, VIEW_ONLY | EXPORT, SHARE |
| **FINANCIAL** | MASKED_READ, VIEW_ONLY | EXPORT, SHARE |

## 🚀 향후 개선 계획

### Phase 1 - 지능형 분류
- [ ] AI 기반 자동 민감도 분류
- [ ] 데이터 내용 스캔을 통한 민감도 추천
- [ ] 사용 패턴 학습을 통한 동적 조정

### Phase 2 - 고급 정책 관리
- [ ] 조직별 커스텀 민감도 레벨
- [ ] 시간대별 차등 접근 제어
- [ ] 지역별 규제 준수 자동 적용

### Phase 3 - 통합 거버넌스
- [ ] 데이터 계보와 민감도 연동
- [ ] 실시간 민감도 변경 추적
- [ ] 규제 변경 시 자동 정책 업데이트

## 📋 마이그레이션 가이드

### 기존 시스템에서 새 시스템으로

```sql
-- 기존 민감도 레벨 매핑
UPDATE approvals SET sensitivity_level = 
  CASE 
    WHEN sensitivity_level = 'STRICT' THEN 'RESTRICTED'
    ELSE sensitivity_level
  END;

-- 사용 기간 정규화 (시간 -> 일)
UPDATE approvals SET usage_duration_days = 
  CASE 
    WHEN access_duration_hours IS NOT NULL 
    THEN CEIL(access_duration_hours / 24.0)
    ELSE 90
  END;
```

### API 호환성

- 기존 API는 `@Deprecated` 처리하되 동작 유지
- 새로운 API로 점진적 마이그레이션 권장
- 6개월 후 기존 API 제거 예정

---

**문서 버전**: v1.0  
**최종 업데이트**: 2024년 1월 20일  
**담당자**: Security & Governance Team