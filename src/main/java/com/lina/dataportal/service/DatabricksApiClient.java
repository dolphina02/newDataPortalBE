package com.lina.dataportal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class DatabricksApiClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public Map<String, Object> executeQuery(String workspaceUrl, String accessToken, String warehouseId, String sql) {
        try {
            // Step 1: Create statement
            String statementId = createStatement(workspaceUrl, accessToken, warehouseId, sql);
            
            // Step 2: Poll for results (첫 번째 청크만)
            return pollStatementResults(workspaceUrl, accessToken, statementId);
            
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", e.getMessage(),
                "results", new ArrayList<>(),
                "columns", new ArrayList<>(),
                "rowCount", 0,
                "executionTime", 0L,
                "totalChunks", 0,
                "currentChunk", 0,
                "hasNextChunk", false
            );
        }
    }
    
    public Map<String, Object> getNextChunk(String workspaceUrl, String accessToken, String statementId, int chunkIndex) {
        try {
            String url = workspaceUrl + "/api/2.0/sql/statements/" + statementId + "/result/chunks/" + chunkIndex;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return parseChunkResults(jsonNode, chunkIndex);
            } else {
                throw new RuntimeException("Failed to get chunk: " + response.getBody());
            }
            
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", e.getMessage(),
                "results", new ArrayList<>(),
                "columns", new ArrayList<>(),
                "rowCount", 0,
                "executionTime", 0L
            );
        }
    }
    
    private String createStatement(String workspaceUrl, String accessToken, String warehouseId, String sql) throws Exception {
        String url = workspaceUrl + "/api/2.0/sql/statements";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        
        Map<String, Object> requestBody = Map.of(
            "warehouse_id", warehouseId,
            "statement", sql,
            "wait_timeout", "10s"
        );
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("statement_id").asText();
        } else {
            throw new RuntimeException("Failed to create statement: " + response.getBody());
        }
    }
    
    private Map<String, Object> pollStatementResults(String workspaceUrl, String accessToken, String statementId) throws Exception {
        String url = workspaceUrl + "/api/2.0/sql/statements/" + statementId;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<Void> request = new HttpEntity<>(headers);
        
        // Poll for results (max 30 seconds)
        long startTime = System.currentTimeMillis();
        long maxWaitTime = 30000; // 30 seconds
        
        while (System.currentTimeMillis() - startTime < maxWaitTime) {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String status = jsonNode.get("status").get("state").asText();
                
                if ("SUCCEEDED".equals(status)) {
                    return parseSuccessfulResults(jsonNode, System.currentTimeMillis() - startTime);
                } else if ("FAILED".equals(status) || "CANCELED".equals(status)) {
                    String errorMessage = jsonNode.has("status") && jsonNode.get("status").has("error") 
                        ? jsonNode.get("status").get("error").get("message").asText()
                        : "Query execution failed";
                    throw new RuntimeException(errorMessage);
                } else if ("RUNNING".equals(status) || "PENDING".equals(status)) {
                    // Continue polling
                    Thread.sleep(1000); // Wait 1 second before next poll
                    continue;
                }
            }
            
            Thread.sleep(1000);
        }
        
        throw new RuntimeException("Query execution timeout");
    }
    
    private Map<String, Object> parseSuccessfulResults(JsonNode jsonNode, long executionTime) {
        List<Map<String, Object>> results = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        
        // 디버깅을 위해 전체 응답 로깅
        System.out.println("Databricks Response: " + jsonNode.toString());
        
        // 실제 Databricks 응답 구조에 맞게 파싱
        // manifest.schema.columns에서 컬럼 정보 추출
        if (jsonNode.has("manifest") && jsonNode.get("manifest").has("schema") 
            && jsonNode.get("manifest").get("schema").has("columns")) {
            JsonNode columnsNode = jsonNode.get("manifest").get("schema").get("columns");
            for (JsonNode column : columnsNode) {
                columns.add(column.get("name").asText());
            }
        }
        
        // result.data_array에서 데이터 추출
        if (jsonNode.has("result") && jsonNode.get("result").has("data_array")) {
            JsonNode dataArray = jsonNode.get("result").get("data_array");
            
            for (JsonNode row : dataArray) {
                Map<String, Object> rowMap = new HashMap<>();
                
                if (row.isArray()) {
                    // 배열 형태: ["first_catalog"], ["hive_metastore"], ...
                    for (int i = 0; i < columns.size() && i < row.size(); i++) {
                        JsonNode cellValue = row.get(i);
                        Object value = parseJsonValue(cellValue);
                        rowMap.put(columns.get(i), value);
                    }
                } else if (row.isObject()) {
                    // 객체 형태 처리
                    row.fields().forEachRemaining(entry -> {
                        String key = entry.getKey();
                        Object value = parseJsonValue(entry.getValue());
                        rowMap.put(key, value);
                        if (!columns.contains(key)) {
                            columns.add(key);
                        }
                    });
                }
                
                results.add(rowMap);
            }
        }
        
        // 청킹 정보 추출
        int totalChunks = 1;
        int currentChunk = 0;
        String statementId = null;
        
        if (jsonNode.has("statement_id")) {
            statementId = jsonNode.get("statement_id").asText();
        }
        
        if (jsonNode.has("manifest") && jsonNode.get("manifest").has("total_chunk_count")) {
            totalChunks = jsonNode.get("manifest").get("total_chunk_count").asInt();
        }
        
        if (jsonNode.has("result") && jsonNode.get("result").has("chunk_index")) {
            currentChunk = jsonNode.get("result").get("chunk_index").asInt();
        }
        
        return Map.of(
            "success", true,
            "results", results,
            "columns", columns,
            "rowCount", results.size(),
            "executionTime", executionTime,
            "totalChunks", totalChunks,
            "currentChunk", currentChunk,
            "hasNextChunk", currentChunk < totalChunks - 1,
            "statementId", statementId != null ? statementId : ""
        );
    }
    
    private Object parseJsonValue(JsonNode cellValue) {
        if (cellValue.isNull()) return null;
        if (cellValue.isTextual()) return cellValue.asText();
        if (cellValue.isNumber()) return cellValue.asDouble();
        if (cellValue.isBoolean()) return cellValue.asBoolean();
        return cellValue.toString();
    }
    
    private Map<String, Object> parseChunkResults(JsonNode jsonNode, int chunkIndex) {
        List<Map<String, Object>> results = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        
        // 청크 데이터에서는 컬럼 정보가 없으므로 데이터에서 추론
        if (jsonNode.has("data_array")) {
            JsonNode dataArray = jsonNode.get("data_array");
            
            // 첫 번째 행에서 컬럼 수 추론 (실제로는 원본 쿼리에서 컬럼 정보를 저장해야 함)
            if (dataArray.isArray() && dataArray.size() > 0) {
                JsonNode firstRow = dataArray.get(0);
                if (firstRow.isArray()) {
                    for (int i = 0; i < firstRow.size(); i++) {
                        columns.add("column_" + i); // 임시 컬럼명
                    }
                }
            }
            
            // 데이터 파싱
            for (JsonNode row : dataArray) {
                Map<String, Object> rowMap = new HashMap<>();
                
                if (row.isArray()) {
                    for (int i = 0; i < columns.size() && i < row.size(); i++) {
                        JsonNode cellValue = row.get(i);
                        Object value = parseJsonValue(cellValue);
                        rowMap.put(columns.get(i), value);
                    }
                }
                
                results.add(rowMap);
            }
        }
        
        return Map.of(
            "success", true,
            "results", results,
            "columns", columns,
            "rowCount", results.size(),
            "currentChunk", chunkIndex,
            "executionTime", 0L
        );
    }
    
    public Map<String, Object> listCatalogs(String workspaceUrl, String accessToken) {
        try {
            String url = workspaceUrl + "/api/2.1/unity-catalog/catalogs";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                List<Map<String, Object>> catalogs = new ArrayList<>();
                
                if (jsonNode.has("catalogs")) {
                    for (JsonNode catalog : jsonNode.get("catalogs")) {
                        catalogs.add(Map.of(
                            "name", catalog.get("name").asText(),
                            "comment", catalog.has("comment") ? catalog.get("comment").asText() : "",
                            "metastore_id", catalog.has("metastore_id") ? catalog.get("metastore_id").asText() : ""
                        ));
                    }
                }
                
                return Map.of("success", true, "catalogs", catalogs);
            } else {
                return Map.of("success", false, "error", "Failed to list catalogs");
            }
        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage());
        }
    }
    
    public Map<String, Object> listSchemas(String workspaceUrl, String accessToken, String catalogName) {
        try {
            String url = workspaceUrl + "/api/2.1/unity-catalog/schemas?catalog_name=" + catalogName;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                List<Map<String, Object>> schemas = new ArrayList<>();
                
                if (jsonNode.has("schemas")) {
                    for (JsonNode schema : jsonNode.get("schemas")) {
                        schemas.add(Map.of(
                            "name", schema.get("name").asText(),
                            "catalog_name", schema.get("catalog_name").asText(),
                            "comment", schema.has("comment") ? schema.get("comment").asText() : ""
                        ));
                    }
                }
                
                return Map.of("success", true, "schemas", schemas);
            } else {
                return Map.of("success", false, "error", "Failed to list schemas");
            }
        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage());
        }
    }
    
    public Map<String, Object> listTables(String workspaceUrl, String accessToken, String catalogName, String schemaName) {
        try {
            String url = workspaceUrl + "/api/2.1/unity-catalog/tables?catalog_name=" + catalogName + "&schema_name=" + schemaName;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                List<Map<String, Object>> tables = new ArrayList<>();
                
                if (jsonNode.has("tables")) {
                    for (JsonNode table : jsonNode.get("tables")) {
                        tables.add(Map.of(
                            "name", table.get("name").asText(),
                            "catalog_name", table.get("catalog_name").asText(),
                            "schema_name", table.get("schema_name").asText(),
                            "table_type", table.has("table_type") ? table.get("table_type").asText() : "TABLE",
                            "comment", table.has("comment") ? table.get("comment").asText() : ""
                        ));
                    }
                }
                
                return Map.of("success", true, "tables", tables);
            } else {
                return Map.of("success", false, "error", "Failed to list tables");
            }
        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage());
        }
    }
}