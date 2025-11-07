package com.lina.dataportal.service;

import com.lina.dataportal.domain.approval.ApprovalStepTemplate;
import com.lina.dataportal.domain.approval.ApprovalType;
import com.lina.dataportal.repository.ApprovalStepTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ApprovalStepTemplateService {
    
    @Autowired
    private ApprovalStepTemplateRepository templateRepository;
    
    /**
     * 특정 승인 타입의 활성화된 승인 단계 템플릿들을 조회
     */
    @Transactional(readOnly = true)
    public List<ApprovalStepTemplate> getActiveTemplatesByType(ApprovalType approvalType) {
        return templateRepository.findByApprovalTypeAndIsActiveTrueOrderByStepOrder(approvalType);
    }
    
    /**
     * 특정 승인 타입의 필수 승인 단계 템플릿들을 조회
     */
    @Transactional(readOnly = true)
    public List<ApprovalStepTemplate> getRequiredTemplatesByType(ApprovalType approvalType) {
        return templateRepository.findByApprovalTypeAndIsRequiredTrueAndIsActiveTrueOrderByStepOrder(approvalType);
    }
    
    /**
     * 승인 단계 템플릿 생성
     */
    public ApprovalStepTemplate createTemplate(ApprovalStepTemplate template) {
        return templateRepository.save(template);
    }
    
    /**
     * 승인 단계 템플릿 수정
     */
    public ApprovalStepTemplate updateTemplate(Long id, ApprovalStepTemplate updatedTemplate) {
        Optional<ApprovalStepTemplate> existingTemplate = templateRepository.findById(id);
        if (existingTemplate.isPresent()) {
            ApprovalStepTemplate template = existingTemplate.get();
            template.setApprovalType(updatedTemplate.getApprovalType());
            template.setStepOrder(updatedTemplate.getStepOrder());
            template.setApproverRole(updatedTemplate.getApproverRole());
            template.setApproverDepartment(updatedTemplate.getApproverDepartment());
            template.setApproverId(updatedTemplate.getApproverId());
            template.setApproverName(updatedTemplate.getApproverName());
            template.setIsRequired(updatedTemplate.getIsRequired());
            template.setIsActive(updatedTemplate.getIsActive());
            template.setDescription(updatedTemplate.getDescription());
            return templateRepository.save(template);
        }
        throw new RuntimeException("ApprovalStepTemplate not found with id: " + id);
    }
    
    /**
     * 승인 단계 템플릿 부분 수정 (특정 필드만 수정)
     */
    public ApprovalStepTemplate updateTemplatePartial(Long id, ApprovalStepTemplate partialUpdate) {
        ApprovalStepTemplate template = templateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("ApprovalStepTemplate not found with id: " + id));
        
        if (partialUpdate.getApprovalType() != null) {
            template.setApprovalType(partialUpdate.getApprovalType());
        }
        if (partialUpdate.getStepOrder() != null) {
            template.setStepOrder(partialUpdate.getStepOrder());
        }
        if (partialUpdate.getApproverRole() != null) {
            template.setApproverRole(partialUpdate.getApproverRole());
        }
        if (partialUpdate.getApproverDepartment() != null) {
            template.setApproverDepartment(partialUpdate.getApproverDepartment());
        }
        if (partialUpdate.getApproverId() != null) {
            template.setApproverId(partialUpdate.getApproverId());
        }
        if (partialUpdate.getApproverName() != null) {
            template.setApproverName(partialUpdate.getApproverName());
        }
        if (partialUpdate.getIsRequired() != null) {
            template.setIsRequired(partialUpdate.getIsRequired());
        }
        if (partialUpdate.getIsActive() != null) {
            template.setIsActive(partialUpdate.getIsActive());
        }
        if (partialUpdate.getDescription() != null) {
            template.setDescription(partialUpdate.getDescription());
        }
        
        return templateRepository.save(template);
    }
    
    /**
     * 승인 단계 템플릿 비활성화
     */
    public void deactivateTemplate(Long id) {
        Optional<ApprovalStepTemplate> template = templateRepository.findById(id);
        if (template.isPresent()) {
            template.get().setIsActive(false);
            templateRepository.save(template.get());
        } else {
            throw new RuntimeException("ApprovalStepTemplate not found with id: " + id);
        }
    }
    
    /**
     * 특정 승인 타입의 최대 단계 수 조회
     */
    @Transactional(readOnly = true)
    public Integer getMaxStepOrder(ApprovalType approvalType) {
        Integer maxStep = templateRepository.findMaxStepOrderByApprovalType(approvalType);
        return maxStep != null ? maxStep : 0;
    }
    
    /**
     * 모든 승인 단계 템플릿 조회
     */
    @Transactional(readOnly = true)
    public List<ApprovalStepTemplate> getAllTemplates() {
        return templateRepository.findAll();
    }
    
    /**
     * ID로 승인 단계 템플릿 조회
     */
    @Transactional(readOnly = true)
    public Optional<ApprovalStepTemplate> getTemplateById(Long id) {
        return templateRepository.findById(id);
    }
    
    /**
     * 승인 타입별 템플릿 초기화 (개발/테스트용)
     */
    public void initializeDefaultTemplates() {
        // ACCESS 타입의 기본 템플릿들 (데이터 접근)
        if (templateRepository.findByApprovalTypeAndIsActiveTrueOrderByStepOrder(ApprovalType.ACCESS).isEmpty()) {
            templateRepository.save(new ApprovalStepTemplate(ApprovalType.ACCESS, 1, "팀장", "데이터분석팀"));
            templateRepository.save(new ApprovalStepTemplate(ApprovalType.ACCESS, 2, "부장", "IT팀"));
            templateRepository.save(new ApprovalStepTemplate(ApprovalType.ACCESS, 3, "이사", "경영지원팀"));
        }
        
        // DEPLOY 타입의 기본 템플릿들 (배포/발행)
        if (templateRepository.findByApprovalTypeAndIsActiveTrueOrderByStepOrder(ApprovalType.DEPLOY).isEmpty()) {
            templateRepository.save(new ApprovalStepTemplate(ApprovalType.DEPLOY, 1, "팀장", "데이터분석팀"));
            templateRepository.save(new ApprovalStepTemplate(ApprovalType.DEPLOY, 2, "부장", "경영지원팀"));
        }
        
        // CREATE 타입의 기본 템플릿들 (생성)
        if (templateRepository.findByApprovalTypeAndIsActiveTrueOrderByStepOrder(ApprovalType.CREATE).isEmpty()) {
            templateRepository.save(new ApprovalStepTemplate(ApprovalType.CREATE, 1, "팀장", "데이터분석팀"));
        }
    }
    
    /**
     * 승인 타입별 템플릿 전체 교체
     */
    public List<ApprovalStepTemplate> replaceTemplatesForType(ApprovalType approvalType, List<ApprovalStepTemplate> newTemplates) {
        // 기존 템플릿들을 비활성화
        List<ApprovalStepTemplate> existingTemplates = templateRepository.findByApprovalTypeOrderByStepOrder(approvalType);
        for (ApprovalStepTemplate template : existingTemplates) {
            template.setIsActive(false);
            templateRepository.save(template);
        }
        
        // 새 템플릿들 저장
        for (int i = 0; i < newTemplates.size(); i++) {
            ApprovalStepTemplate template = newTemplates.get(i);
            template.setApprovalType(approvalType);
            template.setStepOrder(i + 1);
            template.setIsActive(true);
        }
        
        return templateRepository.saveAll(newTemplates);
    }
    
    /**
     * 승인 단계 템플릿 순서 변경
     */
    public void reorderTemplates(ApprovalType approvalType, List<Long> newOrderIds) {
        List<ApprovalStepTemplate> templates = templateRepository.findByApprovalTypeAndIsActiveTrueOrderByStepOrder(approvalType);
        
        // 새로운 순서로 단계 번호 재할당
        for (int i = 0; i < newOrderIds.size(); i++) {
            Long templateId = newOrderIds.get(i);
            ApprovalStepTemplate template = templates.stream()
                .filter(t -> t.getId().equals(templateId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ApprovalStepTemplate not found with id: " + templateId));
            
            template.setStepOrder(i + 1);
            templateRepository.save(template);
        }
    }
    
    /**
     * 승인 단계 템플릿 복사 (다른 승인 타입으로)
     */
    public List<ApprovalStepTemplate> copyTemplates(ApprovalType fromType, ApprovalType toType) {
        List<ApprovalStepTemplate> sourceTemplates = templateRepository.findByApprovalTypeAndIsActiveTrueOrderByStepOrder(fromType);
        
        List<ApprovalStepTemplate> copiedTemplates = sourceTemplates.stream()
            .map(source -> {
                ApprovalStepTemplate copy = new ApprovalStepTemplate();
                copy.setApprovalType(toType);
                copy.setStepOrder(source.getStepOrder());
                copy.setApproverRole(source.getApproverRole());
                copy.setApproverDepartment(source.getApproverDepartment());
                copy.setApproverId(source.getApproverId());
                copy.setApproverName(source.getApproverName());
                copy.setIsRequired(source.getIsRequired());
                copy.setIsActive(true);
                copy.setDescription(source.getDescription() + " (복사됨)");
                return copy;
            })
            .toList();
        
        return templateRepository.saveAll(copiedTemplates);
    }
    
    /**
     * 승인 단계 템플릿에 새 단계 추가
     */
    public ApprovalStepTemplate addTemplateStep(ApprovalType approvalType, Integer insertPosition, 
                                              String approverRole, String approverDepartment, 
                                              String approverId, String approverName, 
                                              Boolean isRequired, String description) {
        // 삽입 위치 이후의 모든 템플릿들의 단계 순서를 1씩 증가
        List<ApprovalStepTemplate> existingTemplates = templateRepository.findByApprovalTypeAndIsActiveTrueOrderByStepOrder(approvalType);
        
        for (ApprovalStepTemplate template : existingTemplates) {
            if (template.getStepOrder() >= insertPosition) {
                template.setStepOrder(template.getStepOrder() + 1);
                templateRepository.save(template);
            }
        }
        
        // 새 템플릿 생성
        ApprovalStepTemplate newTemplate = new ApprovalStepTemplate();
        newTemplate.setApprovalType(approvalType);
        newTemplate.setStepOrder(insertPosition);
        newTemplate.setApproverRole(approverRole);
        newTemplate.setApproverDepartment(approverDepartment);
        newTemplate.setApproverId(approverId != null && !approverId.trim().isEmpty() ? Long.parseLong(approverId) : null);
        newTemplate.setApproverName(approverName);
        newTemplate.setIsRequired(isRequired != null ? isRequired : true);
        newTemplate.setIsActive(true);
        newTemplate.setDescription(description);
        
        return templateRepository.save(newTemplate);
    }
    
    /**
     * 승인 단계 템플릿 단계 삭제
     */
    public void deleteTemplateStep(Long templateId) {
        ApprovalStepTemplate template = templateRepository.findById(templateId)
            .orElseThrow(() -> new RuntimeException("ApprovalStepTemplate not found with id: " + templateId));
        
        ApprovalType approvalType = template.getApprovalType();
        Integer deletedStepOrder = template.getStepOrder();
        
        // 템플릿 삭제
        templateRepository.delete(template);
        
        // 삭제된 단계 이후의 모든 템플릿들의 단계 순서를 1씩 감소
        List<ApprovalStepTemplate> remainingTemplates = templateRepository.findByApprovalTypeAndIsActiveTrueOrderByStepOrder(approvalType);
        for (ApprovalStepTemplate remainingTemplate : remainingTemplates) {
            if (remainingTemplate.getStepOrder() > deletedStepOrder) {
                remainingTemplate.setStepOrder(remainingTemplate.getStepOrder() - 1);
                templateRepository.save(remainingTemplate);
            }
        }
    }
    
    /**
     * 승인 타입별 템플릿 유효성 검증
     */
    public boolean validateTemplates(ApprovalType approvalType) {
        List<ApprovalStepTemplate> templates = templateRepository.findByApprovalTypeAndIsActiveTrueOrderByStepOrder(approvalType);
        
        if (templates.isEmpty()) {
            return false; // 템플릿이 없음
        }
        
        // 단계 순서가 연속적인지 확인
        for (int i = 0; i < templates.size(); i++) {
            if (!templates.get(i).getStepOrder().equals(i + 1)) {
                return false; // 단계 순서가 연속적이지 않음
            }
        }
        
        // 최소 하나의 필수 단계가 있는지 확인
        boolean hasRequiredStep = templates.stream().anyMatch(ApprovalStepTemplate::getIsRequired);
        
        return hasRequiredStep;
    }
}
