-- ============================================================
-- ApprovalLine을 ApprovalStep으로 통합하는 마이그레이션 스크립트
-- ============================================================
-- 작성일: 2024-01-16
-- 목적: approval_lines 테이블을 approval_steps 테이블로 통합
-- 
-- 주요 변경사항:
-- 1. approver_id: String -> Long (User 테이블 FK)
-- 2. template_version 필드 추가 (템플릿 스냅샷 추적)
-- 3. version 필드 추가 (낙관적 잠금)
-- 4. is_required 필드 추가 (필수 승인 단계 여부)
-- 5. description 필드 추가 (단계 설명)
-- ============================================================

-- Step 1: approval_steps 테이블이 없으면 생성
CREATE TABLE IF NOT EXISTS approval_steps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    approval_id BIGINT NOT NULL,
    template_id BIGINT NOT NULL,
    template_version BIGINT NOT NULL DEFAULT 1,
    step_order INT NOT NULL,
    
    -- 승인자 정보 (User 참조키 사용)
    approver_id BIGINT NOT NULL,
    approver_email VARCHAR(255) NOT NULL,
    approver_name VARCHAR(100) NOT NULL,
    approver_role VARCHAR(100),
    approver_department VARCHAR(100),
    
    -- 단계 정보
    is_required BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT,
    
    -- 승인 상태
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    approved_at TIMESTAMP NULL,
    approval_comment TEXT,
    
    -- 동시성 제어
    version BIGINT NOT NULL DEFAULT 0,
    
    -- 감사 필드
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    
    -- 인덱스
    INDEX idx_approval_step_approval_id (approval_id),
    INDEX idx_approval_step_approver_id (approver_id),
    INDEX idx_approval_step_status (status),
    INDEX idx_approval_step_template (template_id, template_version),
    
    -- 외래키
    FOREIGN KEY (approval_id) REFERENCES approvals(id) ON DELETE CASCADE,
    FOREIGN KEY (template_id) REFERENCES approval_line_templates(id),
    FOREIGN KEY (approver_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Step 2: approval_lines 데이터를 approval_steps로 마이그레이션
-- 주의: approver_id가 String에서 Long으로 변경되므로 User 테이블과 매핑 필요
-- 이 스크립트는 예시이며, 실제 데이터에 맞게 수정 필요

-- 예시: 이메일 기반으로 User ID 매핑
INSERT INTO approval_steps (
    approval_id,
    template_id,
    template_version,
    step_order,
    approver_id,
    approver_email,
    approver_name,
    approver_role,
    approver_department,
    is_required,
    description,
    status,
    approved_at,
    approval_comment,
    version,
    created_at,
    updated_at,
    created_by,
    updated_by
)
SELECT 
    al.approval_id,
    al.template_id,
    1 AS template_version, -- 기본값 1로 설정
    al.step_order,
    COALESCE(u.id, 1) AS approver_id, -- User 테이블에서 ID 조회, 없으면 기본값 1
    COALESCE(u.email, al.approver_id) AS approver_email, -- 기존 approver_id를 이메일로 사용
    al.approver_name,
    al.approver_role,
    al.approver_department,
    TRUE AS is_required, -- 기본값 true
    NULL AS description, -- 기존 데이터에는 없음
    al.status,
    al.approved_at,
    al.approval_comment,
    0 AS version, -- 초기 버전 0
    al.created_at,
    al.updated_at,
    al.created_by,
    al.updated_by
FROM approval_lines al
LEFT JOIN users u ON u.email = al.approver_id OR u.username = al.approver_id
WHERE NOT EXISTS (
    SELECT 1 FROM approval_steps ast 
    WHERE ast.approval_id = al.approval_id 
    AND ast.step_order = al.step_order
);

-- Step 3: 데이터 검증
-- approval_lines와 approval_steps의 레코드 수 비교
SELECT 
    'approval_lines' AS table_name, 
    COUNT(*) AS record_count 
FROM approval_lines
UNION ALL
SELECT 
    'approval_steps' AS table_name, 
    COUNT(*) AS record_count 
FROM approval_steps;

-- Step 4: approval_lines 테이블 백업 (선택사항)
-- 마이그레이션 후 문제가 없으면 삭제 가능
CREATE TABLE IF NOT EXISTS approval_lines_backup AS SELECT * FROM approval_lines;

-- Step 5: approval_lines 테이블 삭제 (신중하게!)
-- 백업 확인 후 실행
-- DROP TABLE IF EXISTS approval_lines;

-- ============================================================
-- 롤백 스크립트 (문제 발생 시)
-- ============================================================
-- approval_lines 테이블 복원
-- CREATE TABLE approval_lines AS SELECT * FROM approval_lines_backup;
-- 
-- approval_steps 데이터 삭제
-- DELETE FROM approval_steps WHERE created_at >= '마이그레이션_시작_시간';
-- ============================================================
