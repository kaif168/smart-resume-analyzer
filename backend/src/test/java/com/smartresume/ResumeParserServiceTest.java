package com.smartresume;

import com.smartresume.dto.resume.ParsedResumeDto;
import com.smartresume.service.ResumeParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResumeParserServiceTest {

    private ResumeParserService parserService;

    @BeforeEach
    void setUp() {
        parserService = new ResumeParserService();
    }

    @Test
    void testParseStructuredResume_ExtractsContactAndSkills() {
        String sampleText = "JOHN DOE\n" +
                "john.doe@example.com | (555) 019-2834 | San Francisco, CA\n\n" +
                "PROFESSIONAL SUMMARY\n" +
                "Results-driven Java Developer with 4 years of experience building Spring Boot microservices.\n\n" +
                "TECHNICAL SKILLS\n" +
                "Java, Spring Boot, React, MySQL, Docker, Git, REST API\n\n" +
                "WORK EXPERIENCE\n" +
                "Software Engineer at TechCorp\n" +
                "2021 – Present\n" +
                "- Architected and deployed 5 microservices reducing latency by 40%.\n\n" +
                "EDUCATION\n" +
                "Bachelor of Science in Computer Science, University of California (2020)\n";

        ParsedResumeDto parsed = parserService.parseStructuredResume(sampleText);

        assertNotNull(parsed);
        assertEquals("john.doe@example.com", parsed.getEmail());
        assertTrue(parsed.getPhone().contains("555"));
        assertTrue(parsed.getTechnicalSkills().contains("Java"));
        assertTrue(parsed.getTechnicalSkills().contains("Spring Boot"));
        assertTrue(parsed.getTechnicalSkills().contains("MySQL"));
        assertTrue(parsed.getTechnicalSkills().contains("Docker"));
        assertTrue(parsed.getWorkExperience().size() > 0);
        assertTrue(parsed.getEducation().size() > 0);
        assertTrue(parsed.getMissingSections().isEmpty());
    }

    @Test
    void testParseStructuredResume_DetectsMissingSections() {
        String incompleteText = "Alex\nJava, HTML\n";

        ParsedResumeDto parsed = parserService.parseStructuredResume(incompleteText);

        assertNotNull(parsed);
        assertTrue(parsed.getMissingSections().contains("Email Address"));
        assertTrue(parsed.getMissingSections().contains("Phone Number"));
        assertTrue(parsed.getMissingSections().contains("Professional Summary"));
        assertTrue(parsed.getWarnings().size() > 0);
    }
}
