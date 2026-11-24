package com.smartresume.dto.jobmatching;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobMatchResponse {
    private Long id;
    private Long resumeId;
    private String targetRole;
    private int matchPercentage; // 0 - 100%
    private String matchVerdict; // Strong Match, Moderate Match, Low Match
    private List<String> matchedSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();
    private List<String> relevantKeywords = new ArrayList<>();
    private List<String> requiredQualifications = new ArrayList<>();
    private String experienceAlignment;
    private List<String> suggestedImprovements = new ArrayList<>();
    private String note = "A missing keyword indicates an opportunity to tailor your resume, not proof that you lack the skill.";
    private LocalDateTime createdAt;

    public JobMatchResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public int getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(int matchPercentage) {
        this.matchPercentage = matchPercentage;
        if (matchPercentage >= 75) {
            this.matchVerdict = "Strong Match";
        } else if (matchPercentage >= 50) {
            this.matchVerdict = "Moderate Match";
        } else {
            this.matchVerdict = "Low Match";
        }
    }

    public String getMatchVerdict() { return matchVerdict; }
    public void setMatchVerdict(String matchVerdict) { this.matchVerdict = matchVerdict; }

    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }

    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }

    public List<String> getRelevantKeywords() { return relevantKeywords; }
    public void setRelevantKeywords(List<String> relevantKeywords) { this.relevantKeywords = relevantKeywords; }

    public List<String> getRequiredQualifications() { return requiredQualifications; }
    public void setRequiredQualifications(List<String> requiredQualifications) { this.requiredQualifications = requiredQualifications; }

    public String getExperienceAlignment() { return experienceAlignment; }
    public void setExperienceAlignment(String experienceAlignment) { this.experienceAlignment = experienceAlignment; }

    public List<String> getSuggestedImprovements() { return suggestedImprovements; }
    public void setSuggestedImprovements(List<String> suggestedImprovements) { this.suggestedImprovements = suggestedImprovements; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
