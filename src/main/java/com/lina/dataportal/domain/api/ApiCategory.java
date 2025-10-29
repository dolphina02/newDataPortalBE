package com.lina.dataportal.domain.api;

public enum ApiCategory {
    CUSTOMER_MANAGEMENT("고객 관리 API"),
    INSURANCE_PRODUCT("보험 상품 API"),
    CLAIM_PROCESSING("청구 처리 API"),
    UNDERWRITING("언더라이팅 API");
    
    private final String displayName;
    
    ApiCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}