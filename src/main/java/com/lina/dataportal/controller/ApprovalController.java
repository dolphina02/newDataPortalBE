package com.lina.dataportal.controller;

import com.lina.dataportal.domain.approval.Approval;
import com.lina.dataportal.domain.approval.ApprovalStep;
import com.lina.dataportal.domain.approval.ApprovalType;
import com.lina.dataportal.domain.approval.TargetType;
import com.lina.dataportal.domain.approval.AccessScope;
import com.lina.dataportal.domain.approval.SensitivityLevel;
import com.lina.dataportal.domain.approval.SensitivityCategory;
import com.lina.dataportal.domain.user.User;
import com.lina.dataportal.repository.ApprovalRepository;
import com.lina.dataportal.service.ApprovalService;
import com.lina.dataportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/approvals")
@CrossOrigin(origins = "*")
public class ApprovalController {
    
    private final ApprovalService approvalService;
    private final UserService userService;
    private final ApprovalRepository approvalRepository;
    
    @Autowired
    public ApprovalController(ApprovalService approvalService, UserService userService, ApprovalRepository approvalRepository) {
        this.approvalService = approvalService;
        this.userService = userService;
        this.approvalRepository = approvalRepository;
    }
    
    @GetMapping("/submitted")
    public ResponseEntity<List<Approval>> getSubmittedApprovals(
            @RequestParam(required = false) Long requesterId,
            @RequestParam(required = false, defaultValue = "admin@company.com") String requesterEmail) {
        
        List<Approval> approvals;
        if (requesterId != null) {
            approvals = approvalService.getSubmittedApprovals(requesterId);
        } else {
            // 하위 호환성을 위한 이메일 기반 조회
            approvals = approvalService.getSubmittedApprovals(requesterEmail);
        }
        return ResponseEntity.ok(approvals);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<Approval>> getPendingApprovals() {
        List<Approval> approvals = approvalService.getPendingApprovals();
        return ResponseEntity.ok(approvals);
    }
    
    @GetMapping("/completed")
    public ResponseEntity<List<Approval>> getCompletedApprovals() {
        List<Approval> approvals = approvalService.getCompletedApprovals();
        return ResponseEntity.ok(approvals);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Approval> getApprovalById(@PathVariable Long id) {
        return approvalService.getApprovalById(id)
            .map(approval -> ResponseEntity.ok(approval))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/deploy")
    public ResponseEntity<Approval> createDeployRequest(@RequestBody Map<String, Object> request) {
        String title = (String) request.get("title");
        String description = (String) request.get("description");
        
        // User ID 또는 이메일로 요청자 조회
        User requester = getRequesterFromRequest(request);
        
        Approval approval = approvalService.createDeployRequest(title, description, requester);
        return ResponseEntity.status(HttpStatus.CREATED).body(approval);
    }
    
    @PostMapping("/subscribe")
    public ResponseEntity<Approval> createSubscribeRequest(@RequestBody Map<String, Object> request) {
        String title = (String) request.get("title");
        String description = (String) request.get("description");
        
        User requester = getRequesterFromRequest(request);
        
        Approval approval = approvalService.createDashboardSubscribeRequest(title, description, requester);
        return ResponseEntity.status(HttpStatus.CREATED).body(approval);
    }
    
    @PostMapping("/data-access")
    public ResponseEntity<Approval> createDataAccessRequest(@RequestBody Map<String, Object> request) {
        String title = (String) request.get("title");
        String description = (String) request.get("description");
        
        User requester = getRequesterFromRequest(request);
        
        Approval approval = approvalService.createDataAccessRequest(title, description, requester);
        return ResponseEntity.status(HttpStatus.CREATED).body(approval);
    }
    
    /**
     * 요청에서 요청자 User 객체 추출 (User ID 우선, 없으면 이메일 사용)
     */
    private User getRequesterFromRequest(Map<String, Object> request) {
        // User ID가 있으면 우선 사용
        if (request.containsKey("requesterId")) {
            Long requesterId = Long.valueOf(request.get("requesterId").toString());
            return userService.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + requesterId));
        }
        
        // 이메일로 조회 또는 생성 (하위 호환성)
        String requesterEmail = (String) request.getOrDefault("requester", "admin@company.com");
        return userService.findByEmailOrCreate(requesterEmail);
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<Approval> approveRequest(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> request) {
        try {
            User reviewer = getReviewerFromRequest(request);
            String comment = (String) request.get("comment");
            
            Approval approval = approvalService.approveRequest(id, reviewer, comment);
            return ResponseEntity.ok(approval);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/reject")
    public ResponseEntity<Approval> rejectRequest(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> request) {
        try {
            User reviewer = getReviewerFromRequest(request);
            String comment = (String) request.get("comment");
            
            Approval approval = approvalService.rejectRequest(id, reviewer, comment);
            return ResponseEntity.ok(approval);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 요청에서 검토자 User 객체 추출 (User ID 우선, 없으면 이메일 사용)
     */
    private User getReviewerFromRequest(Map<String, Object> request) {
        // User ID가 있으면 우선 사용
        if (request.containsKey("reviewerId")) {
            Long reviewerId = Long.valueOf(request.get("reviewerId").toString());
            return userService.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + reviewerId));
        }
        
        // 이메일로 조회 또는 생성 (하위 호환성)
        String reviewerEmail = (String) request.getOrDefault("reviewer", "admin@company.com");
        return userService.findByEmailOrCreate(reviewerEmail);
    }
    
    @PutMapping("/{id}/next-step")
    public ResponseEntity<Approval> moveToNextStep(@PathVariable Long id) {
        try {
            Approval approval = approvalService.moveToNextStep(id);
            return ResponseEntity.ok(approval);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 특정 담당자의 미결 승인 목록 조회 (User ID 기반)
     * GET /approvals/my-pending?approverId=2 또는 GET /approvals/my-pending?approverEmail=kim.leader@company.com
     */
    @GetMapping("/my-pending")
    public ResponseEntity<List<ApprovalStep>> getMyPendingApprovals(
            @RequestParam(required = false) Long approverId,
            @RequestParam(required = false) String approverEmail) {
        
        List<ApprovalStep> pendingApprovals;
        if (approverId != null) {
            pendingApprovals = approvalService.getPendingApprovalsByApproverId(approverId);
        } else if (approverEmail != null) {
            // 하위 호환성을 위한 이메일 기반 조회
            pendingApprovals = approvalService.getPendingApprovalsByApproverId(approverEmail);
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(pendingApprovals);
    }
    
    /**
     * 특정 담당자의 미결 승인 개수 조회 (User ID 기반)
     * GET /approvals/my-pending-count?approverId=2 또는 GET /approvals/my-pending-count?approverEmail=kim.leader@company.com
     */
    @GetMapping("/my-pending-count")
    public ResponseEntity<Map<String, Object>> getMyPendingApprovalsCount(
            @RequestParam(required = false) Long approverId,
            @RequestParam(required = false) String approverEmail) {
        
        long count;
        String identifier;
        
        if (approverId != null) {
            count = approvalService.getPendingApprovalsCountByApproverId(approverId);
            identifier = approverId.toString();
        } else if (approverEmail != null) {
            // 하위 호환성을 위한 이메일 기반 조회
            count = approvalService.getPendingApprovalsCountByApproverId(approverEmail);
            identifier = approverEmail;
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("approverId", identifier);
        result.put("pendingCount", count);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 승인 요청의 진행률 조회 (계산 필드 기반)
     * GET /approvals/{id}/progress
     */
    @GetMapping("/{id}/progress")
    public ResponseEntity<Map<String, Object>> getApprovalProgress(@PathVariable Long id) {
        try {
            Optional<Approval> approvalOpt = approvalService.getApprovalById(id);
            if (approvalOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Approval approval = approvalOpt.get();
            Map<String, Object> progressInfo = new HashMap<>();
            progressInfo.put("approvalId", id);
            progressInfo.put("currentStep", approval.getCurrentStep());
            progressInfo.put("totalSteps", approval.getTotalSteps());
            progressInfo.put("progressPercentage", approval.getProgressPercentage());
            progressInfo.put("isRequiredStepsCompleted", approval.isRequiredStepsCompleted());
            progressInfo.put("hasRejectedStep", approval.hasRejectedStep());
            progressInfo.put("isHighRisk", approval.isHighRiskRequest());
            progressInfo.put("requiresAdditionalApproval", approval.requiresAdditionalApproval());
            progressInfo.put("requiresMasking", approval.requiresMaskingPolicy());
            progressInfo.put("requiresAuditLog", approval.requiresAuditLogging());
            progressInfo.put("accessExpired", approval.isAccessExpired());
            return ResponseEntity.ok(progressInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 승인 요청의 다음 승인 단계 조회 (ApprovalStep 기반)
     * GET /approvals/{id}/next-step
     */
    @GetMapping("/{id}/next-step")
    public ResponseEntity<ApprovalStep> getNextPendingStep(@PathVariable Long id) {
        return approvalService.getNextPendingApprovalLine(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // ========== 승인 대상 오브젝트 기반 API ==========
    
    /**
     * 승인 대상 오브젝트 포함 승인 요청 생성
     * POST /approvals/with-target
     */
    @PostMapping("/with-target")
    public ResponseEntity<Approval> createApprovalWithTarget(@RequestBody Map<String, Object> request) {
        try {
            String title = (String) request.get("title");
            String description = (String) request.get("description");
            ApprovalType type = ApprovalType.valueOf((String) request.get("type"));
            
            // 요청자 정보
            User requester = getRequesterFromRequest(request);
            
            // 승인 대상 오브젝트 정보
            TargetType targetType = TargetType.valueOf((String) request.get("targetType"));
            String targetId = (String) request.get("targetId");
            String targetName = (String) request.get("targetName");
            
            // 접근 범위 및 민감도
            AccessScope accessScope = AccessScope.valueOf((String) request.get("accessScope"));
            SensitivityLevel sensitivityLevel = SensitivityLevel.valueOf((String) request.get("sensitivityLevel"));
            
            // 업무 정당성
            String businessJustification = (String) request.get("businessJustification");
            String dataUsagePurpose = (String) request.get("dataUsagePurpose");
            
            Approval approval = approvalService.createApprovalWithTarget(
                type, title, description, requester,
                targetType, targetId, targetName,
                accessScope, sensitivityLevel,
                businessJustification, dataUsagePurpose
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(approval);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 특정 대상 타입의 승인 요청들 조회
     * GET /approvals/by-target-type/{targetType}
     */
    @GetMapping("/by-target-type/{targetType}")
    public ResponseEntity<List<Approval>> getApprovalsByTargetType(@PathVariable String targetType) {
        try {
            TargetType type = TargetType.valueOf(targetType.toUpperCase());
            List<Approval> approvals = approvalService.getApprovalsByTargetType(type);
            return ResponseEntity.ok(approvals);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 특정 대상 오브젝트의 승인 요청들 조회
     * GET /approvals/by-target/{targetType}/{targetId}
     */
    @GetMapping("/by-target/{targetType}/{targetId}")
    public ResponseEntity<List<Approval>> getApprovalsByTarget(
            @PathVariable String targetType, 
            @PathVariable String targetId) {
        try {
            TargetType type = TargetType.valueOf(targetType.toUpperCase());
            List<Approval> approvals = approvalService.getApprovalsByTarget(type, targetId);
            return ResponseEntity.ok(approvals);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 민감도 레벨별 승인 요청들 조회
     * GET /approvals/by-sensitivity/{sensitivityLevel}
     */
    @GetMapping("/by-sensitivity/{sensitivityLevel}")
    public ResponseEntity<List<Approval>> getApprovalsBySensitivity(@PathVariable String sensitivityLevel) {
        try {
            SensitivityLevel level = SensitivityLevel.valueOf(sensitivityLevel.toUpperCase());
            List<Approval> approvals = approvalService.getApprovalsBySensitivityLevel(level);
            return ResponseEntity.ok(approvals);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 고위험 승인 요청들 조회
     * GET /approvals/high-risk
     */
    @GetMapping("/high-risk")
    public ResponseEntity<List<Approval>> getHighRiskApprovals() {
        List<Approval> approvals = approvalService.getHighRiskApprovals();
        return ResponseEntity.ok(approvals);
    }
    
    /**
     * 만료 예정 접근 권한 조회
     * GET /approvals/expiring?hours=24
     */
    @GetMapping("/expiring")
    public ResponseEntity<List<Approval>> getExpiringAccessApprovals(
            @RequestParam(defaultValue = "24") int hours) {
        List<Approval> approvals = approvalService.getExpiringAccessApprovals(hours);
        return ResponseEntity.ok(approvals);
    }
    
    /**
     * 특정 사용자의 특정 대상에 대한 활성 승인 조회
     * GET /approvals/active?userId=1&targetType=DATASET&targetId=dataset_001
     */
    @GetMapping("/active")
    public ResponseEntity<Approval> getActiveApprovalForUserAndTarget(
            @RequestParam Long userId,
            @RequestParam String targetType,
            @RequestParam String targetId) {
        try {
            TargetType type = TargetType.valueOf(targetType.toUpperCase());
            Optional<Approval> approval = approvalService.getActiveApprovalForUserAndTarget(userId, type, targetId);
            return approval.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 승인 완료 후 접근 권한 활성화
     * POST /approvals/{id}/activate-access
     */
    @PostMapping("/{id}/activate-access")
    public ResponseEntity<Map<String, Object>> activateAccess(@PathVariable Long id) {
        try {
            approvalService.activateApprovedAccess(id);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "Access activated successfully");
            successResponse.put("approvalId", id);
            return ResponseEntity.ok(successResponse);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 만료된 접근 권한 정리
     * POST /approvals/cleanup-expired
     */
    @PostMapping("/cleanup-expired")
    public ResponseEntity<Map<String, Object>> cleanupExpiredAccess() {
        int cleanedCount = approvalService.cleanupExpiredAccess();
        Map<String, Object> cleanupResult = new HashMap<>();
        cleanupResult.put("message", "Expired access cleanup completed");
        cleanupResult.put("cleanedCount", cleanedCount);
        return ResponseEntity.ok(cleanupResult);
    }
    
    /**
     * 만료 예정 사용 권한 조회 (기본 30일 이내)
     * GET /approvals/expiring?days=30
     */
    @GetMapping("/expiring")
    public ResponseEntity<List<Approval>> getExpiringUsageApprovals(
            @RequestParam(defaultValue = "30") int days) {
        List<Approval> approvals = approvalService.getExpiringUsageApprovals(days);
        return ResponseEntity.ok(approvals);
    }
    
    /**
     * 승인 완료 후 사용 권한 활성화
     * POST /approvals/{id}/activate-usage
     */
    @PostMapping("/{id}/activate-usage")
    public ResponseEntity<Map<String, Object>> activateUsage(@PathVariable Long id) {
        try {
            approvalService.activateApprovedUsage(id);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "Usage permission activated successfully");
            successResponse.put("approvalId", id);
            return ResponseEntity.ok(successResponse);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 만료된 사용 권한 정리
     * POST /approvals/cleanup-expired-usage
     */
    @PostMapping("/cleanup-expired-usage")
    public ResponseEntity<Map<String, Object>> cleanupExpiredUsage() {
        int cleanedCount = approvalService.cleanupExpiredUsage();
        Map<String, Object> cleanupResult = new HashMap<>();
        cleanupResult.put("message", "Expired usage permissions cleanup completed");
        cleanupResult.put("cleanedCount", cleanedCount);
        return ResponseEntity.ok(cleanupResult);
    }    /**
 
    * 민감도 레벨별 승인 요청 조회
     * GET /approvals/by-sensitivity/{sensitivityLevel}
     */
    @GetMapping("/by-sensitivity/{sensitivityLevel}")
    public ResponseEntity<List<Approval>> getApprovalsBySensitivityLevel(
            @PathVariable SensitivityLevel sensitivityLevel) {
        List<Approval> approvals = approvalService.getApprovalsBySensitivityLevel(sensitivityLevel);
        return ResponseEntity.ok(approvals);
    }
    
    /**
     * 민감도 카테고리별 승인 요청 조회
     * GET /approvals/by-sensitivity-category/{category}
     */
    @GetMapping("/by-sensitivity-category/{category}")
    public ResponseEntity<List<Approval>> getApprovalsBySensitivityCategory(
            @PathVariable String category) {
        
        List<Approval> approvals;
        if ("NORMAL".equalsIgnoreCase(category)) {
            approvals = approvalRepository.findNormalSensitivityApprovals();
        } else if ("SENSITIVE".equalsIgnoreCase(category)) {
            approvals = approvalRepository.findSensitiveApprovals();
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(approvals);
    }
    
    /**
     * 개인정보 관련 승인 요청 조회
     * GET /approvals/personal-data
     */
    @GetMapping("/personal-data")
    public ResponseEntity<List<Approval>> getPersonalDataApprovals() {
        List<Approval> approvals = approvalRepository.findPersonalDataApprovals();
        return ResponseEntity.ok(approvals);
    }
    
    /**
     * 규제 대상 승인 요청 조회
     * GET /approvals/regulated
     */
    @GetMapping("/regulated")
    public ResponseEntity<List<Approval>> getRegulatedApprovals() {
        List<Approval> approvals = approvalRepository.findRegulatedApprovals();
        return ResponseEntity.ok(approvals);
    }
    
    /**
     * 민감도 레벨별 승인 통계
     * GET /approvals/analytics/sensitivity-stats
     */
    @GetMapping("/analytics/sensitivity-stats")
    public ResponseEntity<Map<String, Object>> getSensitivityStats() {
        Map<SensitivityLevel, Long> levelStats = approvalService.getApprovalCountBySensitivityLevel();
        Map<SensitivityCategory, Long> categoryStats = approvalService.getApprovalCountBySensitivityCategory();
        
        Map<String, Object> result = new HashMap<>();
        result.put("byLevel", levelStats);
        result.put("byCategory", categoryStats);
        
        // 추가 통계 정보
        long totalNormal = categoryStats.getOrDefault(SensitivityCategory.NORMAL, 0L);
        long totalSensitive = categoryStats.getOrDefault(SensitivityCategory.SENSITIVE, 0L);
        long totalAll = totalNormal + totalSensitive;
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalApprovals", totalAll);
        summary.put("normalPercentage", totalAll > 0 ? (double) totalNormal / totalAll * 100 : 0);
        summary.put("sensitivePercentage", totalAll > 0 ? (double) totalSensitive / totalAll * 100 : 0);
        
        result.put("summary", summary);
        
        return ResponseEntity.ok(result);
    }}
