package com.lina.dataportal.controller;

import com.lina.dataportal.domain.approval.*;
import com.lina.dataportal.domain.dashboard.DashboardType;
import com.lina.dataportal.domain.report.ReportType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 프론트엔드에서 사용할 Enum 정보를 제공하는 컨트롤러
 * UI 라벨 매핑은 프론트엔드에서 처리
 */
@RestController
@RequestMapping("/api/enums")
@CrossOrigin(origins = "*")
public class EnumController {
    
    /**
     * 모든 Enum 정보를 한 번에 조회
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllEnums() {
        Map<String, Object> enums = new HashMap<>();
        enums.put("approvalTypes", getApprovalTypes());
        enums.put("approvalStatuses", getApprovalStatuses());
        enums.put("approvalLineStatuses", getApprovalLineStatuses());
        enums.put("priorities", getPriorities());
        enums.put("targetTypes", getTargetTypes());
        enums.put("accessScopes", getAccessScopes());
        enums.put("sensitivityLevels", getSensitivityLevels());
        enums.put("dashboardTypes", getDashboardTypes());
        enums.put("reportTypes", getReportTypes());
        
        return ResponseEntity.ok(enums);
    }
    
    /**
     * 승인 타입 목록 조회
     */
    @GetMapping("/approval-types")
    public ResponseEntity<List<String>> getApprovalTypes() {
        List<String> types = Arrays.stream(ApprovalType.values())
            .map(Enum::name)
            .collect(Collectors.toList());
        return ResponseEntity.ok(types);
    }
    
    /**
     * 승인 상태 목록 조회
     */
    @GetMapping("/approval-statuses")
    public ResponseEntity<List<String>> getApprovalStatuses() {
        List<String> statuses = Arrays.stream(ApprovalStatus.values())
            .map(Enum::name)
            .collect(Collectors.toList());
        return ResponseEntity.ok(statuses);
    }
    
    /**
     * 승인 라인 상태 목록 조회
     */
    @GetMapping("/approval-line-statuses")
    public ResponseEntity<List<String>> getApprovalLineStatuses() {
        List<String> statuses = Arrays.stream(ApprovalLineStatus.values())
            .map(Enum::name)
            .collect(Collectors.toList());
        return ResponseEntity.ok(statuses);
    }
    
    /**
     * 우선순위 목록 조회
     */
    @GetMapping("/priorities")
    public ResponseEntity<List<String>> getPriorities() {
        List<String> priorities = Arrays.stream(Priority.values())
            .map(Enum::name)
            .collect(Collectors.toList());
        return ResponseEntity.ok(priorities);
    }
    
    /**
     * 대시보드 타입 목록 조회
     */
    @GetMapping("/dashboard-types")
    public ResponseEntity<List<String>> getDashboardTypes() {
        List<String> types = Arrays.stream(DashboardType.values())
            .map(Enum::name)
            .collect(Collectors.toList());
        return ResponseEntity.ok(types);
    }
    
    /**
     * 보고서 타입 목록 조회
     */
    @GetMapping("/report-types")
    public ResponseEntity<List<String>> getReportTypes() {
        List<String> types = Arrays.stream(ReportType.values())
            .map(Enum::name)
            .collect(Collectors.toList());
        return ResponseEntity.ok(types);
    }
    
    // ========== 승인 대상 오브젝트 관련 Enum API ==========
    
    /**
     * 승인 대상 타입 목록 조회
     */
    @GetMapping("/target-types")
    public ResponseEntity<List<Map<String, Object>>> getTargetTypes() {
        List<Map<String, Object>> types = Arrays.stream(TargetType.values())
            .map(type -> {
                Map<String, Object> typeMap = new HashMap<>();
                typeMap.put("name", type.name());
                typeMap.put("displayName", type.getDisplayName());
                typeMap.put("description", type.getDescription());
                typeMap.put("isDataType", type.isDataType());
                typeMap.put("isAnalyticsType", type.isAnalyticsType());
                typeMap.put("isInfraType", type.isInfraType());
                typeMap.put("isSecurityType", type.isSecurityType());
                typeMap.put("isMlType", type.isMlType());
                return typeMap;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(types);
    }
    
    /**
     * 접근 범위 목록 조회
     */
    @GetMapping("/access-scopes")
    public ResponseEntity<List<Map<String, Object>>> getAccessScopes() {
        List<Map<String, Object>> scopes = Arrays.stream(AccessScope.values())
            .map(scope -> {
                Map<String, Object> scopeMap = new HashMap<>();
                scopeMap.put("name", scope.name());
                scopeMap.put("displayName", scope.getDisplayName());
                scopeMap.put("description", scope.getDescription());
                scopeMap.put("riskLevel", scope.getRiskLevel());
                scopeMap.put("includesRead", scope.includesRead());
                scopeMap.put("includesWrite", scope.includesWrite());
                scopeMap.put("isHighRisk", scope.isHighRisk());
                scopeMap.put("isTemporary", scope.isTemporary());
                scopeMap.put("requiresMasking", scope.requiresMasking());
                return scopeMap;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(scopes);
    }
    
    /**
     * 민감도 레벨 목록 조회
     */
    @GetMapping("/sensitivity-levels")
    public ResponseEntity<List<Map<String, Object>>> getSensitivityLevels() {
        List<Map<String, Object>> levels = Arrays.stream(SensitivityLevel.values())
            .map(level -> {
                Map<String, Object> levelMap = new HashMap<>();
                levelMap.put("name", level.name());
                levelMap.put("displayName", level.getDisplayName());
                levelMap.put("description", level.getDescription());
                levelMap.put("level", level.getLevel());
                levelMap.put("requiresSpecialHandling", level.requiresSpecialHandling());
                levelMap.put("isHighSensitive", level.isHighSensitive());
                levelMap.put("isCriticalSensitive", level.isCriticalSensitive());
                levelMap.put("isPersonalData", level.isPersonalData());
                levelMap.put("isRegulated", level.isRegulated());
                levelMap.put("requiresMasking", level.requiresMasking());
                levelMap.put("requiresAuditLog", level.requiresAuditLog());
                levelMap.put("requiresAdditionalApproval", level.requiresAdditionalApproval());
                return levelMap;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(levels);
    }
    
    /**
     * 특정 대상 타입에 적합한 접근 범위 조회
     */
    @GetMapping("/target-types/{targetType}/access-scopes")
    public ResponseEntity<List<Map<String, Object>>> getAccessScopesForTargetType(@PathVariable String targetType) {
        try {
            TargetType type = TargetType.valueOf(targetType.toUpperCase());
            
            // 대상 타입에 따른 적합한 접근 범위 필터링
            List<Map<String, Object>> scopes = Arrays.stream(AccessScope.values())
                .filter(scope -> isAccessScopeApplicable(type, scope))
                .map(scope -> {
                    Map<String, Object> scopeMap = new HashMap<>();
                    scopeMap.put("name", scope.name());
                    scopeMap.put("displayName", scope.getDisplayName());
                    scopeMap.put("description", scope.getDescription());
                    scopeMap.put("riskLevel", scope.getRiskLevel());
                    return scopeMap;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(scopes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 특정 민감도 레벨에 권장되는 접근 범위 조회
     */
    @GetMapping("/sensitivity-levels/{sensitivityLevel}/recommended-access-scopes")
    public ResponseEntity<List<Map<String, Object>>> getRecommendedAccessScopes(@PathVariable String sensitivityLevel) {
        try {
            SensitivityLevel level = SensitivityLevel.valueOf(sensitivityLevel.toUpperCase());
            
            // 민감도 레벨에 따른 권장 접근 범위
            List<Map<String, Object>> scopes = Arrays.stream(AccessScope.values())
                .filter(scope -> isAccessScopeRecommended(level, scope))
                .map(scope -> {
                    Map<String, Object> scopeMap = new HashMap<>();
                    scopeMap.put("name", scope.name());
                    scopeMap.put("displayName", scope.getDisplayName());
                    scopeMap.put("description", scope.getDescription());
                    scopeMap.put("riskLevel", scope.getRiskLevel());
                    scopeMap.put("recommended", true);
                    return scopeMap;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(scopes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Helper methods
    private boolean isAccessScopeApplicable(TargetType targetType, AccessScope accessScope) {
        // 데이터 타입에는 모든 접근 범위 적용 가능
        if (targetType.isDataType()) {
            return true;
        }
        
        // 분석 타입에는 읽기, 공유, 내보내기 주로 적용
        if (targetType.isAnalyticsType()) {
            return accessScope.includesRead() || accessScope == AccessScope.SHARE || accessScope == AccessScope.EXPORT;
        }
        
        // 인프라 타입에는 실행, 배포 권한 주로 적용
        if (targetType.isInfraType()) {
            return accessScope == AccessScope.EXECUTE || accessScope == AccessScope.DEPLOY || accessScope == AccessScope.ADMIN;
        }
        
        // 보안 타입에는 관리 권한 주로 적용
        if (targetType.isSecurityType()) {
            return accessScope == AccessScope.READ || accessScope == AccessScope.ADMIN || accessScope == AccessScope.OWNER;
        }
        
        return true; // 기본적으로 모든 접근 범위 허용
    }
    
    private boolean isAccessScopeRecommended(SensitivityLevel sensitivityLevel, AccessScope accessScope) {
        // 고민감도 데이터에는 제한적 접근만 권장
        if (sensitivityLevel.isHighSensitive()) {
            return accessScope == AccessScope.VIEW_ONLY || 
                   accessScope == AccessScope.MASKED_READ || 
                   accessScope == AccessScope.TEMPORARY_READ;
        }
        
        // 개인정보에는 마스킹된 접근 권장
        if (sensitivityLevel.isPersonalData()) {
            return accessScope == AccessScope.MASKED_READ || 
                   accessScope == AccessScope.VIEW_ONLY;
        }
        
        // 일반 데이터에는 기본 접근 권한 권장
        return accessScope == AccessScope.READ || 
               accessScope == AccessScope.WRITE || 
               accessScope == AccessScope.SHARE;
    }
    

    
    /**
     * 민감도 카테고리 목록 조회
     * GET /enums/sensitivity-categories
     */
    @GetMapping("/sensitivity-categories")
    public ResponseEntity<List<Map<String, Object>>> getSensitivityCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();
        
        for (SensitivityCategory category : SensitivityCategory.values()) {
            Map<String, Object> categoryInfo = new HashMap<>();
            categoryInfo.put("name", category.name());
            categoryInfo.put("displayName", category.getDisplayName());
            categoryInfo.put("code", category.getCode());
            categoryInfo.put("description", category.getDescription());
            categoryInfo.put("defaultUsageDays", category.getDefaultUsageDays());
            
            // 해당 카테고리에 속하는 레벨들
            List<String> levels = new ArrayList<>();
            for (SensitivityLevel level : SensitivityLevel.values()) {
                if (level.getCategory() == category) {
                    levels.add(level.name());
                }
            }
            categoryInfo.put("levels", levels);
            
            categories.add(categoryInfo);
        }
        
        return ResponseEntity.ok(categories);
    }
    
    /**
     * 특정 민감도 레벨의 상세 정보 조회
     * GET /enums/sensitivity-levels/{level}
     */
    @GetMapping("/sensitivity-levels/{level}")
    public ResponseEntity<Map<String, Object>> getSensitivityLevelDetail(
            @PathVariable SensitivityLevel level) {
        
        Map<String, Object> levelInfo = new HashMap<>();
        levelInfo.put("name", level.name());
        levelInfo.put("displayName", level.getDisplayName());
        levelInfo.put("code", level.getCode());
        levelInfo.put("category", level.getCategory().name());
        levelInfo.put("categoryDisplayName", level.getCategory().getDisplayName());
        levelInfo.put("level", level.getLevel());
        levelInfo.put("description", level.getDescription());
        levelInfo.put("defaultUsageDays", level.getDefaultUsageDays());
        levelInfo.put("maxAllowedUsageDays", level.getMaxAllowedUsageDays());
        levelInfo.put("requiresAdditionalApproval", level.requiresAdditionalApproval());
        levelInfo.put("requiresMasking", level.requiresMasking());
        levelInfo.put("requiresAuditLog", level.requiresAuditLog());
        levelInfo.put("securityRequirements", level.getSecurityRequirements());
        
        // 특성 정보
        Map<String, Boolean> characteristics = new HashMap<>();
        characteristics.put("isNormal", level.isNormal());
        characteristics.put("isSensitive", level.isSensitive());
        characteristics.put("isHighSensitive", level.isHighSensitive());
        characteristics.put("isPersonalData", level.isPersonalData());
        characteristics.put("isRegulated", level.isRegulated());
        levelInfo.put("characteristics", characteristics);
        
        // 비교 정보 (다른 레벨들과의 관계)
        Map<String, Object> comparisons = new HashMap<>();
        List<String> moreSensitive = new ArrayList<>();
        List<String> lessSensitive = new ArrayList<>();
        
        for (SensitivityLevel other : SensitivityLevel.values()) {
            if (level.isMoreSensitiveThan(other)) {
                lessSensitive.add(other.name());
            } else if (level.isLessSensitiveThan(other)) {
                moreSensitive.add(other.name());
            }
        }
        
        comparisons.put("moreSensitiveLevels", moreSensitive);
        comparisons.put("lessSensitiveLevels", lessSensitive);
        levelInfo.put("comparisons", comparisons);
        
        return ResponseEntity.ok(levelInfo);
    }
    
    /**
     * 민감도 레벨별 권장 접근 범위 조회
     * GET /enums/sensitivity-levels/{level}/recommended-access-scopes
     */
    @GetMapping("/sensitivity-levels/{level}/recommended-access-scopes")
    public ResponseEntity<List<String>> getRecommendedAccessScopes(
            @PathVariable SensitivityLevel level) {
        
        List<String> recommendedScopes = new ArrayList<>();
        
        if (level.isNormal()) {
            // 일반 정보는 대부분의 접근 범위 허용
            recommendedScopes.addAll(Arrays.asList(
                "READ", "WRITE", "EXPORT", "SHARE", "VIEW_ONLY"
            ));
        } else if (level.isSensitive()) {
            if (level.isHighSensitive()) {
                // 고민감도는 제한적 접근만 허용
                recommendedScopes.addAll(Arrays.asList(
                    "MASKED_READ", "VIEW_ONLY"
                ));
            } else {
                // 일반 민감정보는 마스킹된 접근 위주
                recommendedScopes.addAll(Arrays.asList(
                    "READ", "MASKED_READ", "VIEW_ONLY", "EXPORT"
                ));
            }
        }
        
        // 개인정보는 특별 제한
        if (level.isPersonalData()) {
            recommendedScopes.clear();
            recommendedScopes.addAll(Arrays.asList(
                "MASKED_READ", "VIEW_ONLY"
            ));
        }
        
        return ResponseEntity.ok(recommendedScopes);
    }
    
    /**
     * ApprovalType과 TargetType 호환성 매트릭스 조회
     * GET /enums/approval-target-compatibility
     */
    @GetMapping("/approval-target-compatibility")
    public ResponseEntity<Map<String, Object>> getApprovalTargetCompatibility() {
        Map<String, Object> compatibility = new HashMap<>();
        
        for (ApprovalType approvalType : ApprovalType.values()) {
            Map<String, Object> approvalInfo = new HashMap<>();
            approvalInfo.put("displayName", approvalType.getDisplayName());
            approvalInfo.put("description", approvalType.getDescription());
            approvalInfo.put("isReadOnly", approvalType.isReadOnlyAction());
            approvalInfo.put("isWrite", approvalType.isWriteAction());
            approvalInfo.put("isHighRisk", approvalType.isHighRiskAction());
            approvalInfo.put("isDataExposureRisk", approvalType.isDataExposureRisk());
            
            // 호환되는 TargetType 목록
            List<String> compatibleTargets = new ArrayList<>();
            for (TargetType targetType : TargetType.values()) {
                if (approvalType.isCompatibleWith(targetType)) {
                    compatibleTargets.add(targetType.name());
                }
            }
            approvalInfo.put("compatibleTargets", compatibleTargets);
            
            compatibility.put(approvalType.name(), approvalInfo);
        }
        
        return ResponseEntity.ok(compatibility);
    }
    
    /**
     * 특정 TargetType에 권장되는 ApprovalType 목록 조회
     * GET /enums/target-types/{targetType}/recommended-approval-types
     */
    @GetMapping("/target-types/{targetType}/recommended-approval-types")
    public ResponseEntity<List<Map<String, Object>>> getRecommendedApprovalTypes(@PathVariable String targetType) {
        try {
            TargetType target = TargetType.valueOf(targetType.toUpperCase());
            
            List<Map<String, Object>> recommendedTypes = Arrays.stream(target.getRecommendedApprovalTypes())
                .map(approvalType -> {
                    Map<String, Object> typeInfo = new HashMap<>();
                    typeInfo.put("name", approvalType.name());
                    typeInfo.put("displayName", approvalType.getDisplayName());
                    typeInfo.put("description", approvalType.getDescription());
                    typeInfo.put("isHighRisk", approvalType.isHighRiskAction());
                    typeInfo.put("isDataExposureRisk", approvalType.isDataExposureRisk());
                    return typeInfo;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(recommendedTypes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * ApprovalType과 TargetType 조합의 위험도 평가
     * GET /enums/risk-assessment?approvalType={type}&targetType={target}&sensitivityLevel={level}&accessScope={scope}
     */
    @GetMapping("/risk-assessment")
    public ResponseEntity<Map<String, Object>> getRiskAssessment(
            @RequestParam String approvalType,
            @RequestParam String targetType,
            @RequestParam(required = false) String sensitivityLevel,
            @RequestParam(required = false) String accessScope) {
        
        try {
            ApprovalType approval = ApprovalType.valueOf(approvalType.toUpperCase());
            TargetType target = TargetType.valueOf(targetType.toUpperCase());
            
            Map<String, Object> assessment = new HashMap<>();
            assessment.put("compatible", approval.isCompatibleWith(target));
            assessment.put("approvalTypeRisk", approval.isHighRiskAction());
            assessment.put("dataExposureRisk", approval.isDataExposureRisk());
            assessment.put("targetSensitive", target.isSecurityType() || target.isDataType());
            
            // 조합 설명
            assessment.put("actionDescription", String.format("%s %s", 
                target.getDisplayName(), approval.getDisplayName()));
            
            // 권장 사항
            List<String> recommendations = new ArrayList<>();
            if (approval.isHighRiskAction()) {
                recommendations.add("고위험 액션으로 추가 승인 단계 필요");
            }
            if (approval.isDataExposureRisk() && target.isDataType()) {
                recommendations.add("데이터 유출 위험으로 마스킹 정책 적용 권장");
            }
            if (!approval.isCompatibleWith(target)) {
                recommendations.add("호환되지 않는 조합입니다");
            }
            assessment.put("recommendations", recommendations);
            
            return ResponseEntity.ok(assessment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}