package com.lina.dataportal.domain.stt;

public enum CallType {
    INBOUND_INQUIRY("인바운드 문의"),
    OUTBOUND_SALES("아웃바운드 영업"),
    CLAIM_CONSULTATION("보험금 상담"),
    POLICY_CONSULTATION("보험 상담"),
    COMPLAINT_HANDLING("불만 처리"),
    RENEWAL_CONSULTATION("갱신 상담"),
    CANCELLATION_REQUEST("해지 요청");
    
    private final String displayName;
    
    CallType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}