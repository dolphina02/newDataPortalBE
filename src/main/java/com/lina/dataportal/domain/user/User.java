package com.lina.dataportal.domain.user;

import com.lina.dataportal.domain.common.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 내부 사용자 정보
 * Entra ID (Azure AD) 또는 내부 사용자 시스템과 연동
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_entra_id", columnList = "entraObjectId"),
    @Index(name = "idx_user_employee_id", columnList = "employeeId"),
    @Index(name = "idx_user_department", columnList = "department")
})
public class User extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예: 1 (내부 사용자 ID)
    
    @Column(name = "entra_object_id", unique = true)
    @Size(max = 100)
    private String entraObjectId; // 예: "12345678-1234-1234-1234-123456789abc" (Entra ID Object ID)
    
    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    @Size(max = 255)
    private String email; // 예: "kim.analyst@company.com"
    
    @NotBlank
    @Column(nullable = false)
    @Size(max = 100)
    private String name; // 예: "김분석가"
    
    @Column(name = "display_name")
    @Size(max = 100)
    private String displayName; // 예: "김분석가 (데이터팀)"
    
    @Column(name = "employee_id")
    @Size(max = 50)
    private String employeeId; // 예: "EMP001234"
    
    @Column(name = "department")
    @Size(max = 100)
    private String department; // 예: "데이터분석팀"
    
    @Column(name = "job_title")
    @Size(max = 100)
    private String jobTitle; // 예: "선임 데이터 분석가"
    
    @Column(name = "manager_email")
    @Email
    @Size(max = 255)
    private String managerEmail; // 예: "park.manager@company.com"
    
    @Column(name = "phone_number")
    @Size(max = 20)
    private String phoneNumber; // 예: "010-1234-5678"
    
    @Column(name = "office_location")
    @Size(max = 100)
    private String officeLocation; // 예: "서울 본사 15층"
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE; // 예: ACTIVE, INACTIVE, SUSPENDED
    
    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false; // 예: false (관리자 여부)
    
    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L; // 예: 0, 1, 2... (동시성 제어용)
    
    // Constructors
    public User() {}
    
    public User(String email, String name, String department) {
        this.email = email;
        this.name = name;
        this.department = department;
    }
    
    public User(String entraObjectId, String email, String name, String department) {
        this.entraObjectId = entraObjectId;
        this.email = email;
        this.name = name;
        this.department = department;
    }
    
    // Business Methods
    public String getFullDisplayName() {
        if (displayName != null && !displayName.trim().isEmpty()) {
            return displayName;
        }
        return department != null ? name + " (" + department + ")" : name;
    }
    
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
    
    public void activate() {
        this.status = UserStatus.ACTIVE;
    }
    
    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }
    
    public void suspend() {
        this.status = UserStatus.SUSPENDED;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEntraObjectId() { return entraObjectId; }
    public void setEntraObjectId(String entraObjectId) { this.entraObjectId = entraObjectId; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    
    public String getManagerEmail() { return managerEmail; }
    public void setManagerEmail(String managerEmail) { this.managerEmail = managerEmail; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getOfficeLocation() { return officeLocation; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }
    
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    
    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }
    
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}