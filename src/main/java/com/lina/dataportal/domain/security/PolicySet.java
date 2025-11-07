package com.lina.dataportal.domain.security;

import com.lina.dataportal.domain.common.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 보안 정책 세트
 * 여러 보안 정책을 묶어서 관리하는 정책 집합
 */
@Entity
@Table(name = "policy_sets")
public class PolicySet extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false, unique = true)
    private String policySetName; // 예: "FINANCIAL_DATA_POLICY_SET"
    
    @NotBlank
    @Column(nullable = false)
    private String displayName; // 예: "금융 데이터 보안 정책 세트"
    
    @Column(columnDefinition = "TEXT")
    private String description; // 예: "금융 관련 데이터에 적용되는 종합 보안 정책"
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private PolicyCategory category; // 예: DATA_PROTECTION, ACCESS_CONTROL, COMPLIANCE
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private PolicySeverity severity; // 예: LOW, MEDIUM, HIGH, CRITICAL
    
    // 포함된 정책들 (JSON 형태로 저장)
    @Column(name = "masking_policy_ids", columnDefinition = "TEXT")
    private String maskingPolicyIds; // 예: "[1,2,3]"
    
    @Column(name = "access_control_rules", columnDefinition = "TEXT")
    private String accessControlRules; // JSON 형태의 접근 제어 규칙
    
    @Column(name = "audit_requirements", columnDefinition = "TEXT")
    private String auditRequirements; // JSON 형태의 감사 요구사항
    
    @Column(name = "retention_policy", columnDefinition = "TEXT")
    private String retentionPolicy; // JSON 형태의 보존 정책
    
    @Column(name = "encryption_requirements", columnDefinition = "TEXT")
    private String encryptionRequirements; // JSON 형태의 암호화 요구사항
    
    // 적용 조건
    @Column(name = "applicable_target_types", columnDefinition = "TEXT")
    private String applicableTargetTypes; // 적용 가능한 대상 타입들
    
    @Column(name = "applicable_sensitivity_levels", columnDefinition = "TEXT")
    private String applicableSensitivityLevels; // 적용 가능한 민감도 레벨들
    
    @Column(name = "required_approvals")
    private Integer requiredApprovals = 1; // 필요한 승인 수
    
    @Column(name = "approval_timeout_hours")
    private Integer approvalTimeoutHours = 72; // 승인 타임아웃 (시간)
    
    @NotNull
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @NotNull
    @Column(nullable = false)
    private Boolean isDefault = false; // 기본 정책 세트 여부
    
    @Version
    private Long version = 0L;
    
    // Constructors
    public PolicySet() {}
    
    public PolicySet(String policySetName, String displayName, PolicyCategory category, PolicySeverity severity) {
        this.policySetName = policySetName;
        this.displayName = displayName;
        this.category = category;
        this.severity = severity;
    }
    
    // Business Methods
    public void activate() {
        this.isActive = true;
    }
    
    public void deactivate() {
        this.isActive = false;
    }
    
    public void setAsDefault() {
        this.isDefault = true;
    }
    
    public void unsetAsDefault() {
        this.isDefault = false;
    }
    
    public boolean isHighSeverity() {
        return severity == PolicySeverity.HIGH || severity == PolicySeverity.CRITICAL;
    }
    
    public boolean requiresMultipleApprovals() {
        return requiredApprovals > 1;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPolicySetName() { return policySetName; }
    public void setPolicySetName(String policySetName) { this.policySetName = policySetName; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public PolicyCategory getCategory() { return category; }
    public void setCategory(PolicyCategory category) { this.category = category; }
    
    public PolicySeverity getSeverity() { return severity; }
    public void setSeverity(PolicySeverity severity) { this.severity = severity; }
    
    public String getMaskingPolicyIds() { return maskingPolicyIds; }
    public void setMaskingPolicyIds(String maskingPolicyIds) { this.maskingPolicyIds = maskingPolicyIds; }
    
    public String getAccessControlRules() { return accessControlRules; }
    public void setAccessControlRules(String accessControlRules) { this.accessControlRules = accessControlRules; }
    
    public String getAuditRequirements() { return auditRequirements; }
    public void setAuditRequirements(String auditRequirements) { this.auditRequirements = auditRequirements; }
    
    public String getRetentionPolicy() { return retentionPolicy; }
    public void setRetentionPolicy(String retentionPolicy) { this.retentionPolicy = retentionPolicy; }
    
    public String getEncryptionRequirements() { return encryptionRequirements; }
    public void setEncryptionRequirements(String encryptionRequirements) { this.encryptionRequirements = encryptionRequirements; }
    
    public String getApplicableTargetTypes() { return applicableTargetTypes; }
    public void setApplicableTargetTypes(String applicableTargetTypes) { this.applicableTargetTypes = applicableTargetTypes; }
    
    public String getApplicableSensitivityLevels() { return applicableSensitivityLevels; }
    public void setApplicableSensitivityLevels(String applicableSensitivityLevels) { this.applicableSensitivityLevels = applicableSensitivityLevels; }
    
    public Integer getRequiredApprovals() { return requiredApprovals; }
    public void setRequiredApprovals(Integer requiredApprovals) { this.requiredApprovals = requiredApprovals; }
    
    public Integer getApprovalTimeoutHours() { return approvalTimeoutHours; }
    public void setApprovalTimeoutHours(Integer approvalTimeoutHours) { this.approvalTimeoutHours = approvalTimeoutHours; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}

/**
 * 정책 카테고리
 */
enum PolicyCategory {
    DATA_PROTECTION("데이터 보호", "데이터 보호 관련 정책"),
    ACCESS_CONTROL("접근 제어", "접근 제어 관련 정책"),
    COMPLIANCE("규정 준수", "규정 준수 관련 정책"),
    PRIVACY("개인정보보호", "개인정보보호 관련 정책"),
    SECURITY("보안", "일반 보안 관련 정책"),
    AUDIT("감사", "감사 관련 정책");
    
    private final String displayName;
    private final String description;
    
    PolicyCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}

/**
 * 정책 심각도
 */
enum PolicySeverity {
    LOW("낮음", "일반적인 보안 수준", 1),
    MEDIUM("보통", "중간 보안 수준", 2),
    HIGH("높음", "높은 보안 수준", 3),
    CRITICAL("매우높음", "최고 보안 수준", 4);
    
    private final String displayName;
    private final String description;
    private final int level;
    
    PolicySeverity(String displayName, String description, int level) {
        this.displayName = displayName;
        this.description = description;
        this.level = level;
    }
    
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public int getLevel() { return level; }
}