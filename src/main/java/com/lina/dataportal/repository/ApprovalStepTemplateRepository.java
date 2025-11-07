package com.lina.dataportal.repository;

import com.lina.dataportal.domain.approval.ApprovalStepTemplate;
import com.lina.dataportal.domain.approval.ApprovalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalStepTemplateRepository extends JpaRepository<ApprovalStepTemplate, Long> {
    
    /**
     * 특정 승인 타입의 활성화된 템플릿들을 단계 순서대로 조회
     */
    List<ApprovalStepTemplate> findByApprovalTypeAndIsActiveTrueOrderByStepOrder(ApprovalType approvalType);
    
    /**
     * 특정 승인 타입의 모든 템플릿들을 단계 순서대로 조회
     */
    List<ApprovalStepTemplate> findByApprovalTypeOrderByStepOrder(ApprovalType approvalType);
    
    /**
     * 특정 승인 타입과 단계의 템플릿 조회
     */
    ApprovalStepTemplate findByApprovalTypeAndStepOrderAndIsActiveTrue(ApprovalType approvalType, Integer stepOrder);
    
    /**
     * 특정 역할과 부서의 템플릿들 조회
     */
    List<ApprovalStepTemplate> findByApproverRoleAndApproverDepartmentAndIsActiveTrue(String approverRole, String approverDepartment);
    
    /**
     * 승인 타입별 최대 단계 수 조회
     */
    @Query("SELECT MAX(ast.stepOrder) FROM ApprovalStepTemplate ast WHERE ast.approvalType = :approvalType AND ast.isActive = true")
    Integer findMaxStepOrderByApprovalType(@Param("approvalType") ApprovalType approvalType);
    
    /**
     * 필수 승인 단계만 조회
     */
    List<ApprovalStepTemplate> findByApprovalTypeAndIsRequiredTrueAndIsActiveTrueOrderByStepOrder(ApprovalType approvalType);
}
