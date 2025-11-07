package com.lina.dataportal.domain.report;

/**
 * 보고서 타입
 * DB에는 EnumType.STRING으로 저장되며, UI 라벨은 프론트엔드에서 매핑
 */
public enum ReportType {
    /**
     * PDF 보고서
     */
    PDF,
    
    /**
     * Excel 보고서
     */
    EXCEL,
    
    /**
     * PowerPoint 보고서
     */
    POWERPOINT,
    
    /**
     * 고객 분석 보고서
     */
    CUSTOMER_ANALYSIS,
    
    /**
     * KPI 보고서
     */
    KPI_REPORT,
    
    /**
     * 모니터링 보고서
     */
    MONITORING,
    
    /**
     * 월간 보고서
     */
    MONTHLY_REPORT
}