package com.lina.dataportal.service;

import com.lina.dataportal.domain.approval.Approval;
import com.lina.dataportal.domain.approval.ApprovalStatus;
import com.lina.dataportal.domain.approval.ApprovalType;
import com.lina.dataportal.repository.ApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ApprovalService {
    
    private final ApprovalRepository approvalRepository;
    
    @Autowired
    public ApprovalService(ApprovalRepository approvalRepository) {
        this.approvalRepository = approvalRepository;
    }
    
    public List<Approval> getSubmittedApprovals(String requester) {
        return approvalRepository.findByRequester(requester);
    }
    
    public List<Approval> getPendingApprovals() {
        return approvalRepository.findPendingApprovalsByPriority();
    }
    
    public List<Approval> getCompletedApprovals() {
        return approvalRepository.findCompletedApprovals();
    }
    
    public Optional<Approval> getApprovalById(Long id) {
        return approvalRepository.findById(id);
    }
    
    public Approval createDeployRequest(String title, String description, String requester) {
        Approval approval = new Approval(ApprovalType.DEPLOY, title, description, requester);
        approval.setTotalSteps(3); // 배포요청 → 검토 → 배포완료
        return approvalRepository.save(approval);
    }
    
    public Approval createDashboardSubscribeRequest(String title, String description, String requester) {
        Approval approval = new Approval(ApprovalType.DASHBOARD, title, description, requester);
        return approvalRepository.save(approval);
    }
    
    public Approval createDataAccessRequest(String title, String description, String requester) {
        Approval approval = new Approval(ApprovalType.DATA, title, description, requester);
        return approvalRepository.save(approval);
    }
    
    public Approval approveRequest(Long id, String reviewer, String comment) {
        Approval approval = approvalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Approval not found with id: " + id));
        
        approval.approve(reviewer, comment);
        return approvalRepository.save(approval);
    }
    
    public Approval rejectRequest(Long id, String reviewer, String comment) {
        Approval approval = approvalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Approval not found with id: " + id));
        
        approval.reject(reviewer, comment);
        return approvalRepository.save(approval);
    }
    
    public Approval moveToNextStep(Long id) {
        Approval approval = approvalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Approval not found with id: " + id));
        
        approval.nextStep();
        return approvalRepository.save(approval);
    }
    
    public List<Approval> getApprovalsByType(ApprovalType type) {
        return approvalRepository.findByType(type);
    }
}