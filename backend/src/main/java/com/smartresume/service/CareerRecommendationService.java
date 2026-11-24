package com.smartresume.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartresume.dto.recommendation.CareerRecommendationDto;
import com.smartresume.dto.recommendation.SkillGapDto;
import com.smartresume.entity.*;
import com.smartresume.exception.ResourceNotFoundException;
import com.smartresume.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CareerRecommendationService {

    private final ResumeRepository resumeRepository;
    private final ExtractedSkillRepository extractedSkillRepository;
    private final RecommendationRepository recommendationRepository;
    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Standard Industry Skill Benchmark Matrix
    private static final Map<String, List<String>> ROLE_BENCHMARKS = new LinkedHashMap<>();

    static {
        ROLE_BENCHMARKS.put("Java Developer", Arrays.asList(
                "Java", "Spring Boot", "MySQL", "REST API", "Hibernate", "Git", "Maven", "Docker", "JUnit", "Microservices"
        ));
        ROLE_BENCHMARKS.put("Full Stack Developer", Arrays.asList(
                "Java", "Spring Boot", "React", "JavaScript", "HTML", "CSS", "MySQL", "Git", "REST API", "Tailwind CSS", "Docker"
        ));
        ROLE_BENCHMARKS.put("Frontend Developer", Arrays.asList(
                "React", "JavaScript", "TypeScript", "HTML", "CSS", "Tailwind CSS", "Redux", "Git", "REST API"
        ));
        ROLE_BENCHMARKS.put("Backend Developer", Arrays.asList(
                "Java", "Spring Boot", "Microservices", "REST API", "SQL", "Redis", "Kafka", "Docker", "Git", "AWS"
        ));
        ROLE_BENCHMARKS.put("Python & Data Analyst", Arrays.asList(
                "Python", "SQL", "Pandas", "NumPy", "Data Visualization", "Tableau", "PowerBI", "Statistics", "Machine Learning", "Git"
        ));
        ROLE_BENCHMARKS.put("Software Engineer", Arrays.asList(
                "Java", "Python", "Data Structures", "Algorithms", "Object-Oriented Programming", "Git", "SQL", "Unit Testing", "Problem Solving"
        ));
    }

    public CareerRecommendationService(ResumeRepository resumeRepository,
                                       ExtractedSkillRepository extractedSkillRepository,
                                       RecommendationRepository recommendationRepository,
                                       AuthService authService) {
        this.resumeRepository = resumeRepository;
        this.extractedSkillRepository = extractedSkillRepository;
        this.recommendationRepository = recommendationRepository;
        this.authService = authService;
    }

    @Transactional
    public List<CareerRecommendationDto> getCareerRecommendations(Long resumeId) {
        User user = authService.getAuthenticatedUser();
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        List<ExtractedSkill> candidateSkills = extractedSkillRepository.findAllByResumeId(resume.getId());
        Set<String> candidateSkillNames = candidateSkills.stream()
                .map(s -> s.getSkillName().toLowerCase())
                .collect(Collectors.toSet());

        String rawLower = (resume.getExtractedText() != null ? resume.getExtractedText() : "").toLowerCase();

        List<CareerRecommendationDto> recommendations = new ArrayList<>();
        List<Recommendation> entities = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : ROLE_BENCHMARKS.entrySet()) {
            String role = entry.getKey();
            List<String> required = entry.getValue();

            List<String> matching = new ArrayList<>();
            List<String> missing = new ArrayList<>();

            for (String req : required) {
                if (candidateSkillNames.contains(req.toLowerCase()) || rawLower.contains(req.toLowerCase())) {
                    matching.add(req);
                } else {
                    missing.add(req);
                }
            }

            int matchPercent = (int) Math.round(((double) matching.size() / required.size()) * 100);
            matchPercent = Math.max(10, Math.min(100, matchPercent));

            CareerRecommendationDto dto = new CareerRecommendationDto();
            dto.setJobTitle(role);
            dto.setCompatibilityPercentage(matchPercent);
            dto.setMatchingSkills(matching);
            dto.setMissingSkills(missing);
            dto.setGrowthPotential(matchPercent >= 70 ? "Very High" : "High");

            List<String> learningAreas = missing.stream().limit(3).collect(Collectors.toList());
            dto.setSuggestedLearningAreas(learningAreas);

            dto.setReason("Your resume displays strong overlap in " + matching.size() + " key technical areas (" +
                    String.join(", ", matching.subList(0, Math.min(matching.size(), 3))) + ").");

            recommendations.add(dto);

            // Save to database
            Recommendation recEntity = new Recommendation();
            recEntity.setResume(resume);
            recEntity.setRecommendedRole(role);
            recEntity.setCompatibilityPercentage(matchPercent);
            recEntity.setGrowthPotential(dto.getGrowthPotential());
            try {
                recEntity.setMatchingSkillsJson(objectMapper.writeValueAsString(matching));
                recEntity.setMissingSkillsJson(objectMapper.writeValueAsString(missing));
                recEntity.setLearningPathJson(objectMapper.writeValueAsString(learningAreas));
            } catch (JsonProcessingException ignored) {}
            entities.add(recEntity);
        }

        recommendations.sort((a, b) -> Integer.compare(b.getCompatibilityPercentage(), a.getCompatibilityPercentage()));

        recommendationRepository.deleteAllByResumeId(resume.getId());
        recommendationRepository.saveAll(entities);

        return recommendations;
    }

    public SkillGapDto analyzeSkillGap(Long resumeId, String targetRole) {
        User user = authService.getAuthenticatedUser();
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        if (targetRole == null || targetRole.trim().isEmpty()) {
            targetRole = resume.getTargetRole() != null ? resume.getTargetRole() : "Java Developer";
        }

        List<String> benchmarkSkills = ROLE_BENCHMARKS.getOrDefault(targetRole, ROLE_BENCHMARKS.get("Software Engineer"));

        List<ExtractedSkill> candidateSkills = extractedSkillRepository.findAllByResumeId(resume.getId());
        Set<String> candidateSkillNames = candidateSkills.stream()
                .map(s -> s.getSkillName().toLowerCase())
                .collect(Collectors.toSet());

        String rawLower = (resume.getExtractedText() != null ? resume.getExtractedText() : "").toLowerCase();

        List<String> existing = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String skill : benchmarkSkills) {
            if (candidateSkillNames.contains(skill.toLowerCase()) || rawLower.contains(skill.toLowerCase())) {
                existing.add(skill);
            } else {
                missing.add(skill);
            }
        }

        int coverage = (int) Math.round(((double) existing.size() / benchmarkSkills.size()) * 100);

        SkillGapDto dto = new SkillGapDto();
        dto.setTargetRole(targetRole);
        dto.setCoveragePercentage(coverage);
        dto.setExistingSkills(existing);
        dto.setMissingSkills(missing);
        dto.setRecommendedSkillsToLearn(missing);

        List<String> sequence = new ArrayList<>();
        if (!missing.isEmpty()) {
            sequence.add("Phase 1 (Foundations): Master " + missing.get(0) + " core principles & project building.");
            if (missing.size() > 1) {
                sequence.add("Phase 2 (Architecture): Implement " + missing.get(1) + " integration with real database systems.");
            }
            if (missing.size() > 2) {
                sequence.add("Phase 3 (Production Readiness): Add automated testing and containerization with " + missing.get(2) + ".");
            }
        } else {
            sequence.add("Complete coverage achieved! Focus on advanced system design and scalable distributed architectures.");
        }
        dto.setSuggestedLearningSequence(sequence);

        return dto;
    }
}
