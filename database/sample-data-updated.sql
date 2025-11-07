-- LINA Data Portal Sample Data (Updated with User References)
-- PostgreSQL Sample Data Insertion

-- ============================================
-- 1. USERS SAMPLE DATA (NEW)
-- ============================================
INSERT INTO users (entra_object_id, email, name, display_name, employee_id, department, job_title, manager_email, phone_number, office_location, status, is_admin) VALUES
('12345678-1234-1234-1234-123456789abc', 'admin@company.com', '관리자', '시스템 관리자', 'EMP001', 'IT팀', '시스템 관리자', null, '02-1234-5678', '서울 본사 15층', 'ACTIVE', true),
('23456789-2345-2345-2345-234567890bcd', 'kim.analyst@company.com', '김분석가', '김분석가 (데이터팀)', 'EMP002', '데이터분석팀', '선임 데이터 분석가', 'park.manager@company.com', '02-1234-5679', '서울 본사 12층', 'ACTIVE', false),
('34567890-3456-3456-3456-345678901cde', 'park.manager@company.com', '박매니저', '박매니저 (데이터팀장)', 'EMP003', '데이터분석팀', '팀장', 'choi.director@company.com', '02-1234-5680', '서울 본사 12층', 'ACTIVE', false),
('45678901-4567-4567-4567-456789012def', 'lee.security@company.com', '이보안', '이보안 (보안팀)', 'EMP004', '보안팀', '보안 담당자', 'jung.ciso@company.com', '02-1234-5681', '서울 본사 14층', 'ACTIVE', false),
('56789012-5678-5678-5678-567890123ef0', 'jung.ciso@company.com', '정보안', '정보안 (CISO)', 'EMP005', '보안팀', '정보보호책임자', null, '02-1234-5682', '서울 본사 14층', 'ACTIVE', true),
('67890123-6789-6789-6789-678901234f01', 'choi.director@company.com', '최이사', '최이사 (데이터본부)', 'EMP006', '데이터본부', '이사', null, '02-1234-5683', '서울 본사 16층', 'ACTIVE', true),
('78901234-7890-7890-7890-789012345012', 'user1@company.com', '사용자1', '사용자1 (영업팀)', 'EMP007', '영업팀', '영업 담당자', 'kim.sales@company.com', '02-1234-5684', '서울 본사 10층', 'ACTIVE', false),
('89012345-8901-8901-8901-890123456123', 'user2@company.com', '사용자2', '사용자2 (마케팅팀)', 'EMP008', '마케팅팀', '마케팅 전문가', 'lee.marketing@company.com', '02-1234-5685', '서울 본사 11층', 'ACTIVE', false),
('90123456-9012-9012-9012-901234567234', 'user3@company.com', '사용자3', '사용자3 (재무팀)', 'EMP009', '재무팀', '재무 분석가', 'park.finance@company.com', '02-1234-5686', '서울 본사 13층', 'ACTIVE', false),
('01234567-0123-0123-0123-012345678345', 'user4@company.com', '사용자4', '사용자4 (인사팀)', 'EMP010', '인사팀', 'HR 전문가', 'choi.hr@company.com', '02-1234-5687', '서울 본사 9층', 'ACTIVE', false),
('11234567-1123-1123-1123-112345678456', 'user5@company.com', '사용자5', '사용자5 (운영팀)', 'EMP011', '운영팀', '운영 담당자', 'kim.ops@company.com', '02-1234-5688', '서울 본사 8층', 'ACTIVE', false);

-- ============================================
-- 2. DASHBOARDS SAMPLE DATA (Updated with User references)
-- ============================================
INSERT INTO dashboards (title, description, category, type, rating, downloads, image, config, dashboard_url, contains_sensitive_data, created_by_id, created_by_email, created_by_name, is_active) VALUES
('매출 분석 대시보드', '실시간 매출 현황과 트렌드를 한눈에 확인할 수 있는 종합 대시보드', 'sales', 'TEMPLATE', 4.8, 2100, '/sampleDashboard.png', '{"charts": ["sales_trend", "product_performance"]}', '/dashboards/sales-analysis', false, 1, 'admin@company.com', '관리자', true),
('고객 행동 분석', '고객 여정과 행동 패턴을 시각화하여 인사이트를 제공하는 대시보드', 'customer', 'TEMPLATE', 4.6, 1800, '/sampleDashboard.png', '{"charts": ["customer_journey", "behavior_analysis"]}', '/dashboards/customer-behavior', true, 2, 'kim.analyst@company.com', '김분석가', true),
('운영 효율성 모니터', '시스템 성능과 운영 지표를 실시간으로 모니터링하는 대시보드', 'operations', 'TEMPLATE', 4.9, 1500, '/sampleDashboard.png', '{"charts": ["system_performance", "operational_kpis"]}', '/dashboards/operations-monitor', false, 1, 'admin@company.com', '관리자', true),
('Claim Summary', '보험 청구 현황과 처리 상태를 종합적으로 관리하는 대시보드', 'insurance', 'TEMPLATE', 4.7, 1300, '/sampleDashboard2.png', '{"charts": ["claim_status", "processing_time"]}', '/dashboards/claim-summary', true, 2, 'kim.analyst@company.com', '김분석가', true),
('APE Report', '연간보험료 환산 실적을 분석하는 핵심 KPI 대시보드', 'kpi', 'TEMPLATE', 4.5, 980, '/sampleDashboard.png', '{"charts": ["ape_performance", "target_achievement"]}', '/dashboards/ape-report', false, 1, 'admin@company.com', '관리자', true),
('PLR Report', '보험계약 지속률 분석을 위한 핵심 지표 대시보드', 'kpi', 'TEMPLATE', 4.3, 756, '/sampleDashboard.png', '{"charts": ["persistency_rate", "lapse_analysis"]}', '/dashboards/plr-report', false, 1, 'admin@company.com', '관리자', true),
('UW Decline', '언더라이팅 거절 현황 분석 대시보드', 'underwriting', 'TEMPLATE', 4.4, 642, '/sampleDashboard.png', '{"charts": ["decline_rate", "risk_analysis"]}', '/dashboards/uw-decline', true, 2, 'kim.analyst@company.com', '김분석가', true),
('Daily Sales Report', '일일 영업 실적 현황을 실시간으로 모니터링하는 대시보드', 'sales', 'TEMPLATE', 4.6, 1100, '/sampleDashboard.png', '{"charts": ["daily_sales", "team_performance"]}', '/dashboards/daily-sales', false, 1, 'admin@company.com', '관리자', true);

-- Dashboard Tags
INSERT INTO dashboard_tags (dashboard_id, tag) VALUES
(1, '매출'), (1, '분석'), (1, '트렌드'), (1, 'KPI'),
(2, '고객'), (2, '행동분석'), (2, '마케팅'), (2, '인사이트'),
(3, '운영'), (3, '모니터링'), (3, '성능'), (3, '인프라'),
(4, '청구'), (4, 'claim'), (4, '보험'), (4, '처리현황'),
(5, 'APE'), (5, 'KPI'), (5, '영업실적'), (5, '보험료'),
(6, 'PLR'), (6, '지속률'), (6, '해지율'), (6, 'KPI'),
(7, '언더라이팅'), (7, '거절률'), (7, '인수심사'), (7, '리스크'),
(8, '일일실적'), (8, '영업'), (8, '계약'), (8, '실시간');

-- Dashboard Subscriptions (My Dashboard) - Updated with User references
INSERT INTO dashboard_subscriptions (user_id, user_email, dashboard_id, display_order, is_favorite, custom_title, subscribed_at, last_accessed) VALUES
(7, 'user1@company.com', 1, 1, true, '내 매출 대시보드', '2024-01-15 09:00:00', '2024-01-20 14:30:00'),
(7, 'user1@company.com', 4, 2, true, '청구 현황', '2024-01-16 10:00:00', '2024-01-20 11:15:00'),
(7, 'user1@company.com', 8, 3, false, null, '2024-01-17 11:00:00', '2024-01-19 16:45:00'),
(8, 'user2@company.com', 2, 1, true, '고객 분석', '2024-01-18 14:00:00', '2024-01-20 09:20:00'),
(8, 'user2@company.com', 3, 2, false, null, '2024-01-19 15:00:00', '2024-01-20 13:10:00');

-- ============================================
-- 3. SECURITY POLICIES SAMPLE DATA (NEW)
-- ============================================

-- Masking Policies 샘플 데이터
INSERT INTO masking_policies (policy_name, display_name, description, masking_type, mask_pattern, applicable_columns, is_active) VALUES
('PII_PARTIAL_MASKING', '개인정보 부분 마스킹', '개인식별정보의 일부를 마스킹하는 정책', 'PARTIAL', '***-**-####', '["name", "phone", "email", "ssn"]', true),
('FINANCIAL_FULL_MASKING', '금융정보 전체 마스킹', '금융 관련 정보를 완전히 마스킹하는 정책', 'FULL', '********', '["account_number", "card_number", "balance"]', true),
('EMAIL_HASH_MASKING', '이메일 해시 마스킹', '이메일 주소를 해시값으로 변환하는 정책', 'HASH', null, '["email", "contact_email"]', true),
('SENSITIVE_ENCRYPT', '민감정보 암호화', '민감정보를 암호화하여 저장하는 정책', 'ENCRYPT', null, '["password", "token", "secret"]', true);

-- Policy Sets 샘플 데이터
INSERT INTO policy_sets (policy_set_name, display_name, description, category, severity, masking_policy_ids, applicable_target_types, applicable_sensitivity_levels, required_approvals, approval_timeout_hours, is_active, is_default) VALUES
('FINANCIAL_DATA_POLICY', '금융 데이터 보안 정책', '금융 관련 데이터에 적용되는 종합 보안 정책', 'DATA_PROTECTION', 'HIGH', '[2,4]', '["DATASET", "TABLE", "VIEW"]', '["FINANCIAL", "SENSITIVE", "CONFIDENTIAL"]', 2, 48, true, false),
('PII_PROTECTION_POLICY', '개인정보보호 정책', '개인식별정보 보호를 위한 정책 세트', 'PRIVACY', 'CRITICAL', '[1,3]', '["DATASET", "TABLE", "VIEW", "REPORT"]', '["PII", "PHI", "SENSITIVE"]', 3, 24, true, true),
('GENERAL_DATA_POLICY', '일반 데이터 정책', '일반적인 데이터 접근을 위한 기본 정책', 'ACCESS_CONTROL', 'MEDIUM', '[1]', '["DATASET", "DASHBOARD", "REPORT"]', '["NORMAL", "INTERNAL"]', 1, 72, true, false),
('COMPLIANCE_POLICY', '규정 준수 정책', '규제 요구사항 준수를 위한 정책 세트', 'COMPLIANCE', 'HIGH', '[1,2,3,4]', '["DATASET", "TABLE", "VIEW", "REPORT", "DASHBOARD"]', '["REGULATORY", "FINANCIAL", "PII", "PHI"]', 2, 48, true, false);

-- ============================================
-- 4. APPROVAL LINE TEMPLATES SAMPLE DATA (NEW)
-- ============================================
INSERT INTO approval_line_templates (approval_type, step_order, approver_role, approver_department, approver_id, approver_email, approver_name, is_required, is_active, description) VALUES
-- DASHBOARD_DEPLOY 승인 라인
('DASHBOARD_DEPLOY', 1, '팀장', '데이터분석팀', 3, 'park.manager@company.com', '박매니저', true, true, '데이터팀 팀장 승인'),
('DASHBOARD_DEPLOY', 2, '시스템 관리자', 'IT팀', 1, 'admin@company.com', '관리자', true, true, 'IT팀 시스템 검토'),
('DASHBOARD_DEPLOY', 3, '보안 담당자', '보안팀', 4, 'lee.security@company.com', '이보안', true, true, '보안 검토 및 최종 승인'),

-- DATA_ACCESS 승인 라인
('DATA_ACCESS', 1, '보안 담당자', '보안팀', 4, 'lee.security@company.com', '이보안', true, true, '데이터 접근 보안 검토'),
('DATA_ACCESS', 2, 'CISO', '보안팀', 5, 'jung.ciso@company.com', '정보안', true, true, '정보보호책임자 승인'),
('DATA_ACCESS', 3, '이사', '데이터본부', 6, 'choi.director@company.com', '최이사', false, true, '고위험 데이터 접근 시 이사 승인'),

-- DASHBOARD 구독 승인 라인
('DASHBOARD', 1, '팀장', '데이터분석팀', 3, 'park.manager@company.com', '박매니저', true, true, '대시보드 구독 팀장 승인'),
('DASHBOARD', 2, '보안 담당자', '보안팀', 4, 'lee.security@company.com', '이보안', false, true, '민감정보 포함 시 보안 검토');

-- ============================================
-- 5. APPROVALS SAMPLE DATA (Updated with Target Objects)
-- ============================================
INSERT INTO approvals (
    type, title, description, status, priority, 
    requester_id, requester_email, requester_name, 
    reviewer_id, reviewer_email, reviewer_name,
    target_type, target_id, target_name, target_description,
    access_scope, sensitivity_level,
    masking_policy_id, policy_set_id,
    requires_masking, requires_audit_log, access_duration_hours,
    business_justification, data_usage_purpose,
    request_date, review_date, review_comment
) VALUES
(
    'DASHBOARD_DEPLOY', 'Sales Dashboard 배포 요청', '영업팀 대시보드를 프로덕션 환경에 배포 요청합니다.', 
    'PENDING', 'HIGH', 
    7, 'user1@company.com', '사용자1', 
    null, null, null,
    'DASHBOARD', 'dashboard_001', '영업 실적 대시보드', '월별 영업 실적 및 KPI 모니터링 대시보드',
    'DEPLOY', 'INTERNAL',
    null, 3,
    false, true, 720,
    '영업팀 성과 관리 및 실시간 모니터링 필요', '월별 영업 목표 달성률 추적 및 분석',
    '2024-01-15 09:30:00', null, null
),
(
    'DATA_ACCESS', '고객 데이터 접근 권한 요청', '마케팅 캠페인 분석을 위한 고객 데이터베이스 접근 권한이 필요합니다.', 
    'APPROVED', 'HIGH', 
    8, 'user2@company.com', '사용자2', 
    1, 'admin@company.com', '관리자',
    'DATASET', 'customer_dataset_001', '고객 정보 데이터셋', '고객 기본정보 및 구매 이력 데이터',
    'MASKED_READ', 'PII',
    1, 2,
    true, true, 168,
    '타겟 마케팅 캠페인 기획 및 고객 세그먼트 분석', '개인정보 보호를 위한 마스킹된 고객 데이터 분석',
    '2024-01-10 14:20:00', '2024-01-12 16:45:00', '마스킹 정책 적용 조건으로 승인합니다.'
),
(
    'DATA_ACCESS', '금융 데이터 테이블 접근 권한', '리스크 분석을 위한 금융 거래 데이터 접근 권한을 요청합니다.', 
    'REJECTED', 'CRITICAL', 
    9, 'user3@company.com', '사용자3', 
    4, 'lee.security@company.com', '이보안',
    'TABLE', 'financial_transactions', '금융 거래 테이블', '고객 금융 거래 내역 및 잔액 정보',
    'READ', 'FINANCIAL',
    2, 1,
    true, true, 72,
    '신용 리스크 모델 개발 및 검증', '고객 신용도 평가 모델 훈련용 데이터',
    '2024-01-08 11:15:00', '2024-01-09 09:30:00', '추가 보안 승인 및 상급자 승인이 필요합니다.'
),
(
    'DASHBOARD', '재무 대시보드 구독 요청', '재무팀 전용 대시보드 구독을 요청합니다.', 
    'PENDING', 'MEDIUM', 
    10, 'user4@company.com', '사용자4', 
    null, null, null,
    'DASHBOARD', 'finance_dashboard_001', '재무 현황 대시보드', '회사 재무 상태 및 예산 집행 현황',
    'READ', 'CONFIDENTIAL',
    null, 1,
    false, true, 2160,
    '재무 현황 모니터링 및 예산 관리 업무', '월별 재무 보고서 작성 및 예산 분석',
    '2024-01-16 10:45:00', null, null
),
(
    'MODEL', 'ML 모델 배포 권한 요청', '고객 이탈 예측 모델을 프로덕션 환경에 배포하고자 합니다.', 
    'PENDING', 'HIGH', 
    11, 'user5@company.com', '사용자5', 
    null, null, null,
    'MODEL', 'churn_prediction_model_v3', '고객 이탈 예측 모델', '머신러닝 기반 고객 이탈 가능성 예측 모델',
    'DEPLOY', 'SENSITIVE',
    1, 3,
    true, true, 1440,
    '고객 유지 전략 수립 및 이탈 방지 캠페인 기획', '고객 이탈 위험도 실시간 모니터링 및 예방 조치',
    '2024-01-15 13:20:00', null, null
);

-- ============================================
-- 6. APPROVAL STEPS SAMPLE DATA (NEW - 템플릿 스냅샷 기반)
-- ============================================
INSERT INTO approval_steps (approval_id, template_id, template_version, step_order, approver_id, approver_email, approver_name, approver_role, approver_department, is_required, description, status, approved_at, approval_comment) VALUES
-- Sales Dashboard 배포 요청 (approval_id: 1) - DASHBOARD_DEPLOY 템플릿 기반
(1, 1, 0, 1, 3, 'park.manager@company.com', '박매니저', '팀장', '데이터분석팀', true, '데이터팀 팀장 승인', 'APPROVED', '2024-01-15 15:30:00', '영업팀 검토 완료'),
(1, 2, 0, 2, 1, 'admin@company.com', '관리자', '시스템 관리자', 'IT팀', true, 'IT팀 시스템 검토', 'PENDING', null, null),
(1, 3, 0, 3, 4, 'lee.security@company.com', '이보안', '보안 담당자', '보안팀', true, '보안 검토 및 최종 승인', 'PENDING', null, null),

-- 민감정보 포함 대시보드 구독 (approval_id: 2) - DASHBOARD 템플릿 기반
(2, 4, 0, 1, 3, 'park.manager@company.com', '박매니저', '팀장', '데이터분석팀', true, '대시보드 구독 팀장 승인', 'APPROVED', '2024-01-11 10:15:00', '마케팅팀 승인'),
(2, 5, 0, 2, 4, 'lee.security@company.com', '이보안', '보안 담당자', '보안팀', false, '민감정보 포함 시 보안 검토', 'APPROVED', '2024-01-12 14:20:00', '보안 검토 완료'),

-- Customer DB 테이블 접근 권한 (approval_id: 3) - DATA_ACCESS 템플릿 기반
(3, 6, 0, 1, 4, 'lee.security@company.com', '이보안', '보안 담당자', '보안팀', true, '데이터 접근 보안 검토', 'REJECTED', '2024-01-09 09:30:00', '추가 보안 승인 필요'),
(3, 7, 0, 2, 5, 'jung.ciso@company.com', '정보안', 'CISO', '보안팀', true, '정보보호책임자 승인', 'PENDING', null, null),
(3, 8, 0, 3, 6, 'choi.director@company.com', '최이사', '이사', '데이터본부', false, '고위험 데이터 접근 시 이사 승인', 'PENDING', null, null),

-- 재무 대시보드 구독 요청 (approval_id: 4) - DASHBOARD 템플릿 기반
(4, 4, 0, 1, 3, 'park.manager@company.com', '박매니저', '팀장', '데이터분석팀', true, '대시보드 구독 팀장 승인', 'PENDING', null, null),

-- HR Analytics Dashboard 배포 (approval_id: 5) - DASHBOARD_DEPLOY 템플릿 기반
(5, 1, 0, 1, 3, 'park.manager@company.com', '박매니저', '팀장', '데이터분석팀', true, '데이터팀 팀장 승인', 'PENDING', null, null),
(5, 2, 0, 2, 1, 'admin@company.com', '관리자', '시스템 관리자', 'IT팀', true, 'IT팀 시스템 검토', 'PENDING', null, null),
(5, 3, 0, 3, 4, 'lee.security@company.com', '이보안', '보안 담당자', '보안팀', true, '보안 검토 및 최종 승인', 'PENDING', null, null);

-- ============================================
-- 6. REPORTS SAMPLE DATA (Updated with User references)
-- ============================================
INSERT INTO reports (title, description, category, type, file_path, file_name, file_size, contains_sensitive_data, created_by_id, created_by_email, created_by_name) VALUES
('보험통계월보 25년7월호', '보험업계 주요 통계 및 동향 분석 (367호)', 'monthly', 'MONTHLY_REPORT', '/보험통계월보_25년 7월호(367호).pdf', '보험통계월보_25년7월호.pdf', 2456789, false, 2, 'kim.analyst@company.com', '김분석가'),
('월간생명보험 25년9월호', '생명보험업계 월간 리포트', 'monthly', 'MONTHLY_REPORT', '/월간생명보험_25년9월호.pdf', '월간생명보험_25년9월호.pdf', 1834567, false, 2, 'kim.analyst@company.com', '김분석가'),
('고객통합리포트', '고객 행동 패턴 및 세그먼트 분석 종합 리포트', 'customer', 'CUSTOMER_ANALYSIS', '/reports/customer-integrated-report.pdf', '고객통합리포트.pdf', 2456789, true, 2, 'kim.analyst@company.com', '김분석가'),
('고객DB모니터링리포트', '고객 데이터베이스 품질 및 정합성 모니터링 결과', 'monitoring', 'MONITORING', '/reports/customer-db-monitoring.pdf', '고객DB모니터링리포트.pdf', 1834567, true, 1, 'admin@company.com', '관리자'),
('Monthly KPI 리포트', '9월 주요 성과 지표 및 목표 달성률 분석', 'kpi', 'KPI_REPORT', '/reports/monthly-kpi-report.pptx', 'Monthly_KPI_리포트.pptx', 3245678, false, 2, 'kim.analyst@company.com', '김분석가'),
('보험상품 판매현황 분석', '상품별 판매 실적 및 트렌드 분석 리포트', 'monthly', 'MONTHLY_REPORT', '/reports/product-sales-analysis.docx', '보험상품_판매현황_분석.docx', 1567890, false, 2, 'kim.analyst@company.com', '김분석가'),
('고객 이탈 예측 모델 성과', 'ML 기반 고객 이탈 예측 모델의 성과 평가 리포트', 'customer', 'CUSTOMER_ANALYSIS', '/reports/churn-prediction-model.pdf', '고객_이탈_예측_모델_성과.pdf', 2789012, true, 2, 'kim.analyst@company.com', '김분석가'),
('시스템 성능 모니터링', '데이터 플랫폼 시스템 성능 및 안정성 모니터링', 'monitoring', 'MONITORING', '/reports/system-performance-monitoring.pdf', '시스템_성능_모니터링.pdf', 1456789, false, 1, 'admin@company.com', '관리자');

-- Report Tags
INSERT INTO report_tags (report_id, tag) VALUES
(1, '보험통계'), (1, '월보'), (1, '업계동향'),
(2, '생명보험'), (2, '월간'), (2, '업계리포트'),
(3, '고객분석'), (3, '세그먼트'), (3, '행동패턴'),
(4, '데이터품질'), (4, '모니터링'), (4, 'DB'),
(5, 'KPI'), (5, '성과지표'), (5, '월간'),
(6, '상품분석'), (6, '판매실적'), (6, '트렌드'),
(7, '머신러닝'), (7, '이탈예측'), (7, '모델성과'),
(8, '시스템성능'), (8, '안정성'), (8, '인프라');

-- ============================================
-- 7. DATA TABLES (CATALOG) SAMPLE DATA
-- ============================================
INSERT INTO data_tables (table_name, schema_name, description, category, data_type, record_count, column_count, is_favorite, last_updated) VALUES
('Property Claims Database', 'insurance', '종합 재산보험 청구 데이터베이스 - 주거용 및 상업용 부동산', 'property', 'DATASET', 2400000, 25, false, '2024-01-20 14:30:00'),
('Cyber Risk Assessment Data', 'security', '기업 고객 대상 사이버보안 사고 데이터 및 위험 평가 지표', 'cyber', 'DATASET', 1800000, 18, true, '2024-01-20 09:15:00'),
('Marine Insurance Portfolio', 'marine', '글로벌 해상보험 데이터 - 화물, 선체, 배상책임 보장', 'marine', 'DATASET', 1200000, 22, false, '2024-01-19 16:45:00'),
('Claims Performance Analytics', 'analytics', '실시간 청구 처리 효율성 및 고객 만족도 지표 모니터링', 'claims', 'DASHBOARD', 3100000, 15, true, '2024-01-20 10:30:00'),
('Underwriting Risk Monitor', 'underwriting', '포괄적 위험 평가 대시보드 - 노출 분석 및 가격 모델', 'risk', 'DASHBOARD', 2700000, 20, false, '2024-01-20 11:00:00'),
('Cyber Threat Intelligence', 'security', '사이버보험 상품 대상 위협 환경 및 사고 트렌드 분석', 'cyber', 'DASHBOARD', 1900000, 12, false, '2024-01-20 07:45:00'),
('Q1 2025 Property Loss Report', 'reports', '분기별 재산보험 손실 분석 - 재해 사건 및 지역별 영향 평가', 'property', 'REPORT', 4200000, 8, true, '2024-01-18 13:20:00'),
('Cyber Insurance Market Analysis', 'reports', '사이버보험 시장 연구 보고서 - 신흥 위험 및 경쟁 환경 분석', 'cyber', 'REPORT', 3500000, 10, false, '2024-01-17 15:30:00'),
('Marine Claims Severity Study', 'reports', '해상보험 청구 심각도 패턴 심층 분석 - 화물 손상 및 환경 요인', 'marine', 'REPORT', 2100000, 14, false, '2024-01-13 11:45:00'),
('Commercial Auto Fleet Data', 'fleet', '상업용 차량 보험 데이터 - 차량 사양, 운전자 프로필, 텔레매틱스', 'auto', 'DATASET', 1600000, 28, false, '2024-01-20 08:15:00'),
('Regulatory Compliance Monitor', 'compliance', '다중 관할권 보험 규제 준수 추적 대시보드', 'compliance', 'DASHBOARD', 2300000, 16, true, '2024-01-20 06:30:00'),
('ESG Impact Assessment', 'esg', '보험 운영 및 투자 포트폴리오 ESG 영향 분석', 'esg', 'REPORT', 1700000, 12, false, '2024-01-15 14:20:00');

-- Data Table Tags
INSERT INTO data_table_tags (table_id, tag) VALUES
(1, 'property'), (1, 'claims'), (1, 'residential'), (1, 'commercial'),
(2, 'cyber'), (2, 'risk'), (2, 'security'), (2, 'enterprise'),
(3, 'marine'), (3, 'cargo'), (3, 'shipping'), (3, 'liability'),
(4, 'claims'), (4, 'performance'), (4, 'analytics'), (4, 'kpi'),
(5, 'underwriting'), (5, 'risk'), (5, 'pricing'), (5, 'portfolio'),
(6, 'cyber'), (6, 'threat'), (6, 'intelligence'), (6, 'security'),
(7, 'quarterly'), (7, 'property'), (7, 'loss'), (7, 'catastrophic'),
(8, 'market'), (8, 'cyber'), (8, 'trends'), (8, 'competitive'),
(9, 'marine'), (9, 'severity'), (9, 'cargo'), (9, 'environmental'),
(10, 'commercial'), (10, 'auto'), (10, 'fleet'), (10, 'telematics'),
(11, 'compliance'), (11, 'regulatory'), (11, 'reporting'), (11, 'alerts'),
(12, 'esg'), (12, 'sustainability'), (12, 'environmental'), (12, 'governance');

-- Data Table Columns
INSERT INTO data_table_columns (table_id, column_name) VALUES
(1, 'claim_id'), (1, 'policy_number'), (1, 'property_type'), (1, 'claim_amount'), (1, 'incident_date'),
(2, 'company_id'), (2, 'risk_score'), (2, 'incident_type'), (2, 'severity_level'), (2, 'mitigation_status'),
(3, 'vessel_id'), (3, 'cargo_type'), (3, 'route'), (3, 'coverage_amount'), (3, 'claim_status'),
(4, 'claim_id'), (4, 'processing_time'), (4, 'customer_satisfaction'), (4, 'resolution_status'), (4, 'cost_efficiency'),
(5, 'application_id'), (5, 'risk_category'), (5, 'premium_rate'), (5, 'coverage_limit'), (5, 'underwriter_decision');

-- ============================================
-- 8. ML MODELS SAMPLE DATA (Updated with User references)
-- ============================================
INSERT INTO ml_models (name, description, type, status, version, accuracy, f1_score, response_time, model_path, created_by_id, created_by_email, created_by_name, deployed_at) VALUES
('Fraud Detection Model', '보험금 청구 사기 탐지를 위한 머신러닝 모델', 'FRAUD_DETECTION', 'DEPLOYED', '2.1', 94.2, 0.89, 150, '/models/fraud_detection_v2.1.pkl', 2, 'kim.analyst@company.com', '김분석가', '2024-01-10 14:30:00'),
('Risk Assessment Model', '보험 인수 심사를 위한 리스크 평가 모델', 'RISK_SCORING', 'TRAINING', '1.3', 87.6, 0.83, 200, '/models/risk_assessment_v1.3.pkl', 2, 'kim.analyst@company.com', '김분석가', null),
('Customer Churn Prediction', '고객 이탈 가능성 예측 모델', 'CHURN_PREDICTION', 'DEPLOYED', '3.0', 91.8, 0.87, 120, '/models/churn_prediction_v3.0.pkl', 2, 'kim.analyst@company.com', '김분석가', '2024-01-05 09:15:00'),
('Claim Amount Prediction', '보험금 청구 금액 예측 모델', 'UNDERWRITING', 'TESTING', '1.5', 89.3, 0.85, 180, '/models/claim_amount_v1.5.pkl', 2, 'kim.analyst@company.com', '김분석가', null),
('Customer Segmentation', '고객 세그먼트 분류 모델', 'CUSTOMER_SEGMENTATION', 'DEPLOYED', '2.2', 88.7, 0.82, 95, '/models/customer_segmentation_v2.2.pkl', 2, 'kim.analyst@company.com', '김분석가', '2024-01-12 11:45:00');

-- Model Features
INSERT INTO model_features (model_id, feature_name, importance) VALUES
-- Fraud Detection Model features
(1, 'transaction_amount', 0.85), (1, 'user_behavior_score', 0.72), (1, 'time_of_day', 0.58), (1, 'location_risk', 0.45), (1, 'payment_method', 0.32),
-- Risk Assessment Model features  
(2, 'credit_score', 0.92), (2, 'income_level', 0.78), (2, 'employment_history', 0.65), (2, 'debt_ratio', 0.54), (2, 'age', 0.41),
-- Customer Churn Prediction features
(3, 'policy_tenure', 0.88), (3, 'claim_frequency', 0.76), (3, 'premium_payment_history', 0.69), (3, 'customer_service_interactions', 0.52), (3, 'policy_changes', 0.38),
-- Claim Amount Prediction features
(4, 'claim_type', 0.91), (4, 'policy_coverage', 0.84), (4, 'customer_history', 0.67), (4, 'incident_severity', 0.73), (4, 'geographic_location', 0.49),
-- Customer Segmentation features
(5, 'annual_premium', 0.89), (5, 'policy_count', 0.77), (5, 'age_group', 0.64), (5, 'geographic_region', 0.56), (5, 'product_mix', 0.43);

-- Model Tags
INSERT INTO model_tags (model_id, tag) VALUES
(1, 'fraud'), (1, 'detection'), (1, 'classification'), (1, 'production'),
(2, 'risk'), (2, 'scoring'), (2, 'underwriting'), (2, 'regression'),
(3, 'churn'), (3, 'prediction'), (3, 'customer'), (3, 'retention'),
(4, 'claim'), (4, 'amount'), (4, 'prediction'), (4, 'pricing'),
(5, 'segmentation'), (5, 'customer'), (5, 'clustering'), (5, 'marketing');

-- ============================================
-- 9. STT (SPEECH-TO-TEXT) SAMPLE DATA
-- ============================================

-- Call Records Table
INSERT INTO call_records (call_id, customer_id, customer_name, customer_phone, agent_id, agent_name, agent_department, customer_type, call_type, call_duration, audio_file_path, transcript, processing_status, call_date, processed_at) VALUES
('CALL_20240115_001', 'CUST_12345', '김고객', '010-1234-5678', 'AGENT_001', '이상담', '고객센터', 'EXISTING_CUSTOMER', 'CLAIM_CONSULTATION', 1200, '/audio/call_20240115_001.wav', '안녕하세요. 보험금 청구 관련해서 문의드리고 싶습니다...', 'COMPLETED', '2024-01-15 09:30:00', '2024-01-15 10:45:00'),
('CALL_20240115_002', 'CUST_67890', '박고객', '010-9876-5432', 'AGENT_002', '최상담', '영업팀', 'PROSPECT', 'OUTBOUND_SALES', 900, '/audio/call_20240115_002.wav', '안녕하세요. 새로운 보험 상품에 대해 안내드리고자 연락드렸습니다...', 'COMPLETED', '2024-01-15 14:20:00', '2024-01-15 15:10:00'),
('CALL_20240116_001', 'CUST_11111', '정고객', '010-1111-2222', 'AGENT_001', '이상담', '고객센터', 'VIP_CUSTOMER', 'POLICY_CONSULTATION', 1800, '/audio/call_20240116_001.wav', '보험 약관 변경 사항에 대해 자세히 알고 싶습니다...', 'COMPLETED', '2024-01-16 11:00:00', '2024-01-16 12:30:00');

-- Call Segments Table
INSERT INTO call_segments (call_record_id, start_time, end_time, speaker, text, confidence_score) VALUES
(1, 0, 15, 'AGENT', '안녕하세요. 라이나생명 고객센터입니다. 무엇을 도와드릴까요?', 0.95),
(1, 15, 45, 'CUSTOMER', '안녕하세요. 보험금 청구 관련해서 문의드리고 싶습니다. 지난주에 교통사고가 났는데요.', 0.92),
(1, 45, 75, 'AGENT', '교통사고로 인한 보험금 청구 건이시군요. 먼저 고객님의 성함과 증권번호를 확인해주시겠어요?', 0.94),
(2, 0, 20, 'AGENT', '안녕하세요. 라이나생명 박고객님, 새로운 보험 상품 안내차 연락드렸습니다.', 0.93),
(2, 20, 50, 'CUSTOMER', '아, 네. 어떤 상품인지 궁금하네요. 간단히 설명해주실 수 있나요?', 0.91);

-- Keyword Matches Table
INSERT INTO keyword_matches (call_record_id, keyword, category, match_count, first_occurrence_time, context_text, speaker, sentiment_score) VALUES
(1, '보험금', 'CLAIM', 3, 25, '보험금 청구 관련해서 문의드리고 싶습니다', 'CUSTOMER', 0.1),
(1, '교통사고', 'CLAIM', 2, 35, '지난주에 교통사고가 났는데요', 'CUSTOMER', -0.2),
(1, '청구', 'CLAIM', 2, 25, '보험금 청구 관련해서', 'CUSTOMER', 0.0),
(2, '새로운 상품', 'PRODUCT', 1, 15, '새로운 보험 상품 안내차', 'AGENT', 0.3),
(2, '궁금', 'EMOTION_POSITIVE', 1, 35, '어떤 상품인지 궁금하네요', 'CUSTOMER', 0.4),
(3, '약관 변경', 'POLICY', 1, 30, '보험 약관 변경 사항에 대해', 'CUSTOMER', 0.1);

-- ============================================
-- 10. USER TOKENS SAMPLE DATA (Updated with User references)
-- ============================================
INSERT INTO user_tokens (user_id, user_email, token_type, encrypted_token, workspace_url, cluster_id, warehouse_id, is_active, created_at, last_used_at, expires_at) VALUES
(2, 'kim.analyst@company.com', 'DATABRICKS', 'encrypted_token_12345', 'https://adb-workspace.azuredatabricks.net', 'cluster-001', 'warehouse-001', true, '2024-01-10 09:00:00', '2024-01-20 14:30:00', '2024-07-10 09:00:00'),
(7, 'user1@company.com', 'DATABRICKS', 'encrypted_token_67890', 'https://adb-workspace.azuredatabricks.net', 'cluster-002', 'warehouse-002', true, '2024-01-15 10:30:00', '2024-01-19 16:45:00', '2024-07-15 10:30:00'),
(8, 'user2@company.com', 'DATABRICKS', 'encrypted_token_abcde', 'https://adb-workspace.azuredatabricks.net', 'cluster-001', 'warehouse-001', false, '2024-01-05 14:20:00', '2024-01-18 11:15:00', '2024-07-05 14:20:00');

-- ============================================
-- VERIFICATION QUERIES
-- ============================================

-- 사용자별 승인 요청 현황 확인
-- SELECT u.name, a.title, a.status, a.created_at 
-- FROM approvals a 
-- JOIN users u ON a.requester_id = u.id 
-- ORDER BY a.created_at DESC;

-- 승인 단계별 진행 현황 확인
-- SELECT a.title, s.step_order, u.name as approver_name, s.status, s.approved_at
-- FROM approval_steps s
-- JOIN approvals a ON s.approval_id = a.id
-- JOIN users u ON s.approver_id = u.id
-- ORDER BY a.id, s.step_order;

-- 사용자별 대시보드 구독 현황 확인
-- SELECT u.name, d.title, ds.is_favorite, ds.last_accessed
-- FROM dashboard_subscriptions ds
-- JOIN users u ON ds.user_id = u.id
-- JOIN dashboards d ON ds.dashboard_id = d.id
-- ORDER BY u.name, ds.display_order;