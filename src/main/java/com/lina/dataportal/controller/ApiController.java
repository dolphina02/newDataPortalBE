package com.lina.dataportal.controller;

import com.lina.dataportal.domain.api.ApiCategory;
import com.lina.dataportal.domain.api.ApiEndpoint;
import com.lina.dataportal.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/apis")
@CrossOrigin(origins = "*")
public class ApiController {
    
    private final ApiService apiService;
    
    @Autowired
    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }
    
    @GetMapping
    public ResponseEntity<List<ApiEndpoint>> getAllEndpoints(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active) {
        
        List<ApiEndpoint> endpoints;
        
        if (active != null && active) {
            endpoints = apiService.getActiveEndpoints();
        } else if (search != null && !search.trim().isEmpty()) {
            endpoints = apiService.searchEndpoints(search);
        } else if (category != null) {
            ApiCategory apiCategory = ApiCategory.valueOf(category.toUpperCase());
            endpoints = apiService.getEndpointsByCategory(apiCategory);
        } else {
            endpoints = apiService.getAllEndpoints();
        }
        
        return ResponseEntity.ok(endpoints);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiEndpoint> getEndpointById(@PathVariable Long id) {
        return apiService.getEndpointById(id)
            .map(endpoint -> ResponseEntity.ok(endpoint))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<ApiEndpoint> createEndpoint(@Valid @RequestBody ApiEndpoint apiEndpoint) {
        ApiEndpoint createdEndpoint = apiService.createEndpoint(apiEndpoint);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEndpoint);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiEndpoint> updateEndpoint(
            @PathVariable Long id, 
            @Valid @RequestBody ApiEndpoint endpointDetails) {
        try {
            ApiEndpoint updatedEndpoint = apiService.updateEndpoint(id, endpointDetails);
            return ResponseEntity.ok(updatedEndpoint);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEndpoint(@PathVariable Long id) {
        try {
            apiService.deleteEndpoint(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}