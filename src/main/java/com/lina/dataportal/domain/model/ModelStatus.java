package com.lina.dataportal.domain.model;

public enum ModelStatus {
    TRAINING("훈련중"),
    TESTING("테스트중"),
    DEPLOYED("배포됨"),
    RETIRED("사용중지");
    
    private final String displayName;
    
    ModelStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}