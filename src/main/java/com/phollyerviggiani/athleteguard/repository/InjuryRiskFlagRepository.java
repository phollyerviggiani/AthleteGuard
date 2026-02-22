package com.phollyerviggiani.athleteguard.repository;

import com.phollyerviggiani.athleteguard.entity.InjuryRiskFlag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InjuryRiskFlagRepository extends JpaRepository<InjuryRiskFlag, Integer> {
}