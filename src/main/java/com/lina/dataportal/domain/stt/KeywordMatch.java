package com.lina.dataportal.domain.stt;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "keyword_matches")
public class KeywordMatch {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "call_record_id", nullable = false)
    private Long callRecordId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "call_record_id", insertable = false, updatable = false)
    private CallRecord callRecord;
    
    @NotBlank
    @Column(nullable = false)
    private String keyword;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private KeywordCategory category;
    
    @NotNull
    @Column(name = "match_count", nullable = false)
    private Integer matchCount = 1;
    
    @Column(name = "first_occurrence_time")
    private Integer firstOccurrenceTime; // seconds
    
    @Column(name = "context_text", columnDefinition = "TEXT")
    private String contextText;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Speaker speaker;
    
    @Column(name = "sentiment_score")
    private Double sentimentScore;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public KeywordMatch() {}
    
    public KeywordMatch(Long callRecordId, String keyword, KeywordCategory category, Speaker speaker) {
        this.callRecordId = callRecordId;
        this.keyword = keyword;
        this.category = category;
        this.speaker = speaker;
    }
    
    // Business Methods
    public void incrementMatchCount() {
        this.matchCount++;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCallRecordId() { return callRecordId; }
    public void setCallRecordId(Long callRecordId) { this.callRecordId = callRecordId; }
    
    public CallRecord getCallRecord() { return callRecord; }
    public void setCallRecord(CallRecord callRecord) { this.callRecord = callRecord; }
    
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    
    public KeywordCategory getCategory() { return category; }
    public void setCategory(KeywordCategory category) { this.category = category; }
    
    public Integer getMatchCount() { return matchCount; }
    public void setMatchCount(Integer matchCount) { this.matchCount = matchCount; }
    
    public Integer getFirstOccurrenceTime() { return firstOccurrenceTime; }
    public void setFirstOccurrenceTime(Integer firstOccurrenceTime) { this.firstOccurrenceTime = firstOccurrenceTime; }
    
    public String getContextText() { return contextText; }
    public void setContextText(String contextText) { this.contextText = contextText; }
    
    public Speaker getSpeaker() { return speaker; }
    public void setSpeaker(Speaker speaker) { this.speaker = speaker; }
    
    public Double getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(Double sentimentScore) { this.sentimentScore = sentimentScore; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}