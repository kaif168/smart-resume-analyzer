package com.smartresume.service;

import com.smartresume.dto.resume.ParsedResumeDto;
import com.smartresume.entity.SkillCategory;
import com.smartresume.exception.BadRequestException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.Year;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumeParserService {

    // Regex Patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}"
    );

    private static final Pattern YEAR_RANGE_PATTERN = Pattern.compile(
            "\\b(20\\d{2}|19\\d{2})\\s*(?:–|-|to)\\s*(Present|Current|20\\d{2})\\b",
            Pattern.CASE_INSENSITIVE
    );

    // Dictionaries for Skills
    private static final Map<String, SkillCategory> SKILL_DICTIONARY = new LinkedHashMap<>();

    static {
        // Languages & Core
        SKILL_DICTIONARY.put("Java", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("Python", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("JavaScript", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("TypeScript", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("C++", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("C#", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("Go", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("Golang", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("Rust", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("Kotlin", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("Swift", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("PHP", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("Ruby", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("SQL", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("HTML", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("HTML5", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("CSS", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("CSS3", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("Data Structures", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("Algorithms", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("Object-Oriented Programming", SkillCategory.TECHNICAL);
        SKILL_DICTIONARY.put("OOP", SkillCategory.TECHNICAL);

        // Frameworks
        SKILL_DICTIONARY.put("Spring Boot", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("Spring MVC", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("Spring Security", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("Spring Data JPA", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("Hibernate", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("React", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("React.js", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("Angular", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("Vue", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("Vue.js", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("Next.js", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("Node.js", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("Express.js", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("Express", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("Django", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("Flask", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("FastAPI", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put(".NET", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("Tailwind CSS", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("Bootstrap", SkillCategory.FRAMEWORK);
        SKILL_DICTIONARY.put("Redux", SkillCategory.FRAMEWORK);

        // Databases
        SKILL_DICTIONARY.put("MySQL", SkillCategory.DATABASE);
        SKILL_DICTIONARY.put("PostgreSQL", SkillCategory.DATABASE);
        SKILL_DICTIONARY.put("MongoDB", SkillCategory.DATABASE);
        SKILL_DICTIONARY.put("Redis", SkillCategory.DATABASE);
        SKILL_DICTIONARY.put("Oracle", SkillCategory.DATABASE);
        SKILL_DICTIONARY.put("SQL Server", SkillCategory.DATABASE);
        SKILL_DICTIONARY.put("SQLite", SkillCategory.DATABASE);
        SKILL_DICTIONARY.put("Cassandra", SkillCategory.DATABASE);
        SKILL_DICTIONARY.put("Elasticsearch", SkillCategory.DATABASE);

        // Cloud & DevOps
        SKILL_DICTIONARY.put("Docker", SkillCategory.CLOUD);
        SKILL_DICTIONARY.put("Kubernetes", SkillCategory.CLOUD);
        SKILL_DICTIONARY.put("AWS", SkillCategory.CLOUD);
        SKILL_DICTIONARY.put("Azure", SkillCategory.CLOUD);
        SKILL_DICTIONARY.put("Google Cloud", SkillCategory.CLOUD);
        SKILL_DICTIONARY.put("GCP", SkillCategory.CLOUD);
        SKILL_DICTIONARY.put("CI/CD", SkillCategory.CLOUD);
        SKILL_DICTIONARY.put("Jenkins", SkillCategory.CLOUD);
        SKILL_DICTIONARY.put("Terraform", SkillCategory.CLOUD);
        SKILL_DICTIONARY.put("Kafka", SkillCategory.CLOUD);
        SKILL_DICTIONARY.put("RabbitMQ", SkillCategory.CLOUD);
        SKILL_DICTIONARY.put("Microservices", SkillCategory.CLOUD);

        // Tools
        SKILL_DICTIONARY.put("Git", SkillCategory.TOOL);
        SKILL_DICTIONARY.put("GitHub", SkillCategory.TOOL);
        SKILL_DICTIONARY.put("GitLab", SkillCategory.TOOL);
        SKILL_DICTIONARY.put("Maven", SkillCategory.TOOL);
        SKILL_DICTIONARY.put("Gradle", SkillCategory.TOOL);
        SKILL_DICTIONARY.put("Postman", SkillCategory.TOOL);
        SKILL_DICTIONARY.put("REST API", SkillCategory.TOOL);
        SKILL_DICTIONARY.put("RESTful APIs", SkillCategory.TOOL);
        SKILL_DICTIONARY.put("GraphQL", SkillCategory.TOOL);
        SKILL_DICTIONARY.put("JIRA", SkillCategory.TOOL);
        SKILL_DICTIONARY.put("Linux", SkillCategory.TOOL);
        SKILL_DICTIONARY.put("Bash", SkillCategory.TOOL);
        SKILL_DICTIONARY.put("JUnit", SkillCategory.TOOL);
        SKILL_DICTIONARY.put("Mockito", SkillCategory.TOOL);

        // Soft Skills
        SKILL_DICTIONARY.put("Problem Solving", SkillCategory.SOFT);
        SKILL_DICTIONARY.put("Team Leadership", SkillCategory.SOFT);
        SKILL_DICTIONARY.put("Leadership", SkillCategory.SOFT);
        SKILL_DICTIONARY.put("Communication", SkillCategory.SOFT);
        SKILL_DICTIONARY.put("Teamwork", SkillCategory.SOFT);
        SKILL_DICTIONARY.put("Time Management", SkillCategory.SOFT);
        SKILL_DICTIONARY.put("Critical Thinking", SkillCategory.SOFT);
        SKILL_DICTIONARY.put("Mentoring", SkillCategory.SOFT);
        SKILL_DICTIONARY.put("Adaptability", SkillCategory.SOFT);
        SKILL_DICTIONARY.put("Collaboration", SkillCategory.SOFT);
        SKILL_DICTIONARY.put("Agile", SkillCategory.SOFT);
        SKILL_DICTIONARY.put("Scrum", SkillCategory.SOFT);
    }

    public String extractTextFromFile(File file, String extension) {
        if (!file.exists()) {
            throw new BadRequestException("Uploaded document file does not exist.");
        }

        try {
            if ("pdf".equalsIgnoreCase(extension)) {
                try (PDDocument document = Loader.loadPDF(file)) {
                    if (document.isEncrypted()) {
                        throw new BadRequestException("PDF is password protected. Please upload an unprotected file.");
                    }
                    PDFTextStripper stripper = new PDFTextStripper();
                    stripper.setSortByPosition(true);
                    String text = stripper.getText(document);
                    if (text == null || text.trim().isEmpty()) {
                        throw new BadRequestException("Document contains no readable text or is an image-only scan.");
                    }
                    return text.trim();
                }
            } else if ("docx".equalsIgnoreCase(extension)) {
                try (InputStream is = new FileInputStream(file);
                     XWPFDocument docx = new XWPFDocument(is);
                     XWPFWordExtractor extractor = new XWPFWordExtractor(docx)) {
                    String text = extractor.getText();
                    if (text == null || text.trim().isEmpty()) {
                        throw new BadRequestException("DOCX document is empty or unreadable.");
                    }
                    return text.trim();
                }
            } else {
                // Fallback for .txt or other text-based uploads
                return Files.readString(file.toPath());
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Failed to parse document: " + e.getMessage() + ". Please verify file integrity.");
        }
    }

    public ParsedResumeDto parseStructuredResume(String rawText) {
        ParsedResumeDto dto = new ParsedResumeDto();
        if (rawText == null || rawText.trim().isEmpty()) {
            dto.getWarnings().add("Resume is completely empty.");
            return dto;
        }

        String[] lines = rawText.split("\\r?\\n");

        // 1. Extract Email
        Matcher emailMatcher = EMAIL_PATTERN.matcher(rawText);
        if (emailMatcher.find()) {
            dto.setEmail(emailMatcher.group(1));
        } else {
            dto.getWarnings().add("No professional email address detected.");
            dto.getMissingSections().add("Email Address");
        }

        // 2. Extract Phone
        Matcher phoneMatcher = PHONE_PATTERN.matcher(rawText);
        if (phoneMatcher.find()) {
            dto.setPhone(phoneMatcher.group(0).trim());
        } else {
            dto.getWarnings().add("No telephone contact number found.");
            dto.getMissingSections().add("Phone Number");
        }

        // 3. Extract Candidate Name
        String name = extractCandidateName(lines, dto.getEmail());
        dto.setCandidateName(name);

        // 4. Extract Location
        String location = extractLocation(rawText, lines);
        dto.setLocation(location);

        // 5. Extract Professional Summary
        String summary = extractSectionContent(lines, Arrays.asList(
                "PROFESSIONAL SUMMARY", "SUMMARY", "CAREER OBJECTIVE", "OBJECTIVE", "ABOUT ME", "PROFILE"
        ), 10);
        dto.setSummary(summary);
        if (summary == null || summary.trim().isEmpty()) {
            dto.getMissingSections().add("Professional Summary");
            dto.getWarnings().add("Missing Professional Summary section to highlight your key qualifications.");
        }

        // 6. Extract Skills
        extractSkills(rawText, dto);

        // 7. Extract Work Experience
        List<String> experienceBlocks = extractBulletPoints(lines, Arrays.asList(
                "WORK EXPERIENCE", "EXPERIENCE", "EMPLOYMENT HISTORY", "PROFESSIONAL EXPERIENCE"
        ));
        dto.setWorkExperience(experienceBlocks);
        if (experienceBlocks.isEmpty()) {
            dto.getMissingSections().add("Work Experience");
            dto.getWarnings().add("No dedicated Work Experience section found.");
        }

        // 8. Extract Education
        List<String> educationBlocks = extractBulletPoints(lines, Arrays.asList(
                "EDUCATION", "ACADEMIC BACKGROUND", "QUALIFICATIONS", "ACADEMICS"
        ));
        dto.setEducation(educationBlocks);
        if (educationBlocks.isEmpty()) {
            dto.getMissingSections().add("Education");
            dto.getWarnings().add("No Education section detected.");
        }

        // 9. Extract Projects
        List<String> projectBlocks = extractBulletPoints(lines, Arrays.asList(
                "PROJECTS", "KEY PROJECTS", "ACADEMIC PROJECTS", "PERSONAL PROJECTS"
        ));
        dto.setProjects(projectBlocks);

        // 10. Extract Certifications
        List<String> certBlocks = extractBulletPoints(lines, Arrays.asList(
                "CERTIFICATIONS", "CERTIFICATES", "LICENSES & CERTIFICATIONS"
        ));
        dto.setCertifications(certBlocks);

        // 11. Extract Languages
        List<String> langBlocks = extractBulletPoints(lines, Arrays.asList(
                "LANGUAGES", "LANGUAGE PROFICIENCY"
        ));
        dto.setLanguages(langBlocks);

        // 12. Calculate approximate experience years
        double expYears = calculateExperienceYears(rawText);
        dto.setExperienceYears(expYears);

        return dto;
    }

    private String extractCandidateName(String[] lines, String email) {
        for (int i = 0; i < Math.min(lines.length, 6); i++) {
            String line = lines[i].trim();
            if (line.isEmpty() || line.length() < 3 || line.length() > 60) continue;
            if (line.contains("@") || line.contains("http") || line.contains("www.") || line.contains("linkedin.com")) continue;
            if (line.toLowerCase().startsWith("resume") || line.toLowerCase().startsWith("curriculum vitae")) continue;
            // Matches name-like line
            if (line.matches("^[A-Za-z\\s.'-]+$")) {
                return line;
            }
        }
        if (email != null && email.contains("@")) {
            String prefix = email.substring(0, email.indexOf("@"));
            return prefix.replace(".", " ").replace("_", " ").toUpperCase();
        }
        return "Candidate";
    }

    private String extractLocation(String text, String[] lines) {
        Pattern locPattern = Pattern.compile("([A-Z][a-zA-Z\\s]+,\\s*[A-Z]{2}|[A-Z][a-zA-Z\\s]+,\\s*[A-Za-z\\s]+(?:USA|India|UK|Canada|Germany))");
        Matcher m = locPattern.matcher(text);
        if (m.find()) {
            return m.group(1).trim();
        }
        for (int i = 0; i < Math.min(lines.length, 4); i++) {
            if (lines[i].contains(",")) {
                String[] parts = lines[i].split("\\|");
                for (String part : parts) {
                    if (part.contains(",") && !part.contains("@") && !part.contains("http")) {
                        return part.trim();
                    }
                }
            }
        }
        return "Not Specified";
    }

    private String extractSectionContent(String[] lines, List<String> headerTriggers, int maxLines) {
        boolean inSection = false;
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            if (isHeaderMatch(trimmed, headerTriggers)) {
                inSection = true;
                continue;
            }

            if (inSection) {
                // If encountering another uppercase major header, stop
                if (isMajorHeader(trimmed) && !isHeaderMatch(trimmed, headerTriggers)) {
                    break;
                }
                sb.append(trimmed).append(" ");
                count++;
                if (count >= maxLines) break;
            }
        }
        return sb.toString().trim();
    }

    private List<String> extractBulletPoints(String[] lines, List<String> headerTriggers) {
        List<String> results = new ArrayList<>();
        boolean inSection = false;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            if (isHeaderMatch(trimmed, headerTriggers)) {
                inSection = true;
                continue;
            }

            if (inSection) {
                if (isMajorHeader(trimmed) && !isHeaderMatch(trimmed, headerTriggers)) {
                    break;
                }
                results.add(trimmed);
            }
        }
        return results;
    }

    private boolean isHeaderMatch(String line, List<String> triggers) {
        String clean = line.replaceAll("[^A-Za-z\\s]", "").trim().toUpperCase();
        for (String trigger : triggers) {
            if (clean.equals(trigger) || clean.startsWith(trigger)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMajorHeader(String line) {
        String clean = line.replaceAll("[^A-Za-z\\s]", "").trim();
        List<String> majors = Arrays.asList(
                "SUMMARY", "PROFESSIONAL SUMMARY", "EXPERIENCE", "WORK EXPERIENCE",
                "EDUCATION", "SKILLS", "TECHNICAL SKILLS", "PROJECTS", "CERTIFICATIONS",
                "LANGUAGES", "ACHIEVEMENTS", "PUBLICATIONS"
        );
        return majors.contains(clean.toUpperCase());
    }

    private void extractSkills(String text, ParsedResumeDto dto) {
        Set<String> techSet = new LinkedHashSet<>();
        Set<String> softSet = new LinkedHashSet<>();

        for (Map.Entry<String, SkillCategory> entry : SKILL_DICTIONARY.entrySet()) {
            String skill = entry.getKey();
            SkillCategory category = entry.getValue();

            // Word-boundary case-insensitive search
            String regex = "\\b" + Pattern.quote(skill) + "\\b";
            if (Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text).find()) {
                if (category == SkillCategory.SOFT) {
                    softSet.add(skill);
                } else {
                    techSet.add(skill);
                }
            }
        }

        dto.getTechnicalSkills().addAll(techSet);
        dto.getSoftSkills().addAll(softSet);

        if (dto.getTechnicalSkills().isEmpty()) {
            dto.getMissingSections().add("Technical Skills");
            dto.getWarnings().add("Low technical keyword density detected. Add core technologies and tools.");
        }
    }

    private double calculateExperienceYears(String text) {
        Matcher matcher = YEAR_RANGE_PATTERN.matcher(text);
        int currentYear = Year.now().getValue();
        double totalYears = 0.0;

        while (matcher.find()) {
            try {
                int startYear = Integer.parseInt(matcher.group(1));
                int endYear;
                String endGroup = matcher.group(2);
                if ("Present".equalsIgnoreCase(endGroup) || "Current".equalsIgnoreCase(endGroup)) {
                    endYear = currentYear;
                } else {
                    endYear = Integer.parseInt(endGroup);
                }
                if (endYear >= startYear && (endYear - startYear) <= 25) {
                    totalYears += (endYear - startYear);
                }
            } catch (Exception ignored) {}
        }
        return Math.min(totalYears, 20.0);
    }

    public Map<String, SkillCategory> getSkillDictionary() {
        return SKILL_DICTIONARY;
    }
}
