package com.lina.dataportal.domain.stt;

public enum Speaker {
    AGENT("상담사"),
    CUSTOMER("고객");
    
    private final String displayName;
    
    Speaker(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}