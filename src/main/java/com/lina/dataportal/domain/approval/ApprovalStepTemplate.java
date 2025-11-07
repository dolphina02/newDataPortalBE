package com.lina.dataportal.domain.approval;

import com.lina.dataportal.domain.common.BaseAuditEntity;
import com.lina.dataportal.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "approval_step_templates")
public class ApprovalStepTemplate extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ApprovalType approvalType;
    
    @NotNull
    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;
    
    @NotBlank
    @Column(name = "approver_role", nullable = false)
    private String approverRole;
    
    @Column(name = "approver_department")
    private String approverDepartment;
    
    @Column(name = "approver_id")
    private Long approverId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", insertable = false, updatable = false)
    private User approverUser;
    
    @Column(name = "approver_email")
    private String approverEmail;
    
    @Column(name = "approver_name")
    private String approverName;
    
    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = true;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "description")
    private String description;
    
    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;
    
    // Constructors
    public ApprovalStepTemplate() {}
    
    public ApprovalStepTemplate(ApprovalType approvalType, Integer stepOrder, String approverRole) {
        this.approvalType = approvalType;
        this.stepOrder = stepOrder;
        this.approverRole = approverRole;
    }
    
    public ApprovalStepTemplate(ApprovalType approvalType, Integer stepOrder, String approverRole, String approverDepartment) {
        this.approvalType = approvalType;
        this.stepOrder = stepOrder;
        this.approverRole = approverRole;
        this.approverDepartment = approverDepartment;
    }
    
    public ApprovalStepTemplate(ApprovalType approvalType, Integer stepOrder, String approverRole, String approverDepartment, Long approverId, String approverEmail, String approverName) {
        this.approvalType = approvalType;
        this.stepOrder = stepOrder;
        this.approverRole = approverRole;
        this.approverDepartment = approverDepartment;
        this.approverId = approverId;
        this.approverEmail = approverEmail;
        this.approverName = approverName;
    }
    
    public ApprovalStepTemplate(ApprovalType approvalType, Integer stepOrder, String approverRole, String approverDepartment, User approver) {
        this.approvalType = approvalType;
        this.stepOrder = stepOrder;
        this.approverRole = approverRole;
        this.approverDepartment = approverDepartment;
        if (approver != null) {
            this.approverId = approver.getId();
            this.approverEmail = approver.getEmail();
            this.approverName = approver.getName();
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public ApprovalType getApprovalType() { return approvalType; }
    public void setApprovalType(ApprovalType approvalType) { this.approvalType = approvalType; }
    
    public Integer getStepOrder() { return stepOrder; }
    public void setStepOrder(Integer stepOrder) { this.stepOrder = stepOrder; }
    
    public String getApproverRole() { return approverRole; }
    public void setApproverRole(String approverRole) { this.approverRole = approverRole; }
    
    public String getApproverDepartment() { return approverDepartment; }
    public void setApproverDepartment(String approverDepartment) { this.approverDepartment = approverDepartment; }
    
    public Boolean getIsRequired() { return isRequired; }
    public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getApproverId() { return approverId; }
    public void setApproverId(Long approverId) { this.approverId = approverId; }
    
    public User getApproverUser() { return approverUser; }
    public void setApproverUser(User approverUser) { this.approverUser = approverUser; }
    
    public String getApproverEmail() { return approverEmail; }
    public void setApproverEmail(String approverEmail) { this.approverEmail = approverEmail; }
    
    public String getApproverName() { return approverName; }
    public void setApproverName(String approverName) { this.approverName = approverName; }
    
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
