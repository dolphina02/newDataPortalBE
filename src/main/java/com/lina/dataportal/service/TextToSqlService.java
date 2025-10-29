package com.lina.dataportal.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TextToSqlService {

    private final Map<String, List<Map<String, Object>>> chatHistoryStore = new ConcurrentHashMap<>();
    
    public Map<String, Object> generateSQL(String naturalLanguageQuery, String model) {
        // AI 모델을 통한 SQL 생성 시뮬레이션
        String sql = generateSQLFromNaturalLanguage(naturalLanguageQuery);
        String explanation = generateExplanation(naturalLanguageQuery);
        
        long generationTime = (long) (Math.random() * 2000) + 500; // 500-2500ms
        int confidence = (int) (Math.random() * 20) + 80; // 80-100%
        
        Map<String, Object> result = new HashMap<>();
        result.put("sql", sql);
        result.put("explanation", explanation);
        result.put("metadata", Map.of(
            "model", model,
            "generationTime", generationTime,
            "confidence", confidence
        ));
        
        return result;
    }
    
    public Map<String, Object> executeSQL(String sql) {
        // SQL 실행 시뮬레이션
        List<Map<String, Object>> results = generateMockResults(sql);
        long executionTime = (long) (Math.random() * 500) + 100; // 100-600ms
        
        Map<String, Object> result = new HashMap<>();
        result.put("results", results);
        result.put("rowCount", results.size());
        result.put("executionTime", executionTime);
        result.put("success", true);
        
        return result;
    }
    
    public String formatSQL(String sql) {
        // 간단한 SQL 포맷팅
        return sql.replaceAll("\\s+", " ")
                 .replace("SELECT", "\nSELECT")
                 .replace("FROM", "\nFROM")
                 .replace("WHERE", "\nWHERE")
                 .replace("JOIN", "\nJOIN")
                 .replace("GROUP BY", "\nGROUP BY")
                 .replace("ORDER BY", "\nORDER BY")
                 .replace("LIMIT", "\nLIMIT")
                 .trim();
    }
    
    public List<String> getExampleQueries() {
        // 실제 데이터베이스 스키마에 맞는 예시 쿼리들
        return Arrays.asList(
            "2024년 보험료 수입이 높은 고객 TOP 10을 조회해줘",
            "가장 많이 판매된 보험상품 순으로 보여줘", 
            "월별 신규 계약 건수 추이를 분석해줘",
            "고객별 보험금 청구 이력을 조회해줘",
            "지역별 보험 계약 현황을 보여줘",
            "상품별 평균 보험료를 계산해줘",
            "최근 1개월간 청구된 보험금 현황을 보여줘",
            "고객 연령대별 가입 상품 분포를 분석해줘",
            "설계사별 월간 실적을 조회해줘",
            "사기 위험도가 높은 청구 건을 찾아줘"
        );
    }
    
    public List<Map<String, Object>> getChatHistory() {
        return chatHistoryStore.getOrDefault("default", new ArrayList<>());
    }
    
    public void clearChatHistory() {
        chatHistoryStore.clear();
    }
    
    private String generateSQLFromNaturalLanguage(String query) {
        String lowerQuery = query.toLowerCase();
        
        if (lowerQuery.contains("top") || lowerQuery.contains("상위") || lowerQuery.contains("보험료")) {
            return """
                SELECT 
                  c.customer_name,
                  c.customer_id,
                  SUM(p.premium_amount) as total_premium,
                  COUNT(p.policy_id) as policy_count
                FROM customers c
                JOIN insurance_policies p ON c.customer_id = p.customer_id
                WHERE p.start_date >= '2024-01-01'
                  AND p.policy_status = 'ACTIVE'
                GROUP BY c.customer_id, c.customer_name
                ORDER BY total_premium DESC
                LIMIT 10;""";
        } else if (lowerQuery.contains("상품") || lowerQuery.contains("판매")) {
            return """
                SELECT 
                  pr.product_name,
                  pr.product_type,
                  COUNT(p.policy_id) as contract_count,
                  SUM(p.premium_amount) as total_premium,
                  AVG(p.premium_amount) as avg_premium
                FROM products pr
                JOIN insurance_policies p ON pr.product_id = p.product_id
                WHERE p.start_date >= '2024-01-01'
                GROUP BY pr.product_id, pr.product_name, pr.product_type
                ORDER BY contract_count DESC;""";
        } else if (lowerQuery.contains("월별") || lowerQuery.contains("추이")) {
            return """
                SELECT 
                  DATE_FORMAT(start_date, '%Y-%m') as contract_month,
                  COUNT(*) as new_contracts,
                  SUM(premium_amount) as monthly_premium
                FROM insurance_policies
                WHERE start_date >= '2024-01-01'
                  AND policy_status = 'ACTIVE'
                GROUP BY DATE_FORMAT(start_date, '%Y-%m')
                ORDER BY contract_month;""";
        } else if (lowerQuery.contains("청구") || lowerQuery.contains("보험금")) {
            return """
                SELECT 
                  c.customer_name,
                  c.customer_id,
                  COUNT(cl.claim_id) as claim_count,
                  SUM(cl.claim_amount) as total_claim_amount,
                  SUM(cl.settlement_amount) as total_settlement
                FROM customers c
                JOIN insurance_policies p ON c.customer_id = p.customer_id
                JOIN claims cl ON p.policy_id = cl.policy_id
                WHERE cl.claim_date >= '2024-01-01'
                GROUP BY c.customer_id, c.customer_name
                ORDER BY total_claim_amount DESC;""";
        } else {
            return """
                SELECT 
                  c.customer_name,
                  p.policy_id,
                  pr.product_name,
                  p.premium_amount,
                  p.start_date,
                  p.policy_status
                FROM customers c
                JOIN insurance_policies p ON c.customer_id = p.customer_id
                JOIN products pr ON p.product_id = pr.product_id
                WHERE p.start_date >= '2024-01-01'
                ORDER BY p.start_date DESC
                LIMIT 100;""";
        }
    }
    
    private String generateExplanation(String query) {
        String lowerQuery = query.toLowerCase();
        
        if (lowerQuery.contains("top") || lowerQuery.contains("상위")) {
            return "보험료 수입이 높은 상위 고객들을 조회하는 쿼리를 생성했습니다. 고객별 총 보험료를 계산하여 내림차순으로 정렬합니다.";
        } else if (lowerQuery.contains("상품") || lowerQuery.contains("판매")) {
            return "가장 많이 판매된 보험상품들을 조회하는 쿼리입니다. 상품별 계약 건수와 총 보험료를 계산하여 내림차순으로 정렬합니다.";
        } else if (lowerQuery.contains("월별") || lowerQuery.contains("추이")) {
            return "월별 신규 계약 건수 추이를 분석하는 쿼리입니다. 각 월의 신규 계약 건수와 보험료 수입을 계산합니다.";
        } else if (lowerQuery.contains("청구") || lowerQuery.contains("보험금")) {
            return "고객별 보험금 청구 이력을 조회하는 쿼리입니다. 청구 건수, 총 청구금액, 지급금액을 함께 표시합니다.";
        } else {
            return "요청하신 내용을 바탕으로 일반적인 보험 데이터 조회 쿼리를 생성했습니다.";
        }
    }
    
    private List<Map<String, Object>> generateMockResults(String sql) {
        // TextToSqlService는 실제로 SQL을 실행하지 않고 미리보기만 제공
        // 실제 실행은 QueryService에서 Databricks를 통해 수행됨
        List<Map<String, Object>> results = new ArrayList<>();
        
        // 간단한 미리보기 데이터만 제공
        results.add(Map.of("message", "SQL이 생성되었습니다. '실행' 버튼을 클릭하여 실제 데이터를 조회하세요."));
        
        return results;
    }
}