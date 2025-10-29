package com.lina.dataportal.repository;

import com.lina.dataportal.domain.report.Report;
import com.lina.dataportal.domain.report.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    List<Report> findByType(ReportType type);
    
    List<Report> findByCategory(String category);
    
    @Query("SELECT r FROM Report r WHERE r.title LIKE %:keyword% OR r.description LIKE %:keyword%")
    List<Report> findByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT r FROM Report r WHERE r.tags LIKE %:tag%")
    List<Report> findByTag(@Param("tag") String tag);
    
    @Query("SELECT r FROM Report r ORDER BY r.createdAt DESC")
    List<Report> findRecentReports();
    
    List<Report> findByIsActiveTrue();
    
    List<Report> findByContainsSensitiveDataTrue();
    
    List<Report> findByCreatedBy(String createdBy);
    
    @Query("SELECT r FROM Report r WHERE r.isActive = true AND (:includeSensitive = true OR r.containsSensitiveData = false)")
    List<Report> findActiveAccessibleReports(@Param("includeSensitive") boolean includeSensitive);
    
    // HomeService에서 필요한 메서드들
    List<Report> findTop3ByOrderByCreatedAtDesc();
    
    List<Report> findByTitleContainingIgnoreCase(String title);
    
    List<Report> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
}