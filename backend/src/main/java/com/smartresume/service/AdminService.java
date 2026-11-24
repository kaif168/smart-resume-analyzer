package com.smartresume.service;

import com.smartresume.dto.admin.AdminDashboardStatsDto;
import com.smartresume.dto.admin.UserAdminDto;
import com.smartresume.entity.JobDescription;
import com.smartresume.entity.User;
import com.smartresume.exception.BadRequestException;
import com.smartresume.exception.ResourceNotFoundException;
import com.smartresume.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final JobDescriptionRepository jobDescriptionRepository;

    public AdminService(UserRepository userRepository,
                        ResumeRepository resumeRepository,
                        ResumeAnalysisRepository resumeAnalysisRepository,
                        JobDescriptionRepository jobDescriptionRepository) {
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
    }

    public AdminDashboardStatsDto getDashboardStats() {
        AdminDashboardStatsDto stats = new AdminDashboardStatsDto();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalResumes(resumeRepository.count());
        stats.setTotalAnalyses(resumeAnalysisRepository.count());

        Double avgScore = resumeAnalysisRepository.getGlobalAverageScore();
        stats.setAverageAtsScore(avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 72.5);
        stats.setTotalJobTemplates(jobDescriptionRepository.count());

        Map<String, Long> roleDist = new HashMap<>();
        jobDescriptionRepository.findAll().forEach(jd -> {
            String cat = jd.getRoleCategory();
            roleDist.put(cat, roleDist.getOrDefault(cat, 0L) + 1);
        });
        stats.setRoleDistribution(roleDist);

        return stats;
    }

    public List<UserAdminDto> getAllUsers() {
        List<User> users = userRepository.findAllByOrderByCreatedAtDesc();
        List<UserAdminDto> dtoList = new ArrayList<>();

        for (User u : users) {
            UserAdminDto dto = new UserAdminDto();
            dto.setId(u.getId());
            dto.setEmail(u.getEmail());
            dto.setFullName(u.getFullName());
            dto.setPhone(u.getPhone());
            dto.setRole(u.getRole());
            dto.setEnabled(u.isEnabled());
            dto.setCreatedAt(u.getCreatedAt());
            dto.setResumeCount(resumeRepository.countByUserId(u.getId()));
            dtoList.add(dto);
        }

        return dtoList;
    }

    @Transactional
    public void toggleUserStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if ("admin@smartresume.com".equalsIgnoreCase(user.getEmail())) {
            throw new BadRequestException("Primary system administrator cannot be disabled.");
        }

        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Transactional
    public JobDescription createJobTemplate(JobDescription template) {
        template.setTemplate(true);
        return jobDescriptionRepository.save(template);
    }

    @Transactional
    public void deleteJobTemplate(Long templateId) {
        jobDescriptionRepository.deleteById(templateId);
    }
}
