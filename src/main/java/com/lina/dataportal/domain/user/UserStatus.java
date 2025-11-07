package com.lina.dataportal.domain.user;

/**
 * 사용자 상태
 * DB에는 EnumType.STRING으로 저장되며, UI 라벨은 프론트엔드에서 매핑
 */
public enum UserStatus {
    /**
     * 활성 사용자
     */
    ACTIVE,
    
    /**
     * 비활성 사용자 (퇴사, 휴직 등)
     */
    INACTIVE,
    
    /**
     * 일시 정지된 사용자
     */
    SUSPENDED,
    
    /**
     * 대기 중인 사용자 (신규 가입 승인 대기)
     */
    PENDING
}