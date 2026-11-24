package com.smartresume.repository;

import com.smartresume.entity.ExtractedSkill;
import com.smartresume.entity.SkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtractedSkillRepository extends JpaRepository<ExtractedSkill, Long> {
    List<ExtractedSkill> findAllByResumeId(Long resumeId);
    List<ExtractedSkill> findAllByResumeIdAndCategory(Long resumeId, SkillCategory category);
    void deleteAllByResumeId(Long resumeId);
}
