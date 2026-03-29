package com.phollyerviggiani.athleteguard.controller;

import com.phollyerviggiani.athleteguard.entity.Users;
import com.phollyerviggiani.athleteguard.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// home controller which returns the home page
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Users user = userDetails.getUser();

        if (user.getRole().equals("ATHLETE")) {
            return "redirect:/athletes/" + user.getAthlete().getAthleteId();
        }

        return "index";
    }
}
