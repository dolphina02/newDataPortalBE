package com.lina.dataportal.repository;

import com.lina.dataportal.domain.api.ApiCategory;
import com.lina.dataportal.domain.api.ApiEndpoint;
import com.lina.dataportal.domain.api.HttpMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiEndpointRepository extends JpaRepository<ApiEndpoint, Long> {
    
    List<ApiEndpoint> findByCategory(ApiCategory category);
    
    List<ApiEndpoint> findByMethod(HttpMethod method);
    
    List<ApiEndpoint> findByIsActiveTrue();
    
    @Query("SELECT a FROM ApiEndpoint a WHERE a.name LIKE %:keyword% OR a.description LIKE %:keyword%")
    List<ApiEndpoint> findByKeyword(@Param("keyword") String keyword);
    
    // @Query("SELECT a FROM ApiEndpoint a WHERE :tag MEMBER OF a.tags")
    // List<ApiEndpoint> findByTag(@Param("tag") String tag); // 임시 비활성화
    
    @Query("SELECT a FROM ApiEndpoint a WHERE a.isActive = true ORDER BY a.category, a.name")
    List<ApiEndpoint> findActiveEndpointsByCategory();
    
    // HomeService에서 필요한 메서드들
    List<ApiEndpoint> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}