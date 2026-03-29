package com.phollyerviggiani.athleteguard.controller;

import com.phollyerviggiani.athleteguard.entity.Athlete;
import com.phollyerviggiani.athleteguard.entity.Users;
import com.phollyerviggiani.athleteguard.entity.WorkloadSummary;
import com.phollyerviggiani.athleteguard.entity.InjuryRiskFlag;
import com.phollyerviggiani.athleteguard.repository.WorkloadSummaryRepository;
import com.phollyerviggiani.athleteguard.repository.InjuryRiskFlagRepository;
import com.phollyerviggiani.athleteguard.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import com.phollyerviggiani.athleteguard.service.AthleteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


// dealing with going to /athletes and /athletes/{id} and showing the appropriate data on the page
@Controller
public class AthleteController {

    private final AthleteService athleteService;
    private final WorkloadSummaryRepository workloadSummaryRepository;
    private final InjuryRiskFlagRepository injuryRiskFlagRepository;

    // constructor injection of the service and repositories
    public AthleteController(AthleteService athleteService,
                             WorkloadSummaryRepository workloadSummaryRepository,
                             InjuryRiskFlagRepository injuryRiskFlagRepository) {
        this.athleteService = athleteService;
        this.workloadSummaryRepository = workloadSummaryRepository;
        this.injuryRiskFlagRepository = injuryRiskFlagRepository;
    }

    // going to /athletes which shows all athletes
    @GetMapping("/athletes")
    public String getAllAthletes(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 Model model) {

        Users user = userDetails.getUser();

        // if the user is an athlete, redirect them to their own athlete page instead of showing all athletes
        if (user.getRole().equals("ATHLETE")) {
            return "redirect:/athletes/" + user.getAthlete().getAthleteId();
        }

        List<Athlete> athletes = athleteService.getAllAthletes();
        model.addAttribute("athletes", athletes);

        // coach can proceed as normal
        return "athletes";
    }


    // going to specific athlete page, showing all athlete details
    @GetMapping("/athletes/{id}")
    public String getAthleteDetail(@PathVariable Integer id,
                                   @AuthenticationPrincipal CustomUserDetails userDetails,
                                   Model model) {

        Users user = userDetails.getUser();

        if (user.getRole().equals("ATHLETE")) {
            Integer userAthleteId = user.getAthlete().getAthleteId();

            if (!userAthleteId.equals(id)) {
                return "redirect:/athletes/" + userAthleteId;
            }
        }

        Optional<Athlete> athleteOpt = athleteService.getAthleteById(id);

        if (athleteOpt.isEmpty()) {
            return "redirect:/athletes";
        }

        Athlete athlete = athleteOpt.get();

        Optional<WorkloadSummary> summary =
                workloadSummaryRepository
                        .findTopByAthlete_AthleteIdOrderByCalculatedAtDesc(id);

        Optional<InjuryRiskFlag> risk =
                injuryRiskFlagRepository
                        .findTopByAthlete_AthleteIdOrderByGeneratedAtDesc(id);

        model.addAttribute("athlete", athlete);
        model.addAttribute("summary", summary.orElse(null));
        model.addAttribute("risk", risk.orElse(null));

        return "athlete-detail";
    }
}