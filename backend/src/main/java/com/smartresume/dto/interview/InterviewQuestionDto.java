package com.smartresume.dto.interview;

public class InterviewQuestionDto {
    private Long id;
    private String question;
    private String category; // TECHNICAL, SYSTEM_DESIGN, BEHAVIORAL
    private String difficulty; // Easy, Medium, Hard
    private String sampleAnswer;
    private String relatedSkill;

    public InterviewQuestionDto() {}

    public InterviewQuestionDto(String question, String category, String difficulty, String sampleAnswer, String relatedSkill) {
        this.question = question;
        this.category = category;
        this.difficulty = difficulty;
        this.sampleAnswer = sampleAnswer;
        this.relatedSkill = relatedSkill;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getSampleAnswer() { return sampleAnswer; }
    public void setSampleAnswer(String sampleAnswer) { this.sampleAnswer = sampleAnswer; }

    public String getRelatedSkill() { return relatedSkill; }
    public void setRelatedSkill(String relatedSkill) { this.relatedSkill = relatedSkill; }
}
