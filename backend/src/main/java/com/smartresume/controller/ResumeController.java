package com.smartresume.controller;

import com.smartresume.dto.common.ApiResponse;
import com.smartresume.dto.resume.*;
import com.smartresume.entity.ResumeChecklistItem;
import com.smartresume.service.ResumeService;
import com.smartresume.service.ResumeStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final ResumeStorageService resumeStorageService;

    public ResumeController(ResumeService resumeService, ResumeStorageService resumeStorageService) {
        this.resumeService = resumeService;
        this.resumeStorageService = resumeStorageService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ResumeResponse>> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "targetRole", required = false, defaultValue = "Software Engineer") String targetRole,
            @RequestParam(value = "versionTag", required = false, defaultValue = "v1.0") String versionTag) {
        ResumeResponse response = resumeService.uploadAndParseResume(file, targetRole, versionTag);
        return ResponseEntity.ok(ApiResponse.ok("Resume uploaded and parsed successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResumeResponse>>> getUserResumes() {
        List<ResumeResponse> resumes = resumeService.getCurrentUserResumes();
        return ResponseEntity.ok(ApiResponse.ok(resumes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResumeDetailResponse>> getResumeById(@PathVariable Long id) {
        ResumeDetailResponse response = resumeService.getResumeDetail(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
        return ResponseEntity.ok(ApiResponse.ok("Resume deleted successfully", null));
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadResumeFile(@PathVariable String fileName) {
        Resource resource = resumeStorageService.loadFileAsResource(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/{resumeId}/checklist/{itemId}/toggle")
    public ResponseEntity<ApiResponse<ResumeChecklistItem>> toggleChecklistItem(
            @PathVariable Long resumeId,
            @PathVariable Long itemId,
            @RequestBody Map<String, Boolean> body) {
        boolean completed = body.getOrDefault("completed", false);
        ResumeChecklistItem updated = resumeService.toggleChecklistItem(resumeId, itemId, completed);
        return ResponseEntity.ok(ApiResponse.ok("Checklist item updated", updated));
    }

    @GetMapping("/compare")
    public ResponseEntity<ApiResponse<ResumeVersionCompareDto>> compareVersions(
            @RequestParam("oldResumeId") Long oldResumeId,
            @RequestParam("newResumeId") Long newResumeId) {
        ResumeVersionCompareDto comparison = resumeService.compareResumeVersions(oldResumeId, newResumeId);
        return ResponseEntity.ok(ApiResponse.ok("Resume versions compared successfully", comparison));
    }
}
