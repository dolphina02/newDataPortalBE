package com.lina.dataportal.domain.approval;

/**
 * 승인 대상 오브젝트 타입
 */
public enum TargetType {
    
    // 데이터 관련
    DATASET("데이터셋", "DATASET", "데이터셋 및 테이블"),
    VIEW("뷰", "VIEW", "데이터베이스 뷰"),
    TABLE("테이블", "TABLE", "데이터베이스 테이블"),
    
    // 분석 도구
    DASHBOARD("대시보드", "DASHBOARD", "분석 대시보드"),
    REPORT("리포트", "REPORT", "분석 리포트"),
    NOTEBOOK("노트북", "NOTEBOOK", "Jupyter 노트북"),
    
    // 인프라
    CLUSTER("클러스터", "CLUSTER", "컴퓨팅 클러스터"),
    WORKSPACE("워크스페이스", "WORKSPACE", "작업 공간"),
    
    // 보안 및 정책
    POLICY("정책", "POLICY", "보안 정책"),
    ROLE("역할", "ROLE", "사용자 역할"),
    
    // 서비스
    API("API", "API", "API 엔드포인트"),
    SERVICE("서비스", "SERVICE", "마이크로서비스"),
    MODEL("모델", "MODEL", "ML 모델"),
    PIPELINE("파이프라인", "PIPELINE", "데이터 파이프라인"),
    
    // 파일 시스템
    FILE("파일", "FILE", "개별 파일"),
    FOLDER("폴더", "FOLDER", "디렉토리"),
    
    // 기타
    OTHER("기타", "OTHER", "기타 리소스");
    
    private final String displayName;   // 한글 표시명
    private final String code;          // 영문 코드
    private final String description;   // 설명
    
    TargetType(String displayName, String code, String description) {
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
    
    // ========== 분류 메서드 ==========
    
    /**
     * 데이터 관련 타입 여부
     */
    public boolean isDataType() {
        return this == DATASET || this == VIEW || this == TABLE;
    }
    
    /**
     * 분석 도구 타입 여부
     */
    public boolean isAnalyticsType() {
        return this == DASHBOARD || this == REPORT || this == NOTEBOOK;
    }
    
    /**
     * 인프라 타입 여부
     */
    public boolean isInfrastructureType() {
        return this == CLUSTER || this == WORKSPACE;
    }
    
    /**
     * 보안 관련 타입 여부
     */
    public boolean isSecurityType() {
        return this == POLICY || this == ROLE;
    }
    
    /**
     * 서비스 타입 여부
     */
    public boolean isServiceType() {
        return this == API || this == SERVICE || this == MODEL || this == PIPELINE;
    }
    
    /**
     * 파일 시스템 타입 여부
     */
    public boolean isFileSystemType() {
        return this == FILE || this == FOLDER;
    }
    
    // ========== 카테고리별 조회 메서드 ==========
    
    /**
     * 데이터 관련 타입 목록
     */
    public static TargetType[] getDataTypes() {
        return new TargetType[]{DATASET, VIEW, TABLE};
    }
    
    /**
     * 분석 도구 타입 목록
     */
    public static TargetType[] getAnalyticsTypes() {
        return new TargetType[]{DASHBOARD, REPORT, NOTEBOOK};
    }
    
    /**
     * 인프라 타입 목록
     */
    public static TargetType[] getInfrastructureTypes() {
        return new TargetType[]{CLUSTER, WORKSPACE};
    }
    
    /**
     * 보안 관련 타입 목록
     */
    public static TargetType[] getSecurityTypes() {
        return new TargetType[]{POLICY, ROLE};
    }
    
    /**
     * 서비스 타입 목록
     */
    public static TargetType[] getServiceTypes() {
        return new TargetType[]{API, SERVICE, MODEL, PIPELINE};
    }
    
    // ========== 호환성 메서드 (기존 코드 지원) ==========
    
    /**
     * 인프라 타입 여부 (기존 코드 호환성)
     */
    public boolean isInfraType() {
        return isInfrastructureType();
    }
    
    /**
     * ML 타입 여부 (기존 코드 호환성)
     */
    public boolean isMlType() {
        return this == MODEL;
    }
    
    // ========== ApprovalType 호환성 메서드 ==========
    
    /**
     * 생성 가능한 리소스 타입 여부
     */
    public boolean isCreatable() {
        return this == DASHBOARD || this == REPORT || this == NOTEBOOK || 
               this == DATASET || this == VIEW || this == TABLE ||
               this == MODEL || this == API || this == SERVICE || 
               this == PIPELINE || this == POLICY || this == ROLE ||
               this == FILE || this == FOLDER;
    }
    
    /**
     * 배포 가능한 리소스 타입 여부
     */
    public boolean isDeployable() {
        return this == DASHBOARD || this == REPORT || this == MODEL || 
               this == API || this == SERVICE || this == PIPELINE;
    }
    
    /**
     * 구독 가능한 리소스 타입 여부
     */
    public boolean isSubscribable() {
        return this == DASHBOARD || this == REPORT || this == API || 
               this == SERVICE || this == DATASET;
    }
    
    /**
     * 공유 가능한 리소스 타입 여부
     */
    public boolean isSharable() {
        return this == DASHBOARD || this == REPORT || this == NOTEBOOK ||
               this == DATASET || this == VIEW || this == FILE;
    }
    
    /**
     * 내보내기 가능한 리소스 타입 여부
     */
    public boolean isExportable() {
        return this == DATASET || this == VIEW || this == TABLE ||
               this == REPORT || this == FILE;
    }
    
    /**
     * 실행 가능한 리소스 타입 여부
     */
    public boolean isExecutable() {
        return this == MODEL || this == PIPELINE || this == NOTEBOOK ||
               this == API || this == SERVICE;
    }
    
    /**
     * 특정 ApprovalType과 호환되는지 확인
     */
    public boolean isCompatibleWith(ApprovalType approvalType) {
        return approvalType.isCompatibleWith(this);
    }
    
    /**
     * 이 리소스 타입에 권장되는 ApprovalType 목록
     */
    public ApprovalType[] getRecommendedApprovalTypes() {
        switch (this) {
            case DASHBOARD:
                return new ApprovalType[]{ApprovalType.ACCESS, ApprovalType.CREATE, 
                                        ApprovalType.DEPLOY, ApprovalType.SUBSCRIBE, ApprovalType.SHARE};
            case DATASET:
            case VIEW:
            case TABLE:
                return new ApprovalType[]{ApprovalType.ACCESS, ApprovalType.CREATE, 
                                        ApprovalType.EXPORT, ApprovalType.SHARE};
            case REPORT:
                return new ApprovalType[]{ApprovalType.ACCESS, ApprovalType.CREATE, 
                                        ApprovalType.DEPLOY, ApprovalType.EXPORT, ApprovalType.SHARE};
            case MODEL:
                return new ApprovalType[]{ApprovalType.ACCESS, ApprovalType.CREATE, 
                                        ApprovalType.DEPLOY, ApprovalType.EXECUTE};
            case API:
            case SERVICE:
                return new ApprovalType[]{ApprovalType.ACCESS, ApprovalType.CREATE, 
                                        ApprovalType.DEPLOY, ApprovalType.SUBSCRIBE, ApprovalType.EXECUTE};
            case PIPELINE:
                return new ApprovalType[]{ApprovalType.ACCESS, ApprovalType.CREATE, 
                                        ApprovalType.DEPLOY, ApprovalType.EXECUTE};
            case NOTEBOOK:
                return new ApprovalType[]{ApprovalType.ACCESS, ApprovalType.CREATE, 
                                        ApprovalType.EXECUTE, ApprovalType.SHARE};
            case FILE:
            case FOLDER:
                return new ApprovalType[]{ApprovalType.ACCESS, ApprovalType.CREATE, 
                                        ApprovalType.MODIFY, ApprovalType.DELETE, ApprovalType.EXPORT};
            default:
                return new ApprovalType[]{ApprovalType.ACCESS, ApprovalType.MANAGE};
        }
    }
}