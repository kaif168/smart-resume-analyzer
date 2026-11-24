package com.smartresume.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resume_analysis")
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "overall_score", nullable = false)
    private int overallScore; // 0 - 100

    @Column(name = "contact_score", nullable = false)
    private int contactScore; // Max 10

    @Column(name = "structure_score", nullable = false)
    private int structureScore; // Max 15

    @Column(name = "skills_score", nullable = false)
    private int skillsScore; // Max 20

    @Column(name = "keyword_score", nullable = false)
    private int keywordScore; // Max 20

    @Column(name = "experience_score", nullable = false)
    private int experienceScore; // Max 15

    @Column(name = "education_score", nullable = false)
    private int educationScore; // Max 10

    @Column(name = "readability_score", nullable = false)
    private int readabilityScore; // Max 10

    @Column(name = "strengths_json", columnDefinition = "TEXT")
    private String strengthsJson;

    @Column(name = "weaknesses_json", columnDefinition = "TEXT")
    private String weaknessesJson;

    @Column(name = "actionable_suggestions_json", columnDefinition = "TEXT")
    private String actionableSuggestionsJson;

    @Column(name = "missing_sections_json", columnDefinition = "TEXT")
    private String missingSectionsJson;

    @Column(name = "ats_breakdown_json", columnDefinition = "TEXT")
    private String atsBreakdownJson;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ResumeAnalysis() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }

    public int getContactScore() { return contactScore; }
    public void setContactScore(int contactScore) { this.contactScore = contactScore; }

    public int getStructureScore() { return structureScore; }
    public void setStructureScore(int structureScore) { this.structureScore = structureScore; }

    public int getSkillsScore() { return skillsScore; }
    public void setSkillsScore(int skillsScore) { this.skillsScore = skillsScore; }

    public int getKeywordScore() { return keywordScore; }
    public void setKeywordScore(int keywordScore) { this.keywordScore = keywordScore; }

    public int getExperienceScore() { return experienceScore; }
    public void setExperienceScore(int experienceScore) { this.experienceScore = experienceScore; }

    public int getEducationScore() { return educationScore; }
    public void setEducationScore(int educationScore) { this.educationScore = educationScore; }

    public int getReadabilityScore() { return readabilityScore; }
    public void setReadabilityScore(int readabilityScore) { this.readabilityScore = readabilityScore; }

    public String getStrengthsJson() { return strengthsJson; }
    public void setStrengthsJson(String strengthsJson) { this.strengthsJson = strengthsJson; }

    public String getWeaknessesJson() { return weaknessesJson; }
    public void setWeaknessesJson(String weaknessesJson) { this.weaknessesJson = weaknessesJson; }

    public String getActionableSuggestionsJson() { return actionableSuggestionsJson; }
    public void setActionableSuggestionsJson(String actionableSuggestionsJson) { this.actionableSuggestionsJson = actionableSuggestionsJson; }

    public String getMissingSectionsJson() { return missingSectionsJson; }
    public void setMissingSectionsJson(String missingSectionsJson) { this.missingSectionsJson = missingSectionsJson; }

    public String getAtsBreakdownJson() { return atsBreakdownJson; }
    public void setAtsBreakdownJson(String atsBreakdownJson) { this.atsBreakdownJson = atsBreakdownJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
