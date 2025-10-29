package com.lina.dataportal.domain.stt;

public enum ProcessingStatus {
    PENDING("처리대기"),
    PROCESSING("처리중"),
    COMPLETED("완료"),
    FAILED("실패");
    
    private final String displayName;
    
    ProcessingStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}