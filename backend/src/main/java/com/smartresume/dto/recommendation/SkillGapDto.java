package com.smartresume.dto.recommendation;

import java.util.ArrayList;
import java.util.List;

public class SkillGapDto {
    private String targetRole;
    private int coveragePercentage;
    private List<String> existingSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();
    private List<String> recommendedSkillsToLearn = new ArrayList<>();
    private List<String> suggestedLearningSequence = new ArrayList<>();

    public SkillGapDto() {}

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public int getCoveragePercentage() { return coveragePercentage; }
    public void setCoveragePercentage(int coveragePercentage) { this.coveragePercentage = coveragePercentage; }

    public List<String> getExistingSkills() { return existingSkills; }
    public void setExistingSkills(List<String> existingSkills) { this.existingSkills = existingSkills; }

    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }

    public List<String> getRecommendedSkillsToLearn() { return recommendedSkillsToLearn; }
    public void setRecommendedSkillsToLearn(List<String> recommendedSkillsToLearn) { this.recommendedSkillsToLearn = recommendedSkillsToLearn; }

    public List<String> getSuggestedLearningSequence() { return suggestedLearningSequence; }
    public void setSuggestedLearningSequence(List<String> suggestedLearningSequence) { this.suggestedLearningSequence = suggestedLearningSequence; }
}
