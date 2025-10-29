package com.lina.dataportal.domain.stt;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "call_records")
public class CallRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "call_id", nullable = false, unique = true)
    private String callId;
    
    @NotBlank
    @Column(name = "customer_id", nullable = false)
    private String customerId;
    
    @Column(name = "customer_name")
    private String customerName;
    
    @Column(name = "customer_phone")
    private String customerPhone;
    
    @NotBlank
    @Column(name = "agent_id", nullable = false)
    private String agentId;
    
    @Column(name = "agent_name")
    private String agentName;
    
    @Column(name = "agent_department")
    private String agentDepartment;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private CustomerType customerType;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private CallType callType;
    
    @Column(name = "call_duration")
    private Integer callDuration; // seconds
    
    @Column(name = "audio_file_path")
    private String audioFilePath;
    
    @Column(name = "transcript", columnDefinition = "TEXT")
    private String transcript;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ProcessingStatus processingStatus = ProcessingStatus.PENDING;
    
    @Column(name = "call_date", nullable = false)
    private LocalDateTime callDate;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "callRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CallSegment> segments;
    
    @OneToMany(mappedBy = "callRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<KeywordMatch> keywordMatches;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public CallRecord() {}
    
    public CallRecord(String callId, String customerId, String agentId, CustomerType customerType, CallType callType, LocalDateTime callDate) {
        this.callId = callId;
        this.customerId = customerId;
        this.agentId = agentId;
        this.customerType = customerType;
        this.callType = callType;
        this.callDate = callDate;
    }
    
    // Business Methods
    public void markAsProcessed() {
        this.processingStatus = ProcessingStatus.COMPLETED;
        this.processedAt = LocalDateTime.now();
    }
    
    public void markAsFailed() {
        this.processingStatus = ProcessingStatus.FAILED;
        this.processedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCallId() { return callId; }
    public void setCallId(String callId) { this.callId = callId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    
    public String getAgentDepartment() { return agentDepartment; }
    public void setAgentDepartment(String agentDepartment) { this.agentDepartment = agentDepartment; }
    
    public CustomerType getCustomerType() { return customerType; }
    public void setCustomerType(CustomerType customerType) { this.customerType = customerType; }
    
    public CallType getCallType() { return callType; }
    public void setCallType(CallType callType) { this.callType = callType; }
    
    public Integer getCallDuration() { return callDuration; }
    public void setCallDuration(Integer callDuration) { this.callDuration = callDuration; }
    
    public String getAudioFilePath() { return audioFilePath; }
    public void setAudioFilePath(String audioFilePath) { this.audioFilePath = audioFilePath; }
    
    public String getTranscript() { return transcript; }
    public void setTranscript(String transcript) { this.transcript = transcript; }
    
    public ProcessingStatus getProcessingStatus() { return processingStatus; }
    public void setProcessingStatus(ProcessingStatus processingStatus) { this.processingStatus = processingStatus; }
    
    public LocalDateTime getCallDate() { return callDate; }
    public void setCallDate(LocalDateTime callDate) { this.callDate = callDate; }
    
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<CallSegment> getSegments() { return segments; }
    public void setSegments(List<CallSegment> segments) { this.segments = segments; }
    
    public List<KeywordMatch> getKeywordMatches() { return keywordMatches; }
    public void setKeywordMatches(List<KeywordMatch> keywordMatches) { this.keywordMatches = keywordMatches; }
}