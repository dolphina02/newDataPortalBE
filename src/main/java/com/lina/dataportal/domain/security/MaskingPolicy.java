package com.lina.dataportal.domain.security;

import com.lina.dataportal.domain.common.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 데이터 마스킹 정책
 * 민감정보 보호를 위한 마스킹 규칙 정의
 */
@Entity
@Table(name = "masking_policies")
public class MaskingPolicy extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false, unique = true)
    private String policyName; // 예: "PII_MASKING_POLICY"
    
    @NotBlank
    @Column(nullable = false)
    private String displayName; // 예: "개인정보 마스킹 정책"
    
    @Column(columnDefinition = "TEXT")
    private String description; // 예: "개인식별정보에 대한 마스킹 규칙"
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private MaskingType maskingType; // 예: PARTIAL, FULL, HASH, ENCRYPT
    
    @Column(name = "mask_pattern")
    private String maskPattern; // 예: "***-**-####", "XXX@XXX.com"
    
    @Column(name = "mask_character")
    private String maskCharacter = "*"; // 마스킹 문자
    
    @Column(name = "preserve_length")
    private Boolean preserveLength = true; // 원본 길이 유지 여부
    
    @Column(name = "preserve_format")
    private Boolean preserveFormat = false; // 원본 형식 유지 여부
    
    @Column(name = "applicable_columns", columnDefinition = "TEXT")
    private String applicableColumns; // JSON 형태로 적용 가능한 컬럼 패턴
    
    @Column(name = "exclusion_rules", columnDefinition = "TEXT")
    private String exclusionRules; // JSON 형태로 제외 규칙
    
    @NotNull
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Version
    private Long version = 0L;
    
    // Constructors
    public MaskingPolicy() {}
    
    public MaskingPolicy(String policyName, String displayName, MaskingType maskingType) {
        this.policyName = policyName;
        this.displayName = displayName;
        this.maskingType = maskingType;
    }
    
    // Business Methods
    public void activate() {
        this.isActive = true;
    }
    
    public void deactivate() {
        this.isActive = false;
    }
    
    public boolean isApplicableToColumn(String columnName) {
        // 실제 구현에서는 applicableColumns JSON을 파싱하여 확인
        return applicableColumns != null && applicableColumns.contains(columnName.toLowerCase());
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public MaskingType getMaskingType() { return maskingType; }
    public void setMaskingType(MaskingType maskingType) { this.maskingType = maskingType; }
    
    public String getMaskPattern() { return maskPattern; }
    public void setMaskPattern(String maskPattern) { this.maskPattern = maskPattern; }
    
    public String getMaskCharacter() { return maskCharacter; }
    public void setMaskCharacter(String maskCharacter) { this.maskCharacter = maskCharacter; }
    
    public Boolean getPreserveLength() { return preserveLength; }
    public void setPreserveLength(Boolean preserveLength) { this.preserveLength = preserveLength; }
    
    public Boolean getPreserveFormat() { return preserveFormat; }
    public void setPreserveFormat(Boolean preserveFormat) { this.preserveFormat = preserveFormat; }
    
    public String getApplicableColumns() { return applicableColumns; }
    public void setApplicableColumns(String applicableColumns) { this.applicableColumns = applicableColumns; }
    
    public String getExclusionRules() { return exclusionRules; }
    public void setExclusionRules(String exclusionRules) { this.exclusionRules = exclusionRules; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}

/**
 * 마스킹 타입
 */
enum MaskingType {
    PARTIAL("부분 마스킹", "일부만 마스킹"),
    FULL("전체 마스킹", "전체를 마스킹"),
    HASH("해시", "해시값으로 변환"),
    ENCRYPT("암호화", "암호화하여 저장"),
    TOKENIZE("토큰화", "토큰으로 대체"),
    REDACT("삭제", "완전히 제거");
    
    private final String displayName;
    private final String description;
    
    MaskingType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}