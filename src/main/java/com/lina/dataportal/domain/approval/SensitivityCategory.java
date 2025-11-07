package com.lina.dataportal.domain.approval;

/**
 * 민감도 기본 카테고리
 * 
 * 데이터를 크게 2가지 카테고리로 분류:
 * - NORMAL: 일반적인 업무 데이터 (보안 제약이 적음)
 * - SENSITIVE: 민감한 데이터 (강화된 보안 제약)
 */
public enum SensitivityCategory {
    
    /**
     * 일반 정보 카테고리
     * - 공개, 내부, 일반 업무 정보
     * - 상대적으로 낮은 보안 요구사항
     * - 긴 사용 기간 허용 (기본 3개월)
     */
    NORMAL("일반", "NORMAL", "일반적인 업무 데이터로 표준 보안 정책 적용"),
    
    /**
     * 민감 정보 카테고리  
     * - 민감, 기밀, 개인정보, 금융정보 등
     * - 강화된 보안 요구사항
     * - 짧은 사용 기간 (기본 1개월 이하)
     */
    SENSITIVE("민감", "SENSITIVE", "민감한 데이터로 강화된 보안 정책 적용");
    
    private final String displayName;   // 한글 표시명
    private final String code;          // 영문 코드
    private final String description;   // 설명
    
    SensitivityCategory(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }
    
    // ========== Getter 메서드 ==========
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    // ========== 유틸리티 메서드 ==========
    
    /**
     * 일반 카테고리 여부 확인
     */
    public boolean isNormal() {
        return this == NORMAL;
    }
    
    /**
     * 민감 카테고리 여부 확인
     */
    public boolean isSensitive() {
        return this == SENSITIVE;
    }
    
    /**
     * 기본 사용 기간 (일)
     */
    public int getDefaultUsageDays() {
        switch (this) {
            case NORMAL:
                return 90;  // 3개월
            case SENSITIVE:
                return 30;  // 1개월
            default:
                return 30;
        }
    }
    
    /**
     * 기본 보안 정책
     */
    public SecurityPolicy getDefaultSecurityPolicy() {
        switch (this) {
            case NORMAL:
                return SecurityPolicy.builder()
                    .requiresAdditionalApproval(false)
                    .requiresMasking(false)
                    .requiresAuditLog(false)
                    .maxUsageDays(180)
                    .build();
                    
            case SENSITIVE:
                return SecurityPolicy.builder()
                    .requiresAdditionalApproval(true)
                    .requiresMasking(true)
                    .requiresAuditLog(true)
                    .maxUsageDays(60)
                    .build();
                    
            default:
                return SecurityPolicy.builder().build();
        }
    }
    
    /**
     * 보안 정책 내부 클래스
     */
    public static class SecurityPolicy {
        private boolean requiresAdditionalApproval;
        private boolean requiresMasking;
        private boolean requiresAuditLog;
        private int maxUsageDays;
        
        private SecurityPolicy(Builder builder) {
            this.requiresAdditionalApproval = builder.requiresAdditionalApproval;
            this.requiresMasking = builder.requiresMasking;
            this.requiresAuditLog = builder.requiresAuditLog;
            this.maxUsageDays = builder.maxUsageDays;
        }
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private boolean requiresAdditionalApproval = false;
            private boolean requiresMasking = false;
            private boolean requiresAuditLog = false;
            private int maxUsageDays = 90;
            
            public Builder requiresAdditionalApproval(boolean requiresAdditionalApproval) {
                this.requiresAdditionalApproval = requiresAdditionalApproval;
                return this;
            }
            
            public Builder requiresMasking(boolean requiresMasking) {
                this.requiresMasking = requiresMasking;
                return this;
            }
            
            public Builder requiresAuditLog(boolean requiresAuditLog) {
                this.requiresAuditLog = requiresAuditLog;
                return this;
            }
            
            public Builder maxUsageDays(int maxUsageDays) {
                this.maxUsageDays = maxUsageDays;
                return this;
            }
            
            public SecurityPolicy build() {
                return new SecurityPolicy(this);
            }
        }
        
        // Getters
        public boolean isRequiresAdditionalApproval() { return requiresAdditionalApproval; }
        public boolean isRequiresMasking() { return requiresMasking; }
        public boolean isRequiresAuditLog() { return requiresAuditLog; }
        public int getMaxUsageDays() { return maxUsageDays; }
    }
}