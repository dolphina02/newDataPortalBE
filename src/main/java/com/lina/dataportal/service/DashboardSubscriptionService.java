package com.lina.dataportal.service;

import com.lina.dataportal.domain.dashboard.DashboardSubscription;
import com.lina.dataportal.repository.DashboardSubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DashboardSubscriptionService {
    
    private final DashboardSubscriptionRepository subscriptionRepository;
    
    @Autowired
    public DashboardSubscriptionService(DashboardSubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }
    
    public List<DashboardSubscription> getUserDashboards(String userId) {
        return subscriptionRepository.findByUserIdWithDashboard(userId);
    }
    
    public List<DashboardSubscription> getUserFavoriteDashboards(String userId) {
        return subscriptionRepository.findByUserIdAndIsFavoriteTrueOrderByDisplayOrder(userId);
    }
    
    public List<DashboardSubscription> getRecentlyAccessedDashboards(String userId) {
        return subscriptionRepository.findByUserIdOrderByLastAccessed(userId);
    }
    
    public DashboardSubscription subscribeToDashboard(String userId, Long dashboardId) {
        // 이미 구독 중인지 확인
        if (subscriptionRepository.existsByUserIdAndDashboardId(userId, dashboardId)) {
            throw new RuntimeException("Already subscribed to this dashboard");
        }
        
        // 새로운 구독의 display order 계산
        List<DashboardSubscription> userSubscriptions = subscriptionRepository.findByUserIdOrderByDisplayOrder(userId);
        int nextOrder = userSubscriptions.size();
        
        DashboardSubscription subscription = new DashboardSubscription(userId, dashboardId, nextOrder);
        return subscriptionRepository.save(subscription);
    }
    
    public void unsubscribeFromDashboard(String userId, Long dashboardId) {
        subscriptionRepository.deleteByUserIdAndDashboardId(userId, dashboardId);
    }
    
    public DashboardSubscription updateDisplayOrder(String userId, Long dashboardId, Integer newOrder) {
        DashboardSubscription subscription = subscriptionRepository.findByUserIdAndDashboardId(userId, dashboardId)
            .orElseThrow(() -> new RuntimeException("Subscription not found"));
        
        subscription.setDisplayOrder(newOrder);
        return subscriptionRepository.save(subscription);
    }
    
    public DashboardSubscription toggleFavorite(String userId, Long dashboardId) {
        DashboardSubscription subscription = subscriptionRepository.findByUserIdAndDashboardId(userId, dashboardId)
            .orElseThrow(() -> new RuntimeException("Subscription not found"));
        
        subscription.toggleFavorite();
        return subscriptionRepository.save(subscription);
    }
    
    public DashboardSubscription updateCustomTitle(String userId, Long dashboardId, String customTitle) {
        DashboardSubscription subscription = subscriptionRepository.findByUserIdAndDashboardId(userId, dashboardId)
            .orElseThrow(() -> new RuntimeException("Subscription not found"));
        
        subscription.setCustomTitle(customTitle);
        return subscriptionRepository.save(subscription);
    }
    
    public DashboardSubscription recordAccess(String userId, Long dashboardId) {
        DashboardSubscription subscription = subscriptionRepository.findByUserIdAndDashboardId(userId, dashboardId)
            .orElseThrow(() -> new RuntimeException("Subscription not found"));
        
        subscription.updateLastAccessed();
        return subscriptionRepository.save(subscription);
    }
    
    public Long getDashboardSubscriberCount(Long dashboardId) {
        return subscriptionRepository.countSubscribersByDashboardId(dashboardId);
    }
    
    public boolean isUserSubscribed(String userId, Long dashboardId) {
        return subscriptionRepository.existsByUserIdAndDashboardId(userId, dashboardId);
    }
    
    public List<DashboardSubscription> reorderDashboards(String userId, List<Long> dashboardIds) {
        List<DashboardSubscription> subscriptions = subscriptionRepository.findByUserIdOrderByDisplayOrder(userId);
        
        for (int i = 0; i < dashboardIds.size(); i++) {
            Long dashboardId = dashboardIds.get(i);
            final int displayOrder = i; // final 변수로 만들어서 lambda에서 사용 가능하게 함
            subscriptions.stream()
                .filter(sub -> sub.getDashboardId().equals(dashboardId))
                .findFirst()
                .ifPresent(sub -> sub.setDisplayOrder(displayOrder));
        }
        
        return subscriptionRepository.saveAll(subscriptions);
    }
}