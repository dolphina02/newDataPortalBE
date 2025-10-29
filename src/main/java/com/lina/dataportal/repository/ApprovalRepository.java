package com.lina.dataportal.repository;

import com.lina.dataportal.domain.approval.Approval;
import com.lina.dataportal.domain.approval.ApprovalStatus;
import com.lina.dataportal.domain.approval.ApprovalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    
    List<Approval> findByRequester(String requester);
    
    List<Approval> findByStatus(ApprovalStatus status);
    
    List<Approval> findByType(ApprovalType type);
    
    @Query("SELECT a FROM Approval a WHERE a.requester = :requester AND a.status = :status")
    List<Approval> findByRequesterAndStatus(@Param("requester") String requester, 
                                          @Param("status") ApprovalStatus status);
    
    @Query("SELECT a FROM Approval a WHERE a.status = 'PENDING' ORDER BY a.priority DESC, a.requestDate ASC")
    List<Approval> findPendingApprovalsByPriority();
    
    @Query("SELECT a FROM Approval a WHERE a.status IN ('APPROVED', 'REJECTED') ORDER BY a.reviewDate DESC")
    List<Approval> findCompletedApprovals();
}