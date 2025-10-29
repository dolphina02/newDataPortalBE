package com.lina.dataportal.domain.api;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "api_endpoints")
public class ApiEndpoint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotBlank
    @Column(nullable = false)
    private String endpoint;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private HttpMethod method;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ApiCategory category;
    
    @Column(columnDefinition = "TEXT")
    private String requestSchema;
    
    @Column(columnDefinition = "TEXT")
    private String responseSchema;
    
    @ElementCollection
    @CollectionTable(name = "api_tags", joinColumns = @JoinColumn(name = "api_id"))
    @Column(name = "tag")
    private List<String> tags;
    
    private String documentationUrl;
    
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public ApiEndpoint() {}
    
    public ApiEndpoint(String name, String description, String endpoint, HttpMethod method, ApiCategory category) {
        this.name = name;
        this.description = description;
        this.endpoint = endpoint;
        this.method = method;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    
    public HttpMethod getMethod() { return method; }
    public void setMethod(HttpMethod method) { this.method = method; }
    
    public ApiCategory getCategory() { return category; }
    public void setCategory(ApiCategory category) { this.category = category; }
    
    public String getRequestSchema() { return requestSchema; }
    public void setRequestSchema(String requestSchema) { this.requestSchema = requestSchema; }
    
    public String getResponseSchema() { return responseSchema; }
    public void setResponseSchema(String responseSchema) { this.responseSchema = responseSchema; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public String getDocumentationUrl() { return documentationUrl; }
    public void setDocumentationUrl(String documentationUrl) { this.documentationUrl = documentationUrl; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}