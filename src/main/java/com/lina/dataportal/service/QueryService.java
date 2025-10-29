package com.lina.dataportal.service;

import com.lina.dataportal.config.DatabricksConfig;
import com.lina.dataportal.domain.user.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QueryService {
    
    @Autowired
    private UserTokenService userTokenService;
    
    @Autowired
    private DatabricksApiClient databricksApiClient;
    
    @Autowired
    private DatabricksConfig databricksConfig;

    public List<Map<String, Object>> getSchemaInfo() {
        return getSchemaInfo("current_user"); // Default user for now
    }
    
    public List<Map<String, Object>> getSchemaInfo(String userId) {
        Optional<UserToken> tokenOpt = userTokenService.getDatabricksToken(userId);
        
        String workspaceUrl;
        String accessToken;
        
        if (tokenOpt.isPresent()) {
            // 사용자 설정 사용
            UserToken token = tokenOpt.get();
            workspaceUrl = token.getWorkspaceUrl();
            accessToken = userTokenService.decryptToken(token);
            
            // Update last used timestamp
            userTokenService.updateLastUsed(token.getId());
        } else {
            // 기본 설정 사용 (테스트용)
            workspaceUrl = databricksConfig.getDefault().getWorkspaceUrl();
            accessToken = databricksConfig.getDefault().getAccessToken();
            
            if (workspaceUrl == null || accessToken == null) {
                return getFallbackSchemaInfo(); // Return mock data if no config
            }
        }
        
        try {
            // Get catalogs from Databricks
            Map<String, Object> catalogsResult = databricksApiClient.listCatalogs(workspaceUrl, accessToken);
            
            if ((Boolean) catalogsResult.get("success")) {
                List<Map<String, Object>> catalogs = (List<Map<String, Object>>) catalogsResult.get("catalogs");
                List<Map<String, Object>> schemas = new ArrayList<>();
                
                // For each catalog, get schemas
                for (Map<String, Object> catalog : catalogs) {
                    String catalogName = (String) catalog.get("name");
                    Map<String, Object> schemasResult = databricksApiClient.listSchemas(
                        workspaceUrl, accessToken, catalogName);
                    
                    if ((Boolean) schemasResult.get("success")) {
                        List<Map<String, Object>> catalogSchemas = (List<Map<String, Object>>) schemasResult.get("schemas");
                        
                        for (Map<String, Object> schema : catalogSchemas) {
                            String schemaName = (String) schema.get("name");
                            
                            // Get tables for this schema
                            Map<String, Object> tablesResult = databricksApiClient.listTables(
                                workspaceUrl, accessToken, catalogName, schemaName);
                            
                            if ((Boolean) tablesResult.get("success")) {
                                List<Map<String, Object>> tables = (List<Map<String, Object>>) tablesResult.get("tables");
                                
                                Map<String, Object> schemaInfo = new HashMap<>();
                                schemaInfo.put("name", catalogName + "." + schemaName);
                                schemaInfo.put("catalog", catalogName);
                                schemaInfo.put("schema", schemaName);
                                schemaInfo.put("tables", tables.stream().map(table -> 
                                    createTableFromDatabricks(table)).toList());
                                
                                schemas.add(schemaInfo);
                            }
                        }
                    }
                }
                
                return schemas;
            } else {
                return getFallbackSchemaInfo();
            }
        } catch (Exception e) {
            // Log error and return fallback data
            System.err.println("Error fetching schema info from Databricks: " + e.getMessage());
            return getFallbackSchemaInfo();
        }
    }
    
    private List<Map<String, Object>> getFallbackSchemaInfo() {
        List<Map<String, Object>> schemas = new ArrayList<>();
        
        // Insurance schema
        Map<String, Object> insuranceSchema = new HashMap<>();
        insuranceSchema.put("name", "insurance");
        insuranceSchema.put("tables", Arrays.asList(
            createTable("insurance_policies", "보험 계약 정보", "1.2M"),
            createTable("customers", "고객 기본 정보", "850K"),
            createTable("claims", "보험 청구 내역", "2.3M"),
            createTable("agents", "보험 설계사 정보", "15K"),
            createTable("products", "보험 상품 정보", "245")
        ));
        schemas.add(insuranceSchema);
        
        // Customer schema
        Map<String, Object> customerSchema = new HashMap<>();
        customerSchema.put("name", "customer");
        customerSchema.put("tables", Arrays.asList(
            createTable("customer_contacts", "고객 연락처 정보", "1.1M"),
            createTable("customer_segments", "고객 세그먼트 정보", "125")
        ));
        schemas.add(customerSchema);
        
        return schemas;
    }
    
    public List<Map<String, Object>> getTables(String schemaName) {
        List<Map<String, Object>> schemas = getSchemaInfo();
        
        return schemas.stream()
            .filter(schema -> schemaName.equals(schema.get("name")))
            .findFirst()
            .map(schema -> (List<Map<String, Object>>) schema.get("tables"))
            .orElse(new ArrayList<>());
    }
    
    public List<Map<String, Object>> getTableColumns(String tableName) {
        return getTableColumns(tableName, "current_user");
    }
    
    public List<Map<String, Object>> getTableColumns(String tableName, String userId) {
        // 먼저 실제 Databricks에서 컬럼 정보 가져오기 시도
        try {
            String describeQuery = "DESCRIBE TABLE " + tableName;
            Map<String, Object> result = executeQuery(describeQuery, userId);
            
            if ((Boolean) result.get("success")) {
                List<Map<String, Object>> queryResults = (List<Map<String, Object>>) result.get("results");
                List<Map<String, Object>> columns = new ArrayList<>();
                
                for (Map<String, Object> row : queryResults) {
                    String colName = (String) row.get("col_name");
                    String dataType = (String) row.get("data_type");
                    String comment = (String) row.getOrDefault("comment", "");
                    
                    // 빈 행이나 파티션 정보는 제외
                    if (colName != null && !colName.trim().isEmpty() && 
                        !colName.startsWith("#") && !colName.equals("")) {
                        
                        Map<String, Object> column = new HashMap<>();
                        column.put("name", colName);
                        column.put("type", dataType != null ? dataType : "STRING");
                        column.put("nullable", true); // Databricks에서는 기본적으로 nullable
                        column.put("isPrimaryKey", false); // DESCRIBE TABLE로는 PK 정보를 알 수 없음
                        column.put("isForeignKey", false);
                        column.put("description", comment != null ? comment : "");
                        
                        columns.add(column);
                    }
                }
                
                return columns;
            }
        } catch (Exception e) {
            System.err.println("Error fetching table columns from Databricks: " + e.getMessage());
        }
        
        // Databricks 조회 실패 시 fallback 데이터 반환
        return getFallbackTableColumns(tableName);
    }
    
    private List<Map<String, Object>> getFallbackTableColumns(String tableName) {
        Map<String, List<Map<String, Object>>> tableColumns = new HashMap<>();
        
        // insurance_policies columns
        tableColumns.put("insurance_policies", Arrays.asList(
            createColumn("policy_id", "VARCHAR(20)", false, true, false, "보험계약번호"),
            createColumn("customer_id", "VARCHAR(20)", false, false, true, "고객ID"),
            createColumn("product_id", "VARCHAR(10)", false, false, true, "상품ID"),
            createColumn("policy_status", "VARCHAR(10)", false, false, false, "계약상태"),
            createColumn("start_date", "DATE", false, false, false, "계약시작일"),
            createColumn("end_date", "DATE", true, false, false, "계약종료일"),
            createColumn("premium_amount", "DECIMAL(15,2)", false, false, false, "보험료"),
            createColumn("created_at", "TIMESTAMP", false, false, false, "생성일시")
        ));
        
        // customers columns
        tableColumns.put("customers", Arrays.asList(
            createColumn("customer_id", "VARCHAR(20)", false, true, false, "고객ID"),
            createColumn("customer_name", "VARCHAR(100)", false, false, false, "고객명"),
            createColumn("birth_date", "DATE", true, false, false, "생년월일"),
            createColumn("gender", "CHAR(1)", true, false, false, "성별"),
            createColumn("phone_number", "VARCHAR(20)", true, false, false, "전화번호"),
            createColumn("email", "VARCHAR(100)", true, false, false, "이메일"),
            createColumn("address", "VARCHAR(200)", true, false, false, "주소"),
            createColumn("registration_date", "TIMESTAMP", false, false, false, "등록일시")
        ));
        
        // claims columns
        tableColumns.put("claims", Arrays.asList(
            createColumn("claim_id", "VARCHAR(20)", false, true, false, "청구ID"),
            createColumn("policy_id", "VARCHAR(20)", false, false, true, "보험계약번호"),
            createColumn("claim_type", "VARCHAR(20)", false, false, false, "청구유형"),
            createColumn("claim_amount", "DECIMAL(15,2)", false, false, false, "청구금액"),
            createColumn("claim_status", "VARCHAR(10)", false, false, false, "청구상태"),
            createColumn("claim_date", "DATE", false, false, false, "청구일자"),
            createColumn("settlement_amount", "DECIMAL(15,2)", true, false, false, "지급금액"),
            createColumn("processed_at", "TIMESTAMP", true, false, false, "처리일시")
        ));
        
        return tableColumns.getOrDefault(tableName, new ArrayList<>());
    }
    
    public Map<String, Object> executeQuery(String sql) {
        return executeQuery(sql, "current_user"); // Default user for now
    }
    
    public Map<String, Object> executeQuery(String sql, String userId) {
        // 사용자 토큰 확인
        Optional<UserToken> tokenOpt = userTokenService.getDatabricksToken(userId);
        
        String workspaceUrl;
        String accessToken;
        String warehouseId;
        
        if (tokenOpt.isPresent()) {
            // 사용자 설정 사용
            UserToken token = tokenOpt.get();
            workspaceUrl = token.getWorkspaceUrl();
            accessToken = userTokenService.decryptToken(token);
            warehouseId = token.getWarehouseId();
            
            // Update last used timestamp
            userTokenService.updateLastUsed(token.getId());
        } else {
            // 기본 설정 사용 (테스트용)
            workspaceUrl = databricksConfig.getDefault().getWorkspaceUrl();
            accessToken = databricksConfig.getDefault().getAccessToken();
            warehouseId = databricksConfig.getDefault().getWarehouseId();
            
            if (workspaceUrl == null || accessToken == null) {
                return Map.of(
                    "success", false,
                    "error", "No Databricks workspace URL or access token found. Please configure your settings.",
                    "results", new ArrayList<>(),
                    "columns", new ArrayList<>(),
                    "rowCount", 0,
                    "executionTime", 0L
                );
            }
            
            if (warehouseId == null || warehouseId.isEmpty()) {
                return Map.of(
                    "success", false,
                    "error", "No Databricks warehouse ID configured. Please set DATABRICKS_WAREHOUSE_ID environment variable or find your SQL Warehouse ID in Databricks console.",
                    "results", new ArrayList<>(),
                    "columns", new ArrayList<>(),
                    "rowCount", 0,
                    "executionTime", 0L
                );
            }
        }
        
        try {
            Map<String, Object> result = databricksApiClient.executeQuery(
                workspaceUrl, 
                accessToken, 
                warehouseId, 
                sql
            );
            
            return result;
            
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", "Query execution failed: " + e.getMessage(),
                "results", new ArrayList<>(),
                "columns", new ArrayList<>(),
                "rowCount", 0,
                "executionTime", 0L
            );
        }
    }
    
    public String formatQuery(String sql) {
        return sql.replaceAll("\\s+", " ")
                 .replace(",", ",\n  ")
                 .replace("SELECT", "SELECT")
                 .replace("FROM", "\nFROM")
                 .replace("WHERE", "\nWHERE")
                 .replace("JOIN", "\nJOIN")
                 .replace("GROUP BY", "\nGROUP BY")
                 .replace("ORDER BY", "\nORDER BY")
                 .replace("LIMIT", "\nLIMIT")
                 .trim();
    }
    
    public Map<String, Object> validateQuery(String sql) {
        // 간단한 SQL 유효성 검사
        boolean isValid = sql.trim().toUpperCase().startsWith("SELECT");
        List<String> errors = new ArrayList<>();
        
        if (!isValid) {
            errors.add("쿼리는 SELECT 문으로 시작해야 합니다.");
        }
        
        if (!sql.contains("FROM")) {
            errors.add("FROM 절이 필요합니다.");
            isValid = false;
        }
        
        Map<String, Object> validation = new HashMap<>();
        validation.put("isValid", isValid);
        validation.put("errors", errors);
        validation.put("warnings", new ArrayList<>());
        
        return validation;
    }
    
    public Map<String, Object> saveQuery(String name, String sql, String description) {
        // 쿼리 저장 시뮬레이션
        Map<String, Object> savedQuery = new HashMap<>();
        savedQuery.put("id", System.currentTimeMillis());
        savedQuery.put("name", name);
        savedQuery.put("sql", sql);
        savedQuery.put("description", description);
        savedQuery.put("createdAt", new Date());
        savedQuery.put("updatedAt", new Date());
        
        return savedQuery;
    }
    
    public List<Map<String, Object>> getSavedQueries() {
        // TODO: 실제 저장된 쿼리 테이블에서 조회하도록 구현 필요
        // 현재는 기본 예시 쿼리들을 반환
        return Arrays.asList(
            Map.of("id", 1L, "name", "고객별 보험료 합계", "description", "고객별 총 보험료 조회", "createdAt", new Date()),
            Map.of("id", 2L, "name", "월별 신규 계약", "description", "월별 신규 계약 건수", "createdAt", new Date()),
            Map.of("id", 3L, "name", "상품별 판매 현황", "description", "상품별 판매 실적", "createdAt", new Date())
        );
    }
    
    public void deleteSavedQuery(Long queryId) {
        // 쿼리 삭제 시뮬레이션
        System.out.println("Deleted query with ID: " + queryId);
    }
    
    public String generateSelectQuery(String tableName) {
        List<Map<String, Object>> columns = getTableColumns(tableName);
        
        if (columns.isEmpty()) {
            return "SELECT * FROM " + tableName + " LIMIT 10;";
        }
        
        StringBuilder sql = new StringBuilder("SELECT ");
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) sql.append(",\n  ");
            sql.append(columns.get(i).get("name"));
        }
        sql.append("\nFROM ").append(tableName).append("\nLIMIT 10;");
        
        return sql.toString();
    }
    
    public Map<String, Object> getNextChunk(String statementId, int chunkIndex, String userId) {
        Optional<UserToken> tokenOpt = userTokenService.getDatabricksToken(userId);
        
        String workspaceUrl;
        String accessToken;
        
        if (tokenOpt.isPresent()) {
            UserToken token = tokenOpt.get();
            workspaceUrl = token.getWorkspaceUrl();
            accessToken = userTokenService.decryptToken(token);
            userTokenService.updateLastUsed(token.getId());
        } else {
            workspaceUrl = databricksConfig.getDefault().getWorkspaceUrl();
            accessToken = databricksConfig.getDefault().getAccessToken();
            
            if (workspaceUrl == null || accessToken == null) {
                return Map.of(
                    "success", false,
                    "error", "No Databricks configuration found",
                    "results", new ArrayList<>(),
                    "columns", new ArrayList<>(),
                    "rowCount", 0
                );
            }
        }
        
        try {
            return databricksApiClient.getNextChunk(workspaceUrl, accessToken, statementId, chunkIndex);
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", "Failed to get next chunk: " + e.getMessage(),
                "results", new ArrayList<>(),
                "columns", new ArrayList<>(),
                "rowCount", 0
            );
        }
    }
    
    private Map<String, Object> createTable(String name, String description, String rowCount) {
        Map<String, Object> table = new HashMap<>();
        table.put("name", name);
        table.put("description", description);
        table.put("rowCount", rowCount);
        return table;
    }
    
    private Map<String, Object> createTableFromDatabricks(Map<String, Object> databricksTable) {
        Map<String, Object> table = new HashMap<>();
        table.put("name", databricksTable.get("name"));
        table.put("description", databricksTable.getOrDefault("comment", ""));
        table.put("rowCount", "Unknown"); // Databricks doesn't provide row count in list API
        table.put("catalog", databricksTable.get("catalog_name"));
        table.put("schema", databricksTable.get("schema_name"));
        table.put("tableType", databricksTable.getOrDefault("table_type", "TABLE"));
        return table;
    }
    
    private Map<String, Object> createColumn(String name, String type, boolean nullable, 
                                           boolean isPrimaryKey, boolean isForeignKey, String description) {
        Map<String, Object> column = new HashMap<>();
        column.put("name", name);
        column.put("type", type);
        column.put("nullable", nullable);
        column.put("isPrimaryKey", isPrimaryKey);
        column.put("isForeignKey", isForeignKey);
        column.put("description", description);
        return column;
    }
    

}