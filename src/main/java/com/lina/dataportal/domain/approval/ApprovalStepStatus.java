package com.lina.dataportal.domain.approval;

/**
 * 승인 단계 상태
 * DB에는 EnumType.STRING으로 저장되며, UI 라벨은 프론트엔드에서 매핑
 */
public enum ApprovalStepStatus {
    /**
     * 승인 대기 중
     */
    PENDING,
    
    /**
     * 승인 완료
     */
    APPROVED,
    
    /**
     * 승인 거절
     */
    REJECTED,
    
    /**
     * 단계 건너뜀 (선택적 승인 단계)
     */
    SKIPPED
}
