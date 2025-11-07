package com.lina.dataportal.repository;

import com.lina.dataportal.domain.user.User;
import com.lina.dataportal.domain.user.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Entra ID Object ID로 사용자 조회
     */
    Optional<User> findByEntraObjectId(String entraObjectId);
    
    /**
     * 사번으로 사용자 조회
     */
    Optional<User> findByEmployeeId(String employeeId);
    
    /**
     * 부서별 사용자 목록 조회
     */
    List<User> findByDepartmentAndStatus(String department, UserStatus status);
    
    /**
     * 활성 사용자 목록 조회
     */
    List<User> findByStatus(UserStatus status);
    
    /**
     * 이름으로 사용자 검색 (부분 일치)
     */
    @Query("SELECT u FROM User u WHERE u.name LIKE %:name% AND u.status = 'ACTIVE' ORDER BY u.name")
    List<User> findByNameContainingAndActive(@Param("name") String name);
    
    /**
     * 부서와 직책으로 사용자 검색
     */
    @Query("SELECT u FROM User u WHERE u.department = :department AND u.jobTitle LIKE %:jobTitle% AND u.status = 'ACTIVE'")
    List<User> findByDepartmentAndJobTitleContaining(@Param("department") String department, @Param("jobTitle") String jobTitle);
    
    /**
     * 관리자 사용자 목록 조회
     */
    List<User> findByIsAdminTrueAndStatus(UserStatus status);
    
    /**
     * 특정 매니저의 부하직원 목록 조회
     */
    List<User> findByManagerEmailAndStatus(String managerEmail, UserStatus status);
    
    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);
    
    /**
     * Entra ID Object ID 존재 여부 확인
     */
    boolean existsByEntraObjectId(String entraObjectId);
    
    /**
     * 부서별 활성 사용자 수 조회
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.department = :department AND u.status = 'ACTIVE'")
    Long countActiveUsersByDepartment(@Param("department") String department);
}