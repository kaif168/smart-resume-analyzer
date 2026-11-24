package com.smartresume.service;

import com.smartresume.dto.auth.UserProfileDto;
import com.smartresume.entity.User;
import com.smartresume.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public UserService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public UserProfileDto getCurrentUserProfile() {
        User user = authService.getAuthenticatedUser();
        return toDto(user);
    }

    @Transactional
    public UserProfileDto updateProfile(String fullName, String phone, String bio) {
        User user = authService.getAuthenticatedUser();
        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName.trim());
        }
        if (phone != null) {
            user.setPhone(phone.trim());
        }
        if (bio != null) {
            user.setBio(bio.trim());
        }
        User updated = userRepository.save(user);
        return toDto(updated);
    }

    public UserProfileDto toDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setBio(user.getBio());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
