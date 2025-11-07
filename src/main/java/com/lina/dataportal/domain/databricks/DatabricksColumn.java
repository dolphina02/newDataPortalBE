package com.lina.dataportal.domain.databricks;

public class DatabricksColumn {
    
    private String name; // 예: "customer_id"
    private String type; // 예: "string", "int", "double", "timestamp"
    private String comment; // 예: "고객 고유 식별자"
    private boolean nullable; // 예: true (NULL 값 허용 여부)
    
    // Constructors
    public DatabricksColumn() {}
    
    public DatabricksColumn(String name, String type) {
        this.name = name;
        this.type = type;
        this.nullable = true; // Databricks 기본값
    }
    
    public DatabricksColumn(String name, String type, String comment) {
        this.name = name;
        this.type = type;
        this.comment = comment;
        this.nullable = true;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public boolean isNullable() { return nullable; }
    public void setNullable(boolean nullable) { this.nullable = nullable; }
}