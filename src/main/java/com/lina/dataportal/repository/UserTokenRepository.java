package com.lina.dataportal.repository;

import com.lina.dataportal.domain.user.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    
    @Query("SELECT ut FROM UserToken ut WHERE ut.userId = :userId AND ut.tokenType = :tokenType AND ut.isActive = true")
    Optional<UserToken> findActiveTokenByUserIdAndType(@Param("userId") String userId, @Param("tokenType") String tokenType);
    
    @Query("SELECT ut FROM UserToken ut WHERE ut.userId = :userId AND ut.tokenType = 'DATABRICKS_PAT' AND ut.isActive = true")
    Optional<UserToken> findActiveDatabricksToken(@Param("userId") String userId);
    
    @Query("UPDATE UserToken ut SET ut.isActive = false WHERE ut.userId = :userId AND ut.tokenType = :tokenType")
    void deactivateTokensByUserIdAndType(@Param("userId") String userId, @Param("tokenType") String tokenType);
}