package com.lina.dataportal.repository;

import com.lina.dataportal.domain.approval.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    
    // User ID 기반 메서드들
    List<Approval> findByRequesterId(Long requesterId);
    
    List<Approval> findByStatus(ApprovalStatus status);
    
    List<Approval> findByType(ApprovalType type);
    
    @Query("SELECT a FROM Approval a WHERE a.requesterId = :requesterId AND a.status = :status")
    List<Approval> findByRequesterIdAndStatus(@Param("requesterId") Long requesterId, 
                                            @Param("status") ApprovalStatus status);
    
    // 하위 호환성을 위한 이메일 기반 메서드들 (Deprecated)
    @Deprecated
    @Query("SELECT a FROM Approval a WHERE a.requesterEmail = :requesterEmail")
    List<Approval> findByRequester(@Param("requesterEmail") String requesterEmail);
    
    @Deprecated
    @Query("SELECT a FROM Approval a WHERE a.requesterEmail = :requesterEmail AND a.status = :status")
    List<Approval> findByRequesterAndStatus(@Param("requesterEmail") String requesterEmail, 
                                          @Param("status") ApprovalStatus status);
    
    @Query("SELECT a FROM Approval a WHERE a.status = 'PENDING' ORDER BY a.priority DESC, a.requestDate ASC")
    List<Approval> findPendingApprovalsByPriority();
    
    @Query("SELECT a FROM Approval a WHERE a.status IN ('APPROVED', 'REJECTED') ORDER BY a.reviewDate DESC")
    List<Approval> findCompletedApprovals();
    
    /**
     * ApprovalStep과 함께 Approval 조회 (계산 필드 사용을 위해)
     */
    @Query("SELECT a FROM Approval a LEFT JOIN FETCH a.approvalSteps WHERE a.id = :id")
    Approval findByIdWithSteps(@Param("id") Long id);
    
    /**
     * ApprovalStep과 함께 모든 Approval 조회
     */
    @Query("SELECT DISTINCT a FROM Approval a LEFT JOIN FETCH a.approvalSteps ORDER BY a.requestDate DESC")
    List<Approval> findAllWithSteps();
    
    /**
     * 특정 상태의 Approval을 ApprovalStep과 함께 조회
     */
    @Query("SELECT DISTINCT a FROM Approval a LEFT JOIN FETCH a.approvalSteps WHERE a.status = :status ORDER BY a.requestDate DESC")
    List<Approval> findByStatusWithSteps(@Param("status") ApprovalStatus status);
    
    // ========== 승인 대상 오브젝트 기반 조회 메서드들 ==========
    
    /**
     * 특정 대상 타입의 승인 요청들 조회
     */
    List<Approval> findByTargetType(TargetType targetType);
    
    /**
     * 특정 대상 오브젝트의 승인 요청들 조회
     */
    List<Approval> findByTargetTypeAndTargetId(TargetType targetType, String targetId);
    
    /**
     * 민감도 레벨별 승인 요청들 조회
     */
    List<Approval> findBySensitivityLevel(SensitivityLevel sensitivityLevel);
    
    /**
     * 고위험 승인 요청들 조회 (고민감도 또는 고위험 접근)
     */
    @Query("SELECT a FROM Approval a WHERE a.sensitivityLevel IN ('SENSITIVE', 'CONFIDENTIAL', 'STRICT', 'PII', 'PHI', 'FINANCIAL') OR a.accessScope IN ('DEPLOY', 'ADMIN', 'OWNER') ORDER BY a.createdAt DESC")
    List<Approval> findHighRiskApprovals();
    
    /**
     * 만료 예정 접근 권한 조회
     */
    @Query("SELECT a FROM Approval a WHERE a.status = 'APPROVED' AND a.accessExpiresAt IS NOT NULL AND a.accessExpiresAt <= :expiryThreshold ORDER BY a.accessExpiresAt ASC")
    List<Approval> findExpiringAccess(@Param("expiryThreshold") LocalDateTime expiryThreshold);
    
    /**
     * 만료된 접근 권한 조회
     */
    @Query("SELECT a FROM Approval a WHERE a.status = 'APPROVED' AND a.accessExpiresAt IS NOT NULL AND a.accessExpiresAt < :now")
    List<Approval> findExpiredAccess(@Param("now") LocalDateTime now);
    
    /**
     * 특정 사용자의 특정 대상에 대한 활성 승인 조회
     */
    @Query("SELECT a FROM Approval a WHERE a.requesterId = :userId AND a.targetType = :targetType AND a.targetId = :targetId AND a.status = 'APPROVED' AND (a.accessExpiresAt IS NULL OR a.accessExpiresAt > CURRENT_TIMESTAMP) ORDER BY a.createdAt DESC")
    Optional<Approval> findActiveApprovalForUserAndTarget(@Param("userId") Long userId, @Param("targetType") TargetType targetType, @Param("targetId") String targetId);
    
    /**
     * 특정 접근 범위의 승인 요청들 조회
     */
    List<Approval> findByAccessScope(AccessScope accessScope);
    
    /**
     * 특정 정책 세트가 적용된 승인 요청들 조회
     */
    List<Approval> findByPolicySetId(Long policySetId);
    
    /**
     * 특정 마스킹 정책이 적용된 승인 요청들 조회
     */
    List<Approval> findByMaskingPolicyId(Long maskingPolicyId);
    
    /**
     * 감사 로그가 필요한 승인 요청들 조회
     */
    @Query("SELECT a FROM Approval a WHERE a.requiresAuditLog = true OR a.sensitivityLevel IN ('SENSITIVE', 'CONFIDENTIAL', 'STRICT', 'PII', 'PHI', 'FINANCIAL', 'REGULATORY') ORDER BY a.createdAt DESC")
    List<Approval> findRequiringAuditLog();
    
    /**
     * 마스킹이 필요한 승인 요청들 조회
     */
    @Query("SELECT a FROM Approval a WHERE a.requiresMasking = true OR a.accessScope IN ('MASKED_READ', 'VIEW_ONLY') ORDER BY a.createdAt DESC")
    List<Approval> findRequiringMasking();
    
    /**
     * 특정 대상 타입과 민감도 레벨 조합으로 조회
     */
    List<Approval> findByTargetTypeAndSensitivityLevel(TargetType targetType, SensitivityLevel sensitivityLevel);
    
    /**
     * 특정 기간 내 생성된 승인 요청들 조회
     */
    @Query("SELECT a FROM Approval a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    List<Approval> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * 업무 정당성이 포함된 승인 요청들 조회
     */
    @Query("SELECT a FROM Approval a WHERE a.businessJustification IS NOT NULL AND a.businessJustification != '' ORDER BY a.createdAt DESC")
    List<Approval> findWithBusinessJustification();
    
    /**
     * 만료 예정 사용 권한 조회
     */
    @Query("SELECT a FROM Approval a WHERE a.status = 'APPROVED' AND a.usageExpiresAt IS NOT NULL AND a.usageExpiresAt <= :expiryThreshold ORDER BY a.usageExpiresAt ASC")
    List<Approval> findExpiringUsage(@Param("expiryThreshold") LocalDateTime expiryThreshold);
    
    /**
     * 만료된 사용 권한 조회
     */
    @Query("SELECT a FROM Approval a WHERE a.status = 'APPROVED' AND a.usageExpiresAt IS NOT NULL AND a.usageExpiresAt < :now")
    List<Approval> findExpiredUsage(@Param("now") LocalDateTime now);
    
    /**
     * 민감도 레벨별 승인 요청 개수 조회
     */
    Long countBySensitivityLevel(SensitivityLevel sensitivityLevel);
    
    /**
     * 민감도 카테고리별 승인 요청들 조회 (NORMAL 카테고리)
     */
    @Query("SELECT a FROM Approval a WHERE a.sensitivityLevel IN ('PUBLIC', 'INTERNAL', 'NORMAL') ORDER BY a.createdAt DESC")
    List<Approval> findNormalSensitivityApprovals();
    
    /**
     * 민감도 카테고리별 승인 요청들 조회 (SENSITIVE 카테고리)
     */
    @Query("SELECT a FROM Approval a WHERE a.sensitivityLevel IN ('SENSITIVE', 'CONFIDENTIAL', 'RESTRICTED', 'PII', 'PHI', 'FINANCIAL', 'REGULATORY') ORDER BY a.createdAt DESC")
    List<Approval> findSensitiveApprovals();
    
    /**
     * 개인정보 관련 승인 요청들 조회
     */
    @Query("SELECT a FROM Approval a WHERE a.sensitivityLevel IN ('PII', 'PHI') ORDER BY a.createdAt DESC")
    List<Approval> findPersonalDataApprovals();
    
    /**
     * 규제 대상 승인 요청들 조회
     */
    @Query("SELECT a FROM Approval a WHERE a.sensitivityLevel IN ('PII', 'PHI', 'FINANCIAL', 'REGULATORY') ORDER BY a.createdAt DESC")
    List<Approval> findRegulatedApprovals();
}