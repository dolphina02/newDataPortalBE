package com.lina.dataportal.domain.dashboard;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "dashboard_subscriptions")
public class DashboardSubscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예: 1
    
    @NotBlank
    @Column(nullable = false)
    private String userId; // 예: "user.kim@company.com"
    
    @NotNull
    @Column(name = "dashboard_id", nullable = false)
    private Long dashboardId; // 예: 1 (구독한 Dashboard의 ID)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dashboard_id", insertable = false, updatable = false)
    private Dashboard dashboard; // 구독한 대시보드
    
    @Column(name = "display_order")
    private Integer displayOrder = 0; // 예: 1 (마이 대시보드에서의 표시 순서)
    
    @Column(name = "is_favorite")
    private Boolean isFavorite = false; // 예: true (즐겨찾기 여부)
    
    @Column(name = "custom_title")
    private String customTitle; // 예: "내 매출 분석" (사용자 지정 제목)
    
    @Column(name = "subscribed_at", nullable = false, updatable = false)
    private LocalDateTime subscribedAt; // 예: 2024-01-15T10:00:00
    
    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed; // 예: 2024-01-25T14:30:00
    
    @PrePersist
    protected void onCreate() {
        subscribedAt = LocalDateTime.now();
    }
    
    // Constructors
    public DashboardSubscription() {}
    
    public DashboardSubscription(String userId, Long dashboardId) {
        this.userId = userId;
        this.dashboardId = dashboardId;
    }
    
    public DashboardSubscription(String userId, Long dashboardId, Integer displayOrder) {
        this.userId = userId;
        this.dashboardId = dashboardId;
        this.displayOrder = displayOrder;
    }
    
    // Business Methods
    public void updateLastAccessed() {
        this.lastAccessed = LocalDateTime.now();
    }
    
    public void toggleFavorite() {
        this.isFavorite = !this.isFavorite;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public Long getDashboardId() { return dashboardId; }
    public void setDashboardId(Long dashboardId) { this.dashboardId = dashboardId; }
    
    public Dashboard getDashboard() { return dashboard; }
    public void setDashboard(Dashboard dashboard) { this.dashboard = dashboard; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(Boolean isFavorite) { this.isFavorite = isFavorite; }
    
    public String getCustomTitle() { return customTitle; }
    public void setCustomTitle(String customTitle) { this.customTitle = customTitle; }
    
    public LocalDateTime getSubscribedAt() { return subscribedAt; }
    public void setSubscribedAt(LocalDateTime subscribedAt) { this.subscribedAt = subscribedAt; }
    
    public LocalDateTime getLastAccessed() { return lastAccessed; }
    public void setLastAccessed(LocalDateTime lastAccessed) { this.lastAccessed = lastAccessed; }
}