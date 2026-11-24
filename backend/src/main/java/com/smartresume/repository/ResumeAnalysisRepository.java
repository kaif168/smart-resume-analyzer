package com.smartresume.repository;

import com.smartresume.entity.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {
    Optional<ResumeAnalysis> findTopByResumeIdOrderByCreatedAtDesc(Long resumeId);
    Optional<ResumeAnalysis> findByIdAndUserId(Long id, Long userId);
    List<ResumeAnalysis> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserId(Long userId);

    @Query("SELECT AVG(ra.overallScore) FROM ResumeAnalysis ra WHERE ra.user.id = :userId")
    Double getAverageScoreByUserId(@Param("userId") Long userId);

    @Query("SELECT AVG(ra.overallScore) FROM ResumeAnalysis ra")
    Double getGlobalAverageScore();
}
