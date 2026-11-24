package com.smartresume.dto.ai;

import java.util.ArrayList;
import java.util.List;

public class AiImprovementResponse {
    private Long resumeId;
    private String originalContent;
    private String suggestedSummary;
    private List<String> improvedBulletPoints = new ArrayList<>();
    private List<String> powerActionVerbs = new ArrayList<>();
    private List<String> grammarAndStyleTips = new ArrayList<>();
    private List<String> weakSectionFixes = new ArrayList<>();
    private String engineUsed; // "AI_LLM" or "INTELLIGENT_RULE_BASED"
    private String ethicalNotice = "All suggestions are based strictly on candidate-provided background. No degrees or experience were fabricated.";

    public AiImprovementResponse() {}

    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }

    public String getOriginalContent() { return originalContent; }
    public void setOriginalContent(String originalContent) { this.originalContent = originalContent; }

    public String getSuggestedSummary() { return suggestedSummary; }
    public void setSuggestedSummary(String suggestedSummary) { this.suggestedSummary = suggestedSummary; }

    public List<String> getImprovedBulletPoints() { return improvedBulletPoints; }
    public void setImprovedBulletPoints(List<String> improvedBulletPoints) { this.improvedBulletPoints = improvedBulletPoints; }

    public List<String> getPowerActionVerbs() { return powerActionVerbs; }
    public void setPowerActionVerbs(List<String> powerActionVerbs) { this.powerActionVerbs = powerActionVerbs; }

    public List<String> getGrammarAndStyleTips() { return grammarAndStyleTips; }
    public void setGrammarAndStyleTips(List<String> grammarAndStyleTips) { this.grammarAndStyleTips = grammarAndStyleTips; }

    public List<String> getWeakSectionFixes() { return weakSectionFixes; }
    public void setWeakSectionFixes(List<String> weakSectionFixes) { this.weakSectionFixes = weakSectionFixes; }

    public String getEngineUsed() { return engineUsed; }
    public void setEngineUsed(String engineUsed) { this.engineUsed = engineUsed; }

    public String getEthicalNotice() { return ethicalNotice; }
    public void setEthicalNotice(String ethicalNotice) { this.ethicalNotice = ethicalNotice; }
}
