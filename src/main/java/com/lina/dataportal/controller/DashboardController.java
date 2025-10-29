package com.lina.dataportal.controller;

import com.lina.dataportal.domain.dashboard.Dashboard;
import com.lina.dataportal.domain.dashboard.DashboardType;
import com.lina.dataportal.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboards")
@CrossOrigin(origins = "*")
@Tag(name = "Dashboard", description = "대시보드 관리 API - 대시보드 스토어, 생성, 수정, 삭제")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    
    @GetMapping
    public ResponseEntity<List<Dashboard>> getAllDashboards(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        
        List<Dashboard> dashboards;
        
        if (search != null && !search.trim().isEmpty()) {
            dashboards = dashboardService.searchDashboards(search);
        } else if (type != null) {
            DashboardType dashboardType = DashboardType.valueOf(type.toUpperCase());
            dashboards = dashboardService.getDashboardsByType(dashboardType);
        } else if (category != null) {
            dashboards = dashboardService.getDashboardsByCategory(category);
        } else {
            dashboards = dashboardService.getAllDashboards();
        }
        
        return ResponseEntity.ok(dashboards);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Dashboard> getDashboardById(@PathVariable Long id) {
        return dashboardService.getDashboardById(id)
            .map(dashboard -> ResponseEntity.ok(dashboard))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Dashboard> createDashboard(@Valid @RequestBody Dashboard dashboard) {
        Dashboard createdDashboard = dashboardService.createDashboard(dashboard);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDashboard);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Dashboard> updateDashboard(
            @PathVariable Long id, 
            @Valid @RequestBody Dashboard dashboardDetails) {
        try {
            Dashboard updatedDashboard = dashboardService.updateDashboard(id, dashboardDetails);
            return ResponseEntity.ok(updatedDashboard);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/install")
    public ResponseEntity<Dashboard> installDashboard(@PathVariable Long id) {
        try {
            Dashboard dashboard = dashboardService.installDashboard(id);
            return ResponseEntity.ok(dashboard);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/rating")
    public ResponseEntity<Dashboard> rateDashboard(
            @PathVariable Long id, 
            @RequestBody Map<String, Double> ratingRequest) {
        try {
            Double rating = ratingRequest.get("rating");
            if (rating == null || rating < 0 || rating > 5) {
                return ResponseEntity.badRequest().build();
            }
            
            Dashboard dashboard = dashboardService.rateDashboard(id, rating);
            return ResponseEntity.ok(dashboard);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDashboard(@PathVariable Long id) {
        try {
            dashboardService.deleteDashboard(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/popular")
    public ResponseEntity<List<Dashboard>> getPopularDashboards() {
        List<Dashboard> dashboards = dashboardService.getPopularDashboards();
        return ResponseEntity.ok(dashboards);
    }
    
    @GetMapping("/top-rated")
    public ResponseEntity<List<Dashboard>> getTopRatedDashboards() {
        List<Dashboard> dashboards = dashboardService.getTopRatedDashboards();
        return ResponseEntity.ok(dashboards);
    }
}