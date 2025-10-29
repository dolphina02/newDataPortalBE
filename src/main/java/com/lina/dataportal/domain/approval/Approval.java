package com.lina.dataportal.domain.approval;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "approvals")
public class Approval {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ApprovalType type;
    
    @NotBlank
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;
    
    @NotBlank
    @Column(nullable = false)
    private String requester;
    
    private String reviewer;
    
    @Column(name = "request_date", nullable = false, updatable = false)
    private LocalDateTime requestDate;
    
    @Column(name = "review_date")
    private LocalDateTime reviewDate;
    
    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment;
    
    @Column(name = "current_step", nullable = false)
    private Integer currentStep = 1;
    
    @Column(name = "total_steps", nullable = false)
    private Integer totalSteps = 3;
    
    @PrePersist
    protected void onCreate() {
        requestDate = LocalDateTime.now();
    }
    
    // Constructors
    public Approval() {}
    
    public Approval(ApprovalType type, String title, String description, String requester) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.requester = requester;
    }
    
    // Business Methods
    public void approve(String reviewer, String comment) {
        this.status = ApprovalStatus.APPROVED;
        this.reviewer = reviewer;
        this.reviewComment = comment;
        this.reviewDate = LocalDateTime.now();
        this.currentStep = totalSteps;
    }
    
    public void reject(String reviewer, String comment) {
        this.status = ApprovalStatus.REJECTED;
        this.reviewer = reviewer;
        this.reviewComment = comment;
        this.reviewDate = LocalDateTime.now();
    }
    
    public void nextStep() {
        if (currentStep < totalSteps) {
            currentStep++;
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public ApprovalType getType() { return type; }
    public void setType(ApprovalType type) { this.type = type; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ApprovalStatus getStatus() { return status; }
    public void setStatus(ApprovalStatus status) { this.status = status; }
    
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    
    public String getRequester() { return requester; }
    public void setRequester(String requester) { this.requester = requester; }
    
    public String getReviewer() { return reviewer; }
    public void setReviewer(String reviewer) { this.reviewer = reviewer; }
    
    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }
    
    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }
    
    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
    
    public Integer getCurrentStep() { return currentStep; }
    public void setCurrentStep(Integer currentStep) { this.currentStep = currentStep; }
    
    public Integer getTotalSteps() { return totalSteps; }
    public void setTotalSteps(Integer totalSteps) { this.totalSteps = totalSteps; }
}