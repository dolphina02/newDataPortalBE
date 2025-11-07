# 🧱 Databricks 통합 시스템 (Databricks Integration System)

## 🎯 개요

LINA Data Portal의 Databricks 통합 시스템은 Databricks 플랫폼과의 원활한 연동을 통해 쿼리 실행, 클러스터 관리, 토큰 보안 관리 등의 기능을 제공합니다.

## 🏗️ 시스템 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  QueryService   │    │ DatabricksApi   │    │ TokenEncryption │
│   (쿼리 실행)    │────│    Client       │────│    Service      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   UserToken     │    │ DatabricksQuery │    │ SecurityPolicy  │
│  (사용자 토큰)   │    │   Result        │    │   (보안 정책)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Databricks Platform                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │ SQL Engine  │  │ Clusters    │  │    Notebooks & Jobs     │ │
│  │             │  │ Management  │  │                         │ │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## 📊 데이터 모델

### 1. UserToken (사용자 토큰)

```java
@Entity
public class UserToken {
    private Long id;
    private Long userId;                // 사용자 ID
    private String tokenName;           // 토큰 이름
    private String encryptedToken;      // 암호화된 토큰
    private String tokenHash;           // 토큰 해시 (검증용)
    private TokenType tokenType;        // DATABRICKS, AWS, AZURE, GCP
    private TokenStatus status;         // ACTIVE, EXPIRED, REVOKED
    
    // 메타데이터
    private String description;         // 토큰 설명
    private String[] scopes;            // 토큰 권한 범위
    private LocalDateTime createdAt;    // 생성일시
    private LocalDateTime expiresAt;    // 만료일시
    private LocalDateTime lastUsedAt;   // 최종 사용일시
    
    // 보안 정보
    private String createdByIp;         // 생성 IP
    private String lastUsedIp;          // 최종 사용 IP
    private Integer usageCount = 0;     // 사용 횟수
    private Boolean isDefault = false;  // 기본 토큰 여부
    
    // 암호화 정보
    private String encryptionAlgorithm; // 암호화 알고리즘
    private String keyVersion;          // 키 버전
}
```

### 2. DatabricksQueryResult (쿼리 결과)

```java
public class DatabricksQueryResult {
    private String queryId;             // 쿼리 ID
    private String sql;                 // 실행된 SQL
    private QueryStatus status;         // RUNNING, COMPLETED, FAILED, CANCELLED
    private Long executionTime;         // 실행 시간 (밀리초)
    
    // 결과 데이터
    private List<DatabricksColumn> columns; // 컬럼 정보
    private List<List<Object>> rows;    // 결과 행 데이터
    private Integer totalRows;          // 전체 행 수
    private Integer returnedRows;       // 반환된 행 수
    
    // 메타데이터
    private String clusterId;           // 클러스터 ID
    private String warehouseId;         // 웨어하우스 ID
    private LocalDateTime startedAt;    // 시작 시간
    private LocalDateTime completedAt;  // 완료 시간
    
    // 오류 정보
    private String errorMessage;        // 오류 메시지
    private String errorCode;           // 오류 코드
    private String stackTrace;          // 스택 트레이스
    
    // 성능 정보
    private Long bytesScanned;          // 스캔된 바이트 수
    private Long bytesReturned;         // 반환된 바이트 수
    private Double cpuTime;             // CPU 시간
    private Double memoryUsage;         // 메모리 사용량
}
```

### 3. DatabricksColumn (컬럼 정보)

```java
public class DatabricksColumn {
    private String name;                // 컬럼명
    private String type;                // 데이터 타입
    private String comment;             // 컬럼 설명
    private Boolean nullable;           // NULL 허용 여부
    private Object defaultValue;        // 기본값
    private Integer precision;          // 정밀도 (숫자형)
    private Integer scale;              // 스케일 (숫자형)
    private Integer maxLength;          // 최대 길이 (문자형)
}
```

## 🔐 토큰 암호화 시스템

### 1. 토큰 암호화 서비스

```java
@Service
public class TokenEncryptionService {
    
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    
    @Value("${app.encryption.key}")
    private String encryptionKey;
    
    /**
     * 토큰 암호화
     */
    public String encryptToken(String plainToken) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(
                encryptionKey.getBytes(), "AES"
            );
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom.getInstanceStrong().nextBytes(iv);
            
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
            
            byte[] encryptedData = cipher.doFinal(plainToken.getBytes());
            
            // IV + 암호화된 데이터를 Base64로 인코딩
            byte[] encryptedWithIv = new byte[GCM_IV_LENGTH + encryptedData.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedData, 0, encryptedWithIv, GCM_IV_LENGTH, encryptedData.length);
            
            return Base64.getEncoder().encodeToString(encryptedWithIv);
            
        } catch (Exception e) {
            throw new TokenEncryptionException("Failed to encrypt token", e);
        }
    }
    
    /**
     * 토큰 복호화
     */
    public String decryptToken(String encryptedToken) {
        try {
            byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedToken);
            
            // IV와 암호화된 데이터 분리
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedData = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
            
            System.arraycopy(encryptedWithIv, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedWithIv, GCM_IV_LENGTH, encryptedData, 0, encryptedData.length);
            
            SecretKeySpec keySpec = new SecretKeySpec(
                encryptionKey.getBytes(), "AES"
            );
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
            
            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData);
            
        } catch (Exception e) {
            throw new TokenDecryptionException("Failed to decrypt token", e);
        }
    }
    
    /**
     * 토큰 해시 생성 (검증용)
     */
    public String generateTokenHash(String plainToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainToken.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
```

### 2. 토큰 관리 서비스

```java
@Service
public class UserTokenService {
    
    private final UserTokenRepository userTokenRepository;
    private final TokenEncryptionService encryptionService;
    
    /**
     * 토큰 저장
     */
    @Transactional
    public UserToken saveToken(Long userId, String tokenName, String plainToken, 
                              TokenType tokenType, String description) {
        
        // 기존 동일 이름 토큰 비활성화
        userTokenRepository.deactivateTokensByName(userId, tokenName);
        
        // 토큰 암호화
        String encryptedToken = encryptionService.encryptToken(plainToken);
        String tokenHash = encryptionService.generateTokenHash(plainToken);
        
        UserToken userToken = UserToken.builder()
            .userId(userId)
            .tokenName(tokenName)
            .encryptedToken(encryptedToken)
            .tokenHash(tokenHash)
            .tokenType(tokenType)
            .status(TokenStatus.ACTIVE)
            .description(description)
            .createdAt(LocalDateTime.now())
            .createdByIp(getCurrentUserIp())
            .build();
        
        return userTokenRepository.save(userToken);
    }
    
    /**
     * 토큰 조회 및 복호화
     */
    public String getDecryptedToken(Long userId, String tokenName) {
        UserToken userToken = userTokenRepository
            .findByUserIdAndTokenNameAndStatus(userId, tokenName, TokenStatus.ACTIVE)
            .orElseThrow(() -> new TokenNotFoundException("Token not found"));
        
        // 토큰 만료 확인
        if (userToken.getExpiresAt() != null && 
            userToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token has expired");
        }
        
        // 사용 통계 업데이트
        updateTokenUsage(userToken);
        
        return encryptionService.decryptToken(userToken.getEncryptedToken());
    }
    
    /**
     * 토큰 검증
     */
    public boolean validateToken(Long userId, String tokenName, String plainToken) {
        try {
            UserToken userToken = userTokenRepository
                .findByUserIdAndTokenNameAndStatus(userId, tokenName, TokenStatus.ACTIVE)
                .orElse(null);
            
            if (userToken == null) {
                return false;
            }
            
            String expectedHash = encryptionService.generateTokenHash(plainToken);
            return userToken.getTokenHash().equals(expectedHash);
            
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return false;
        }
    }
}
```

## 🔌 Databricks API 클라이언트

### 1. API 클라이언트 구현

```java
@Service
public class DatabricksApiClient {
    
    private final RestTemplate restTemplate;
    private final UserTokenService userTokenService;
    
    @Value("${databricks.workspace.url}")
    private String workspaceUrl;
    
    /**
     * SQL 쿼리 실행
     */
    public DatabricksQueryResult executeQuery(Long userId, String sql, String warehouseId) {
        String token = userTokenService.getDecryptedToken(userId, "databricks");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        
        Map<String, Object> requestBody = Map.of(
            "statement", sql,
            "warehouse_id", warehouseId,
            "wait_timeout", "30s",
            "row_limit", 1000
        );
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            String url = workspaceUrl + "/api/2.0/sql/statements";
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            return mapToDatabricksQueryResult(response.getBody());
            
        } catch (HttpClientErrorException e) {
            throw new DatabricksApiException("Query execution failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * 클러스터 목록 조회
     */
    public List<DatabricksCluster> getClusters(Long userId) {
        String token = userTokenService.getDecryptedToken(userId, "databricks");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        
        HttpEntity<Void> request = new HttpEntity<>(headers);
        
        try {
            String url = workspaceUrl + "/api/2.0/clusters/list";
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, request, Map.class
            );
            
            return mapToDatabricksClusters(response.getBody());
            
        } catch (HttpClientErrorException e) {
            throw new DatabricksApiException("Failed to fetch clusters: " + e.getMessage(), e);
        }
    }
    
    /**
     * 웨어하우스 목록 조회
     */
    public List<DatabricksWarehouse> getWarehouses(Long userId) {
        String token = userTokenService.getDecryptedToken(userId, "databricks");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        
        HttpEntity<Void> request = new HttpEntity<>(headers);
        
        try {
            String url = workspaceUrl + "/api/2.0/sql/warehouses";
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, request, Map.class
            );
            
            return mapToDatabricksWarehouses(response.getBody());
            
        } catch (HttpClientErrorException e) {
            throw new DatabricksApiException("Failed to fetch warehouses: " + e.getMessage(), e);
        }
    }
}
```

### 2. 쿼리 실행 서비스

```java
@Service
public class QueryService {
    
    private final DatabricksApiClient databricksClient;
    private final QueryHistoryRepository queryHistoryRepository;
    
    /**
     * SQL 쿼리 실행
     */
    @Transactional
    public DatabricksQueryResult executeQuery(Long userId, String sql, String warehouseId) {
        
        // 쿼리 검증
        validateQuery(sql);
        
        // 쿼리 실행
        DatabricksQueryResult result = databricksClient.executeQuery(userId, sql, warehouseId);
        
        // 쿼리 히스토리 저장
        saveQueryHistory(userId, sql, warehouseId, result);
        
        return result;
    }
    
    /**
     * 쿼리 검증
     */
    private void validateQuery(String sql) {
        // SQL 인젝션 방지
        if (containsSqlInjectionPatterns(sql)) {
            throw new SecurityException("Potentially dangerous SQL detected");
        }
        
        // 허용되지 않은 명령어 확인
        List<String> forbiddenKeywords = Arrays.asList(
            "DROP", "DELETE", "TRUNCATE", "ALTER", "CREATE", "INSERT", "UPDATE"
        );
        
        String upperSql = sql.toUpperCase();
        for (String keyword : forbiddenKeywords) {
            if (upperSql.contains(keyword)) {
                throw new SecurityException("Forbidden SQL keyword: " + keyword);
            }
        }
    }
    
    /**
     * 쿼리 히스토리 저장
     */
    private void saveQueryHistory(Long userId, String sql, String warehouseId, 
                                 DatabricksQueryResult result) {
        QueryHistory history = QueryHistory.builder()
            .userId(userId)
            .sql(sql)
            .warehouseId(warehouseId)
            .status(result.getStatus())
            .executionTime(result.getExecutionTime())
            .rowCount(result.getReturnedRows())
            .bytesScanned(result.getBytesScanned())
            .executedAt(LocalDateTime.now())
            .build();
        
        queryHistoryRepository.save(history);
    }
}
```

## 🎨 Text-to-SQL 서비스

### 1. 자연어 쿼리 변환

```java
@Service
public class TextToSqlService {
    
    private final OpenAIClient openAIClient;
    private final QueryService queryService;
    
    /**
     * 자연어를 SQL로 변환
     */
    public TextToSqlResult convertToSql(Long userId, String naturalLanguageQuery, 
                                       String schemaContext) {
        
        // 프롬프트 생성
        String prompt = buildPrompt(naturalLanguageQuery, schemaContext);
        
        // OpenAI API 호출
        String generatedSql = callOpenAI(prompt);
        
        // SQL 검증 및 정제
        String validatedSql = validateAndCleanSql(generatedSql);
        
        return TextToSqlResult.builder()
            .originalQuery(naturalLanguageQuery)
            .generatedSql(validatedSql)
            .confidence(calculateConfidence(naturalLanguageQuery, validatedSql))
            .explanation(generateExplanation(validatedSql))
            .build();
    }
    
    /**
     * 프롬프트 생성
     */
    private String buildPrompt(String query, String schemaContext) {
        return String.format("""
            You are a SQL expert. Convert the following natural language query to SQL.
            
            Schema Context:
            %s
            
            Natural Language Query:
            %s
            
            Requirements:
            - Generate only SELECT statements
            - Use proper table and column names from the schema
            - Include appropriate WHERE clauses and JOINs
            - Optimize for performance
            - Return only the SQL query without explanations
            
            SQL Query:
            """, schemaContext, query);
    }
    
    /**
     * 쿼리 실행 및 결과 반환
     */
    public TextToSqlExecutionResult executeNaturalLanguageQuery(
            Long userId, String naturalLanguageQuery, String warehouseId) {
        
        // 스키마 컨텍스트 조회
        String schemaContext = getSchemaContext(warehouseId);
        
        // SQL 변환
        TextToSqlResult sqlResult = convertToSql(userId, naturalLanguageQuery, schemaContext);
        
        // SQL 실행
        DatabricksQueryResult queryResult = queryService.executeQuery(
            userId, sqlResult.getGeneratedSql(), warehouseId
        );
        
        return TextToSqlExecutionResult.builder()
            .textToSqlResult(sqlResult)
            .queryResult(queryResult)
            .executedAt(LocalDateTime.now())
            .build();
    }
}
```

### 2. 스키마 컨텍스트 관리

```java
@Service
public class SchemaContextService {
    
    private final DatabricksApiClient databricksClient;
    
    /**
     * 스키마 정보 조회
     */
    public String getSchemaContext(String warehouseId) {
        // 테이블 목록 조회
        List<String> tables = getTableList(warehouseId);
        
        StringBuilder context = new StringBuilder();
        context.append("Available Tables and Columns:\n\n");
        
        for (String table : tables) {
            // 각 테이블의 컬럼 정보 조회
            List<DatabricksColumn> columns = getTableColumns(table, warehouseId);
            
            context.append(String.format("Table: %s\n", table));
            context.append("Columns:\n");
            
            for (DatabricksColumn column : columns) {
                context.append(String.format("  - %s (%s)", 
                    column.getName(), column.getType()));
                
                if (column.getComment() != null) {
                    context.append(String.format(" - %s", column.getComment()));
                }
                context.append("\n");
            }
            context.append("\n");
        }
        
        return context.toString();
    }
}
```

## 📊 API 엔드포인트

### 토큰 관리 API
```http
# 토큰 저장
POST /api/user-tokens
Content-Type: application/json

{
  "tokenName": "databricks-main",
  "token": "dapi1234567890abcdef",
  "tokenType": "DATABRICKS",
  "description": "Main Databricks access token",
  "expiresAt": "2024-12-31T23:59:59"
}

# 토큰 목록 조회
GET /api/user-tokens

# 토큰 삭제
DELETE /api/user-tokens/{tokenId}

# 토큰 검증
POST /api/user-tokens/validate
```

### 쿼리 실행 API
```http
# SQL 쿼리 실행
POST /api/query/execute
Content-Type: application/json

{
  "sql": "SELECT * FROM sales_data WHERE date >= '2024-01-01' LIMIT 100",
  "warehouseId": "warehouse-123"
}

# 쿼리 히스토리 조회
GET /api/query/history?page=0&size=20

# 쿼리 결과 다운로드
GET /api/query/{queryId}/download?format=csv
```

### Text-to-SQL API
```http
# 자연어 쿼리 변환
POST /api/text-to-sql/convert
Content-Type: application/json

{
  "query": "지난 달 매출이 가장 높은 상위 10개 제품을 보여줘",
  "warehouseId": "warehouse-123"
}

# 자연어 쿼리 실행
POST /api/text-to-sql/execute
Content-Type: application/json

{
  "query": "고객별 총 주문 금액을 내림차순으로 정렬해서 보여줘",
  "warehouseId": "warehouse-123"
}
```

### Databricks 리소스 API
```http
# 클러스터 목록 조회
GET /api/databricks/clusters

# 웨어하우스 목록 조회
GET /api/databricks/warehouses

# 데이터베이스 목록 조회
GET /api/databricks/databases

# 테이블 목록 조회
GET /api/databricks/tables?database=default

# 테이블 스키마 조회
GET /api/databricks/tables/{tableName}/schema
```

## 🔧 설정

### 1. Databricks 설정

```yaml
# application.yml
databricks:
  workspace:
    url: ${DATABRICKS_WORKSPACE_URL}
  default:
    warehouse-id: ${DATABRICKS_DEFAULT_WAREHOUSE_ID}
  
  # API 설정
  api:
    timeout: 30s
    retry:
      max-attempts: 3
      backoff-delay: 1s
  
  # 쿼리 제한
  query:
    max-rows: 10000
    timeout: 300s
    allowed-statements:
      - SELECT
      - SHOW
      - DESCRIBE
      - EXPLAIN
```

### 2. 암호화 설정

```yaml
# application.yml
app:
  encryption:
    key: ${ENCRYPTION_KEY} # 32바이트 키
    algorithm: AES/GCM/NoPadding
  
  security:
    token:
      max-per-user: 5
      default-expiry-days: 90
      rotation-warning-days: 7
```

## 🚀 향후 개발 계획

### Phase 1 - 고급 쿼리 기능
- [ ] 쿼리 최적화 제안 시스템
- [ ] 실행 계획 분석 및 시각화
- [ ] 쿼리 성능 모니터링
- [ ] 자동 인덱스 추천

### Phase 2 - 고급 보안 기능
- [ ] 토큰 자동 로테이션
- [ ] 세밀한 권한 제어 (Row/Column Level)
- [ ] 쿼리 결과 마스킹
- [ ] 감사 로그 고도화

### Phase 3 - AI/ML 통합
- [ ] 고급 Text-to-SQL (복잡한 조인, 서브쿼리)
- [ ] 쿼리 의도 분석 및 제안
- [ ] 자동 데이터 프로파일링
- [ ] 이상 쿼리 패턴 탐지

### Phase 4 - 고급 시각화
- [ ] 실시간 쿼리 결과 시각화
- [ ] 인터랙티브 데이터 탐색
- [ ] 자동 차트 생성
- [ ] 대시보드 자동 생성

---

**문서 버전**: v1.0  
**최종 업데이트**: 2024년 1월 20일  
**담당자**: Data Platform Team