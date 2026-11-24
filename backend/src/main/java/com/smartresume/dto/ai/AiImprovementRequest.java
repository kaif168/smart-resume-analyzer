package com.smartresume.dto.ai;

import jakarta.validation.constraints.NotNull;

public class AiImprovementRequest {
    @NotNull(message = "Resume ID is required")
    private Long resumeId;

    private String targetRole;
    private String sectionType; // SUMMARY, PROJECTS, SKILLS, EXPERIENCE, GENERAL
    private String customPrompt;

    public AiImprovementRequest() {}

    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public String getSectionType() { return sectionType; }
    public void setSectionType(String sectionType) { this.sectionType = sectionType; }

    public String getCustomPrompt() { return customPrompt; }
    public void setCustomPrompt(String customPrompt) { this.customPrompt = customPrompt; }
}
