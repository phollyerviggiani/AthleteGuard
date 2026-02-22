package com.phollyerviggiani.athleteguard.repository;

import com.phollyerviggiani.athleteguard.entity.WorkloadSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// repository interface for the WorkloadSummary entity, extending JpaRepository to provide basic CRUD operations
public interface WorkloadSummaryRepository extends JpaRepository<WorkloadSummary, Integer> {
    Optional<WorkloadSummary>
    findTopByAthlete_AthleteIdOrderByCalculatedAtDesc(Integer athleteId);
}