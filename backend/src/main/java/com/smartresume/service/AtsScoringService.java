package com.smartresume.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartresume.dto.analysis.AnalysisResponse;
import com.smartresume.dto.analysis.CategoryScoreDto;
import com.smartresume.entity.*;
import com.smartresume.exception.ResourceNotFoundException;
import com.smartresume.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AtsScoringService {

    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final ExtractedSkillRepository extractedSkillRepository;
    private final AnalysisHistoryRepository analysisHistoryRepository;
    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Standard Power Action Verbs for ATS resumes
    private static final Set<String> ACTION_VERBS = new HashSet<>(Arrays.asList(
            "architected", "engineered", "developed", "deployed", "implemented", "optimized",
            "designed", "spearheaded", "accelerated", "automated", "refactored", "orchestrated",
            "streamlined", "built", "mentored", "collaborated", "managed", "increased", "decreased",
            "resolved", "scaled", "delivered", "executed", "configured", "maintained"
    ));

    // Regex for numbers, percentages, metrics
    private static final Pattern METRICS_PATTERN = Pattern.compile(
            "(\\b\\d+%(?:\\s*increase|\\s*decrease|\\s*reduction|\\s*boost)?\\b|\\$\\d+(?:[kKmMbB])?|\\b\\d+(?:x|X)\\b|\\b\\d+\\s*(?:million|billion|thousand|ms|seconds|users|requests|services)\\b)",
            Pattern.CASE_INSENSITIVE
    );

    public AtsScoringService(ResumeRepository resumeRepository,
                             ResumeAnalysisRepository resumeAnalysisRepository,
                             ExtractedSkillRepository extractedSkillRepository,
                             AnalysisHistoryRepository analysisHistoryRepository,
                             AuthService authService) {
        this.resumeRepository = resumeRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.extractedSkillRepository = extractedSkillRepository;
        this.analysisHistoryRepository = analysisHistoryRepository;
        this.authService = authService;
    }

    @Transactional
    public AnalysisResponse analyzeResume(Long resumeId) {
        User user = authService.getAuthenticatedUser();
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found or unauthorized"));

        List<ExtractedSkill> skills = extractedSkillRepository.findAllByResumeId(resume.getId());
        String text = resume.getExtractedText() != null ? resume.getExtractedText() : "";

        // 1. Contact Information Score (Max 10)
        int contactScore = 0;
        List<String> contactFeedback = new ArrayList<>();
        if (resume.getCandidateEmail() != null && !resume.getCandidateEmail().isEmpty()) {
            contactScore += 3;
        } else {
            contactFeedback.add("Missing email address");
        }
        if (resume.getCandidatePhone() != null && !resume.getCandidatePhone().isEmpty()) {
            contactScore += 3;
        } else {
            contactFeedback.add("Missing phone number");
        }
        if (resume.getCandidateLocation() != null && !"Not Specified".equalsIgnoreCase(resume.getCandidateLocation())) {
            contactScore += 2;
        } else {
            contactFeedback.add("Location not detected");
        }
        if (text.toLowerCase().contains("linkedin.com") || text.toLowerCase().contains("github.com") || text.toLowerCase().contains("portfolio")) {
            contactScore += 2;
        } else {
            contactFeedback.add("Add LinkedIn or GitHub profile link");
        }
        contactScore = Math.min(contactScore, 10);

        // 2. Resume Structure and Headings Score (Max 15)
        int structureScore = 0;
        List<String> missingHeadings = new ArrayList<>();
        String upperText = text.toUpperCase();

        if (upperText.contains("SUMMARY") || upperText.contains("OBJECTIVE") || upperText.contains("PROFILE")) {
            structureScore += 3;
        } else {
            missingHeadings.add("Professional Summary");
        }
        if (upperText.contains("EXPERIENCE") || upperText.contains("EMPLOYMENT")) {
            structureScore += 3;
        } else {
            missingHeadings.add("Work Experience");
        }
        if (upperText.contains("EDUCATION") || upperText.contains("ACADEMIC")) {
            structureScore += 3;
        } else {
            missingHeadings.add("Education");
        }
        if (upperText.contains("SKILLS") || upperText.contains("TECHNICAL")) {
            structureScore += 3;
        } else {
            missingHeadings.add("Skills Section");
        }
        if (upperText.contains("PROJECTS") || upperText.contains("CERTIFICATION")) {
            structureScore += 3;
        } else {
            missingHeadings.add("Projects/Certifications");
        }
        structureScore = Math.min(structureScore, 15);

        // 3. Skills Relevance (Max 20)
        int skillsScore;
        int skillCount = skills.size();
        if (skillCount >= 14) {
            skillsScore = 20;
        } else if (skillCount >= 10) {
            skillsScore = 17;
        } else if (skillCount >= 6) {
            skillsScore = 13;
        } else if (skillCount >= 3) {
            skillsScore = 8;
        } else {
            skillsScore = 4;
        }

        // 4. Job Keyword Matching (Max 20)
        int keywordScore = calculateKeywordDensityScore(text, resume.getTargetRole());

        // 5. Work Experience & Achievements (Max 15)
        int expScore = 0;
        int actionVerbMatches = countActionVerbs(text);
        if (actionVerbMatches >= 8) {
            expScore += 7;
        } else if (actionVerbMatches >= 4) {
            expScore += 5;
        } else if (actionVerbMatches >= 1) {
            expScore += 3;
        }

        int metricMatches = countMetrics(text);
        if (metricMatches >= 4) {
            expScore += 8;
        } else if (metricMatches >= 2) {
            expScore += 5;
        } else if (metricMatches >= 1) {
            expScore += 3;
        }
        expScore = Math.min(expScore, 15);

        // 6. Education and Certifications (Max 10)
        int eduScore = 0;
        if (upperText.contains("BACHELOR") || upperText.contains("MASTER") || upperText.contains("B.TECH")
                || upperText.contains("M.TECH") || upperText.contains("B.S.") || upperText.contains("COMPUTER SCIENCE")
                || upperText.contains("DEGREE")) {
            eduScore += 6;
        }
        if (upperText.contains("UNIVERSITY") || upperText.contains("COLLEGE") || upperText.contains("INSTITUTE")) {
            eduScore += 2;
        }
        if (upperText.contains("CERTIF") || upperText.contains("AWS") || upperText.contains("ORACLE")
                || upperText.contains("ASSOCIATE") || upperText.contains("PROFESSIONAL")) {
            eduScore += 2;
        }
        eduScore = Math.min(eduScore, 10);

        // 7. Readability and Formatting (Max 10)
        int readabilityScore = 0;
        int wordCount = text.split("\\s+").length;
        if (wordCount >= 300 && wordCount <= 1000) {
            readabilityScore += 5;
        } else if (wordCount >= 200 && wordCount <= 1400) {
            readabilityScore += 3;
        } else {
            readabilityScore += 1;
        }

        // Clean text check: no unreadable chars
        if (!text.contains("") && !text.contains("???") && text.length() > 200) {
            readabilityScore += 5;
        } else {
            readabilityScore += 2;
        }
        readabilityScore = Math.min(readabilityScore, 10);

        // Overall Score (0 - 100)
        int overallScore = contactScore + structureScore + skillsScore + keywordScore + expScore + eduScore + readabilityScore;
        overallScore = Math.max(10, Math.min(overallScore, 100));

        // Generate Strengths & Weaknesses
        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        if (skillsScore >= 16) {
            strengths.add("Diverse technical skills covering " + skillCount + " modern technologies.");
        } else {
            weaknesses.add("Low technical keyword density. Modern ATS scans prioritize specific tooling and framework keywords.");
            suggestions.add("Add missing core technologies relevant to your target role (" + (resume.getTargetRole() != null ? resume.getTargetRole() : "Software Engineer") + ").");
        }

        if (expScore >= 11) {
            strengths.add("Strong achievement statements with " + metricMatches + "+ quantifiable impact metrics.");
        } else {
            weaknesses.add("Achievement bullet points lack sufficient numbers, percentages, or measurable business results.");
            suggestions.add("Incorporate concrete metrics: e.g. 'Optimized API latency by 35%' or 'Managed a microservice scaling to 1M daily requests'.");
        }

        if (structureScore == 15) {
            strengths.add("Clear, standard ATS heading structure (Summary, Experience, Education, Skills, Projects).");
        } else {
            weaknesses.add("Missing standard ATS sections: " + String.join(", ", missingHeadings));
            suggestions.add("Include clearly separated standard section headings in uppercase for maximum ATS parsability.");
        }

        if (actionVerbMatches >= 5) {
            strengths.add("Effective use of strong action verbs at the start of bullet points.");
        } else {
            suggestions.add("Start experience bullet points with active verbs like 'Architected', 'Engineered', 'Streamlined', or 'Spearheaded'.");
        }

        if (contactScore == 10) {
            strengths.add("Complete contact information including professional email, phone, location, and profiles.");
        } else {
            suggestions.add("Ensure professional contact info (email, phone, location, LinkedIn/GitHub) is visible in the header.");
        }

        // Save Analysis Entity
        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setResume(resume);
        analysis.setUser(user);
        analysis.setOverallScore(overallScore);
        analysis.setContactScore(contactScore);
        analysis.setStructureScore(structureScore);
        analysis.setSkillsScore(skillsScore);
        analysis.setKeywordScore(keywordScore);
        analysis.setExperienceScore(expScore);
        analysis.setEducationScore(eduScore);
        analysis.setReadabilityScore(readabilityScore);

        try {
            analysis.setStrengthsJson(objectMapper.writeValueAsString(strengths));
            analysis.setWeaknessesJson(objectMapper.writeValueAsString(weaknesses));
            analysis.setActionableSuggestionsJson(objectMapper.writeValueAsString(suggestions));
            analysis.setMissingSectionsJson(objectMapper.writeValueAsString(missingHeadings));
        } catch (JsonProcessingException ignored) {}

        ResumeAnalysis saved = resumeAnalysisRepository.save(analysis);

        // Record in History
        AnalysisHistory history = new AnalysisHistory(
                user, resume, saved.getId(), overallScore, resume.getTargetRole(), "ATS score: " + overallScore + "/100"
        );
        analysisHistoryRepository.save(history);

        // Build Response
        return toAnalysisResponse(saved, resume, strengths, weaknesses, suggestions, missingHeadings);
    }

    public AnalysisResponse getLatestAnalysis(Long resumeId) {
        User user = authService.getAuthenticatedUser();
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        ResumeAnalysis analysis = resumeAnalysisRepository.findTopByResumeIdOrderByCreatedAtDesc(resumeId)
                .orElseGet(() -> {
                    // Trigger fresh analysis if none exists yet
                    return null;
                });

        if (analysis == null) {
            return analyzeResume(resumeId);
        }

        List<String> strengths = parseJsonList(analysis.getStrengthsJson());
        List<String> weaknesses = parseJsonList(analysis.getWeaknessesJson());
        List<String> suggestions = parseJsonList(analysis.getActionableSuggestionsJson());
        List<String> missing = parseJsonList(analysis.getMissingSectionsJson());

        return toAnalysisResponse(analysis, resume, strengths, weaknesses, suggestions, missing);
    }

    public List<AnalysisHistory> getUserAnalysisHistory() {
        User user = authService.getAuthenticatedUser();
        return analysisHistoryRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
    }

    private int countActionVerbs(String text) {
        String lower = text.toLowerCase();
        int count = 0;
        for (String verb : ACTION_VERBS) {
            if (lower.contains(verb)) {
                count++;
            }
        }
        return count;
    }

    private int countMetrics(String text) {
        Matcher matcher = METRICS_PATTERN.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private int calculateKeywordDensityScore(String text, String targetRole) {
        String lower = text.toLowerCase();
        List<String> targetKeywords = Arrays.asList(
                "rest", "api", "database", "sql", "testing", "unit", "git", "cloud",
                "agile", "scrum", "performance", "scalable", "design", "security", "ci/cd"
        );
        int matches = 0;
        for (String kw : targetKeywords) {
            if (lower.contains(kw)) {
                matches++;
            }
        }
        if (matches >= 10) return 20;
        if (matches >= 7) return 16;
        if (matches >= 5) return 12;
        if (matches >= 3) return 8;
        return 5;
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isEmpty()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private AnalysisResponse toAnalysisResponse(ResumeAnalysis analysis, Resume resume,
                                                List<String> strengths, List<String> weaknesses,
                                                List<String> suggestions, List<String> missingHeadings) {
        AnalysisResponse response = new AnalysisResponse();
        response.setId(analysis.getId());
        response.setResumeId(resume.getId());
        response.setCandidateName(resume.getCandidateName());
        response.setTargetRole(resume.getTargetRole());
        response.setOverallScore(analysis.getOverallScore());
        response.setCreatedAt(analysis.getCreatedAt());

        // Category breakdown
        List<CategoryScoreDto> categoryScores = new ArrayList<>();
        categoryScores.add(new CategoryScoreDto("Contact Information", analysis.getContactScore(), 10, "Email, telephone, location, and professional portfolio links."));
        categoryScores.add(new CategoryScoreDto("Resume Structure & Headings", analysis.getStructureScore(), 15, "Standard recognized section headers for ATS parsing."));
        categoryScores.add(new CategoryScoreDto("Skills Relevance", analysis.getSkillsScore(), 20, "Breadth and depth of technical and professional skills."));
        categoryScores.add(new CategoryScoreDto("Job Keyword Matching", analysis.getKeywordScore(), 20, "Alignment with modern industry and engineering terms."));
        categoryScores.add(new CategoryScoreDto("Work Experience & Achievements", analysis.getExperienceScore(), 15, "Strong action verbs and quantifiable impact metrics."));
        categoryScores.add(new CategoryScoreDto("Education & Certifications", analysis.getEducationScore(), 10, "Academic degrees, universities, and industry credentials."));
        categoryScores.add(new CategoryScoreDto("Readability & Formatting", analysis.getReadabilityScore(), 10, "Word count, formatting cleanliness, and textual density."));

        response.setCategoryScores(categoryScores);
        response.setStrengths(strengths);
        response.setWeaknesses(weaknesses);
        response.setActionableSuggestions(suggestions);
        response.setMissingSections(missingHeadings);

        return response;
    }
}
