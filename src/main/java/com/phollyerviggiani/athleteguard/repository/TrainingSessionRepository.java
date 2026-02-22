package com.phollyerviggiani.athleteguard.repository;

import com.phollyerviggiani.athleteguard.entity.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// repository interface for the TrainingSession entity, extending JpaRepository to provide basic CRUD operations
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Integer> {
    List<TrainingSession> findByAthlete_AthleteId(Integer athleteId);
}