package com.lina.dataportal.controller;

import com.lina.dataportal.domain.api.ApiEndpoint;
import com.lina.dataportal.domain.catalog.DataTable;
import com.lina.dataportal.domain.dashboard.Dashboard;
import com.lina.dataportal.domain.model.MLModel;
import com.lina.dataportal.domain.report.Report;
import com.lina.dataportal.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
@CrossOrigin(origins = "*")
public class SearchController {
    
    private final DashboardService dashboardService;
    private final ReportService reportService;
    private final DataCatalogService dataCatalogService;
    private final MLModelService mlModelService;
    private final ApiService apiService;
    
    @Autowired
    public SearchController(DashboardService dashboardService,
                          ReportService reportService,
                          DataCatalogService dataCatalogService,
                          MLModelService mlModelService,
                          ApiService apiService) {
        this.dashboardService = dashboardService;
        this.reportService = reportService;
        this.dataCatalogService = dataCatalogService;
        this.mlModelService = mlModelService;
        this.apiService = apiService;
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> search(@RequestParam String q) {
        Map<String, Object> results = new HashMap<>();
        
        // 모든 도메인에서 검색
        List<Dashboard> dashboards = dashboardService.searchDashboards(q);
        List<Report> reports = reportService.searchReports(q);
        List<DataTable> tables = dataCatalogService.searchTables(q);
        List<MLModel> models = mlModelService.searchModels(q);
        List<ApiEndpoint> apis = apiService.searchEndpoints(q);
        
        results.put("dashboards", dashboards);
        results.put("reports", reports);
        results.put("tables", tables);
        results.put("models", models);
        results.put("apis", apis);
        
        // 검색 통계
        Map<String, Integer> stats = new HashMap<>();
        stats.put("dashboards", dashboards.size());
        stats.put("reports", reports.size());
        stats.put("tables", tables.size());
        stats.put("models", models.size());
        stats.put("apis", apis.size());
        stats.put("total", dashboards.size() + reports.size() + tables.size() + models.size() + apis.size());
        
        results.put("stats", stats);
        results.put("query", q);
        
        return ResponseEntity.ok(results);
    }
}