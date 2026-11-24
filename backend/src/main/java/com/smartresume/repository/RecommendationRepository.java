package com.smartresume.repository;

import com.smartresume.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findAllByResumeIdOrderByCompatibilityPercentageDesc(Long resumeId);
    void deleteAllByResumeId(Long resumeId);
}
