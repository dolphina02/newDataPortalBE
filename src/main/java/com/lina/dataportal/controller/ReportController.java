package com.lina.dataportal.controller;

import com.lina.dataportal.domain.report.Report;
import com.lina.dataportal.domain.report.ReportType;
import com.lina.dataportal.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {
    
    private final ReportService reportService;
    
    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    
    @GetMapping
    public ResponseEntity<List<Report>> getAllReports(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        
        List<Report> reports;
        
        if (search != null && !search.trim().isEmpty()) {
            reports = reportService.searchReports(search);
        } else if (type != null) {
            ReportType reportType = ReportType.valueOf(type.toUpperCase());
            reports = reportService.getReportsByType(reportType);
        } else if (category != null) {
            reports = reportService.getReportsByCategory(category);
        } else {
            reports = reportService.getAllReports();
        }
        
        return ResponseEntity.ok(reports);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        return reportService.getReportById(id)
            .map(report -> ResponseEntity.ok(report))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<Report>> getRecentReports() {
        List<Report> reports = reportService.getRecentReports();
        return ResponseEntity.ok(reports);
    }
    
    @PostMapping
    public ResponseEntity<Report> createReport(@Valid @RequestBody Report report) {
        Report createdReport = reportService.createReport(report);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReport);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Report> updateReport(
            @PathVariable Long id, 
            @Valid @RequestBody Report reportDetails) {
        try {
            Report updatedReport = reportService.updateReport(id, reportDetails);
            return ResponseEntity.ok(updatedReport);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        try {
            reportService.deleteReport(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // SearchView.vue 화면을 위한 추가 API들
    
    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, Object>>> getCategories() {
        List<Map<String, Object>> categories = reportService.getCategories();
        return ResponseEntity.ok(categories);
    }
    
    @PostMapping("/{id}/download")
    public ResponseEntity<Map<String, Object>> downloadReport(@PathVariable Long id) {
        Map<String, Object> downloadInfo = reportService.downloadReport(id);
        return ResponseEntity.ok(downloadInfo);
    }
    
    @PostMapping("/{id}/share")
    public ResponseEntity<Map<String, Object>> shareReport(
            @PathVariable Long id,
            @RequestBody Map<String, Object> shareRequest) {
        
        String email = (String) shareRequest.get("email");
        String message = (String) shareRequest.get("message");
        
        Map<String, Object> result = reportService.shareReport(id, email, message);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}/content")
    public ResponseEntity<Map<String, Object>> getReportContent(@PathVariable Long id) {
        Map<String, Object> content = reportService.getReportContent(id);
        return ResponseEntity.ok(content);
    }
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadReport(@RequestBody Map<String, Object> request) {
        String title = (String) request.get("title");
        String description = (String) request.get("description");
        String category = (String) request.get("category");
        String fileUrl = (String) request.get("fileUrl");
        
        Map<String, Object> result = reportService.uploadReport(title, description, category, fileUrl);
        return ResponseEntity.ok(result);
    }
}