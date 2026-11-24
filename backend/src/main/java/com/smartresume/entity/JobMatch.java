package com.smartresume.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_matches")
public class JobMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(name = "job_description_id")
    private Long jobDescriptionId;

    @Column(name = "target_role", length = 150)
    private String targetRole;

    @Column(name = "match_percentage", nullable = false)
    private int matchPercentage;

    @Column(name = "matched_skills_json", columnDefinition = "TEXT")
    private String matchedSkillsJson;

    @Column(name = "missing_skills_json", columnDefinition = "TEXT")
    private String missingSkillsJson;

    @Column(name = "keyword_score", nullable = false)
    private int keywordScore;

    @Column(name = "experience_alignment", length = 100)
    private String experienceAlignment;

    @Column(name = "improvement_tips_json", columnDefinition = "TEXT")
    private String improvementTipsJson;

    @Column(name = "qualifications_json", columnDefinition = "TEXT")
    private String qualificationsJson;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public JobMatch() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }

    public Long getJobDescriptionId() { return jobDescriptionId; }
    public void setJobDescriptionId(Long jobDescriptionId) { this.jobDescriptionId = jobDescriptionId; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public int getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(int matchPercentage) { this.matchPercentage = matchPercentage; }

    public String getMatchedSkillsJson() { return matchedSkillsJson; }
    public void setMatchedSkillsJson(String matchedSkillsJson) { this.matchedSkillsJson = matchedSkillsJson; }

    public String getMissingSkillsJson() { return missingSkillsJson; }
    public void setMissingSkillsJson(String missingSkillsJson) { this.missingSkillsJson = missingSkillsJson; }

    public int getKeywordScore() { return keywordScore; }
    public void setKeywordScore(int keywordScore) { this.keywordScore = keywordScore; }

    public String getExperienceAlignment() { return experienceAlignment; }
    public void setExperienceAlignment(String experienceAlignment) { this.experienceAlignment = experienceAlignment; }

    public String getImprovementTipsJson() { return improvementTipsJson; }
    public void setImprovementTipsJson(String improvementTipsJson) { this.improvementTipsJson = improvementTipsJson; }

    public String getQualificationsJson() { return qualificationsJson; }
    public void setQualificationsJson(String qualificationsJson) { this.qualificationsJson = qualificationsJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
