package com.lina.dataportal.service;

import com.lina.dataportal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InsightService {

    @Autowired
    private DashboardRepository dashboardRepository;
    
    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private DataTableRepository dataTableRepository;
    
    @Autowired
    private MLModelRepository mlModelRepository;

    public Map<String, Object> getProducer360Data(String scope, String month) {
        Map<String, Object> data = new HashMap<>();
        
        // KPIs
        data.put("kpis", generateKPIs(scope, month));
        
        // Chart data
        data.put("daySeries", generateDaySeries());
        data.put("yearSeries", generateYearSeries());
        data.put("monthBars", generateMonthBars());
        data.put("productSeries", generateProductSeries());
        data.put("ranking", generateRanking());
        
        // Visit and education data
        data.put("visits", generateVisitData());
        data.put("edu", generateEducationData());
        
        return data;
    }
    
    public Map<String, Object> getKPIs(String scope, String month) {
        return Map.of("kpis", generateKPIs(scope, month));
    }
    
    public Map<String, Object> getPerformanceTrends(String scope, String month) {
        Map<String, Object> trends = new HashMap<>();
        trends.put("daySeries", generateDaySeries());
        trends.put("yearSeries", generateYearSeries());
        trends.put("monthBars", generateMonthBars());
        trends.put("productSeries", generateProductSeries());
        return trends;
    }
    
    public Map<String, Object> getBranchManagement(String scope, String month) {
        Map<String, Object> branchData = new HashMap<>();
        branchData.put("ranking", generateRanking());
        branchData.put("visits", generateVisitData());
        branchData.put("education", generateEducationData());
        return branchData;
    }
    
    public Map<String, Object> getRiskOpportunities(String scope) {
        Map<String, Object> riskData = new HashMap<>();
        
        List<Map<String, Object>> riskItems = Arrays.asList(
            Map.of("chip", "위험", "title", "메타티지 > 보험스토어", "note", "3개월 연속 하락"),
            Map.of("chip", "위험", "title", "지급중코리아 > 대연", "note", "핵심 인력 이탈")
        );
        
        List<Map<String, Object>> warnItems = Arrays.asList(
            Map.of("chip", "주의", "title", "더블유에셋 > 안산센터", "note", "계약 품질 이슈")
        );
        
        List<Map<String, Object>> opportunityItems = Arrays.asList(
            Map.of("chip", "기회", "title", "글로벌금융판매 > 리더스일산", "note", "실적 급상승"),
            Map.of("chip", "기회", "title", "에셋현대금융 > 구미스튜디오", "note", "고액 계약 체결")
        );
        
        riskData.put("riskItems", riskItems);
        riskData.put("warnItems", warnItems);
        riskData.put("opportunityItems", opportunityItems);
        
        return riskData;
    }
    
    public Map<String, Object> exportWeeklySummaryPDF(String scope, String month) {
        // PDF 생성 시뮬레이션
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("fileName", "Producer360_Weekly_Summary_" + month.replace(" ", "_") + ".pdf");
        result.put("downloadUrl", "/api/files/download/" + UUID.randomUUID().toString());
        result.put("generatedAt", new Date());
        
        return result;
    }
    
    public Map<String, Object> getChartData(String chartType, String scope, String month) {
        Map<String, Object> chartData = new HashMap<>();
        
        switch (chartType) {
            case "day-series":
                chartData.put("data", generateDaySeries());
                break;
            case "year-series":
                chartData.put("data", generateYearSeries());
                break;
            case "month-bars":
                chartData.put("data", generateMonthBars());
                break;
            case "product-series":
                chartData.put("data", generateProductSeries());
                break;
            case "ranking":
                chartData.put("data", generateRanking());
                break;
            default:
                chartData.put("data", new ArrayList<>());
        }
        
        return chartData;
    }
    
    private List<Map<String, Object>> generateKPIs(String scope, String month) {
        // 실제 데이터를 기반으로 KPI 계산 (현재는 시뮬레이션)
        long totalDashboards = dashboardRepository.count();
        long totalReports = reportRepository.count();
        long totalDatasets = dataTableRepository.count();
        long totalModels = mlModelRepository.count();
        
        // 실제 비즈니스 로직에서는 이 값들을 실제 보험 데이터에서 계산해야 함
        double monthlyAchievement = Math.min(0.95, 0.75 + (totalDashboards * 0.01));
        int newContracts = (int) (1000 + (totalReports * 10));
        double apeAchievement = Math.min(0.98, 0.85 + (totalDatasets * 0.005));
        int activeAgents = (int) (80 + (totalModels * 2));
        
        return Arrays.asList(
            Map.of("title", "월 달성률", "value", monthlyAchievement, "delta", 0.05),
            Map.of("title", "신계약 건수", "value", newContracts, "unit", "건", "of", Arrays.asList(newContracts, newContracts + 300), "delta", 0.12),
            Map.of("title", "APE 달성률", "value", apeAchievement, "delta", 0.08),
            Map.of("title", "활동 설계사", "value", activeAgents, "unit", "%", "delta", -0.02)
        );
    }
    
    private List<Integer> generateDaySeries() {
        List<Integer> series = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 30; i++) {
            series.add(80 + random.nextInt(80)); // 80-160 range
        }
        return series;
    }
    
    private List<Integer> generateYearSeries() {
        return Arrays.asList(85, 92, 88, 95, 91);
    }
    
    private List<Integer> generateMonthBars() {
        return Arrays.asList(120, 135, 128, 142, 138, 155, 148, 162, 145, 158, 152, 168);
    }
    
    private Map<String, List<Integer>> generateProductSeries() {
        Map<String, List<Integer>> series = new HashMap<>();
        series.put("A", Arrays.asList(45, 52, 48, 58, 55, 62)); // 건강보험
        series.put("B", Arrays.asList(38, 42, 40, 45, 43, 48)); // 종신보험
        series.put("C", Arrays.asList(25, 28, 26, 32, 30, 35)); // 저축보험
        return series;
    }
    
    private List<Integer> generateRanking() {
        return Arrays.asList(245, 238, 225, 218, 205, 198, 185, 172, 165, 158);
    }
    
    private List<Map<String, Object>> generateVisitData() {
        // TODO: 실제 지점 방문 데이터를 데이터베이스에서 조회하도록 구현 필요
        // 현재는 시뮬레이션 데이터 반환
        return Arrays.asList(
            Map.of("branch", "강남센터", "visits", 12, "rate", 0.85, "owner", "김영업"),
            Map.of("branch", "서초지점", "visits", 8, "rate", 0.72, "owner", "이관리"),
            Map.of("branch", "판교센터", "visits", 15, "rate", 0.93, "owner", "박담당"),
            Map.of("branch", "분당지점", "visits", 6, "rate", 0.58, "owner", "최지원")
        );
    }
    
    private List<Map<String, Object>> generateEducationData() {
        // TODO: 실제 교육 진행 데이터를 데이터베이스에서 조회하도록 구현 필요
        // 현재는 시뮬레이션 데이터 반환
        return Arrays.asList(
            Map.of("branch", "강남센터", "status", "완료", "rate", 0.95, "last", "2025-09-15", "left", 2),
            Map.of("branch", "서초지점", "status", "진행중", "rate", 0.78, "last", "2025-09-10", "left", 8),
            Map.of("branch", "판교센터", "status", "완료", "rate", 1.0, "last", "2025-09-18", "left", 0),
            Map.of("branch", "분당지점", "status", "미시작", "rate", null, "last", "—", "left", 15)
        );
    }
}