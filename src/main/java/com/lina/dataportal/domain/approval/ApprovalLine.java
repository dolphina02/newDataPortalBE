package com.lina.dataportal.domain.approval;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_lines")
public class ApprovalLine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "approval_id", nullable = false)
    private Long approvalId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_id", insertable = false, updatable = false)
    private Approval approval;
    
    @NotNull
    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;
    
    @NotBlank
    @Column(name = "approver_id", nullable = false)
    private String approverId;
    
    @NotBlank
    @Column(name = "approver_name", nullable = false)
    private String approverName;
    
    @Column(name = "approver_role")
    private String approverRole;
    
    @Column(name = "approver_department")
    private String approverDepartment;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ApprovalLineStatus status = ApprovalLineStatus.PENDING;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "approval_comment", columnDefinition = "TEXT")
    private String approvalComment;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public ApprovalLine() {}
    
    public ApprovalLine(Long approvalId, Integer stepOrder, String approverId, String approverName) {
        this.approvalId = approvalId;
        this.stepOrder = stepOrder;
        this.approverId = approverId;
        this.approverName = approverName;
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
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}