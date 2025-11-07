# 📚 데이터 카탈로그 시스템 (Data Catalog System)

## 🎯 개요

LINA Data Portal의 데이터 카탈로그 시스템은 조직 내 모든 데이터 자산을 체계적으로 관리하고, 데이터 발견성을 높이며, 메타데이터 기반의 데이터 거버넌스를 지원합니다.

## 🏗️ 시스템 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   DataTable     │    │    Report       │    │   MLModel       │
│  (데이터 테이블)  │    │   (리포트)       │    │  (ML 모델)       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Data Catalog Registry                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │ Metadata    │  │ Lineage     │  │    Quality Metrics      │ │
│  │ Management  │  │ Tracking    │  │      & Profiling        │ │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   ApiEndpoint   │    │  Dashboard      │    │ SecurityPolicy  │
│  (API 엔드포인트) │    │  (대시보드)      │    │   (보안 정책)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 📊 데이터 모델

### 1. DataTable (데이터 테이블)

```java
@Entity
public class DataTable {
    private Long id;
    private String tableName;           // 테이블명
    private String schemaName;          // 스키마명
    private String description;         // 테이블 설명
    private String category;            // 카테고리 (customer, product, claim 등)
    private DataType dataType;          // DATASET, VIEW, TABLE, EXTERNAL_TABLE
    
    // 메타데이터
    private Long recordCount;           // 레코드 수
    private Integer columnCount;        // 컬럼 수
    private Long dataSizeBytes;         // 데이터 크기 (바이트)
    private LocalDateTime lastUpdated;  // 최종 업데이트 시간
    private String owner;               // 데이터 소유자
    private String steward;             // 데이터 스튜어드
    
    // 품질 지표
    private Double qualityScore;        // 데이터 품질 점수 (0-100)
    private Double completenessRate;    // 완전성 비율
    private Double accuracyRate;        // 정확성 비율
    private Double consistencyRate;     // 일관성 비율
    
    // 사용성 지표
    private Boolean isFavorite = false; // 즐겨찾기 여부
    private Integer viewCount = 0;      // 조회 수
    private Integer downloadCount = 0;  // 다운로드 수
    private Double popularityScore;     // 인기도 점수
    
    // 보안 & 거버넌스
    private SensitivityLevel sensitivityLevel; // 민감도 레벨
    private Boolean isPii = false;      // 개인정보 포함 여부
    private Boolean isRegulated = false; // 규제 대상 여부
    private String retentionPolicy;     // 보존 정책
    private LocalDateTime retentionDate; // 보존 만료일
    
    // 태그 & 분류
    private String[] tags;              // 태그 배열
    private String businessGlossary;    // 비즈니스 용어집 연결
    private String dataLineage;         // 데이터 계보 정보
}
```

### 2. Report (리포트)

```java
@Entity
public class Report {
    private Long id;
    private String title;               // 리포트 제목
    private String description;         // 리포트 설명
    private String category;            // 카테고리 (monthly, customer, kpi 등)
    private ReportType type;            // MONTHLY_REPORT, CUSTOMER_ANALYSIS, KPI_REPORT
    
    // 파일 정보
    private String filePath;            // 파일 경로
    private String fileName;            // 파일명
    private Long fileSize;              // 파일 크기
    private String fileFormat;          // 파일 형식 (PDF, XLSX, PPTX)
    private String checksum;            // 파일 체크섬
    
    // 메타데이터
    private Boolean containsSensitiveData = false; // 민감정보 포함 여부
    private String createdBy;           // 생성자
    private LocalDateTime createdAt;    // 생성일시
    private LocalDateTime publishedAt;  // 발행일시
    private String version;             // 버전
    
    // 사용성 지표
    private Integer downloadCount = 0;  // 다운로드 수
    private Integer viewCount = 0;      // 조회 수
    private Double rating;              // 평점
    private String[] tags;              // 태그 배열
    
    // 접근 제어
    private AccessLevel accessLevel;    // PUBLIC, INTERNAL, RESTRICTED
    private String[] authorizedRoles;   // 접근 권한 역할
    private LocalDateTime expiryDate;   // 만료일
}
```

### 3. MLModel (ML 모델)

```java
@Entity
public class MLModel {
    private Long id;
    private String modelName;           // 모델명
    private String description;         // 모델 설명
    private ModelType modelType;        // CLASSIFICATION, REGRESSION, CLUSTERING
    private String algorithm;           // 알고리즘 (RandomForest, XGBoost 등)
    
    // 모델 메타데이터
    private String version;             // 모델 버전
    private ModelStatus status;         // TRAINING, DEPLOYED, RETIRED
    private String framework;           // 프레임워크 (scikit-learn, TensorFlow 등)
    private String[] inputFeatures;     // 입력 피처 목록
    private String targetVariable;      // 타겟 변수
    
    // 성능 지표
    private Double accuracy;            // 정확도
    private Double precision;           // 정밀도
    private Double recall;              // 재현율
    private Double f1Score;             // F1 점수
    private Double auc;                 // AUC 점수
    
    // 배포 정보
    private String deploymentEndpoint;  // 배포 엔드포인트
    private LocalDateTime deployedAt;   // 배포일시
    private String deployedBy;          // 배포자
    private Integer predictionCount = 0; // 예측 수행 횟수
    
    // 데이터 계보
    private String[] trainingDatasets;  // 훈련 데이터셋 목록
    private String[] validationDatasets; // 검증 데이터셋 목록
    private LocalDateTime lastTrainedAt; // 최종 훈련일시
    
    // 거버넌스
    private String owner;               // 모델 소유자
    private String[] tags;              // 태그 배열
    private Boolean isApproved = false; // 승인 여부
    private String approvalComment;     // 승인 의견
}
```

### 4. ApiEndpoint (API 엔드포인트)

```java
@Entity
public class ApiEndpoint {
    private Long id;
    private String endpointName;        // 엔드포인트명
    private String description;         // 설명
    private String path;                // API 경로
    private HttpMethod method;          // HTTP 메서드
    private String baseUrl;             // 기본 URL
    
    // API 메타데이터
    private String version;             // API 버전
    private ApiStatus status;           // ACTIVE, DEPRECATED, RETIRED
    private String[] tags;              // 태그 배열
    private String category;            // 카테고리
    
    // 문서화
    private String requestSchema;       // 요청 스키마 (JSON Schema)
    private String responseSchema;      // 응답 스키마 (JSON Schema)
    private String[] requestExample;    // 요청 예시
    private String[] responseExample;   // 응답 예시
    
    // 사용성 지표
    private Integer callCount = 0;      // 호출 횟수
    private Double avgResponseTime;     // 평균 응답 시간
    private Double successRate;         // 성공률
    private LocalDateTime lastCalled;   // 최종 호출 시간
    
    // 보안 & 인증
    private AuthType authType;          // NONE, API_KEY, OAUTH2, JWT
    private String[] requiredScopes;    // 필요한 권한 범위
    private RateLimit rateLimit;        // 속도 제한
    
    // 의존성
    private String[] dependencies;      // 의존하는 다른 API들
    private String[] consumers;         // 이 API를 사용하는 서비스들
}
```

## 🔍 검색 & 발견 기능

### 1. 통합 검색

```java
@RestController
public class DataCatalogController {
    
    // 통합 검색 (모든 데이터 자산)
    @GetMapping("/api/catalog/search")
    public SearchResponse<DataAsset> searchDataAssets(
            @RequestParam String query,
            @RequestParam(required = false) String[] types,
            @RequestParam(required = false) String[] categories,
            @RequestParam(required = false) String[] tags,
            @RequestParam(required = false) SensitivityLevel sensitivityLevel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        return catalogService.searchDataAssets(
            query, types, categories, tags, sensitivityLevel, page, size
        );
    }
}
```

### 2. 고급 필터링

```java
// 데이터 테이블 고급 검색
@GetMapping("/api/catalog/tables/search")
public SearchResponse<DataTable> searchTables(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String schema,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) DataType dataType,
        @RequestParam(required = false) SensitivityLevel sensitivityLevel,
        @RequestParam(required = false) Double minQualityScore,
        @RequestParam(required = false) Boolean isPii,
        @RequestParam(required = false) String owner,
        @RequestParam(defaultValue = "lastUpdated") String sortBy,
        @RequestParam(defaultValue = "desc") String sortOrder,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    
    return catalogService.searchTables(/* parameters */);
}
```

### 3. 추천 시스템

```java
// 관련 데이터 자산 추천
@GetMapping("/api/catalog/recommendations/{assetId}")
public List<DataAsset> getRecommendations(
        @PathVariable Long assetId,
        @RequestParam(defaultValue = "5") int limit) {
    
    return recommendationService.getRelatedAssets(assetId, limit);
}

// 사용자 맞춤 추천
@GetMapping("/api/catalog/recommendations/personalized")
public List<DataAsset> getPersonalizedRecommendations(
        @RequestParam Long userId,
        @RequestParam(defaultValue = "10") int limit) {
    
    return recommendationService.getPersonalizedRecommendations(userId, limit);
}
```

## 📈 데이터 품질 관리

### 1. 품질 지표 계산

```java
@Service
public class DataQualityService {
    
    public DataQualityReport calculateQualityScore(Long tableId) {
        DataTable table = dataTableRepository.findById(tableId)
            .orElseThrow(() -> new EntityNotFoundException("Table not found"));
        
        // 완전성 검사 (NULL 값 비율)
        double completeness = calculateCompleteness(table);
        
        // 정확성 검사 (데이터 타입, 형식 검증)
        double accuracy = calculateAccuracy(table);
        
        // 일관성 검사 (중복, 참조 무결성)
        double consistency = calculateConsistency(table);
        
        // 적시성 검사 (데이터 신선도)
        double timeliness = calculateTimeliness(table);
        
        // 종합 품질 점수 계산
        double overallScore = (completeness * 0.3) + (accuracy * 0.3) + 
                             (consistency * 0.2) + (timeliness * 0.2);
        
        return DataQualityReport.builder()
            .tableId(tableId)
            .completeness(completeness)
            .accuracy(accuracy)
            .consistency(consistency)
            .timeliness(timeliness)
            .overallScore(overallScore)
            .build();
    }
}
```

### 2. 품질 모니터링

```java
// 품질 지표 조회
@GetMapping("/api/catalog/tables/{id}/quality")
public DataQualityReport getQualityReport(@PathVariable Long id) {
    return dataQualityService.getQualityReport(id);
}

// 품질 트렌드 분석
@GetMapping("/api/catalog/quality/trend")
public List<QualityTrend> getQualityTrend(
        @RequestParam(required = false) String schema,
        @RequestParam(defaultValue = "30") int days) {
    
    return dataQualityService.getQualityTrend(schema, days);
}
```

## 🔗 데이터 계보 (Data Lineage)

### 1. 계보 추적

```java
@Entity
public class DataLineage {
    private Long id;
    private String sourceAssetId;       // 소스 자산 ID
    private String targetAssetId;       // 타겟 자산 ID
    private LineageType lineageType;    // DERIVES_FROM, FEEDS_INTO, TRANSFORMS
    private String transformationLogic; // 변환 로직
    private String[] transformationSteps; // 변환 단계
    private LocalDateTime createdAt;
    private String createdBy;
}

// 계보 조회 API
@GetMapping("/api/catalog/lineage/{assetId}")
public LineageGraph getDataLineage(
        @PathVariable String assetId,
        @RequestParam(defaultValue = "BOTH") String direction,
        @RequestParam(defaultValue = "3") int depth) {
    
    return lineageService.getLineageGraph(assetId, direction, depth);
}
```

### 2. 영향도 분석

```java
// 변경 영향도 분석
@GetMapping("/api/catalog/impact-analysis/{assetId}")
public ImpactAnalysisResult analyzeImpact(@PathVariable String assetId) {
    return lineageService.analyzeDownstreamImpact(assetId);
}

// 의존성 분석
@GetMapping("/api/catalog/dependency-analysis/{assetId}")
public DependencyAnalysisResult analyzeDependency(@PathVariable String assetId) {
    return lineageService.analyzeUpstreamDependency(assetId);
}
```

## 🏷️ 메타데이터 관리

### 1. 비즈니스 용어집

```java
@Entity
public class BusinessGlossary {
    private Long id;
    private String term;                // 용어
    private String definition;          // 정의
    private String businessDefinition;  // 비즈니스 정의
    private String[] synonyms;          // 동의어
    private String[] relatedTerms;      // 관련 용어
    private String category;            // 카테고리
    private String owner;               // 용어 소유자
    private GlossaryStatus status;      // DRAFT, APPROVED, DEPRECATED
}

// 용어집 검색
@GetMapping("/api/catalog/glossary/search")
public List<BusinessGlossary> searchGlossary(
        @RequestParam String query,
        @RequestParam(required = false) String category) {
    
    return glossaryService.searchTerms(query, category);
}
```

### 2. 태그 관리

```java
@Entity
public class Tag {
    private Long id;
    private String name;                // 태그명
    private String description;         // 설명
    private String color;               // 색상 코드
    private TagType type;               // BUSINESS, TECHNICAL, SECURITY
    private Integer usageCount = 0;     // 사용 횟수
}

// 태그 자동 추천
@GetMapping("/api/catalog/tags/suggestions")
public List<Tag> suggestTags(
        @RequestParam String assetId,
        @RequestParam(defaultValue = "5") int limit) {
    
    return tagService.suggestTags(assetId, limit);
}
```

## 📊 사용성 분석 & 통계

### 1. 사용 패턴 분석

```java
@Service
public class UsageAnalyticsService {
    
    // 인기 데이터 자산 조회
    @GetMapping("/api/catalog/analytics/popular")
    public List<DataAsset> getPopularAssets(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "10") int limit) {
        
        return analyticsService.getPopularAssets(days, limit);
    }
    
    // 사용자별 접근 패턴
    @GetMapping("/api/catalog/analytics/user-patterns/{userId}")
    public UserAccessPattern getUserAccessPattern(@PathVariable Long userId) {
        return analyticsService.getUserAccessPattern(userId);
    }
}
```

### 2. 데이터 자산 현황

```java
// 카탈로그 대시보드 데이터
@GetMapping("/api/catalog/dashboard")
public CatalogDashboardData getDashboardData() {
    return CatalogDashboardData.builder()
        .totalAssets(catalogService.getTotalAssetCount())
        .assetsByType(catalogService.getAssetCountByType())
        .qualityDistribution(catalogService.getQualityDistribution())
        .recentlyAdded(catalogService.getRecentlyAddedAssets(10))
        .topSearches(catalogService.getTopSearchQueries(10))
        .build();
}
```

## 🔐 보안 & 거버넌스

### 1. 접근 제어

```java
@Entity
public class DataAssetAccess {
    private Long id;
    private String assetId;             // 데이터 자산 ID
    private Long userId;                // 사용자 ID
    private AccessType accessType;      // READ, WRITE, ADMIN
    private LocalDateTime grantedAt;    // 권한 부여일
    private LocalDateTime expiresAt;    // 권한 만료일
    private String grantedBy;           // 권한 부여자
    private String reason;              // 권한 부여 사유
}

// 접근 권한 확인
@GetMapping("/api/catalog/access-check/{assetId}")
public AccessCheckResult checkAccess(
        @PathVariable String assetId,
        @RequestParam Long userId) {
    
    return accessControlService.checkAccess(assetId, userId);
}
```

### 2. 감사 로그

```java
@Entity
public class CatalogAuditLog {
    private Long id;
    private String assetId;             // 데이터 자산 ID
    private Long userId;                // 사용자 ID
    private AuditAction action;         // VIEW, DOWNLOAD, MODIFY, DELETE
    private String details;             // 상세 정보
    private LocalDateTime timestamp;    // 시간
    private String ipAddress;           // IP 주소
    private String userAgent;           // 사용자 에이전트
}

// 감사 로그 조회
@GetMapping("/api/catalog/audit-logs")
public Page<CatalogAuditLog> getAuditLogs(
        @RequestParam(required = false) String assetId,
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) AuditAction action,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    
    return auditService.getAuditLogs(assetId, userId, action, page, size);
}
```

## 📊 API 엔드포인트 전체 목록

### 데이터 테이블 관리
```http
GET    /api/catalog/tables              # 테이블 목록 조회
GET    /api/catalog/tables/{id}         # 테이블 상세 조회
GET    /api/catalog/tables/recent       # 최근 업데이트된 테이블
GET    /api/catalog/tables/popular      # 인기 테이블
GET    /api/catalog/tables/search       # 테이블 검색
POST   /api/catalog/tables              # 신규 테이블 등록
PUT    /api/catalog/tables/{id}         # 테이블 정보 수정
PUT    /api/catalog/tables/{id}/favorite # 즐겨찾기 토글
DELETE /api/catalog/tables/{id}         # 테이블 삭제
```

### 리포트 관리
```http
GET    /api/catalog/reports             # 리포트 목록 조회
GET    /api/catalog/reports/{id}        # 리포트 상세 조회
GET    /api/catalog/reports/recent      # 최근 리포트
POST   /api/catalog/reports             # 신규 리포트 등록
PUT    /api/catalog/reports/{id}        # 리포트 수정
DELETE /api/catalog/reports/{id}        # 리포트 삭제
```

### ML 모델 관리
```http
GET    /api/catalog/models              # 모델 목록 조회
GET    /api/catalog/models/{id}         # 모델 상세 조회
GET    /api/catalog/models/deployed     # 배포된 모델 목록
POST   /api/catalog/models              # 신규 모델 등록
PUT    /api/catalog/models/{id}         # 모델 정보 수정
POST   /api/catalog/models/{id}/deploy  # 모델 배포
POST   /api/catalog/models/{id}/retire  # 모델 사용중지
```

### API 엔드포인트 관리
```http
GET    /api/catalog/apis                # API 목록 조회
GET    /api/catalog/apis/{id}           # API 상세 조회
POST   /api/catalog/apis                # 신규 API 등록
PUT    /api/catalog/apis/{id}           # API 정보 수정
DELETE /api/catalog/apis/{id}           # API 삭제
```

### 검색 & 발견
```http
GET    /api/catalog/search              # 통합 검색
GET    /api/catalog/recommendations/{id} # 관련 자산 추천
GET    /api/catalog/recommendations/personalized # 개인화 추천
```

### 데이터 품질
```http
GET    /api/catalog/tables/{id}/quality # 품질 리포트 조회
GET    /api/catalog/quality/trend       # 품질 트렌드 분석
POST   /api/catalog/quality/scan/{id}   # 품질 스캔 실행
```

### 데이터 계보
```http
GET    /api/catalog/lineage/{id}        # 데이터 계보 조회
GET    /api/catalog/impact-analysis/{id} # 영향도 분석
GET    /api/catalog/dependency-analysis/{id} # 의존성 분석
```

### 메타데이터 관리
```http
GET    /api/catalog/glossary            # 비즈니스 용어집
GET    /api/catalog/glossary/search     # 용어 검색
GET    /api/catalog/tags                # 태그 목록
GET    /api/catalog/tags/suggestions    # 태그 추천
```

### 분석 & 통계
```http
GET    /api/catalog/analytics/popular   # 인기 자산
GET    /api/catalog/analytics/usage     # 사용 통계
GET    /api/catalog/dashboard           # 카탈로그 대시보드
```

## 🚀 향후 개발 계획

### Phase 1 - 고급 메타데이터 관리
- [ ] 자동 메타데이터 수집 (Schema Crawler)
- [ ] 데이터 프로파일링 자동화
- [ ] 스마트 태깅 (AI 기반 자동 태그 생성)
- [ ] 데이터 카탈로그 API 표준화

### Phase 2 - 고급 품질 관리
- [ ] 실시간 데이터 품질 모니터링
- [ ] 품질 규칙 엔진 (Custom Rules)
- [ ] 데이터 품질 알림 시스템
- [ ] 품질 개선 제안 시스템

### Phase 3 - 고급 거버넌스
- [ ] 데이터 분류 자동화 (PII 탐지)
- [ ] 데이터 보존 정책 자동 적용
- [ ] 규정 준수 모니터링 (GDPR, CCPA)
- [ ] 데이터 사용 승인 워크플로우 통합

### Phase 4 - AI/ML 통합
- [ ] 자연어 기반 데이터 검색
- [ ] 데이터 사용 패턴 예측
- [ ] 이상 데이터 자동 탐지
- [ ] 데이터 자산 가치 평가 모델

---

**문서 버전**: v1.0  
**최종 업데이트**: 2024년 1월 20일  
**담당자**: Data Governance Team