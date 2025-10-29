package com.lina.dataportal.domain.databricks;

import java.util.List;
import java.util.Map;

public class DatabricksQueryResult {
    
    private boolean success;
    private String error;
    private List<DatabricksColumn> columns;
    private List<Map<String, Object>> results;
    private int rowCount;
    private long executionTime;
    
    // Constructors
    public DatabricksQueryResult() {}
    
    public DatabricksQueryResult(boolean success) {
        this.success = success;
    }
    
    // Static factory methods
    public static DatabricksQueryResult success(List<DatabricksColumn> columns, 
                                              List<Map<String, Object>> results, 
                                              long executionTime) {
        DatabricksQueryResult result = new DatabricksQueryResult(true);
        result.columns = columns;
        result.results = results;
        result.rowCount = results.size();
        result.executionTime = executionTime;
        return result;
    }
    
    public static DatabricksQueryResult failure(String error) {
        DatabricksQueryResult result = new DatabricksQueryResult(false);
        result.error = error;
        result.rowCount = 0;
        result.executionTime = 0;
        return result;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    
    public List<DatabricksColumn> getColumns() { return columns; }
    public void setColumns(List<DatabricksColumn> columns) { this.columns = columns; }
    
    public List<Map<String, Object>> getResults() { return results; }
    public void setResults(List<Map<String, Object>> results) { this.results = results; }
    
    public int getRowCount() { return rowCount; }
    public void setRowCount(int rowCount) { this.rowCount = rowCount; }
    
    public long getExecutionTime() { return executionTime; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
}