package com.lina.dataportal.repository;

import com.lina.dataportal.domain.catalog.DataTable;
import com.lina.dataportal.domain.catalog.DataType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataTableRepository extends JpaRepository<DataTable, Long> {
    
    List<DataTable> findByDataType(DataType dataType);
    
    List<DataTable> findByCategory(String category);
    
    List<DataTable> findByIsFavoriteTrue();
    
    @Query("SELECT dt FROM DataTable dt WHERE dt.tableName LIKE %:keyword% OR dt.description LIKE %:keyword%")
    List<DataTable> findByKeyword(@Param("keyword") String keyword);
    
    // @Query("SELECT dt FROM DataTable dt WHERE :tag MEMBER OF dt.tags")
    // List<DataTable> findByTag(@Param("tag") String tag); // 임시 비활성화
    
    @Query("SELECT dt FROM DataTable dt WHERE dt.schemaName = :schema")
    List<DataTable> findBySchema(@Param("schema") String schema);
    
    @Query("SELECT dt FROM DataTable dt ORDER BY dt.lastUpdated DESC")
    List<DataTable> findRecentlyUpdated();
    
    // HomeService에서 필요한 메서드들
    List<DataTable> findTop5ByOrderByRecordCountDesc();
    
    List<DataTable> findTop2ByOrderByLastUpdatedDesc();
    
    List<DataTable> findByTableNameContainingIgnoreCase(String tableName);
    
    List<DataTable> findByTableNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String tableName, String description);
}