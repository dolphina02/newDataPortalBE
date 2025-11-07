# 데이터베이스 User 참조키 마이그레이션 가이드

## 🎯 개요
기존 문자열 기반 사용자 정보를 User 테이블 참조키로 변경하는 데이터베이스 마이그레이션 가이드입니다.

## 📋 마이그레이션 파일 목록

### 1. 스키마 파일
- **`database/schema.sql`** - 업데이트된 전체 스키마 (User 참조키 포함)
- **`database/schema-updated.sql`** - 새로운 정규화된 스키마 (백업용)

### 2. 마이그레이션 스크립트
- **`database/migration-to-user-references.sql`** - 완전한 마이그레이션 스크립트 (기존 → 새로운)
- **`database/apply-user-references-migration.sql`** - 실제 적용용 간소화된 스크립트

### 3. 샘플 데이터
- **`database/sample-data-updated.sql`** - User 참조키 기반 샘플 데이터

## 🚀 마이그레이션 실행 방법

### 방법 1: 새로운 데이터베이스 구축 (권장)

```sql
-- 1. 새로운 스키마로 데이터베이스 생성
\i database/schema.sql

-- 2. 샘플 데이터 삽입
\i database/sample-data-updated.sql
```

### 방법 2: 기존 데이터베이스 마이그레이션

```sql
-- 1. 백업 생성 (필수!)
pg_dump your_database > backup_before_migration.sql

-- 2. 간소화된 마이그레이션 적용
\i database/apply-user-references-migration.sql

-- 3. 데이터 검증
SELECT * FROM users LIMIT 5;
SELECT * FROM approval_line_templates;
SELECT * FROM approval_steps LIMIT 5;
```

### 방법 3: 완전한 마이그레이션 (고급 사용자용)

```sql
-- 1. 백업 생성
pg_dump your_database > backup_before_migration.sql

-- 2. 완전한 마이그레이션 실행
\i database/migration-to-user-references.sql

-- 3. 검증 및 정리
-- (스크립트 내 검증 쿼리 실행)
```

## 📊 주요 변경사항

### 새로 추가된 테이블
1. **`users`** - 사용자 정보 정규화
2. **`approval_line_templates`** - 승인 라인 템플릿
3. **`approval_steps`** - 실제 승인 진행 단계

### 업데이트된 테이블
1. **`approvals`** - requester/reviewer → User 참조키
2. **`dashboards`** - created_by → User 참조키
3. **`reports`** - created_by → User 참조키
4. **`ml_models`** - created_by → User 참조키
5. **`dashboard_subscriptions`** - user_id → User 참조키
6. **`user_tokens`** - user_id → User 참조키

## ✅ 마이그레이션 후 검증

### 1. 테이블 생성 확인
```sql
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
  AND table_name IN ('users', 'approval_line_templates', 'approval_steps')
ORDER BY table_name;
```

### 2. 외래키 제약 조건 확인
```sql
SELECT 
    tc.table_name, 
    tc.constraint_name, 
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name 
FROM information_schema.table_constraints AS tc 
JOIN information_schema.key_column_usage AS kcu
    ON tc.constraint_name = kcu.constraint_name
JOIN information_schema.constraint_column_usage AS ccu
    ON ccu.constraint_name = tc.constraint_name
WHERE tc.constraint_type = 'FOREIGN KEY' 
    AND tc.table_name IN ('approvals', 'dashboards', 'reports', 'ml_models', 'dashboard_subscriptions', 'user_tokens', 'approval_line_templates', 'approval_steps')
ORDER BY tc.table_name;
```

### 3. 데이터 정합성 확인
```sql
-- 사용자 데이터 확인
SELECT COUNT(*) as user_count FROM users;

-- 승인 요청의 User 참조 확인
SELECT 
    COUNT(*) as total_approvals,
    COUNT(requester_id) as with_requester_id,
    COUNT(reviewer_id) as with_reviewer_id
FROM approvals;

-- 승인 단계 데이터 확인
SELECT 
    COUNT(*) as total_steps,
    COUNT(DISTINCT approval_id) as unique_approvals,
    COUNT(DISTINCT approver_id) as unique_approvers
FROM approval_steps;
```

### 4. 애플리케이션 연동 테스트
```sql
-- User 기반 승인 요청 조회 테스트
SELECT a.title, u.name as requester_name, a.status 
FROM approvals a 
JOIN users u ON a.requester_id = u.id 
LIMIT 5;

-- 승인 단계 기반 조회 테스트
SELECT 
    a.title, 
    s.step_order, 
    u.name as approver_name, 
    s.status 
FROM approval_steps s
JOIN approvals a ON s.approval_id = a.id
JOIN users u ON s.approver_id = u.id
ORDER BY a.id, s.step_order
LIMIT 10;
```

## 🔧 트러블슈팅

### 문제 1: 외래키 제약 조건 오류
```sql
-- 해결: 참조되지 않는 사용자 데이터 확인
SELECT DISTINCT requester_email 
FROM approvals 
WHERE requester_id IS NULL;

-- 누락된 사용자 추가
INSERT INTO users (email, name, department, status)
VALUES ('missing@company.com', 'Missing User', 'Unknown', 'ACTIVE');
```

### 문제 2: 중복 데이터 오류
```sql
-- 해결: 중복 이메일 확인 및 정리
SELECT email, COUNT(*) 
FROM users 
GROUP BY email 
HAVING COUNT(*) > 1;
```

### 문제 3: 성능 이슈
```sql
-- 해결: 인덱스 재생성
REINDEX TABLE users;
REINDEX TABLE approvals;
REINDEX TABLE approval_steps;
```

## 📈 성능 최적화

### 1. 인덱스 확인
```sql
-- 중요 인덱스 존재 확인
SELECT indexname, tablename 
FROM pg_indexes 
WHERE tablename IN ('users', 'approvals', 'approval_steps')
ORDER BY tablename, indexname;
```

### 2. 쿼리 성능 분석
```sql
-- 자주 사용되는 쿼리 성능 확인
EXPLAIN ANALYZE 
SELECT a.*, u.name as requester_name 
FROM approvals a 
JOIN users u ON a.requester_id = u.id 
WHERE a.status = 'PENDING';
```

## 🔄 롤백 방법

### 긴급 롤백 (백업 복원)
```bash
# 1. 백업에서 복원
pg_restore -d your_database backup_before_migration.sql

# 2. 또는 SQL 백업 복원
psql your_database < backup_before_migration.sql
```

### 부분 롤백 (컬럼 기반)
```sql
-- 기존 컬럼이 유지된 경우 임시 복원
UPDATE approvals SET 
    requester = requester_email,
    reviewer = reviewer_email
WHERE requester_id IS NOT NULL;
```

## 📝 마이그레이션 체크리스트

- [ ] 데이터베이스 백업 완료
- [ ] 애플리케이션 서비스 중지
- [ ] 마이그레이션 스크립트 실행
- [ ] 테이블 생성 확인
- [ ] 외래키 제약 조건 확인
- [ ] 데이터 정합성 검증
- [ ] 인덱스 성능 확인
- [ ] 애플리케이션 연동 테스트
- [ ] 샘플 쿼리 실행 테스트
- [ ] 애플리케이션 서비스 재시작
- [ ] 운영 모니터링 시작

## 🚨 주의사항

1. **반드시 백업 생성**: 마이그레이션 전 전체 데이터베이스 백업 필수
2. **서비스 중지**: 마이그레이션 중 애플리케이션 서비스 중지 권장
3. **단계별 검증**: 각 단계마다 데이터 정합성 확인
4. **롤백 준비**: 문제 발생 시 즉시 롤백할 수 있도록 준비
5. **성능 모니터링**: 마이그레이션 후 쿼리 성능 모니터링

## 📞 지원

마이그레이션 중 문제가 발생하면:
1. 즉시 서비스 중지
2. 백업으로 롤백
3. 개발팀에 문의
4. 로그 및 오류 메시지 수집

---

**마이그레이션 완료 후 이 가이드를 보관하여 향후 참조용으로 활용하세요.**