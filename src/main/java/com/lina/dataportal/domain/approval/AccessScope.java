package com.lina.dataportal.domain.approval;

/**
 * 접근 범위 (권한 레벨)
 * 승인된 후 어떤 수준의 접근이 허용되는지 정의
 */
public enum AccessScope {
    
    // 기본 접근 권한
    READ("읽기", "데이터 조회 및 읽기 권한", 1),
    WRITE("쓰기", "데이터 수정 및 쓰기 권한", 2),
    DELETE("삭제", "데이터 삭제 권한", 3),
    
    // 고급 접근 권한
    EXECUTE("실행", "스크립트, 쿼리 실행 권한", 2),
    DEPLOY("배포", "시스템 배포 권한", 4),
    
    // 데이터 공유 권한
    SHARE("공유", "다른 사용자와 공유 권한", 3),
    EXPORT("내보내기", "데이터 내보내기 권한", 3),
    DOWNLOAD("다운로드", "파일 다운로드 권한", 2),
    
    // 관리 권한
    ADMIN("관리", "전체 관리 권한", 5),
    OWNER("소유", "소유자 권한", 5),
    
    // 특수 권한
    VIEW_ONLY("보기전용", "읽기 전용 (복사/다운로드 불가)", 1),
    MASKED_READ("마스킹읽기", "민감정보 마스킹된 읽기 권한", 1),
    
    // 시간 제한 권한
    TEMPORARY_READ("임시읽기", "시간 제한된 읽기 권한", 1),
    TEMPORARY_WRITE("임시쓰기", "시간 제한된 쓰기 권한", 2);
    
    private final String displayName;
    private final String description;
    private final int riskLevel; // 1(낮음) ~ 5(높음)
    
    AccessScope(String displayName, String description, int riskLevel) {
        this.displayName = displayName;
        this.description = description;
        this.riskLevel = riskLevel;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getRiskLevel() {
        return riskLevel;
    }
    
    /**
     * 읽기 권한 포함 여부
     */
    public boolean includesRead() {
        return this != WRITE && this != DELETE && this != DEPLOY;
    }
    
    /**
     * 쓰기 권한 포함 여부
     */
    public boolean includesWrite() {
        return this == WRITE || this == DELETE || this == ADMIN || this == OWNER || this == TEMPORARY_WRITE;
    }
    
    /**
     * 고위험 권한 여부
     */
    public boolean isHighRisk() {
        return riskLevel >= 4;
    }
    
    /**
     * 임시 권한 여부
     */
    public boolean isTemporary() {
        return this == TEMPORARY_READ || this == TEMPORARY_WRITE;
    }
    
    /**
     * 마스킹 필요 여부
     */
    public boolean requiresMasking() {
        return this == MASKED_READ || this == VIEW_ONLY;
    }
}