package com.smartresume.controller;

import com.smartresume.dto.admin.AdminDashboardStatsDto;
import com.smartresume.dto.admin.UserAdminDto;
import com.smartresume.dto.common.ApiResponse;
import com.smartresume.entity.JobDescription;
import com.smartresume.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardStatsDto>> getDashboardStats() {
        AdminDashboardStatsDto stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserAdminDto>>> getAllUsers() {
        List<UserAdminDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(
            @PathVariable Long userId,
            @RequestBody Map<String, Boolean> body) {
        boolean enabled = body.getOrDefault("enabled", true);
        adminService.toggleUserStatus(userId, enabled);
        return ResponseEntity.ok(ApiResponse.ok("User status updated successfully", null));
    }

    @PostMapping("/job-templates")
    public ResponseEntity<ApiResponse<JobDescription>> createJobTemplate(@RequestBody JobDescription template) {
        JobDescription created = adminService.createJobTemplate(template);
        return ResponseEntity.ok(ApiResponse.ok("Job template created", created));
    }

    @DeleteMapping("/job-templates/{templateId}")
    public ResponseEntity<ApiResponse<Void>> deleteJobTemplate(@PathVariable Long templateId) {
        adminService.deleteJobTemplate(templateId);
        return ResponseEntity.ok(ApiResponse.ok("Job template deleted", null));
    }
}
