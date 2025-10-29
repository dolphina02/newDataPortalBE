package com.lina.dataportal.service;

import com.lina.dataportal.domain.catalog.DataTable;
import com.lina.dataportal.domain.catalog.DataType;
import com.lina.dataportal.repository.DataTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DataCatalogService {
    
    private final DataTableRepository dataTableRepository;
    
    @Autowired
    public DataCatalogService(DataTableRepository dataTableRepository) {
        this.dataTableRepository = dataTableRepository;
    }
    
    public List<DataTable> getAllTables() {
        return dataTableRepository.findAll();
    }
    
    public Optional<DataTable> getTableById(Long id) {
        return dataTableRepository.findById(id);
    }
    
    public List<DataTable> getTablesByType(DataType dataType) {
        return dataTableRepository.findByDataType(dataType);
    }
    
    public List<DataTable> getTablesByCategory(String category) {
        return dataTableRepository.findByCategory(category);
    }
    
    public List<DataTable> getFavoriteTables() {
        return dataTableRepository.findByIsFavoriteTrue();
    }
    
    public List<DataTable> searchTables(String keyword) {
        return dataTableRepository.findByKeyword(keyword);
    }
    
    public List<DataTable> getRecentlyUpdatedTables() {
        return dataTableRepository.findRecentlyUpdated();
    }
    
    public DataTable createTable(DataTable dataTable) {
        return dataTableRepository.save(dataTable);
    }
    
    public DataTable updateTable(Long id, DataTable tableDetails) {
        DataTable dataTable = dataTableRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));
        
        dataTable.setTableName(tableDetails.getTableName());
        dataTable.setSchemaName(tableDetails.getSchemaName());
        dataTable.setDescription(tableDetails.getDescription());
        dataTable.setCategory(tableDetails.getCategory());
        dataTable.setDataType(tableDetails.getDataType());
        dataTable.setRecordCount(tableDetails.getRecordCount());
        dataTable.setColumnCount(tableDetails.getColumnCount());
        dataTable.setTags(tableDetails.getTags());
        dataTable.setColumns(tableDetails.getColumns());
        
        return dataTableRepository.save(dataTable);
    }
    
    public DataTable toggleFavorite(Long id) {
        DataTable dataTable = dataTableRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));
        
        dataTable.setIsFavorite(!dataTable.getIsFavorite());
        return dataTableRepository.save(dataTable);
    }
    
    public void deleteTable(Long id) {
        dataTableRepository.deleteById(id);
    }
}