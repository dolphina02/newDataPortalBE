-- Migration Script: String-based User Fields to User References
-- 기존 문자열 기반 사용자 정보를 User 테이블 참조키로 변경

-- ============================================
-- STEP 1: 백업 테이블 생성 (안전을 위해)
-- ============================================

-- 기존 테이블 백업
CREATE TABLE approvals_backup AS SELECT * FROM approvals;
CREATE TABLE dashboards_backup AS SELECT * FROM dashboards;
CREATE TABLE reports_backup AS SELECT * FROM reports;
CREATE TABLE ml_models_backup AS SELECT * FROM ml_models;
CREATE TABLE dashboard_subscriptions_backup AS SELECT * FROM dashboard_subscriptions;
CREATE TABLE user_tokens_backup AS SELECT * FROM user_tokens;

-- ============================================
-- STEP 2: Users 테이블 생성 및 기존 사용자 데이터 마이그레이션
-- ============================================

-- Users 테이블 생성 (이미 존재하지 않는 경우)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    entra_object_id VARCHAR(100) UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    display_name VARCHAR(100),
    employee_id VARCHAR(50),
    department VARCHAR(100),
    job_title VARCHAR(100),
    manager_email VARCHAR(255),
    phone_number VARCHAR(20),
    office_location VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 기존 데이터에서 고유한 사용자 이메일 추출 및 Users 테이블에 삽입
INSERT INTO users (email, name, department, status)
SELECT DISTINCT 
    email,
    COALESCE(SPLIT_PART(email, '@', 1), 'Unknown') as name,
    'Unknown' as department,
    'ACTIVE' as status
FROM (
    SELECT requester as email FROM approvals WHERE requester IS NOT NULL
    UNION
    SELECT reviewer as email FROM approvals WHERE reviewer IS NOT NULL
    UNION
    SELECT created_by as email FROM dashboards WHERE created_by IS NOT NULL
    UNION
    SELECT created_by as email FROM reports WHERE created_by IS NOT NULL
    UNION
    SELECT created_by as email FROM ml_models WHERE created_by IS NOT NULL
    UNION
    SELECT user_id as email FROM dashboard_subscriptions WHERE user_id IS NOT NULL
    UNION
    SELECT user_id as email FROM user_tokens WHERE user_id IS NOT NULL
) unique_emails
WHERE email IS NOT NULL
ON CONFLICT (email) DO NOTHING;

-- ============================================
-- STEP 3: 테이블 구조 변경 (컬럼 추가)
-- ============================================

-- Approvals 테이블 업데이트
ALTER TABLE approvals 
ADD COLUMN IF NOT EXISTS requester_id BIGINT,
ADD COLUMN IF NOT EXISTS requester_email VARCHAR(255),
ADD COLUMN IF NOT EXISTS requester_name VARCHAR(255),
ADD COLUMN IF NOT EXISTS reviewer_id BIGINT,
ADD COLUMN IF NOT EXISTS reviewer_email VARCHAR(255),
ADD COLUMN IF NOT EXISTS reviewer_name VARCHAR(255);

-- Dashboards 테이블 업데이트
ALTER TABLE dashboards
ADD COLUMN IF NOT EXISTS created_by_id BIGINT,
ADD COLUMN IF NOT EXISTS created_by_email VARCHAR(255),
ADD COLUMN IF NOT EXISTS created_by_name VARCHAR(255);

-- Reports 테이블 업데이트
ALTER TABLE reports
ADD COLUMN IF NOT EXISTS created_by_id BIGINT,
ADD COLUMN IF NOT EXISTS created_by_email VARCHAR(255),
ADD COLUMN IF NOT EXISTS created_by_name VARCHAR(255);

-- ML Models 테이블 업데이트
ALTER TABLE ml_models
ADD COLUMN IF NOT EXISTS created_by_id BIGINT,
ADD COLUMN IF NOT EXISTS created_by_email VARCHAR(255),
ADD COLUMN IF NOT EXISTS created_by_name VARCHAR(255);

-- Dashboard Subscriptions 테이블 업데이트
ALTER TABLE dashboard_subscriptions
ADD COLUMN IF NOT EXISTS user_id_new BIGINT,
ADD COLUMN IF NOT EXISTS user_email VARCHAR(255);

-- User Tokens 테이블 업데이트
ALTER TABLE user_tokens
ADD COLUMN IF NOT EXISTS user_id_new BIGINT,
ADD COLUMN IF NOT EXISTS user_email VARCHAR(255);

-- ============================================
-- STEP 4: 데이터 마이그레이션 (기존 문자열 → User ID 매핑)
-- ============================================

-- Approvals 테이블 데이터 마이그레이션
UPDATE approvals SET 
    requester_id = u.id,
    requester_email = u.email,
    requester_name = u.name
FROM users u 
WHERE approvals.requester = u.email;

UPDATE approvals SET 
    reviewer_id = u.id,
    reviewer_email = u.email,
    reviewer_name = u.name
FROM users u 
WHERE approvals.reviewer = u.email;

-- Dashboards 테이블 데이터 마이그레이션
UPDATE dashboards SET 
    created_by_id = u.id,
    created_by_email = u.email,
    created_by_name = u.name
FROM users u 
WHERE dashboards.created_by = u.email;

-- Reports 테이블 데이터 마이그레이션
UPDATE reports SET 
    created_by_id = u.id,
    created_by_email = u.email,
    created_by_name = u.name
FROM users u 
WHERE reports.created_by = u.email;

-- ML Models 테이블 데이터 마이그레이션
UPDATE ml_models SET 
    created_by_id = u.id,
    created_by_email = u.email,
    created_by_name = u.name
FROM users u 
WHERE ml_models.created_by = u.email;

-- Dashboard Subscriptions 테이블 데이터 마이그레이션
UPDATE dashboard_subscriptions SET 
    user_id_new = u.id,
    user_email = u.email
FROM users u 
WHERE dashboard_subscriptions.user_id = u.email;

-- User Tokens 테이블 데이터 마이그레이션
UPDATE user_tokens SET 
    user_id_new = u.id,
    user_email = u.email
FROM users u 
WHERE user_tokens.user_id = u.email;

-- ============================================
-- STEP 5: 제약 조건 및 외래키 추가
-- ============================================

-- 필수 필드 NOT NULL 제약 조건 추가 (데이터 마이그레이션 후)
ALTER TABLE approvals 
ALTER COLUMN requester_id SET NOT NULL,
ALTER COLUMN requester_email SET NOT NULL,
ALTER COLUMN requester_name SET NOT NULL;

ALTER TABLE dashboards
ALTER COLUMN created_by_email SET NOT NULL;

ALTER TABLE reports
ALTER COLUMN created_by_email SET NOT NULL;

ALTER TABLE dashboard_subscriptions
ALTER COLUMN user_id_new SET NOT NULL,
ALTER COLUMN user_email SET NOT NULL;

ALTER TABLE user_tokens
ALTER COLUMN user_id_new SET NOT NULL,
ALTER COLUMN user_email SET NOT NULL;

-- 외래키 제약 조건 추가
ALTER TABLE approvals 
ADD CONSTRAINT fk_approvals_requester FOREIGN KEY (requester_id) REFERENCES users(id),
ADD CONSTRAINT fk_approvals_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id);

ALTER TABLE dashboards
ADD CONSTRAINT fk_dashboards_created_by FOREIGN KEY (created_by_id) REFERENCES users(id);

ALTER TABLE reports
ADD CONSTRAINT fk_reports_created_by FOREIGN KEY (created_by_id) REFERENCES users(id);

ALTER TABLE ml_models
ADD CONSTRAINT fk_ml_models_created_by FOREIGN KEY (created_by_id) REFERENCES users(id);

-- ============================================
-- STEP 6: 컬럼 이름 변경 및 기존 컬럼 제거
-- ============================================

-- Dashboard Subscriptions 컬럼 교체
ALTER TABLE dashboard_subscriptions DROP COLUMN user_id;
ALTER TABLE dashboard_subscriptions RENAME COLUMN user_id_new TO user_id;

-- User Tokens 컬럼 교체  
ALTER TABLE user_tokens DROP COLUMN user_id;
ALTER TABLE user_tokens RENAME COLUMN user_id_new TO user_id;

-- 외래키 제약 조건 추가 (컬럼 이름 변경 후)
ALTER TABLE dashboard_subscriptions
ADD CONSTRAINT fk_dashboard_subscriptions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE user_tokens
ADD CONSTRAINT fk_user_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- ============================================
-- STEP 7: 인덱스 생성
-- ============================================

-- User 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_entra_id ON users(entra_object_id);
CREATE INDEX IF NOT EXISTS idx_user_employee_id ON users(employee_id);
CREATE INDEX IF NOT EXISTS idx_user_department ON users(department);
CREATE INDEX IF NOT EXISTS idx_user_status ON users(status);

-- Approval 테이블 인덱스 (업데이트)
CREATE INDEX IF NOT EXISTS idx_approval_requester ON approvals(requester_id);
CREATE INDEX IF NOT EXISTS idx_approval_reviewer ON approvals(reviewer_id);
CREATE INDEX IF NOT EXISTS idx_approval_requester_email ON approvals(requester_email);
CREATE INDEX IF NOT EXISTS idx_approval_reviewer_email ON approvals(reviewer_email);

-- Dashboard 테이블 인덱스 (업데이트)
CREATE INDEX IF NOT EXISTS idx_dashboards_created_by ON dashboards(created_by_id);
CREATE INDEX IF NOT EXISTS idx_dashboards_created_by_email ON dashboards(created_by_email);

-- Report 테이블 인덱스 (업데이트)
CREATE INDEX IF NOT EXISTS idx_reports_created_by ON reports(created_by_id);
CREATE INDEX IF NOT EXISTS idx_reports_created_by_email ON reports(created_by_email);

-- ML Model 테이블 인덱스 (업데이트)
CREATE INDEX IF NOT EXISTS idx_ml_models_created_by ON ml_models(created_by_id);
CREATE INDEX IF NOT EXISTS idx_ml_models_created_by_email ON ml_models(created_by_email);

-- Dashboard Subscription 테이블 인덱스 (업데이트)
CREATE INDEX IF NOT EXISTS idx_dashboard_subscriptions_user_id ON dashboard_subscriptions(user_id);
CREATE INDEX IF NOT EXISTS idx_dashboard_subscriptions_user_email ON dashboard_subscriptions(user_email);

-- User Token 테이블 인덱스 (업데이트)
CREATE INDEX IF NOT EXISTS idx_user_tokens_user_id ON user_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_user_tokens_user_email ON user_tokens(user_email);

-- ============================================
-- STEP 8: 새로운 테이블 생성 (Approval Line Templates & Steps)
-- ============================================

-- Approval Line Templates 테이블 생성
CREATE TABLE IF NOT EXISTS approval_line_templates (
    id BIGSERIAL PRIMARY KEY,
    approval_type VARCHAR(50) NOT NULL CHECK (approval_type IN ('DASHBOARD_DEPLOY', 'DASHBOARD_CREATE', 'DASHBOARD', 'DATA', 'DATA_ACCESS', 'REPORT_PUBLISH')),
    step_order INTEGER NOT NULL,
    approver_role VARCHAR(255) NOT NULL,
    approver_department VARCHAR(255),
    
    -- 승인자 정보 (정규화) - 선택적
    approver_id BIGINT,
    approver_email VARCHAR(255),
    approver_name VARCHAR(255),
    
    is_required BOOLEAN NOT NULL DEFAULT TRUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (approver_id) REFERENCES users(id)
);

-- Approval Steps 테이블 생성
CREATE TABLE IF NOT EXISTS approval_steps (
    id BIGSERIAL PRIMARY KEY,
    approval_id BIGINT NOT NULL,
    template_id BIGINT NOT NULL,
    template_version BIGINT NOT NULL,
    step_order INTEGER NOT NULL,
    
    -- 승인자 정보 (정규화)
    approver_id BIGINT NOT NULL,
    approver_email VARCHAR(255) NOT NULL,
    approver_name VARCHAR(255) NOT NULL,
    
    approver_role VARCHAR(255),
    approver_department VARCHAR(255),
    is_required BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT,
    
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'SKIPPED')),
    approved_at TIMESTAMP,
    approval_comment TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (approval_id) REFERENCES approvals(id) ON DELETE CASCADE,
    FOREIGN KEY (approver_id) REFERENCES users(id),
    UNIQUE(approval_id, step_order)
);

-- 새 테이블 인덱스 생성
CREATE INDEX idx_approval_line_templates_type ON approval_line_templates(approval_type);
CREATE INDEX idx_approval_line_templates_step_order ON approval_line_templates(approval_type, step_order);
CREATE INDEX idx_approval_line_templates_approver ON approval_line_templates(approver_id);
CREATE INDEX idx_approval_line_templates_active ON approval_line_templates(is_active);

CREATE INDEX idx_approval_steps_approval_id ON approval_steps(approval_id);
CREATE INDEX idx_approval_steps_approver_id ON approval_steps(approver_id);
CREATE INDEX idx_approval_steps_status ON approval_steps(status);
CREATE INDEX idx_approval_steps_step_order ON approval_steps(approval_id, step_order);
CREATE INDEX idx_approval_steps_approver_email ON approval_steps(approver_email);
CREATE INDEX idx_approval_steps_template ON approval_steps(template_id, template_version);

-- ============================================
-- STEP 9: 기존 Approval Lines 데이터 마이그레이션 (있는 경우)
-- ============================================

-- 기존 approval_lines 테이블이 있다면 approval_steps로 마이그레이션
DO $$
BEGIN
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'approval_lines') THEN
        -- 기존 approval_lines 데이터를 approval_steps로 마이그레이션
        INSERT INTO approval_steps (
            approval_id, template_id, template_version, step_order,
            approver_id, approver_email, approver_name,
            approver_role, approver_department, status, approved_at, approval_comment
        )
        SELECT 
            al.approval_id,
            0 as template_id, -- 기본값
            0 as template_version, -- 기본값
            al.step_order,
            u.id as approver_id,
            u.email as approver_email,
            u.name as approver_name,
            al.approver_role,
            al.approver_department,
            al.status,
            al.approved_at,
            al.approval_comment
        FROM approval_lines al
        LEFT JOIN users u ON al.approver_id = u.email
        WHERE u.id IS NOT NULL;
        
        -- 기존 테이블 백업 후 제거
        ALTER TABLE approval_lines RENAME TO approval_lines_old;
    END IF;
END $$;

-- ============================================
-- STEP 10: 트리거 생성 (updated_at 자동 업데이트)
-- ============================================

-- updated_at 트리거 함수 (이미 존재하지 않는 경우)
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 트리거 적용
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_approval_line_templates_updated_at BEFORE UPDATE ON approval_line_templates FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_approval_steps_updated_at BEFORE UPDATE ON approval_steps FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- STEP 11: 데이터 검증 쿼리
-- ============================================

-- 마이그레이션 결과 검증
SELECT 'Users' as table_name, COUNT(*) as record_count FROM users
UNION ALL
SELECT 'Approvals with User IDs', COUNT(*) FROM approvals WHERE requester_id IS NOT NULL
UNION ALL
SELECT 'Dashboards with User IDs', COUNT(*) FROM dashboards WHERE created_by_id IS NOT NULL
UNION ALL
SELECT 'Reports with User IDs', COUNT(*) FROM reports WHERE created_by_id IS NOT NULL
UNION ALL
SELECT 'ML Models with User IDs', COUNT(*) FROM ml_models WHERE created_by_id IS NOT NULL
UNION ALL
SELECT 'Dashboard Subscriptions with User IDs', COUNT(*) FROM dashboard_subscriptions WHERE user_id IS NOT NULL
UNION ALL
SELECT 'User Tokens with User IDs', COUNT(*) FROM user_tokens WHERE user_id IS NOT NULL;

-- 외래키 제약 조건 확인
SELECT 
    tc.table_name, 
    tc.constraint_name, 
    tc.constraint_type,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name 
FROM information_schema.table_constraints AS tc 
JOIN information_schema.key_column_usage AS kcu
    ON tc.constraint_name = kcu.constraint_name
    AND tc.table_schema = kcu.table_schema
JOIN information_schema.constraint_column_usage AS ccu
    ON ccu.constraint_name = tc.constraint_name
    AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY' 
    AND tc.table_name IN ('approvals', 'dashboards', 'reports', 'ml_models', 'dashboard_subscriptions', 'user_tokens', 'approval_line_templates', 'approval_steps')
ORDER BY tc.table_name, tc.constraint_name;

-- ============================================
-- STEP 12: 정리 작업 (선택사항)
-- ============================================

-- 마이그레이션이 성공적으로 완료된 후 기존 컬럼 제거 (주의: 데이터 검증 후 실행)
-- ALTER TABLE approvals DROP COLUMN IF EXISTS requester;
-- ALTER TABLE approvals DROP COLUMN IF EXISTS reviewer;
-- ALTER TABLE dashboards DROP COLUMN IF EXISTS created_by;
-- ALTER TABLE reports DROP COLUMN IF EXISTS created_by;
-- ALTER TABLE ml_models DROP COLUMN IF EXISTS created_by;

-- 백업 테이블 제거 (주의: 마이그레이션 검증 완료 후 실행)
-- DROP TABLE IF EXISTS approvals_backup;
-- DROP TABLE IF EXISTS dashboards_backup;
-- DROP TABLE IF EXISTS reports_backup;
-- DROP TABLE IF EXISTS ml_models_backup;
-- DROP TABLE IF EXISTS dashboard_subscriptions_backup;
-- DROP TABLE IF EXISTS user_tokens_backup;

COMMIT;

-- ============================================
-- 마이그레이션 완료 메시지
-- ============================================
SELECT 'Migration to User References completed successfully!' as status;