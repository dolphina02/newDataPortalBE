package com.lina.dataportal.service;

import com.lina.dataportal.domain.dashboard.Dashboard;
import com.lina.dataportal.domain.dashboard.DashboardType;
import com.lina.dataportal.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DashboardService {
    
    private final DashboardRepository dashboardRepository;
    
    @Autowired
    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }
    
    public List<Dashboard> getAllDashboards() {
        return dashboardRepository.findAll();
    }
    
    public Optional<Dashboard> getDashboardById(Long id) {
        return dashboardRepository.findById(id);
    }
    
    public List<Dashboard> getDashboardsByType(DashboardType type) {
        return dashboardRepository.findByType(type);
    }
    
    public List<Dashboard> getDashboardsByCategory(String category) {
        return dashboardRepository.findByCategory(category);
    }
    
    public List<Dashboard> searchDashboards(String keyword) {
        return dashboardRepository.findByKeyword(keyword);
    }
    
    public Dashboard createDashboard(Dashboard dashboard) {
        return dashboardRepository.save(dashboard);
    }
    
    public Dashboard updateDashboard(Long id, Dashboard dashboardDetails) {
        Dashboard dashboard = dashboardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Dashboard not found with id: " + id));
        
        dashboard.setTitle(dashboardDetails.getTitle());
        dashboard.setDescription(dashboardDetails.getDescription());
        dashboard.setCategory(dashboardDetails.getCategory());
        dashboard.setType(dashboardDetails.getType());
        dashboard.setTags(dashboardDetails.getTags());
        dashboard.setImage(dashboardDetails.getImage());
        dashboard.setConfig(dashboardDetails.getConfig());
        dashboard.setDashboardUrl(dashboardDetails.getDashboardUrl());
        
        return dashboardRepository.save(dashboard);
    }
    
    public Dashboard installDashboard(Long id) {
        Dashboard dashboard = dashboardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Dashboard not found with id: " + id));
        
        dashboard.setDownloads(dashboard.getDownloads() + 1);
        return dashboardRepository.save(dashboard);
    }
    
    public Dashboard rateDashboard(Long id, Double rating) {
        Dashboard dashboard = dashboardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Dashboard not found with id: " + id));
        
        // Simple rating update - in real app, you'd calculate average from multiple ratings
        dashboard.setRating(rating);
        return dashboardRepository.save(dashboard);
    }
    
    public void deleteDashboard(Long id) {
        dashboardRepository.deleteById(id);
    }
    
    public List<Dashboard> getPopularDashboards() {
        return dashboardRepository.findTopByDownloads();
    }
    
    public List<Dashboard> getTopRatedDashboards() {
        return dashboardRepository.findTopByRating();
    }
}