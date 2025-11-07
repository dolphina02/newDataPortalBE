package com.lina.dataportal.domain.approval;

import com.lina.dataportal.domain.common.BaseAuditEntity;
import com.lina.dataportal.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "approvals", indexes = {
    @Index(name = "idx_approval_requester", columnList = "requesterId"),
    @Index(name = "idx_approval_reviewer", columnList = "reviewerId"),
    @Index(name = "idx_approval_status", columnList = "status"),
    @Index(name = "idx_approval_type", columnList = "type"),
    @Index(name = "idx_approval_created", columnList = "createdAt"),
    @Index(name = "idx_approval_target", columnList = "targetType,targetId"),
    @Index(name = "idx_approval_target_type", columnList = "targetType"),
    @Index(name = "idx_approval_access_scope", columnList = "accessScope"),
    @Index(name = "idx_approval_sensitivity", columnList = "sensitivityLevel"),
    @Index(name = "idx_approval_usage_expires", columnList = "usageExpiresAt"),
    @Index(name = "idx_approval_masking_policy", columnList = "maskingPolicyId"),
    @Index(name = "idx_approval_policy_set", columnList = "policySetId")
})
public class Approval extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예: 1
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ApprovalType type; // 예: dashboard create/deploy, data access/sensitives
    
    @NotBlank
    @Column(nullable = false)
    private String title; // 예: "고객 데이터 접근 권한 요청"
    
    @Column(columnDefinition = "TEXT")
    private String description; // 예: "마케팅 캠페인 분석을 위한 고객 데이터베이스 접근 권한이 필요합니다."
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING; // 예: PENDING, APPROVED, REJECTED
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM; // 예: LOW, MEDIUM, HIGH, URGENT
    
    // 요청자 정보 (정규화)
    @NotNull
    @Column(name = "requester_id", nullable = false)
    private Long requesterId; // 예: 1 (User 테이블의 ID)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", insertable = false, updatable = false)
    private User requesterUser; // 연관된 요청자 정보
    
    @NotBlank
    @Column(name = "requester_email", nullable = false)
    private String requesterEmail; // 예: "kim.analyst@company.com" (보조 정보, 빠른 조회용)
    
    @NotBlank
    @Column(name = "requester_name", nullable = false)
    private String requesterName; // 예: "김분석가" (보조 정보, 빠른 조회용)
    
    // 검토자 정보 (정규화)
    @Column(name = "reviewer_id")
    private Long reviewerId; // 예: 2 (User 테이블의 ID)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", insertable = false, updatable = false)
    private User reviewerUser; // 연관된 검토자 정보
    
    @Column(name = "reviewer_email")
    private String reviewerEmail; // 예: "park.manager@company.com" (보조 정보)
    
    @Column(name = "reviewer_name")
    private String reviewerName; // 예: "박매니저" (보조 정보)
    
    // ========== 승인 대상 오브젝트 정보 (1급 개념) ==========
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "target_type", nullable = false)
    private TargetType targetType; // 예: DATASET, DASHBOARD, REPORT, CLUSTER 등
    
    @NotBlank
    @Column(name = "target_id", nullable = false)
    private String targetId; // 예: "dataset_123", "dashboard_456", "report_789"
    
    @Column(name = "target_name")
    private String targetName; // 예: "고객 데이터셋", "매출 대시보드" (보조 정보)
    
    @Column(name = "target_description", columnDefinition = "TEXT")
    private String targetDescription; // 대상 오브젝트 설명
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "access_scope", nullable = false)
    private AccessScope accessScope; // 예: READ, WRITE, EXPORT, SHARE 등
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "sensitivity_level", nullable = false)
    private SensitivityLevel sensitivityLevel; // 예: NORMAL, SENSITIVE, STRICT 등
    
    // 보안 정책 연결
    @Column(name = "masking_policy_id")
    private Long maskingPolicyId; // 적용할 마스킹 정책 ID
    
    @Column(name = "policy_set_id")
    private Long policySetId; // 적용할 정책 세트 ID
    
    // 추가 보안 설정
    @Column(name = "requires_masking")
    private Boolean requiresMasking = false; // 마스킹 필요 여부
    
    @Column(name = "requires_audit_log")
    private Boolean requiresAuditLog = false; // 감사 로그 필요 여부
    
    // 사용 기간 관리 (기본 3개월)
    @Column(name = "usage_duration_days")
    private Integer usageDurationDays = 90; // 사용 권한 유효 기간 (일 단위, 기본 3개월)
    
    @Column(name = "usage_expires_at")
    private LocalDateTime usageExpiresAt; // 사용 권한 만료 시간
    
    @Column(name = "business_justification", columnDefinition = "TEXT")
    private String businessJustification; // 업무 목적 및 정당성
    
    @Column(name = "data_usage_purpose", columnDefinition = "TEXT")
    private String dataUsagePurpose; // 데이터 사용 목적
    
    @Column(name = "expected_completion_date")
    private LocalDateTime expectedCompletionDate; // 예상 완료 일자
    
    @CreatedDate
    @Column(name = "request_date", nullable = false, updatable = false)
    private LocalDateTime requestDate; // 예: 2024-01-15T09:30:00 (JPA Auditing으로 자동 설정)
    
    @Column(name = "review_date")
    private LocalDateTime reviewDate; // 예: 2024-01-16T14:20:00
    
    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment; // 예: "승인합니다. 30일간 유효합니다."
    
    @OneToMany(mappedBy = "approval", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ApprovalStep> approvalSteps; // 연관된 승인 단계들 (템플릿 스냅샷)
    
    // Constructors
    public Approval() {}
    
    public Approval(ApprovalType type, String title, String description, Long requesterId, String requesterEmail, String requesterName) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.requesterId = requesterId;
        this.requesterEmail = requesterEmail;
        this.requesterName = requesterName;
    }
    
    // 편의 생성자 (User 객체 사용)
    public Approval(ApprovalType type, String title, String description, User requester) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.requesterId = requester.getId();
        this.requesterEmail = requester.getEmail();
        this.requesterName = requester.getName();
    }
    
    // 승인 대상 오브젝트 포함 생성자
    public Approval(ApprovalType type, String title, String description, User requester,
                   TargetType targetType, String targetId, String targetName,
                   AccessScope accessScope, SensitivityLevel sensitivityLevel) {
        this(type, title, description, requester);
        this.targetType = targetType;
        this.targetId = targetId;
        this.targetName = targetName;
        this.accessScope = accessScope;
        this.sensitivityLevel = sensitivityLevel;
        
        // 민감도에 따른 자동 설정
        this.requiresMasking = sensitivityLevel.requiresMasking();
        this.requiresAuditLog = sensitivityLevel.requiresAuditLog();
        
        // 접근 범위와 민감도에 따른 기본 사용 기간 설정
        if (accessScope.isTemporary()) {
            this.usageDurationDays = 1; // 임시 권한은 1일
        } else if (sensitivityLevel.isHighSensitive()) {
            this.usageDurationDays = 30; // 민감정보는 1개월
        } else {
            this.usageDurationDays = 90; // 일반적으로 3개월 (기본값)
        }
    }
    
    // Business Methods
    public void approve(User reviewer, String comment) {
        this.status = ApprovalStatus.APPROVED;
        this.reviewerId = reviewer.getId();
        this.reviewerEmail = reviewer.getEmail();
        this.reviewerName = reviewer.getName();
        this.reviewComment = comment;
        this.reviewDate = LocalDateTime.now();
    }
    
    public void reject(User reviewer, String comment) {
        this.status = ApprovalStatus.REJECTED;
        this.reviewerId = reviewer.getId();
        this.reviewerEmail = reviewer.getEmail();
        this.reviewerName = reviewer.getName();
        this.reviewComment = comment;
        this.reviewDate = LocalDateTime.now();
    }
    
    // 하위 호환성을 위한 메서드 (이메일 기반)
    @Deprecated
    public void approve(String reviewerEmail, String comment) {
        this.status = ApprovalStatus.APPROVED;
        this.reviewerEmail = reviewerEmail;
        this.reviewComment = comment;
        this.reviewDate = LocalDateTime.now();
    }
    
    @Deprecated
    public void reject(String reviewerEmail, String comment) {
        this.status = ApprovalStatus.REJECTED;
        this.reviewerEmail = reviewerEmail;
        this.reviewComment = comment;
        this.reviewDate = LocalDateTime.now();
    }
    
    /**
     * 전체 승인 단계 수 (계산 필드)
     * ApprovalStep 엔티티의 개수로 계산
     */
    public Integer getTotalSteps() {
        if (approvalSteps == null) {
            return 0;
        }
        return approvalSteps.size();
    }
    
    /**
     * 현재 진행 단계 (계산 필드)
     * 승인된 단계 수 + 1로 계산
     */
    public Integer getCurrentStep() {
        if (approvalSteps == null) {
            return 1;
        }
        
        long approvedCount = approvalSteps.stream()
            .filter(step -> step.getStatus() == ApprovalStepStatus.APPROVED)
            .count();
        
        return (int) approvedCount + 1;
    }
    
    /**
     * 승인 진행률 계산 (백분율)
     */
    public Double getProgressPercentage() {
        Integer total = getTotalSteps();
        if (total == 0) {
            return 0.0;
        }
        
        long approvedCount = approvalSteps.stream()
            .filter(step -> step.getStatus() == ApprovalStepStatus.APPROVED)
            .count();
        
        return (double) approvedCount / total * 100.0;
    }
    
    /**
     * 필수 단계 승인 완료 여부 확인
     */
    public Boolean isRequiredStepsCompleted() {
        if (approvalSteps == null) {
            return false;
        }
        
        return approvalSteps.stream()
            .filter(ApprovalStep::getIsRequired)
            .allMatch(step -> step.getStatus() == ApprovalStepStatus.APPROVED);
    }
    
    /**
     * 다음 승인 단계 조회
     */
    public ApprovalStep getNextPendingStep() {
        if (approvalSteps == null) {
            return null;
        }
        
        return approvalSteps.stream()
            .filter(step -> step.getStatus() == ApprovalStepStatus.PENDING)
            .min((s1, s2) -> Integer.compare(s1.getStepOrder(), s2.getStepOrder()))
            .orElse(null);
    }
    
    /**
     * 반려된 단계 존재 여부 확인
     */
    public Boolean hasRejectedStep() {
        if (approvalSteps == null) {
            return false;
        }
        
        return approvalSteps.stream()
            .anyMatch(step -> step.getStatus() == ApprovalStepStatus.REJECTED);
    }
    
    // ========== 승인 대상 오브젝트 관련 비즈니스 메서드 ==========
    
    /**
     * 고위험 승인 요청 여부 확인
     */
    public Boolean isHighRiskRequest() {
        return sensitivityLevel.isHighSensitive() || 
               accessScope.isHighRisk() || 
               (targetType.isSecurityType() && accessScope.includesWrite());
    }
    
    /**
     * 추가 승인 필요 여부 확인
     */
    public Boolean requiresAdditionalApproval() {
        return sensitivityLevel.requiresAdditionalApproval() || 
               accessScope.isHighRisk() ||
               isHighRiskRequest();
    }
    
    /**
     * 사용 권한 만료 시간 계산 및 설정
     */
    public void calculateUsageExpiration() {
        if (usageDurationDays != null && usageDurationDays > 0) {
            this.usageExpiresAt = LocalDateTime.now().plusDays(usageDurationDays);
        } else {
            // 기본 3개월 설정
            this.usageExpiresAt = LocalDateTime.now().plusDays(90);
        }
    }
    
    /**
     * 사용 권한 만료 여부 확인
     */
    public Boolean isUsageExpired() {
        return usageExpiresAt != null && LocalDateTime.now().isAfter(usageExpiresAt);
    }
    
    /**
     * 사용 권한 만료까지 남은 일수
     */
    public Long getDaysUntilExpiry() {
        if (usageExpiresAt == null) {
            return null;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), usageExpiresAt);
    }
    
    /**
     * 하위 호환성을 위한 메서드
     */
    @Deprecated
    public void calculateAccessExpiration() {
        calculateUsageExpiration();
    }
    
    @Deprecated
    public Boolean isAccessExpired() {
        return isUsageExpired();
    }
    
    /**
     * 마스킹 정책 적용 필요 여부
     */
    public Boolean requiresMaskingPolicy() {
        return requiresMasking || 
               sensitivityLevel.requiresMasking() || 
               accessScope.requiresMasking();
    }
    
    /**
     * 감사 로그 필요 여부
     */
    public Boolean requiresAuditLogging() {
        return requiresAuditLog || 
               sensitivityLevel.requiresAuditLog() || 
               accessScope.isHighRisk();
    }
    
    /**
     * 승인 완료 시 사용 권한 활성화
     */
    public void activateUsage() {
        if (this.status == ApprovalStatus.APPROVED) {
            calculateUsageExpiration();
        }
    }
    
    /**
     * 하위 호환성을 위한 메서드
     */
    @Deprecated
    public void activateAccess() {
        activateUsage();
    }
    
    /**
     * 대상 오브젝트 전체 식별자 생성
     */
    public String getFullTargetIdentifier() {
        return targetType.name() + ":" + targetId;
    }
    
    /**
     * 승인 요청 요약 정보 생성
     */
    public String getApprovalSummary() {
        return String.format("%s %s에 대한 %s 권한 요청 (민감도: %s)", 
                           type.getDisplayName(),
                           targetName != null ? targetName : targetId,
                           accessScope.getDisplayName(),
                           sensitivityLevel.getDisplayName());
    }
    
    /**
     * ApprovalType과 TargetType 호환성 검증
     */
    public boolean isTypeCompatible() {
        return type.isCompatibleWith(targetType);
    }
    
    /**
     * 승인 요청의 전체 설명 생성 (ApprovalType + TargetType 조합)
     */
    public String getFullActionDescription() {
        return String.format("%s %s (%s)", 
                           targetType.getDisplayName(), 
                           type.getDisplayName(),
                           type.getDescription());
    }
    
    /**
     * 위험도 평가 (ApprovalType과 TargetType 조합 고려)
     */
    public String getRiskAssessment() {
        boolean highRiskAction = type.isHighRiskAction();
        boolean sensitiveTarget = targetType.isSecurityType() || targetType.isDataType();
        boolean highSensitivity = sensitivityLevel.isHighSensitive();
        boolean highRiskAccess = accessScope.isHighRisk();
        
        int riskScore = 0;
        if (highRiskAction) riskScore += 3;
        if (sensitiveTarget) riskScore += 2;
        if (highSensitivity) riskScore += 2;
        if (highRiskAccess) riskScore += 2;
        if (type.isDataExposureRisk() && targetType.isDataType()) riskScore += 1;
        
        if (riskScore >= 7) return "매우 높음";
        if (riskScore >= 5) return "높음";
        if (riskScore >= 3) return "보통";
        return "낮음";
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public ApprovalType getType() { return type; }
    public void setType(ApprovalType type) { this.type = type; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ApprovalStatus getStatus() { return status; }
    public void setStatus(ApprovalStatus status) { this.status = status; }
    
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    
    // 요청자 관련 getter/setter
    public Long getRequesterId() { return requesterId; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }
    
    public User getRequesterUser() { return requesterUser; }
    public void setRequesterUser(User requesterUser) { this.requesterUser = requesterUser; }
    
    public String getRequesterEmail() { return requesterEmail; }
    public void setRequesterEmail(String requesterEmail) { this.requesterEmail = requesterEmail; }
    
    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    
    // 검토자 관련 getter/setter
    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }
    
    public User getReviewerUser() { return reviewerUser; }
    public void setReviewerUser(User reviewerUser) { this.reviewerUser = reviewerUser; }
    
    public String getReviewerEmail() { return reviewerEmail; }
    public void setReviewerEmail(String reviewerEmail) { this.reviewerEmail = reviewerEmail; }
    
    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }
    
    // 하위 호환성을 위한 메서드
    @Deprecated
    public String getRequester() { return requesterEmail; }
    
    @Deprecated
    public void setRequester(String requester) { this.requesterEmail = requester; }
    
    @Deprecated
    public String getReviewer() { return reviewerEmail; }
    
    @Deprecated
    public void setReviewer(String reviewer) { this.reviewerEmail = reviewer; }
    
    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }
    
    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }
    
    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
    
    public List<ApprovalStep> getApprovalSteps() { return approvalSteps; }
    public void setApprovalSteps(List<ApprovalStep> approvalSteps) { this.approvalSteps = approvalSteps; }
    
    // ========== 승인 대상 오브젝트 관련 getter/setter ==========
    
    public TargetType getTargetType() { return targetType; }
    public void setTargetType(TargetType targetType) { this.targetType = targetType; }
    
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    
    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }
    
    public String getTargetDescription() { return targetDescription; }
    public void setTargetDescription(String targetDescription) { this.targetDescription = targetDescription; }
    
    public AccessScope getAccessScope() { return accessScope; }
    public void setAccessScope(AccessScope accessScope) { this.accessScope = accessScope; }
    
    public SensitivityLevel getSensitivityLevel() { return sensitivityLevel; }
    public void setSensitivityLevel(SensitivityLevel sensitivityLevel) { this.sensitivityLevel = sensitivityLevel; }
    
    public Long getMaskingPolicyId() { return maskingPolicyId; }
    public void setMaskingPolicyId(Long maskingPolicyId) { this.maskingPolicyId = maskingPolicyId; }
    
    public Long getPolicySetId() { return policySetId; }
    public void setPolicySetId(Long policySetId) { this.policySetId = policySetId; }
    
    public Boolean getRequiresMasking() { return requiresMasking; }
    public void setRequiresMasking(Boolean requiresMasking) { this.requiresMasking = requiresMasking; }
    
    public Boolean getRequiresAuditLog() { return requiresAuditLog; }
    public void setRequiresAuditLog(Boolean requiresAuditLog) { this.requiresAuditLog = requiresAuditLog; }
    
    public Integer getUsageDurationDays() { return usageDurationDays; }
    public void setUsageDurationDays(Integer usageDurationDays) { this.usageDurationDays = usageDurationDays; }
    
    public LocalDateTime getUsageExpiresAt() { return usageExpiresAt; }
    public void setUsageExpiresAt(LocalDateTime usageExpiresAt) { this.usageExpiresAt = usageExpiresAt; }
    
    // 하위 호환성을 위한 메서드들
    @Deprecated
    public Integer getAccessDurationHours() { 
        return usageDurationDays != null ? usageDurationDays * 24 : null; 
    }
    
    @Deprecated
    public void setAccessDurationHours(Integer accessDurationHours) { 
        this.usageDurationDays = accessDurationHours != null ? accessDurationHours / 24 : null; 
    }
    
    @Deprecated
    public LocalDateTime getAccessExpiresAt() { return usageExpiresAt; }
    
    @Deprecated
    public void setAccessExpiresAt(LocalDateTime accessExpiresAt) { this.usageExpiresAt = accessExpiresAt; }
    
    public String getBusinessJustification() { return businessJustification; }
    public void setBusinessJustification(String businessJustification) { this.businessJustification = businessJustification; }
    
    public String getDataUsagePurpose() { return dataUsagePurpose; }
    public void setDataUsagePurpose(String dataUsagePurpose) { this.dataUsagePurpose = dataUsagePurpose; }
    
    public LocalDateTime getExpectedCompletionDate() { return expectedCompletionDate; }
    public void setExpectedCompletionDate(LocalDateTime expectedCompletionDate) { this.expectedCompletionDate = expectedCompletionDate; }
}