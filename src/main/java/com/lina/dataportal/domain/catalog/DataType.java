package com.lina.dataportal.domain.catalog;

public enum DataType {
    DATASET("Dataset"),
    DASHBOARD("Dashboard"),
    REPORT("Report"),
    TABLE("Table");
    
    private final String displayName;
    
    DataType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}