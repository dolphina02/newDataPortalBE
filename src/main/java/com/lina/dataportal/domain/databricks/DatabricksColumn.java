package com.lina.dataportal.domain.databricks;

public class DatabricksColumn {
    
    private String name;
    private String type;
    private String comment;
    private boolean nullable;
    
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