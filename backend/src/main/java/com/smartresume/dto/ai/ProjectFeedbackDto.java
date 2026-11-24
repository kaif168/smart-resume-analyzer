package com.smartresume.dto.ai;

import java.util.ArrayList;
import java.util.List;

public class ProjectFeedbackDto {
    private String projectTitle;
    private String originalDescription;
    private int score; // 0 - 100
    private String starStructureRating; // Weak, Fair, Strong, Excellent
    private List<String> suggestions = new ArrayList<>();
    private String improvedVersion;

    public ProjectFeedbackDto() {}

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public String getOriginalDescription() { return originalDescription; }
    public void setOriginalDescription(String originalDescription) { this.originalDescription = originalDescription; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getStarStructureRating() { return starStructureRating; }
    public void setStarStructureRating(String starStructureRating) { this.starStructureRating = starStructureRating; }

    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }

    public String getImprovedVersion() { return improvedVersion; }
    public void setImprovedVersion(String improvedVersion) { this.improvedVersion = improvedVersion; }
}
