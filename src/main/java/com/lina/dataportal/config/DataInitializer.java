package com.lina.dataportal.config;

import com.lina.dataportal.domain.api.ApiCategory;
import com.lina.dataportal.domain.api.ApiEndpoint;
import com.lina.dataportal.domain.api.HttpMethod;
import com.lina.dataportal.domain.approval.Approval;
import com.lina.dataportal.domain.approval.ApprovalLine;
import com.lina.dataportal.domain.approval.ApprovalLineStatus;
import com.lina.dataportal.domain.approval.ApprovalLineTemplate;
import com.lina.dataportal.domain.approval.ApprovalType;
import com.lina.dataportal.domain.approval.TargetType;
import com.lina.dataportal.domain.approval.Priority;
import com.lina.dataportal.domain.catalog.DataTable;
import com.lina.dataportal.domain.catalog.DataType;
import com.lina.dataportal.domain.dashboard.Dashboard;
import com.lina.dataportal.domain.dashboard.DashboardSubscription;
import com.lina.dataportal.domain.dashboard.DashboardType;
import com.lina.dataportal.domain.model.MLModel;
import com.lina.dataportal.domain.model.ModelStatus;
import com.lina.dataportal.domain.model.ModelType;
import com.lina.dataportal.domain.report.Report;
import com.lina.dataportal.domain.report.ReportType;
import com.lina.dataportal.domain.user.User;
import com.lina.dataportal.domain.user.UserStatus;

import com.lina.dataportal.repository.*;
import com.lina.dataportal.repository.ApprovalLineRepository;
import com.lina.dataportal.repository.ApprovalLineTemplateRepository;
import com.lina.dataportal.repository.DashboardSubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final DashboardRepository dashboardRepository;
    private final DashboardSubscriptionRepository dashboardSubscriptionRepository;
    private final ApprovalRepository approvalRepository;
    private final ApprovalLineRepository approvalLineRepository;
    private final ApprovalLineTemplateRepository approvalLineTemplateRepository;
    private final ReportRepository reportRepository;
    private final DataTableRepository dataTableRepository;
    private final MLModelRepository mlModelRepository;
    private final ApiEndpointRepository apiEndpointRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public DataInitializer(DashboardRepository dashboardRepository,
                          DashboardSubscriptionRepository dashboardSubscriptionRepository,
                          ApprovalRepository approvalRepository,
                          ApprovalLineRepository approvalLineRepository,
                          ApprovalLineTemplateRepository approvalLineTemplateRepository,
                          ReportRepository reportRepository,
                          DataTableRepository dataTableRepository,
                          MLModelRepository mlModelRepository,
                          ApiEndpointRepository apiEndpointRepository,
                          UserRepository userRepository) {
        this.dashboardRepository = dashboardRepository;
        this.dashboardSubscriptionRepository = dashboardSubscriptionRepository;
        this.approvalRepository = approvalRepository;
        this.approvalLineRepository = approvalLineRepository;
        this.approvalLineTemplateRepository = approvalLineTemplateRepository;
        this.reportRepository = reportRepository;
        this.dataTableRepository = dataTableRepository;
        this.mlModelRepository = mlModelRepository;
        this.apiEndpointRepository = apiEndpointRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        initializeApprovalLineTemplates(); // 템플릿을 먼저 초기화
        initializeDashboards();
        initializeDashboardSubscriptions();
        initializeApprovals();
        initializeApprovalLines();
        initializeReports();
        // initializeDataTables(); // 아직 수정 안됨
        // initializeMLModels(); // 아직 수정 안됨
        // initializeApiEndpoints(); // 아직 수정 안됨
        
        System.out.println("DataInitializer: 기본 데이터 초기화 완료");
    }
    
    private void initializeDashboards() {
        if (dashboardRepository.count() == 0) {
            // 보험 KPI 대시보드
            Dashboard insuranceKpi = new Dashboard(
                "보험 KPI 대시보드", 
                "보험계약, 보험료 수입, 심사기간, 고객만족도 등 핵심 지표 모니터링", 
                "보험업무", 
                DashboardType.TEMPLATE,
                "시스템관리자"
            );
            insuranceKpi.setRating(4.8);
            insuranceKpi.setDownloads(156);
            insuranceKpi.setTagsList(Arrays.asList("보험", "KPI", "모니터링", "실시간"));
            insuranceKpi.setImage("/images/insurance-kpi-dashboard.png");
            insuranceKpi.setDashboardUrl("https://dashboard.lina.co.kr/insurance-kpi");
            
            // 청구 요약 대시보드
            Dashboard claimSummary = new Dashboard(
                "청구 요약 대시보드", 
                "보험금 청구 현황, 처리 상태, 승인률 등 청구 관련 통계", 
                "청구관리", 
                DashboardType.TEMPLATE,
                "김데이터"
            );
            claimSummary.setRating(4.6);
            claimSummary.setDownloads(89);
            claimSummary.setTagsList(Arrays.asList("청구", "보험금", "승인률", "통계"));
            claimSummary.setImage("/images/claim-summary-dashboard.png");
            claimSummary.setDashboardUrl("https://dashboard.lina.co.kr/claim-summary");
            
            // 고객 분석 대시보드
            Dashboard customerAnalysis = new Dashboard(
                "고객 분석 대시보드", 
                "고객 세그먼트, 이탈률, 만족도 분석 및 예측 모델", 
                "고객관리", 
                DashboardType.TEMPLATE,
                "이마케팅"
            );
            customerAnalysis.setContainsSensitiveData(true);
            customerAnalysis.setRating(4.7);
            customerAnalysis.setDownloads(124);
            customerAnalysis.setTagsList(Arrays.asList("고객", "분석", "세그먼트", "예측"));
            customerAnalysis.setImage("/images/customer-analysis-dashboard.png");
            customerAnalysis.setDashboardUrl("https://dashboard.lina.co.kr/customer-analysis");
            
            // 리스크 모니터링 대시보드
            Dashboard riskMonitoring = new Dashboard(
                "리스크 모니터링 대시보드", 
                "언더라이팅 리스크, 사기 탐지, 포트폴리오 리스크 분석", 
                "리스크관리", 
                DashboardType.TEMPLATE,
                "박분석"
            );
            riskMonitoring.setContainsSensitiveData(true);
            riskMonitoring.setRating(4.9);
            riskMonitoring.setDownloads(67);
            riskMonitoring.setTagsList(Arrays.asList("리스크", "사기탐지", "언더라이팅", "모니터링"));
            riskMonitoring.setImage("/images/risk-monitoring-dashboard.png");
            riskMonitoring.setDashboardUrl("https://dashboard.lina.co.kr/risk-monitoring");
            
            dashboardRepository.saveAll(Arrays.asList(
                insuranceKpi, claimSummary, customerAnalysis, riskMonitoring
            ));
        }
    }
    
    private void initializeApprovals() {
        if (approvalRepository.count() == 0) {
            // 대시보드 배포 요청 (DEPLOY + DASHBOARD)
            Approval deployRequest = new Approval(
                ApprovalType.DEPLOY,
                "신규 고객 분석 대시보드 배포",
                "Q4 신규 고객 획득 전략을 위한 분석 대시보드 프로덕션 배포 요청",
                1L, "kim.data@company.com", "김데이터"
            );
            deployRequest.setTargetType(TargetType.DASHBOARD);
            deployRequest.setTargetId("dashboard_001");
            deployRequest.setTargetName("신규 고객 분석 대시보드");
            deployRequest.setPriority(Priority.HIGH);
            
            // 민감정보 대시보드 구독 요청 (SUBSCRIBE + DASHBOARD)
            Approval subscribeRequest = new Approval(
                ApprovalType.SUBSCRIBE,
                "개인정보 포함 고객 세그먼트 대시보드 구독",
                "마케팅 캠페인을 위한 고객 개인정보가 포함된 세그먼트 분석 대시보드 접근 권한 요청",
                2L, "lee.marketing@company.com", "이마케팅"
            );
            subscribeRequest.setTargetType(TargetType.DASHBOARD);
            subscribeRequest.setTargetId("dashboard_002");
            subscribeRequest.setTargetName("고객 세그먼트 대시보드");
            subscribeRequest.setPriority(Priority.MEDIUM);
            
            // 데이터 접근 권한 요청 (ACCESS + DATASET)
            Approval dataAccessRequest = new Approval(
                ApprovalType.ACCESS,
                "보험금 청구 상세 데이터 접근 권한",
                "사기 탐지 모델 개발을 위한 과거 5년간 보험금 청구 상세 데이터 접근 권한 요청",
                3L, "park.analysis@company.com", "박분석"
            );
            dataAccessRequest.setTargetType(TargetType.DATASET);
            dataAccessRequest.setTargetId("claims_dataset_001");
            dataAccessRequest.setTargetName("보험금 청구 데이터셋");
            dataAccessRequest.setPriority(Priority.LOW);
            
            approvalRepository.saveAll(Arrays.asList(
                deployRequest, subscribeRequest, dataAccessRequest
            ));
        }
    }
    
    private void initializeReports() {
        if (reportRepository.count() == 0) {
            Report monthlyInsurance = new Report(
                "보험통계월보 2025년 7월호",
                "생명보험업계 월간 통계 및 트렌드 분석 리포트",
                "월간 리포트",
                ReportType.MONTHLY_REPORT,
                "/reports/보험통계월보_25년7월호.pdf",
                "통계팀"
            );
            monthlyInsurance.setFileName("보험통계월보_25년7월호.pdf");
            monthlyInsurance.setFileSize(2048576L);
            monthlyInsurance.setTagsList(Arrays.asList("월간", "통계", "보험업계", "트렌드"));
            
            Report customerAnalysisReport = new Report(
                "고객 세그먼트 분석 리포트",
                "2025년 상반기 고객 행동 패턴 및 세그먼트별 특성 분석",
                "고객 분석",
                ReportType.CUSTOMER_ANALYSIS,
                "/reports/customer_analysis_2025h1.pdf",
                "이마케팅"
            );
            customerAnalysisReport.setContainsSensitiveData(true);
            customerAnalysisReport.setFileName("customer_analysis_2025h1.pdf");
            customerAnalysisReport.setFileSize(1536000L);
            customerAnalysisReport.setTagsList(Arrays.asList("고객", "세그먼트", "행동패턴", "분석"));
            
            Report kpiReport = new Report(
                "Q3 KPI 성과 리포트",
                "3분기 핵심 성과 지표 달성 현황 및 분석",
                "KPI 리포트",
                ReportType.KPI_REPORT,
                "/reports/q3_kpi_report.pdf",
                "경영기획팀"
            );
            kpiReport.setFileName("q3_kpi_report.pdf");
            kpiReport.setFileSize(1024000L);
            kpiReport.setTagsList(Arrays.asList("KPI", "성과", "분기", "지표"));
            
            reportRepository.saveAll(Arrays.asList(monthlyInsurance, customerAnalysisReport, kpiReport));
        }
    }
    
    private void initializeDataTables() {
        if (dataTableRepository.count() == 0) {
            DataTable policyTable = new DataTable(
                "POLICY_MASTER",
                "INSURANCE",
                "보험계약 마스터 테이블 - 모든 보험계약 정보",
                "보험계약",
                DataType.TABLE
            );
            policyTable.setRecordCount(1250000L);
            policyTable.setColumnCount(25);
            policyTable.setColumns(Arrays.asList("POLICY_NO", "CUSTOMER_ID", "PRODUCT_CODE", "PREMIUM_AMOUNT", "START_DATE", "END_DATE"));
            policyTable.setTags(Arrays.asList("보험계약", "마스터", "정책", "고객"));
            
            DataTable claimTable = new DataTable(
                "CLAIM_HISTORY",
                "INSURANCE",
                "보험금 청구 이력 테이블",
                "청구관리",
                DataType.TABLE
            );
            claimTable.setRecordCount(450000L);
            claimTable.setColumnCount(18);
            claimTable.setColumns(Arrays.asList("CLAIM_ID", "POLICY_NO", "CLAIM_AMOUNT", "CLAIM_DATE", "STATUS", "APPROVAL_DATE"));
            claimTable.setTags(Arrays.asList("청구", "보험금", "이력", "승인"));
            claimTable.setIsFavorite(true);
            
            DataTable customerTable = new DataTable(
                "CUSTOMER_PROFILE",
                "CRM",
                "고객 프로필 및 기본 정보",
                "고객관리",
                DataType.DATASET
            );
            customerTable.setRecordCount(850000L);
            customerTable.setColumnCount(32);
            customerTable.setColumns(Arrays.asList("CUSTOMER_ID", "NAME", "AGE", "GENDER", "REGION", "INCOME_LEVEL"));
            customerTable.setTags(Arrays.asList("고객", "프로필", "개인정보", "세그먼트"));
            
            dataTableRepository.saveAll(Arrays.asList(policyTable, claimTable, customerTable));
        }
    }
    
    private void initializeMLModels() {
        if (mlModelRepository.count() == 0) {
            MLModel fraudModel = new MLModel(
                "보험금 청구 사기 탐지 모델 v2.1",
                "머신러닝 기반 보험금 청구 사기 패턴 탐지 및 리스크 스코어링",
                ModelType.FRAUD_DETECTION,
                "김데이터"
            );
            fraudModel.setVersion("v2.1");
            fraudModel.setStatus(ModelStatus.DEPLOYED);
            fraudModel.setAccuracy(0.94);
            fraudModel.setF1Score(0.89);
            fraudModel.setResponseTime(150L);
            
            Map<String, Double> fraudFeatures = new HashMap<>();
            fraudFeatures.put("claim_amount", 0.35);
            fraudFeatures.put("claim_frequency", 0.28);
            fraudFeatures.put("customer_history", 0.22);
            fraudFeatures.put("medical_complexity", 0.15);
            fraudModel.setFeatureImportance(fraudFeatures);
            fraudModel.setTags(Arrays.asList("사기탐지", "머신러닝", "리스크", "청구"));
            
            MLModel churnModel = new MLModel(
                "고객 이탈 예측 모델",
                "고객 행동 패턴 분석을 통한 이탈 가능성 예측",
                ModelType.CHURN_PREDICTION,
                "박분석"
            );
            churnModel.setVersion("v1.3");
            churnModel.setStatus(ModelStatus.TESTING);
            churnModel.setAccuracy(0.87);
            churnModel.setF1Score(0.82);
            churnModel.setResponseTime(200L);
            
            Map<String, Double> churnFeatures = new HashMap<>();
            churnFeatures.put("premium_payment_delay", 0.42);
            churnFeatures.put("customer_service_calls", 0.31);
            churnFeatures.put("policy_changes", 0.27);
            churnModel.setFeatureImportance(churnFeatures);
            churnModel.setTags(Arrays.asList("이탈예측", "고객분석", "머신러닝", "리텐션"));
            
            MLModel riskModel = new MLModel(
                "언더라이팅 리스크 점수 모델",
                "신규 가입자 리스크 평가 및 점수 산출",
                ModelType.RISK_SCORING,
                "이언더"
            );
            riskModel.setVersion("v3.0");
            riskModel.setStatus(ModelStatus.DEPLOYED);
            riskModel.setAccuracy(0.91);
            riskModel.setF1Score(0.88);
            riskModel.setResponseTime(120L);
            
            Map<String, Double> riskFeatures = new HashMap<>();
            riskFeatures.put("age", 0.25);
            riskFeatures.put("health_score", 0.35);
            riskFeatures.put("occupation_risk", 0.20);
            riskFeatures.put("lifestyle_factors", 0.20);
            riskModel.setFeatureImportance(riskFeatures);
            riskModel.setTags(Arrays.asList("리스크", "언더라이팅", "점수", "평가"));
            
            mlModelRepository.saveAll(Arrays.asList(fraudModel, churnModel, riskModel));
        }
    }
    
    private void initializeApiEndpoints() {
        if (apiEndpointRepository.count() == 0) {
            // 고객 관리 API
            ApiEndpoint getCustomer = new ApiEndpoint(
                "고객 정보 조회",
                "고객 ID로 고객 상세 정보 조회",
                "/api/customers/{customerId}",
                HttpMethod.GET,
                ApiCategory.CUSTOMER_MANAGEMENT
            );
            getCustomer.setResponseSchema("{ \"customerId\": \"string\", \"name\": \"string\", \"age\": \"number\" }");
            getCustomer.setDocumentationUrl("/docs/customer-api");
            
            ApiEndpoint createCustomer = new ApiEndpoint(
                "신규 고객 등록",
                "새로운 고객 정보 등록",
                "/api/customers",
                HttpMethod.POST,
                ApiCategory.CUSTOMER_MANAGEMENT
            );
            createCustomer.setRequestSchema("{ \"name\": \"string\", \"age\": \"number\", \"email\": \"string\" }");
            createCustomer.setResponseSchema("{ \"customerId\": \"string\", \"status\": \"string\" }");
            
            // 보험 상품 API
            ApiEndpoint getProducts = new ApiEndpoint(
                "보험 상품 목록 조회",
                "활성화된 보험 상품 목록 조회",
                "/api/products",
                HttpMethod.GET,
                ApiCategory.INSURANCE_PRODUCT
            );
            getProducts.setResponseSchema("[ { \"productId\": \"string\", \"name\": \"string\", \"premium\": \"number\" } ]");
            
            // 청구 처리 API
            ApiEndpoint submitClaim = new ApiEndpoint(
                "보험금 청구 접수",
                "새로운 보험금 청구 접수",
                "/api/claims",
                HttpMethod.POST,
                ApiCategory.CLAIM_PROCESSING
            );
            submitClaim.setRequestSchema("{ \"policyNo\": \"string\", \"claimAmount\": \"number\", \"reason\": \"string\" }");
            submitClaim.setResponseSchema("{ \"claimId\": \"string\", \"status\": \"string\" }");
            
            ApiEndpoint getClaimStatus = new ApiEndpoint(
                "청구 상태 조회",
                "보험금 청구 처리 상태 조회",
                "/api/claims/{claimId}/status",
                HttpMethod.GET,
                ApiCategory.CLAIM_PROCESSING
            );
            getClaimStatus.setResponseSchema("{ \"claimId\": \"string\", \"status\": \"string\", \"processDate\": \"string\" }");
            
            // 언더라이팅 API
            ApiEndpoint riskAssessment = new ApiEndpoint(
                "리스크 평가",
                "신규 가입자 리스크 점수 평가",
                "/api/underwriting/risk-assessment",
                HttpMethod.POST,
                ApiCategory.UNDERWRITING
            );
            riskAssessment.setRequestSchema("{ \"customerId\": \"string\", \"productId\": \"string\", \"healthInfo\": \"object\" }");
            riskAssessment.setResponseSchema("{ \"riskScore\": \"number\", \"recommendation\": \"string\" }");
            
            apiEndpointRepository.saveAll(Arrays.asList(
                getCustomer, createCustomer, getProducts, submitClaim, getClaimStatus, riskAssessment
            ));
        }
    }    
 
   private void initializeDashboardSubscriptions() {
        if (dashboardSubscriptionRepository.count() == 0) {
            // admin 사용자의 구독
            DashboardSubscription adminSub1 = new DashboardSubscription("admin", 1L, 0);
            adminSub1.setIsFavorite(true);
            adminSub1.setCustomTitle("내 보험 KPI");
            
            DashboardSubscription adminSub2 = new DashboardSubscription("admin", 2L, 1);
            adminSub2.setIsFavorite(false);
            
            DashboardSubscription adminSub3 = new DashboardSubscription("admin", 3L, 2);
            adminSub3.setIsFavorite(true);
            adminSub3.setCustomTitle("고객 인사이트");
            
            // 김데이터 사용자의 구독
            DashboardSubscription kimSub1 = new DashboardSubscription("김데이터", 1L, 0);
            DashboardSubscription kimSub2 = new DashboardSubscription("김데이터", 4L, 1);
            kimSub2.setIsFavorite(true);
            kimSub2.setCustomTitle("리스크 대시보드");
            
            // 이마케팅 사용자의 구독
            DashboardSubscription leeSub1 = new DashboardSubscription("이마케팅", 3L, 0);
            leeSub1.setIsFavorite(true);
            leeSub1.setCustomTitle("마케팅 고객 분석");
            
            DashboardSubscription leeSub2 = new DashboardSubscription("이마케팅", 1L, 1);
            
            // 박분석 사용자의 구독
            DashboardSubscription parkSub1 = new DashboardSubscription("박분석", 2L, 0);
            parkSub1.setIsFavorite(true);
            
            DashboardSubscription parkSub2 = new DashboardSubscription("박분석", 4L, 1);
            parkSub2.setIsFavorite(true);
            
            DashboardSubscription parkSub3 = new DashboardSubscription("박분석", 1L, 2);
            
            dashboardSubscriptionRepository.saveAll(Arrays.asList(
                adminSub1, adminSub2, adminSub3,
                kimSub1, kimSub2,
                leeSub1, leeSub2,
                parkSub1, parkSub2, parkSub3
            ));
        }
    }    

    private void initializeApprovalLines() {
        if (approvalLineRepository.count() == 0) {
            // 첫 번째 승인 요청 (ID: 1) - 대시보드 배포 요청
            ApprovalLine line1_1 = new ApprovalLine(1L, 1L, 1, "team_leader_data", "김팀장");
            line1_1.setApproverRole("팀장");
            line1_1.setApproverDepartment("데이터분석팀");
            line1_1.setStatus(ApprovalLineStatus.APPROVED);
            line1_1.setApprovalComment("검토 완료. 승인합니다.");
            
            ApprovalLine line1_2 = new ApprovalLine(1L, 1L, 2, "dept_manager_it", "박부장");
            line1_2.setApproverRole("부서장");
            line1_2.setApproverDepartment("IT본부");
            
            ApprovalLine line1_3 = new ApprovalLine(1L, 1L, 3, "cto", "최CTO");
            line1_3.setApproverRole("최고기술책임자");
            line1_3.setApproverDepartment("IT본부");
            
            // 두 번째 승인 요청 (ID: 2) - 민감정보 대시보드 구독
            ApprovalLine line2_1 = new ApprovalLine(2L, 2L, 1, "team_leader_marketing", "이팀장");
            line2_1.setApproverRole("팀장");
            line2_1.setApproverDepartment("마케팅팀");
            
            ApprovalLine line2_2 = new ApprovalLine(2L, 2L, 2, "dept_manager_business", "정부장");
            line2_2.setApproverRole("부서장");
            line2_2.setApproverDepartment("영업본부");
            
            ApprovalLine line2_3 = new ApprovalLine(2L, 2L, 3, "privacy_officer", "한개인정보보호책임자");
            line2_3.setApproverRole("개인정보보호책임자");
            line2_3.setApproverDepartment("컴플라이언스팀");
            
            // 세 번째 승인 요청 (ID: 3) - 데이터 접근 권한
            ApprovalLine line3_1 = new ApprovalLine(3L, 3L, 1, "team_leader_analysis", "박팀장");
            line3_1.setApproverRole("팀장");
            line3_1.setApproverDepartment("분석팀");
            
            ApprovalLine line3_2 = new ApprovalLine(3L, 3L, 2, "dept_manager_it", "박부장");
            line3_2.setApproverRole("부서장");
            line3_2.setApproverDepartment("IT본부");
            
            approvalLineRepository.saveAll(Arrays.asList(
                line1_1, line1_2, line1_3,
                line2_1, line2_2, line2_3,
                line3_1, line3_2
            ));
        }
    }
    
    private void initializeApprovalLineTemplates() {
        if (approvalLineTemplateRepository.count() == 0) {
            // ACCESS 타입의 승인 라인 템플릿 (데이터 접근)
            ApprovalLineTemplate dataAccess1 = new ApprovalLineTemplate(ApprovalType.ACCESS, 1, "팀장", "데이터분석팀", 1L, "kim.leader@company.com", "김팀장");
            dataAccess1.setDescription("데이터 접근 권한에 대한 팀장 승인");
            dataAccess1.setIsRequired(true);
            
            ApprovalLineTemplate dataAccess2 = new ApprovalLineTemplate(ApprovalType.ACCESS, 2, "부장", "IT팀", 2L, "park.manager@company.com", "박부장");
            dataAccess2.setDescription("데이터 접근 권한에 대한 IT 부장 승인");
            dataAccess2.setIsRequired(true);
            
            ApprovalLineTemplate dataAccess3 = new ApprovalLineTemplate(ApprovalType.ACCESS, 3, "이사", "경영지원팀", 3L, "lee.director@company.com", "이이사");
            dataAccess3.setDescription("민감 데이터 접근에 대한 최종 승인");
            dataAccess3.setIsRequired(false);
            
            // DEPLOY 타입의 승인 라인 템플릿 (배포/발행)
            ApprovalLineTemplate deployTemplate1 = new ApprovalLineTemplate(ApprovalType.DEPLOY, 1, "팀장", "데이터분석팀", 1L, "kim.leader@company.com", "김팀장");
            deployTemplate1.setDescription("배포/발행에 대한 팀장 승인");
            deployTemplate1.setIsRequired(true);
            
            ApprovalLineTemplate deployTemplate2 = new ApprovalLineTemplate(ApprovalType.DEPLOY, 2, "부장", "경영지원팀", 4L, "choi.manager@company.com", "최부장");
            deployTemplate2.setDescription("배포/발행에 대한 경영지원 부장 승인");
            deployTemplate2.setIsRequired(true);
            
            // CREATE 타입의 승인 라인 템플릿 (생성)
            ApprovalLineTemplate createTemplate1 = new ApprovalLineTemplate(ApprovalType.CREATE, 1, "팀장", "데이터분석팀", 1L, "kim.leader@company.com", "김팀장");
            createTemplate1.setDescription("리소스 생성에 대한 팀장 승인");
            createTemplate1.setIsRequired(true);
            
            // SUBSCRIBE 타입의 승인 라인 템플릿 (구독)
            ApprovalLineTemplate subscribeTemplate1 = new ApprovalLineTemplate(ApprovalType.SUBSCRIBE, 1, "팀장", "데이터분석팀", 1L, "kim.leader@company.com", "김팀장");
            subscribeTemplate1.setDescription("구독 권한에 대한 팀장 승인");
            subscribeTemplate1.setIsRequired(true);
            
            approvalLineTemplateRepository.saveAll(Arrays.asList(
                dataAccess1, dataAccess2, dataAccess3,
                deployTemplate1, deployTemplate2,
                createTemplate1, subscribeTemplate1
            ));
        }
    }
}
