package com.lina.dataportal.domain.dashboard;

import com.lina.dataportal.domain.common.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "dashboards")
public class Dashboard extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예: 1
    
    @NotBlank
    @Column(nullable = false)
    private String title; // 예: "월별 매출 분석 대시보드"
    
    @Column(columnDefinition = "TEXT")
    private String description; // 예: "월별 매출 현황과 트렌드를 분석하는 대시보드입니다."
    
    @NotBlank
    @Column(nullable = false)
    private String category; // 예: "Sales", "Marketing", "Finance"
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private DashboardType type; // 예: TEMPLATE, CUSTOM
    
    @Column(nullable = false)
    private Double rating = 0.0; // 예: 4.5
    
    @Column(nullable = false)
    private Integer downloads = 0; // 예: 127
    
    @Column(name = "tags")
    private String tags; // 예: "sales,revenue,monthly,analytics" (쉼표로 구분된 태그 문자열)
    
    private String image; // 예: "/images/dashboards/sales-dashboard.png"
    
    @Column(columnDefinition = "TEXT")
    private String config; // 예: "{'charts': [{'type': 'line', 'data': 'sales_data'}]}"
    
    @Column(name = "dashboard_url")
    private String dashboardUrl; // 예: "https://databricks.com/sql/dashboards/abc123"
    
    @Column(name = "contains_sensitive_data", nullable = false)
    private Boolean containsSensitiveData = false; // 예: true (민감한 데이터 포함 여부)
    
    @Column(name = "created_by", nullable = false)
    private String createdBy; // 예: "lee.analyst@company.com"
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // 예: true (활성 상태)
    

    
    // Constructors
    public Dashboard() {}
    
    public Dashboard(String title, String description, String category, DashboardType type, String createdBy) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.type = type;
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
    
    public DashboardType getType() { return type; }
    public void setType(DashboardType type) { this.type = type; }
    
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    
    public Integer getDownloads() { return downloads; }
    public void setDownloads(Integer downloads) { this.downloads = downloads; }
    
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
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    public String getConfig() { return config; }
    public void setConfig(String config) { this.config = config; }
    
    public String getDashboardUrl() { return dashboardUrl; }
    public void setDashboardUrl(String dashboardUrl) { this.dashboardUrl = dashboardUrl; }
    
    public Boolean getContainsSensitiveData() { return containsSensitiveData; }
    public void setContainsSensitiveData(Boolean containsSensitiveData) { this.containsSensitiveData = containsSensitiveData; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
}