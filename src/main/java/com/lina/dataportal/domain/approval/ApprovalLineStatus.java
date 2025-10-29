package com.lina.dataportal.domain.approval;

public enum ApprovalLineStatus {
    PENDING("대기중"),
    APPROVED("승인"),
    REJECTED("거절"),
    SKIPPED("건너뜀");
    
    private final String displayName;
    
    ApprovalLineStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}