package com.lina.dataportal.service;

import com.lina.dataportal.domain.approval.ApprovalStep;
import com.lina.dataportal.domain.approval.ApprovalLineStatus;
import com.lina.dataportal.domain.approval.ApprovalLineTemplate;
import com.lina.dataportal.domain.approval.ApprovalType;
import com.lina.dataportal.domain.user.User;
import com.lina.dataportal.repository.ApprovalStepRepository;
import com.lina.dataportal.repository.ApprovalLineTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ApprovalStepService {
    
    @Autowired
    private ApprovalStepRepository approvalStepRepository;
    
    @Autowired
    private ApprovalLineTemplateRepository templateRepository;
    
    @Autowired
    private UserService userService;
    
    /**
     * 승인 요청 생성 시 템플릿을 기반으로 승인 단계들 생성 (스냅샷)
     */
    public List<ApprovalStep> createApprovalStepsFromTemplate(Long approvalId, ApprovalType approvalType) {
        // 현재 활성화된 템플릿들 조회
        List<ApprovalLineTemplate> templates = templateRepository.findByApprovalTypeAndIsActiveTrueOrderByStepOrder(approvalType);
        
        if (templates.isEmpty()) {
            throw new RuntimeException("No active approval line templates found for type: " + approvalType);
        }
        
        // 템플릿을 기반으로 승인 단계들 생성 (스냅샷)
        List<ApprovalStep> steps = templates.stream()
            .map(template -> ApprovalStep.fromTemplate(approvalId, template))
            .toList();
        
        return approvalStepRepository.saveAll(steps);
    }
    
    /**
     * 승인 요청의 모든 승인 단계 조회
     */
    @Transactional(readOnly = true)
    public List<ApprovalStep> getApprovalSteps(Long approvalId) {
        return approvalStepRepository.findByApprovalIdOrderByStepOrder(approvalId);
    }
    
    /**
     * 특정 승인자의 미결 승인 단계들 조회 - User 기반
     */
    @Transactional(readOnly = true)
    public List<ApprovalStep> getPendingApprovalsByApproverId(Long approverId) {
        return approvalStepRepository.findPendingApprovalsByApproverId(approverId);
    }
    
    @Transactional(readOnly = true)
    public List<ApprovalStep> getPendingApprovalsByApprover(User approver) {
        return approvalStepRepository.findPendingApprovalsByApproverId(approver.getId());
    }
    
    // 하위 호환성을 위한 이메일 기반 메서드 (Deprecated)
    @Deprecated
    @Transactional(readOnly = true)
    public List<ApprovalStep> getPendingApprovalsByApproverId(String approverEmail) {
        User approver = userService.findByEmail(approverEmail);
        return approver != null ? getPendingApprovalsByApproverId(approver.getId()) : List.of();
    }
    
    /**
     * 다음 승인 단계 조회
     */
    @Transactional(readOnly = true)
    public Optional<ApprovalStep> getNextPendingApprovalStep(Long approvalId) {
        return approvalStepRepository.findNextPendingApprovalStep(approvalId);
    }
    
    /**
     * 승인 단계 승인 처리
     */
    public ApprovalStep approveStep(Long stepId, String comment) {
        ApprovalStep step = approvalStepRepository.findById(stepId)
            .orElseThrow(() -> new RuntimeException("Approval step not found with id: " + stepId));
        
        if (step.getStatus() != ApprovalLineStatus.PENDING) {
            throw new RuntimeException("Cannot approve step that is not pending. Current status: " + step.getStatus());
        }
        
        step.approve(comment);
        return approvalStepRepository.save(step);
    }
    
    /**
     * 승인 단계 반려 처리
     */
    public ApprovalStep rejectStep(Long stepId, String comment) {
        ApprovalStep step = approvalStepRepository.findById(stepId)
            .orElseThrow(() -> new RuntimeException("Approval step not found with id: " + stepId));
        
        if (step.getStatus() != ApprovalLineStatus.PENDING) {
            throw new RuntimeException("Cannot reject step that is not pending. Current status: " + step.getStatus());
        }
        
        step.reject(comment);
        return approvalStepRepository.save(step);
    }
    
    /**
     * 승인 단계 건너뛰기 처리
     */
    public ApprovalStep skipStep(Long stepId, String comment) {
        ApprovalStep step = approvalStepRepository.findById(stepId)
            .orElseThrow(() -> new RuntimeException("Approval step not found with id: " + stepId));
        
        if (step.getStatus() != ApprovalLineStatus.PENDING) {
            throw new RuntimeException("Cannot skip step that is not pending. Current status: " + step.getStatus());
        }
        
        if (step.getIsRequired()) {
            throw new RuntimeException("Cannot skip required approval step");
        }
        
        step.skip(comment);
        return approvalStepRepository.save(step);
    }
    
    /**
     * 승인 완료 여부 확인 (모든 필수 단계 승인 완료)
     */
    @Transactional(readOnly = true)
    public boolean isApprovalCompleted(Long approvalId) {
        Long approvedRequiredCount = approvalStepRepository.countApprovedRequiredStepsByApprovalId(approvalId);
        Long totalRequiredCount = approvalStepRepository.countTotalRequiredStepsByApprovalId(approvalId);
        
        return approvedRequiredCount.equals(totalRequiredCount);
    }
    
    /**
     * 반려된 단계 존재 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean hasRejectedStep(Long approvalId) {
        return approvalStepRepository.existsRejectedStepByApprovalId(approvalId);
    }
    
    /**
     * 승인 진행률 계산 (승인된 필수 단계 / 전체 필수 단계)
     */
    @Transactional(readOnly = true)
    public double getApprovalProgress(Long approvalId) {
        Long approvedRequiredCount = approvalStepRepository.countApprovedRequiredStepsByApprovalId(approvalId);
        Long totalRequiredCount = approvalStepRepository.countTotalRequiredStepsByApprovalId(approvalId);
        
        if (totalRequiredCount == 0) return 0.0;
        return (double) approvedRequiredCount / totalRequiredCount * 100.0;
    }
    
    /**
     * 전체 진행률 계산 (승인된 단계 / 전체 단계)
     */
    @Transactional(readOnly = true)
    public double getTotalProgress(Long approvalId) {
        Long approvedCount = approvalStepRepository.countApprovedStepsByApprovalId(approvalId);
        Long totalCount = approvalStepRepository.countTotalStepsByApprovalId(approvalId);
        
        if (totalCount == 0) return 0.0;
        return (double) approvedCount / totalCount * 100.0;
    }
    
    /**
     * 특정 템플릿 버전을 사용하는 승인 단계들 조회 (템플릿 변경 영향 분석용)
     */
    @Transactional(readOnly = true)
    public List<ApprovalStep> getStepsByTemplateVersion(Long templateId, Long templateVersion) {
        return approvalStepRepository.findByTemplateIdAndTemplateVersion(templateId, templateVersion);
    }
    
    /**
     * 승인 단계 수정 가능 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean canModifyStep(Long stepId) {
        ApprovalStep step = approvalStepRepository.findById(stepId)
            .orElseThrow(() -> new RuntimeException("Approval step not found with id: " + stepId));
        
        return step.isPending();
    }
    
    /**
     * 승인 요청의 수정 가능한 단계들 조회
     */
    @Transactional(readOnly = true)
    public List<ApprovalStep> getModifiableSteps(Long approvalId) {
        return approvalStepRepository.findPendingStepsByApprovalId(approvalId);
    }
}