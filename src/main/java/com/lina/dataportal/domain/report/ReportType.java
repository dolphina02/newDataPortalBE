package com.lina.dataportal.domain.report;

public enum ReportType {
    CUSTOMER_ANALYSIS("고객 분석"),
    KPI_REPORT("KPI 리포트"),
    MONITORING("모니터링"),
    MONTHLY_REPORT("월간 리포트");
    
    private final String displayName;
    
    ReportType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}