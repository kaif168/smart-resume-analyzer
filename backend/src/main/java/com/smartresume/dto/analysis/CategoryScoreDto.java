package com.smartresume.dto.analysis;

public class CategoryScoreDto {
    private String name;
    private int score;
    private int maxScore;
    private int percentage;
    private String feedback;
    private String status; // EXCELLENT, GOOD, NEEDS_IMPROVEMENT

    public CategoryScoreDto() {}

    public CategoryScoreDto(String name, int score, int maxScore, String feedback) {
        this.name = name;
        this.score = score;
        this.maxScore = maxScore;
        this.percentage = (int) Math.round(((double) score / maxScore) * 100);
        this.feedback = feedback;
        if (percentage >= 80) {
            this.status = "EXCELLENT";
        } else if (percentage >= 60) {
            this.status = "GOOD";
        } else {
            this.status = "NEEDS_IMPROVEMENT";
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getMaxScore() { return maxScore; }
    public void setMaxScore(int maxScore) { this.maxScore = maxScore; }

    public int getPercentage() { return percentage; }
    public void setPercentage(int percentage) { this.percentage = percentage; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
