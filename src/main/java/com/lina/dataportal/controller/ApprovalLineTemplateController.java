package com.lina.dataportal.controller;

import com.lina.dataportal.domain.approval.ApprovalLineTemplate;
import com.lina.dataportal.domain.approval.ApprovalType;
import com.lina.dataportal.service.ApprovalLineTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/approval-line-templates")
public class ApprovalLineTemplateController {
    
    @Autowired
    private ApprovalLineTemplateService templateService;
    
    /**
     * 모든 승인 라인 템플릿 조회
     */
    @GetMapping
    public ResponseEntity<List<ApprovalLineTemplate>> getAllTemplates() {
        List<ApprovalLineTemplate> templates = templateService.getAllTemplates();
        return ResponseEntity.ok(templates);
    }
    
    /**
     * 특정 승인 타입의 활성화된 템플릿들 조회
     */
    @GetMapping("/type/{approvalType}")
    public ResponseEntity<List<ApprovalLineTemplate>> getTemplatesByType(@PathVariable ApprovalType approvalType) {
        List<ApprovalLineTemplate> templates = templateService.getActiveTemplatesByType(approvalType);
        return ResponseEntity.ok(templates);
    }
    
    /**
     * 특정 승인 타입의 필수 템플릿들 조회
     */
    @GetMapping("/type/{approvalType}/required")
    public ResponseEntity<List<ApprovalLineTemplate>> getRequiredTemplatesByType(@PathVariable ApprovalType approvalType) {
        List<ApprovalLineTemplate> templates = templateService.getRequiredTemplatesByType(approvalType);
        return ResponseEntity.ok(templates);
    }
    
    /**
     * ID로 승인 라인 템플릿 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalLineTemplate> getTemplateById(@PathVariable Long id) {
        Optional<ApprovalLineTemplate> template = templateService.getTemplateById(id);
        return template.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 승인 라인 템플릿 생성
     */
    @PostMapping
    public ResponseEntity<ApprovalLineTemplate> createTemplate(@RequestBody ApprovalLineTemplate template) {
        try {
            ApprovalLineTemplate createdTemplate = templateService.createTemplate(template);
            return ResponseEntity.ok(createdTemplate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 승인 라인 템플릿 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApprovalLineTemplate> updateTemplate(@PathVariable Long id, @RequestBody ApprovalLineTemplate template) {
        try {
            ApprovalLineTemplate updatedTemplate = templateService.updateTemplate(id, template);
            return ResponseEntity.ok(updatedTemplate);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 승인 라인 템플릿 비활성화
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
        return ResponseEntity.ok("기본 승인 라인 템플릿이 초기화되었습니다.");
    }
    
    // ========== 고급 템플릿 수정 기능 ==========
    
    /**
     * 승인 라인 템플릿 부분 수정 (PATCH)
     * PATCH /api/approval-line-templates/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApprovalLineTemplate> updateTemplatePartial(@PathVariable Long id, @RequestBody ApprovalLineTemplate partialUpdate) {
        try {
            ApprovalLineTemplate updatedTemplate = templateService.updateTemplatePartial(id, partialUpdate);
            return ResponseEntity.ok(updatedTemplate);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 승인 타입별 템플릿 전체 교체
     * PUT /api/approval-line-templates/type/{approvalType}/replace
     */
    @PutMapping("/type/{approvalType}/replace")
    public ResponseEntity<List<ApprovalLineTemplate>> replaceTemplatesForType(
            @PathVariable ApprovalType approvalType, 
            @RequestBody List<ApprovalLineTemplate> newTemplates) {
        try {
            List<ApprovalLineTemplate> replacedTemplates = templateService.replaceTemplatesForType(approvalType, newTemplates);
            return ResponseEntity.ok(replacedTemplates);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 승인 라인 템플릿 순서 변경
     * PUT /api/approval-line-templates/type/{approvalType}/reorder
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
     * 승인 라인 템플릿 복사 (다른 승인 타입으로)
     * POST /api/approval-line-templates/copy
     */
    @PostMapping("/copy")
    public ResponseEntity<List<ApprovalLineTemplate>> copyTemplates(@RequestBody Map<String, String> request) {
        try {
            ApprovalType fromType = ApprovalType.valueOf(request.get("fromType"));
            ApprovalType toType = ApprovalType.valueOf(request.get("toType"));
            
            List<ApprovalLineTemplate> copiedTemplates = templateService.copyTemplates(fromType, toType);
            return ResponseEntity.ok(copiedTemplates);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 승인 라인 템플릿에 새 단계 추가
     * POST /api/approval-line-templates/type/{approvalType}/add-step
     */
    @PostMapping("/type/{approvalType}/add-step")
    public ResponseEntity<ApprovalLineTemplate> addTemplateStep(
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
            
            ApprovalLineTemplate newTemplate = templateService.addTemplateStep(
                approvalType, insertPosition, approverRole, approverDepartment, 
                approverId, approverName, isRequired, description);
            
            return ResponseEntity.ok(newTemplate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 승인 라인 템플릿 단계 삭제
     * DELETE /api/approval-line-templates/step/{templateId}
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
     * GET /api/approval-line-templates/type/{approvalType}/validate
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