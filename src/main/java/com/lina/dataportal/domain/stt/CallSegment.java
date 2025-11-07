package com.lina.dataportal.domain.stt;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "call_segments")
public class CallSegment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예: 1
    
    @NotNull
    @Column(name = "call_record_id", nullable = false)
    private Long callRecordId; // 예: 1 (CallRecord의 ID)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "call_record_id", insertable = false, updatable = false)
    private CallRecord callRecord; // 연관된 통화 기록
    
    @NotNull
    @Column(name = "start_time", nullable = false)
    private Integer startTime; // 예: 0 (시작 시간, 초 단위)
    
    @NotNull
    @Column(name = "end_time", nullable = false)
    private Integer endTime; // 예: 15 (종료 시간, 초 단위)
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private Speaker speaker; // 예: AGENT, CUSTOMER
    
    @Column(columnDefinition = "TEXT")
    private String text; // 예: "안녕하세요. 고객센터입니다. 무엇을 도와드릴까요?"
    
    @Column(name = "confidence_score")
    private Double confidenceScore; // 예: 0.95 (음성 인식 신뢰도)
    
    // Constructors
    public CallSegment() {}
    
    public CallSegment(Long callRecordId, Integer startTime, Integer endTime, Speaker speaker, String text) {
        this.callRecordId = callRecordId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.speaker = speaker;
        this.text = text;
    }
    
    // Business Methods
    public Integer getDuration() {
        return endTime - startTime;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCallRecordId() { return callRecordId; }
    public void setCallRecordId(Long callRecordId) { this.callRecordId = callRecordId; }
    
    public CallRecord getCallRecord() { return callRecord; }
    public void setCallRecord(CallRecord callRecord) { this.callRecord = callRecord; }
    
    public Integer getStartTime() { return startTime; }
    public void setStartTime(Integer startTime) { this.startTime = startTime; }
    
    public Integer getEndTime() { return endTime; }
    public void setEndTime(Integer endTime) { this.endTime = endTime; }
    
    public Speaker getSpeaker() { return speaker; }
    public void setSpeaker(Speaker speaker) { this.speaker = speaker; }
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
}