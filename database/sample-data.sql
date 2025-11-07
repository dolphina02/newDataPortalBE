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
-- 1. DASHBOARDS SAMPLE DATA
-- ============================================
-- ============================================
-- 2. DASHBOARDS SAMPLE DATA (Updated with User references)
-- ============================================
INSERT INTO dashboards (title, description, category, type, rating, downloads, image, config, dashboard_url, contains_sensitive_data, created_by_id, created_by_email, created_by_name, is_active) VALUES
('매출 분석 대시보드', '실시간 매출 현황과 트렌드를 한눈에 확인할 수 있는 종합 대시보드', 'sales', 'TEMPLATE', 4.8, 2100, '/sampleDashboard.png', '{"charts": ["sales_trend", "product_performance"]}', '/dashboards/sales-analysis', false, 1, 'admin@company.com', '관리자', true),
('고객 행동 분석', '고객 여정과 행동 패턴을 시각화하여 인사이트를 제공하는 대시보드', 'customer', 'TEMPLATE', 4.6, 1800, '/sampleDashboard.png', '{"charts": ["customer_journey", "behavior_analysis"]}', '/dashboards/customer-behavior', true, 'admin', true),
('운영 효율성 모니터', '시스템 성능과 운영 지표를 실시간으로 모니터링하는 대시보드', 'operations', 'TEMPLATE', 4.9, 1500, '/sampleDashboard.png', '{"charts": ["system_performance", "operational_kpis"]}', '/dashboards/operations-monitor', false, 'admin', true),
('Claim Summary', '보험 청구 현황과 처리 상태를 종합적으로 관리하는 대시보드', 'insurance', 'TEMPLATE', 4.7, 1300, '/sampleDashboard2.png', '{"charts": ["claim_status", "processing_time"]}', '/dashboards/claim-summary', true, 'admin', true),
('APE Report', '연간보험료 환산 실적을 분석하는 핵심 KPI 대시보드', 'kpi', 'TEMPLATE', 4.5, 980, '/sampleDashboard.png', '{"charts": ["ape_performance", "target_achievement"]}', '/dashboards/ape-report', false, 'admin', true),
('PLR Report', '보험계약 지속률 분석을 위한 핵심 지표 대시보드', 'kpi', 'TEMPLATE', 4.3, 756, '/sampleDashboard.png', '{"charts": ["persistency_rate", "lapse_analysis"]}', '/dashboards/plr-report', false, 'admin', true),
('UW Decline', '언더라이팅 거절 현황 분석 대시보드', 'underwriting', 'TEMPLATE', 4.4, 642, '/sampleDashboard.png', '{"charts": ["decline_rate", "risk_analysis"]}', '/dashboards/uw-decline', true, 'admin', true),
('Daily Sales Report', '일일 영업 실적 현황을 실시간으로 모니터링하는 대시보드', 'sales', 'TEMPLATE', 4.6, 1100, '/sampleDashboard.png', '{"charts": ["daily_sales", "team_performance"]}', '/dashboards/daily-sales', false, 'admin', true);

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

-- Dashboard Subscriptions (My Dashboard)
INSERT INTO dashboard_subscriptions (user_id, dashboard_id, display_order, is_favorite, custom_title, subscribed_at, last_accessed) VALUES
('user1', 1, 1, true, '내 매출 대시보드', '2024-01-15 09:00:00', '2024-01-20 14:30:00'),
('user1', 4, 2, true, '청구 현황', '2024-01-16 10:00:00', '2024-01-20 11:15:00'),
('user1', 8, 3, false, null, '2024-01-17 11:00:00', '2024-01-19 16:45:00'),
('user2', 2, 1, true, '고객 분석', '2024-01-18 14:00:00', '2024-01-20 09:20:00'),
('user2', 3, 2, false, null, '2024-01-19 15:00:00', '2024-01-20 13:10:00');-
- ============================================
-- 2. APPROVALS SAMPLE DATA
-- ============================================
INSERT INTO approvals (type, title, description, status, priority, requester, reviewer, request_date, review_date, review_comment, current_step, total_steps) VALUES
('DEPLOY', 'Sales Dashboard 배포 요청', '영업팀 대시보드를 프로덕션 환경에 배포 요청합니다.', 'PENDING', 'HIGH', 'user1', null, '2024-01-15 09:30:00', null, null, 2, 3),
('DASHBOARD', '민감정보 포함 대시보드 구독', '고객 개인정보가 포함된 마케팅 대시보드 구독을 요청합니다.', 'APPROVED', 'MEDIUM', 'user2', 'admin', '2024-01-10 14:20:00', '2024-01-12 16:45:00', '보안 검토 완료 후 승인합니다.', 3, 3),
('DATA', 'Customer DB 테이블 접근 권한', 'customer_info 테이블에 대한 읽기 권한을 요청합니다.', 'REJECTED', 'LOW', 'user3', 'security_admin', '2024-01-08 11:15:00', '2024-01-09 09:30:00', '추가 보안 승인이 필요합니다.', 1, 3),
('DASHBOARD', '재무 대시보드 구독 요청', '재무팀 전용 대시보드 구독을 요청합니다.', 'PENDING', 'HIGH', 'user4', null, '2024-01-16 10:45:00', null, null, 1, 3),
('DEPLOY', 'HR Analytics Dashboard 배포', '인사 분석 대시보드의 신규 배포를 요청합니다.', 'PENDING', 'MEDIUM', 'user5', null, '2024-01-15 13:20:00', null, null, 1, 3);

-- ============================================
-- 3. APPROVAL LINES SAMPLE DATA
-- ============================================
INSERT INTO approval_lines (approval_id, step_order, approver_id, approver_name, approver_role, approver_department, status, approved_at, approval_comment) VALUES
-- Sales Dashboard 배포 요청 (approval_id: 1)
(1, 1, 'manager1', '김팀장', 'Team Manager', '영업팀', 'APPROVED', '2024-01-15 15:30:00', '영업팀 검토 완료'),
(1, 2, 'admin1', '이관리자', 'System Admin', 'IT팀', 'PENDING', null, null),
(1, 3, 'security1', '박보안', 'Security Officer', '보안팀', 'PENDING', null, null),

-- 민감정보 포함 대시보드 구독 (approval_id: 2)
(2, 1, 'manager2', '최팀장', 'Team Manager', '마케팅팀', 'APPROVED', '2024-01-11 10:15:00', '마케팅팀 승인'),
(2, 2, 'security1', '박보안', 'Security Officer', '보안팀', 'APPROVED', '2024-01-12 14:20:00', '보안 검토 완료'),
(2, 3, 'admin1', '이관리자', 'System Admin', 'IT팀', 'APPROVED', '2024-01-12 16:45:00', '최종 승인'),

-- Customer DB 테이블 접근 권한 (approval_id: 3)
(3, 1, 'security1', '박보안', 'Security Officer', '보안팀', 'REJECTED', '2024-01-09 09:30:00', '추가 보안 승인 필요'),
(3, 2, 'admin1', '이관리자', 'System Admin', 'IT팀', 'PENDING', null, null),
(3, 3, 'ciso1', '정보보호책임자', 'CISO', '보안팀', 'PENDING', null, null);

-- ============================================
-- 4. REPORTS SAMPLE DATA
-- ============================================
INSERT INTO reports (title, description, category, type, file_path, file_name, file_size, contains_sensitive_data, created_by) VALUES
('보험통계월보 25년7월호', '보험업계 주요 통계 및 동향 분석 (367호)', 'monthly', 'MONTHLY_REPORT', '/보험통계월보_25년 7월호(367호).pdf', '보험통계월보_25년7월호.pdf', 2456789, false, '보험개발원'),
('월간생명보험 25년9월호', '생명보험업계 월간 리포트', 'monthly', 'MONTHLY_REPORT', '/월간생명보험_25년9월호.pdf', '월간생명보험_25년9월호.pdf', 1834567, false, '생명보험협회'),
('고객통합리포트', '고객 행동 패턴 및 세그먼트 분석 종합 리포트', 'customer', 'CUSTOMER_ANALYSIS', '/reports/customer-integrated-report.pdf', '고객통합리포트.pdf', 2456789, true, '김분석'),
('고객DB모니터링리포트', '고객 데이터베이스 품질 및 정합성 모니터링 결과', 'monitoring', 'MONITORING', '/reports/customer-db-monitoring.pdf', '고객DB모니터링리포트.pdf', 1834567, true, '이모니터'),
('Monthly KPI 리포트', '9월 주요 성과 지표 및 목표 달성률 분석', 'kpi', 'KPI_REPORT', '/reports/monthly-kpi-report.pptx', 'Monthly_KPI_리포트.pptx', 3245678, false, '박KPI'),
('보험상품 판매현황 분석', '상품별 판매 실적 및 트렌드 분석 리포트', 'monthly', 'MONTHLY_REPORT', '/reports/product-sales-analysis.docx', '보험상품_판매현황_분석.docx', 1567890, false, '정상품'),
('고객 이탈 예측 모델 성과', 'ML 기반 고객 이탈 예측 모델의 성과 평가 리포트', 'customer', 'CUSTOMER_ANALYSIS', '/reports/churn-prediction-model.pdf', '고객_이탈_예측_모델_성과.pdf', 2789012, true, '최ML'),
('시스템 성능 모니터링', '데이터 플랫폼 시스템 성능 및 안정성 모니터링', 'monitoring', 'MONITORING', '/reports/system-performance-monitoring.pdf', '시스템_성능_모니터링.pdf', 1456789, false, '한시스템');

-- Report Tags
INSERT INTO report_tags (report_id, tag) VALUES
(1, '보험통계'), (1, '월보'), (1, '업계동향'),
(2, '생명보험'), (2, '월간'), (2, '업계리포트'),
(3, '고객분석'), (3, '세그먼트'), (3, '행동패턴'),
(4, '데이터품질'), (4, '모니터링'), (4, 'DB'),
(5, 'KPI'), (5, '성과지표'), (5, '월간'),
(6, '상품분석'), (6, '판매실적'), (6, '트렌드'),
(7, '머신러닝'), (7, '이탈예측'), (7, '모델성과'),
(8, '시스템성능'), (8, '안정성'), (8, '인프라');-
- ============================================
-- 5. DATA TABLES (CATALOG) SAMPLE DATA
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
(12, 'esg'), (12, 'sustainability'), (12, 'environmental'), (12, 'governance');-- ====
========================================
-- 6. ML MODELS SAMPLE DATA
-- ============================================
INSERT INTO ml_models (name, description, type, status, version, accuracy, f1_score, response_time, model_path, created_by, deployed_at) VALUES
('Fraud Detection Model', '보험금 청구 사기 탐지를 위한 머신러닝 모델', 'FRAUD_DETECTION', 'DEPLOYED', '2.1', 94.2, 0.89, 150, '/models/fraud_detection_v2.1.pkl', 'data_scientist1', '2024-01-10 14:30:00'),
('Risk Assessment Model', '보험 인수 심사를 위한 리스크 평가 모델', 'RISK_SCORING', 'TRAINING', '1.3', 87.6, 0.83, 200, '/models/risk_assessment_v1.3.pkl', 'data_scientist2', null),
('Customer Churn Prediction', '고객 이탈 가능성 예측 모델', 'CHURN_PREDICTION', 'DEPLOYED', '3.0', 91.8, 0.87, 120, '/models/churn_prediction_v3.0.pkl', 'data_scientist1', '2024-01-05 09:15:00'),
('Claim Amount Prediction', '보험금 청구 금액 예측 모델', 'UNDERWRITING', 'TESTING', '1.5', 89.3, 0.85, 180, '/models/claim_amount_v1.5.pkl', 'data_scientist3', null),
('Customer Segmentation', '고객 세그먼트 분류 모델', 'CUSTOMER_SEGMENTATION', 'DEPLOYED', '2.2', 88.7, 0.82, 95, '/models/customer_segmentation_v2.2.pkl', 'data_scientist2', '2024-01-12 11:45:00');

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
-- 6. APPROVALS SAMPLE DATA
-- ============================================
INSERT INTO approvals (
    type, title, description, status, priority,
    requester_id, requester_email, requester_name,
    reviewer_id, reviewer_email, reviewer_name,
    target_type, target_id, target_name,
    access_scope, sensitivity_level,
    requires_masking, requires_audit_log, usage_duration_days,
    business_justification, data_usage_purpose,
    request_date, review_date, review_comment
) VALUES
-- 일반 대시보드 배포 승인 (3개월)
('DASHBOARD_DEPLOY', '마케팅 대시보드 배포 승인', '고객 세그먼트 분석 대시보드를 프로덕션 환경에 배포하기 위한 승인 요청', 'APPROVED', 'MEDIUM',
 1, 'kim.analyst@company.com', '김분석가',
 2, 'park.manager@company.com', '박매니저',
 'DASHBOARD', 'dashboard_001', '고객 세그먼트 분석 대시보드',
 'DEPLOY', 'NORMAL',
 false, true, 90,
 '마케팅 캠페인 효과 분석 및 고객 타겟팅 개선을 위해 필요', '고객 행동 패턴 분석 및 세그먼트별 마케팅 전략 수립',
 '2024-01-15 09:30:00', '2024-01-16 14:20:00', '승인합니다. 3개월간 유효합니다.'),

-- 개인정보 데이터 접근 (1개월)
('DATA_ACCESS', '개인정보 데이터 접근 권한', '고객 개인정보가 포함된 데이터셋에 대한 분석 권한 요청', 'APPROVED', 'HIGH',
 3, 'lee.researcher@company.com', '이연구원',
 4, 'choi.security@company.com', '최보안담당자',
 'DATASET', 'pii_dataset_001', '고객 개인정보 데이터셋',
 'MASKED_READ', 'PII',
 true, true, 30,
 '개인정보보호법 준수 하에 고객 만족도 조사 분석', '개인정보는 마스킹 처리하여 통계 분석 목적으로만 사용',
 '2024-01-10 11:00:00', '2024-01-12 16:30:00', '마스킹 처리 조건으로 승인. 1개월간 유효.'),

-- 금융 데이터 접근 (1주일)
('DATA_ACCESS', '금융 데이터 긴급 분석', '분기 실적 분석을 위한 금융 데이터 긴급 접근 요청', 'APPROVED', 'URGENT',
 5, 'jung.finance@company.com', '정재무팀장',
 6, 'kim.cfo@company.com', '김CFO',
 'DATASET', 'financial_dataset_001', '분기별 재무 데이터',
 'READ', 'CONFIDENTIAL',
 true, true, 7,
 '분기 실적 보고서 작성 및 이사회 보고 자료 준비', '분기 매출, 비용, 수익성 분석 및 전년 동기 대비 성과 평가',
 '2024-01-18 08:00:00', '2024-01-18 10:15:00', '긴급 승인. 1주일간 제한적 접근 허용.'),

-- 재무 대시보드 생성 (3개월)
('DASHBOARD_CREATE', '재무 성과 대시보드 생성', '경영진 대상 실시간 재무 성과 모니터링 대시보드 생성', 'PENDING', 'HIGH',
 7, 'han.bi@company.com', '한BI개발자',
 null, null, null,
 'DASHBOARD', 'dashboard_002', '재무 성과 모니터링 대시보드',
 'CREATE', 'CONFIDENTIAL',
 false, true, 90,
 '경영진 의사결정 지원을 위한 실시간 재무 지표 모니터링', '매출, 비용, 수익성, 현금흐름 등 핵심 재무 지표 시각화',
 '2024-01-20 14:00:00', null, null),

-- ML 모델 배포 (1개월)
('DATA_ACCESS', 'ML 모델 배포 권한', '고객 이탈 예측 모델을 프로덕션 환경에 배포하기 위한 권한', 'APPROVED', 'MEDIUM',
 8, 'ai.engineer@company.com', 'AI엔지니어',
 9, 'tech.lead@company.com', '기술리드',
 'MODEL', 'ml_model_001', '고객 이탈 예측 모델',
 'DEPLOY', 'SENSITIVE',
 true, true, 30,
 '고객 이탈 방지 및 리텐션 전략 수립을 위한 예측 모델 운영', '고객 행동 데이터 기반 이탈 확률 예측 및 마케팅 액션 트리거',
 '2024-01-17 10:30:00', '2024-01-19 09:45:00', '모델 성능 검증 완료. 1개월간 시범 운영 승인.');