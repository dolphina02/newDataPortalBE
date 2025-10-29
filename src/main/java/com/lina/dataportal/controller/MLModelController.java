package com.lina.dataportal.controller;

import com.lina.dataportal.domain.model.MLModel;
import com.lina.dataportal.domain.model.ModelStatus;
import com.lina.dataportal.domain.model.ModelType;
import com.lina.dataportal.service.MLModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/models")
@CrossOrigin(origins = "*")
public class MLModelController {
    
    private final MLModelService mlModelService;
    
    @Autowired
    public MLModelController(MLModelService mlModelService) {
        this.mlModelService = mlModelService;
    }
    
    @GetMapping
    public ResponseEntity<List<MLModel>> getAllModels(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        
        List<MLModel> models;
        
        if (search != null && !search.trim().isEmpty()) {
            models = mlModelService.searchModels(search);
        } else if (type != null) {
            ModelType modelType = ModelType.valueOf(type.toUpperCase());
            models = mlModelService.getModelsByType(modelType);
        } else if (status != null) {
            ModelStatus modelStatus = ModelStatus.valueOf(status.toUpperCase());
            models = mlModelService.getModelsByStatus(modelStatus);
        } else {
            models = mlModelService.getAllModels();
        }
        
        return ResponseEntity.ok(models);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MLModel> getModelById(@PathVariable Long id) {
        return mlModelService.getModelById(id)
            .map(model -> ResponseEntity.ok(model))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/deployed")
    public ResponseEntity<List<MLModel>> getDeployedModels() {
        List<MLModel> models = mlModelService.getDeployedModels();
        return ResponseEntity.ok(models);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<MLModel>> getRecentModels() {
        List<MLModel> models = mlModelService.getRecentModels();
        return ResponseEntity.ok(models);
    }
    
    @PostMapping
    public ResponseEntity<MLModel> createModel(@Valid @RequestBody MLModel model) {
        MLModel createdModel = mlModelService.createModel(model);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdModel);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MLModel> updateModel(
            @PathVariable Long id, 
            @Valid @RequestBody MLModel modelDetails) {
        try {
            MLModel updatedModel = mlModelService.updateModel(id, modelDetails);
            return ResponseEntity.ok(updatedModel);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/deploy")
    public ResponseEntity<MLModel> deployModel(@PathVariable Long id) {
        try {
            MLModel model = mlModelService.deployModel(id);
            return ResponseEntity.ok(model);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/retire")
    public ResponseEntity<MLModel> retireModel(@PathVariable Long id) {
        try {
            MLModel model = mlModelService.retireModel(id);
            return ResponseEntity.ok(model);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        try {
            mlModelService.deleteModel(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}