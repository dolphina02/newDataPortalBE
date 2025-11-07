package com.lina.dataportal.domain.approval;

/**
 * 승인 요청 우선순위
 * DB에는 EnumType.STRING으로 저장되며, UI 라벨은 프론트엔드에서 매핑
 */
public enum Priority {
    /**
     * 긴급 (24시간 이내 처리)
     */
    URGENT,
    
    /**
     * 높음 (3일 이내 처리)
     */
    HIGH,
    
    /**
     * 보통 (1주일 이내 처리)
     */
    MEDIUM,
    
    /**
     * 낮음 (2주일 이내 처리)
     */
    LOW
}