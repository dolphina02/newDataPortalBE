package com.lina.dataportal.repository;

import com.lina.dataportal.domain.dashboard.DashboardSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardSubscriptionRepository extends JpaRepository<DashboardSubscription, Long> {
    
    List<DashboardSubscription> findByUserIdOrderByDisplayOrder(String userId);
    
    List<DashboardSubscription> findByUserIdAndIsFavoriteTrueOrderByDisplayOrder(String userId);
    
    Optional<DashboardSubscription> findByUserIdAndDashboardId(String userId, Long dashboardId);
    
    @Query("SELECT ds FROM DashboardSubscription ds JOIN FETCH ds.dashboard WHERE ds.userId = :userId ORDER BY ds.displayOrder")
    List<DashboardSubscription> findByUserIdWithDashboard(@Param("userId") String userId);
    
    @Query("SELECT ds FROM DashboardSubscription ds WHERE ds.userId = :userId ORDER BY ds.lastAccessed DESC")
    List<DashboardSubscription> findByUserIdOrderByLastAccessed(@Param("userId") String userId);
    
    @Query("SELECT COUNT(ds) FROM DashboardSubscription ds WHERE ds.dashboardId = :dashboardId")
    Long countSubscribersByDashboardId(@Param("dashboardId") Long dashboardId);
    
    boolean existsByUserIdAndDashboardId(String userId, Long dashboardId);
    
    void deleteByUserIdAndDashboardId(String userId, Long dashboardId);
}