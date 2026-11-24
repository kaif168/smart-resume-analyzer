package com.smartresume.dto.admin;

import com.smartresume.entity.Role;
import java.time.LocalDateTime;

public class UserAdminDto {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private Role role;
    private boolean enabled;
    private long resumeCount;
    private LocalDateTime createdAt;

    public UserAdminDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public long getResumeCount() { return resumeCount; }
    public void setResumeCount(long resumeCount) { this.resumeCount = resumeCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
