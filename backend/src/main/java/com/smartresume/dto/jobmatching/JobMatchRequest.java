package com.smartresume.dto.jobmatching;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class JobMatchRequest {
    @NotNull(message = "Resume ID is required")
    private Long resumeId;

    private Long jobTemplateId; // Optional: pick from pre-configured industry templates

    private String targetJobTitle;

    @NotBlank(message = "Job description text is required")
    private String jobDescriptionText;

    public JobMatchRequest() {}

    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }

    public Long getJobTemplateId() { return jobTemplateId; }
    public void setJobTemplateId(Long jobTemplateId) { this.jobTemplateId = jobTemplateId; }

    public String getTargetJobTitle() { return targetJobTitle; }
    public void setTargetJobTitle(String targetJobTitle) { this.targetJobTitle = targetJobTitle; }

    public String getJobDescriptionText() { return jobDescriptionText; }
    public void setJobDescriptionText(String jobDescriptionText) { this.jobDescriptionText = jobDescriptionText; }
}
