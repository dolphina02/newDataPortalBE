package com.lina.dataportal.domain.model;

public enum ModelType {
    FRAUD_DETECTION("보험금 청구 사기 탐지"),
    CHURN_PREDICTION("고객 이탈 예측"),
    RISK_SCORING("리스크 점수 산출"),
    UNDERWRITING("언더라이팅 자동화"),
    CUSTOMER_SEGMENTATION("고객 세그먼테이션");
    
    private final String displayName;
    
    ModelType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}