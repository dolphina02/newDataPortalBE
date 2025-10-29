package com.lina.dataportal.repository;

import com.lina.dataportal.domain.stt.CallRecord;
import com.lina.dataportal.domain.stt.CallType;
import com.lina.dataportal.domain.stt.CustomerType;
import com.lina.dataportal.domain.stt.ProcessingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CallRecordRepository extends JpaRepository<CallRecord, Long> {
    
    List<CallRecord> findByAgentId(String agentId);
    
    List<CallRecord> findByCustomerType(CustomerType customerType);
    
    List<CallRecord> findByCallType(CallType callType);
    
    List<CallRecord> findByProcessingStatus(ProcessingStatus status);
    
    @Query("SELECT cr FROM CallRecord cr WHERE cr.callDate BETWEEN :startDate AND :endDate")
    List<CallRecord> findByCallDateBetween(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT cr FROM CallRecord cr WHERE cr.agentId = :agentId AND cr.callDate BETWEEN :startDate AND :endDate")
    List<CallRecord> findByAgentIdAndCallDateBetween(@Param("agentId") String agentId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT cr FROM CallRecord cr WHERE cr.customerType = :customerType AND cr.callDate BETWEEN :startDate AND :endDate")
    List<CallRecord> findByCustomerTypeAndCallDateBetween(@Param("customerType") CustomerType customerType,
                                                         @Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT cr FROM CallRecord cr WHERE cr.transcript LIKE %:keyword%")
    List<CallRecord> findByTranscriptContaining(@Param("keyword") String keyword);
    
    @Query("SELECT cr FROM CallRecord cr WHERE cr.processingStatus = 'COMPLETED' ORDER BY cr.callDate DESC")
    List<CallRecord> findProcessedCallsOrderByDateDesc();
    
    @Query("SELECT COUNT(cr) FROM CallRecord cr WHERE cr.callDate BETWEEN :startDate AND :endDate")
    Long countCallsByDateRange(@Param("startDate") LocalDateTime startDate, 
                              @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT cr.agentId, COUNT(cr) FROM CallRecord cr WHERE cr.callDate BETWEEN :startDate AND :endDate GROUP BY cr.agentId")
    List<Object[]> getCallCountByAgent(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
}