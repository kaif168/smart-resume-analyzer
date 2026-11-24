package com.smartresume.dto.resume;

import java.util.List;

public class ResumeVersionCompareDto {
    private Long oldResumeId;
    private String oldFileName;
    private String oldVersionTag;
    private int oldOverallScore;

    private Long newResumeId;
    private String newFileName;
    private String newVersionTag;
    private int newOverallScore;

    private int scoreDifference;
    private List<String> newlyAddedSkills;
    private List<String> resolvedWeaknesses;
    private List<String> remainingGaps;
    private String summaryComparison;

    public ResumeVersionCompareDto() {}

    public Long getOldResumeId() { return oldResumeId; }
    public void setOldResumeId(Long oldResumeId) { this.oldResumeId = oldResumeId; }

    public String getOldFileName() { return oldFileName; }
    public void setOldFileName(String oldFileName) { this.oldFileName = oldFileName; }

    public String getOldVersionTag() { return oldVersionTag; }
    public void setOldVersionTag(String oldVersionTag) { this.oldVersionTag = oldVersionTag; }

    public int getOldOverallScore() { return oldOverallScore; }
    public void setOldOverallScore(int oldOverallScore) { this.oldOverallScore = oldOverallScore; }

    public Long getNewResumeId() { return newResumeId; }
    public void setNewResumeId(Long newResumeId) { this.newResumeId = newResumeId; }

    public String getNewFileName() { return newFileName; }
    public void setNewFileName(String newFileName) { this.newFileName = newFileName; }

    public String getNewVersionTag() { return newVersionTag; }
    public void setNewVersionTag(String newVersionTag) { this.newVersionTag = newVersionTag; }

    public int getNewOverallScore() { return newOverallScore; }
    public void setNewOverallScore(int newOverallScore) { this.newOverallScore = newOverallScore; }

    public int getScoreDifference() { return scoreDifference; }
    public void setScoreDifference(int scoreDifference) { this.scoreDifference = scoreDifference; }

    public List<String> getNewlyAddedSkills() { return newlyAddedSkills; }
    public void setNewlyAddedSkills(List<String> newlyAddedSkills) { this.newlyAddedSkills = newlyAddedSkills; }

    public List<String> getResolvedWeaknesses() { return resolvedWeaknesses; }
    public void setResolvedWeaknesses(List<String> resolvedWeaknesses) { this.resolvedWeaknesses = resolvedWeaknesses; }

    public List<String> getRemainingGaps() { return remainingGaps; }
    public void setRemainingGaps(List<String> remainingGaps) { this.remainingGaps = remainingGaps; }

    public String getSummaryComparison() { return summaryComparison; }
    public void setSummaryComparison(String summaryComparison) { this.summaryComparison = summaryComparison; }
}
