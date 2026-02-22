package com.phollyerviggiani.athleteguard.service;

import com.phollyerviggiani.athleteguard.entity.InjuryRiskFlag;
import com.phollyerviggiani.athleteguard.entity.WorkloadSummary;
import com.phollyerviggiani.athleteguard.repository.InjuryRiskFlagRepository;
import org.springframework.stereotype.Service;

// service class for evaluating injury risk based on workload summaries, determining risk levels, and saving injury risk flags to the database
@Service
public class InjuryRiskService {

    private final InjuryRiskFlagRepository injuryRiskFlagRepository;

    public InjuryRiskService(InjuryRiskFlagRepository injuryRiskFlagRepository) {
        this.injuryRiskFlagRepository = injuryRiskFlagRepository;
    }

    public void evaluateRisk(WorkloadSummary summary) {

        double acwr = summary.getAcwr();

        String riskLevel;
        String reason;

        if (acwr > 1.5) {
            riskLevel = "HIGH";
            reason = "ACWR exceeds 1.5 indicating significant workload spike.";
        } else if (acwr > 1.2) {
            riskLevel = "MODERATE";
            reason = "ACWR moderately elevated above recommended range.";
        } else if (acwr < 0.8) {
            riskLevel = "UNDERLOAD";
            reason = "ACWR below optimal range; potential deconditioning risk.";
        } else {
            riskLevel = "LOW";
            reason = "Workload within optimal training range.";
        }

        InjuryRiskFlag flag = new InjuryRiskFlag();
        flag.setAthlete(summary.getAthlete());
        flag.setWorkloadSummary(summary);
        flag.setRiskLevel(riskLevel);
        flag.setReason(reason);

        injuryRiskFlagRepository.save(flag);
    }
}