package com.smartresume.controller;

import com.smartresume.dto.auth.UserProfileDto;
import com.smartresume.dto.common.ApiResponse;
import com.smartresume.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfile() {
        UserProfileDto profile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(@RequestBody Map<String, String> payload) {
        String fullName = payload.get("fullName");
        String phone = payload.get("phone");
        String bio = payload.get("bio");
        UserProfileDto updated = userService.updateProfile(fullName, phone, bio);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }
}
