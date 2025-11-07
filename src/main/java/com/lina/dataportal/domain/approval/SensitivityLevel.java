package com.lina.dataportal.domain.approval;

/**
 * 데이터 민감도 레벨 정의
 * 
 * 2단계 구조:
 * 1. 기본 카테고리: NORMAL vs SENSITIVE
 * 2. 세부 레벨: 각 카테고리 내 세분화
 */
public enum SensitivityLevel {
    
    // ========== NORMAL 카테고리 (일반 정보) ==========
    
    /**
     * 공개 정보 - 외부 공개 가능한 정보
     * 사용기간: 90일, 추가승인: 불필요, 마스킹: 불필요
     */
    PUBLIC("공개", "PUBLIC", SensitivityCategory.NORMAL, 0, 90, false, false, false),
    
    /**
     * 내부 정보 - 조직 내부에서만 사용하는 일반 정보
     * 사용기간: 90일, 추가승인: 불필요, 마스킹: 불필요
     */
    INTERNAL("내부", "INTERNAL", SensitivityCategory.NORMAL, 1, 90, false, false, false),
    
    /**
     * 일반 업무 정보 - 표준적인 업무 데이터
     * 사용기간: 90일, 추가승인: 불필요, 마스킹: 불필요
     */
    NORMAL("일반", "NORMAL", SensitivityCategory.NORMAL, 2, 90, false, false, false),
    
    // ========== SENSITIVE 카테고리 (민감 정보) ==========
    
    /**
     * 민감 정보 - 주의가 필요한 업무 정보
     * 사용기간: 30일, 추가승인: 필요, 마스킹: 권장
     */
    SENSITIVE("민감", "SENSITIVE", SensitivityCategory.SENSITIVE, 3, 30, true, true, true),
    
    /**
     * 기밀 정보 - 높은 수준의 보안이 필요한 정보
     * 사용기간: 30일, 추가승인: 필요, 마스킹: 필수
     */
    CONFIDENTIAL("기밀", "CONFIDENTIAL", SensitivityCategory.SENSITIVE, 4, 30, true, true, true),
    
    /**
     * 극비 정보 - 최고 수준의 보안이 필요한 정보
     * 사용기간: 7일, 추가승인: 필수, 마스킹: 필수
     */
    RESTRICTED("극비", "RESTRICTED", SensitivityCategory.SENSITIVE, 5, 7, true, true, true),
    
    // ========== 특수 카테고리 (규제 대상 정보) ==========
    
    /**
     * 개인정보 (PII) - 개인정보보호법 적용 대상
     * 사용기간: 30일, 추가승인: 필수, 마스킹: 필수
     */
    PII("개인정보", "PII", SensitivityCategory.SENSITIVE, 4, 30, true, true, true),
    
    /**
     * 의료정보 (PHI) - 의료법 적용 대상
     * 사용기간: 30일, 추가승인: 필수, 마스킹: 필수
     */
    PHI("의료정보", "PHI", SensitivityCategory.SENSITIVE, 4, 30, true, true, true),
    
    /**
     * 금융정보 - 금융관련법 적용 대상
     * 사용기간: 30일, 추가승인: 필수, 마스킹: 필수
     */
    FINANCIAL("금융정보", "FINANCIAL", SensitivityCategory.SENSITIVE, 4, 30, true, true, true),
    
    /**
     * 규제정보 - 기타 규제 적용 대상
     * 사용기간: 30일, 추가승인: 필수, 마스킹: 필수
     */
    REGULATORY("규제정보", "REGULATORY", SensitivityCategory.SENSITIVE, 4, 30, true, true, true);
    
    // ========== 필드 정의 ==========
    
    private final String displayName;           // 한글 표시명
    private final String code;                  // 영문 코드
    private final SensitivityCategory category; // 기본 카테고리
    private final int level;                    // 민감도 레벨 (0-5)
    private final int defaultUsageDays;         // 기본 사용 기간 (일)
    private final boolean requiresAdditionalApproval; // 추가 승인 필요 여부
    private final boolean requiresMasking;      // 마스킹 필요 여부
    private final boolean requiresAuditLog;     // 감사 로그 필요 여부
    
    // ========== 생성자 ==========
    
    SensitivityLevel(String displayName, String code, SensitivityCategory category, 
                    int level, int defaultUsageDays, boolean requiresAdditionalApproval,
                    boolean requiresMasking, boolean requiresAuditLog) {
        this.displayName = displayName;
        this.code = code;
        this.category = category;
        this.level = level;
        this.defaultUsageDays = defaultUsageDays;
        this.requiresAdditionalApproval = requiresAdditionalApproval;
        this.requiresMasking = requiresMasking;
        this.requiresAuditLog = requiresAuditLog;
    }
    
    // ========== 카테고리 판별 메서드 ==========
    
    /**
     * 일반 정보 여부 확인
     */
    public boolean isNormal() {
        return this.category == SensitivityCategory.NORMAL;
    }
    
    /**
     * 민감 정보 여부 확인
     */
    public boolean isSensitive() {
        return this.category == SensitivityCategory.SENSITIVE;
    }
    
    /**
     * 고민감도 정보 여부 확인 (레벨 4 이상)
     */
    public boolean isHighSensitive() {
        return this.level >= 4;
    }
    
    /**
     * 개인정보 관련 여부 확인
     */
    public boolean isPersonalData() {
        return this == PII || this == PHI;
    }
    
    /**
     * 규제 대상 정보 여부 확인
     */
    public boolean isRegulated() {
        return this == PII || this == PHI || this == FINANCIAL || this == REGULATORY;
    }
    
    // ========== 보안 정책 메서드 ==========
    
    /**
     * 마스킹 필요 여부
     */
    public boolean requiresMasking() {
        return this.requiresMasking;
    }
    
    /**
     * 감사 로그 필요 여부
     */
    public boolean requiresAuditLog() {
        return this.requiresAuditLog;
    }
    
    /**
     * 추가 승인 필요 여부
     */
    public boolean requiresAdditionalApproval() {
        return this.requiresAdditionalApproval;
    }
    
    // ========== 사용 기간 관련 메서드 ==========
    
    /**
     * 기본 사용 기간 (일)
     */
    public int getDefaultUsageDays() {
        return this.defaultUsageDays;
    }
    
    /**
     * 최대 허용 사용 기간 (일)
     * 민감도에 따라 제한
     */
    public int getMaxAllowedUsageDays() {
        switch (this.category) {
            case NORMAL:
                return 180; // 최대 6개월
            case SENSITIVE:
                if (this.level >= 5) return 7;   // 극비: 최대 1주일
                if (this.level >= 4) return 60;  // 기밀/개인정보: 최대 2개월
                return 90; // 민감: 최대 3개월
            default:
                return 90;
        }
    }
    
    // ========== 비교 및 정렬 메서드 ==========
    
    /**
     * 민감도 레벨 비교 (높을수록 더 민감)
     */
    public boolean isMoreSensitiveThan(SensitivityLevel other) {
        return this.level > other.level;
    }
    
    /**
     * 민감도 레벨 비교 (낮을수록 덜 민감)
     */
    public boolean isLessSensitiveThan(SensitivityLevel other) {
        return this.level < other.level;
    }
    
    // ========== 카테고리별 조회 메서드 ==========
    
    /**
     * 일반 정보 카테고리 목록
     */
    public static SensitivityLevel[] getNormalLevels() {
        return new SensitivityLevel[]{PUBLIC, INTERNAL, NORMAL};
    }
    
    /**
     * 민감 정보 카테고리 목록
     */
    public static SensitivityLevel[] getSensitiveLevels() {
        return new SensitivityLevel[]{SENSITIVE, CONFIDENTIAL, RESTRICTED, PII, PHI, FINANCIAL, REGULATORY};
    }
    
    /**
     * 개인정보 관련 목록
     */
    public static SensitivityLevel[] getPersonalDataLevels() {
        return new SensitivityLevel[]{PII, PHI};
    }
    
    /**
     * 규제 대상 목록
     */
    public static SensitivityLevel[] getRegulatedLevels() {
        return new SensitivityLevel[]{PII, PHI, FINANCIAL, REGULATORY};
    }
    
    // ========== Getter 메서드 ==========
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public SensitivityCategory getCategory() {
        return category;
    }
    
    public int getLevel() {
        return level;
    }
    
    // ========== 설명 메서드 ==========
    
    /**
     * 민감도 레벨 상세 설명
     */
    public String getDescription() {
        switch (this) {
            case PUBLIC:
                return "외부 공개가 가능한 일반 정보";
            case INTERNAL:
                return "조직 내부에서만 사용하는 일반 정보";
            case NORMAL:
                return "표준적인 업무 데이터";
            case SENSITIVE:
                return "주의가 필요한 민감 정보";
            case CONFIDENTIAL:
                return "높은 수준의 보안이 필요한 기밀 정보";
            case RESTRICTED:
                return "최고 수준의 보안이 필요한 극비 정보";
            case PII:
                return "개인정보보호법 적용 대상 개인정보";
            case PHI:
                return "의료법 적용 대상 의료정보";
            case FINANCIAL:
                return "금융관련법 적용 대상 금융정보";
            case REGULATORY:
                return "기타 규제 적용 대상 정보";
            default:
                return "정의되지 않은 민감도 레벨";
        }
    }
    
    /**
     * 보안 요구사항 설명
     */
    public String getSecurityRequirements() {
        StringBuilder sb = new StringBuilder();
        sb.append("사용기간: ").append(defaultUsageDays).append("일");
        
        if (requiresAdditionalApproval) {
            sb.append(", 추가승인 필요");
        }
        
        if (requiresMasking) {
            sb.append(", 마스킹 필수");
        }
        
        if (requiresAuditLog) {
            sb.append(", 감사로그 필수");
        }
        
        return sb.toString();
    }
    
    // ========== 호환성 메서드 (기존 코드 지원) ==========
    
    /**
     * 특별 처리 필요 여부 (기존 코드 호환성)
     */
    public boolean requiresSpecialHandling() {
        return this.level >= 3; // SENSITIVE 이상
    }
    
    /**
     * 극도로 민감한 정보 여부 (기존 코드 호환성)
     */
    public boolean isCriticalSensitive() {
        return this.level >= 5; // RESTRICTED 이상
    }
}