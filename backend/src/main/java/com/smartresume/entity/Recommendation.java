package com.smartresume.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(name = "recommended_role", nullable = false, length = 150)
    private String recommendedRole;

    @Column(name = "compatibility_percentage", nullable = false)
    private int compatibilityPercentage;

    @Column(name = "matching_skills_json", columnDefinition = "TEXT")
    private String matchingSkillsJson;

    @Column(name = "missing_skills_json", columnDefinition = "TEXT")
    private String missingSkillsJson;

    @Column(name = "learning_path_json", columnDefinition = "TEXT")
    private String learningPathJson;

    @Column(name = "growth_potential", length = 50)
    private String growthPotential = "High";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Recommendation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }

    public String getRecommendedRole() { return recommendedRole; }
    public void setRecommendedRole(String recommendedRole) { this.recommendedRole = recommendedRole; }

    public int getCompatibilityPercentage() { return compatibilityPercentage; }
    public void setCompatibilityPercentage(int compatibilityPercentage) { this.compatibilityPercentage = compatibilityPercentage; }

    public String getMatchingSkillsJson() { return matchingSkillsJson; }
    public void setMatchingSkillsJson(String matchingSkillsJson) { this.matchingSkillsJson = matchingSkillsJson; }

    public String getMissingSkillsJson() { return missingSkillsJson; }
    public void setMissingSkillsJson(String missingSkillsJson) { this.missingSkillsJson = missingSkillsJson; }

    public String getLearningPathJson() { return learningPathJson; }
    public void setLearningPathJson(String learningPathJson) { this.learningPathJson = learningPathJson; }

    public String getGrowthPotential() { return growthPotential; }
    public void setGrowthPotential(String growthPotential) { this.growthPotential = growthPotential; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
