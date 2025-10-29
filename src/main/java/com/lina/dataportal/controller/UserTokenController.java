package com.lina.dataportal.controller;

import com.lina.dataportal.domain.user.UserToken;
import com.lina.dataportal.service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-tokens")
@CrossOrigin(origins = "*")
public class UserTokenController {

    @Autowired
    private UserTokenService userTokenService;

    @PostMapping("/databricks")
    public ResponseEntity<Map<String, Object>> saveDatabricksToken(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        String accessToken = (String) request.get("accessToken");
        String workspaceUrl = (String) request.get("workspaceUrl");
        String clusterId = (String) request.get("clusterId");
        String warehouseId = (String) request.get("warehouseId");
        
        if (userId == null || accessToken == null || workspaceUrl == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "userId, accessToken, and workspaceUrl are required"
            ));
        }
        
        try {
            UserToken savedToken = userTokenService.saveDatabricksToken(
                userId, accessToken, workspaceUrl, clusterId, warehouseId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Databricks token saved successfully",
                "tokenId", savedToken.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Failed to save token: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/databricks/{userId}")
    public ResponseEntity<Map<String, Object>> getDatabricksTokenInfo(@PathVariable String userId) {
        Optional<UserToken> tokenOpt = userTokenService.getDatabricksToken(userId);
        
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                "hasToken", false,
                "message", "No Databricks token found for user"
            ));
        }
        
        UserToken token = tokenOpt.get();
        return ResponseEntity.ok(Map.of(
            "hasToken", true,
            "workspaceUrl", token.getWorkspaceUrl(),
            "clusterId", token.getClusterId() != null ? token.getClusterId() : "",
            "warehouseId", token.getWarehouseId() != null ? token.getWarehouseId() : "",
            "createdAt", token.getCreatedAt(),
            "lastUsedAt", token.getLastUsedAt()
        ));
    }

    @DeleteMapping("/databricks/{userId}")
    public ResponseEntity<Map<String, Object>> deleteDatabricksToken(@PathVariable String userId) {
        try {
            userTokenService.deactivateToken(userId, "DATABRICKS_PAT");
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Databricks token deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Failed to delete token: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/databricks/{userId}/status")
    public ResponseEntity<Map<String, Object>> checkDatabricksTokenStatus(@PathVariable String userId) {
        boolean hasValidToken = userTokenService.hasValidDatabricksToken(userId);
        
        return ResponseEntity.ok(Map.of(
            "hasValidToken", hasValidToken,
            "status", hasValidToken ? "active" : "inactive"
        ));
    }
}