package com.smartresume.dto.analysis;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnalysisResponse {
    private Long id;
    private Long resumeId;
    private String candidateName;
    private String targetRole;
    private int overallScore; // 0 - 100
    private String scoreRating; // Excellent (85+), Good (70-84), Average (50-69), Poor (<50)
    private List<CategoryScoreDto> categoryScores = new ArrayList<>();
    private List<String> strengths = new ArrayList<>();
    private List<String> weaknesses = new ArrayList<>();
    private List<String> actionableSuggestions = new ArrayList<>();
    private List<String> missingSections = new ArrayList<>();
    private String disclaimer = "This score is an estimated compatibility score based on modern ATS algorithmic criteria, not an actual proprietary score from an employer's ATS system.";
    private LocalDateTime createdAt;

    public AnalysisResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
        if (overallScore >= 85) {
            this.scoreRating = "Excellent";
        } else if (overallScore >= 70) {
            this.scoreRating = "Good";
        } else if (overallScore >= 50) {
            this.scoreRating = "Average";
        } else {
            this.scoreRating = "Needs Significant Improvement";
        }
    }

    public String getScoreRating() { return scoreRating; }
    public void setScoreRating(String scoreRating) { this.scoreRating = scoreRating; }

    public List<CategoryScoreDto> getCategoryScores() { return categoryScores; }
    public void setCategoryScores(List<CategoryScoreDto> categoryScores) { this.categoryScores = categoryScores; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getWeaknesses() { return weaknesses; }
    public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }

    public List<String> getActionableSuggestions() { return actionableSuggestions; }
    public void setActionableSuggestions(List<String> actionableSuggestions) { this.actionableSuggestions = actionableSuggestions; }

    public List<String> getMissingSections() { return missingSections; }
    public void setMissingSections(List<String> missingSections) { this.missingSections = missingSections; }

    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
