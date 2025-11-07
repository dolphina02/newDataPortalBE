package com.lina.dataportal.domain.approval;

/**
 * 승인 요청 상태
 * DB에는 EnumType.STRING으로 저장되며, UI 라벨은 프론트엔드에서 매핑
 */
public enum ApprovalStatus {
    /**
     * 승인 대기 중
     */
    PENDING,
    
    /**
     * 승인 진행 중
     */
    IN_PROGRESS,
    
    /**
     * 승인 완료
     */
    APPROVED,
    
    /**
     * 승인 거절
     */
    REJECTED,
    
    /**
     * 승인 취소
     */
    CANCELLED,
    
    /**
     * 승인 만료
     */
    EXPIRED
}