package com.lina.dataportal.domain.stt;

public enum CustomerType {
    PROSPECT("잠재고객"),
    NEW_CUSTOMER("신규고객"),
    EXISTING_CUSTOMER("기존고객"),
    VIP_CUSTOMER("VIP고객"),
    CORPORATE_CUSTOMER("법인고객");
    
    private final String displayName;
    
    CustomerType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}