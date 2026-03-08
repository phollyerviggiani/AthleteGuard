package com.phollyerviggiani.athleteguard.controller;

import com.phollyerviggiani.athleteguard.entity.Athlete;
import com.phollyerviggiani.athleteguard.entity.TrainingSession;
import com.phollyerviggiani.athleteguard.service.AthleteService;
import com.phollyerviggiani.athleteguard.service.TrainingSessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/athletes/{athleteId}/sessions")
public class TrainingSessionController {

    private final AthleteService athleteService;
    private final TrainingSessionService trainingSessionService;

    public TrainingSessionController(AthleteService athleteService,
                                     TrainingSessionService trainingSessionService) {
        this.athleteService = athleteService;
        this.trainingSessionService = trainingSessionService;
    }

    @GetMapping
    public String listSessions(@PathVariable Integer athleteId, Model model) {

        Athlete athlete = athleteService.getAthleteById(athleteId)
                .orElseThrow(() -> new RuntimeException("Athlete not found"));

        List<TrainingSession> sessions =
                trainingSessionService.getSessionsForAthlete(athleteId);

        model.addAttribute("athlete", athlete);
        model.addAttribute("sessions", sessions);

        return "training-session-list";
    }

    // Show form
    @GetMapping("/new")
    public String showSessionForm(@PathVariable Integer athleteId, Model model) {

        Optional<Athlete> athleteOpt = athleteService.getAthleteById(athleteId);

        if (athleteOpt.isEmpty()) {
            return "redirect:/athletes";
        }

        model.addAttribute("athlete", athleteOpt.get());
        model.addAttribute("trainingSession", new TrainingSession());

        return "training-session-form";
    }



    // Handle submission
    @PostMapping
    public String addSession(@PathVariable Integer athleteId,
                             @ModelAttribute TrainingSession trainingSession) {

        Athlete athlete = athleteService.getAthleteById(athleteId)
                .orElseThrow(() -> new RuntimeException("Athlete not found"));

        trainingSession.setAthlete(athlete);

        trainingSessionService.addSession(trainingSession);

        return "redirect:/athletes/" + athleteId;
    }
}