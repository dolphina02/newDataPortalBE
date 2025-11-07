package com.lina.dataportal.domain.approval;

import com.lina.dataportal.domain.common.BaseAuditEntity;
import com.lina.dataportal.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 실제 승인 진행을 위한 승인 단계 (템플릿의 스냅샷)
 * 승인 요청 생성 시 ApprovalStepTemplate을 기반으로 생성되며,
 * 이후 템플릿이 변경되어도 기존 진행 건에는 영향을 주지 않습니다.
 */
@Entity
@Table(name = "approval_steps")
public class ApprovalStep extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예: 1
    
    @NotNull
    @Column(name = "approval_id", nullable = false)
    private Long approvalId; // 예: 1 (연관된 Approval의 ID)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_id", insertable = false, updatable = false)
    private Approval approval; // 연관된 승인 요청
    
    @NotNull
    @Column(name = "template_id", nullable = false)
    private Long templateId; // 예: 1 (기반이 된 ApprovalStepTemplate의 ID)
    
    @NotNull
    @Column(name = "template_version", nullable = false)
    private Long templateVersion; // 예: 2 (템플릿 생성 당시의 버전)
    
    @NotNull
    @Column(name = "step_order", nullable = false)
    private Integer stepOrder; // 예: 1 (승인 단계 순서)
    
    // 승인자 정보 (정규화)
    @NotNull
    @Column(name = "approver_id", nullable = false)
    private Long approverId; // 예: 2 (User 테이블의 ID)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", insertable = false, updatable = false)
    private User approverUser; // 연관된 승인자 정보
    
    @NotBlank
    @Column(name = "approver_email", nullable = false)
    private String approverEmail; // 예: "manager.kim@company.com" (보조 정보, 빠른 조회용)
    
    @NotBlank
    @Column(name = "approver_name", nullable = false)
    private String approverName; // 예: "김팀장" (보조 정보, 빠른 조회용)
    
    @Column(name = "approver_role")
    private String approverRole; // 예: "팀장", "부장", "이사" (템플릿에서 복사)
    
    @Column(name = "approver_department")
    private String approverDepartment; // 예: "데이터분석팀" (템플릿에서 복사)
    
    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = true; // 예: true (필수 승인 단계 여부, 템플릿에서 복사)
    
    @Column(name = "description")
    private String description; // 예: "데이터 접근 권한에 대한 팀장 승인" (템플릿에서 복사)
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ApprovalStepStatus status = ApprovalStepStatus.PENDING; // 예: PENDING, APPROVED, REJECTED, SKIPPED
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt; // 예: 2024-01-16T10:30:00
    
    @Column(name = "approval_comment", columnDefinition = "TEXT")
    private String approvalComment; // 예: "데이터 접근 권한을 승인합니다."
    
    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L; // 예: 0, 1, 2... (동시성 제어용)
    
    // Constructors
    public ApprovalStep() {}
    
    public ApprovalStep(Long approvalId, Long templateId, Long templateVersion, Integer stepOrder, 
                       Long approverId, String approverEmail, String approverName) {
        this.approvalId = approvalId;
        this.templateId = templateId;
        this.templateVersion = templateVersion;
        this.stepOrder = stepOrder;
        this.approverId = approverId;
        this.approverEmail = approverEmail;
        this.approverName = approverName;
    }
    
    // 편의 생성자 (User 객체 사용)
    public ApprovalStep(Long approvalId, Long templateId, Long templateVersion, Integer stepOrder, User approver) {
        this.approvalId = approvalId;
        this.templateId = templateId;
        this.templateVersion = templateVersion;
        this.stepOrder = stepOrder;
        this.approverId = approver.getId();
        this.approverEmail = approver.getEmail();
        this.approverName = approver.getName();
    }
    
    /**
     * 템플릿으로부터 승인 단계 생성 (스냅샷)
     */
    public static ApprovalStep fromTemplate(Long approvalId, ApprovalStepTemplate template) {
        ApprovalStep step = new ApprovalStep();
        step.approvalId = approvalId;
        step.templateId = template.getId();
        step.templateVersion = template.getVersion();
        step.stepOrder = template.getStepOrder();
        // 템플릿의 승인자 정보를 그대로 복사 (템플릿도 User 참조키 사용 가정)
        step.approverRole = template.getApproverRole();
        step.approverDepartment = template.getApproverDepartment();
        step.isRequired = template.getIsRequired();
        step.description = template.getDescription();
        return step;
    }
    
    /**
     * 템플릿으로부터 승인 단계 생성 (실제 승인자 지정 - User 객체)
     */
    public static ApprovalStep fromTemplate(Long approvalId, ApprovalStepTemplate template, User actualApprover) {
        ApprovalStep step = fromTemplate(approvalId, template);
        step.approverId = actualApprover.getId();
        step.approverEmail = actualApprover.getEmail();
        step.approverName = actualApprover.getName();
        return step;
    }
    
    /**
     * 템플릿으로부터 승인 단계 생성 (실제 승인자 지정 - 개별 필드)
     */
    public static ApprovalStep fromTemplate(Long approvalId, ApprovalStepTemplate template, 
                                          Long actualApproverId, String actualApproverEmail, String actualApproverName) {
        ApprovalStep step = fromTemplate(approvalId, template);
        step.approverId = actualApproverId;
        step.approverEmail = actualApproverEmail;
        step.approverName = actualApproverName;
        return step;
    }
    
    // Business Methods
    public void approve(String comment) {
        this.status = ApprovalStepStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
        this.approvalComment = comment;
    }
    
    public void reject(String comment) {
        this.status = ApprovalStepStatus.REJECTED;
        this.approvedAt = LocalDateTime.now();
        this.approvalComment = comment;
    }
    
    public void skip(String comment) {
        this.status = ApprovalStepStatus.SKIPPED;
        this.approvedAt = LocalDateTime.now();
        this.approvalComment = comment;
    }
    
    public boolean isPending() {
        return this.status == ApprovalStepStatus.PENDING;
    }
    
    public boolean isProcessed() {
        return this.status != ApprovalStepStatus.PENDING;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getApprovalId() { return approvalId; }
    public void setApprovalId(Long approvalId) { this.approvalId = approvalId; }
    
    public Approval getApproval() { return approval; }
    public void setApproval(Approval approval) { this.approval = approval; }
    
    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    
    public Long getTemplateVersion() { return templateVersion; }
    public void setTemplateVersion(Long templateVersion) { this.templateVersion = templateVersion; }
    
    public Integer getStepOrder() { return stepOrder; }
    public void setStepOrder(Integer stepOrder) { this.stepOrder = stepOrder; }
    
    public Long getApproverId() { return approverId; }
    public void setApproverId(Long approverId) { this.approverId = approverId; }
    
    public User getApproverUser() { return approverUser; }
    public void setApproverUser(User approverUser) { this.approverUser = approverUser; }
    
    public String getApproverEmail() { return approverEmail; }
    public void setApproverEmail(String approverEmail) { this.approverEmail = approverEmail; }
    
    public String getApproverName() { return approverName; }
    public void setApproverName(String approverName) { this.approverName = approverName; }
    
    public String getApproverRole() { return approverRole; }
    public void setApproverRole(String approverRole) { this.approverRole = approverRole; }
    
    public String getApproverDepartment() { return approverDepartment; }
    public void setApproverDepartment(String approverDepartment) { this.approverDepartment = approverDepartment; }
    
    public Boolean getIsRequired() { return isRequired; }
    public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ApprovalStepStatus getStatus() { return status; }
    public void setStatus(ApprovalStepStatus status) { this.status = status; }
    
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    
    public String getApprovalComment() { return approvalComment; }
    public void setApprovalComment(String approvalComment) { this.approvalComment = approvalComment; }
    
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}