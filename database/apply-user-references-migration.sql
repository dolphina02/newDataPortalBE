-- 실제 데이터베이스에 User 참조키 변경사항 적용
-- 이 스크립트는 기존 데이터베이스에 안전하게 적용할 수 있도록 설계되었습니다.

-- ============================================
-- STEP 1: 백업 및 안전 조치
-- ============================================

-- 트랜잭션 시작
BEGIN;

-- 기존 테이블 백업 (선택사항)
-- CREATE TABLE approvals_backup AS SELECT * FROM approvals;
-- CREATE TABLE dashboards_backup AS SELECT * FROM dashboards;

-- ============================================
-- STEP 2: Users 테이블 생성
-- ============================================

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

-- Users 테이블 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_entra_id ON users(entra_object_id);
CREATE INDEX IF NOT EXISTS idx_user_employee_id ON users(employee_id);
CREATE INDEX IF NOT EXISTS idx_user_department ON users(department);
CREATE INDEX IF NOT EXISTS idx_user_status ON users(status);

-- ============================================
-- STEP 3: 기본 사용자 데이터 삽입
-- ============================================

-- 관리자 사용자 생성 (이미 존재하지 않는 경우)
INSERT INTO users (email, name, department, is_admin, status)
VALUES ('admin@company.com', '시스템관리자', 'IT팀', true, 'ACTIVE')
ON CONFLICT (email) DO NOTHING;

-- 기존 데이터에서 사용자 추출 및 삽입 (테이블이 존재하는 경우)
DO $$
BEGIN
    -- approvals 테이블에서 사용자 추출
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'approvals') THEN
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
        ) unique_emails
        WHERE email IS NOT NULL
        ON CONFLICT (email) DO NOTHING;
    END IF;
    
    -- dashboards 테이블에서 사용자 추출
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'dashboards') THEN
        INSERT INTO users (email, name, department, status)
        SELECT DISTINCT 
            created_by as email,
            COALESCE(SPLIT_PART(created_by, '@', 1), 'Unknown') as name,
            'Unknown' as department,
            'ACTIVE' as status
        FROM dashboards 
        WHERE created_by IS NOT NULL
        ON CONFLICT (email) DO NOTHING;
    END IF;
END $$;

-- ============================================
-- STEP 4: 새로운 테이블 생성
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
CREATE INDEX IF NOT EXISTS idx_approval_line_templates_type ON approval_line_templates(approval_type);
CREATE INDEX IF NOT EXISTS idx_approval_line_templates_step_order ON approval_line_templates(approval_type, step_order);
CREATE INDEX IF NOT EXISTS idx_approval_line_templates_approver ON approval_line_templates(approver_id);
CREATE INDEX IF NOT EXISTS idx_approval_line_templates_active ON approval_line_templates(is_active);

CREATE INDEX IF NOT EXISTS idx_approval_steps_approval_id ON approval_steps(approval_id);
CREATE INDEX IF NOT EXISTS idx_approval_steps_approver_id ON approval_steps(approver_id);
CREATE INDEX IF NOT EXISTS idx_approval_steps_status ON approval_steps(status);
CREATE INDEX IF NOT EXISTS idx_approval_steps_step_order ON approval_steps(approval_id, step_order);
CREATE INDEX IF NOT EXISTS idx_approval_steps_approver_email ON approval_steps(approver_email);
CREATE INDEX IF NOT EXISTS idx_approval_steps_template ON approval_steps(template_id, template_version);

-- ============================================
-- STEP 5: 기본 승인 라인 템플릿 데이터 삽입
-- ============================================

-- 관리자 사용자 ID 조회
DO $$
DECLARE
    admin_user_id BIGINT;
BEGIN
    SELECT id INTO admin_user_id FROM users WHERE email = 'admin@company.com' LIMIT 1;
    
    IF admin_user_id IS NOT NULL THEN
        -- DASHBOARD_DEPLOY 승인 라인 템플릿
        INSERT INTO approval_line_templates (approval_type, step_order, approver_role, approver_department, approver_id, approver_email, approver_name, is_required, description)
        VALUES 
        ('DASHBOARD_DEPLOY', 1, '시스템 관리자', 'IT팀', admin_user_id, 'admin@company.com', '시스템관리자', true, 'IT팀 시스템 검토'),
        ('DASHBOARD_DEPLOY', 2, '보안 담당자', '보안팀', admin_user_id, 'admin@company.com', '시스템관리자', true, '보안 검토 및 최종 승인')
        ON CONFLICT DO NOTHING;
        
        -- DATA_ACCESS 승인 라인 템플릿
        INSERT INTO approval_line_templates (approval_type, step_order, approver_role, approver_department, approver_id, approver_email, approver_name, is_required, description)
        VALUES 
        ('DATA_ACCESS', 1, '보안 담당자', '보안팀', admin_user_id, 'admin@company.com', '시스템관리자', true, '데이터 접근 보안 검토'),
        ('DATA_ACCESS', 2, 'CISO', '보안팀', admin_user_id, 'admin@company.com', '시스템관리자', true, '정보보호책임자 승인')
        ON CONFLICT DO NOTHING;
        
        -- DASHBOARD 구독 승인 라인 템플릿
        INSERT INTO approval_line_templates (approval_type, step_order, approver_role, approver_department, approver_id, approver_email, approver_name, is_required, description)
        VALUES 
        ('DASHBOARD', 1, '팀장', '데이터분석팀', admin_user_id, 'admin@company.com', '시스템관리자', true, '대시보드 구독 팀장 승인')
        ON CONFLICT DO NOTHING;
    END IF;
END $$;

-- ============================================
-- STEP 6: 트리거 생성
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
DROP TRIGGER IF EXISTS update_users_updated_at ON users;
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_approval_line_templates_updated_at ON approval_line_templates;
CREATE TRIGGER update_approval_line_templates_updated_at BEFORE UPDATE ON approval_line_templates FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_approval_steps_updated_at ON approval_steps;
CREATE TRIGGER update_approval_steps_updated_at BEFORE UPDATE ON approval_steps FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- STEP 7: 검증 쿼리
-- ============================================

-- 생성된 테이블 및 데이터 확인
SELECT 'Users' as table_name, COUNT(*) as record_count FROM users
UNION ALL
SELECT 'Approval Line Templates', COUNT(*) FROM approval_line_templates
UNION ALL
SELECT 'Approval Steps', COUNT(*) FROM approval_steps;

-- 외래키 제약 조건 확인
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
    AND tc.table_name IN ('users', 'approval_line_templates', 'approval_steps')
ORDER BY tc.table_name;

-- 트랜잭션 커밋
COMMIT;

-- 성공 메시지
SELECT 'User references migration applied successfully!' as status;