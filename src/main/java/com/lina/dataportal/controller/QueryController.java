package com.lina.dataportal.controller;

import com.lina.dataportal.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/query")
@CrossOrigin(origins = "*")
@Tag(name = "Query", description = "데이터베이스 쿼리 실행 API - Databricks 연동")
public class QueryController {

    @Autowired
    private QueryService queryService;

    @GetMapping("/schema")
    public ResponseEntity<List<Map<String, Object>>> getSchemaInfo(@RequestParam(defaultValue = "current_user") String userId) {
        List<Map<String, Object>> schemas = queryService.getSchemaInfo(userId);
        return ResponseEntity.ok(schemas);
    }

    @GetMapping("/schema/{schemaName}/tables")
    public ResponseEntity<List<Map<String, Object>>> getTables(@PathVariable String schemaName) {
        List<Map<String, Object>> tables = queryService.getTables(schemaName);
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/table/{tableName}/columns")
    public ResponseEntity<List<Map<String, Object>>> getTableColumns(@PathVariable String tableName) {
        List<Map<String, Object>> columns = queryService.getTableColumns(tableName);
        return ResponseEntity.ok(columns);
    }

    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeQuery(@RequestBody Map<String, Object> request) {
        String sql = (String) request.get("sql");
        String userId = (String) request.getOrDefault("userId", "current_user");
        
        Map<String, Object> result = queryService.executeQuery(sql, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/format")
    public ResponseEntity<Map<String, Object>> formatQuery(@RequestBody Map<String, Object> request) {
        String sql = (String) request.get("sql");
        
        String formattedSQL = queryService.formatQuery(sql);
        return ResponseEntity.ok(Map.of("formattedSQL", formattedSQL));
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateQuery(@RequestBody Map<String, Object> request) {
        String sql = (String) request.get("sql");
        
        Map<String, Object> validation = queryService.validateQuery(sql);
        return ResponseEntity.ok(validation);
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveQuery(@RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        String sql = (String) request.get("sql");
        String description = (String) request.get("description");
        
        Map<String, Object> savedQuery = queryService.saveQuery(name, sql, description);
        return ResponseEntity.ok(savedQuery);
    }

    @GetMapping("/saved")
    public ResponseEntity<List<Map<String, Object>>> getSavedQueries() {
        List<Map<String, Object>> queries = queryService.getSavedQueries();
        return ResponseEntity.ok(queries);
    }

    @DeleteMapping("/saved/{queryId}")
    public ResponseEntity<Void> deleteSavedQuery(@PathVariable Long queryId) {
        queryService.deleteSavedQuery(queryId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate-select")
    public ResponseEntity<Map<String, Object>> generateSelectQuery(@RequestBody Map<String, Object> request) {
        String tableName = (String) request.get("tableName");
        
        String sql = queryService.generateSelectQuery(tableName);
        return ResponseEntity.ok(Map.of("sql", sql));
    }
    
    @GetMapping("/chunks/{statementId}/{chunkIndex}")
    public ResponseEntity<Map<String, Object>> getNextChunk(
            @PathVariable String statementId,
            @PathVariable int chunkIndex,
            @RequestParam(defaultValue = "current_user") String userId) {
        
        Map<String, Object> result = queryService.getNextChunk(statementId, chunkIndex, userId);
        return ResponseEntity.ok(result);
    }
}