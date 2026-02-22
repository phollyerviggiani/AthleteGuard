package com.phollyerviggiani.athleteguard.repository;

import com.phollyerviggiani.athleteguard.entity.InjuryRiskFlag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// repository interface for the InjuryRiskFlag entity, extending JpaRepository to provide basic CRUD operations
public interface InjuryRiskFlagRepository extends JpaRepository<InjuryRiskFlag, Integer> {
    Optional<InjuryRiskFlag>
    findTopByAthlete_AthleteIdOrderByGeneratedAtDesc(Integer athleteId);
}