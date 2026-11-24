package com.smartresume.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartresume.dto.ai.*;
import com.smartresume.dto.interview.InterviewQuestionDto;
import com.smartresume.entity.*;
import com.smartresume.exception.ResourceNotFoundException;
import com.smartresume.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiAssistantService {

    private final ResumeRepository resumeRepository;
    private final ExtractedSkillRepository extractedSkillRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final AuthService authService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.ai.api-key:}")
    private String apiKey;

    @Value("${app.ai.api-url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${app.ai.model:gpt-4o-mini}")
    private String modelName;

    public AiAssistantService(ResumeRepository resumeRepository,
                              ExtractedSkillRepository extractedSkillRepository,
                              InterviewQuestionRepository interviewQuestionRepository,
                              AuthService authService) {
        this.resumeRepository = resumeRepository;
        this.extractedSkillRepository = extractedSkillRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
        this.authService = authService;
    }

    public AiImprovementResponse generateImprovements(AiImprovementRequest request) {
        User user = authService.getAuthenticatedUser();
        Resume resume = resumeRepository.findByIdAndUserId(request.getResumeId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        List<ExtractedSkill> skills = extractedSkillRepository.findAllByResumeId(resume.getId());
        List<String> skillNames = skills.stream().map(ExtractedSkill::getSkillName).collect(Collectors.toList());

        String targetRole = request.getTargetRole() != null && !request.getTargetRole().trim().isEmpty()
                ? request.getTargetRole().trim()
                : (resume.getTargetRole() != null ? resume.getTargetRole() : "Software Engineer");

        // Try LLM API first if key configured
        if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equals("your_llm_api_key_here")) {
            try {
                AiImprovementResponse llmResponse = callLlmApi(resume, skillNames, targetRole, request);
                if (llmResponse != null) {
                    llmResponse.setEngineUsed("AI_LLM (" + modelName + ")");
                    return llmResponse;
                }
            } catch (Exception e) {
                // Fall back to rule-based engine on any network or API key error
            }
        }

        // Rule-Based Intelligent Fallback
        return generateRuleBasedImprovements(resume, skillNames, targetRole, request);
    }

    public ProjectFeedbackDto analyzeProjectDescription(String projectTitle, String description) {
        ProjectFeedbackDto dto = new ProjectFeedbackDto();
        dto.setProjectTitle(projectTitle != null && !projectTitle.trim().isEmpty() ? projectTitle : "Key Project");
        dto.setOriginalDescription(description != null ? description : "");

        if (description == null || description.trim().length() < 20) {
            dto.setScore(30);
            dto.setStarStructureRating("Needs Significant Work");
            dto.getSuggestions().add("Description is too brief. Follow the STAR framework (Situation, Task, Action, Result).");
            dto.setImprovedVersion("Architected and developed " + dto.getProjectTitle() + " using modern software engineering patterns, implementing robust REST APIs and optimizing query throughput by 30%.");
            return dto;
        }

        int score = 50;
        List<String> suggestions = new ArrayList<>();

        // Check Action Verbs
        if (description.toLowerCase().contains("built") || description.toLowerCase().contains("developed")
                || description.toLowerCase().contains("engineered") || description.toLowerCase().contains("implemented")) {
            score += 20;
        } else {
            suggestions.add("Begin bullet points with high-impact power verbs like 'Architected', 'Engineered', or 'Delivered'.");
        }

        // Check Metrics
        if (description.matches(".*\\d+%.*") || description.matches(".*\\d+.*")) {
            score += 20;
        } else {
            suggestions.add("Add measurable business impact (e.g. '% reduction in load times', 'number of concurrent users served', or 'latency improvements').");
        }

        // Check Tech Stack
        if (description.toLowerCase().contains("using") || description.toLowerCase().contains("with") || description.contains("(") || description.contains("Java") || description.contains("React")) {
            score += 10;
        } else {
            suggestions.add("Explicitly name the primary technologies, libraries, and databases leveraged.");
        }

        dto.setScore(Math.min(score, 100));
        dto.setStarStructureRating(score >= 80 ? "Strong STAR Alignment" : (score >= 60 ? "Moderate STAR Structure" : "Weak"));
        dto.setSuggestions(suggestions);

        // Generate enhanced STAR drop-in replacement
        String enhanced = "Engineered and deployed " + dto.getProjectTitle() + " using modern frameworks and best practices. " +
                "Architected core endpoints and integrated caching mechanisms, reducing response latency by 35% while ensuring 99.9% reliability under peak user traffic.";
        dto.setImprovedVersion(enhanced);

        return dto;
    }

    @Transactional
    public List<InterviewQuestionDto> generateInterviewQuestions(Long resumeId) {
        User user = authService.getAuthenticatedUser();
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        List<ExtractedSkill> skills = extractedSkillRepository.findAllByResumeId(resumeId);
        Set<String> skillSet = skills.stream().map(s -> s.getSkillName().toLowerCase()).collect(Collectors.toSet());

        List<InterviewQuestionDto> questions = new ArrayList<>();

        // Generate tailored questions based on actual resume skills
        if (skillSet.contains("java") || skillSet.contains("spring boot")) {
            questions.add(new InterviewQuestionDto(
                    "How does Spring Boot manage dependency injection and bean lifecycles under the hood? Explain @Autowired vs constructor injection.",
                    "TECHNICAL", "Medium",
                    "Constructor injection is favored because it enforces immutability, eases unit testing with Mockito, and prevents NullPointerExceptions before runtime.",
                    "Java / Spring Boot"
            ));
            questions.add(new InterviewQuestionDto(
                    "Explain the difference between optimistic locking and pessimistic locking in JPA / Hibernate.",
                    "TECHNICAL", "Hard",
                    "Optimistic locking uses a @Version column and fails on concurrent modification. Pessimistic locking locks database rows directly with SELECT FOR UPDATE.",
                    "Hibernate / JPA"
            ));
        }

        if (skillSet.contains("react") || skillSet.contains("javascript") || skillSet.contains("typescript")) {
            questions.add(new InterviewQuestionDto(
                    "Explain React's Virtual DOM reconciliation algorithm and why useEffect dependencies must be accurately declared.",
                    "TECHNICAL", "Medium",
                    "React computes diffs in memory and batches DOM updates. Stale closures occur when state variables aren't included in useEffect's dependency array.",
                    "React / JavaScript"
            ));
        }

        if (skillSet.contains("mysql") || skillSet.contains("sql") || skillSet.contains("postgresql")) {
            questions.add(new InterviewQuestionDto(
                    "How do B-Tree indexes work in MySQL, and when would adding an index degrade database performance?",
                    "TECHNICAL", "Medium",
                    "B-Trees offer O(log N) lookup. Indexes add write overhead during INSERT/UPDATE/DELETE and consume disk space on high-churn tables.",
                    "MySQL / SQL"
            ));
        }

        if (skillSet.contains("docker") || skillSet.contains("microservices") || skillSet.contains("aws")) {
            questions.add(new InterviewQuestionDto(
                    "How do you handle distributed transactions and eventual consistency in a microservices architecture?",
                    "SYSTEM_DESIGN", "Hard",
                    "Implement the Saga pattern (orchestration or choreography) using an event bus like Kafka or RabbitMQ, paired with compensating transactions on failure.",
                    "Microservices / Cloud"
            ));
        }

        // Always include behavioral & leadership questions
        questions.add(new InterviewQuestionDto(
                "Describe a situation where a critical production bug occurred. How did you diagnose, resolve, and prevent it from recurring?",
                "BEHAVIORAL", "Medium",
                "Structure using STAR: Situation (production alert), Task (isolate root cause), Action (roll back / apply targeted patch / write regression tests), Result (restored service in 12 mins and added automated CI/CD gating).",
                "Problem Solving / Reliability"
        ));

        questions.add(new InterviewQuestionDto(
                "Tell me about a time you had a technical disagreement with a team member about system architecture. How was it resolved?",
                "BEHAVIORAL", "Medium",
                "Focus on constructive collaboration, objective benchmarks/data rather than opinions, alignment with business goals, and committing once a consensus is reached.",
                "Teamwork & Communication"
        ));

        // Save into repository
        interviewQuestionRepository.deleteAllByResumeId(resume.getId());
        List<InterviewQuestion> entities = new ArrayList<>();
        for (InterviewQuestionDto q : questions) {
            InterviewQuestion entity = new InterviewQuestion();
            entity.setResume(resume);
            entity.setQuestion(q.getQuestion());
            entity.setCategory(q.getCategory());
            entity.setDifficulty(q.getDifficulty());
            entity.setSampleAnswer(q.getSampleAnswer());
            entities.add(entity);
        }
        interviewQuestionRepository.saveAll(entities);

        return questions;
    }

    private AiImprovementResponse generateRuleBasedImprovements(Resume resume, List<String> skills,
                                                               String targetRole, AiImprovementRequest request) {
        AiImprovementResponse response = new AiImprovementResponse();
        response.setResumeId(resume.getId());
        response.setEngineUsed("INTELLIGENT_RULE_BASED (Deterministic Career Engine)");
        response.setOriginalContent(resume.getProfessionalSummary() != null ? resume.getProfessionalSummary() : "No summary provided");

        // 1. Synthesize Targeted Professional Summary
        String primarySkills = skills.stream().limit(5).collect(Collectors.joining(", "));
        double exp = resume.getExperienceYears() != null ? resume.getExperienceYears() : 2.0;
        String expString = exp >= 1.0 ? String.format("%.0f+ years", Math.max(1, Math.round(exp))) : "hands-on academic and project";

        String tailoredSummary = "Dynamic and results-oriented " + targetRole + " with " + expString + " of experience delivering scalable, high-quality software solutions. " +
                "Proven expertise across " + (primarySkills.isEmpty() ? "modern full-stack engineering" : primarySkills) + ". " +
                "Skilled in architecting robust RESTful APIs, optimizing system throughput, and collaborating in agile environments to ship mission-critical applications.";
        response.setSuggestedSummary(tailoredSummary);

        // 2. High-Impact Action Bullet Points
        List<String> bullets = new ArrayList<>();
        bullets.add("Architected and deployed enterprise microservices utilizing " + (skills.contains("Spring Boot") ? "Spring Boot and Docker" : "modern backend architectures") + ", handling high concurrency with 99.9% availability.");
        bullets.add("Refactored core database queries and introduced optimized caching layers, cutting average API response latency by 35%.");
        bullets.add("Automated continuous integration and deployment (CI/CD) pipelines, shortening production release cycles from weeks to hours.");
        bullets.add("Collaborated cross-functionally with product managers and QA engineers to deliver sprint objectives ahead of schedule.");
        response.setImprovedBulletPoints(bullets);

        // 3. Power Action Verbs
        response.setPowerActionVerbs(Arrays.asList(
                "Architected (instead of 'designed')",
                "Engineered (instead of 'worked on')",
                "Spearheaded (instead of 'helped with')",
                "Accelerated (instead of 'made faster')",
                "Automated (instead of 'manually did')",
                "Orchestrated (instead of 'organized')"
        ));

        // 4. Grammar and Style Tips
        response.setGrammarAndStyleTips(Arrays.asList(
                "Use past tense verbs ('Engineered', 'Designed') for completed roles; use present tense only for current positions.",
                "Ensure every bullet point includes a tangible outcome or metric (e.g., %, ms, number of users).",
                "Eliminate first-person pronouns ('I', 'me', 'my') to preserve formal third-person resume convention.",
                "Consistently format dates across all employment and education blocks (e.g. 'MMM YYYY – Present')."
        ));

        // 5. Weak Section Fixes
        List<String> fixes = new ArrayList<>();
        if (resume.getProfessionalSummary() == null || resume.getProfessionalSummary().trim().isEmpty()) {
            fixes.add("Summary Section: Your resume is missing a 3-sentence summary. Copy our generated summary above.");
        }
        if (skills.size() < 8) {
            fixes.add("Skills Categorization: Group your skills into Languages, Frameworks, Databases, and Tools for rapid ATS parsing.");
        }
        fixes.add("Quantification Check: Add at least 3 bullet points with quantifiable figures (e.g. 'reduced latency by 40%').");
        response.setWeakSectionFixes(fixes);

        return response;
    }

    private AiImprovementResponse callLlmApi(Resume resume, List<String> skills,
                                            String targetRole, AiImprovementRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String systemPrompt = "You are a professional ATS resume strategist. " +
                "Rules: 1. NEVER invent or fabricate degrees, qualifications, jobs, or certifications. " +
                "2. Base all suggestions solely on candidate's existing background. " +
                "3. Return a clean JSON object with keys: suggestedSummary, improvedBulletPoints (array), powerActionVerbs (array), grammarAndStyleTips (array), weakSectionFixes (array).";

        String userPrompt = "Candidate Target Role: " + targetRole + "\n" +
                "Existing Skills: " + String.join(", ", skills) + "\n" +
                "Existing Summary: " + (resume.getProfessionalSummary() != null ? resume.getProfessionalSummary() : "None") + "\n" +
                "Experience Years: " + resume.getExperienceYears() + "\n" +
                "Raw Resume Snippet: " + (resume.getExtractedText() != null ? resume.getExtractedText().substring(0, Math.min(resume.getExtractedText().length(), 1500)) : "");

        Map<String, Object> body = new HashMap<>();
        body.put("model", modelName);
        body.put("messages", Arrays.asList(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        ));
        body.put("temperature", 0.4);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                JsonNode root = objectMapper.readTree(response.getBody());
                String content = root.path("choices").get(0).path("message").path("content").asText();

                // Clean markdown code fence if present
                if (content.contains("```json")) {
                    content = content.substring(content.indexOf("```json") + 7);
                    content = content.substring(0, content.indexOf("```"));
                } else if (content.contains("```")) {
                    content = content.substring(content.indexOf("```") + 3);
                    content = content.substring(0, content.indexOf("```"));
                }

                JsonNode parsed = objectMapper.readTree(content.trim());
                AiImprovementResponse res = new AiImprovementResponse();
                res.setResumeId(resume.getId());
                res.setOriginalContent(resume.getProfessionalSummary());
                res.setSuggestedSummary(parsed.path("suggestedSummary").asText());

                if (parsed.has("improvedBulletPoints")) {
                    List<String> bullets = new ArrayList<>();
                    parsed.path("improvedBulletPoints").forEach(b -> bullets.add(b.asText()));
                    res.setImprovedBulletPoints(bullets);
                }
                if (parsed.has("powerActionVerbs")) {
                    List<String> verbs = new ArrayList<>();
                    parsed.path("powerActionVerbs").forEach(v -> verbs.add(v.asText()));
                    res.setPowerActionVerbs(verbs);
                }
                if (parsed.has("grammarAndStyleTips")) {
                    List<String> tips = new ArrayList<>();
                    parsed.path("grammarAndStyleTips").forEach(t -> tips.add(t.asText()));
                    res.setGrammarAndStyleTips(tips);
                }
                if (parsed.has("weakSectionFixes")) {
                    List<String> fixes = new ArrayList<>();
                    parsed.path("weakSectionFixes").forEach(f -> fixes.add(f.asText()));
                    res.setWeakSectionFixes(fixes);
                }
                return res;
            } catch (Exception ex) {
                // Return null to allow fallback to rule-based engine
                return null;
            }
        }
        return null;
    }
}
