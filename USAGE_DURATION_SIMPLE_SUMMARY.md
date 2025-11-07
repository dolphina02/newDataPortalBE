# 사용 기간 관리 시스템 (단순 버전) 구현 완료

## 🎯 구현 목표
승인 시스템에 명확한 사용 기간 정의 및 기본값 3개월 설정

## 📋 주요 변경사항

### 1. Approval 엔티티 개선
- `accessDurationHours` → `usageDurationDays` 변경
- 기본값 90일 (3개월) 설정
- 민감도별 차등 기간 적용:
  - 임시 권한: 1일
  - 민감정보: 30일 (1개월)
  - 일반 데이터: 90일 (3개월)

### 2. 데이터베이스 스키마 업데이트
```sql
-- 기존
access_duration_hours INTEGER,
access_expires_at TIMESTAMP,

-- 변경 후
usage_duration_days INTEGER DEFAULT 90,
usage_expires_at TIMESTAMP,
```

### 3. 비즈니스 메서드 개선
- `calculateUsageExpiration()`: 일 단위 만료 시간 계산
- `isUsageExpired()`: 사용 권한 만료 여부 확인
- `getDaysUntilExpiry()`: 만료까지 남은 일수 계산
- `activateUsage()`: 승인 완료 시 사용 권한 활성화

### 4. 서비스 레이어 확장
- `activateApprovedUsage()`: 사용 권한 활성화
- `getExpiringUsageApprovals()`: 만료 예정 권한 조회
- `cleanupExpiredUsage()`: 만료된 권한 정리

### 5. API 엔드포인트 추가
- `GET /approvals/expiring?days=30`: 만료 예정 조회
- `POST /approvals/{id}/activate-usage`: 사용 권한 활성화
- `POST /approvals/cleanup-expired-usage`: 만료된 권한 정리

### 6. 하위 호환성 보장
- 기존 `accessDurationHours` 관련 메서드들을 `@Deprecated` 처리
- 자동 변환 로직으로 기존 API 호환성 유지

## 🔑 핵심 개선 효과

### 1. 명확한 사용 기간
- **기본 3개월**: 실용적이고 합리적인 기본 사용 기간
- **일 단위 관리**: 시간 단위보다 직관적이고 관리하기 쉬움
- **차등 적용**: 민감도에 따른 자동 차등 적용

### 2. 보안 강화
- **민감정보**: 1개월로 제한
- **임시 권한**: 1일로 최소화
- **일반 데이터**: 3개월의 합리적 기간

### 3. 운영 효율성
- **자동 만료**: 수동 관리 부담 감소
- **만료 조회**: 30일 전 미리 확인 가능
- **일괄 정리**: 만료된 권한 자동 정리

## 📊 사용 예시

### 1. 일반 데이터 접근
```java
// 기본 3개월 사용 기간 자동 설정
Approval approval = new Approval(
    ApprovalType.DATA_ACCESS,
    "마케팅 데이터 분석",
    "고객 세그먼트 분석용",
    requester,
    TargetType.DATASET,
    "marketing_data",
    "마케팅 데이터셋",
    AccessScope.READ,
    SensitivityLevel.NORMAL
);
// usageDurationDays = 90 (자동 설정)
```

### 2. 민감정보 접근
```java
// 1개월로 자동 제한
Approval approval = new Approval(
    ApprovalType.DATA_ACCESS,
    "개인정보 분석",
    "고객 만족도 조사",
    requester,
    TargetType.DATASET,
    "pii_data",
    "개인정보 데이터셋",
    AccessScope.MASKED_READ,
    SensitivityLevel.PII
);
// usageDurationDays = 30 (자동 설정)
```

### 3. 임시 권한
```java
// 1일로 자동 제한
Approval approval = new Approval(
    ApprovalType.DATA_ACCESS,
    "긴급 데이터 확인",
    "시스템 장애 원인 분석",
    requester,
    TargetType.DATASET,
    "system_logs",
    "시스템 로그",
    AccessScope.TEMPORARY_READ,
    SensitivityLevel.NORMAL
);
// usageDurationDays = 1 (자동 설정)
```

## 🚀 활용 방법

### 1. 만료 예정 권한 모니터링
```bash
# 30일 이내 만료 예정 조회
GET /api/approvals/expiring?days=30

# 7일 이내 만료 예정 조회  
GET /api/approvals/expiring?days=7
```

### 2. 사용 권한 활성화
```bash
# 승인 완료 후 사용 권한 활성화
POST /api/approvals/123/activate-usage
```

### 3. 만료된 권한 정리
```bash
# 만료된 사용 권한 일괄 정리
POST /api/approvals/cleanup-expired-usage
```

---

**구현 완료일**: 2024년 1월 20일  
**핵심 개선**: 사용 기간 명확화 및 기본 3개월 설정  
**다음 단계**: 자동화 배치 작업 및 알림 시스템 구현