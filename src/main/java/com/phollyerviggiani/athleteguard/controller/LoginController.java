package com.phollyerviggiani.athleteguard.controller;

import com.phollyerviggiani.athleteguard.entity.Users;
import com.phollyerviggiani.athleteguard.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // if the user is already authenticated, redirect them to the appropriate page based on their role
    @GetMapping("/login")
    public String login(@AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails != null) {
            Users user = userDetails.getUser();

            if (user.getRole().equals("ATHLETE")) {
                return "redirect:/athletes/" + user.getAthlete().getAthleteId();
            }

            return "redirect:/";
        }

        return "login";
    }
}
