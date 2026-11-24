package com.smartresume.dto.recommendation;

import java.util.ArrayList;
import java.util.List;

public class CareerRecommendationDto {
    private String jobTitle;
    private int compatibilityPercentage;
    private List<String> matchingSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();
    private List<String> suggestedLearningAreas = new ArrayList<>();
    private String growthPotential;
    private String reason;

    public CareerRecommendationDto() {}

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public int getCompatibilityPercentage() { return compatibilityPercentage; }
    public void setCompatibilityPercentage(int compatibilityPercentage) { this.compatibilityPercentage = compatibilityPercentage; }

    public List<String> getMatchingSkills() { return matchingSkills; }
    public void setMatchingSkills(List<String> matchingSkills) { this.matchingSkills = matchingSkills; }

    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }

    public List<String> getSuggestedLearningAreas() { return suggestedLearningAreas; }
    public void setSuggestedLearningAreas(List<String> suggestedLearningAreas) { this.suggestedLearningAreas = suggestedLearningAreas; }

    public String getGrowthPotential() { return growthPotential; }
    public void setGrowthPotential(String growthPotential) { this.growthPotential = growthPotential; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
