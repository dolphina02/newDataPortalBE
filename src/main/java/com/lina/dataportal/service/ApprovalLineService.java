package com.lina.dataportal.service;

import com.lina.dataportal.domain.approval.ApprovalLine;
import com.lina.dataportal.domain.approval.ApprovalLineStatus;
import com.lina.dataportal.repository.ApprovalLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ApprovalLineService {
    
    private final ApprovalLineRepository approvalLineRepository;
    
    @Autowired
    public ApprovalLineService(ApprovalLineRepository approvalLineRepository) {
        this.approvalLineRepository = approvalLineRepository;
    }
    
    public List<ApprovalLine> getApprovalLines(Long approvalId) {
        return approvalLineRepository.findByApprovalIdOrderByStepOrder(approvalId);
    }
    
    public List<ApprovalLine> getPendingApprovalsByApproverId(String approverId) {
        return approvalLineRepository.findPendingApprovalsByApproverId(approverId);
    }
    
    public Optional<ApprovalLine> getNextPendingApprovalLine(Long approvalId) {
        return approvalLineRepository.findNextPendingApprovalLine(approvalId);
    }
    
    public ApprovalLine createApprovalLine(Long approvalId, Integer stepOrder, String approverId, String approverName) {
        ApprovalLine approvalLine = new ApprovalLine(approvalId, 1L, stepOrder, approverId, approverName);
        return approvalLineRepository.save(approvalLine);
    }
    
    public ApprovalLine createApprovalLine(Long approvalId, Integer stepOrder, String approverId, String approverName, 
                                         String approverRole, String approverDepartment) {
        ApprovalLine approvalLine = new ApprovalLine(approvalId, 1L, stepOrder, approverId, approverName);
        approvalLine.setApproverRole(approverRole);
        approvalLine.setApproverDepartment(approverDepartment);
        return approvalLineRepository.save(approvalLine);
    }
    
    public ApprovalLine approveStep(Long approvalLineId, String comment) {
        ApprovalLine approvalLine = approvalLineRepository.findById(approvalLineId)
            .orElseThrow(() -> new RuntimeException("Approval line not found with id: " + approvalLineId));
        
        approvalLine.approve(comment);
        return approvalLineRepository.save(approvalLine);
    }
    
    public ApprovalLine rejectStep(Long approvalLineId, String comment) {
        ApprovalLine approvalLine = approvalLineRepository.findById(approvalLineId)
            .orElseThrow(() -> new RuntimeException("Approval line not found with id: " + approvalLineId));
        
        approvalLine.reject(comment);
        return approvalLineRepository.save(approvalLine);
    }
    
    public ApprovalLine skipStep(Long approvalLineId, String comment) {
        ApprovalLine approvalLine = approvalLineRepository.findById(approvalLineId)
            .orElseThrow(() -> new RuntimeException("Approval line not found with id: " + approvalLineId));
        
        approvalLine.skip(comment);
        return approvalLineRepository.save(approvalLine);
    }
    
    public boolean isApprovalCompleted(Long approvalId) {
        Long approvedCount = approvalLineRepository.countApprovedLinesByApprovalId(approvalId);
        Long totalCount = approvalLineRepository.countTotalLinesByApprovalId(approvalId);
        return approvedCount.equals(totalCount);
    }
    
    public boolean hasRejectedStep(Long approvalId) {
        List<ApprovalLine> lines = approvalLineRepository.findByApprovalIdOrderByStepOrder(approvalId);
        return lines.stream().anyMatch(line -> line.getStatus() == ApprovalLineStatus.REJECTED);
    }
    
    public double getApprovalProgress(Long approvalId) {
        Long approvedCount = approvalLineRepository.countApprovedLinesByApprovalId(approvalId);
        Long totalCount = approvalLineRepository.countTotalLinesByApprovalId(approvalId);
        
        if (totalCount == 0) return 0.0;
        return (double) approvedCount / totalCount * 100.0;
    }
    
    public List<ApprovalLine> createStandardApprovalLine(Long approvalId, String requesterDepartment) {
        // 표준 승인 라인 생성 (부서별 다른 승인 라인 적용 가능)
        List<ApprovalLine> lines = List.of(
            createApprovalLine(approvalId, 1, "team_leader", "팀장", "팀장", requesterDepartment),
            createApprovalLine(approvalId, 2, "dept_manager", "부서장", "부서장", requesterDepartment),
            createApprovalLine(approvalId, 3, "cto", "CTO", "최고기술책임자", "IT본부")
        );
        
        return approvalLineRepository.saveAll(lines);
    }
}