package com.lina.dataportal.service;

import com.lina.dataportal.domain.approval.Approval;
import com.lina.dataportal.domain.approval.ApprovalStep;
import com.lina.dataportal.domain.approval.ApprovalStatus;
import com.lina.dataportal.domain.approval.ApprovalType;
import com.lina.dataportal.domain.approval.SensitivityLevel;
import com.lina.dataportal.domain.approval.SensitivityCategory;
import com.lina.dataportal.domain.approval.TargetType;
import com.lina.dataportal.domain.approval.AccessScope;
import com.lina.dataportal.domain.user.User;
import com.lina.dataportal.repository.ApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class ApprovalService {
    
    private static final Logger log = LoggerFactory.getLogger(ApprovalService.class);
    
    private final ApprovalRepository approvalRepository;
    private final ApprovalStepService approvalStepService;
    private final UserService userService;
    
    @Autowired
    public ApprovalService(ApprovalRepository approvalRepository, ApprovalStepService approvalStepService, UserService userService) {
        this.approvalRepository = approvalRepository;
        this.approvalStepService = approvalStepService;
        this.userService = userService;
    }
    
    // User 기반 메서드들
    public List<Approval> getSubmittedApprovals(Long requesterId) {
        return approvalRepository.findByRequesterId(requesterId);
    }
    
    public List<Approval> getSubmittedApprovals(User requester) {
        return approvalRepository.findByRequesterId(requester.getId());
    }
    
    // 하위 호환성을 위한 이메일 기반 메서드 (Deprecated)
    @Deprecated
    public List<Approval> getSubmittedApprovals(String requesterEmail) {
        User user = userService.findByEmail(requesterEmail);
        return user != null ? approvalRepository.findByRequesterId(user.getId()) : List.of();
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
    
    // User 기반 생성 메서드들
    public Approval createDeployRequest(String title, String description, User requester) {
        Approval approval = new Approval(ApprovalType.DEPLOY, title, description, requester);
        return approvalRepository.save(approval);
    }
    
    public Approval createDashboardSubscribeRequest(String title, String description, User requester) {
        Approval approval = new Approval(ApprovalType.SUBSCRIBE, title, description, requester);
        return approvalRepository.save(approval);
    }
    
    public Approval createDataAccessRequest(String title, String description, User requester) {
        Approval approval = new Approval(ApprovalType.ACCESS, title, description, requester);
        return approvalRepository.save(approval);
    }
    
    // 하위 호환성을 위한 이메일 기반 메서드들 (Deprecated)
    @Deprecated
    public Approval createDeployRequest(String title, String description, String requesterEmail) {
        User requester = userService.findByEmailOrCreate(requesterEmail);
        return createDeployRequest(title, description, requester);
    }
    
    @Deprecated
    public Approval createDashboardSubscribeRequest(String title, String description, String requesterEmail) {
        User requester = userService.findByEmailOrCreate(requesterEmail);
        return createDashboardSubscribeRequest(title, description, requester);
    }
    
    @Deprecated
    public Approval createDataAccessRequest(String title, String description, String requesterEmail) {
        User requester = userService.findByEmailOrCreate(requesterEmail);
        return createDataAccessRequest(title, description, requester);
    }
    
    // User 기반 승인/반려 메서드들
    public Approval approveRequest(Long id, User reviewer, String comment) {
        Approval approval = approvalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Approval not found with id: " + id));
        
        approval.approve(reviewer, comment);
        return approvalRepository.save(approval);
    }
    
    public Approval rejectRequest(Long id, User reviewer, String comment) {
        Approval approval = approvalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Approval not found with id: " + id));
        
        approval.reject(reviewer, comment);
        return approvalRepository.save(approval);
    }
    
    // 하위 호환성을 위한 이메일 기반 메서드들 (Deprecated)
    @Deprecated
    public Approval approveRequest(Long id, String reviewerEmail, String comment) {
        User reviewer = userService.findByEmailOrCreate(reviewerEmail);
        return approveRequest(id, reviewer, comment);
    }
    
    @Deprecated
    public Approval rejectRequest(Long id, String reviewerEmail, String comment) {
        User reviewer = userService.findByEmailOrCreate(reviewerEmail);
        return rejectRequest(id, reviewer, comment);
    }
    
    public Approval moveToNextStep(Long id) {
        Approval approval = approvalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Approval not found with id: " + id));
        
        // 다음 단계로 이동 로직 (임시 구현)
        if (approval.getStatus() == ApprovalStatus.PENDING) {
            approval.setStatus(ApprovalStatus.IN_PROGRESS);
        } else if (approval.getStatus() == ApprovalStatus.IN_PROGRESS) {
            approval.setStatus(ApprovalStatus.APPROVED);
        }
        
        return approvalRepository.save(approval);
    }
    
    public List<Approval> getApprovalsByType(ApprovalType type) {
        return approvalRepository.findByType(type);
    }
    
    /**
     * 특정 담당자의 미결 승인 목록 조회 (User 기반) - ApprovalStep 사용
     */
    @Transactional(readOnly = true)
    public List<ApprovalStep> getPendingApprovalsByApproverId(Long approverId) {
        return approvalStepService.getPendingApprovalsByApproverId(approverId);
    }
    
    @Transactional(readOnly = true)
    public List<ApprovalStep> getPendingApprovalsByApprover(User approver) {
        return approvalStepService.getPendingApprovalsByApproverId(approver.getId());
    }
    
    /**
     * 담당자별 미결 승인 개수 조회 (User 기반) - ApprovalStep 사용
     */
    @Transactional(readOnly = true)
    public long getPendingApprovalsCountByApproverId(Long approverId) {
        return approvalStepService.getPendingApprovalsByApproverId(approverId).size();
    }
    
    @Transactional(readOnly = true)
    public long getPendingApprovalsCountByApprover(User approver) {
        return getPendingApprovalsCountByApproverId(approver.getId());
    }
    
    // 하위 호환성을 위한 이메일 기반 메서드들 (Deprecated)
    @Deprecated
    @Transactional(readOnly = true)
    public List<ApprovalStep> getPendingApprovalsByApproverId(String approverEmail) {
        User approver = userService.findByEmail(approverEmail);
        return approver != null ? getPendingApprovalsByApproverId(approver.getId()) : List.of();
    }
    
    @Deprecated
    @Transactional(readOnly = true)
    public long getPendingApprovalsCountByApproverId(String approverEmail) {
        User approver = userService.findByEmail(approverEmail);
        return approver != null ? getPendingApprovalsCountByApproverId(approver.getId()) : 0;
    }
    
    /**
     * 특정 승인 요청의 다음 승인 단계 조회 - ApprovalStep 사용
     */
    @Transactional(readOnly = true)
    public Optional<ApprovalStep> getNextPendingApprovalLine(Long approvalId) {
        return approvalStepService.getNextPendingApprovalStep(approvalId);
    }
    
    /**
     * 승인 진행률 조회 (승인된 단계 / 전체 단계) - ApprovalStep 사용
     */
    @Transactional(readOnly = true)
    public double getApprovalProgress(Long approvalId) {
        return approvalStepService.getApprovalProgress(approvalId);
    }
    
    // ========== 새로운 ApprovalStep 기반 메서드들 ==========
    
    /**
     * 승인 요청 생성 (템플릿 스냅샷 기반) - User 기반
     */
    public Approval createApprovalWithSteps(ApprovalType type, String title, String description, User requester) {
        // 1. 승인 요청 생성
        Approval approval = new Approval(type, title, description, requester);
        approval = approvalRepository.save(approval);
        
        // 2. 템플릿을 기반으로 승인 단계들 생성 (스냅샷)
        List<ApprovalStep> steps = approvalStepService.createApprovalStepsFromTemplate(approval.getId(), type);
        
        // 3. 승인 단계들을 Approval에 설정 (계산 필드 사용을 위해)
        approval.setApprovalSteps(steps);
        
        return approval;
    }
    
    // 하위 호환성을 위한 이메일 기반 메서드 (Deprecated)
    @Deprecated
    public Approval createApprovalWithSteps(ApprovalType type, String title, String description, String requesterEmail) {
        User requester = userService.findByEmailOrCreate(requesterEmail);
        return createApprovalWithSteps(type, title, description, requester);
    }
    
    /**
     * 승인 단계 기반 미결 승인 조회 - User 기반
     */
    @Transactional(readOnly = true)
    public List<ApprovalStep> getPendingApprovalStepsByApproverId(Long approverId) {
        return approvalStepService.getPendingApprovalsByApproverId(approverId);
    }
    
    @Transactional(readOnly = true)
    public List<ApprovalStep> getPendingApprovalStepsByApprover(User approver) {
        return approvalStepService.getPendingApprovalsByApproverId(approver.getId());
    }
    
    // 하위 호환성을 위한 이메일 기반 메서드 (Deprecated)
    @Deprecated
    @Transactional(readOnly = true)
    public List<ApprovalStep> getPendingApprovalStepsByApproverId(String approverEmail) {
        User approver = userService.findByEmail(approverEmail);
        return approver != null ? getPendingApprovalStepsByApproverId(approver.getId()) : List.of();
    }
    
    /**
     * 승인 단계 기반 진행률 조회
     */
    @Transactional(readOnly = true)
    public double getApprovalProgressBySteps(Long approvalId) {
        return approvalStepService.getApprovalProgress(approvalId);
    }
    
    /**
     * 승인 단계 기반 다음 단계 조회
     */
    @Transactional(readOnly = true)
    public Optional<ApprovalStep> getNextPendingApprovalStep(Long approvalId) {
        return approvalStepService.getNextPendingApprovalStep(approvalId);
    }
    
    /**
     * 승인 단계 기반 완료 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean isApprovalCompletedBySteps(Long approvalId) {
        return approvalStepService.isApprovalCompleted(approvalId);
    }
    
    /**
     * ApprovalStep과 함께 승인 요청 조회 (계산 필드 사용을 위해)
     */
    @Transactional(readOnly = true)
    public Approval getApprovalWithSteps(Long id) {
        return approvalRepository.findByIdWithSteps(id);
    }
    
    /**
     * 모든 승인 요청을 ApprovalStep과 함께 조회
     */
    @Transactional(readOnly = true)
    public List<Approval> getAllApprovalsWithSteps() {
        return approvalRepository.findAllWithSteps();
    }
    
    /**
     * 특정 상태의 승인 요청들을 ApprovalStep과 함께 조회
     */
    @Transactional(readOnly = true)
    public List<Approval> getApprovalsByStatusWithSteps(ApprovalStatus status) {
        return approvalRepository.findByStatusWithSteps(status);
    }
    
    // ========== 승인 대상 오브젝트 기반 메서드들 ==========
    
    /**
     * 승인 대상 오브젝트 포함 승인 요청 생성
     */
    public Approval createApprovalWithTarget(ApprovalType type, String title, String description, User requester,
                                           TargetType targetType, String targetId, String targetName,
                                           AccessScope accessScope, SensitivityLevel sensitivityLevel,
                                           String businessJustification, String dataUsagePurpose) {
        
        Approval approval = new Approval(type, title, description, requester, 
                                       targetType, targetId, targetName, 
                                       accessScope, sensitivityLevel);
        
        approval.setBusinessJustification(businessJustification);
        approval.setDataUsagePurpose(dataUsagePurpose);
        
        // 민감도와 접근 범위에 따른 자동 정책 적용
        applySecurityPolicies(approval);
        
        return approvalRepository.save(approval);
    }
    
    /**
     * 특정 대상 타입의 승인 요청들 조회
     */
    @Transactional(readOnly = true)
    public List<Approval> getApprovalsByTargetType(TargetType targetType) {
        return approvalRepository.findByTargetType(targetType);
    }
    
    /**
     * 특정 대상 오브젝트의 승인 요청들 조회
     */
    @Transactional(readOnly = true)
    public List<Approval> getApprovalsByTarget(TargetType targetType, String targetId) {
        return approvalRepository.findByTargetTypeAndTargetId(targetType, targetId);
    }
    
    /**
     * 민감도 레벨별 승인 요청들 조회
     */
    @Transactional(readOnly = true)
    public List<Approval> getApprovalsBySensitivityLevel(SensitivityLevel sensitivityLevel) {
        return approvalRepository.findBySensitivityLevel(sensitivityLevel);
    }
    
    /**
     * 고위험 승인 요청들 조회
     */
    @Transactional(readOnly = true)
    public List<Approval> getHighRiskApprovals() {
        return approvalRepository.findHighRiskApprovals();
    }
    
    /**
     * 만료 예정 접근 권한 조회
     */
    @Transactional(readOnly = true)
    public List<Approval> getExpiringAccessApprovals(int hoursFromNow) {
        LocalDateTime expiryThreshold = LocalDateTime.now().plusHours(hoursFromNow);
        return approvalRepository.findExpiringAccess(expiryThreshold);
    }
    
    /**
     * 특정 사용자의 특정 대상에 대한 활성 승인 조회
     */
    @Transactional(readOnly = true)
    public Optional<Approval> getActiveApprovalForUserAndTarget(Long userId, TargetType targetType, String targetId) {
        return approvalRepository.findActiveApprovalForUserAndTarget(userId, targetType, targetId);
    }
    
    /**
     * 보안 정책 자동 적용
     */
    private void applySecurityPolicies(Approval approval) {
        // 민감도 레벨에 따른 기본 정책 적용
        if (approval.getSensitivityLevel().isHighSensitive()) {
            approval.setRequiresMasking(true);
            approval.setRequiresAuditLog(true);
            
            // 고민감도 데이터는 짧은 유효 기간 설정
            if (approval.getAccessDurationHours() == null || approval.getAccessDurationHours() > 168) {
                approval.setAccessDurationHours(168); // 1주일
            }
        }
        
        // 개인정보의 경우 마스킹 정책 자동 적용
        if (approval.getSensitivityLevel().isPersonalData()) {
            // 기본 PII 마스킹 정책 적용 (실제 구현에서는 정책 ID 조회)
            approval.setMaskingPolicyId(1L); // PII_PARTIAL_MASKING
            approval.setPolicySetId(2L); // PII_PROTECTION_POLICY
        }
        
        // 금융 정보의 경우 금융 데이터 정책 적용
        if (approval.getSensitivityLevel() == SensitivityLevel.FINANCIAL) {
            approval.setMaskingPolicyId(2L); // FINANCIAL_FULL_MASKING
            approval.setPolicySetId(1L); // FINANCIAL_DATA_POLICY
        }
        
        // 고위험 접근 범위의 경우 추가 제한
        if (approval.getAccessScope().isHighRisk()) {
            approval.setRequiresAuditLog(true);
            
            // 고위험 접근은 더 짧은 유효 기간
            if (approval.getAccessDurationHours() == null || approval.getAccessDurationHours() > 72) {
                approval.setAccessDurationHours(72); // 3일
            }
        }
    }
    
    /**
     * 승인 완료 후 접근 권한 활성화
     */
    public void activateApprovedAccess(Long approvalId) {
        Approval approval = approvalRepository.findById(approvalId)
            .orElseThrow(() -> new RuntimeException("Approval not found with id: " + approvalId));
        
        if (approval.getStatus() == ApprovalStatus.APPROVED) {
            approval.activateAccess();
            approvalRepository.save(approval);
        }
    }
    
    /**
     * 만료된 접근 권한 정리
     */
    @Transactional
    public int cleanupExpiredAccess() {
        List<Approval> expiredApprovals = approvalRepository.findExpiredAccess(LocalDateTime.now());
        
        for (Approval approval : expiredApprovals) {
            // 실제 구현에서는 접근 권한 비활성화 로직 추가
            // 예: 데이터베이스 권한 제거, 캐시 무효화 등
        }
        
        return expiredApprovals.size();
    }
    
    /**
     * 승인 완료 후 사용 권한 활성화
     */
    @Transactional
    public void activateApprovedUsage(Long approvalId) {
        Approval approval = approvalRepository.findById(approvalId)
            .orElseThrow(() -> new RuntimeException("Approval not found with id: " + approvalId));
        
        if (approval.getStatus() == ApprovalStatus.APPROVED) {
            approval.activateUsage();
            approvalRepository.save(approval);
        }
    }
    
    /**
     * 만료 예정 사용 권한 조회 (기본 30일 이내)
     */
    @Transactional(readOnly = true)
    public List<Approval> getExpiringUsageApprovals() {
        return getExpiringUsageApprovals(30);
    }
    
    /**
     * 만료 예정 사용 권한 조회 (지정된 일수 이내)
     */
    @Transactional(readOnly = true)
    public List<Approval> getExpiringUsageApprovals(int daysFromNow) {
        LocalDateTime expiryThreshold = LocalDateTime.now().plusDays(daysFromNow);
        return approvalRepository.findExpiringUsage(expiryThreshold);
    }
    
    /**
     * 만료된 사용 권한 정리
     */
    @Transactional
    public int cleanupExpiredUsage() {
        List<Approval> expiredApprovals = approvalRepository.findExpiredUsage(LocalDateTime.now());
        
        for (Approval approval : expiredApprovals) {
            // 실제 구현에서는 사용 권한 비활성화 로직 추가
            // 예: 데이터베이스 권한 제거, 캐시 무효화 등
            log.info("Cleaning up expired usage for approval: {}", approval.getId());
        }
        
        return expiredApprovals.size();
    }
    

    
    /**
     * 민감도 레벨별 승인 통계 조회
     */
    @Transactional(readOnly = true)
    public Map<SensitivityLevel, Long> getApprovalCountBySensitivityLevel() {
        Map<SensitivityLevel, Long> result = new HashMap<>();
        
        for (SensitivityLevel level : SensitivityLevel.values()) {
            Long count = approvalRepository.countBySensitivityLevel(level);
            result.put(level, count);
        }
        
        return result;
    }
    
    /**
     * 민감도 카테고리별 승인 통계 조회
     */
    @Transactional(readOnly = true)
    public Map<SensitivityCategory, Long> getApprovalCountBySensitivityCategory() {
        Map<SensitivityCategory, Long> result = new HashMap<>();
        
        // NORMAL 카테고리 집계
        long normalCount = 0;
        for (SensitivityLevel level : SensitivityLevel.getNormalLevels()) {
            normalCount += approvalRepository.countBySensitivityLevel(level);
        }
        result.put(SensitivityCategory.NORMAL, normalCount);
        
        // SENSITIVE 카테고리 집계
        long sensitiveCount = 0;
        for (SensitivityLevel level : SensitivityLevel.getSensitiveLevels()) {
            sensitiveCount += approvalRepository.countBySensitivityLevel(level);
        }
        result.put(SensitivityCategory.SENSITIVE, sensitiveCount);
        
        return result;
    }}
