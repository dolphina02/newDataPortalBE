package com.lina.dataportal.service;

import com.lina.dataportal.domain.user.User;
import com.lina.dataportal.domain.user.UserStatus;
import com.lina.dataportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 이메일로 사용자 조회 (Optional 반환)
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmailOptional(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * 이메일로 사용자 조회 (null 반환 가능)
     */
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    /**
     * 이메일로 사용자 조회 또는 생성 (하위 호환성용)
     */
    public User findByEmailOrCreate(String email) {
        Optional<User> existingUser = findByEmailOptional(email);
        
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            // 이메일에서 이름 추출 (간단한 방식)
            String name = email.split("@")[0];
            User newUser = new User(email, name, "Unknown");
            return userRepository.save(newUser);
        }
    }
    
    /**
     * ID로 사용자 조회
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Entra ID Object ID로 사용자 조회
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEntraObjectId(String entraObjectId) {
        return userRepository.findByEntraObjectId(entraObjectId);
    }
    
    /**
     * 사용자 생성 또는 업데이트 (Entra ID 연동용)
     */
    public User createOrUpdateUser(String entraObjectId, String email, String name, String department) {
        Optional<User> existingUser = findByEntraObjectId(entraObjectId);
        
        if (existingUser.isPresent()) {
            // 기존 사용자 정보 업데이트
            User user = existingUser.get();
            user.setEmail(email);
            user.setName(name);
            user.setDepartment(department);
            return userRepository.save(user);
        } else {
            // 새 사용자 생성
            User newUser = new User(entraObjectId, email, name, department);
            return userRepository.save(newUser);
        }
    }
    
    /**
     * 이메일로 사용자 생성 또는 조회 (간단한 버전)
     */
    public User getOrCreateUserByEmail(String email, String name, String department) {
        Optional<User> existingUser = findByEmailOptional(email);
        
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            User newUser = new User(email, name, department);
            return userRepository.save(newUser);
        }
    }
    
    /**
     * 부서별 활성 사용자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<User> getActiveUsersByDepartment(String department) {
        return userRepository.findByDepartmentAndStatus(department, UserStatus.ACTIVE);
    }
    
    /**
     * 직책으로 사용자 검색
     */
    @Transactional(readOnly = true)
    public List<User> findUsersByJobTitle(String department, String jobTitle) {
        return userRepository.findByDepartmentAndJobTitleContaining(department, jobTitle);
    }
    
    /**
     * 관리자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<User> getActiveAdmins() {
        return userRepository.findByIsAdminTrueAndStatus(UserStatus.ACTIVE);
    }
    
    /**
     * 사용자 상태 변경
     */
    public User updateUserStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setStatus(status);
        return userRepository.save(user);
    }
    
    /**
     * 사용자 정보 업데이트
     */
    public User updateUser(Long userId, User updatedUser) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setName(updatedUser.getName());
        user.setDisplayName(updatedUser.getDisplayName());
        user.setDepartment(updatedUser.getDepartment());
        user.setJobTitle(updatedUser.getJobTitle());
        user.setManagerEmail(updatedUser.getManagerEmail());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setOfficeLocation(updatedUser.getOfficeLocation());
        
        return userRepository.save(user);
    }
    
    /**
     * 모든 활성 사용자 조회
     */
    @Transactional(readOnly = true)
    public List<User> getAllActiveUsers() {
        return userRepository.findByStatus(UserStatus.ACTIVE);
    }
    
    /**
     * 사용자 검색 (이름 부분 일치)
     */
    @Transactional(readOnly = true)
    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContainingAndActive(name);
    }
}