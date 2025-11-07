package com.lina.dataportal.domain.dashboard;

/**
 * 대시보드 타입
 * DB에는 EnumType.STRING으로 저장되며, UI 라벨은 프론트엔드에서 매핑
 */
public enum DashboardType {
    /**
     * 템플릿 대시보드 (미리 정의된 표준 대시보드)
     */
    TEMPLATE,
    
    /**
     * 사용자 정의 대시보드
     */
    CUSTOM
}