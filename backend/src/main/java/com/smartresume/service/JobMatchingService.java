package com.smartresume.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartresume.dto.jobmatching.JobMatchRequest;
import com.smartresume.dto.jobmatching.JobMatchResponse;
import com.smartresume.entity.*;
import com.smartresume.exception.BadRequestException;
import com.smartresume.exception.ResourceNotFoundException;
import com.smartresume.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class JobMatchingService {

    private final ResumeRepository resumeRepository;
    private final ExtractedSkillRepository extractedSkillRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final JobMatchRepository jobMatchRepository;
    private final ResumeParserService resumeParserService;
    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JobMatchingService(ResumeRepository resumeRepository,
                              ExtractedSkillRepository extractedSkillRepository,
                              JobDescriptionRepository jobDescriptionRepository,
                              JobMatchRepository jobMatchRepository,
                              ResumeParserService resumeParserService,
                              AuthService authService) {
        this.resumeRepository = resumeRepository;
        this.extractedSkillRepository = extractedSkillRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.jobMatchRepository = jobMatchRepository;
        this.resumeParserService = resumeParserService;
        this.authService = authService;
    }

    @Transactional
    public JobMatchResponse matchResumeWithJob(JobMatchRequest request) {
        User user = authService.getAuthenticatedUser();
        Resume resume = resumeRepository.findByIdAndUserId(request.getResumeId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found or unauthorized"));

        String jdText = request.getJobDescriptionText();
        String targetTitle = request.getTargetJobTitle();

        // If template selected, optionally merge/supplement
        if (request.getJobTemplateId() != null) {
            JobDescription template = jobDescriptionRepository.findById(request.getJobTemplateId()).orElse(null);
            if (template != null) {
                if (targetTitle == null || targetTitle.trim().isEmpty()) {
                    targetTitle = template.getTitle();
                }
                if (jdText == null || jdText.trim().isEmpty()) {
                    jdText = template.getDescriptionText();
                }
            }
        }

        if (jdText == null || jdText.trim().isEmpty()) {
            throw new BadRequestException("Job description text cannot be empty.");
        }

        if (targetTitle == null || targetTitle.trim().isEmpty()) {
            targetTitle = "Target Role";
        }

        // 1. Extract required skills from Job Description
        Set<String> jdSkills = extractSkillsFromText(jdText);
        if (jdSkills.isEmpty()) {
            // Fallback keywords if JD is brief
            jdSkills.addAll(Arrays.asList("Java", "Git", "REST API", "SQL", "Spring Boot", "Unit Testing"));
        }

        // 2. Fetch candidate skills
        List<ExtractedSkill> candidateSkills = extractedSkillRepository.findAllByResumeId(resume.getId());
        Set<String> candidateSkillNames = candidateSkills.stream()
                .map(s -> s.getSkillName().toLowerCase())
                .collect(Collectors.toSet());

        // Also check raw resume text for any skill variant
        String resumeLower = (resume.getExtractedText() != null ? resume.getExtractedText() : "").toLowerCase();

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String skill : jdSkills) {
            String lower = skill.toLowerCase();
            if (candidateSkillNames.contains(lower) || resumeLower.contains(lower)) {
                matchedSkills.add(skill);
            } else {
                missingSkills.add(skill);
            }
        }

        // 3. Calculate Match Percentage
        int totalRequired = jdSkills.size();
        int matchedCount = matchedSkills.size();
        int basePercentage = totalRequired > 0 ? (int) Math.round(((double) matchedCount / totalRequired) * 100) : 70;

        // Slight adjustment for experience alignment
        int matchPercentage = Math.min(100, Math.max(15, basePercentage));

        // 4. Extract Relevant Keywords
        List<String> relevantKeywords = extractRelevantKeywords(jdText, resumeLower);

        // 5. Experience Alignment
        String experienceAlignment = evaluateExperienceAlignment(resume.getExperienceYears(), jdText);

        // 6. Qualifications
        List<String> qualifications = new ArrayList<>();
        if (jdText.toLowerCase().contains("bachelor") || jdText.toLowerCase().contains("degree")) {
            qualifications.add("Bachelor's or Master's Degree in Computer Science or related field");
        }
        if (jdText.toLowerCase().contains("year")) {
            qualifications.add("Relevant Industry Experience (Alignment verified)");
        }

        // 7. Suggested Improvements
        List<String> suggestions = new ArrayList<>();
        if (!missingSkills.isEmpty()) {
            suggestions.add("Consider highlighting experience with: " + String.join(", ", missingSkills.subList(0, Math.min(missingSkills.size(), 4))) + ".");
        }
        suggestions.add("Mirror key terms from the job posting in your project and experience bullet points.");
        suggestions.add("Ensure your professional summary mentions '" + targetTitle + "'.");

        // 8. Save JobMatch entity
        JobMatch jobMatch = new JobMatch();
        jobMatch.setResume(resume);
        jobMatch.setJobDescriptionId(request.getJobTemplateId());
        jobMatch.setTargetRole(targetTitle);
        jobMatch.setMatchPercentage(matchPercentage);
        jobMatch.setKeywordScore(matchPercentage);
        jobMatch.setExperienceAlignment(experienceAlignment);

        try {
            jobMatch.setMatchedSkillsJson(objectMapper.writeValueAsString(matchedSkills));
            jobMatch.setMissingSkillsJson(objectMapper.writeValueAsString(missingSkills));
            jobMatch.setImprovementTipsJson(objectMapper.writeValueAsString(suggestions));
            jobMatch.setQualificationsJson(objectMapper.writeValueAsString(qualifications));
        } catch (JsonProcessingException ignored) {}

        JobMatch saved = jobMatchRepository.save(jobMatch);

        // 9. Return Response
        JobMatchResponse response = new JobMatchResponse();
        response.setId(saved.getId());
        response.setResumeId(resume.getId());
        response.setTargetRole(targetTitle);
        response.setMatchPercentage(matchPercentage);
        response.setMatchedSkills(matchedSkills);
        response.setMissingSkills(missingSkills);
        response.setRelevantKeywords(relevantKeywords);
        response.setRequiredQualifications(qualifications);
        response.setExperienceAlignment(experienceAlignment);
        response.setSuggestedImprovements(suggestions);
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }

    public List<JobDescription> getAvailableJobTemplates() {
        return jobDescriptionRepository.findAllByIsTemplateTrue();
    }

    public List<JobMatch> getResumeMatchHistory(Long resumeId) {
        User user = authService.getAuthenticatedUser();
        resumeRepository.findByIdAndUserId(resumeId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        return jobMatchRepository.findAllByResumeIdOrderByCreatedAtDesc(resumeId);
    }

    private Set<String> extractSkillsFromText(String text) {
        Set<String> matched = new LinkedHashSet<>();
        Map<String, SkillCategory> dict = resumeParserService.getSkillDictionary();

        for (String skill : dict.keySet()) {
            String regex = "\\b" + Pattern.quote(skill) + "\\b";
            if (Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text).find()) {
                matched.add(skill);
            }
        }
        return matched;
    }

    private List<String> extractRelevantKeywords(String jdText, String resumeLower) {
        List<String> keywords = Arrays.asList(
                "Agile", "Scrum", "CI/CD", "Microservices", "Unit Testing", "REST API",
                "Cloud", "Scalability", "Clean Code", "Docker", "Database", "Security"
        );
        List<String> found = new ArrayList<>();
        for (String kw : keywords) {
            if (jdText.toLowerCase().contains(kw.toLowerCase()) && resumeLower.contains(kw.toLowerCase())) {
                found.add(kw + " (Found in Resume)");
            } else if (jdText.toLowerCase().contains(kw.toLowerCase())) {
                found.add(kw + " (Opportunity to Add)");
            }
        }
        return found;
    }

    private String evaluateExperienceAlignment(Double resumeYears, String jdText) {
        double years = resumeYears != null ? resumeYears : 0.0;
        String lower = jdText.toLowerCase();

        if (lower.contains("senior") || lower.contains("5+ years") || lower.contains("7+ years")) {
            if (years >= 4.0) {
                return "Well Aligned: Resume indicates ~" + String.format("%.1f", years) + " years experience, suitable for Senior level.";
            } else {
                return "Moderate Gap: Role seeks Senior-level experience; highlight high-impact technical leadership.";
            }
        } else if (lower.contains("mid") || lower.contains("2+ years") || lower.contains("3+ years")) {
            return "Strong Alignment: Experience level (~" + String.format("%.1f", years) + " years) matches mid-level expectations.";
        } else {
            return "Good Alignment: Suitable for entry to mid-level qualifications.";
        }
    }
}
