package com.lina.dataportal.domain.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "data_tables")
public class DataTable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예: 1
    
    @NotBlank
    @Column(nullable = false)
    private String tableName; // 예: "customer_orders"
    
    @NotBlank
    @Column(nullable = false)
    private String schemaName; // 예: "sales_db"
    
    @Column(columnDefinition = "TEXT")
    private String description; // 예: "고객 주문 정보를 저장하는 테이블입니다."
    
    @NotBlank
    @Column(nullable = false)
    private String category; // 예: "Sales", "Customer", "Product"
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private DataType dataType; // 예: TABLE, VIEW, MATERIALIZED_VIEW
    
    @Column(nullable = false)
    private Long recordCount = 0L; // 예: 1250000 (125만 건)
    
    @Column(nullable = false)
    private Integer columnCount = 0; // 예: 15 (15개 컬럼)
    
    @ElementCollection
    @CollectionTable(name = "data_table_tags", joinColumns = @JoinColumn(name = "table_id"))
    @Column(name = "tag")
    private List<String> tags; // 예: ["orders", "customer", "sales", "transactional"]
    
    @ElementCollection
    @CollectionTable(name = "data_table_columns", joinColumns = @JoinColumn(name = "table_id"))
    @Column(name = "column_name")
    private List<String> columns; // 예: ["order_id", "customer_id", "order_date", "total_amount"]
    
    private Boolean isFavorite = false; // 예: true (즐겨찾기 여부)
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated; // 예: 2024-01-25T23:59:59
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 예: 2024-01-01T00:00:00
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
    
    // Constructors
    public DataTable() {}
    
    public DataTable(String tableName, String schemaName, String description, String category, DataType dataType) {
        this.tableName = tableName;
        this.schemaName = schemaName;
        this.description = description;
        this.category = category;
        this.dataType = dataType;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    
    public String getSchemaName() { return schemaName; }
    public void setSchemaName(String schemaName) { this.schemaName = schemaName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public DataType getDataType() { return dataType; }
    public void setDataType(DataType dataType) { this.dataType = dataType; }
    
    public Long getRecordCount() { return recordCount; }
    public void setRecordCount(Long recordCount) { this.recordCount = recordCount; }
    
    public Integer getColumnCount() { return columnCount; }
    public void setColumnCount(Integer columnCount) { this.columnCount = columnCount; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public List<String> getColumns() { return columns; }
    public void setColumns(List<String> columns) { this.columns = columns; }
    
    public Boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(Boolean isFavorite) { this.isFavorite = isFavorite; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}