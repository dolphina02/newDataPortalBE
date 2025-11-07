package com.lina.dataportal.domain.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_tokens")
public class UserToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예: 1
    
    @Column(name = "user_id", nullable = false)
    private String userId; // 예: "john.doe@company.com"
    
    @Column(name = "token_type", nullable = false)
    private String tokenType; // 예: "DATABRICKS_PAT", "ELASTICSEARCH_API_KEY"
    
    @Column(name = "encrypted_token", nullable = false, length = 1000)
    private String encryptedToken; // 예: "AES256암호화된토큰문자열..."
    
    @Column(name = "workspace_url")
    private String workspaceUrl; // 예: "https://adb-123456789.12.azuredatabricks.net"
    
    @Column(name = "cluster_id")
    private String clusterId; // 예: "0123-456789-abc123"
    
    @Column(name = "warehouse_id")
    private String warehouseId; // 예: "abc123def456"
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // 예: true (토큰 활성 상태)
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 예: 2024-01-15T10:00:00
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 예: 2024-01-20T14:30:00
    
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt; // 예: 2024-01-25T16:45:00
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // 예: 2024-12-31T23:59:59
    
    // Constructors
    public UserToken() {
        this.createdAt = LocalDateTime.now();
    }
    
    public UserToken(String userId, String tokenType, String encryptedToken, String workspaceUrl) {
        this();
        this.userId = userId;
        this.tokenType = tokenType;
        this.encryptedToken = encryptedToken;
        this.workspaceUrl = workspaceUrl;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public String getEncryptedToken() {
        return encryptedToken;
    }
    
    public void setEncryptedToken(String encryptedToken) {
        this.encryptedToken = encryptedToken;
    }
    
    public String getWorkspaceUrl() {
        return workspaceUrl;
    }
    
    public void setWorkspaceUrl(String workspaceUrl) {
        this.workspaceUrl = workspaceUrl;
    }
    
    public String getClusterId() {
        return clusterId;
    }
    
    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }
    
    public String getWarehouseId() {
        return warehouseId;
    }
    
    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }
    
    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}