package com.lina.dataportal.domain.dashboard;

public enum DashboardType {
    TEMPLATE("template"),
    CUSTOM("custom");
    
    private final String value;
    
    DashboardType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}