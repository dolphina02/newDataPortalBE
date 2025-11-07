package com.lina.dataportal.domain.approval;

import com.lina.dataportal.domain.common.BaseAuditEntity;
import com.lina.dataportal.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_line_templates")
public class ApprovalLineTemplate extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예: 1
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ApprovalType approvalType; // 예: DATA_ACCESS, REPORT_PUBLISH, DASHBOARD_CREATE
    
    @NotNull
    @Column(name = "step_order", nullable = false)
    private Integer stepOrder; // 예: 1 (승인 단계 순서)
    
    @NotBlank
    @Column(name = "approver_role", nullable = false)
    private String approverRole; // 예: "팀장", "부장", "이사"
    
    @Column(name = "approver_department")
    private String approverDepartment; // 예: "데이터분석팀", "IT팀", "경영지원팀"
    
    // 승인자 정보 (정규화) - 선택적 (역할/부서만으로도 템플릿 생성 가능)
    @Column(name = "approver_id")
    private Long approverId; // 예: 2 (User 테이블의 ID, 특정 담당자 지정 시)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", insertable = false, updatable = false)
    private User approverUser; // 연관된 승인자 정보
    
    @Column(name = "approver_email")
    private String approverEmail; // 예: "kim.leader@company.com" (보조 정보)
    
    @Column(name = "approver_name")
    private String approverName; // 예: "김팀장" (보조 정보)
    
    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = true; // 예: true (필수 승인 단계 여부)
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // 예: true (템플릿 활성 상태)
    
    @Column(name = "description")
    private String description; // 예: "데이터 접근 권한에 대한 팀장 승인"
    
    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L; // 예: 0, 1, 2... (템플릿 수정 시마다 증가)
    
    // Constructors
    public ApprovalLineTemplate() {}
    
    public ApprovalLineTemplate(ApprovalType approvalType, Integer stepOrder, String approverRole) {
        this.approvalType = approvalType;
        this.stepOrder = stepOrder;
        this.approverRole = approverRole;
    }
    
    public ApprovalLineTemplate(ApprovalType approvalType, Integer stepOrder, String approverRole, String approverDepartment) {
        this.approvalType = approvalType;
        this.stepOrder = stepOrder;
        this.approverRole = approverRole;
        this.approverDepartment = approverDepartment;
    }
    
    public ApprovalLineTemplate(ApprovalType approvalType, Integer stepOrder, String approverRole, String approverDepartment, Long approverId, String approverEmail, String approverName) {
        this.approvalType = approvalType;
        this.stepOrder = stepOrder;
        this.approverRole = approverRole;
        this.approverDepartment = approverDepartment;
        this.approverId = approverId;
        this.approverEmail = approverEmail;
        this.approverName = approverName;
    }
    
    // 편의 생성자 (User 객체 사용)
    public ApprovalLineTemplate(ApprovalType approvalType, Integer stepOrder, String approverRole, String approverDepartment, User approver) {
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