package com.lina.dataportal.service;

import com.lina.dataportal.domain.user.UserToken;
import com.lina.dataportal.repository.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserTokenService {
    
    @Autowired
    private UserTokenRepository userTokenRepository;
    
    @Autowired
    private TokenEncryptionService tokenEncryptionService;
    
    @Transactional
    public UserToken saveDatabricksToken(String userId, String accessToken, String workspaceUrl, 
                                       String clusterId, String warehouseId) {
        // Deactivate existing tokens
        userTokenRepository.deactivateTokensByUserIdAndType(userId, "DATABRICKS_PAT");
        
        // Encrypt the token
        String encryptedToken = tokenEncryptionService.encrypt(accessToken);
        
        // Create new token
        UserToken userToken = new UserToken(userId, "DATABRICKS_PAT", encryptedToken, workspaceUrl);
        userToken.setClusterId(clusterId);
        userToken.setWarehouseId(warehouseId);
        
        return userTokenRepository.save(userToken);
    }
    
    public Optional<UserToken> getDatabricksToken(String userId) {
        return userTokenRepository.findActiveDatabricksToken(userId);
    }
    
    public String decryptToken(UserToken userToken) {
        return tokenEncryptionService.decrypt(userToken.getEncryptedToken());
    }
    
    @Transactional
    public void updateLastUsed(Long tokenId) {
        userTokenRepository.findById(tokenId).ifPresent(token -> {
            token.setLastUsedAt(LocalDateTime.now());
            userTokenRepository.save(token);
        });
    }
    
    @Transactional
    public void deactivateToken(String userId, String tokenType) {
        userTokenRepository.deactivateTokensByUserIdAndType(userId, tokenType);
    }
    
    public boolean hasValidDatabricksToken(String userId) {
        Optional<UserToken> token = getDatabricksToken(userId);
        return token.isPresent() && token.get().getIsActive();
    }
}