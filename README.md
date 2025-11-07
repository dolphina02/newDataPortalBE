# 🚀 LINA Data Portal Backend

LINA Data Portal의 백엔드 API 서버입니다. Spring Boot 기반으로 구축된 RESTful API를 제공하여 보험업계 특화 데이터 분석 플랫폼을 지원합니다.

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-6DB33F?style=flat-square&logo=spring-boot)
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk)
![Maven](https://img.shields.io/badge/Maven-3.9.0-C71A36?style=flat-square&logo=apache-maven)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-336791?style=flat-square&logo=postgresql)
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-8.0-005571?style=flat-square&logo=elasticsearch)

## 🎯 프로젝트 개요

이 백엔드 서버는 LINA Data Portal 프론트엔드와 연동하여 다음과 같은 기능을 제공합니다:

- **Dashboard Store API**: 대시보드 템플릿 관리 및 설치
- **승인관리 API**: 배포/구독/데이터 접근 승인 워크플로우
- **통합 검색 API**: 전체 리소스 통합 검색
- **Data Catalog API**: 데이터 카탈로그 및 계보 관리 (향후 구현)
- **Producer360 API**: 통합 인사이트 및 KPI (향후 구현)

## ✨ 주요 기능

### 🏪 Dashboard Store
- 대시보드 템플릿 CRUD 작업
- 카테고리별 대시보드 분류
- 인기도 및 평점 관리
- 설치 횟수 추적

### ✅ 승인관리 시스템 (Enhanced)
- **다단계 승인 워크플로우**: 유연한 승인 라인 템플릿 시스템
- **승인 대상 오브젝트**: 데이터셋, 대시보드, 리포트, 클러스터 등 다양한 리소스 지원
- **사용 기간 관리**: 기본 3개월, 민감도별 차등 적용 (임시:1일, 민감정보:1개월)
- **보안 정책 연동**: 마스킹 정책, 감사 로그, 접근 범위 제어
- **사용자 참조 정규화**: User 엔티티 기반 정규화된 데이터 관리

### 🔍 통합 검색 & STT 분석
- 키워드 기반 전체 리소스 검색
- **STT 키워드 검색**: Elasticsearch 기반 음성 인식 데이터 분석
- 보험업계 특화 키워드 분석 및 통계
- 실시간 상담 품질 모니터링

### 📊 Data Catalog & ML Model Management
- 데이터 테이블 카탈로그 관리
- ML 모델 생명주기 관리
- API 엔드포인트 탐색 및 문서화
- 리포트 관리 시스템

### 🔐 보안 & 토큰 관리
- Databricks 토큰 암호화 저장
- 사용자별 토큰 관리
- 보안 정책 및 마스킹 규칙

## 🛠️ 기술 스택

### Core Framework
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Build Tool**: Maven
- **ORM**: Spring Data JPA
- **Security**: Spring Security

### Database & Search
- **Database**: H2 (개발), PostgreSQL (프로덕션)
- **Search Engine**: Elasticsearch 8.0 (STT 데이터 분석)
- **Connection Pool**: HikariCP

### External Integrations
- **Databricks**: REST API 연동 (쿼리 실행, 클러스터 관리)
- **Text-to-SQL**: AI 기반 자연어 쿼리 변환
- **Token Encryption**: AES-256 암호화

### Documentation & Monitoring
- **API Documentation**: Swagger/OpenAPI 3.0
- **Logging**: SLF4J + Logback
- **Health Check**: Spring Boot Actuator

## 🚀 시작하기

### 필수 요구사항
- Java 17 이상
- Maven 3.6 이상

### 설치 및 실행

```bash
# 저장소 클론
git clone <repository-url>
cd lina-data-portal-backend

# 의존성 설치 및 빌드
mvn clean install

# 개발 서버 실행
mvn spring-boot:run

# 또는 JAR 파일로 실행
java -jar target/data-portal-backend-1.0.0.jar
```

### 개발 서버
- **API 서버**: http://localhost:8080/api

#### H2 개발 환경 (기본)
- **H2 Console**: http://localhost:8080/api/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (비어있음)

#### Neon PostgreSQL 개발 환경
- **Database**: Neon PostgreSQL (클라우드)
- **Connection**: 자동 연결 (설정 완료)

## 📚 상세 문서

### 📋 시스템 개요
- **[전체 시스템 개요](docs/SYSTEM_OVERVIEW.md)** - 아키텍처, 기술 스택, 로드맵 전체 가이드

### 시스템별 상세 가이드
- **[승인관리 시스템](docs/APPROVAL_SYSTEM.md)** - 다단계 승인 워크플로우, 사용 기간 관리, 보안 정책 연동
- **[STT 키워드 검색 시스템](docs/STT_SEARCH_SYSTEM.md)** - Elasticsearch 기반 음성 인식 데이터 분석
- **[데이터 카탈로그 시스템](docs/DATA_CATALOG_SYSTEM.md)** - 메타데이터 관리, 데이터 품질, 계보 추적
- **[Databricks 통합 시스템](docs/DATABRICKS_INTEGRATION.md)** - 쿼리 실행, 토큰 보안, Text-to-SQL

### 기술 문서
- **[보안 가이드](SECURITY.md)** - 보안 정책, 인증/인가, 데이터 보호
- **[민감도 레벨 시스템](docs/SENSITIVITY_LEVEL_ENHANCEMENT.md)** - 2단계 민감도 분류 체계
- **[데이터베이스 마이그레이션](DATABASE_MIGRATION_GUIDE.md)** - 스키마 변경, 데이터 이관
- **[사용 기간 관리](USAGE_DURATION_SIMPLE_SUMMARY.md)** - 승인 사용 기간 정책

## 📁 프로젝트 구조

```
src/main/java/com/lina/dataportal/
├── DataPortalApplication.java          # 메인 애플리케이션 클래스
├── config/                            # 설정 클래스들
│   ├── SecurityConfig.java            # Spring Security 설정
│   └── DataInitializer.java           # 샘플 데이터 초기화
├── controller/                        # REST 컨트롤러
│   ├── DashboardController.java       # 대시보드 API
│   ├── ApprovalController.java        # 승인관리 API
│   └── SearchController.java          # 통합 검색 API
├── domain/                           # 도메인 모델
│   ├── dashboard/                    # 대시보드 도메인
│   │   ├── Dashboard.java            # 대시보드 엔티티
│   │   └── DashboardType.java        # 대시보드 타입 열거형
│   └── approval/                     # 승인 도메인
│       ├── Approval.java             # 승인 엔티티
│       ├── ApprovalType.java         # 승인 타입 열거형
│       ├── ApprovalStatus.java       # 승인 상태 열거형
│       └── Priority.java             # 우선순위 열거형
├── repository/                       # 데이터 접근 계층
│   ├── DashboardRepository.java      # 대시보드 리포지토리
│   └── ApprovalRepository.java       # 승인 리포지토리
└── service/                         # 비즈니스 로직 계층
    ├── DashboardService.java        # 대시보드 서비스
    └── ApprovalService.java         # 승인 서비스

src/main/resources/
└── application.yml                   # 애플리케이션 설정
```

## 🔌 API 엔드포인트

### Dashboard Store API

```http
GET    /api/dashboards              # 대시보드 목록 조회
GET    /api/dashboards/{id}         # 대시보드 상세 조회
POST   /api/dashboards              # 신규 대시보드 생성
PUT    /api/dashboards/{id}         # 대시보드 수정
DELETE /api/dashboards/{id}         # 대시보드 삭제
POST   /api/dashboards/{id}/install # 대시보드 설치
PUT    /api/dashboards/{id}/rating  # 대시보드 평가
GET    /api/dashboards/popular      # 인기 대시보드 목록
GET    /api/dashboards/top-rated    # 높은 평점 대시보드 목록
```

### My Dashboard API

```http
GET    /api/my-dashboards           # 내 구독 대시보드 목록
GET    /api/my-dashboards/favorites # 즐겨찾기 대시보드 목록
GET    /api/my-dashboards/recent    # 최근 접근한 대시보드 목록
POST   /api/my-dashboards/subscribe # 대시보드 구독
DELETE /api/my-dashboards/unsubscribe # 대시보드 구독 해제
PUT    /api/my-dashboards/reorder   # 대시보드 순서 변경
PUT    /api/my-dashboards/favorite  # 즐겨찾기 토글
PUT    /api/my-dashboards/custom-title # 사용자 정의 제목 설정
POST   /api/my-dashboards/access    # 대시보드 접근 기록
GET    /api/my-dashboards/subscription-status # 구독 상태 조회
```

### 승인관리 API

```http
# 상신 관련
GET    /api/approvals/submitted     # 내가 요청한 승인 목록
POST   /api/approvals/deploy        # 대시보드 배포 요청
POST   /api/approvals/subscribe     # 대시보드 구독 요청
POST   /api/approvals/data-access   # 데이터 접근 권한 요청

# 승인 처리
GET    /api/approvals/pending       # 승인 대기 목록
PUT    /api/approvals/{id}/approve  # 승인 처리
PUT    /api/approvals/{id}/reject   # 거절 처리
PUT    /api/approvals/{id}/next-step # 다음 단계로 이동

# 결재 완료
GET    /api/approvals/completed     # 결재 완료 목록
GET    /api/approvals/{id}          # 승인 상세 조회
```

### Data Report API

```http
GET    /api/reports                 # 리포트 목록 조회
GET    /api/reports/{id}            # 리포트 상세 조회
GET    /api/reports/recent          # 최근 리포트 목록
POST   /api/reports                 # 신규 리포트 등록
PUT    /api/reports/{id}            # 리포트 수정
DELETE /api/reports/{id}            # 리포트 삭제
```

### Data Catalog API

```http
GET    /api/catalog/tables          # 테이블 목록 조회
GET    /api/catalog/tables/{id}     # 테이블 상세 조회
GET    /api/catalog/tables/recent   # 최근 업데이트된 테이블
POST   /api/catalog/tables          # 신규 테이블 등록
PUT    /api/catalog/tables/{id}     # 테이블 정보 수정
PUT    /api/catalog/tables/{id}/favorite # 즐겨찾기 토글
DELETE /api/catalog/tables/{id}     # 테이블 삭제
GET    /api/catalog/lineage/{id}    # 데이터 계보 조회 (향후 구현)
GET    /api/catalog/quality/{id}    # 데이터 품질 메트릭 (향후 구현)
```

### ML Model Management API

```http
GET    /api/models                  # 모델 목록 조회
GET    /api/models/{id}             # 모델 상세 조회
GET    /api/models/deployed         # 배포된 모델 목록
GET    /api/models/recent           # 최근 모델 목록
POST   /api/models                  # 신규 모델 등록
PUT    /api/models/{id}             # 모델 정보 수정
POST   /api/models/{id}/deploy      # 모델 배포
POST   /api/models/{id}/retire      # 모델 사용중지
DELETE /api/models/{id}             # 모델 삭제
```

### API Explorer API

```http
GET    /api/apis                    # API 엔드포인트 목록 조회
GET    /api/apis/{id}               # API 엔드포인트 상세 조회
POST   /api/apis                    # 신규 API 등록
PUT    /api/apis/{id}               # API 정보 수정
DELETE /api/apis/{id}               # API 삭제
```

### STT 키워드 검색 API (Elasticsearch 기반)

#### 기본 검색 API
```http
GET    /api/stt/calls               # 상담 녹취 목록 조회 (고급 필터링 지원)
GET    /api/stt/calls/{callId}      # 상담 녹취 상세 조회
GET    /api/stt/calls/{callId}/segments # 상담 세그먼트 조회 (키워드 검색 지원)
GET    /api/stt/calls/{callId}/segments/at/{timeMs} # 특정 시간대 세그먼트 조회
GET    /api/stt/keywords/search     # 키워드 검색 (날짜 범위 필터)
GET    /api/stt/calls/{callId}/keywords # 상담별 키워드 추출
```

#### 고급 검색 API
```http
GET    /api/stt/search/general      # 일반 키워드 검색 (summary, full_text)
GET    /api/stt/search/segments     # 세그먼트 내 문장 단위 검색 (Nested)
GET    /api/stt/search/customer-attributes # 고객 속성 필터링 (지역, 연령대)
GET    /api/stt/search/agent-keyword # 특정 상담원의 키워드 관련 콜 검색
GET    /api/stt/search/advanced     # 고급 필터링 검색 (다중 조건)
GET    /api/stt/search/insurance-keywords # 보험 특화 키워드 검색
```

#### 분석 및 통계 API
```http
GET    /api/stt/analytics/keyword-frequency # 키워드 빈도 분석
GET    /api/stt/analytics/campaign-distribution # 캠페인 타입별 분포
GET    /api/stt/analytics/call-trend # 통화 트렌드 분석
GET    /api/stt/analytics/top-keywords # 주제별 상위 키워드 Top 10
GET    /api/stt/analytics/sentiment-keywords # 긍정/부정 키워드 분석
GET    /api/stt/calls/{callId}/highlights # 키워드 하이라이팅 정보
GET    /api/stt/dashboard           # STT 통계 대시보드 데이터
GET    /api/stt/export              # 분석 결과 내보내기 (Excel/PDF용)
```

### 통합 검색 API

```http
GET    /api/search?q={keyword}      # 통합 검색 (모든 리소스)
```

## 📊 데이터 모델

### Dashboard 엔티티
```json
{
  "id": "Long",
  "title": "String",
  "description": "String", 
  "category": "String",
  "type": "TEMPLATE|CUSTOM",
  "rating": "Double",
  "downloads": "Integer",
  "tags": ["String"],
  "image": "String",
  "config": "String",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

### Approval 엔티티
```json
{
  "id": "Long",
  "type": "DEPLOY|DASHBOARD|DATA",
  "title": "String",
  "description": "String",
  "status": "PENDING|APPROVED|REJECTED",
  "priority": "HIGH|MEDIUM|LOW",
  "requester": "String",
  "reviewer": "String",
  "requestDate": "LocalDateTime",
  "reviewDate": "LocalDateTime", 
  "reviewComment": "String",
  "currentStep": "Integer",
  "totalSteps": "Integer"
}
```

## 🔧 설정

### 개발 환경 설정
```yaml
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

### 프로덕션 환경 설정
```yaml
spring:
  profiles:
    active: prod
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
```

## 🧪 테스트

```bash
# 단위 테스트 실행
mvn test

# 통합 테스트 포함 전체 테스트
mvn verify
```

## 📦 빌드 및 배포

```bash
# 프로덕션 빌드
mvn clean package -Pprod

# Docker 이미지 빌드 (향후 추가)
docker build -t lina-data-portal-backend .

# 프로덕션 실행
java -jar -Dspring.profiles.active=prod target/data-portal-backend-1.0.0.jar
```

## 🔄 향후 개발 계획

### Phase 2 - 고급 분석 도구 ✅
- ✅ SQL Editor API (쿼리 실행 및 결과 관리)
- ✅ Text-to-SQL API (AI 기반 자연어 쿼리 변환)
- ✅ STT 키워드 검색 API (음성 인식 및 분석)

### Phase 3 - Producer360 통합 인사이트
- KPI 대시보드 API
- 실시간 차트 데이터
- 액션 아이템 관리
- 통합 인사이트 제공

### Phase 4 - 고급 기능
- JWT 기반 인증/인가
- ✅ API 문서화 (Swagger/OpenAPI)
- 로깅 및 모니터링 (ELK Stack)
- 캐싱 (Redis)
- 메시지 큐 (RabbitMQ)
- 파일 업로드/다운로드 (AWS S3)
- 실시간 알림 (WebSocket)

### Phase 5 - AI/ML 고도화
- 지능형 승인 추천 시스템
- 자동 데이터 품질 모니터링
- 예측 분석 기반 리소스 관리
- 자연어 기반 데이터 탐색

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

---

**Made with ❤️ for LINA Life Insurance**