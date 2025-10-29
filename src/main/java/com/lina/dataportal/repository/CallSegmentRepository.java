package com.lina.dataportal.repository;

import com.lina.dataportal.domain.stt.CallSegment;
import com.lina.dataportal.domain.stt.Speaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CallSegmentRepository extends JpaRepository<CallSegment, Long> {
    
    List<CallSegment> findByCallRecordIdOrderByStartTime(Long callRecordId);
    
    List<CallSegment> findByCallRecordIdAndSpeaker(Long callRecordId, Speaker speaker);
    
    @Query("SELECT cs FROM CallSegment cs WHERE cs.callRecordId = :callRecordId AND cs.text LIKE %:keyword%")
    List<CallSegment> findByCallRecordIdAndTextContaining(@Param("callRecordId") Long callRecordId, 
                                                         @Param("keyword") String keyword);
    
    @Query("SELECT cs FROM CallSegment cs WHERE cs.callRecordId = :callRecordId AND cs.startTime <= :time AND cs.endTime >= :time")
    List<CallSegment> findSegmentAtTime(@Param("callRecordId") Long callRecordId, 
                                       @Param("time") Integer time);
    
    @Query("SELECT cs FROM CallSegment cs WHERE cs.text LIKE %:keyword%")
    List<CallSegment> findByTextContaining(@Param("keyword") String keyword);
}