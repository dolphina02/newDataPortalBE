# 승인 대상 오브젝트 1급 개념 도입 완료 요약

## 🎯 작업 목표
승인 시스템에 "무엇을 승인하는지"를 명확하게 하는 1급 개념 도입:
- **targetType** (DATASET/VIEW/REPORT/DASHBOARD/CLUSTER/POLICY 등)
- **targetId** (dataset id, dashboard id 등)
- **accessScope** (READ/WRITE/EXPORT/SHARE 등)
- **sensitivityLevel** (NORMAL/SENSITIVE/STRICT 등)
- **보안정책 연결** (maskingPolicyId/policySetId)

## 📋 주요 구현사항

### 1. 새로운 Enum 클래스들

#### ✅ TargetType (승인 대상 타입)
- **파일**: `src/main/java/com/lina/dataportal/domain/approval/TargetType.java`
- **주요 타입**: DATASET, VIEW, TABLE, DASHBOARD, REPORT, NOTEBOOK, CLUSTER, WORKSPACE, POLICY, ROLE, API, SERVICE, MODEL, PIPELINE, FILE, FOLDER, OTHER
- **카테고리 메서드**: isDataType(), isAnalyticsType(), isInfraType(), isSecurityType(), isMlType()

#### ✅ AccessScope (접근 범위)
- **파일**: `src/main/java/com/lina/dataportal/domain/approval/AccessScope.java`
- **주요 범위**: READ, WRITE, DELETE, EXECUTE, DEPLOY, SHARE, EXPORT, DOWNLOAD, ADMIN, OWNER, VIEW_ONLY, MASKED_READ, TEMPORARY_READ, TEMPORARY_WRITE
- **위험도 레벨**: 1(낮음) ~ 5(높음)
- **특성 메서드**: includesRead(), includesWrite(), isHighRisk(), isTemporary(), requiresMasking()

#### ✅ SensitivityLevel (민감도 레벨)
- **파일**: `src/main/java/com/lina/dataportal/domain/approval/SensitivityLevel.java`
- **주요 레벨**: PUBLIC, INTERNAL, NORMAL, SENSITIVE, CONFIDENTIAL, STRICT, PII, PHI, FINANCIAL, REGULATORY
- **레벨**: 0(공개) ~ 5(극비)
- **특성 메서드**: isHighSensitive(), isCriticalSensitive(), isPersonalData(), isRegulated(), requiresMasking(), requiresAuditLog(), requiresAdditionalApproval()

### 2. 보안 정책 엔티티들

#### ✅ MaskingPolicy (마스킹 정책)
- **파일**: `src/main/java/com/lina/dataportal/domain/security/MaskingPolicy.java`
- **마스킹 타입**: PARTIAL, FULL, HASH, ENCRYPT, TOKENIZE, REDACT
- **설정**: maskPattern, maskCharacter, preserveLength, preserveFormat
- **적용 규칙**: applicableColumns, exclusionRules

#### ✅ PolicySet (정책 세트)
- **파일**: `src/main/java/com/lina/dataportal/domain/security/PolicySet.java`
- **카테고리**: DATA_PROTECTION, ACCESS_CONTROL, COMPLIANCE, PRIVACY, SECURITY, AUDIT
- **심각도**: LOW, MEDIUM, HIGH, CRITICAL
- **포함 정책**: maskingPolicyIds, accessControlRules, auditRequirements, retentionPolicy, encryptionRequirements

### 3. Approval 엔티티 확장

#### ✅ 승인 대상 오브젝트 필드 추가
```java
// 승인 대상 정보
private TargetType targetType;        // 대상 타입
private String targetId;              // 대상 ID
private String targetName;            // 대상 이름
private String targetDescription;     // 대상 설명

// 접근 범위 및 민감도
private AccessScope accessScope;      // 접근 범위
private SensitivityLevel sensitivityLevel; // 민감도 레벨

// 보안 정책 연결
private Long maskingPolicyId;         // 마스킹 정책 ID
private Long policySetId;             // 정책 세트 ID

// 추가 보안 설정
private Boolean requiresMasking;      // 마스킹 필요 여부
private Boolean requiresAuditLog;     // 감사 로그 필요 여부
private Integer accessDurationHours;  // 접근 권한 유효 시간
private LocalDateTime accessExpiresAt; // 접근 권한 만료 시간

// 업무 정당성
private String businessJustification; // 업무 목적 및 정당성
private String dataUsagePurpose;     // 데이터 사용 목적
private LocalDateTime expectedCompletionDate; // 예상 완료 일자
```

#### ✅ 비즈니스 메서드 추가
- `isHighRiskRequest()`: 고위험 승인 요청 여부
- `requiresAdditionalApproval()`: 추가 승인 필요 여부
- `calculateAccessExpiration()`: 접근 권한 만료 시간 계산
- `isAccessExpired()`: 접근 권한 만료 여부
- `requiresMaskingPolicy()`: 마스킹 정책 적용 필요 여부
- `requiresAuditLogging()`: 감사 로그 필요 여부
- `activateAccess()`: 승인 완료 시 접근 권한 활성화
- `getFullTargetIdentifier()`: 대상 오브젝트 전체 식별자
- `getApprovalSummary()`: 승인 요청 요약 정보

### 4. 서비스 레이어 확장

#### ✅ ApprovalService 새로운 메서드들
- `createApprovalWithTarget()`: 승인 대상 오브젝트 포함 승인 요청 생성
- `getApprovalsByTargetType()`: 특정 대상 타입의 승인 요청들 조회
- `getApprovalsByTarget()`: 특정 대상 오브젝트의 승인 요청들 조회
- `getApprovalsBySensitivityLevel()`: 민감도 레벨별 승인 요청들 조회
- `getHighRiskApprovals()`: 고위험 승인 요청들 조회
- `getExpiringAccessApprovals()`: 만료 예정 접근 권한 조회
- `getActiveApprovalForUserAndTarget()`: 특정 사용자의 특정 대상에 대한 활성 승인 조회
- `applySecurityPolicies()`: 보안 정책 자동 적용
- `activateApprovedAccess()`: 승인 완료 후 접근 권한 활성화
- `cleanupExpiredAccess()`: 만료된 접근 권한 정리

### 5. 리포지토리 확장

#### ✅ ApprovalRepository 새로운 쿼리 메서드들
- 대상 타입/오브젝트별 조회
- 민감도 레벨별 조회
- 접근 범위별 조회
- 고위험 승인 조회
- 만료 관련 조회
- 보안 정책별 조회
- 감사/마스킹 필요 조회

### 6. API 엔드포인트 확장

#### ✅ ApprovalController 새로운 API들
- `POST /approvals/with-target`: 승인 대상 오브젝트 포함 승인 요청 생성
- `GET /approvals/by-target-type/{targetType}`: 대상 타입별 조회
- `GET /approvals/by-target/{targetType}/{targetId}`: 특정 대상 오브젝트 조회
- `GET /approvals/by-sensitivity/{sensitivityLevel}`: 민감도별 조회
- `GET /approvals/high-risk`: 고위험 승인 조회
- `GET /approvals/expiring`: 만료 예정 접근 권한 조회
- `GET /approvals/active`: 활성 승인 조회
- `POST /approvals/{id}/activate-access`: 접근 권한 활성화
- `POST /approvals/cleanup-expired`: 만료된 접근 권한 정리

#### ✅ EnumController 확장
- `GET /api/enums/target-types`: 승인 대상 타입 목록 (상세 정보 포함)
- `GET /api/enums/access-scopes`: 접근 범위 목록 (위험도 정보 포함)
- `GET /api/enums/sensitivity-levels`: 민감도 레벨 목록 (특성 정보 포함)
- `GET /api/enums/target-types/{targetType}/access-scopes`: 대상 타입별 적합한 접근 범위
- `GET /api/enums/sensitivity-levels/{sensitivityLevel}/recommended-access-scopes`: 민감도별 권장 접근 범위

### 7. 데이터베이스 스키마 업데이트

#### ✅ approvals 테이블 확장
```sql
-- 승인 대상 오브젝트 정보
target_type VARCHAR(50) NOT NULL,
target_id VARCHAR(255) NOT NULL,
target_name VARCHAR(255),
target_description TEXT,

-- 접근 범위 및 민감도
access_scope VARCHAR(50) NOT NULL,
sensitivity_level VARCHAR(50) NOT NULL,

-- 보안 정책 연결
masking_policy_id BIGINT,
policy_set_id BIGINT,

-- 추가 보안 설정
requires_masking BOOLEAN DEFAULT FALSE,
requires_audit_log BOOLEAN DEFAULT FALSE,
access_duration_hours INTEGER,
access_expires_at TIMESTAMP,

-- 업무 정당성
business_justification TEXT,
data_usage_purpose TEXT,
expected_completion_date TIMESTAMP,

-- 외래키 제약 조건
FOREIGN KEY (masking_policy_id) REFERENCES masking_policies(id),
FOREIGN KEY (policy_set_id) REFERENCES policy_sets(id)
```

#### ✅ 새로운 보안 정책 테이블들
- `masking_policies`: 데이터 마스킹 정책 관리
- `policy_sets`: 보안 정책 세트 관리

#### ✅ 성능 최적화 인덱스
- 승인 대상 관련: `idx_approval_target`, `idx_approval_target_type`
- 접근 범위/민감도: `idx_approval_access_scope`, `idx_approval_sensitivity`
- 만료 관리: `idx_approval_expires`
- 보안 정책: `idx_approval_masking_policy`, `idx_approval_policy_set`
- 고위험 조회: `idx_approval_high_risk` (조건부 인덱스)

### 8. 샘플 데이터

#### ✅ 보안 정책 샘플 데이터
- **마스킹 정책**: PII 부분 마스킹, 금융정보 전체 마스킹, 이메일 해시 마스킹, 민감정보 암호화
- **정책 세트**: 금융 데이터 정책, 개인정보보호 정책, 일반 데이터 정책, 규정 준수 정책

#### ✅ 승인 요청 샘플 데이터 (대상 오브젝트 포함)
- 영업 대시보드 배포 요청 (DASHBOARD/DEPLOY/INTERNAL)
- 고객 데이터 접근 권한 (DATASET/MASKED_READ/PII)
- 금융 데이터 테이블 접근 (TABLE/READ/FINANCIAL)
- 재무 대시보드 구독 (DASHBOARD/READ/CONFIDENTIAL)
- ML 모델 배포 권한 (MODEL/DEPLOY/SENSITIVE)

## 🔑 핵심 개선 효과

### 1. 명확한 승인 대상 정의
- **무엇을**: TargetType + TargetId로 정확한 대상 식별
- **어떤 권한을**: AccessScope로 구체적인 접근 범위 정의
- **어떤 수준으로**: SensitivityLevel로 민감도 기반 보안 수준 적용

### 2. 자동화된 보안 정책 적용
- 민감도 레벨에 따른 자동 마스킹 정책 적용
- 접근 범위에 따른 자동 유효 기간 설정
- 고위험 요청에 대한 자동 추가 승인 요구

### 3. 세밀한 접근 제어
- 시간 제한 접근 권한 (accessDurationHours, accessExpiresAt)
- 마스킹된 읽기 권한 (MASKED_READ, VIEW_ONLY)
- 임시 권한 (TEMPORARY_READ, TEMPORARY_WRITE)

### 4. 강화된 감사 및 추적
- 업무 정당성 및 사용 목적 기록
- 자동 감사 로그 요구사항 적용
- 접근 권한 만료 및 정리 자동화

### 5. 유연한 보안 정책 관리
- 재사용 가능한 마스킹 정책
- 조합 가능한 정책 세트
- 대상 타입별 적용 가능한 정책 자동 매칭

## 🚀 사용 예시

### 1. 고객 데이터 접근 요청
```json
{
  "type": "DATA_ACCESS",
  "title": "마케팅 캠페인용 고객 데이터 접근",
  "targetType": "DATASET",
  "targetId": "customer_dataset_001",
  "accessScope": "MASKED_READ",
  "sensitivityLevel": "PII",
  "businessJustification": "타겟 마케팅 캠페인 기획",
  "dataUsagePurpose": "개인정보 보호된 고객 세그먼트 분석"
}
```

### 2. 대시보드 배포 요청
```json
{
  "type": "DASHBOARD_DEPLOY",
  "title": "영업 실적 대시보드 배포",
  "targetType": "DASHBOARD",
  "targetId": "sales_dashboard_001",
  "accessScope": "DEPLOY",
  "sensitivityLevel": "INTERNAL",
  "businessJustification": "영업팀 성과 관리 및 실시간 모니터링"
}
```

### 3. ML 모델 배포 요청
```json
{
  "type": "MODEL_DEPLOY",
  "title": "고객 이탈 예측 모델 배포",
  "targetType": "MODEL",
  "targetId": "churn_prediction_v3",
  "accessScope": "DEPLOY",
  "sensitivityLevel": "SENSITIVE",
  "businessJustification": "고객 유지 전략 수립"
}
```

## 📈 향후 확장 방향

### 1. 동적 정책 엔진
- 규칙 기반 자동 승인/반려
- 조건부 승인 (시간, 위치, 역할 기반)
- 위험도 점수 기반 승인 라우팅

### 2. 실시간 모니터링
- 접근 권한 사용 현황 추적
- 이상 접근 패턴 탐지
- 자동 권한 회수 시스템

### 3. 외부 시스템 연동
- Active Directory/Entra ID 연동
- SIEM 시스템 연동
- 데이터 거버넌스 도구 연동

### 4. 고급 마스킹 기능
- 동적 마스킹 (사용자별 다른 마스킹)
- 컨텍스트 기반 마스킹
- 차등 프라이버시 적용

---

**작업 완료일**: 2024년 1월 20일  
**구현 범위**: 승인 대상 오브젝트 1급 개념 도입 완료  
**다음 단계**: 실제 보안 정책 엔진 구현 및 외부 시스템 연동