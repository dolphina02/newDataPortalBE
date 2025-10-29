package com.lina.dataportal.controller;

import com.lina.dataportal.config.DatabricksConfig;
import com.lina.dataportal.service.QueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
@Tag(name = "Test", description = "API 테스트용 엔드포인트")
public class TestController {
    
    @Autowired(required = false)
    private DatabricksConfig databricksConfig;
    
    @Autowired(required = false)
    private QueryService queryService;

    @Operation(
        summary = "헬스 체크", 
        description = "서버 상태 및 기본 정보 확인"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "서버 정상 작동",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "status": "OK",
                        "message": "LINA Data Portal API Server is running",
                        "timestamp": "2024-01-20T10:30:00",
                        "version": "1.0.0"
                    }
                """)))
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "LINA Data Portal API Server is running");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "에코 테스트", 
        description = "입력된 메시지를 그대로 반환하는 테스트 API"
    )
    @PostMapping("/echo")
    public ResponseEntity<Map<String, Object>> echo(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "에코할 메시지",
            content = @Content(
                examples = @ExampleObject(value = "{\"message\": \"Hello LINA Data Portal!\"}")
            )
        )
        @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("echo", request.get("message"));
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "파라미터 테스트", 
        description = "쿼리 파라미터와 패스 변수 테스트"
    )
    @GetMapping("/params/{id}")
    public ResponseEntity<Map<String, Object>> testParams(
        @Parameter(description = "테스트 ID", example = "123") 
        @PathVariable String id,
        @Parameter(description = "테스트 이름", example = "test-name") 
        @RequestParam(required = false) String name,
        @Parameter(description = "활성 여부", example = "true") 
        @RequestParam(defaultValue = "true") boolean active) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("name", name);
        response.put("active", active);
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "데이터베이스 연결 테스트", 
        description = "데이터베이스 연결 상태 확인 (실제로는 시뮬레이션)"
    )
    @GetMapping("/db-status")
    public ResponseEntity<Map<String, Object>> databaseStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("database", "PostgreSQL");
        response.put("status", "Connected");
        response.put("host", "neon.tech");
        response.put("connectionPool", "Active");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Databricks 설정 확인", 
        description = "현재 Databricks 설정 정보 조회"
    )
    @GetMapping("/databricks-config")
    public ResponseEntity<Map<String, Object>> getDatabricksConfig() {
        Map<String, Object> config = new HashMap<>();
        
        if (databricksConfig != null) {
            config.put("workspaceUrl", databricksConfig.getDefault().getWorkspaceUrl());
            config.put("warehouseId", databricksConfig.getDefault().getWarehouseId());
            config.put("hasAccessToken", databricksConfig.getDefault().getAccessToken() != null && 
                                        !databricksConfig.getDefault().getAccessToken().isEmpty());
            config.put("httpPath", databricksConfig.getDefault().getHttpPath());
            config.put("apiTimeout", databricksConfig.getApi().getTimeout());
            config.put("maxRows", databricksConfig.getSql().getMaxRows());
        } else {
            config.put("error", "DatabricksConfig not loaded");
        }
        
        config.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(config);
    }
    
    @Operation(
        summary = "Databricks 연결 테스트", 
        description = "Databricks에 간단한 쿼리를 실행하여 연결 상태 확인"
    )
    @PostMapping("/databricks-test")
    public ResponseEntity<Map<String, Object>> testDatabricksConnection() {
        Map<String, Object> response = new HashMap<>();
        
        if (queryService == null) {
            response.put("connectionTest", false);
            response.put("error", "QueryService not available");
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(response);
        }
        
        try {
            // 간단한 테스트 쿼리 실행
            Map<String, Object> result = queryService.executeQuery("SELECT 1 as test_column", "test_user");
            
            response.put("connectionTest", result.get("success"));
            response.put("executionTime", result.get("executionTime"));
            response.put("rowCount", result.get("rowCount"));
            response.put("timestamp", LocalDateTime.now());
            
            if (!(Boolean) result.get("success")) {
                response.put("error", result.get("error"));
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("connectionTest", false);
            response.put("error", "Databricks connection test failed: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @Operation(
        summary = "등록된 엔드포인트 확인", 
        description = "현재 등록된 모든 엔드포인트 목록 조회"
    )
    @GetMapping("/endpoints")
    public ResponseEntity<Map<String, Object>> getEndpoints() {
        Map<String, Object> response = new HashMap<>();
        response.put("baseUrl", "http://localhost:8080/api");
        response.put("contextPath", "/api");
        response.put("testEndpoints", Map.of(
            "health", "GET /api/test/health",
            "databricks-config", "GET /api/test/databricks-config", 
            "databricks-test", "POST /api/test/databricks-test",
            "db-status", "GET /api/test/db-status",
            "endpoints", "GET /api/test/endpoints"
        ));
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong - TestController is working!");
    }
}