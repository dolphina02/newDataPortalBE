package com.lina.dataportal.controller;

import com.lina.dataportal.domain.dashboard.DashboardSubscription;
import com.lina.dataportal.service.DashboardSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/my-dashboards")
@CrossOrigin(origins = "*")
public class MyDashboardController {
    
    private final DashboardSubscriptionService subscriptionService;
    
    @Autowired
    public MyDashboardController(DashboardSubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }
    
    @GetMapping
    public ResponseEntity<List<DashboardSubscription>> getMyDashboards(
            @RequestParam(defaultValue = "admin") String userId) {
        List<DashboardSubscription> subscriptions = subscriptionService.getUserDashboards(userId);
        return ResponseEntity.ok(subscriptions);
    }
    
    @GetMapping("/favorites")
    public ResponseEntity<List<DashboardSubscription>> getMyFavoriteDashboards(
            @RequestParam(defaultValue = "admin") String userId) {
        List<DashboardSubscription> subscriptions = subscriptionService.getUserFavoriteDashboards(userId);
        return ResponseEntity.ok(subscriptions);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<DashboardSubscription>> getRecentlyAccessedDashboards(
            @RequestParam(defaultValue = "admin") String userId) {
        List<DashboardSubscription> subscriptions = subscriptionService.getRecentlyAccessedDashboards(userId);
        return ResponseEntity.ok(subscriptions);
    }
    
    @PostMapping("/subscribe")
    public ResponseEntity<DashboardSubscription> subscribeToDashboard(
            @RequestBody Map<String, Object> request) {
        String userId = (String) request.getOrDefault("userId", "admin");
        Long dashboardId = Long.valueOf(request.get("dashboardId").toString());
        
        try {
            DashboardSubscription subscription = subscriptionService.subscribeToDashboard(userId, dashboardId);
            return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/unsubscribe")
    public ResponseEntity<Void> unsubscribeFromDashboard(
            @RequestParam(defaultValue = "admin") String userId,
            @RequestParam Long dashboardId) {
        try {
            subscriptionService.unsubscribeFromDashboard(userId, dashboardId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/reorder")
    public ResponseEntity<List<DashboardSubscription>> reorderDashboards(
            @RequestBody Map<String, Object> request) {
        String userId = (String) request.getOrDefault("userId", "admin");
        @SuppressWarnings("unchecked")
        List<Long> dashboardIds = (List<Long>) request.get("dashboardIds");
        
        List<DashboardSubscription> subscriptions = subscriptionService.reorderDashboards(userId, dashboardIds);
        return ResponseEntity.ok(subscriptions);
    }
    
    @PutMapping("/favorite")
    public ResponseEntity<DashboardSubscription> toggleFavorite(
            @RequestBody Map<String, Object> request) {
        String userId = (String) request.getOrDefault("userId", "admin");
        Long dashboardId = Long.valueOf(request.get("dashboardId").toString());
        
        try {
            DashboardSubscription subscription = subscriptionService.toggleFavorite(userId, dashboardId);
            return ResponseEntity.ok(subscription);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/custom-title")
    public ResponseEntity<DashboardSubscription> updateCustomTitle(
            @RequestBody Map<String, Object> request) {
        String userId = (String) request.getOrDefault("userId", "admin");
        Long dashboardId = Long.valueOf(request.get("dashboardId").toString());
        String customTitle = (String) request.get("customTitle");
        
        try {
            DashboardSubscription subscription = subscriptionService.updateCustomTitle(userId, dashboardId, customTitle);
            return ResponseEntity.ok(subscription);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/access")
    public ResponseEntity<DashboardSubscription> recordDashboardAccess(
            @RequestBody Map<String, Object> request) {
        String userId = (String) request.getOrDefault("userId", "admin");
        Long dashboardId = Long.valueOf(request.get("dashboardId").toString());
        
        try {
            DashboardSubscription subscription = subscriptionService.recordAccess(userId, dashboardId);
            return ResponseEntity.ok(subscription);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/subscription-status")
    public ResponseEntity<Map<String, Object>> getSubscriptionStatus(
            @RequestParam(defaultValue = "admin") String userId,
            @RequestParam Long dashboardId) {
        boolean isSubscribed = subscriptionService.isUserSubscribed(userId, dashboardId);
        Long subscriberCount = subscriptionService.getDashboardSubscriberCount(dashboardId);
        
        Map<String, Object> status = Map.of(
            "isSubscribed", isSubscribed,
            "subscriberCount", subscriberCount
        );
        
        return ResponseEntity.ok(status);
    }
}