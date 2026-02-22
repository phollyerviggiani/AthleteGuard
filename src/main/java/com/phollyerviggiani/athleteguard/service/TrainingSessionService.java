package com.phollyerviggiani.athleteguard.service;

import com.phollyerviggiani.athleteguard.entity.TrainingSession;
import com.phollyerviggiani.athleteguard.repository.TrainingSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final WorkloadService workloadService;

    public TrainingSessionService(TrainingSessionRepository trainingSessionRepository,
                                  WorkloadService workloadService) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.workloadService = workloadService;
    }

    public List<TrainingSession> getSessionsForAthlete(Integer athleteId) {
        return trainingSessionRepository.findByAthlete_AthleteId(athleteId);
    }

    @Transactional
    public TrainingSession addSession(TrainingSession session) {

        TrainingSession saved = trainingSessionRepository.save(session);

        // After saving, recalculate workload
        workloadService.calculateAndStoreWorkload(saved.getAthlete());

        return saved;
    }
}