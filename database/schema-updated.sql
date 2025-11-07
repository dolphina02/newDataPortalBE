-- LINA Data Portal Database Schema (Updated with User References)
-- PostgreSQL DDL Statements

-- ============================================
-- 1. USERS TABLE (NEW - 사용자 정보 정규화)
-- ============================================
CREATE TABLE users (
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

-- User indexes
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_entra_id ON users(entra_object_id);
CREATE INDEX idx_user_employee_id ON users(employee_id);
CREATE INDEX idx_user_department ON users(department);
CREATE INDEX idx_user_status ON users(status);

-- ============================================
-- 2. DASHBOARDS TABLE (Updated with User references)
-- ============================================
CREATE TABLE dashboards (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('TEMPLATE', 'CUSTOM')),
    rating DOUBLE PRECISION DEFAULT 0.0,
    downloads INTEGER DEFAULT 0,
    image VARCHAR(500),
    config TEXT,
    dashboard_url VARCHAR(1000),
    contains_sensitive_data BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- User reference (정규화)
    created_by_id BIGINT,
    created_by_email VARCHAR(255) NOT NULL, -- 보조 정보 (빠른 조회용)
    created_by_name VARCHAR(255), -- 보조 정보
    
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (created_by_id) REFERENCES users(id)
);

-- Dashboard Tags (Many-to-Many relationship)
CREATE TABLE dashboard_tags (
    dashboard_id BIGINT NOT NULL,
    tag VARCHAR(255) NOT NULL,
    PRIMARY KEY (dashboard_id, tag),
    FOREIGN KEY (dashboard_id) REFERENCES dashboards(id) ON DELETE CASCADE
);

-- Dashboard Subscriptions (User's My Dashboard) - Updated with User references
CREATE TABLE dashboard_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    
    -- User reference (정규화)
    user_id BIGINT NOT NULL,
    user_email VARCHAR(255) NOT NULL, -- 보조 정보
    
    dashboard_id BIGINT NOT NULL,
    display_order INTEGER DEFAULT 0,
    is_favorite BOOLEAN DEFAULT FALSE,
    custom_title VARCHAR(255),
    subscribed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_accessed TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (dashboard_id) REFERENCES dashboards(id) ON DELETE CASCADE,
    UNIQUE(user_id, dashboard_id)
);

-- ============================================
-- 3. APPROVALS TABLE (Updated with User references)
-- ============================================
CREATE TABLE approvals (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL CHECK (type IN ('DASHBOARD_DEPLOY', 'DASHBOARD_CREATE', 'DASHBOARD', 'DATA', 'DATA_ACCESS', 'REPORT_PUBLISH')),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM' CHECK (priority IN ('URGENT', 'HIGH', 'MEDIUM', 'LOW')),
    
    -- 요청자 정보 (정규화)
    requester_id BIGINT NOT NULL,
    requester_email VARCHAR(255) NOT NULL, -- 보조 정보 (빠른 조회용)
    requester_name VARCHAR(255) NOT NULL, -- 보조 정보
    
    -- 검토자 정보 (정규화) - 선택적
    reviewer_id BIGINT,
    reviewer_email VARCHAR(255), -- 보조 정보
    reviewer_name VARCHAR(255), -- 보조 정보
    
    request_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    review_date TIMESTAMP,
    review_comment TEXT,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (requester_id) REFERENCES users(id),
    FOREIGN KEY (reviewer_id) REFERENCES users(id)
);

-- ============================================
-- 4. APPROVAL LINE TEMPLATES TABLE (NEW - 승인 라인 템플릿)
-- ============================================
CREATE TABLE approval_line_templates (
    id BIGSERIAL PRIMARY KEY,
    approval_type VARCHAR(50) NOT NULL CHECK (approval_type IN ('DASHBOARD_DEPLOY', 'DASHBOARD_CREATE', 'DASHBOARD', 'DATA', 'DATA_ACCESS', 'REPORT_PUBLISH')),
    step_order INTEGER NOT NULL,
    approver_role VARCHAR(255) NOT NULL,
    approver_department VARCHAR(255),
    
    -- 승인자 정보 (정규화) - 선택적 (역할/부서만으로도 템플릿 생성 가능)
    approver_id BIGINT,
    approver_email VARCHAR(255), -- 보조 정보
    approver_name VARCHAR(255), -- 보조 정보
    
    is_required BOOLEAN NOT NULL DEFAULT TRUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (approver_id) REFERENCES users(id)
);

-- ============================================
-- 5. APPROVAL STEPS TABLE (NEW - 실제 승인 진행 단계)
-- ============================================
CREATE TABLE approval_steps (
    id BIGSERIAL PRIMARY KEY,
    approval_id BIGINT NOT NULL,
    template_id BIGINT NOT NULL,
    template_version BIGINT NOT NULL,
    step_order INTEGER NOT NULL,
    
    -- 승인자 정보 (정규화)
    approver_id BIGINT NOT NULL,
    approver_email VARCHAR(255) NOT NULL, -- 보조 정보 (빠른 조회용)
    approver_name VARCHAR(255) NOT NULL, -- 보조 정보
    
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

-- ============================================
-- 6. REPORTS TABLE (Updated with User references)
-- ============================================
CREATE TABLE reports (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('CUSTOMER_ANALYSIS', 'KPI_REPORT', 'MONITORING', 'MONTHLY_REPORT')),
    file_path VARCHAR(500) NOT NULL,
    file_name VARCHAR(255),
    file_size BIGINT,
    contains_sensitive_data BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- User reference (정규화)
    created_by_id BIGINT,
    created_by_email VARCHAR(255) NOT NULL, -- 보조 정보
    created_by_name VARCHAR(255), -- 보조 정보
    
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (created_by_id) REFERENCES users(id)
);

-- Report Tags
CREATE TABLE report_tags (
    report_id BIGINT NOT NULL,
    tag VARCHAR(255) NOT NULL,
    PRIMARY KEY (report_id, tag),
    FOREIGN KEY (report_id) REFERENCES reports(id) ON DELETE CASCADE
);

-- ============================================
-- 7. DATA TABLES (CATALOG)
-- ============================================
CREATE TABLE data_tables (
    id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(255) NOT NULL,
    schema_name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(255) NOT NULL,
    data_type VARCHAR(50) NOT NULL CHECK (data_type IN ('DATASET', 'DASHBOARD', 'REPORT', 'TABLE')),
    record_count BIGINT DEFAULT 0,
    column_count INTEGER DEFAULT 0,
    is_favorite BOOLEAN DEFAULT FALSE,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Data Table Tags
CREATE TABLE data_table_tags (
    table_id BIGINT NOT NULL,
    tag VARCHAR(255) NOT NULL,
    PRIMARY KEY (table_id, tag),
    FOREIGN KEY (table_id) REFERENCES data_tables(id) ON DELETE CASCADE
);

-- Data Table Columns
CREATE TABLE data_table_columns (
    table_id BIGINT NOT NULL,
    column_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (table_id, column_name),
    FOREIGN KEY (table_id) REFERENCES data_tables(id) ON DELETE CASCADE
);

-- ============================================
-- 8. ML MODELS TABLE (Updated with User references)
-- ============================================
CREATE TABLE ml_models (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL CHECK (type IN ('FRAUD_DETECTION', 'CHURN_PREDICTION', 'RISK_SCORING', 'UNDERWRITING', 'CUSTOMER_SEGMENTATION')),
    status VARCHAR(50) NOT NULL DEFAULT 'TRAINING' CHECK (status IN ('TRAINING', 'TESTING', 'DEPLOYED', 'RETIRED')),
    version VARCHAR(50),
    accuracy DOUBLE PRECISION,
    f1_score DOUBLE PRECISION,
    response_time BIGINT,
    model_path VARCHAR(500),
    
    -- User reference (정규화)
    created_by_id BIGINT,
    created_by_email VARCHAR(255), -- 보조 정보
    created_by_name VARCHAR(255), -- 보조 정보
    
    deployed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (created_by_id) REFERENCES users(id)
);

-- Model Feature Importance (Key-Value pairs)
CREATE TABLE model_features (
    model_id BIGINT NOT NULL,
    feature_name VARCHAR(255) NOT NULL,
    importance DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (model_id, feature_name),
    FOREIGN KEY (model_id) REFERENCES ml_models(id) ON DELETE CASCADE
);

-- Model Tags
CREATE TABLE model_tags (
    model_id BIGINT NOT NULL,
    tag VARCHAR(255) NOT NULL,
    PRIMARY KEY (model_id, tag),
    FOREIGN KEY (model_id) REFERENCES ml_models(id) ON DELETE CASCADE
);

-- ============================================
-- 9. API ENDPOINTS TABLE
-- ============================================
CREATE TABLE api_endpoints (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    endpoint VARCHAR(500) NOT NULL,
    method VARCHAR(10) NOT NULL CHECK (method IN ('GET', 'POST', 'PUT', 'DELETE', 'PATCH')),
    category VARCHAR(50) NOT NULL CHECK (category IN ('CUSTOMER_MANAGEMENT', 'INSURANCE_PRODUCT', 'CLAIM_PROCESSING', 'UNDERWRITING')),
    request_schema TEXT,
    response_schema TEXT,
    documentation_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- API Tags
CREATE TABLE api_tags (
    api_id BIGINT NOT NULL,
    tag VARCHAR(255) NOT NULL,
    PRIMARY KEY (api_id, tag),
    FOREIGN KEY (api_id) REFERENCES api_endpoints(id) ON DELETE CASCADE
);

-- ============================================
-- 10. STT (SPEECH-TO-TEXT) TABLES
-- ============================================

-- Call Records Table
CREATE TABLE call_records (
    id BIGSERIAL PRIMARY KEY,
    call_id VARCHAR(255) NOT NULL UNIQUE,
    customer_id VARCHAR(255) NOT NULL,
    customer_name VARCHAR(255),
    customer_phone VARCHAR(50),
    agent_id VARCHAR(255) NOT NULL,
    agent_name VARCHAR(255),
    agent_department VARCHAR(255),
    customer_type VARCHAR(50) NOT NULL CHECK (customer_type IN ('PROSPECT', 'NEW_CUSTOMER', 'EXISTING_CUSTOMER', 'VIP_CUSTOMER', 'CORPORATE_CUSTOMER')),
    call_type VARCHAR(50) NOT NULL CHECK (call_type IN ('INBOUND_INQUIRY', 'OUTBOUND_SALES', 'CLAIM_CONSULTATION', 'POLICY_CONSULTATION', 'COMPLAINT_HANDLING', 'RENEWAL_CONSULTATION', 'CANCELLATION_REQUEST')),
    call_duration INTEGER,
    audio_file_path VARCHAR(1000),
    transcript TEXT,
    processing_status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (processing_status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')),
    call_date TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Call Segments Table
CREATE TABLE call_segments (
    id BIGSERIAL PRIMARY KEY,
    call_record_id BIGINT NOT NULL,
    start_time INTEGER NOT NULL,
    end_time INTEGER NOT NULL,
    speaker VARCHAR(50) NOT NULL CHECK (speaker IN ('AGENT', 'CUSTOMER')),
    text TEXT,
    confidence_score DOUBLE PRECISION,
    FOREIGN KEY (call_record_id) REFERENCES call_records(id) ON DELETE CASCADE
);

-- Keyword Matches Table
CREATE TABLE keyword_matches (
    id BIGSERIAL PRIMARY KEY,
    call_record_id BIGINT NOT NULL,
    keyword VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL CHECK (category IN ('PRODUCT', 'CLAIM', 'COMPLAINT', 'COMPLIMENT', 'POLICY', 'PREMIUM', 'RENEWAL', 'CANCELLATION', 'COMPETITOR', 'EMOTION_POSITIVE', 'EMOTION_NEGATIVE')),
    match_count INTEGER NOT NULL DEFAULT 1,
    first_occurrence_time INTEGER,
    context_text TEXT,
    speaker VARCHAR(50) NOT NULL CHECK (speaker IN ('AGENT', 'CUSTOMER')),
    sentiment_score DOUBLE PRECISION,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (call_record_id) REFERENCES call_records(id) ON DELETE CASCADE
);

-- ============================================
-- 11. USER TOKENS TABLE (Updated with User references)
-- ============================================
CREATE TABLE user_tokens (
    id BIGSERIAL PRIMARY KEY,
    
    -- User reference (정규화)
    user_id BIGINT NOT NULL,
    user_email VARCHAR(255) NOT NULL, -- 보조 정보 (빠른 조회용)
    
    token_type VARCHAR(50) NOT NULL,
    encrypted_token TEXT NOT NULL,
    workspace_url VARCHAR(500),
    cluster_id VARCHAR(255),
    warehouse_id VARCHAR(255),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    last_used_at TIMESTAMP,
    expires_at TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================
-- INDEXES FOR PERFORMANCE
-- ============================================

-- Dashboard indexes
CREATE INDEX idx_dashboards_type ON dashboards(type);
CREATE INDEX idx_dashboards_category ON dashboards(category);
CREATE INDEX idx_dashboards_rating ON dashboards(rating DESC);
CREATE INDEX idx_dashboards_downloads ON dashboards(downloads DESC);
CREATE INDEX idx_dashboards_created_at ON dashboards(created_at DESC);
CREATE INDEX idx_dashboards_created_by ON dashboards(created_by_id);
CREATE INDEX idx_dashboards_created_by_email ON dashboards(created_by_email);
CREATE INDEX idx_dashboards_sensitive ON dashboards(contains_sensitive_data);
CREATE INDEX idx_dashboards_active ON dashboards(is_active);

-- Dashboard Subscription indexes
CREATE INDEX idx_dashboard_subscriptions_user_id ON dashboard_subscriptions(user_id);
CREATE INDEX idx_dashboard_subscriptions_dashboard_id ON dashboard_subscriptions(dashboard_id);
CREATE INDEX idx_dashboard_subscriptions_user_order ON dashboard_subscriptions(user_id, display_order);
CREATE INDEX idx_dashboard_subscriptions_user_favorite ON dashboard_subscriptions(user_id, is_favorite);
CREATE INDEX idx_dashboard_subscriptions_last_accessed ON dashboard_subscriptions(user_id, last_accessed DESC);

-- Approval indexes
CREATE INDEX idx_approval_requester ON approvals(requester_id);
CREATE INDEX idx_approval_reviewer ON approvals(reviewer_id);
CREATE INDEX idx_approval_status ON approvals(status);
CREATE INDEX idx_approval_type ON approvals(type);
CREATE INDEX idx_approval_priority ON approvals(priority);
CREATE INDEX idx_approval_created ON approvals(created_at DESC);
CREATE INDEX idx_approval_requester_email ON approvals(requester_email);
CREATE INDEX idx_approval_reviewer_email ON approvals(reviewer_email);

-- Approval Line Template indexes
CREATE INDEX idx_approval_line_templates_type ON approval_line_templates(approval_type);
CREATE INDEX idx_approval_line_templates_step_order ON approval_line_templates(approval_type, step_order);
CREATE INDEX idx_approval_line_templates_approver ON approval_line_templates(approver_id);
CREATE INDEX idx_approval_line_templates_active ON approval_line_templates(is_active);

-- Approval Step indexes
CREATE INDEX idx_approval_steps_approval_id ON approval_steps(approval_id);
CREATE INDEX idx_approval_steps_approver_id ON approval_steps(approver_id);
CREATE INDEX idx_approval_steps_status ON approval_steps(status);
CREATE INDEX idx_approval_steps_step_order ON approval_steps(approval_id, step_order);
CREATE INDEX idx_approval_steps_approver_email ON approval_steps(approver_email);
CREATE INDEX idx_approval_steps_template ON approval_steps(template_id, template_version);

-- Report indexes
CREATE INDEX idx_reports_type ON reports(type);
CREATE INDEX idx_reports_category ON reports(category);
CREATE INDEX idx_reports_created_at ON reports(created_at DESC);
CREATE INDEX idx_reports_created_by ON reports(created_by_id);
CREATE INDEX idx_reports_created_by_email ON reports(created_by_email);
CREATE INDEX idx_reports_sensitive ON reports(contains_sensitive_data);
CREATE INDEX idx_reports_active ON reports(is_active);

-- Data Table indexes
CREATE INDEX idx_data_tables_data_type ON data_tables(data_type);
CREATE INDEX idx_data_tables_category ON data_tables(category);
CREATE INDEX idx_data_tables_schema ON data_tables(schema_name);
CREATE INDEX idx_data_tables_favorite ON data_tables(is_favorite);
CREATE INDEX idx_data_tables_updated ON data_tables(last_updated DESC);

-- ML Model indexes
CREATE INDEX idx_ml_models_type ON ml_models(type);
CREATE INDEX idx_ml_models_status ON ml_models(status);
CREATE INDEX idx_ml_models_created_by ON ml_models(created_by_id);
CREATE INDEX idx_ml_models_created_by_email ON ml_models(created_by_email);
CREATE INDEX idx_ml_models_accuracy ON ml_models(accuracy DESC);
CREATE INDEX idx_ml_models_created_at ON ml_models(created_at DESC);

-- API Endpoint indexes
CREATE INDEX idx_api_endpoints_category ON api_endpoints(category);
CREATE INDEX idx_api_endpoints_method ON api_endpoints(method);
CREATE INDEX idx_api_endpoints_active ON api_endpoints(is_active);

-- STT indexes
CREATE INDEX idx_call_records_call_id ON call_records(call_id);
CREATE INDEX idx_call_records_agent_id ON call_records(agent_id);
CREATE INDEX idx_call_records_customer_type ON call_records(customer_type);
CREATE INDEX idx_call_records_call_type ON call_records(call_type);
CREATE INDEX idx_call_records_call_date ON call_records(call_date);
CREATE INDEX idx_call_records_processing_status ON call_records(processing_status);

CREATE INDEX idx_call_segments_call_record_id ON call_segments(call_record_id);
CREATE INDEX idx_call_segments_start_time ON call_segments(call_record_id, start_time);
CREATE INDEX idx_call_segments_speaker ON call_segments(speaker);

CREATE INDEX idx_keyword_matches_call_record_id ON keyword_matches(call_record_id);
CREATE INDEX idx_keyword_matches_keyword ON keyword_matches(keyword);
CREATE INDEX idx_keyword_matches_category ON keyword_matches(category);
CREATE INDEX idx_keyword_matches_speaker ON keyword_matches(speaker);
CREATE INDEX idx_keyword_matches_created_at ON keyword_matches(created_at);

-- User Token indexes
CREATE INDEX idx_user_tokens_user_id ON user_tokens(user_id);
CREATE INDEX idx_user_tokens_user_type ON user_tokens(user_id, token_type);
CREATE INDEX idx_user_tokens_active ON user_tokens(is_active);
CREATE INDEX idx_user_tokens_user_email ON user_tokens(user_email);

-- ============================================
-- TRIGGERS FOR UPDATED_AT COLUMNS
-- ============================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply triggers to tables with updated_at columns
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_dashboards_updated_at BEFORE UPDATE ON dashboards FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_approvals_updated_at BEFORE UPDATE ON approvals FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_approval_line_templates_updated_at BEFORE UPDATE ON approval_line_templates FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_approval_steps_updated_at BEFORE UPDATE ON approval_steps FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_reports_updated_at BEFORE UPDATE ON reports FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_ml_models_updated_at BEFORE UPDATE ON ml_models FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_api_endpoints_updated_at BEFORE UPDATE ON api_endpoints FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_call_records_updated_at BEFORE UPDATE ON call_records FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_user_tokens_updated_at BEFORE UPDATE ON user_tokens FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Trigger for data_tables last_updated
CREATE TRIGGER update_data_tables_last_updated BEFORE UPDATE ON data_tables FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- COMMENTS FOR DOCUMENTATION
-- ============================================

COMMENT ON TABLE users IS '사용자 정보 (Entra ID 연동 또는 내부 사용자)';
COMMENT ON TABLE dashboards IS '대시보드 템플릿 및 사용자 정의 대시보드 (User 참조키 사용)';
COMMENT ON TABLE dashboard_tags IS '대시보드 태그 (다대다 관계)';
COMMENT ON TABLE dashboard_subscriptions IS '사용자별 대시보드 구독 정보 (My Dashboard, User 참조키 사용)';
COMMENT ON TABLE approvals IS '승인 관리 시스템 (배포/구독/데이터 접근, User 참조키 사용)';
COMMENT ON TABLE approval_line_templates IS '승인 라인 템플릿 (승인 유형별 단계 정의)';
COMMENT ON TABLE approval_steps IS '실제 승인 진행 단계 (템플릿 스냅샷 기반)';
COMMENT ON TABLE reports IS 'PDF 리포트 및 분석 문서 (User 참조키 사용)';
COMMENT ON TABLE report_tags IS '리포트 태그';
COMMENT ON TABLE data_tables IS '데이터 카탈로그 - 테이블 메타데이터';
COMMENT ON TABLE data_table_tags IS '데이터 테이블 태그';
COMMENT ON TABLE data_table_columns IS '데이터 테이블 컬럼 정보';
COMMENT ON TABLE ml_models IS '머신러닝 모델 관리 (User 참조키 사용)';
COMMENT ON TABLE model_features IS '모델 특성 중요도';
COMMENT ON TABLE model_tags IS '모델 태그';
COMMENT ON TABLE api_endpoints IS 'API 엔드포인트 카탈로그';
COMMENT ON TABLE api_tags IS 'API 태그';
COMMENT ON TABLE call_records IS 'STT 상담 녹취 기록';
COMMENT ON TABLE call_segments IS 'STT 상담 세그먼트 (화자별 구간)';
COMMENT ON TABLE keyword_matches IS 'STT 키워드 매칭 결과';
COMMENT ON TABLE user_tokens IS '사용자별 Personal Access Token 저장 (암호화, User 참조키 사용)';

-- ============================================
-- MIGRATION NOTES
-- ============================================

-- 기존 데이터 마이그레이션을 위한 참고사항:
-- 1. 기존 문자열 기반 사용자 정보를 users 테이블로 이관
-- 2. 외래키 관계 설정 전 데이터 정합성 확인
-- 3. 인덱스 재생성으로 성능 최적화
-- 4. 하위 호환성을 위한 이메일 필드 유지

-- ============================================
-- SAMPLE DATA VERIFICATION QUERIES
-- ============================================

-- Verify table creation
-- SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name;

-- Check foreign key constraints
-- SELECT conname, contype FROM pg_constraint WHERE contype = 'f' ORDER BY conname;

-- View indexes
-- SELECT indexname, indexdef FROM pg_indexes WHERE tablename LIKE '%approval%' OR tablename = 'users' ORDER BY tablename, indexname;