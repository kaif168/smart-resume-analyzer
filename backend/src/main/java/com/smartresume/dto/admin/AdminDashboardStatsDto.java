package com.smartresume.dto.admin;

import java.util.HashMap;
import java.util.Map;

public class AdminDashboardStatsDto {
    private long totalUsers;
    private long totalResumes;
    private long totalAnalyses;
    private double averageAtsScore;
    private long totalJobTemplates;
    private Map<String, Long> roleDistribution = new HashMap<>();

    public AdminDashboardStatsDto() {}

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalResumes() { return totalResumes; }
    public void setTotalResumes(long totalResumes) { this.totalResumes = totalResumes; }

    public long getTotalAnalyses() { return totalAnalyses; }
    public void setTotalAnalyses(long totalAnalyses) { this.totalAnalyses = totalAnalyses; }

    public double getAverageAtsScore() { return averageAtsScore; }
    public void setAverageAtsScore(double averageAtsScore) { this.averageAtsScore = averageAtsScore; }

    public long getTotalJobTemplates() { return totalJobTemplates; }
    public void setTotalJobTemplates(long totalJobTemplates) { this.totalJobTemplates = totalJobTemplates; }

    public Map<String, Long> getRoleDistribution() { return roleDistribution; }
    public void setRoleDistribution(Map<String, Long> roleDistribution) { this.roleDistribution = roleDistribution; }
}
