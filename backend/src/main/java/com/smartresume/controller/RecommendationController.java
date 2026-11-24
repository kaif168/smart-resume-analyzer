package com.smartresume.controller;

import com.smartresume.dto.common.ApiResponse;
import com.smartresume.dto.recommendation.CareerRecommendationDto;
import com.smartresume.dto.recommendation.SkillGapDto;
import com.smartresume.service.CareerRecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final CareerRecommendationService careerRecommendationService;

    public RecommendationController(CareerRecommendationService careerRecommendationService) {
        this.careerRecommendationService = careerRecommendationService;
    }

    @GetMapping("/career/{resumeId}")
    public ResponseEntity<ApiResponse<List<CareerRecommendationDto>>> getCareerRecommendations(@PathVariable Long resumeId) {
        List<CareerRecommendationDto> recommendations = careerRecommendationService.getCareerRecommendations(resumeId);
        return ResponseEntity.ok(ApiResponse.ok(recommendations));
    }

    @GetMapping("/skills/{resumeId}")
    public ResponseEntity<ApiResponse<SkillGapDto>> getSkillGap(
            @PathVariable Long resumeId,
            @RequestParam(value = "targetRole", required = false) String targetRole) {
        SkillGapDto gap = careerRecommendationService.analyzeSkillGap(resumeId, targetRole);
        return ResponseEntity.ok(ApiResponse.ok(gap));
    }
}
