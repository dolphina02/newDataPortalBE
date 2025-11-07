package com.lina.dataportal.domain.approval;

import com.lina.dataportal.domain.common.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_lines")
public class ApprovalLine extends BaseAuditEntity {
    
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
    private Long templateId; // 예: 1 (ApprovalLineTemplate의 ID)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", insertable = false, updatable = false)
    private ApprovalLineTemplate template; // 연관된 승인 라인 템플릿
    
    @NotNull
    @Column(name = "step_order", nullable = false)
    private Integer stepOrder; // 예: 1 (승인 단계 순서)
    
    @NotBlank
    @Column(name = "approver_id", nullable = false)
    private String approverId; // 예: "manager.kim@company.com" (실제 승인자 ID)
    
    @NotBlank
    @Column(name = "approver_name", nullable = false)
    private String approverName; // 예: "김팀장" (실제 승인자 이름)
    
    @Column(name = "approver_role")
    private String approverRole; // 예: "팀장", "부장", "이사" (템플릿에서 복사)
    
    @Column(name = "approver_department")
    private String approverDepartment; // 예: "데이터분석팀" (템플릿에서 복사)
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ApprovalLineStatus status = ApprovalLineStatus.PENDING; // 예: PENDING, APPROVED, REJECTED, SKIPPED
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt; // 예: 2024-01-16T10:30:00
    
    @Column(name = "approval_comment", columnDefinition = "TEXT")
    private String approvalComment; // 예: "데이터 접근 권한을 승인합니다."
    
    // Constructors
    public ApprovalLine() {}
    
    public ApprovalLine(Long approvalId, Long templateId, Integer stepOrder, String approverId, String approverName) {
        this.approvalId = approvalId;
        this.templateId = templateId;
        this.stepOrder = stepOrder;
        this.approverId = approverId;
        this.approverName = approverName;
    }
    
    // 템플릿으로부터 인스턴스 생성
    public static ApprovalLine fromTemplate(Long approvalId, ApprovalLineTemplate template, String approverId, String approverName) {
        ApprovalLine instance = new ApprovalLine();
        instance.approvalId = approvalId;
        instance.templateId = template.getId();
        instance.stepOrder = template.getStepOrder();
        instance.approverId = approverId;
        instance.approverName = approverName;
        instance.approverRole = template.getApproverRole();
        instance.approverDepartment = template.getApproverDepartment();
        return instance;
    }
    
    // Business Methods
    public void approve(String comment) {
        this.status = ApprovalLineStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
        this.approvalComment = comment;
    }
    
    public void reject(String comment) {
        this.status = ApprovalLineStatus.REJECTED;
        this.approvedAt = LocalDateTime.now();
        this.approvalComment = comment;
    }
    
    public void skip(String comment) {
        this.status = ApprovalLineStatus.SKIPPED;
        this.approvedAt = LocalDateTime.now();
        this.approvalComment = comment;
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
    
    public ApprovalLineTemplate getTemplate() { return template; }
    public void setTemplate(ApprovalLineTemplate template) { this.template = template; }
    
    public Integer getStepOrder() { return stepOrder; }
    public void setStepOrder(Integer stepOrder) { this.stepOrder = stepOrder; }
    
    public String getApproverId() { return approverId; }
    public void setApproverId(String approverId) { this.approverId = approverId; }
    
    public String getApproverName() { return approverName; }
    public void setApproverName(String approverName) { this.approverName = approverName; }
    
    public String getApproverRole() { return approverRole; }
    public void setApproverRole(String approverRole) { this.approverRole = approverRole; }
    
    public String getApproverDepartment() { return approverDepartment; }
    public void setApproverDepartment(String approverDepartment) { this.approverDepartment = approverDepartment; }
    
    public ApprovalLineStatus getStatus() { return status; }
    public void setStatus(ApprovalLineStatus status) { this.status = status; }
    
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    
    public String getApprovalComment() { return approvalComment; }
    public void setApprovalComment(String approvalComment) { this.approvalComment = approvalComment; }
}