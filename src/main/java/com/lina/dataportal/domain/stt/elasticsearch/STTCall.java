package com.lina.dataportal.domain.stt.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public class STTCall {
    
    @JsonProperty("call_id")
    private String callId;
    
    @JsonProperty("media_url")
    private String mediaUrl;
    
    @JsonProperty("call_time")
    private LocalDateTime callTime;
    
    @JsonProperty("duration_ms")
    private Integer durationMs;
    
    @JsonProperty("customer_id")
    private String customerId;
    
    @JsonProperty("agent_id")
    private String agentId;
    
    @JsonProperty("campaign_type")
    private String campaignType;
    
    private String summary;
    
    @JsonProperty("full_text")
    private String fullText;
    
    private List<Turn> turns;
    
    private List<Segment> segments;
    
    @JsonProperty("full_text_vec")
    private List<Double> fullTextVec;
    
    private Customer cust;
    
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
        private Integer idx;
        private String speaker;
        
        @JsonProperty("ts_start_ms")
        private Integer tsStartMs;
        
        @JsonProperty("ts_end_ms")
        private Integer tsEndMs;
        
        private String text;
        
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
        private Integer turnIdx;
        
        @JsonProperty("sent_idx")
        private Integer sentIdx;
        
        @JsonProperty("ts_start_ms")
        private Integer tsStartMs;
        
        @JsonProperty("ts_end_ms")
        private Integer tsEndMs;
        
        private String speaker;
        private String text;
        
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
        private String ageBand;
        
        private String gender;
        private String region;
        
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