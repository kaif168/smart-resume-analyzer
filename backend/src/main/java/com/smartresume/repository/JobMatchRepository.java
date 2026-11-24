package com.smartresume.repository;

import com.smartresume.entity.JobMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobMatchRepository extends JpaRepository<JobMatch, Long> {
    List<JobMatch> findAllByResumeIdOrderByCreatedAtDesc(Long resumeId);
    Optional<JobMatch> findTopByResumeIdOrderByCreatedAtDesc(Long resumeId);
}
