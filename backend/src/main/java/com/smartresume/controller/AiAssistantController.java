package com.smartresume.controller;

import com.smartresume.dto.ai.*;
import com.smartresume.dto.common.ApiResponse;
import com.smartresume.dto.interview.InterviewQuestionDto;
import com.smartresume.service.AiAssistantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;

    public AiAssistantController(AiAssistantService aiAssistantService) {
        this.aiAssistantService = aiAssistantService;
    }

    @PostMapping("/improvements")
    public ResponseEntity<ApiResponse<AiImprovementResponse>> getImprovements(@Valid @RequestBody AiImprovementRequest request) {
        AiImprovementResponse response = aiAssistantService.generateImprovements(request);
        return ResponseEntity.ok(ApiResponse.ok("AI suggestions generated successfully", response));
    }

    @PostMapping("/project-feedback")
    public ResponseEntity<ApiResponse<ProjectFeedbackDto>> analyzeProject(@RequestBody Map<String, String> body) {
        String title = body.getOrDefault("title", "Project");
        String description = body.getOrDefault("description", "");
        ProjectFeedbackDto response = aiAssistantService.analyzeProjectDescription(title, description);
        return ResponseEntity.ok(ApiResponse.ok("Project analysis generated", response));
    }

    @GetMapping("/interview-questions/{resumeId}")
    public ResponseEntity<ApiResponse<List<InterviewQuestionDto>>> getInterviewQuestions(@PathVariable Long resumeId) {
        List<InterviewQuestionDto> questions = aiAssistantService.generateInterviewQuestions(resumeId);
        return ResponseEntity.ok(ApiResponse.ok("Interview questions generated", questions));
    }
}
