package com.lina.dataportal.controller;

import com.lina.dataportal.domain.approval.Approval;
import com.lina.dataportal.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/approvals")
@CrossOrigin(origins = "*")
public class ApprovalController {
    
    private final ApprovalService approvalService;
    
    @Autowired
    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }
    
    @GetMapping("/submitted")
    public ResponseEntity<List<Approval>> getSubmittedApprovals(
            @RequestParam(defaultValue = "admin") String requester) {
        List<Approval> approvals = approvalService.getSubmittedApprovals(requester);
        return ResponseEntity.ok(approvals);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<Approval>> getPendingApprovals() {
        List<Approval> approvals = approvalService.getPendingApprovals();
        return ResponseEntity.ok(approvals);
    }
    
    @GetMapping("/completed")
    public ResponseEntity<List<Approval>> getCompletedApprovals() {
        List<Approval> approvals = approvalService.getCompletedApprovals();
        return ResponseEntity.ok(approvals);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Approval> getApprovalById(@PathVariable Long id) {
        return approvalService.getApprovalById(id)
            .map(approval -> ResponseEntity.ok(approval))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/deploy")
    public ResponseEntity<Approval> createDeployRequest(@RequestBody Map<String, String> request) {
        String title = request.get("title");
        String description = request.get("description");
        String requester = request.getOrDefault("requester", "admin");
        
        Approval approval = approvalService.createDeployRequest(title, description, requester);
        return ResponseEntity.status(HttpStatus.CREATED).body(approval);
    }
    
    @PostMapping("/subscribe")
    public ResponseEntity<Approval> createSubscribeRequest(@RequestBody Map<String, String> request) {
        String title = request.get("title");
        String description = request.get("description");
        String requester = request.getOrDefault("requester", "admin");
        
        Approval approval = approvalService.createDashboardSubscribeRequest(title, description, requester);
        return ResponseEntity.status(HttpStatus.CREATED).body(approval);
    }
    
    @PostMapping("/data-access")
    public ResponseEntity<Approval> createDataAccessRequest(@RequestBody Map<String, String> request) {
        String title = request.get("title");
        String description = request.get("description");
        String requester = request.getOrDefault("requester", "admin");
        
        Approval approval = approvalService.createDataAccessRequest(title, description, requester);
        return ResponseEntity.status(HttpStatus.CREATED).body(approval);
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<Approval> approveRequest(
            @PathVariable Long id, 
            @RequestBody Map<String, String> request) {
        try {
            String reviewer = request.getOrDefault("reviewer", "admin");
            String comment = request.get("comment");
            
            Approval approval = approvalService.approveRequest(id, reviewer, comment);
            return ResponseEntity.ok(approval);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/reject")
    public ResponseEntity<Approval> rejectRequest(
            @PathVariable Long id, 
            @RequestBody Map<String, String> request) {
        try {
            String reviewer = request.getOrDefault("reviewer", "admin");
            String comment = request.get("comment");
            
            Approval approval = approvalService.rejectRequest(id, reviewer, comment);
            return ResponseEntity.ok(approval);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/next-step")
    public ResponseEntity<Approval> moveToNextStep(@PathVariable Long id) {
        try {
            Approval approval = approvalService.moveToNextStep(id);
            return ResponseEntity.ok(approval);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}