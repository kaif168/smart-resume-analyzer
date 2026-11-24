package com.smartresume.controller;

import com.smartresume.dto.analysis.AnalysisResponse;
import com.smartresume.dto.common.ApiResponse;
import com.smartresume.entity.AnalysisHistory;
import com.smartresume.service.AtsScoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AtsScoringService atsScoringService;

    public AnalysisController(AtsScoringService atsScoringService) {
        this.atsScoringService = atsScoringService;
    }

    @PostMapping("/{resumeId}")
    public ResponseEntity<ApiResponse<AnalysisResponse>> analyzeResume(@PathVariable Long resumeId) {
        AnalysisResponse response = atsScoringService.analyzeResume(resumeId);
        return ResponseEntity.ok(ApiResponse.ok("Resume analysis completed successfully", response));
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<ApiResponse<AnalysisResponse>> getAnalysis(@PathVariable Long resumeId) {
        AnalysisResponse response = atsScoringService.getLatestAnalysis(resumeId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<AnalysisHistory>>> getUserHistory() {
        List<AnalysisHistory> history = atsScoringService.getUserAnalysisHistory();
        return ResponseEntity.ok(ApiResponse.ok(history));
    }
}
