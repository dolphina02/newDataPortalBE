package com.lina.dataportal.repository;

import com.lina.dataportal.domain.dashboard.Dashboard;
import com.lina.dataportal.domain.dashboard.DashboardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Long> {
    
    List<Dashboard> findByType(DashboardType type);
    
    List<Dashboard> findByCategory(String category);
    
    @Query("SELECT d FROM Dashboard d WHERE d.title LIKE %:keyword% OR d.description LIKE %:keyword%")
    List<Dashboard> findByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT d FROM Dashboard d WHERE d.tags LIKE %:tag%")
    List<Dashboard> findByTag(@Param("tag") String tag);
    
    @Query("SELECT d FROM Dashboard d ORDER BY d.downloads DESC")
    List<Dashboard> findTopByDownloads();
    
    @Query("SELECT d FROM Dashboard d ORDER BY d.rating DESC")
    List<Dashboard> findTopByRating();
    
    List<Dashboard> findByIsActiveTrue();
    
    List<Dashboard> findByContainsSensitiveDataTrue();
    
    List<Dashboard> findByCreatedBy(String createdBy);
    
    @Query("SELECT d FROM Dashboard d WHERE d.isActive = true AND (:includeSensitive = true OR d.containsSensitiveData = false)")
    List<Dashboard> findActiveAccessibleDashboards(@Param("includeSensitive") boolean includeSensitive);
    
    // HomeService에서 필요한 메서드들
    List<Dashboard> findTop3ByOrderByCreatedAtDesc();
    
    List<Dashboard> findTop2ByOrderByCreatedAtDesc();
    
    List<Dashboard> findByTitleContainingIgnoreCase(String title);
    
    List<Dashboard> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
}