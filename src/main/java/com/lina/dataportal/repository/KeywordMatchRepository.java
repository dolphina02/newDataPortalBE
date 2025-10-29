package com.lina.dataportal.repository;

import com.lina.dataportal.domain.stt.KeywordCategory;
import com.lina.dataportal.domain.stt.KeywordMatch;
import com.lina.dataportal.domain.stt.Speaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KeywordMatchRepository extends JpaRepository<KeywordMatch, Long> {
    
    List<KeywordMatch> findByCallRecordId(Long callRecordId);
    
    List<KeywordMatch> findByKeyword(String keyword);
    
    List<KeywordMatch> findByCategory(KeywordCategory category);
    
    List<KeywordMatch> findBySpeaker(Speaker speaker);
    
    @Query("SELECT km FROM KeywordMatch km JOIN km.callRecord cr WHERE cr.callDate BETWEEN :startDate AND :endDate")
    List<KeywordMatch> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT km.keyword, SUM(km.matchCount) as totalCount FROM KeywordMatch km GROUP BY km.keyword ORDER BY totalCount DESC")
    List<Object[]> getKeywordFrequency();
    
    @Query("SELECT km.keyword, SUM(km.matchCount) as totalCount FROM KeywordMatch km JOIN km.callRecord cr WHERE cr.callDate BETWEEN :startDate AND :endDate GROUP BY km.keyword ORDER BY totalCount DESC")
    List<Object[]> getKeywordFrequencyByDateRange(@Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT km.category, COUNT(km) FROM KeywordMatch km GROUP BY km.category")
    List<Object[]> getKeywordCountByCategory();
    
    @Query("SELECT km FROM KeywordMatch km JOIN km.callRecord cr WHERE cr.agentId = :agentId")
    List<KeywordMatch> findByAgentId(@Param("agentId") String agentId);
    
    @Query("SELECT km FROM KeywordMatch km WHERE km.sentimentScore IS NOT NULL AND km.sentimentScore < :threshold")
    List<KeywordMatch> findNegativeSentimentMatches(@Param("threshold") Double threshold);
    
    @Query("SELECT DATE(km.createdAt), COUNT(km) FROM KeywordMatch km WHERE km.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(km.createdAt) ORDER BY DATE(km.createdAt)")
    List<Object[]> getKeywordTrendByDate(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
}