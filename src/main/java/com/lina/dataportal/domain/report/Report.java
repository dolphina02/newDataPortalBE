package com.lina.dataportal.domain.report;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reports")
public class Report {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotBlank
    @Column(nullable = false)
    private String category;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ReportType type;
    
    @NotBlank
    @Column(nullable = false)
    private String filePath;
    
    private String fileName;
    
    private Long fileSize;
    
    @Column(name = "tags")
    private String tags; // 쉼표로 구분된 태그 문자열
    
    @Column(name = "contains_sensitive_data", nullable = false)
    private Boolean containsSensitiveData = false;
    
    @Column(name = "created_by", nullable = false)
    private String createdBy;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Report() {}
    
    public Report(String title, String description, String category, ReportType type, String filePath, String createdBy) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.type = type;
        this.filePath = filePath;
        this.createdBy = createdBy;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public ReportType getType() { return type; }
    public void setType(ReportType type) { this.type = type; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    // 편의 메서드: List<String>을 쉼표로 구분된 문자열로 변환
    public void setTagsList(List<String> tagsList) {
        this.tags = tagsList != null ? String.join(",", tagsList) : null;
    }
    
    // 편의 메서드: 쉼표로 구분된 문자열을 List<String>으로 변환
    public List<String> getTagsList() {
        return tags != null ? List.of(tags.split(",")) : List.of();
    }
    
    public Boolean getContainsSensitiveData() { return containsSensitiveData; }
    public void setContainsSensitiveData(Boolean containsSensitiveData) { this.containsSensitiveData = containsSensitiveData; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}