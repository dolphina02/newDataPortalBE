package com.lina.dataportal.service;

import com.lina.dataportal.domain.api.ApiCategory;
import com.lina.dataportal.domain.api.ApiEndpoint;
import com.lina.dataportal.domain.api.HttpMethod;
import com.lina.dataportal.repository.ApiEndpointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ApiService {
    
    private final ApiEndpointRepository apiEndpointRepository;
    
    @Autowired
    public ApiService(ApiEndpointRepository apiEndpointRepository) {
        this.apiEndpointRepository = apiEndpointRepository;
    }
    
    public List<ApiEndpoint> getAllEndpoints() {
        return apiEndpointRepository.findAll();
    }
    
    public Optional<ApiEndpoint> getEndpointById(Long id) {
        return apiEndpointRepository.findById(id);
    }
    
    public List<ApiEndpoint> getEndpointsByCategory(ApiCategory category) {
        return apiEndpointRepository.findByCategory(category);
    }
    
    public List<ApiEndpoint> getActiveEndpoints() {
        return apiEndpointRepository.findActiveEndpointsByCategory();
    }
    
    public List<ApiEndpoint> searchEndpoints(String keyword) {
        return apiEndpointRepository.findByKeyword(keyword);
    }
    
    public ApiEndpoint createEndpoint(ApiEndpoint apiEndpoint) {
        return apiEndpointRepository.save(apiEndpoint);
    }
    
    public ApiEndpoint updateEndpoint(Long id, ApiEndpoint endpointDetails) {
        ApiEndpoint apiEndpoint = apiEndpointRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("API Endpoint not found with id: " + id));
        
        apiEndpoint.setName(endpointDetails.getName());
        apiEndpoint.setDescription(endpointDetails.getDescription());
        apiEndpoint.setEndpoint(endpointDetails.getEndpoint());
        apiEndpoint.setMethod(endpointDetails.getMethod());
        apiEndpoint.setCategory(endpointDetails.getCategory());
        apiEndpoint.setRequestSchema(endpointDetails.getRequestSchema());
        apiEndpoint.setResponseSchema(endpointDetails.getResponseSchema());
        apiEndpoint.setTags(endpointDetails.getTags());
        apiEndpoint.setDocumentationUrl(endpointDetails.getDocumentationUrl());
        apiEndpoint.setIsActive(endpointDetails.getIsActive());
        
        return apiEndpointRepository.save(apiEndpoint);
    }
    
    public void deleteEndpoint(Long id) {
        apiEndpointRepository.deleteById(id);
    }
}