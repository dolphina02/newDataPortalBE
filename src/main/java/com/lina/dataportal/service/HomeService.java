package com.lina.dataportal.service;

import com.lina.dataportal.repository.*;
import com.lina.dataportal.domain.dashboard.Dashboard;
import com.lina.dataportal.domain.report.Report;
import com.lina.dataportal.domain.catalog.DataTable;
import com.lina.dataportal.domain.model.MLModel;
import com.lina.dataportal.domain.api.ApiEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HomeService {

    @Autowired
    private DashboardRepository dashboardRepository;
    
    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private DataTableRepository dataTableRepository;
    
    @Autowired
    private MLModelRepository mlModelRepository;
    
    @Autowired
    private ApiEndpointRepository apiEndpointRepository;

    public Map<String, Object> globalSearch(String query) {
        Map<String, Object> results = new HashMap<>();
        String lowerQuery = query.toLowerCase();
        
        long startTime = System.currentTimeMillis();
        
        // Search in each category using actual repositories
        results.put("dashboards", searchDashboards(lowerQuery));
        results.put("reports", searchReports(lowerQuery));
        results.put("tables", searchTables(lowerQuery));
        results.put("models", searchModels(lowerQuery));
        results.put("apis", searchApis(lowerQuery));
        
        // Calculate total results
        int totalResults = results.values().stream()
            .mapToInt(list -> ((List<?>) list).size())
            .sum();
        
        long searchTime = System.currentTimeMillis() - startTime;
        
        results.put("totalResults", totalResults);
        results.put("searchTime", searchTime);
        
        return results;
    }

    public List<Map<String, Object>> getRecentActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        // Get recent dashboards (최근 생성된 대시보드)
        List<Dashboard> recentDashboards = dashboardRepository.findTop3ByOrderByCreatedAtDesc();
        for (Dashboard dashboard : recentDashboards) {
            activities.add(Map.of(
                "id", dashboard.getId(),
                "icon", "dashboard",
                "type", "dashboard",
                "title", dashboard.getTitle() + " 대시보드 생성",
                "description", dashboard.getDescription(),
                "user", dashboard.getCreatedBy(),
                "time", getTimeAgo(java.sql.Timestamp.valueOf(dashboard.getCreatedAt())),
                "status", "success",
                "statusText", "완료"
            ));
        }
        
        // Get recent reports (최근 생성된 리포트)
        List<Report> recentReports = reportRepository.findTop3ByOrderByCreatedAtDesc();
        for (Report report : recentReports) {
            activities.add(Map.of(
                "id", report.getId(),
                "icon", "file",
                "type", "report",
                "title", report.getTitle() + " 리포트 생성",
                "description", report.getDescription(),
                "user", report.getCreatedBy(),
                "time", getTimeAgo(java.sql.Timestamp.valueOf(report.getCreatedAt())),
                "status", "success",
                "statusText", "완료"
            ));
        }
        
        // Sort by creation time and limit to 5
        return activities.stream()
            .sorted((a, b) -> b.get("id").toString().compareTo(a.get("id").toString()))
            .limit(5)
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getPopularDatasets() {
        // Get popular data tables based on record count (as proxy for popularity)
        List<DataTable> popularTables = dataTableRepository.findTop5ByOrderByRecordCountDesc();
        
        return popularTables.stream().map(table -> {
            Map<String, Object> dataset = new HashMap<>();
            dataset.put("id", table.getId());
            dataset.put("icon", "database");
            dataset.put("category", table.getCategory().toLowerCase());
            dataset.put("categoryName", getCategoryDisplayName(table.getCategory()));
            dataset.put("title", table.getTableName());
            dataset.put("description", table.getDescription());
            dataset.put("views", formatNumber(table.getRecordCount().intValue()));
            dataset.put("queries", formatNumber(table.getColumnCount()));
            dataset.put("updated", getTimeAgo(java.sql.Timestamp.valueOf(table.getLastUpdated())));
            dataset.put("tags", table.getTags() != null ? table.getTags() : Arrays.asList());
            return dataset;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getNewUpdates() {
        List<Map<String, Object>> updates = new ArrayList<>();
        
        // Get recent dashboards as updates
        List<Dashboard> recentDashboards = dashboardRepository.findTop2ByOrderByCreatedAtDesc();
        for (Dashboard dashboard : recentDashboards) {
            updates.add(Map.of(
                "id", dashboard.getId(),
                "type", "dashboard",
                "message", "새로운 \"" + dashboard.getTitle() + "\" 대시보드가 추가되었습니다",
                "time", getTimeAgo(java.sql.Timestamp.valueOf(dashboard.getCreatedAt())),
                "actionText", "확인하기"
            ));
        }
        
        // Get recent data tables as updates
        List<DataTable> recentTables = dataTableRepository.findTop2ByOrderByLastUpdatedDesc();
        for (DataTable table : recentTables) {
            updates.add(Map.of(
                "id", table.getId() + 1000, // offset to avoid ID conflicts
                "type", "data",
                "message", "\"" + table.getTableName() + "\" 데이터가 업데이트되었습니다",
                "time", getTimeAgo(java.sql.Timestamp.valueOf(table.getLastUpdated())),
                "actionText", "데이터 보기"
            ));
        }
        
        return updates.stream()
            .limit(5)
            .collect(Collectors.toList());
    }

    public void dismissUpdate(Long updateId) {
        // 업데이트 알림 해제 시뮬레이션
        System.out.println("Dismissed update with ID: " + updateId);
    }

    public Map<String, Object> getHomeStats() {
        return Map.of(
            "totalDashboards", dashboardRepository.count(),
            "totalReports", reportRepository.count(),
            "totalDatasets", dataTableRepository.count(),
            "totalModels", mlModelRepository.count(),
            "totalApis", apiEndpointRepository.count(),
            "systemHealth", "healthy",
            "lastUpdated", new Date()
        );
    }

    public Map<String, Object> createNewDashboard(String name, String description) {
        // 새 대시보드 생성 시뮬레이션
        Map<String, Object> newDashboard = new HashMap<>();
        newDashboard.put("id", System.currentTimeMillis());
        newDashboard.put("name", name);
        newDashboard.put("description", description);
        newDashboard.put("createdAt", new Date());
        newDashboard.put("status", "created");
        
        return Map.of(
            "success", true,
            "dashboard", newDashboard,
            "message", "Dashboard created successfully"
        );
    }

    public Map<String, Object> importData(String dataSource, String format) {
        // 데이터 가져오기 시뮬레이션
        return Map.of(
            "success", true,
            "importId", UUID.randomUUID().toString(),
            "dataSource", dataSource,
            "format", format,
            "status", "processing",
            "message", "Data import started successfully"
        );
    }

    public List<String> getSearchSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        // Get suggestions from dashboard titles
        List<Dashboard> dashboards = dashboardRepository.findByTitleContainingIgnoreCase(query);
        suggestions.addAll(dashboards.stream()
            .map(Dashboard::getTitle)
            .limit(3)
            .collect(Collectors.toList()));
        
        // Get suggestions from report titles
        List<Report> reports = reportRepository.findByTitleContainingIgnoreCase(query);
        suggestions.addAll(reports.stream()
            .map(Report::getTitle)
            .limit(3)
            .collect(Collectors.toList()));
        
        // Get suggestions from data table names
        List<DataTable> tables = dataTableRepository.findByTableNameContainingIgnoreCase(query);
        suggestions.addAll(tables.stream()
            .map(DataTable::getTableName)
            .limit(3)
            .collect(Collectors.toList()));
        
        return suggestions.stream()
            .distinct()
            .limit(5)
            .collect(Collectors.toList());
    }

    private List<Map<String, Object>> searchDashboards(String query) {
        List<Dashboard> dashboards = dashboardRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
        return dashboards.stream().map(dashboard -> Map.of(
            "id", dashboard.getId(),
            "title", dashboard.getTitle(),
            "description", dashboard.getDescription(),
            "tags", dashboard.getTags() != null ? dashboard.getTags() : Arrays.asList()
        )).collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> searchReports(String query) {
        List<Report> reports = reportRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
        return reports.stream().map(report -> Map.of(
            "id", report.getId(),
            "title", report.getTitle(),
            "description", report.getDescription(),
            "tags", report.getTags() != null ? report.getTags() : Arrays.asList()
        )).collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> searchTables(String query) {
        List<DataTable> tables = dataTableRepository.findByTableNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
        return tables.stream().map(table -> Map.of(
            "id", table.getId(),
            "title", table.getTableName(),
            "description", table.getDescription(),
            "tags", table.getTags() != null ? table.getTags() : Arrays.asList()
        )).collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> searchModels(String query) {
        List<MLModel> models = mlModelRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
        return models.stream().map(model -> Map.of(
            "id", model.getId(),
            "title", model.getName(),
            "description", model.getDescription(),
            "tags", model.getTags() != null ? model.getTags() : Arrays.asList()
        )).collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> searchApis(String query) {
        List<ApiEndpoint> apis = apiEndpointRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
        return apis.stream().map(api -> Map.of(
            "id", api.getId(),
            "title", api.getName(),
            "description", api.getDescription(),
            "tags", api.getTags() != null ? api.getTags() : Arrays.asList()
        )).collect(Collectors.toList());
    }
    
    private String getTimeAgo(Date date) {
        if (date == null) return "알 수 없음";
        
        long diff = System.currentTimeMillis() - date.getTime();
        long minutes = diff / (60 * 1000);
        long hours = diff / (60 * 60 * 1000);
        long days = diff / (24 * 60 * 60 * 1000);
        
        if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else {
            return days + "일 전";
        }
    }
    
    private String formatNumber(Integer number) {
        if (number == null) return "0";
        if (number >= 1000) {
            return String.format("%.1fk", number / 1000.0);
        }
        return number.toString();
    }
    
    private String getCategoryDisplayName(String category) {
        switch (category.toUpperCase()) {
            case "CUSTOMER": return "고객";
            case "POLICY": return "계약";
            case "CLAIMS": return "청구";
            case "SALES": return "매출";
            case "FINANCE": return "금융";
            case "UNDERWRITING": return "언더라이팅";
            case "KPI": return "KPI";
            case "OPERATION": return "운영";
            default: return category;
        }
    }
}