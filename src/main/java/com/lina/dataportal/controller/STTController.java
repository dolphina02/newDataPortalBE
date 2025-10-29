package com.lina.dataportal.controller;

import com.lina.dataportal.domain.stt.elasticsearch.STTCall;
import com.lina.dataportal.service.STTService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/stt")
@CrossOrigin(origins = "*")
@Tag(name = "STT", description = "음성 인식 데이터 검색 및 분석 API")
public class STTController {
    
    private final STTService sttService;
    
    @Autowired
    public STTController(STTService sttService) {
        this.sttService = sttService;
    }
    
    @Operation(summary = "STT 통화 목록 조회", description = "키워드 및 날짜 범위로 STT 통화 데이터 조회")
    @GetMapping("/calls")
    public ResponseEntity<List<STTCall>> getCalls(
            @Parameter(description = "검색 키워드") @RequestParam(required = false) String keyword,
            @Parameter(description = "시작 날짜") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "종료 날짜") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<STTCall> calls = sttService.searchCalls(keyword, startDate, endDate);
        return ResponseEntity.ok(calls);
    }
    
    @Operation(summary = "키워드 검색", description = "일반적인 키워드로 STT 데이터 검색")
    @GetMapping("/search")
    public ResponseEntity<List<STTCall>> searchByKeyword(
            @Parameter(description = "검색 키워드", required = true) @RequestParam String keyword) {
        List<STTCall> calls = sttService.searchByGeneralKeyword(keyword);
        return ResponseEntity.ok(calls);
    }
    
    @Operation(summary = "고급 검색", description = "여러 조건을 조합한 고급 검색")
    @PostMapping("/search/advanced")
    public ResponseEntity<List<STTCall>> advancedSearch(@RequestBody Map<String, Object> searchParams) {
        String keyword = (String) searchParams.get("keyword");
        String agentId = (String) searchParams.get("agentId");
        String region = (String) searchParams.get("region");
        String ageBand = (String) searchParams.get("ageBand");
        String campaignType = (String) searchParams.get("campaignType");
        
        // 날짜 파라미터 처리
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if (searchParams.get("startDate") != null) {
            startDate = LocalDateTime.parse((String) searchParams.get("startDate"));
        }
        if (searchParams.get("endDate") != null) {
            endDate = LocalDateTime.parse((String) searchParams.get("endDate"));
        }
        
        List<STTCall> calls = sttService.advancedSearch(keyword, agentId, region, ageBand, campaignType, startDate, endDate);
        return ResponseEntity.ok(calls);
    }
    
    @Operation(summary = "키워드 분석", description = "특정 주제에 대한 키워드 빈도 분석")
    @GetMapping("/analysis/keywords")
    public ResponseEntity<Map<String, Long>> getTopKeywordsByTopic(
            @Parameter(description = "분석할 주제") @RequestParam String topic) {
        Map<String, Long> keywords = sttService.getTopKeywordsByTopic(topic);
        return ResponseEntity.ok(keywords);
    }
    
    @Operation(summary = "감정 분석", description = "긍정/부정 키워드 분석")
    @GetMapping("/analysis/sentiment")
    public ResponseEntity<Map<String, Object>> getSentimentAnalysis() {
        Map<String, Object> result = new HashMap<>();
        
        // 긍정 키워드 분석
        Map<String, Long> positiveKeywords = sttService.getTopKeywordsByTopic("만족");
        Map<String, Long> negativeKeywords = sttService.getTopKeywordsByTopic("불만");
        
        result.put("positive_keywords", positiveKeywords);
        result.put("negative_keywords", negativeKeywords);
        result.put("positive_total", positiveKeywords.values().stream().mapToLong(Long::longValue).sum());
        result.put("negative_total", negativeKeywords.values().stream().mapToLong(Long::longValue).sum());
        
        return ResponseEntity.ok(result);
    }
    
    @Operation(summary = "통화 상세 조회", description = "특정 통화의 상세 정보 및 세그먼트 조회")
    @GetMapping("/calls/{callId}")
    public ResponseEntity<STTCall> getCallDetail(
            @Parameter(description = "통화 ID") @PathVariable String callId) {
        STTCall call = sttService.getCallById(callId);
        if (call != null) {
            return ResponseEntity.ok(call);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "키워드 하이라이트", description = "특정 통화에서 키워드가 언급된 부분 하이라이트")
    @GetMapping("/calls/{callId}/highlights")
    public ResponseEntity<Map<String, Object>> getKeywordHighlights(
            @Parameter(description = "통화 ID") @PathVariable String callId,
            @Parameter(description = "하이라이트할 키워드") @RequestParam String keyword) {
        Map<String, Object> highlights = sttService.getKeywordHighlights(callId, keyword);
        return ResponseEntity.ok(highlights);
    }
}