package com.lina.dataportal.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "ml_models")
public class MLModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예: 1
    
    @NotBlank
    @Column(nullable = false)
    private String name; // 예: "고객 이탈 예측 모델"
    
    @Column(columnDefinition = "TEXT")
    private String description; // 예: "고객의 행동 패턴을 분석하여 이탈 가능성을 예측하는 머신러닝 모델입니다."
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ModelType type; // 예: CLASSIFICATION, REGRESSION, CLUSTERING
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ModelStatus status = ModelStatus.TRAINING; // 예: TRAINING, DEPLOYED, RETIRED
    
    private String version; // 예: "v1.2.3"
    
    private Double accuracy; // 예: 0.92 (92% 정확도)
    
    private Double f1Score; // 예: 0.89 (F1 스코어)
    
    private Long responseTime; // 예: 150 (밀리초)
    
    @ElementCollection
    @CollectionTable(name = "model_features", joinColumns = @JoinColumn(name = "model_id"))
    @MapKeyColumn(name = "feature_name")
    @Column(name = "importance")
    private Map<String, Double> featureImportance; // 예: {"age": 0.3, "purchase_history": 0.5, "login_frequency": 0.2}
    
    @ElementCollection
    @CollectionTable(name = "model_tags", joinColumns = @JoinColumn(name = "model_id"))
    @Column(name = "tag")
    private List<String> tags; // 예: ["churn", "classification", "customer"]
    
    @Column(name = "model_path")
    private String modelPath; // 예: "/models/churn-prediction/v1.2.3/model.pkl"
    
    @Column(name = "created_by")
    private String createdBy; // 예: "data.scientist@company.com"
    
    @Column(name = "deployed_at")
    private LocalDateTime deployedAt; // 예: 2024-01-20T10:00:00
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 예: 2024-01-15T09:00:00
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 예: 2024-01-25T14:30:00
    
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
    public MLModel() {}
    
    public MLModel(String name, String description, ModelType type, String createdBy) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.createdBy = createdBy;
    }
    
    // Business Methods
    public void deploy() {
        this.status = ModelStatus.DEPLOYED;
        this.deployedAt = LocalDateTime.now();
    }
    
    public void retire() {
        this.status = ModelStatus.RETIRED;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ModelType getType() { return type; }
    public void setType(ModelType type) { this.type = type; }
    
    public ModelStatus getStatus() { return status; }
    public void setStatus(ModelStatus status) { this.status = status; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }
    
    public Double getF1Score() { return f1Score; }
    public void setF1Score(Double f1Score) { this.f1Score = f1Score; }
    
    public Long getResponseTime() { return responseTime; }
    public void setResponseTime(Long responseTime) { this.responseTime = responseTime; }
    
    public Map<String, Double> getFeatureImportance() { return featureImportance; }
    public void setFeatureImportance(Map<String, Double> featureImportance) { this.featureImportance = featureImportance; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public String getModelPath() { return modelPath; }
    public void setModelPath(String modelPath) { this.modelPath = modelPath; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public LocalDateTime getDeployedAt() { return deployedAt; }
    public void setDeployedAt(LocalDateTime deployedAt) { this.deployedAt = deployedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}