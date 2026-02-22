package com.phollyerviggiani.athleteguard.repository;

import com.phollyerviggiani.athleteguard.entity.WorkloadSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkloadSummaryRepository extends JpaRepository<WorkloadSummary, Integer> {
}