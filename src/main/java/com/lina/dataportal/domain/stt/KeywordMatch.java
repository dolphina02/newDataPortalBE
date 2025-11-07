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
    private Long id; // 예: 1
    
    @NotNull
    @Column(name = "call_record_id", nullable = false)
    private Long callRecordId; // 예: 1 (연관된 CallRecord의 ID)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "call_record_id", insertable = false, updatable = false)
    private CallRecord callRecord; // 연관된 통화 기록
    
    @NotBlank
    @Column(nullable = false)
    private String keyword; // 예: "불만", "환불", "감사합니다"
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private KeywordCategory category; // 예: COMPLAINT, COMPLIMENT, INQUIRY, PRODUCT
    
    @NotNull
    @Column(name = "match_count", nullable = false)
    private Integer matchCount = 1; // 예: 3 (키워드 매칭 횟수)
    
    @Column(name = "first_occurrence_time")
    private Integer firstOccurrenceTime; // 예: 120 (첫 번째 발생 시간, 초 단위)
    
    @Column(name = "context_text", columnDefinition = "TEXT")
    private String contextText; // 예: "제품에 대해 불만이 있어서 전화드렸습니다."
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Speaker speaker; // 예: CUSTOMER, AGENT
    
    @Column(name = "sentiment_score")
    private Double sentimentScore; // 예: -0.7 (감정 점수, -1.0~1.0)
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 예: 2024-01-15T14:35:00
    
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