package com.lina.dataportal.controller;

import com.lina.dataportal.domain.catalog.DataTable;
import com.lina.dataportal.domain.catalog.DataType;
import com.lina.dataportal.service.DataCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/catalog")
@CrossOrigin(origins = "*")
public class DataCatalogController {
    
    private final DataCatalogService dataCatalogService;
    
    @Autowired
    public DataCatalogController(DataCatalogService dataCatalogService) {
        this.dataCatalogService = dataCatalogService;
    }
    
    @GetMapping("/tables")
    public ResponseEntity<List<DataTable>> getAllTables(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean favorites) {
        
        List<DataTable> tables;
        
        if (favorites != null && favorites) {
            tables = dataCatalogService.getFavoriteTables();
        } else if (search != null && !search.trim().isEmpty()) {
            tables = dataCatalogService.searchTables(search);
        } else if (type != null) {
            DataType dataType = DataType.valueOf(type.toUpperCase());
            tables = dataCatalogService.getTablesByType(dataType);
        } else if (category != null) {
            tables = dataCatalogService.getTablesByCategory(category);
        } else {
            tables = dataCatalogService.getAllTables();
        }
        
        return ResponseEntity.ok(tables);
    }
    
    @GetMapping("/tables/{id}")
    public ResponseEntity<DataTable> getTableById(@PathVariable Long id) {
        return dataCatalogService.getTableById(id)
            .map(table -> ResponseEntity.ok(table))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/tables/recent")
    public ResponseEntity<List<DataTable>> getRecentlyUpdatedTables() {
        List<DataTable> tables = dataCatalogService.getRecentlyUpdatedTables();
        return ResponseEntity.ok(tables);
    }
    
    @PostMapping("/tables")
    public ResponseEntity<DataTable> createTable(@Valid @RequestBody DataTable dataTable) {
        DataTable createdTable = dataCatalogService.createTable(dataTable);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTable);
    }
    
    @PutMapping("/tables/{id}")
    public ResponseEntity<DataTable> updateTable(
            @PathVariable Long id, 
            @Valid @RequestBody DataTable tableDetails) {
        try {
            DataTable updatedTable = dataCatalogService.updateTable(id, tableDetails);
            return ResponseEntity.ok(updatedTable);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/tables/{id}/favorite")
    public ResponseEntity<DataTable> toggleFavorite(@PathVariable Long id) {
        try {
            DataTable table = dataCatalogService.toggleFavorite(id);
            return ResponseEntity.ok(table);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/tables/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        try {
            dataCatalogService.deleteTable(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 데이터 계보 관련 엔드포인트 (향후 구현)
    @GetMapping("/lineage/{id}")
    public ResponseEntity<Object> getDataLineage(@PathVariable Long id) {
        // TODO: 데이터 계보 정보 구현
        return ResponseEntity.ok("Data lineage for table " + id);
    }
    
    // 데이터 품질 관련 엔드포인트 (향후 구현)
    @GetMapping("/quality/{id}")
    public ResponseEntity<Object> getDataQuality(@PathVariable Long id) {
        // TODO: 데이터 품질 메트릭 구현
        return ResponseEntity.ok("Data quality metrics for table " + id);
    }
}