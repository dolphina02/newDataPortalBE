package com.lina.dataportal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "databricks")
@Component
public class DatabricksConfig {
    
    private Default defaultConfig = new Default();
    private Api api = new Api();
    private Sql sql = new Sql();
    
    public static class Default {
        private String workspaceUrl;
        private String warehouseId;
        private String accessToken;
        private String httpPath;
        
        // Getters and Setters
        public String getWorkspaceUrl() { return workspaceUrl; }
        public void setWorkspaceUrl(String workspaceUrl) { this.workspaceUrl = workspaceUrl; }
        
        public String getWarehouseId() { return warehouseId; }
        public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
        
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        
        public String getHttpPath() { return httpPath; }
        public void setHttpPath(String httpPath) { this.httpPath = httpPath; }
    }
    
    public static class Api {
        private int timeout = 30000;
        private int retryAttempts = 3;
        
        // Getters and Setters
        public int getTimeout() { return timeout; }
        public void setTimeout(int timeout) { this.timeout = timeout; }
        
        public int getRetryAttempts() { return retryAttempts; }
        public void setRetryAttempts(int retryAttempts) { this.retryAttempts = retryAttempts; }
    }
    
    public static class Sql {
        private int maxRows = 10000;
        private String timeout = "30s";
        
        // Getters and Setters
        public int getMaxRows() { return maxRows; }
        public void setMaxRows(int maxRows) { this.maxRows = maxRows; }
        
        public String getTimeout() { return timeout; }
        public void setTimeout(String timeout) { this.timeout = timeout; }
    }
    
    // Main Getters and Setters
    public Default getDefault() { return defaultConfig; }
    public void setDefault(Default defaultConfig) { this.defaultConfig = defaultConfig; }
    
    public Api getApi() { return api; }
    public void setApi(Api api) { this.api = api; }
    
    public Sql getSql() { return sql; }
    public void setSql(Sql sql) { this.sql = sql; }
}