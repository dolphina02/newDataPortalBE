package com.lina.dataportal.controller;

import com.lina.dataportal.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
@CrossOrigin(origins = "*")
@Tag(name = "Home", description = "홈 화면 관련 API - 검색, 최근 활동, 인기 데이터셋 등")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @Operation(
        summary = "전역 검색", 
        description = "대시보드, 리포트, 데이터 테이블, ML 모델, API 등 전체 카테고리에서 통합 검색"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "검색 성공",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "dashboards": [{"id": 1, "title": "보험 KPI 대시보드", "description": "..."}],
                        "reports": [{"id": 1, "title": "월간 리포트", "description": "..."}],
                        "totalResults": 15,
                        "searchTime": 250
                    }
                """)))
    })
    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> globalSearch(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "검색 쿼리",
            content = @Content(
                examples = @ExampleObject(value = "{\"query\": \"보험금 청구\"}")
            )
        )
        @RequestBody Map<String, Object> request) {
        String query = (String) request.get("query");
        
        Map<String, Object> results = homeService.globalSearch(query);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "최근 활동 조회", description = "최근 생성된 대시보드, 리포트 등의 활동 내역")
    @GetMapping("/recent-activities")
    public ResponseEntity<List<Map<String, Object>>> getRecentActivities() {
        List<Map<String, Object>> activities = homeService.getRecentActivities();
        return ResponseEntity.ok(activities);
    }

    @Operation(summary = "인기 데이터셋 조회", description = "조회수 기준 인기 데이터 테이블 목록")
    @GetMapping("/popular-datasets")
    public ResponseEntity<List<Map<String, Object>>> getPopularDatasets() {
        List<Map<String, Object>> datasets = homeService.getPopularDatasets();
        return ResponseEntity.ok(datasets);
    }

    @Operation(summary = "새로운 업데이트 조회", description = "최근 업데이트된 대시보드 및 데이터 알림")
    @GetMapping("/updates")
    public ResponseEntity<List<Map<String, Object>>> getNewUpdates() {
        List<Map<String, Object>> updates = homeService.getNewUpdates();
        return ResponseEntity.ok(updates);
    }

    @PostMapping("/updates/{updateId}/dismiss")
    public ResponseEntity<Void> dismissUpdate(@PathVariable Long updateId) {
        homeService.dismissUpdate(updateId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getHomeStats() {
        Map<String, Object> stats = homeService.getHomeStats();
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/quick-actions/dashboard")
    public ResponseEntity<Map<String, Object>> createNewDashboard(@RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        String description = (String) request.get("description");
        
        Map<String, Object> result = homeService.createNewDashboard(name, description);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/quick-actions/import-data")
    public ResponseEntity<Map<String, Object>> importData(@RequestBody Map<String, Object> request) {
        String dataSource = (String) request.get("dataSource");
        String format = (String) request.get("format");
        
        Map<String, Object> result = homeService.importData(dataSource, format);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "검색 제안", description = "입력된 쿼리에 대한 자동완성 검색 제안")
    @GetMapping("/search-suggestions")
    public ResponseEntity<List<String>> getSearchSuggestions(
        @Parameter(description = "검색 쿼리", example = "보험") 
        @RequestParam String query) {
        List<String> suggestions = homeService.getSearchSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }
}