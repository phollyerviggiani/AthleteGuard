package com.phollyerviggiani.athleteguard.repository;

import com.phollyerviggiani.athleteguard.entity.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AthleteRepository extends JpaRepository<Athlete, Integer> {
}