package com.lina.dataportal.repository;

import com.lina.dataportal.domain.approval.ApprovalLine;
import com.lina.dataportal.domain.approval.ApprovalLineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalLineRepository extends JpaRepository<ApprovalLine, Long> {
    
    List<ApprovalLine> findByApprovalIdOrderByStepOrder(Long approvalId);
    
    List<ApprovalLine> findByApproverIdAndStatus(String approverId, ApprovalLineStatus status);
    
    @Query("SELECT al FROM ApprovalLine al WHERE al.approverId = :approverId AND al.status = 'PENDING' ORDER BY al.createdAt ASC")
    List<ApprovalLine> findPendingApprovalsByApproverId(@Param("approverId") String approverId);
    
    @Query("SELECT al FROM ApprovalLine al WHERE al.approvalId = :approvalId AND al.stepOrder = :stepOrder")
    Optional<ApprovalLine> findByApprovalIdAndStepOrder(@Param("approvalId") Long approvalId, @Param("stepOrder") Integer stepOrder);
    
    @Query("SELECT al FROM ApprovalLine al WHERE al.approvalId = :approvalId AND al.status = 'PENDING' ORDER BY al.stepOrder ASC")
    List<ApprovalLine> findPendingLinesByApprovalId(@Param("approvalId") Long approvalId);
    
    @Query("SELECT al FROM ApprovalLine al WHERE al.approvalId = :approvalId AND al.stepOrder = (SELECT MIN(al2.stepOrder) FROM ApprovalLine al2 WHERE al2.approvalId = :approvalId AND al2.status = 'PENDING')")
    Optional<ApprovalLine> findNextPendingApprovalLine(@Param("approvalId") Long approvalId);
    
    @Query("SELECT COUNT(al) FROM ApprovalLine al WHERE al.approvalId = :approvalId AND al.status = 'APPROVED'")
    Long countApprovedLinesByApprovalId(@Param("approvalId") Long approvalId);
    
    @Query("SELECT COUNT(al) FROM ApprovalLine al WHERE al.approvalId = :approvalId")
    Long countTotalLinesByApprovalId(@Param("approvalId") Long approvalId);
}