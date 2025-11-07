package com.lina.dataportal.repository;

import com.lina.dataportal.domain.approval.ApprovalStep;
import com.lina.dataportal.domain.approval.ApprovalLineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, Long> {
    
    /**
     * 특정 승인 요청의 모든 승인 단계를 순서대로 조회
     */
    List<ApprovalStep> findByApprovalIdOrderByStepOrder(Long approvalId);
    
    /**
     * 특정 승인자의 특정 상태 승인 단계들 조회 - User ID 기반
     */
    List<ApprovalStep> findByApproverIdAndStatus(Long approverId, ApprovalLineStatus status);
    
    /**
     * 특정 승인자의 미결 승인 단계들을 생성일시 순으로 조회 - User ID 기반
     */
    @Query("SELECT as FROM ApprovalStep as WHERE as.approverId = :approverId AND as.status = 'PENDING' ORDER BY as.createdAt ASC")
    List<ApprovalStep> findPendingApprovalsByApproverId(@Param("approverId") Long approverId);
    
    // 하위 호환성을 위한 이메일 기반 메서드들 (Deprecated)
    @Deprecated
    @Query("SELECT as FROM ApprovalStep as WHERE as.approverEmail = :approverEmail AND as.status = :status")
    List<ApprovalStep> findByApproverEmailAndStatus(@Param("approverEmail") String approverEmail, @Param("status") ApprovalLineStatus status);
    
    @Deprecated
    @Query("SELECT as FROM ApprovalStep as WHERE as.approverEmail = :approverEmail AND as.status = 'PENDING' ORDER BY as.createdAt ASC")
    List<ApprovalStep> findPendingApprovalsByApproverEmail(@Param("approverEmail") String approverEmail);
    
    /**
     * 특정 승인 요청의 특정 단계 조회
     */
    @Query("SELECT as FROM ApprovalStep as WHERE as.approvalId = :approvalId AND as.stepOrder = :stepOrder")
    Optional<ApprovalStep> findByApprovalIdAndStepOrder(@Param("approvalId") Long approvalId, @Param("stepOrder") Integer stepOrder);
    
    /**
     * 특정 승인 요청의 미결 승인 단계들을 단계 순으로 조회
     */
    @Query("SELECT as FROM ApprovalStep as WHERE as.approvalId = :approvalId AND as.status = 'PENDING' ORDER BY as.stepOrder ASC")
    List<ApprovalStep> findPendingStepsByApprovalId(@Param("approvalId") Long approvalId);
    
    /**
     * 특정 승인 요청의 다음 미결 승인 단계 조회 (가장 낮은 단계 번호)
     */
    @Query("SELECT as FROM ApprovalStep as WHERE as.approvalId = :approvalId AND as.stepOrder = (SELECT MIN(as2.stepOrder) FROM ApprovalStep as2 WHERE as2.approvalId = :approvalId AND as2.status = 'PENDING')")
    Optional<ApprovalStep> findNextPendingApprovalStep(@Param("approvalId") Long approvalId);
    
    /**
     * 특정 승인 요청의 승인된 단계 수 조회
     */
    @Query("SELECT COUNT(as) FROM ApprovalStep as WHERE as.approvalId = :approvalId AND as.status = 'APPROVED'")
    Long countApprovedStepsByApprovalId(@Param("approvalId") Long approvalId);
    
    /**
     * 특정 승인 요청의 전체 단계 수 조회
     */
    @Query("SELECT COUNT(as) FROM ApprovalStep as WHERE as.approvalId = :approvalId")
    Long countTotalStepsByApprovalId(@Param("approvalId") Long approvalId);
    
    /**
     * 특정 승인 요청의 필수 단계 중 승인된 단계 수 조회
     */
    @Query("SELECT COUNT(as) FROM ApprovalStep as WHERE as.approvalId = :approvalId AND as.isRequired = true AND as.status = 'APPROVED'")
    Long countApprovedRequiredStepsByApprovalId(@Param("approvalId") Long approvalId);
    
    /**
     * 특정 승인 요청의 전체 필수 단계 수 조회
     */
    @Query("SELECT COUNT(as) FROM ApprovalStep as WHERE as.approvalId = :approvalId AND as.isRequired = true")
    Long countTotalRequiredStepsByApprovalId(@Param("approvalId") Long approvalId);
    
    /**
     * 특정 템플릿 버전을 사용하는 승인 단계들 조회
     */
    List<ApprovalStep> findByTemplateIdAndTemplateVersion(Long templateId, Long templateVersion);
    
    /**
     * 특정 승인 요청에서 반려된 단계가 있는지 확인
     */
    @Query("SELECT COUNT(as) > 0 FROM ApprovalStep as WHERE as.approvalId = :approvalId AND as.status = 'REJECTED'")
    boolean existsRejectedStepByApprovalId(@Param("approvalId") Long approvalId);
    
    /**
     * 특정 승인 요청의 단계 순서 이후의 미결 단계들 조회
     */
    @Query("SELECT as FROM ApprovalStep as WHERE as.approvalId = :approvalId AND as.stepOrder > :stepOrder AND as.status = 'PENDING' ORDER BY as.stepOrder ASC")
    List<ApprovalStep> findPendingStepsByApprovalIdAndStepOrderGreaterThan(@Param("approvalId") Long approvalId, @Param("stepOrder") Integer stepOrder);
}