package com.lina.dataportal.repository;

import com.lina.dataportal.domain.approval.ApprovalLineTemplate;
import com.lina.dataportal.domain.approval.ApprovalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalLineTemplateRepository extends JpaRepository<ApprovalLineTemplate, Long> {
    
    /**
     * 특정 승인 타입의 활성화된 템플릿들을 단계 순서대로 조회
     */
    List<ApprovalLineTemplate> findByApprovalTypeAndIsActiveTrueOrderByStepOrder(ApprovalType approvalType);
    
    /**
     * 특정 승인 타입의 모든 템플릿들을 단계 순서대로 조회
     */
    List<ApprovalLineTemplate> findByApprovalTypeOrderByStepOrder(ApprovalType approvalType);
    
    /**
     * 특정 승인 타입과 단계의 템플릿 조회
     */
    ApprovalLineTemplate findByApprovalTypeAndStepOrderAndIsActiveTrue(ApprovalType approvalType, Integer stepOrder);
    
    /**
     * 특정 역할과 부서의 템플릿들 조회
     */
    List<ApprovalLineTemplate> findByApproverRoleAndApproverDepartmentAndIsActiveTrue(String approverRole, String approverDepartment);
    
    /**
     * 승인 타입별 최대 단계 수 조회
     */
    @Query("SELECT MAX(alt.stepOrder) FROM ApprovalLineTemplate alt WHERE alt.approvalType = :approvalType AND alt.isActive = true")
    Integer findMaxStepOrderByApprovalType(@Param("approvalType") ApprovalType approvalType);
    
    /**
     * 필수 승인 단계만 조회
     */
    List<ApprovalLineTemplate> findByApprovalTypeAndIsRequiredTrueAndIsActiveTrueOrderByStepOrder(ApprovalType approvalType);
}