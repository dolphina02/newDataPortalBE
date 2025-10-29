package com.lina.dataportal.domain.databricks;

import java.util.List;
import java.util.Map;

public class DatabricksPagedResult {
    
    private boolean success;
    private String error;
    private List<DatabricksColumn> columns;
    private List<Map<String, Object>> results;
    private int rowCount;
    private long executionTime;
    
    // 페이지네이션 정보
    private int totalChunks;
    private int currentChunk;
    private boolean hasNextChunk;
    private String statementId; // 다음 청크 조회용
    
    // Constructors
    public DatabricksPagedResult() {}
    
    public DatabricksPagedResult(boolean success) {
        this.success = success;
    }
    
    // Static factory methods
    public static DatabricksPagedResult success(List<DatabricksColumn> columns, 
                                              List<Map<String, Object>> results, 
                                              long executionTime,
                                              int totalChunks,
                                              int currentChunk,
                                              String statementId) {
        DatabricksPagedResult result = new DatabricksPagedResult(true);
        result.columns = columns;
        result.results = results;
        result.rowCount = results.size();
        result.executionTime = executionTime;
        result.totalChunks = totalChunks;
        result.currentChunk = currentChunk;
        result.hasNextChunk = currentChunk < totalChunks - 1;
        result.statementId = statementId;
        return result;
    }
    
    public static DatabricksPagedResult failure(String error) {
        DatabricksPagedResult result = new DatabricksPagedResult(false);
        result.error = error;
        result.rowCount = 0;
        result.executionTime = 0;
        result.totalChunks = 0;
        result.currentChunk = 0;
        result.hasNextChunk = false;
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
    
    public int getTotalChunks() { return totalChunks; }
    public void setTotalChunks(int totalChunks) { this.totalChunks = totalChunks; }
    
    public int getCurrentChunk() { return currentChunk; }
    public void setCurrentChunk(int currentChunk) { this.currentChunk = currentChunk; }
    
    public boolean isHasNextChunk() { return hasNextChunk; }
    public void setHasNextChunk(boolean hasNextChunk) { this.hasNextChunk = hasNextChunk; }
    
    public String getStatementId() { return statementId; }
    public void setStatementId(String statementId) { this.statementId = statementId; }
}