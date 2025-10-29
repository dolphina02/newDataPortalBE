package com.lina.dataportal.service;

import com.lina.dataportal.domain.report.Report;
import com.lina.dataportal.domain.report.ReportType;
import com.lina.dataportal.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ReportService {
    
    private final ReportRepository reportRepository;
    
    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
    
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }
    
    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }
    
    public List<Report> getReportsByType(ReportType type) {
        return reportRepository.findByType(type);
    }
    
    public List<Report> getReportsByCategory(String category) {
        return reportRepository.findByCategory(category);
    }
    
    public List<Report> searchReports(String keyword) {
        return reportRepository.findByKeyword(keyword);
    }
    
    public List<Report> getRecentReports() {
        return reportRepository.findRecentReports();
    }
    
    public Report createReport(Report report) {
        return reportRepository.save(report);
    }
    
    public Report updateReport(Long id, Report reportDetails) {
        Report report = reportRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
        
        report.setTitle(reportDetails.getTitle());
        report.setDescription(reportDetails.getDescription());
        report.setCategory(reportDetails.getCategory());
        report.setType(reportDetails.getType());
        report.setFilePath(reportDetails.getFilePath());
        report.setFileName(reportDetails.getFileName());
        report.setFileSize(reportDetails.getFileSize());
        report.setTags(reportDetails.getTags());
        
        return reportRepository.save(report);
    }
    
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
    
    // SearchView.vue 화면을 위한 추가 메서드들
    
    public List<Map<String, Object>> getCategories() {
        return List.of(
            Map.of("id", "all", "label", "전체", "icon", "grid", "count", getAllReports().size()),
            Map.of("id", "customer", "label", "고객 분석", "icon", "users", "count", 
                   getReportsByCategory("customer").size()),
            Map.of("id", "kpi", "label", "KPI 리포트", "icon", "trending-up", "count", 
                   getReportsByCategory("kpi").size()),
            Map.of("id", "monitoring", "label", "모니터링", "icon", "monitor", "count", 
                   getReportsByCategory("monitoring").size()),
            Map.of("id", "monthly", "label", "월간 리포트", "icon", "calendar", "count", 
                   getReportsByCategory("monthly").size())
        );
    }
    
    public Map<String, Object> downloadReport(Long id) {
        Optional<Report> reportOpt = getReportById(id);
        if (reportOpt.isEmpty()) {
            return Map.of("success", false, "message", "Report not found");
        }
        
        Report report = reportOpt.get();
        return Map.of(
            "success", true,
            "downloadUrl", report.getFilePath() != null ? report.getFilePath() : "/api/files/download/" + id,
            "fileName", report.getFileName() != null ? report.getFileName() : report.getTitle() + ".pdf",
            "fileSize", report.getFileSize() != null ? report.getFileSize() : 0L
        );
    }
    
    public Map<String, Object> shareReport(Long id, String email, String message) {
        Optional<Report> reportOpt = getReportById(id);
        if (reportOpt.isEmpty()) {
            return Map.of("success", false, "message", "Report not found");
        }
        
        // 이메일 공유 로직 (실제 구현 시 이메일 서비스 연동)
        return Map.of(
            "success", true,
            "message", "Report shared successfully",
            "sharedWith", email,
            "sharedAt", java.time.LocalDateTime.now()
        );
    }
    
    public Map<String, Object> getReportContent(Long id) {
        Optional<Report> reportOpt = getReportById(id);
        if (reportOpt.isEmpty()) {
            return Map.of("success", false, "message", "Report not found");
        }
        
        Report report = reportOpt.get();
        
        // 파일 경로와 메타데이터만 제공 - 실제 뷰잉은 프론트엔드에서 처리
        return Map.of(
            "success", true,
            "report", report,
            "filePath", report.getFilePath(),
            "fileName", report.getFileName(),
            "fileSize", report.getFileSize(),
            "fileType", getFileExtension(report.getFileName()),
            "viewerUrl", "/api/files/view/" + id // 프론트엔드에서 사용할 뷰어 URL
        );
    }
    
    public Map<String, Object> uploadReport(String title, String description, String category, String fileUrl) {
        Report newReport = new Report();
        newReport.setTitle(title);
        newReport.setDescription(description);
        newReport.setCategory(category);
        newReport.setFilePath(fileUrl);
        newReport.setFileName(title + ".pdf");
        newReport.setFileSize(1024000L);
        
        Report savedReport = createReport(newReport);
        
        return Map.of(
            "success", true,
            "report", savedReport,
            "message", "Report uploaded successfully"
        );
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null) return "pdf";
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1).toLowerCase() : "pdf";
    }
    
    // 파일 뷰잉 관련 메서드들 제거 - 프론트엔드에서 처리
}