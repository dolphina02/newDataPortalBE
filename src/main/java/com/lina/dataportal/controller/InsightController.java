package com.lina.dataportal.controller;

import com.lina.dataportal.service.InsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/insight")
@CrossOrigin(origins = "*")
public class InsightController {

    @Autowired
    private InsightService insightService;

    @GetMapping("/producer360")
    public ResponseEntity<Map<String, Object>> getProducer360Data(
            @RequestParam(defaultValue = "all") String scope,
            @RequestParam(defaultValue = "2025년 9월") String month) {
        
        Map<String, Object> data = insightService.getProducer360Data(scope, month);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/kpis")
    public ResponseEntity<Map<String, Object>> getKPIs(
            @RequestParam(defaultValue = "all") String scope,
            @RequestParam(defaultValue = "2025년 9월") String month) {
        
        Map<String, Object> kpis = insightService.getKPIs(scope, month);
        return ResponseEntity.ok(kpis);
    }

    @GetMapping("/performance-trends")
    public ResponseEntity<Map<String, Object>> getPerformanceTrends(
            @RequestParam(defaultValue = "all") String scope,
            @RequestParam(defaultValue = "2025년 9월") String month) {
        
        Map<String, Object> trends = insightService.getPerformanceTrends(scope, month);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/branch-management")
    public ResponseEntity<Map<String, Object>> getBranchManagement(
            @RequestParam(defaultValue = "all") String scope,
            @RequestParam(defaultValue = "2025년 9월") String month) {
        
        Map<String, Object> branchData = insightService.getBranchManagement(scope, month);
        return ResponseEntity.ok(branchData);
    }

    @GetMapping("/risk-opportunities")
    public ResponseEntity<Map<String, Object>> getRiskOpportunities(
            @RequestParam(defaultValue = "all") String scope) {
        
        Map<String, Object> riskData = insightService.getRiskOpportunities(scope);
        return ResponseEntity.ok(riskData);
    }

    @PostMapping("/export-pdf")
    public ResponseEntity<Map<String, Object>> exportWeeklySummaryPDF(
            @RequestBody Map<String, Object> request) {
        
        String scope = (String) request.get("scope");
        String month = (String) request.get("month");
        
        Map<String, Object> result = insightService.exportWeeklySummaryPDF(scope, month);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/chart-data/{chartType}")
    public ResponseEntity<Map<String, Object>> getChartData(
            @PathVariable String chartType,
            @RequestParam(defaultValue = "all") String scope,
            @RequestParam(defaultValue = "2025년 9월") String month) {
        
        Map<String, Object> chartData = insightService.getChartData(chartType, scope, month);
        return ResponseEntity.ok(chartData);
    }
}