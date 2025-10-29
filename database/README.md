# LINA Data Portal Database

이 디렉토리는 LINA Data Portal 백엔드 애플리케이션의 데이터베이스 스키마와 초기 데이터를 포함합니다.

## 📁 파일 구조

```
database/
├── README.md              # 이 파일
├── schema.sql             # 데이터베이스 스키마 (테이블, 인덱스, 제약조건)
├── sample-data.sql        # 샘플 데이터 삽입
├── init-database.sql      # 전체 데이터베이스 초기화 스크립트
└── setup-database.bat     # Windows 실행 스크립트
```

## 🗄️ 데이터베이스 스키마

### 주요 테이블

1. **dashboards** - 대시보드 템플릿 및 사용자 정의 대시보드
2. **approvals** - 승인 관리 시스템 (배포/구독/데이터 접근)
3. **reports** - PDF 리포트 및 분석 문서
4. **data_tables** - 데이터 카탈로그 (테이블 메타데이터)
5. **ml_models** - 머신러닝 모델 관리
6. **api_endpoints** - API 엔드포인트 카탈로그

### 관계 테이블 (Tags)

- **dashboard_tags** - 대시보드 태그
- **report_tags** - 리포트 태그
- **data_table_tags** - 데이터 테이블 태그
- **data_table_columns** - 데이터 테이블 컬럼 정보
- **model_features** - 모델 특성 중요도
- **model_tags** - 모델 태그
- **api_tags** - API 태그

## 🚀 데이터베이스 설정

### 방법 1: 자동 설정 (권장)

Windows에서 배치 파일 실행:
```bash
cd database
setup-database.bat
```

### 방법 2: 수동 설정

1. **PostgreSQL 클라이언트로 연결**
```bash
psql "postgresql://neondb_owner:npg_xnKiwN18QFSu@ep-square-shadow-a174zj2p-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require"
```

2. **스키마 생성**
```sql
\i schema.sql
```

3. **샘플 데이터 삽입**
```sql
\i sample-data.sql
```

### 방법 3: 전체 초기화

```bash
psql "postgresql://neondb_owner:npg_xnKiwN18QFSu@ep-square-shadow-a174zj2p-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require" -f init-database.sql
```

## 📊 샘플 데이터

### 대시보드 (4개)
- 보험 KPI 대시보드
- 청구 요약 대시보드  
- 고객 분석 대시보드
- 리스크 모니터링 대시보드

### 승인 요청 (3개)
- 신규 고객 분석 대시보드 배포 요청
- 개인정보 포함 대시보드 구독 요청
- 보험금 청구 데이터 접근 권한 요청

### 리포트 (3개)
- 보험통계월보 2025년 7월호
- 고객 세그먼트 분석 리포트
- Q3 KPI 성과 리포트

### 데이터 테이블 (3개)
- POLICY_MASTER (보험계약 마스터)
- CLAIM_HISTORY (청구 이력)
- CUSTOMER_PROFILE (고객 프로필)

### ML 모델 (3개)
- 보험금 청구 사기 탐지 모델
- 고객 이탈 예측 모델
- 언더라이팅 리스크 점수 모델

### API 엔드포인트 (6개)
- 고객 관리 API (2개)
- 보험 상품 API (1개)
- 청구 처리 API (2개)
- 언더라이팅 API (1개)

## 🔍 데이터 확인

### 테이블 목록 조회
```sql
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;
```

### 레코드 수 확인
```sql
SELECT 'dashboards' as table_name, COUNT(*) as record_count FROM dashboards
UNION ALL
SELECT 'approvals', COUNT(*) FROM approvals
UNION ALL
SELECT 'reports', COUNT(*) FROM reports
UNION ALL
SELECT 'data_tables', COUNT(*) FROM data_tables
UNION ALL
SELECT 'ml_models', COUNT(*) FROM ml_models
UNION ALL
SELECT 'api_endpoints', COUNT(*) FROM api_endpoints;
```

### 샘플 검색 쿼리
```sql
-- 보험 관련 대시보드 검색
SELECT title, category, rating 
FROM dashboards 
WHERE title ILIKE '%보험%' OR description ILIKE '%보험%';

-- 배포된 ML 모델 조회
SELECT name, type, status, accuracy 
FROM ml_models 
WHERE status = 'DEPLOYED';

-- 대기 중인 승인 요청
SELECT title, type, priority, status 
FROM approvals 
WHERE status = 'PENDING' 
ORDER BY priority DESC;
```

## ⚡ 성능 최적화

### 인덱스
- 각 테이블의 주요 검색 컬럼에 인덱스 생성
- 외래 키 관계에 자동 인덱스
- 정렬용 인덱스 (rating DESC, created_at DESC 등)

### 트리거
- `updated_at` 컬럼 자동 업데이트 트리거
- `last_updated` 컬럼 자동 업데이트 트리거

## 🔧 유지보수

### 백업
```bash
pg_dump "postgresql://neondb_owner:npg_xnKiwN18QFSu@ep-square-shadow-a174zj2p-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require" > backup.sql
```

### 복원
```bash
psql "postgresql://neondb_owner:npg_xnKiwN18QFSu@ep-square-shadow-a174zj2p-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require" < backup.sql
```

### 스키마 재생성 (주의: 모든 데이터 삭제됨)
```sql
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
\i schema.sql
\i sample-data.sql
```

## 📝 참고사항

- **Neon PostgreSQL**: 클라우드 기반 PostgreSQL 서비스
- **SSL 필수**: 모든 연결에 SSL 사용
- **Connection Pooling**: Neon의 connection pooler 사용
- **Character Set**: UTF-8 지원으로 한글 데이터 완벽 지원

## 🔗 관련 링크

- [Neon PostgreSQL 문서](https://neon.tech/docs)
- [PostgreSQL 공식 문서](https://www.postgresql.org/docs/)
- [Spring Boot JPA 문서](https://spring.io/projects/spring-data-jpa)