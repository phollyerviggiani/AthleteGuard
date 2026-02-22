package com.phollyerviggiani.athleteguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// main application class for the AthleteGuard application
// annotated with @SpringBootApplication to enable autoconfiguration and component scanning
@SpringBootApplication
public class AthleteGuardApplication {

    public static void main(String[] args) {
        SpringApplication.run(AthleteGuardApplication.class, args);
    }

}
