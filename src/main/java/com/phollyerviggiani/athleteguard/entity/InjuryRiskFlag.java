package com.phollyerviggiani.athleteguard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "injury_risk_flag")
public class InjuryRiskFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "risk_id")
    private Integer riskId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "athlete_id", referencedColumnName = "athlete_id")
    private Athlete athlete;

    @ManyToOne(optional = false)
    @JoinColumn(name = "summary_id", referencedColumnName = "summary_id")
    private WorkloadSummary workloadSummary;

    @Column(name = "risk_level", nullable = false)
    private String riskLevel;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    public InjuryRiskFlag() {}

    // Getters and setters

    public Integer getRiskId() {
        return riskId;
    }

    public void setRiskId(Integer riskId) {
        this.riskId = riskId;
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public void setAthlete(Athlete athlete) {
        this.athlete = athlete;
    }

    public WorkloadSummary getWorkloadSummary() {
        return workloadSummary;
    }

    public void setWorkloadSummary(WorkloadSummary workloadSummary) {
        this.workloadSummary = workloadSummary;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}