package com.lina.dataportal.service;

import com.lina.dataportal.domain.model.MLModel;
import com.lina.dataportal.domain.model.ModelStatus;
import com.lina.dataportal.domain.model.ModelType;
import com.lina.dataportal.repository.MLModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MLModelService {
    
    private final MLModelRepository mlModelRepository;
    
    @Autowired
    public MLModelService(MLModelRepository mlModelRepository) {
        this.mlModelRepository = mlModelRepository;
    }
    
    public List<MLModel> getAllModels() {
        return mlModelRepository.findAll();
    }
    
    public Optional<MLModel> getModelById(Long id) {
        return mlModelRepository.findById(id);
    }
    
    public List<MLModel> getModelsByType(ModelType type) {
        return mlModelRepository.findByType(type);
    }
    
    public List<MLModel> getModelsByStatus(ModelStatus status) {
        return mlModelRepository.findByStatus(status);
    }
    
    public List<MLModel> getDeployedModels() {
        return mlModelRepository.findDeployedModelsByAccuracy();
    }
    
    public List<MLModel> searchModels(String keyword) {
        return mlModelRepository.findByKeyword(keyword);
    }
    
    public List<MLModel> getRecentModels() {
        return mlModelRepository.findRecentModels();
    }
    
    public MLModel createModel(MLModel model) {
        return mlModelRepository.save(model);
    }
    
    public MLModel updateModel(Long id, MLModel modelDetails) {
        MLModel model = mlModelRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Model not found with id: " + id));
        
        model.setName(modelDetails.getName());
        model.setDescription(modelDetails.getDescription());
        model.setType(modelDetails.getType());
        model.setVersion(modelDetails.getVersion());
        model.setAccuracy(modelDetails.getAccuracy());
        model.setF1Score(modelDetails.getF1Score());
        model.setResponseTime(modelDetails.getResponseTime());
        model.setFeatureImportance(modelDetails.getFeatureImportance());
        model.setTags(modelDetails.getTags());
        model.setModelPath(modelDetails.getModelPath());
        
        return mlModelRepository.save(model);
    }
    
    public MLModel deployModel(Long id) {
        MLModel model = mlModelRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Model not found with id: " + id));
        
        model.deploy();
        return mlModelRepository.save(model);
    }
    
    public MLModel retireModel(Long id) {
        MLModel model = mlModelRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Model not found with id: " + id));
        
        model.retire();
        return mlModelRepository.save(model);
    }
    
    public void deleteModel(Long id) {
        mlModelRepository.deleteById(id);
    }
}