package com.lina.dataportal.controller;

import com.lina.dataportal.domain.approval.ApprovalStepTemplate;
import com.lina.dataportal.domain.approval.ApprovalType;
import com.lina.dataportal.service.ApprovalStepTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/approval-step-templates")
public class ApprovalStepTemplateController {
    
    @Autowired
    private ApprovalStepTemplateService templateService;
    
    /**
     * 모든 승인 단계 템플릿 조회
     */
    @GetMapping
    public ResponseEntity<List<ApprovalStepTemplate>> getAllTemplates() {
        List<ApprovalStepTemplate> templates = templateService.getAllTemplates();
        return ResponseEntity.ok(templates);
    }
    
    /**
     * 특정 승인 타입의 활성화된 템플릿들 조회
     */
    @GetMapping("/type/{approvalType}")
    public ResponseEntity<List<ApprovalStepTemplate>> getTemplatesByType(@PathVariable ApprovalType approvalType) {
        List<ApprovalStepTemplate> templates = templateService.getActiveTemplatesByType(approvalType);
        return ResponseEntity.ok(templates);
    }
    
    /**
     * 특정 승인 타입의 필수 템플릿들 조회
     */
    @GetMapping("/type/{approvalType}/required")
    public ResponseEntity<List<ApprovalStepTemplate>> getRequiredTemplatesByType(@PathVariable ApprovalType approvalType) {
        List<ApprovalStepTemplate> templates = templateService.getRequiredTemplatesByType(approvalType);
        return ResponseEntity.ok(templates);
    }
    
    /**
     * ID로 승인 단계 템플릿 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalStepTemplate> getTemplateById(@PathVariable Long id) {
        Optional<ApprovalStepTemplate> template = templateService.getTemplateById(id);
        return template.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 승인 단계 템플릿 생성
     */
    @PostMapping
    public ResponseEntity<ApprovalStepTemplate> createTemplate(@RequestBody ApprovalStepTemplate template) {
        try {
            ApprovalStepTemplate createdTemplate = templateService.createTemplate(template);
            return ResponseEntity.ok(createdTemplate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 승인 단계 템플릿 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApprovalStepTemplate> updateTemplate(@PathVariable Long id, @RequestBody ApprovalStepTemplate template) {
        try {
            ApprovalStepTemplate updatedTemplate = templateService.updateTemplate(id, template);
            return ResponseEntity.ok(updatedTemplate);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 승인 단계 템플릿 비활성화
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateTemplate(@PathVariable Long id) {
        try {
            templateService.deactivateTemplate(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 특정 승인 타입의 최대 단계 수 조회
     */
    @GetMapping("/type/{approvalType}/max-step")
    public ResponseEntity<Integer> getMaxStepOrder(@PathVariable ApprovalType approvalType) {
        Integer maxStep = templateService.getMaxStepOrder(approvalType);
        return ResponseEntity.ok(maxStep);
    }
    
    /**
     * 기본 템플릿 초기화 (개발/테스트용)
     */
    @PostMapping("/initialize")
    public ResponseEntity<String> initializeDefaultTemplates() {
        templateService.initializeDefaultTemplates();
        return ResponseEntity.ok("기본 승인 단계 템플릿이 초기화되었습니다.");
    }
    
    // ========== 고급 템플릿 수정 기능 ==========
    
    /**
     * 승인 단계 템플릿 부분 수정 (PATCH)
     * PATCH /api/approval-step-templates/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApprovalStepTemplate> updateTemplatePartial(@PathVariable Long id, @RequestBody ApprovalStepTemplate partialUpdate) {
        try {
            ApprovalStepTemplate updatedTemplate = templateService.updateTemplatePartial(id, partialUpdate);
            return ResponseEntity.ok(updatedTemplate);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 승인 타입별 템플릿 전체 교체
     * PUT /api/approval-step-templates/type/{approvalType}/replace
     */
    @PutMapping("/type/{approvalType}/replace")
    public ResponseEntity<List<ApprovalStepTemplate>> replaceTemplatesForType(
            @PathVariable ApprovalType approvalType, 
            @RequestBody List<ApprovalStepTemplate> newTemplates) {
        try {
            List<ApprovalStepTemplate> replacedTemplates = templateService.replaceTemplatesForType(approvalType, newTemplates);
            return ResponseEntity.ok(replacedTemplates);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 승인 단계 템플릿 순서 변경
     * PUT /api/approval-step-templates/type/{approvalType}/reorder
     */
    @PutMapping("/type/{approvalType}/reorder")
    public ResponseEntity<String> reorderTemplates(
            @PathVariable ApprovalType approvalType,
            @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> orderIds = (List<Integer>) request.get("orderIds");
            List<Long> longOrderIds = orderIds.stream().map(Integer::longValue).toList();
            
            templateService.reorderTemplates(approvalType, longOrderIds);
            return ResponseEntity.ok("템플릿 순서가 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("템플릿 순서 변경에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 승인 단계 템플릿 복사 (다른 승인 타입으로)
     * POST /api/approval-step-templates/copy
     */
    @PostMapping("/copy")
    public ResponseEntity<List<ApprovalStepTemplate>> copyTemplates(@RequestBody Map<String, String> request) {
        try {
            ApprovalType fromType = ApprovalType.valueOf(request.get("fromType"));
            ApprovalType toType = ApprovalType.valueOf(request.get("toType"));
            
            List<ApprovalStepTemplate> copiedTemplates = templateService.copyTemplates(fromType, toType);
            return ResponseEntity.ok(copiedTemplates);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 승인 단계 템플릿에 새 단계 추가
     * POST /api/approval-step-templates/type/{approvalType}/add-step
     */
    @PostMapping("/type/{approvalType}/add-step")
    public ResponseEntity<ApprovalStepTemplate> addTemplateStep(
            @PathVariable ApprovalType approvalType,
            @RequestBody Map<String, Object> request) {
        try {
            Integer insertPosition = (Integer) request.get("insertPosition");
            String approverRole = (String) request.get("approverRole");
            String approverDepartment = (String) request.get("approverDepartment");
            String approverId = (String) request.get("approverId");
            String approverName = (String) request.get("approverName");
            Boolean isRequired = (Boolean) request.get("isRequired");
            String description = (String) request.get("description");
            
            ApprovalStepTemplate newTemplate = templateService.addTemplateStep(
                approvalType, insertPosition, approverRole, approverDepartment, 
                approverId, approverName, isRequired, description);
            
            return ResponseEntity.ok(newTemplate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 승인 단계 템플릿 단계 삭제
     * DELETE /api/approval-step-templates/step/{templateId}
     */
    @DeleteMapping("/step/{templateId}")
    public ResponseEntity<String> deleteTemplateStep(@PathVariable Long templateId) {
        try {
            templateService.deleteTemplateStep(templateId);
            return ResponseEntity.ok("템플릿 단계가 삭제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("템플릿 단계 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 승인 타입별 템플릿 유효성 검증
     * GET /api/approval-step-templates/type/{approvalType}/validate
     */
    @GetMapping("/type/{approvalType}/validate")
    public ResponseEntity<Map<String, Object>> validateTemplates(@PathVariable ApprovalType approvalType) {
        boolean isValid = templateService.validateTemplates(approvalType);
        return ResponseEntity.ok(Map.of(
            "approvalType", approvalType,
            "isValid", isValid
        ));
    }
}