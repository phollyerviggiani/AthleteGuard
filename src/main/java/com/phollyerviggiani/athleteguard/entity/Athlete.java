package com.phollyerviggiani.athleteguard.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "athlete")

// entity class representing an athlete
// this class will be used to store athlete information
// in the database and will be linked to the users table through a one-to-one relationship
public class Athlete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "athlete_id")
    private Integer athleteId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private Users user;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "sport", nullable = false)
    private String sport;

    @Column(name = "position")
    private String position;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "athlete")
    private List<TrainingSession> trainingSessions;

    @OneToMany(mappedBy = "athlete")
    private List<WorkloadSummary> workloadSummaries;

    @OneToMany(mappedBy = "athlete")
    private List<InjuryRiskFlag> injuryRiskFlags;

    public Athlete() {}

    // Getters and setters for athlete
    public Integer getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(Integer athleteId) {
        this.athleteId = athleteId;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<TrainingSession> getTrainingSessions() {
        return trainingSessions;
    }

    public void setTrainingSessions(List<TrainingSession> trainingSessions) {
        this.trainingSessions = trainingSessions;
    }

    public List<WorkloadSummary> getWorkloadSummaries() {
        return workloadSummaries;
    }

    public void setWorkloadSummaries(List<WorkloadSummary> workloadSummaries) {
        this.workloadSummaries = workloadSummaries;
    }

    public List<InjuryRiskFlag> getInjuryRiskFlags() {
        return injuryRiskFlags;
    }

    public void setInjuryRiskFlags(List<InjuryRiskFlag> injuryRiskFlags) {
        this.injuryRiskFlags = injuryRiskFlags;
    }
}