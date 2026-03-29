package com.phollyerviggiani.athleteguard.config;

import com.phollyerviggiani.athleteguard.entity.Users;
import com.phollyerviggiani.athleteguard.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        Users user = userDetails.getUser();

        if (user.getRole().equals("ATHLETE")) {
            Integer athleteId = user.getAthlete().getAthleteId();
            response.sendRedirect("/athletes/" + athleteId);
        } else {
            response.sendRedirect("/");
        }
    }
}