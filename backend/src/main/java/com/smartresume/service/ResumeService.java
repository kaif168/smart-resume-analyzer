package com.smartresume.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartresume.dto.resume.*;
import com.smartresume.entity.*;
import com.smartresume.exception.BadRequestException;
import com.smartresume.exception.ResourceNotFoundException;
import com.smartresume.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ExtractedSkillRepository extractedSkillRepository;
    private final ResumeChecklistItemRepository checklistItemRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final ResumeStorageService resumeStorageService;
    private final ResumeParserService resumeParserService;
    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResumeService(ResumeRepository resumeRepository,
                         ExtractedSkillRepository extractedSkillRepository,
                         ResumeChecklistItemRepository checklistItemRepository,
                         ResumeAnalysisRepository resumeAnalysisRepository,
                         ResumeStorageService resumeStorageService,
                         ResumeParserService resumeParserService,
                         AuthService authService) {
        this.resumeRepository = resumeRepository;
        this.extractedSkillRepository = extractedSkillRepository;
        this.checklistItemRepository = checklistItemRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.resumeStorageService = resumeStorageService;
        this.resumeParserService = resumeParserService;
        this.authService = authService;
    }

    @Transactional
    public ResumeResponse uploadAndParseResume(MultipartFile file, String targetRole, String versionTag) {
        User user = authService.getAuthenticatedUser();

        // 1. Store file securely
        String storedFileName = resumeStorageService.storeFile(file);
        Path filePath = resumeStorageService.getFilePath(storedFileName);
        String extension = resumeStorageService.getFileExtension(storedFileName);

        // 2. Extract text with PDFBox or POI
        File physicalFile = filePath.toFile();
        String extractedText = resumeParserService.extractTextFromFile(physicalFile, extension);

        // 3. Parse structured data
        ParsedResumeDto parsed = resumeParserService.parseStructuredResume(extractedText);

        // 4. Create and save Resume entity
        Resume resume = new Resume();
        resume.setUser(user);
        resume.setFileName(storedFileName);
        resume.setOriginalFileName(file.getOriginalFilename());
        resume.setFileType(file.getContentType() != null ? file.getContentType() : "application/pdf");
        resume.setFileSize(file.getSize());
        resume.setStoragePath(filePath.toString());
        resume.setExtractedText(extractedText);
        resume.setCandidateName(parsed.getCandidateName());
        resume.setCandidateEmail(parsed.getEmail());
        resume.setCandidatePhone(parsed.getPhone());
        resume.setCandidateLocation(parsed.getLocation());
        resume.setProfessionalSummary(parsed.getSummary());
        resume.setExperienceYears(parsed.getExperienceYears());
        resume.setVersionTag(versionTag != null && !versionTag.trim().isEmpty() ? versionTag.trim() : "v1.0");
        resume.setTargetRole(targetRole != null && !targetRole.trim().isEmpty() ? targetRole.trim() : "Software Engineer");

        Resume savedResume = resumeRepository.save(resume);

        // 5. Save Extracted Skills
        Map<String, SkillCategory> dict = resumeParserService.getSkillDictionary();
        List<ExtractedSkill> skillEntities = new ArrayList<>();

        for (String skillName : parsed.getTechnicalSkills()) {
            SkillCategory cat = dict.getOrDefault(skillName, SkillCategory.TECHNICAL);
            skillEntities.add(new ExtractedSkill(savedResume, skillName, cat, "INTERMEDIATE"));
        }
        for (String skillName : parsed.getSoftSkills()) {
            skillEntities.add(new ExtractedSkill(savedResume, skillName, SkillCategory.SOFT, "INTERMEDIATE"));
        }
        extractedSkillRepository.saveAll(skillEntities);

        // 6. Generate Initial Improvement Checklist
        createInitialChecklist(savedResume, parsed);

        return toResponse(savedResume);
    }

    public List<ResumeResponse> getCurrentUserResumes() {
        User user = authService.getAuthenticatedUser();
        List<Resume> resumes = resumeRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
        return resumes.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ResumeDetailResponse getResumeDetail(Long id) {
        User user = authService.getAuthenticatedUser();
        Resume resume = resumeRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found or unauthorized access"));

        List<ExtractedSkill> skills = extractedSkillRepository.findAllByResumeId(resume.getId());
        List<ResumeChecklistItem> checklist = checklistItemRepository.findAllByResumeIdOrderByIdAsc(resume.getId());

        ResumeDetailResponse response = new ResumeDetailResponse();
        response.setId(resume.getId());
        response.setFileName(resume.getFileName());
        response.setOriginalFileName(resume.getOriginalFileName());
        response.setFileType(resume.getFileType());
        response.setFileSize(resume.getFileSize());
        response.setCandidateName(resume.getCandidateName());
        response.setCandidateEmail(resume.getCandidateEmail());
        response.setCandidatePhone(resume.getCandidatePhone());
        response.setCandidateLocation(resume.getCandidateLocation());
        response.setProfessionalSummary(resume.getProfessionalSummary());
        response.setExperienceYears(resume.getExperienceYears());
        response.setVersionTag(resume.getVersionTag());
        response.setTargetRole(resume.getTargetRole());
        response.setExtractedText(resume.getExtractedText());
        response.setExtractedSkills(skills);
        response.setChecklist(checklist);
        response.setCreatedAt(resume.getCreatedAt());

        return response;
    }

    @Transactional
    public void deleteResume(Long id) {
        User user = authService.getAuthenticatedUser();
        Resume resume = resumeRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found or unauthorized access"));

        resumeStorageService.deleteFile(resume.getFileName());
        resumeRepository.delete(resume);
    }

    @Transactional
    public ResumeChecklistItem toggleChecklistItem(Long resumeId, Long itemId, boolean completed) {
        User user = authService.getAuthenticatedUser();
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        ResumeChecklistItem item = checklistItemRepository.findByIdAndResumeId(itemId, resume.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Checklist item not found"));

        item.setCompleted(completed);
        return checklistItemRepository.save(item);
    }

    public ResumeVersionCompareDto compareResumeVersions(Long oldResumeId, Long newResumeId) {
        User user = authService.getAuthenticatedUser();
        Resume oldResume = resumeRepository.findByIdAndUserId(oldResumeId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Previous resume version not found"));
        Resume newResume = resumeRepository.findByIdAndUserId(newResumeId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("New resume version not found"));

        ResumeAnalysis oldAnalysis = resumeAnalysisRepository.findTopByResumeIdOrderByCreatedAtDesc(oldResumeId)
                .orElse(null);
        ResumeAnalysis newAnalysis = resumeAnalysisRepository.findTopByResumeIdOrderByCreatedAtDesc(newResumeId)
                .orElse(null);

        int oldScore = oldAnalysis != null ? oldAnalysis.getOverallScore() : 60;
        int newScore = newAnalysis != null ? newAnalysis.getOverallScore() : 75;

        List<ExtractedSkill> oldSkills = extractedSkillRepository.findAllByResumeId(oldResumeId);
        List<ExtractedSkill> newSkills = extractedSkillRepository.findAllByResumeId(newResumeId);

        Set<String> oldSkillNames = oldSkills.stream()
                .map(s -> s.getSkillName().toLowerCase())
                .collect(Collectors.toSet());

        List<String> newlyAddedSkills = newSkills.stream()
                .map(ExtractedSkill::getSkillName)
                .filter(name -> !oldSkillNames.contains(name.toLowerCase()))
                .distinct()
                .collect(Collectors.toList());

        ResumeVersionCompareDto dto = new ResumeVersionCompareDto();
        dto.setOldResumeId(oldResume.getId());
        dto.setOldFileName(oldResume.getOriginalFileName());
        dto.setOldVersionTag(oldResume.getVersionTag());
        dto.setOldOverallScore(oldScore);

        dto.setNewResumeId(newResume.getId());
        dto.setNewFileName(newResume.getOriginalFileName());
        dto.setNewVersionTag(newResume.getVersionTag());
        dto.setNewOverallScore(newScore);

        dto.setScoreDifference(newScore - oldScore);
        dto.setNewlyAddedSkills(newlyAddedSkills);

        List<String> resolved = new ArrayList<>();
        if (newScore > oldScore) {
            resolved.add("Improved keyword alignment and technical skill density.");
            if (oldResume.getProfessionalSummary() == null && newResume.getProfessionalSummary() != null) {
                resolved.add("Added targeted professional summary.");
            }
            if (newlyAddedSkills.size() > 0) {
                resolved.add("Incorporated " + newlyAddedSkills.size() + " in-demand industry technologies.");
            }
        }
        dto.setResolvedWeaknesses(resolved);

        List<String> remainingGaps = new ArrayList<>();
        if (newScore < 85) {
            remainingGaps.add("Quantify bullet points with percentages, revenue figures, or latency metrics.");
            remainingGaps.add("Ensure all certifications and cloud tools align with target role.");
        }
        dto.setRemainingGaps(remainingGaps);

        dto.setSummaryComparison(
                "Version " + newResume.getVersionTag() + " achieves an ATS score of " + newScore + "/100 " +
                "(" + (newScore >= oldScore ? "+" : "") + (newScore - oldScore) + " points compared to " + oldResume.getVersionTag() + ")."
        );

        return dto;
    }

    private void createInitialChecklist(Resume resume, ParsedResumeDto parsed) {
        List<ResumeChecklistItem> items = new ArrayList<>();
        items.add(new ResumeChecklistItem(resume, "chk_contact", "Verify complete contact details (email, phone, location)", "Contact", parsed.getEmail() != null && parsed.getPhone() != null));
        items.add(new ResumeChecklistItem(resume, "chk_summary", "Write a tailored 3-4 sentence professional summary", "Summary", parsed.getSummary() != null && !parsed.getSummary().isEmpty()));
        items.add(new ResumeChecklistItem(resume, "chk_skills", "Group technical skills clearly by Category (Languages, Frameworks, Cloud)", "Skills", parsed.getTechnicalSkills().size() >= 5));
        items.add(new ResumeChecklistItem(resume, "chk_metrics", "Include quantifiable results (e.g., % improvement, scale handled)", "Experience", false));
        items.add(new ResumeChecklistItem(resume, "chk_action_verbs", "Start every bullet point with a strong action verb (Engineered, Architected)", "Format", false));
        items.add(new ResumeChecklistItem(resume, "chk_ats_friendly", "Ensure single-column layout without unreadable tables or graphics", "ATS", true));
        checklistItemRepository.saveAll(items);
    }

    public ResumeResponse toResponse(Resume resume) {
        ResumeResponse resp = new ResumeResponse();
        resp.setId(resume.getId());
        resp.setFileName(resume.getFileName());
        resp.setOriginalFileName(resume.getOriginalFileName());
        resp.setFileType(resume.getFileType());
        resp.setFileSize(resume.getFileSize());
        resp.setCandidateName(resume.getCandidateName());
        resp.setCandidateEmail(resume.getCandidateEmail());
        resp.setCandidatePhone(resume.getCandidatePhone());
        resp.setCandidateLocation(resume.getCandidateLocation());
        resp.setVersionTag(resume.getVersionTag());
        resp.setTargetRole(resume.getTargetRole());
        resp.setCreatedAt(resume.getCreatedAt());

        resumeAnalysisRepository.findTopByResumeIdOrderByCreatedAtDesc(resume.getId())
                .ifPresent(analysis -> resp.setLatestScore(analysis.getOverallScore()));

        return resp;
    }
}
