package com.phollyerviggiani.athleteguard.service;

import com.phollyerviggiani.athleteguard.entity.Athlete;
import com.phollyerviggiani.athleteguard.entity.TrainingSession;
import com.phollyerviggiani.athleteguard.entity.WorkloadSummary;
import com.phollyerviggiani.athleteguard.repository.TrainingSessionRepository;
import com.phollyerviggiani.athleteguard.repository.WorkloadSummaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


// service class for calculating and storing workload summaries for athletes, including methods to calculate
// acute and chronic workloads based on training sessions and evaluate injury risk using the InjuryRiskService
@Service
public class WorkloadService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final WorkloadSummaryRepository workloadSummaryRepository;
    private final InjuryRiskService injuryRiskService;

    public WorkloadService(TrainingSessionRepository trainingSessionRepository,
                           WorkloadSummaryRepository workloadSummaryRepository,
                           InjuryRiskService injuryRiskService) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.workloadSummaryRepository = workloadSummaryRepository;
        this.injuryRiskService = injuryRiskService;
    }

    @Transactional
    public void calculateAndStoreWorkload(Athlete athlete) {

        LocalDate today = LocalDate.now();
        LocalDate acuteStart = today.minusDays(7);
        LocalDate chronicStart = today.minusDays(28);

        List<TrainingSession> sessions =
                trainingSessionRepository.findByAthlete_AthleteId(athlete.getAthleteId());

        double acuteTotal = 0;
        double chronicTotal = 0;

        for (TrainingSession s : sessions) {

            double sessionLoad = s.getDurationMinutes() * s.getIntensity();

            if (!s.getSessionDate().isBefore(acuteStart)) {
                acuteTotal += sessionLoad;
            }

            if (!s.getSessionDate().isBefore(chronicStart)) {
                chronicTotal += sessionLoad;
            }
        }

        double acute = acuteTotal / 7.0;
        double chronic = chronicTotal / 28.0;
        double acwr = chronic > 0 ? acute / chronic : 0;

        WorkloadSummary summary = new WorkloadSummary();
        summary.setAthlete(athlete);
        summary.setPeriodStartDate(chronicStart);
        summary.setPeriodEndDate(today);
        summary.setAcuteWorkload(acute);
        summary.setChronicWorkload(chronic);
        summary.setAcwr(acwr);

        WorkloadSummary savedSummary = workloadSummaryRepository.save(summary);

        injuryRiskService.evaluateRisk(savedSummary);
    }
}