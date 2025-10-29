package com.lina.dataportal.repository;

import com.lina.dataportal.domain.model.MLModel;
import com.lina.dataportal.domain.model.ModelStatus;
import com.lina.dataportal.domain.model.ModelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MLModelRepository extends JpaRepository<MLModel, Long> {
    
    List<MLModel> findByType(ModelType type);
    
    List<MLModel> findByStatus(ModelStatus status);
    
    List<MLModel> findByCreatedBy(String createdBy);
    
    @Query("SELECT m FROM MLModel m WHERE m.name LIKE %:keyword% OR m.description LIKE %:keyword%")
    List<MLModel> findByKeyword(@Param("keyword") String keyword);
    
    // @Query("SELECT m FROM MLModel m WHERE :tag MEMBER OF m.tags")
    // List<MLModel> findByTag(@Param("tag") String tag); // 임시 비활성화
    
    @Query("SELECT m FROM MLModel m WHERE m.status = 'DEPLOYED' ORDER BY m.accuracy DESC")
    List<MLModel> findDeployedModelsByAccuracy();
    
    @Query("SELECT m FROM MLModel m ORDER BY m.createdAt DESC")
    List<MLModel> findRecentModels();
    
    // HomeService에서 필요한 메서드들
    List<MLModel> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}