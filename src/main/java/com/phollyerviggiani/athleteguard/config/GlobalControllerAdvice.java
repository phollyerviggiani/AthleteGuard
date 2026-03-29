package com.phollyerviggiani.athleteguard.config;

import com.phollyerviggiani.athleteguard.entity.Users;
import com.phollyerviggiani.athleteguard.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("displayName")
    public String displayName(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "";
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {

            Users user = userDetails.getUser();

            if (user.getRole().equals("COACH")) {
                return "Welcome, Coach";
            }

            if (user.getRole().equals("ATHLETE") && user.getAthlete() != null) {
                return "Welcome, "+ user.getAthlete().getFirstName();
            }
        }

        return authentication.getName();
    }
}