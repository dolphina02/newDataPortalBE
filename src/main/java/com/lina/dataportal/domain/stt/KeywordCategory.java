package com.lina.dataportal.domain.stt;

public enum KeywordCategory {
    PRODUCT("상품관련"),
    CLAIM("보험금청구"),
    COMPLAINT("불만사항"),
    COMPLIMENT("칭찬"),
    POLICY("약관관련"),
    PREMIUM("보험료"),
    RENEWAL("갱신"),
    CANCELLATION("해지"),
    COMPETITOR("경쟁사"),
    EMOTION_POSITIVE("긍정감정"),
    EMOTION_NEGATIVE("부정감정");
    
    private final String displayName;
    
    KeywordCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}