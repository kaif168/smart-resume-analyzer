package com.smartresume.controller;

import com.smartresume.dto.common.ApiResponse;
import com.smartresume.dto.jobmatching.JobMatchRequest;
import com.smartresume.dto.jobmatching.JobMatchResponse;
import com.smartresume.entity.JobDescription;
import com.smartresume.entity.JobMatch;
import com.smartresume.service.JobMatchingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-matching")
public class JobMatchingController {

    private final JobMatchingService jobMatchingService;

    public JobMatchingController(JobMatchingService jobMatchingService) {
        this.jobMatchingService = jobMatchingService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse<JobMatchResponse>> matchResumeWithJob(@Valid @RequestBody JobMatchRequest request) {
        JobMatchResponse response = jobMatchingService.matchResumeWithJob(request);
        return ResponseEntity.ok(ApiResponse.ok("Job description matched successfully", response));
    }

    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<List<JobDescription>>> getTemplates() {
        List<JobDescription> templates = jobMatchingService.getAvailableJobTemplates();
        return ResponseEntity.ok(ApiResponse.ok(templates));
    }

    @GetMapping("/history/{resumeId}")
    public ResponseEntity<ApiResponse<List<JobMatch>>> getMatchHistory(@PathVariable Long resumeId) {
        List<JobMatch> history = jobMatchingService.getResumeMatchHistory(resumeId);
        return ResponseEntity.ok(ApiResponse.ok(history));
    }
}
