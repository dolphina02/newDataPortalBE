package com.lina.dataportal.domain.approval;

public enum ApprovalType {
    DEPLOY("deploy"),
    DASHBOARD("dashboard"), 
    DATA("data");
    
    private final String value;
    
    ApprovalType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}