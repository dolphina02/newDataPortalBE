package com.lina.dataportal.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.lina.dataportal.domain.stt.elasticsearch.STTCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class STTService {
    
    private final ElasticsearchClient elasticsearchClient;
    private static final String STT_INDEX_PATTERN = "stt-calls-*";
    
    @Autowired
    public STTService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }
    
    /**
     * 모든 STT 통화 데이터 조회
     */
    public List<STTCall> getAllCalls() {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(STT_INDEX_PATTERN)
                .size(100)
                .query(Query.of(q -> q.matchAll(m -> m)))
            );
            
            SearchResponse<STTCall> response = elasticsearchClient.search(searchRequest, STTCall.class);
            return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
        } catch (Exception e) {
            // Elasticsearch 연결 실패 시 빈 리스트 반환
            return new ArrayList<>();
        }
    }
    
    /**
     * 키워드와 날짜 범위로 STT 통화 검색
     */
    public List<STTCall> searchCalls(String keyword, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Query> mustQueries = new ArrayList<>();
            
            // 키워드 검색
            if (keyword != null && !keyword.trim().isEmpty()) {
                mustQueries.add(Query.of(q -> q
                    .multiMatch(m -> m
                        .query(keyword)
                        .fields("summary", "full_text", "segments.text")
                    )
                ));
            }
            
            // 날짜 범위 필터
            if (startDate != null && endDate != null) {
                mustQueries.add(Query.of(q -> q
                    .range(r -> r
                        .field("call_time")
                        .gte(co.elastic.clients.json.JsonData.of(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                        .lte(co.elastic.clients.json.JsonData.of(endDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    )
                ));
            }
            
            Query finalQuery;
            if (mustQueries.isEmpty()) {
                finalQuery = Query.of(q -> q.matchAll(m -> m));
            } else {
                finalQuery = Query.of(q -> q
                    .bool(b -> b.must(mustQueries))
                );
            }
            
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(STT_INDEX_PATTERN)
                .size(100)
                .query(finalQuery)
            );
            
            SearchResponse<STTCall> response = elasticsearchClient.search(searchRequest, STTCall.class);
            return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * 일반적인 키워드 검색
     */
    public List<STTCall> searchByGeneralKeyword(String keyword) {
        return searchCalls(keyword, null, null);
    }
    
    /**
     * 특정 통화 ID로 통화 데이터 조회
     */
    public STTCall getCallById(String callId) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(STT_INDEX_PATTERN)
                .query(Query.of(q -> q
                    .term(t -> t
                        .field("call_id")
                        .value(callId)
                    )
                ))
            );
            
            SearchResponse<STTCall> response = elasticsearchClient.search(searchRequest, STTCall.class);
            if (!response.hits().hits().isEmpty()) {
                return response.hits().hits().get(0).source();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 특정 주제에 대한 상위 키워드 분석
     */
    public Map<String, Long> getTopKeywordsByTopic(String topic) {
        try {
            // 실제 구현에서는 Elasticsearch aggregation을 사용해야 하지만
            // 여기서는 간단한 키워드 분석으로 대체
            List<STTCall> calls = searchByGeneralKeyword(topic);
            Map<String, Long> keywordFreq = new HashMap<>();
            
            for (STTCall call : calls) {
                if (call.getFullText() != null) {
                    String[] words = call.getFullText().toLowerCase().split("\\s+");
                    for (String word : words) {
                        if (word.length() > 1 && word.contains(topic.toLowerCase())) {
                            keywordFreq.merge(word, 1L, Long::sum);
                        }
                    }
                }
            }
            
            return keywordFreq.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
    
    /**
     * 고급 필터링 검색
     */
    public List<STTCall> advancedSearch(String keyword, String agentId, String region, 
                                       String ageBand, String campaignType, 
                                       LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Query> mustQueries = new ArrayList<>();
            List<Query> filterQueries = new ArrayList<>();
            
            // 키워드 검색
            if (keyword != null && !keyword.trim().isEmpty()) {
                mustQueries.add(Query.of(q -> q
                    .multiMatch(m -> m
                        .query(keyword)
                        .fields("summary", "full_text", "segments.text")
                    )
                ));
            }
            
            // 상담원 필터
            if (agentId != null && !agentId.trim().isEmpty()) {
                filterQueries.add(Query.of(q -> q
                    .term(t -> t
                        .field("agent_id")
                        .value(agentId)
                    )
                ));
            }
            
            // 고객 지역 필터
            if (region != null && !region.trim().isEmpty()) {
                filterQueries.add(Query.of(q -> q
                    .term(t -> t
                        .field("cust.region")
                        .value(region)
                    )
                ));
            }
            
            // 고객 연령대 필터
            if (ageBand != null && !ageBand.trim().isEmpty()) {
                filterQueries.add(Query.of(q -> q
                    .term(t -> t
                        .field("cust.age_band")
                        .value(ageBand)
                    )
                ));
            }
            
            // 캠페인 타입 필터
            if (campaignType != null && !campaignType.trim().isEmpty()) {
                filterQueries.add(Query.of(q -> q
                    .term(t -> t
                        .field("campaign_type")
                        .value(campaignType)
                    )
                ));
            }
            
            // 날짜 범위 필터
            if (startDate != null && endDate != null) {
                filterQueries.add(Query.of(q -> q
                    .range(r -> r
                        .field("call_time")
                        .gte(co.elastic.clients.json.JsonData.of(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                        .lte(co.elastic.clients.json.JsonData.of(endDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    )
                ));
            }
            
            Query finalQuery;
            if (mustQueries.isEmpty() && filterQueries.isEmpty()) {
                finalQuery = Query.of(q -> q.matchAll(m -> m));
            } else {
                finalQuery = Query.of(q -> q
                    .bool(b -> {
                        if (!mustQueries.isEmpty()) {
                            b.must(mustQueries);
                        }
                        if (!filterQueries.isEmpty()) {
                            b.filter(filterQueries);
                        }
                        return b;
                    })
                );
            }
            
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(STT_INDEX_PATTERN)
                .size(100)
                .query(finalQuery)
            );
            
            SearchResponse<STTCall> response = elasticsearchClient.search(searchRequest, STTCall.class);
            return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * 세그먼트별 키워드 하이라이팅 정보 반환
     */
    public Map<String, Object> getKeywordHighlights(String callId, String keyword) {
        try {
            STTCall call = getCallById(callId);
            Map<String, Object> result = new HashMap<>();
            
            if (call != null) {
                result.put("call", call);
                result.put("highlighted_segments", call.getSegments());
            }
            
            return result;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}