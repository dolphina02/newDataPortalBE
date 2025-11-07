package com.lina.dataportal.domain.stt.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public class STTCall {
    
    @JsonProperty("call_id")
    private String callId; // 예: "CALL_20240115_001"
    
    @JsonProperty("media_url")
    private String mediaUrl; // 예: "https://storage.company.com/audio/call_001.wav"
    
    @JsonProperty("call_time")
    private LocalDateTime callTime; // 예: 2024-01-15T14:30:00
    
    @JsonProperty("duration_ms")
    private Integer durationMs; // 예: 480000 (8분, 밀리초 단위)
    
    @JsonProperty("customer_id")
    private String customerId; // 예: "CUST_12345"
    
    @JsonProperty("agent_id")
    private String agentId; // 예: "AGENT_789"
    
    @JsonProperty("campaign_type")
    private String campaignType; // 예: "INBOUND", "OUTBOUND", "SUPPORT"
    
    private String summary; // 예: "고객이 제품 불량에 대해 문의하여 교환 처리함"
    
    @JsonProperty("full_text")
    private String fullText; // 예: "안녕하세요. 고객센터입니다. 무엇을 도와드릴까요? 제품에 문제가 있어서..."
    
    private List<Turn> turns; // 화자별 발화 턴 목록
    
    private List<Segment> segments; // 세분화된 음성 세그먼트 목록
    
    @JsonProperty("full_text_vec")
    private List<Double> fullTextVec; // 예: [0.1, -0.3, 0.7, ...] (텍스트 벡터 임베딩)
    
    private Customer cust; // 고객 정보
    
    // Constructors
    public STTCall() {}
    
    public STTCall(String callId, String customerId, String agentId, LocalDateTime callTime) {
        this.callId = callId;
        this.customerId = customerId;
        this.agentId = agentId;
        this.callTime = callTime;
    }
    
    // Getters and Setters
    public String getCallId() { return callId; }
    public void setCallId(String callId) { this.callId = callId; }
    
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    
    public LocalDateTime getCallTime() { return callTime; }
    public void setCallTime(LocalDateTime callTime) { this.callTime = callTime; }
    
    public Integer getDurationMs() { return durationMs; }
    public void setDurationMs(Integer durationMs) { this.durationMs = durationMs; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    
    public String getCampaignType() { return campaignType; }
    public void setCampaignType(String campaignType) { this.campaignType = campaignType; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getFullText() { return fullText; }
    public void setFullText(String fullText) { this.fullText = fullText; }
    
    public List<Turn> getTurns() { return turns; }
    public void setTurns(List<Turn> turns) { this.turns = turns; }
    
    public List<Segment> getSegments() { return segments; }
    public void setSegments(List<Segment> segments) { this.segments = segments; }
    
    public List<Double> getFullTextVec() { return fullTextVec; }
    public void setFullTextVec(List<Double> fullTextVec) { this.fullTextVec = fullTextVec; }
    
    public Customer getCust() { return cust; }
    public void setCust(Customer cust) { this.cust = cust; }
    
    // Nested Classes
    public static class Turn {
        private Integer idx; // 예: 0 (턴 인덱스)
        private String speaker; // 예: "agent", "customer"
        
        @JsonProperty("ts_start_ms")
        private Integer tsStartMs; // 예: 0 (시작 시간, 밀리초)
        
        @JsonProperty("ts_end_ms")
        private Integer tsEndMs; // 예: 15000 (종료 시간, 밀리초)
        
        private String text; // 예: "안녕하세요. 고객센터입니다. 무엇을 도와드릴까요?"
        
        // Constructors
        public Turn() {}
        
        public Turn(Integer idx, String speaker, Integer tsStartMs, Integer tsEndMs, String text) {
            this.idx = idx;
            this.speaker = speaker;
            this.tsStartMs = tsStartMs;
            this.tsEndMs = tsEndMs;
            this.text = text;
        }
        
        // Getters and Setters
        public Integer getIdx() { return idx; }
        public void setIdx(Integer idx) { this.idx = idx; }
        
        public String getSpeaker() { return speaker; }
        public void setSpeaker(String speaker) { this.speaker = speaker; }
        
        public Integer getTsStartMs() { return tsStartMs; }
        public void setTsStartMs(Integer tsStartMs) { this.tsStartMs = tsStartMs; }
        
        public Integer getTsEndMs() { return tsEndMs; }
        public void setTsEndMs(Integer tsEndMs) { this.tsEndMs = tsEndMs; }
        
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
    
    public static class Segment {
        @JsonProperty("turn_idx")
        private Integer turnIdx; // 예: 0 (턴 인덱스)
        
        @JsonProperty("sent_idx")
        private Integer sentIdx; // 예: 0 (문장 인덱스)
        
        @JsonProperty("ts_start_ms")
        private Integer tsStartMs; // 예: 0 (시작 시간, 밀리초)
        
        @JsonProperty("ts_end_ms")
        private Integer tsEndMs; // 예: 5000 (종료 시간, 밀리초)
        
        private String speaker; // 예: "agent", "customer"
        private String text; // 예: "안녕하세요."
        
        // Constructors
        public Segment() {}
        
        public Segment(Integer turnIdx, Integer sentIdx, Integer tsStartMs, Integer tsEndMs, String speaker, String text) {
            this.turnIdx = turnIdx;
            this.sentIdx = sentIdx;
            this.tsStartMs = tsStartMs;
            this.tsEndMs = tsEndMs;
            this.speaker = speaker;
            this.text = text;
        }
        
        // Getters and Setters
        public Integer getTurnIdx() { return turnIdx; }
        public void setTurnIdx(Integer turnIdx) { this.turnIdx = turnIdx; }
        
        public Integer getSentIdx() { return sentIdx; }
        public void setSentIdx(Integer sentIdx) { this.sentIdx = sentIdx; }
        
        public Integer getTsStartMs() { return tsStartMs; }
        public void setTsStartMs(Integer tsStartMs) { this.tsStartMs = tsStartMs; }
        
        public Integer getTsEndMs() { return tsEndMs; }
        public void setTsEndMs(Integer tsEndMs) { this.tsEndMs = tsEndMs; }
        
        public String getSpeaker() { return speaker; }
        public void setSpeaker(String speaker) { this.speaker = speaker; }
        
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
    
    public static class Customer {
        @JsonProperty("age_band")
        private String ageBand; // 예: "30-39", "40-49", "50-59"
        
        private String gender; // 예: "M", "F"
        private String region; // 예: "서울", "부산", "대구"
        
        // Constructors
        public Customer() {}
        
        public Customer(String ageBand, String gender, String region) {
            this.ageBand = ageBand;
            this.gender = gender;
            this.region = region;
        }
        
        // Getters and Setters
        public String getAgeBand() { return ageBand; }
        public void setAgeBand(String ageBand) { this.ageBand = ageBand; }
        
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
    }
}