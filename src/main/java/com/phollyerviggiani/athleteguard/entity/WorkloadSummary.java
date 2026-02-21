package com.phollyerviggiani.athleteguard.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "workload_summary")
public class WorkloadSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "summary_id")
    private Integer summaryId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "athlete_id", referencedColumnName = "athlete_id")
    private Athlete athlete;

    @Column(name = "period_start_date", nullable = false)
    private LocalDate periodStartDate;

    @Column(name = "period_end_date", nullable = false)
    private LocalDate periodEndDate;

    @Column(name = "acute_workload", nullable = false)
    private Double acuteWorkload;

    @Column(name = "chronic_workload", nullable = false)
    private Double chronicWorkload;

    @Column(name = "acwr", nullable = false)
    private Double acwr;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    @OneToMany(mappedBy = "workloadSummary")
    private List<InjuryRiskFlag> injuryRiskFlags;

    public WorkloadSummary() {}

    // Getters and setters

    public Integer getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(Integer summaryId) {
        this.summaryId = summaryId;
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public void setAthlete(Athlete athlete) {
        this.athlete = athlete;
    }

    public LocalDate getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(LocalDate periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public LocalDate getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(LocalDate periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    public Double getAcuteWorkload() {
        return acuteWorkload;
    }

    public void setAcuteWorkload(Double acuteWorkload) {
        this.acuteWorkload = acuteWorkload;
    }

    public Double getChronicWorkload() {
        return chronicWorkload;
    }

    public void setChronicWorkload(Double chronicWorkload) {
        this.chronicWorkload = chronicWorkload;
    }

    public Double getAcwr() {
        return acwr;
    }

    public void setAcwr(Double acwr) {
        this.acwr = acwr;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public List<InjuryRiskFlag> getInjuryRiskFlags() {
        return injuryRiskFlags;
    }

    public void setInjuryRiskFlags(List<InjuryRiskFlag> injuryRiskFlags) {
        this.injuryRiskFlags = injuryRiskFlags;
    }
}