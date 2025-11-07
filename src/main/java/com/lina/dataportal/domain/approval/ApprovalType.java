package com.lina.dataportal.domain.approval;

/**
 * 승인 액션 타입 (리소스에 대한 행위)
 * TargetType과 조합하여 "무엇에 대해 어떤 행위를 할 것인가"를 정의
 * 
 * 예시: ACCESS + DASHBOARD = 대시보드 접근 권한
 *      DEPLOY + DASHBOARD = 대시보드 배포 승인
 *      CREATE + DATASET = 데이터셋 생성 승인
 */
public enum ApprovalType {
    
    // ========== 기본 액션 ==========
    
    /**
     * 접근/조회 권한 (가장 기본적인 권한)
     * 대상: 모든 리소스 타입
     */
    ACCESS("접근", "리소스에 대한 접근/조회 권한"),
    
    /**
     * 생성 권한
     * 대상: DASHBOARD, REPORT, DATASET, MODEL, API 등
     */
    CREATE("생성", "새로운 리소스 생성 권한"),
    
    /**
     * 수정 권한
     * 대상: 모든 리소스 타입
     */
    MODIFY("수정", "기존 리소스 수정 권한"),
    
    /**
     * 삭제 권한
     * 대상: 모든 리소스 타입
     */
    DELETE("삭제", "리소스 삭제 권한"),
    
    /**
     * 배포/발행 권한 (프로덕션 환경 반영)
     * 대상: DASHBOARD, REPORT, MODEL, API 등
     */
    DEPLOY("배포", "프로덕션 환경으로 배포/발행 권한"),
    
    /**
     * 구독/사용 권한 (지속적 사용)
     * 대상: DASHBOARD, REPORT, API, SERVICE 등
     */
    SUBSCRIBE("구독", "리소스에 대한 구독/지속적 사용 권한"),
    
    // ========== 고급 액션 ==========
    
    /**
     * 관리 권한 (설정 변경, 권한 부여 등)
     * 대상: 모든 리소스 타입
     */
    MANAGE("관리", "리소스 관리 및 설정 변경 권한"),
    
    /**
     * 공유 권한 (다른 사용자와 공유)
     * 대상: DASHBOARD, REPORT, DATASET 등
     */
    SHARE("공유", "다른 사용자와 리소스 공유 권한"),
    
    /**
     * 다운로드/내보내기 권한
     * 대상: DATASET, REPORT, FILE 등
     */
    EXPORT("내보내기", "리소스 다운로드/내보내기 권한"),
    
    /**
     * 실행 권한 (쿼리, 모델 실행 등)
     * 대상: MODEL, PIPELINE, NOTEBOOK 등
     */
    EXECUTE("실행", "리소스 실행 권한");
    
    private final String displayName;   // 한글 표시명
    private final String description;   // 설명
    
    ApprovalType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    // ========== Getter 메서드 ==========
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    // ========== 액션 분류 메서드 ==========
    
    /**
     * 읽기 전용 액션 여부
     */
    public boolean isReadOnlyAction() {
        return this == ACCESS || this == SUBSCRIBE;
    }
    
    /**
     * 쓰기 액션 여부
     */
    public boolean isWriteAction() {
        return this == CREATE || this == MODIFY || this == DELETE;
    }
    
    /**
     * 고위험 액션 여부 (프로덕션 영향)
     */
    public boolean isHighRiskAction() {
        return this == DEPLOY || this == DELETE || this == MANAGE;
    }
    
    /**
     * 데이터 유출 위험 액션 여부
     */
    public boolean isDataExposureRisk() {
        return this == ACCESS || this == EXPORT || this == SHARE;
    }
    
    // ========== 호환성 메서드 (기존 코드 지원) ==========
    
    /**
     * 기존 ApprovalType 값들과의 호환성을 위한 매핑
     */
    public static ApprovalType fromLegacyType(String legacyType) {
        switch (legacyType) {
            case "DASHBOARD_DEPLOY": return DEPLOY;
            case "DASHBOARD_SUBSCRIBE": return SUBSCRIBE;
            case "DASHBOARD_CREATE": return CREATE;
            case "DATA_ACCESS": return ACCESS;
            case "DATA_ACCESS_SENSITIVE": return ACCESS;
            case "REPORT_PUBLISH": return DEPLOY;
            case "DASHBOARD": return ACCESS;  // 기본값
            case "DATA": return ACCESS;       // 기본값
            default: return ACCESS;
        }
    }
    
    /**
     * 특정 TargetType과 조합 가능한지 확인
     */
    public boolean isCompatibleWith(TargetType targetType) {
        switch (this) {
            case ACCESS:
            case MODIFY:
            case DELETE:
            case MANAGE:
                return true; // 모든 리소스에 적용 가능
                
            case CREATE:
                return targetType.isCreatable();
                
            case DEPLOY:
                return targetType.isDeployable();
                
            case SUBSCRIBE:
                return targetType.isSubscribable();
                
            case SHARE:
                return targetType.isSharable();
                
            case EXPORT:
                return targetType.isExportable();
                
            case EXECUTE:
                return targetType.isExecutable();
                
            default:
                return false;
        }
    }
}