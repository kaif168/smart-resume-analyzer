package com.smartresume.repository;

import com.smartresume.entity.JobDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {
    List<JobDescription> findAllByIsTemplateTrue();
    Optional<JobDescription> findFirstByRoleCategoryIgnoreCase(String roleCategory);
}
