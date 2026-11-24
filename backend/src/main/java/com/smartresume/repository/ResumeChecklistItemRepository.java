package com.smartresume.repository;

import com.smartresume.entity.ResumeChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeChecklistItemRepository extends JpaRepository<ResumeChecklistItem, Long> {
    List<ResumeChecklistItem> findAllByResumeIdOrderByIdAsc(Long resumeId);
    Optional<ResumeChecklistItem> findByIdAndResumeId(Long id, Long resumeId);
    void deleteAllByResumeId(Long resumeId);
}
